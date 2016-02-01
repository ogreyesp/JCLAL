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
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.listener.ClassicalReporterListener;
import net.sf.jclal.sampling.AbstractSampling;
import net.sf.jclal.util.dataset.DatasetUtils;

import org.apache.commons.configuration.Configuration;

/**
 * Leave One Out Cross Validation evaluation method.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class LeaveOneOutCrossValidation extends AbstractEvaluationMethod {

    private static final long serialVersionUID = 5933144357979580869L;

    /**
     * Average of the evaluations
     */
    private List<AbstractEvaluation> generalEvaluations;

    private int number;

    private String dataSetName, batchModeName, scenarioName, strategyName,
            classifierName;

    private int batchSize;

    private boolean fill = false;

    /**
     * Constructor
     *
     * @param algorithm The algorithm.
     * @param dataset The dataset.
     */
    public LeaveOneOutCrossValidation(AbstractALAlgorithm algorithm,
            IDataset dataset) {
        super(algorithm, dataset);
    }

    /**
     * Empty(default) constructor.
     */
    public LeaveOneOutCrossValidation() {
    }

    /**
     * Executes the process of evaluation of the experiment
     */
    @Override
    public void evaluate() {

        try {

            loadData();

            if (getDataset() != null) {

                DatasetUtils.randomize(createRandGen(), getDataset());

                List<AbstractEvaluation> currentFoldEvaluations;

                number = getDataset().getNumInstances();

                Date dateBeging = new Date(System.currentTimeMillis());

                for (int i = 0; i < getDataset().getNumInstances(); i++) {

                    IDataset dataset = getDataset().copy();

                    IDataset testSet = dataset.removeAllIndexes(
                            new int[]{i});

                    // The list of evaluations is stored
                    currentFoldEvaluations = executeFold(dataset, testSet, i);

                    if (generalEvaluations == null) {
                        generalEvaluations = currentFoldEvaluations;
                    } else {
                        addFoldEvaluations(currentFoldEvaluations);
                    }

                }

                // Average the evaluations over the folds
                averageEvaluations();

                // Simulate the general AL process
                simulateALProcess(dateBeging);

                setFinalEvaluations(generalEvaluations);
            }
        } catch (Exception e) {
            Logger.getLogger(LeaveOneOutCrossValidation.class.getName()).log(
                    Level.SEVERE, null, e);
        }
    }

    private List<AbstractEvaluation> executeFold(IDataset trainDataSet,
            IDataset testDataSet, int fold) {
        try {

            // Resample the instances to construct the labeled and unlabeled set
            getSamplingStrategy().sampling(trainDataSet);

            IAlgorithm algorithmCopy;

            algorithmCopy = getAlgorithm().makeCopy();

            algorithmCopy
                    .setLabeledDataSet(((AbstractSampling) getSamplingStrategy())
                            .getLabeledData());

            algorithmCopy
                    .setUnlabeledDataSet(((AbstractSampling) getSamplingStrategy())
                            .getUnlabeledData());

            algorithmCopy.setTestDataSet(testDataSet);

            // if the listener are ClassicalReporterListener
            ClassicalReporterListener classicalListener = null;

            for (IAlgorithmListener listener : algorithmCopy.getListeners()) {
                if (listener instanceof ClassicalReporterListener) {
                    classicalListener = (ClassicalReporterListener) listener;

                    classicalListener.setReportTitle("Fold " + (fold + 1) + "-"
                            + classicalListener.getReportTitle());
                }
            }

            algorithmCopy.execute();

            if (!fill) {
                fill = true;
                fillFields(algorithmCopy);
            }

            return algorithmCopy.getScenario().getQueryStrategy()
                    .getEvaluations();

        } catch (Exception ex) {
            Logger.getLogger(LeaveOneOutCrossValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void configure(Configuration configuration) {
        super.configure(configuration);
    }

    private void addFoldEvaluations(
            List<AbstractEvaluation> currentFoldEvaluations) {

        for (int i = 0; i < currentFoldEvaluations.size(); i++) {

            generalEvaluations.set(
                    i,
                    addEvaluation(generalEvaluations.get(i),
                            currentFoldEvaluations.get(i)));
        }
    }

    private void averageEvaluations() {

        for (AbstractEvaluation evaluation : generalEvaluations) {

            for (String metricName : evaluation.getMetricNames()) {
                evaluation.setMetricValue(metricName,
                        evaluation.getMetricValue(metricName) / number);
            }
        }
    }

    /**
     * This method is internally used for a k Fold Cross Validation evaluation
     * method
     *
     * @param evaluationOld the old evaluation
     * @param evaluationNew the new evaluation to add
     *
     * @return The evaluation
     */
    private AbstractEvaluation addEvaluation(AbstractEvaluation evaluationOld,
            AbstractEvaluation evaluationNew) {

        for (String metricName : evaluationOld.getMetricNames()) {

            double value1 = evaluationOld.getMetricValue(metricName);
            double value2 = evaluationNew.getMetricValue(metricName);

            value1 = Double.isNaN(value1) ? 0 : value1;
            value2 = Double.isNaN(value2) ? 0 : value2;

            evaluationOld.setMetricValue(metricName, value1 + value2);
        }

        return evaluationOld;
    }

    private void simulateALProcess(Date dateBegin) {
        try {

            AbstractALAlgorithm algorithm = (ClassicalALAlgorithm) getAlgorithm()
                    .makeCopy();

            algorithm.getScenario().getQueryStrategy()
                    .setEvaluations(generalEvaluations);

            // if the listener are ClassicalReporterListener
            ClassicalReporterListener classicalListener = null;

            for (IAlgorithmListener listener : algorithm.getListeners()) {
                if (listener instanceof ClassicalReporterListener) {
                    classicalListener = (ClassicalReporterListener) listener;
                    classicalListener.setReportTitle("General results-"
                            + classicalListener.getReportTitle());

                    if (!generalEvaluations.isEmpty()) {
                        classicalListener.algorithmStarted(dateBegin, dataSetName,
                                1, generalEvaluations.get(0).getLabeledSetSize(),
                                generalEvaluations.get(0).getUnlabeledSetSize(),
                                batchModeName, batchSize, classifierName, scenarioName,
                                strategyName);
                    }
                }
            }

            // Simulated the AL process
            for (int i = 1; i < generalEvaluations.size(); i++) {

                ((ClassicalALAlgorithm) algorithm).setIteration(i);

                algorithm.fireIterationCompleted();

            }

            ((ClassicalALAlgorithm) algorithm).setIteration(generalEvaluations
                    .size());
            algorithm.fireAlgorithmFinished();

        } catch (Exception ex) {
            Logger.getLogger(LeaveOneOutCrossValidation.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    private void fillFields(IAlgorithm algorithmCopy) {

        dataSetName = algorithmCopy.getScenario().getQueryStrategy()
                .getLabelledData().getDataset().relationName();

        AbstractBatchMode batchMode = (AbstractBatchMode) ((AbstractScenario) algorithmCopy
                .getScenario()).getBatchMode();

        batchModeName = batchMode.toString();

        batchSize = batchMode.getBatchSize();

        classifierName = algorithmCopy.getScenario().getQueryStrategy()
                .getClassifier().toString();

        scenarioName = algorithmCopy.getScenario().toString();

        strategyName = algorithmCopy.getScenario().getQueryStrategy()
                .toString();

    }
}
