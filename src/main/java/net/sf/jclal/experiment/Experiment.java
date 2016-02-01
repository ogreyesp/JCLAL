/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.jclal.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.core.IAlgorithmListener;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IEvaluationMethod;
import net.sf.jclal.evaluation.method.FivePerTwoCrossValidation;
import net.sf.jclal.evaluation.method.HoldOut;
import net.sf.jclal.evaluation.method.LeaveOneOutCrossValidation;
import net.sf.jclal.evaluation.method.kFoldCrossValidation;
import net.sf.jclal.listener.ClassicalReporterListener;
import net.sf.jclal.util.file.FileUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Class to execute an experiment.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class Experiment {

	/**
	 * The evaluation method used
	 */
	private IEvaluationMethod method;
	/**
	 * Time of execution of the program in milliseconds
	 */
	private long runtime;

	/**
	 * Execute the job specified as parameter
	 * 
	 * @param jobFilename
	 *            The xml configuration of the experiment.
	 */
	@SuppressWarnings("unchecked")
	public void executeJob(String jobFilename) {
		// Try open job file
		File jobFile = new File(jobFilename);
		if (jobFile.exists()) {
			try {
				// Job configuration
				XMLConfiguration jobConf = new XMLConfiguration(jobFile);

				// Report file verification
				boolean cont = continueCheckReport(jobConf);
				if (!cont) {
					return;
				}

				// Process header
				String header = "process";

				// Create and configure evaluation method
				String aname = jobConf.getString(header + "[@evaluation-method-type]");

				Class<IEvaluationMethod> aclass = (Class<IEvaluationMethod>) Class.forName(aname);

				IEvaluationMethod evaluationMethod = aclass.newInstance();

				// Configure runner
				if (evaluationMethod instanceof IConfigure) {
					((IConfigure) evaluationMethod).configure(jobConf.subset(header));
				}

				long t1 = System.currentTimeMillis();
				// Execute evaluation runner
				evaluationMethod.evaluate();

				method = evaluationMethod;

				t1 = System.currentTimeMillis() - t1;
				runtime = t1;
				System.out.println("Execution time: " + t1 + " ms");
			} catch (ConfigurationException e) {
				System.out.println("Configuration exception ");
			} catch (ClassNotFoundException e) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, e);
			} catch (IllegalAccessException e) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, e);
			} catch (InstantiationException e) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, e);
			} catch (IOException ex) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			System.out.println("Job file not found");
			System.exit(1);
		}
	}

	/**
	 * The evaluation method used
	 *
	 * @return The evaluation method used.
	 */
	public IEvaluationMethod getMethod() {
		return method;
	}

	/**
	 * Time of execution of the program in milliseconds
	 *
	 * @return Get the run time
	 */
	public long getRuntime() {
		return runtime;
	}

	/**
	 * Check if an experiment is finished.
	 *
	 * @param jobConf
	 *            The configuration already loaded from xml file.
	 * @return Return true if the experiment must continue (if it is not
	 *         finished), otherwise return false
	 *
	 * @throws ClassNotFoundException
	 *             Launch an exception in case that an error occurs
	 * @throws InstantiationException
	 *             Launch an exception in case that an error occurs
	 * @throws IllegalAccessException
	 *             Launch an exception in case that an error occurs
	 * @throws IOException
	 *             Launch an exception in case that an error occurs
	 */
	public boolean continueCheckReport(XMLConfiguration jobConf)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

		String[][] td = getTitleDirectoryReports(jobConf);

		if (td.length == 0) {
			return true;
		}

		Class<IEvaluationMethod> aclass = (Class<IEvaluationMethod>) Class
				.forName(jobConf.getString("process[@evaluation-method-type]"));

		IEvaluationMethod evaluationMethod = aclass.newInstance();

		File[] fileReports = getFileReports(td, evaluationMethod);

		for (File newFile : fileReports) {
			if (newFile.exists() && FileUtil.isReport(newFile)) {
				if (!FileUtil.reportFinish(newFile)) {
					deleteReports(fileReports, evaluationMethod);
					return true;
				}
			} else {
				deleteReports(fileReports, evaluationMethod);
				return true;
			}
		}

		for (File newFile : fileReports) {
			System.out.println("\nReport file -" + newFile + "- already finished\n");
		}

		return false;
	}

	/**
	 * Search in the xml file (listeners specifically) all the report's file and
	 * extract the title and directory.
	 *
	 * @param jobConf
	 *            The configuration from xml file.
	 * @return The title and directory of each ClassicalReporterListener.
	 */
	public String[][] getTitleDirectoryReports(XMLConfiguration jobConf) {
		ArrayList<String[]> dev = new ArrayList<String[]>();

		Configuration configuration = jobConf.subset("process").subset("algorithm");
		String listenerError;
		// Number of defined listeners
		int numberOfListeners = configuration.getList("listener[@type]").size();
		// For each listener in list
		for (int i = 0; i < numberOfListeners; i++) {
			String header = "listener(" + i + ")";
			listenerError = "listener type= ";
			try {
				// Listener classname
				String listenerClassname = configuration.getString(header + "[@type]");
				listenerError += listenerClassname;
				// Listener class
				Class<? extends IAlgorithmListener> listenerClass = (Class<? extends IAlgorithmListener>) Class
						.forName(listenerClassname);
				// Listener instance
				IAlgorithmListener listener = listenerClass.newInstance();
				if (listener instanceof ClassicalReporterListener) {
					ClassicalReporterListener temp = (ClassicalReporterListener) listener;
					temp.configure(configuration.subset(header));

					if (temp.isReportOnFile()) {
						dev.add(new String[] { temp.getReportTitle(), temp.getReportDirectory() });
					}
				}

			} catch (ClassNotFoundException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			} catch (InstantiationException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			} catch (IllegalAccessException e) {
				throw new ConfigurationRuntimeException("\nIllegal listener classname: " + listenerError, e);
			}
		}

		return dev.toArray(new String[dev.size()][2]);
	}

	/**
	 * According with the title, the directory and the evaluationMethod,
	 * construct the pathname of the report.
	 *
	 * @param titleDirectory The title of the directory
	 * @param evaluationMethod The evaluation method used
	 * @return The file reports
	 */
	public File[] getFileReports(String[][] titleDirectory, IEvaluationMethod evaluationMethod) {
		File[] dev = new File[titleDirectory.length];
		int cont = 0;
		for (String[] titleDirectory1 : titleDirectory) {
			File dir = new File(titleDirectory1[1]);

			if ((evaluationMethod instanceof kFoldCrossValidation)
					|| (evaluationMethod instanceof LeaveOneOutCrossValidation)
					|| (evaluationMethod instanceof FivePerTwoCrossValidation)) {

				dev[cont++] = FileUtil.reportFile(dir.getAbsolutePath() + "/General results-" + titleDirectory1[0]);

			} else {

				dev[cont++] = FileUtil.reportFile(dir.getAbsolutePath() + "/" + titleDirectory1[0]);

			}
		}
		return dev;
	}

	/**
	 * Deletes the reports according with the evaluationMethod, e.g. if it is a
	 * type of cross-validation will have several number of fold associated and
	 * they must to be eliminated.
	 *
	 * @param reports
	 *            The reports to delete.
	 * @param evaluationMethod
	 *            The evaluation method from xml file.
	 */
	public void deleteReports(File[] reports, IEvaluationMethod evaluationMethod) {

		for (File newFile : reports) {
			if (evaluationMethod instanceof HoldOut) {
				// do nothing
			} else if ((evaluationMethod instanceof kFoldCrossValidation)
					|| (evaluationMethod instanceof LeaveOneOutCrossValidation)
					|| (evaluationMethod instanceof FivePerTwoCrossValidation)) {

				String search = newFile.getName()
						.substring(newFile.getName().indexOf("General results") + "General results".length());
				FileUtil.deleteFoldsFiles(newFile, search);
			}

			newFile.delete();
		}

	}
}
