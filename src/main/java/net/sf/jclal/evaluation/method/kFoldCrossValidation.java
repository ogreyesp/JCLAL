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
package net.sf.jclal.evaluation.method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jclal.activelearning.algorithm.AbstractALAlgorithm;
import net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm;
import net.sf.jclal.activelearning.batchmode.AbstractBatchMode;
import net.sf.jclal.activelearning.scenario.AbstractScenario;
import net.sf.jclal.core.IAlgorithm;
import net.sf.jclal.core.IAlgorithmListener;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.MulanDataset;
import net.sf.jclal.dataset.WekaDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.listener.ClassicalReporterListener;
import net.sf.jclal.sampling.AbstractSampling;
import net.sf.jclal.util.dataset.DatasetUtils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * K-Fold Cross Validation evaluation method.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class kFoldCrossValidation extends AbstractEvaluationMethod {

	private static final long serialVersionUID = 5933144357979580869L;

	/**
	 * Whether or not the dataset will be stratified
	 */
	private boolean stratify;
	/**
	 * The number of folds in the cross-validation. By default is 10
	 */
	private int numFolds = 10;
	/**
	 * Average of the evaluations
	 */
	private List<AbstractEvaluation> generalEvaluations;

	private List<Integer> counter;

	private int testSize = 0;

	private String dataSetName, batchModeName, scenarioName, strategyName, classifierName;

	private int batchSize;

	private boolean fill = false;

	/**
	 * Constructor
	 *
	 * @param algorithm
	 *            The algorithm.
	 * @param dataset
	 *            The dataset.
	 */
	public kFoldCrossValidation(AbstractALAlgorithm algorithm, IDataset dataset) {
		super(algorithm, dataset);
	}

	/**
	 * Empty(default) constructor.
	 */
	public kFoldCrossValidation() {
	}

	/**
	 * Execute the evaluation process
	 */
	@Override
	public void evaluate() {

		try {

			loadData();

			counter = new ArrayList<Integer>();

			if (getDataset() != null) {

				MulanDataset multiLabelDataSet[] = null;

				DatasetUtils.randomize(createRandGen(), getDataset());

				// Stratification process
				if (stratify) {
					if (!isMultiLabel()) {
						DatasetUtils.stratifySingleLabelDataSet(numFolds, (WekaDataset) getDataset());
					} else {
						multiLabelDataSet = DatasetUtils.stratifyMultiLabelDataSet(numFolds,
								(MulanDataset) getDataset());
					}
				}

				List<AbstractEvaluation> currentFoldEvaluations;

				Date dateBeging = new Date(System.currentTimeMillis());

				for (int i = 0; i < numFolds; i++) {

					if (!isMultiLabel()) {

						// The list of evaluations is stored
						currentFoldEvaluations = executeFold(DatasetUtils.trainCV(getDataset(), numFolds, i),
								DatasetUtils.testCV(getDataset(), numFolds, i), i);

					} else {
						// The list of evaluations is stored
						currentFoldEvaluations = executeFold(DatasetUtils.trainCV(multiLabelDataSet, i),
								DatasetUtils.testCV(multiLabelDataSet, i), i);

					}

					if (generalEvaluations == null) {
						generalEvaluations = currentFoldEvaluations;
						fillCounter(generalEvaluations.size());
					} else {
						addFoldEvaluations(currentFoldEvaluations);
					}

				}

				// Average the evaluations across the folds
				averageEvaluations();

				// Simulate the general AL process
				simulateALProcess(dateBeging);

				setFinalEvaluations(generalEvaluations);
			}
		} catch (Exception e) {
			Logger.getLogger(kFoldCrossValidation.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private List<AbstractEvaluation> executeFold(IDataset trainDataSet, IDataset testDataSet, int fold) {
		try {

			// Resample the instances to construct the labeled and unlabeled set
			getSamplingStrategy().sampling(trainDataSet);

			IAlgorithm algorithmCopy;

			algorithmCopy = getAlgorithm().makeCopy();

			algorithmCopy.setLabeledDataSet(((AbstractSampling) getSamplingStrategy()).getLabeledData());

			algorithmCopy.setUnlabeledDataSet(((AbstractSampling) getSamplingStrategy()).getUnlabeledData());

			testSize += testDataSet.getNumInstances();

			algorithmCopy.setTestDataSet(testDataSet);

			// if the listener are ClassicalReporterListener
			ClassicalReporterListener classicalListener = null;

			for (IAlgorithmListener listener : algorithmCopy.getListeners()) {
				if (listener instanceof ClassicalReporterListener) {
					classicalListener = (ClassicalReporterListener) listener;

					classicalListener.setReportTitle("Fold " + (fold + 1) + "-" + classicalListener.getReportTitle());
				}
			}

			algorithmCopy.execute();

			if (!fill) {
				fill = true;
				fillFields(algorithmCopy);
			}

			return algorithmCopy.getScenario().getQueryStrategy().getEvaluations();

		} catch (Exception ex) {
			Logger.getLogger(kFoldCrossValidation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	private void fillFields(IAlgorithm algorithmCopy) {

		dataSetName = algorithmCopy.getScenario().getQueryStrategy().getLabelledData().getDataset().relationName();

		AbstractBatchMode batchMode = (AbstractBatchMode) ((AbstractScenario) algorithmCopy.getScenario())
				.getBatchMode();

		batchModeName = batchMode.toString();

		batchSize = batchMode.getBatchSize();

		classifierName = algorithmCopy.getScenario().getQueryStrategy().getClassifier().toString();

		scenarioName = algorithmCopy.getScenario().toString();

		strategyName = algorithmCopy.getScenario().getQueryStrategy().toString();

	}

	/**
	 * Return if the dataset is stratify
	 * 
	 * @return If the dataset is stratify.
	 */
	public boolean isStratify() {
		return stratify;
	}

	/**
	 * Set if the dataset is stratify
	 *
	 * @param stratify
	 *            Set if the dataset is stratify.
	 */
	public void setStratify(boolean stratify) {
		this.stratify = stratify;
	}

	/**
	 * Get the number of folds
	 *
	 * @return The number of folds
	 */
	public int getNumFolds() {
		return numFolds;
	}

	/**
	 * Set the number of folds
	 * 
	 * @param numFolds
	 *            The number of folds used to stratify.
	 */
	public void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}

	/**
	 * @param configuration
	 *            The configuration of K-Fold cross validation. The XML labels
	 *            supported are:
	 *
	 *            <ul>
	 *            <li><b>stratify= boolean</b></li>
	 *            <li><b>num-folds= int</b></li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {
		super.configure(configuration);

		// Set stratify (default false)
		boolean stratifyValue = configuration.getBoolean("stratify", stratify);
		setStratify(stratifyValue);

		// num folds
		int numFols = configuration.getInt("num-folds", numFolds);
		if (numFols < 1) {
			throw new ConfigurationRuntimeException("\n<num-folds>" + numFols + "</num-folds>. " + "num-folds > 0");
		}
		setNumFolds(numFols);
	}

	private void addFoldEvaluations(List<AbstractEvaluation> currentFoldEvaluations) {

		for (int i = 0; i < currentFoldEvaluations.size(); i++) {

			if (i == generalEvaluations.size()) {
				generalEvaluations.add(currentFoldEvaluations.get(i));
			} else {
				generalEvaluations.set(i, addEvaluation(generalEvaluations.get(i), currentFoldEvaluations.get(i)));
			}

			if (i == counter.size()) {
				counter.add(1);
			} else {
				counter.set(i, counter.get(i) + 1);
			}

		}
	}

	private void averageEvaluations() {

		int index = 0;

		for (AbstractEvaluation evaluation : generalEvaluations) {

			evaluation.setLabeledSetSize(evaluation.getLabeledSetSize() / counter.get(index));
			evaluation.setUnlabeledSetSize(evaluation.getUnlabeledSetSize() / counter.get(index));

			for (String metricName : evaluation.getMetricNames()) {
				evaluation.setMetricValue(metricName, evaluation.getMetricValue(metricName) / counter.get(index));
			}

			++index;
		}
	}

	/**
	 * This method is internally used for a k Fold Cross Validation evaluation
	 * method
	 *
	 * @param evaluationOld
	 *            the old evaluation
	 * @param evaluationNew
	 *            the new evaluation to add
	 *
	 * @return The evaluation
	 */
	private AbstractEvaluation addEvaluation(AbstractEvaluation evaluationOld, AbstractEvaluation evaluationNew) {

		evaluationOld.setLabeledSetSize(evaluationOld.getLabeledSetSize() + evaluationNew.getLabeledSetSize());
		evaluationOld.setUnlabeledSetSize(evaluationOld.getUnlabeledSetSize() + evaluationNew.getUnlabeledSetSize());

		for (String metricName : evaluationOld.getMetricNames()) {

			double value1 = evaluationOld.getMetricValue(metricName);
			double value2 = evaluationNew.getMetricValue(metricName);

			value1 = Double.isNaN(value1) ? 0 : value1;
			value2 = Double.isNaN(value2) ? 0 : value2;

			evaluationOld.setMetricValue(metricName, value1 + value2);
		}

		return evaluationOld;
	}

	private void fillCounter(int numOfIterations) {

		counter = new ArrayList<Integer>(numOfIterations);

		for (int i = 0; i < numOfIterations; i++) {
			counter.add(1);
		}
	}

	private void simulateALProcess(Date dateBegin) {
		try {

			AbstractALAlgorithm algorithm = (ClassicalALAlgorithm) getAlgorithm().makeCopy();

			algorithm.getScenario().getQueryStrategy().setEvaluations(generalEvaluations);

			// if the listener are ClassicalReporterListener
			ClassicalReporterListener classicalListener = null;

			for (IAlgorithmListener listener : algorithm.getListeners()) {
				if (listener instanceof ClassicalReporterListener) {
					classicalListener = (ClassicalReporterListener) listener;
					classicalListener.setReportTitle("General results-" + classicalListener.getReportTitle());

					if (!generalEvaluations.isEmpty()) {
						classicalListener.algorithmStarted(dateBegin, dataSetName, testSize / numFolds,
								generalEvaluations.get(0).getLabeledSetSize(),
								generalEvaluations.get(0).getUnlabeledSetSize(), batchModeName, batchSize,
								classifierName, scenarioName, strategyName);
					}
				}
			}

			// Simulated the AL process
			for (int i = 1; i < generalEvaluations.size(); i++) {

				((ClassicalALAlgorithm) algorithm).setIteration(i);

				algorithm.fireIterationCompleted();

			}

			((ClassicalALAlgorithm) algorithm).setIteration(generalEvaluations.size());
			algorithm.fireAlgorithmFinished();

		} catch (Exception ex) {
			Logger.getLogger(kFoldCrossValidation.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
