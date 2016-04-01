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
package net.sf.jclal.activelearning.querystrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jclal.classifier.MOAClassifier;
import net.sf.jclal.classifier.MulanClassifier;
import net.sf.jclal.classifier.WekaClassifier;
import net.sf.jclal.core.IClassifier;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.core.IQueryStrategy;
import net.sf.jclal.dataset.WekaDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.sort.Container;
import net.sf.jclal.util.sort.OrderUtils;
import net.sf.jclal.util.thread.ThreadControl;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Abstract class for active learning strategies. All strategies must extend
 * this class.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public abstract class AbstractQueryStrategy implements IQueryStrategy, IConfigure {

	private static final long serialVersionUID = 1L;

	/**
	 * Pointer to unlabeled data
	 */
	private IDataset unlabelledData;

	/**
	 * Pointer to labeled data
	 */
	private IDataset labelledData;

	/**
	 * The dataset used to test.
	 */
	private IDataset testData;

	/**
	 * Pointer to the classifier used
	 */
	private IClassifier classifier;

	/**
	 * Indicates whether the query strategy is maximum or minimal. By default is
	 * maximal, i.e, it selects the k instances with the highest utility.
	 */
	private boolean maximal = true;

	/**
	 * To store the evaluations for each iterations.
	 */
	protected List<AbstractEvaluation> evaluations;

	/**
	 * To store the indexes of the selected instances to remove from unlabeled
	 * data
	 */
	private ArrayList<Integer> selectedInstances;

	/**
	 * To store the number of selected instances, it is used on incremental
	 * classifiers
	 */
	private int numberOfSelectedInstances;

	/**
	 * To store if the classifier has been trained
	 */
	private boolean firstTime;

	/**
	 * Set if the internal process can be parallized.
	 */
	private boolean parallel = false;

	/**
	 * Get the selected instances
	 * 
	 * @return Returns the indexes of the instances selected by the query
	 *         strategy.
	 */
	public ArrayList<Integer> getSelectedInstances() {
		return selectedInstances;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSelectedInstances(ArrayList<Integer> selectedInstances) {
		this.selectedInstances = selectedInstances;
	}

	/**
	 * Return true if the internal process is parallelized.
	 *
	 * @return True if the internal process is parallelized, false otherwise
	 */
	public boolean isParallel() {
		return parallel;
	}

	/**
	 * Set if the internal process must be parallelized.
	 *
	 * @param parallel
	 *            If the internal process must be parallelized.
	 */
	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	/**
	 * Empty (default) constructor.
	 */
	public AbstractQueryStrategy() {

		super();

		firstTime = true;

		evaluations = new ArrayList<AbstractEvaluation>();
		selectedInstances = new ArrayList<Integer>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractEvaluation> getEvaluations() {
		return evaluations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvaluations(List<AbstractEvaluation> evaluations) {
		this.evaluations = evaluations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IClassifier getClassifier() {
		return classifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClassifier(IClassifier classifier) {

		try {
			this.classifier = classifier.makeCopy();
		} catch (Exception e) {

			Logger.getLogger(AbstractQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getUnlabelledData() {
		return unlabelledData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUnlabelledData(IDataset unlabelledData) {
		this.unlabelledData = unlabelledData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getLabelledData() {
		return labelledData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLabelledData(IDataset labelledData) {
		this.labelledData = labelledData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximal(boolean max) {

		this.maximal = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMaximal() {
		return maximal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void training() {
		try {

			if (!firstTime) {

				if (classifier instanceof WekaClassifier) {

					if (((WekaClassifier) classifier).getClassifier() instanceof UpdateableClassifier) {

						IDataset newLabeledInstances = new WekaDataset(labelledData,
								labelledData.getNumInstances() - numberOfSelectedInstances, numberOfSelectedInstances);

						numberOfSelectedInstances = 0;

						for (int i = 0; i < newLabeledInstances.getNumInstances(); i++) {
							((UpdateableClassifier) ((WekaClassifier) classifier).getClassifier())
									.updateClassifier(newLabeledInstances.instance(i));
						}

						return;
					}

				}

				if (classifier instanceof MOAClassifier) {

					IDataset newLabeledInstances = new WekaDataset(labelledData,
							labelledData.getNumInstances() - numberOfSelectedInstances, numberOfSelectedInstances);

					numberOfSelectedInstances = 0;

					((MOAClassifier) classifier).updateClassifier(newLabeledInstances);

					return;

				}

				if (classifier instanceof MulanClassifier) {

					if (((MulanClassifier) classifier).getInternalClassifier() instanceof UpdateableClassifier) {

						IDataset newLabeledInstances = new WekaDataset(labelledData,
								labelledData.getNumInstances() - numberOfSelectedInstances, numberOfSelectedInstances);

						numberOfSelectedInstances = 0;

						for (int i = 0; i < newLabeledInstances.getNumInstances(); i++) {
							((UpdateableClassifier) ((MulanClassifier) classifier).getInternalClassifier())
									.updateClassifier(newLabeledInstances.instance(i));
						}

						return;
					}

				}
			}

			classifier.buildClassifier(getLabelledData());

			firstTime = false;

		} catch (Exception ex) {
			Logger.getLogger(AbstractQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testModel() {

		try {
			// test phase with the actual model
			AbstractEvaluation evaluation = classifier.testModel(testData);

			evaluation.setLabeledSetSize(getLabelledData().getNumInstances());

			evaluation.setUnlabeledSetSize(getUnlabelledData().getNumInstances());

			evaluations.add(evaluation);

		} catch (Exception e) {

			Logger.getLogger(AbstractQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] testUnlabeledData() {

		// found values of all unlabeled instances
		final double[] values = new double[getUnlabelledData().getNumInstances()];

		int pos = 0;

		Instances unlabeledInstances = getUnlabelledData().getDataset();

		ThreadControl thread = ThreadControl.defaultThreadControl(isParallel());

		for (final Instance instance : unlabeledInstances) {

			// parallel
			final int i = pos++;

			thread.execute(new Runnable() {

				@Override
				public void run() {
					values[i] = utilityInstance(instance);
				}
			});
			// end-parallel
		}

		thread.end();
		thread = null;

		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTestData(IDataset testData) {
		this.testData = testData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset getTestData() {
		return testData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] distributionForInstance(Instance instance) {
		try {

			return classifier.distributionForInstance(instance);

		} catch (Exception ex) {
			Logger.getLogger(AbstractQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 *
	 * @param configuration
	 *            The configuration object for the abstract query strategy. The
	 *            XML labels supported are:
	 *            <ul>
	 *            <li><b>maximal= boolean</b></li>
	 *            <li><b>wrapper-classifier type= class</b>
	 *            <p>
	 *            Package: net.sf.jclal.classifier
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		// Set max iteration
		boolean maximalT = configuration.getBoolean("maximal", isMaximal());
		setMaximal(maximalT);

		boolean parallel = configuration.getBoolean("parallel", isParallel());
		setParallel(parallel);

		String wrapperError = "wrapper-classifier type= ";
		try {
			// classifier classname
			String classifierClassname = configuration.getString("wrapper-classifier[@type]");

			wrapperError += classifierClassname;
			// classifier class
			Class<? extends IClassifier> classifierClass = (Class<? extends IClassifier>) Class
					.forName(classifierClassname);
			// classifier instance
			IClassifier classifierTemp = classifierClass.newInstance();
			// Configure classifier (if necessary)
			if (classifierTemp instanceof IConfigure) {
				((IConfigure) classifierTemp).configure(configuration.subset("wrapper-classifier"));
			}
			// Add this classifier to the query strategy
			setClassifier(classifierTemp);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal classifier classname: " + wrapperError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Illegal classifier classname: " + wrapperError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Illegal classifier classname: " + wrapperError, e);
		}
	}

	@Override
	public String toString() {
        StringBuilder extra = new StringBuilder();
        ThreadControl c = ThreadControl.defaultThreadControl(isParallel());
        extra.append("-").append(c.getDefaultCores()).append("cores");

        return this.getClass().getSimpleName().concat(extra.toString());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateLabeledData() {

		ArrayList<Container> ordered = new ArrayList<Container>();

		// Adds the instances to labeled set
		for (int index : selectedInstances) {
			labelledData.add(unlabelledData.instance(index));
			ordered.add(new Container(index, index));
		}

		// To order the array in descendant order
		OrderUtils.mergeSort(ordered, true);

		// Removes the instances from unlabeled set. The deleting operation must
		// be in descendant order
		for (Container pairValue : ordered) {

			unlabelledData.remove(Integer.parseInt(pairValue.getValue().toString()));
		}

		numberOfSelectedInstances = selectedInstances.size();

		// Clears the indexes of selected instances
		selectedInstances.clear();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmFinished() {
	}

}
