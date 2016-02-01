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
package net.sf.jclal.listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm;
import net.sf.jclal.activelearning.oracle.AbstractOracle;
import net.sf.jclal.core.AlgorithmEvent;
import net.sf.jclal.core.IConfigure;
import org.apache.commons.configuration.Configuration;

/**
 * This class is a listener for a real scenario.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class RealScenarioListener extends ClassicalReporterListener implements IConfigure {

	private static final long serialVersionUID = -6866004037911080430L;

	/**
	 * Interaction with the user.
	 */
	private BufferedReader in;

	/**
	 * File path to save the informative instances.
	 */
	private String informativeInstances;

	/**
	 * Write in file.
	 */
	private BufferedWriter writer;

	public RealScenarioListener() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		super.algorithmStarted(event);

		in = new BufferedReader(new InputStreamReader(System.in));

		if (getInformativeInstances() == null || getInformativeInstances().isEmpty()) {
			setInformativeInstances(newInformativeInstances());
		}

		File keep = new File(getInformativeInstances());
		keep.getParentFile().mkdirs();

		try {
			writer = Files.newBufferedWriter(new File(getInformativeInstances()).toPath(), Charset.defaultCharset(),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);

		} catch (IOException ex) {
			Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
		}

		///////////////////////////////////
		System.out.println(
				"\nWelcome to the real scenario listener. You can use it to obtain the most informative unlabeled data."
						+ "\nAt the end of this session you can save the labeled data for further analysis."
						+ "\nYou must specify in the configuration file the parameter <informative-instances> for setting the path where the "
						+ "instances that were labeled will be saved."
						+ "\nThis example of real-world use can be combined with a simulated or a human oracle."
						+ "\nThe labeled instances will be stored sequentially in: " + getInformativeInstances() + "."
						+ "\nIf you are ready to begin please press <enter>.");

		readLine();

		System.out.println("Active Learning begins...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iterationCompleted(AlgorithmEvent event) {

		super.iterationCompleted(event);

		saveInFile(event);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmFinished(AlgorithmEvent event) {

		super.algorithmFinished(event);

		saveInFile(event);

		try {
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println("The labeled instances were saved in: " + getInformativeInstances());
	}

	/**
	 * Save in file the last instances processed by the oracle.
	 *
	 * @param event
	 *            The algorithm's event
	 */
	public void saveInFile(AlgorithmEvent event) {

		ClassicalALAlgorithm alg = (ClassicalALAlgorithm) event.getAlgorithm();

		ArrayList<String> lastInstances = ((AbstractOracle) alg.getScenario().getOracle()).getLastLabeledInstances();

		for (String last : lastInstances) {
			try {
				writer.append(last).append("\n");
			} catch (IOException ex) {
				Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		try {
			writer.flush();
		} catch (IOException ex) {
			Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private String readLine() {
		try {
			return in.readLine();
		} catch (IOException ex) {
			Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Interaction with the user.
	 *
	 * @return A BufferedReader object
	 */
	private BufferedReader getIn() {
		return in;
	}

	/**
	 * Interaction with the user.
	 *
	 * @param in
	 *            The BufferedReader
	 */
	public void setIn(BufferedReader in) {
		this.in = in;
	}

	/**
	 * Creates a new path to the file that stores the informative instances.
	 *
	 * @return The path to the file
	 */
	public String newInformativeInstances() {
		try {
			return File.createTempFile("informative-instances-", ".txt", new File("./")).getAbsolutePath();
		} catch (IOException ex) {
			Logger.getLogger(RealScenarioListener.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "informative-instances.txt";
	}

	/**
	 * File path to save the informative instances.
	 * 
	 * @return The informative instances
	 */
	public String getInformativeInstances() {
		return informativeInstances;
	}

	/**
	 * Path to save the informative instances.
	 * 
	 * @param informativeInstances
	 *            The informative instances
	 * 
	 */
	public void setInformativeInstances(String informativeInstances) {
		this.informativeInstances = informativeInstances;
	}

	/**
	 * @param configuration
	 *            The configuration object for the Real Scenario Listener.
	 *
	 *            The XML labels supported are:
	 *
	 *            <ul>
	 *            <li>informative-instances= filepath where will be saved the
	 *            informative instances</li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		super.configure(configuration);

		String informative = configuration.getString("informative-instances", getInformativeInstances());
		setInformativeInstances(informative);
	}
}