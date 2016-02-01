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
package net.sf.jclal.examples;

import net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm;
import net.sf.jclal.activelearning.batchmode.QBestBatchMode;
import net.sf.jclal.activelearning.oracle.SimulatedOracle;
import net.sf.jclal.activelearning.singlelabel.querystrategy.EntropySamplingQueryStrategy;
import net.sf.jclal.activelearning.stopcriteria.MaxIteration;
import net.sf.jclal.activelearning.stopcriteria.UnlabeledSetEmpty;
import net.sf.jclal.activelearning.scenario.PoolBasedSamplingScenario;
import net.sf.jclal.classifier.WekaClassifier;
import net.sf.jclal.core.IClassifier;
import net.sf.jclal.core.IQueryStrategy;
import net.sf.jclal.core.IStopCriterion;
import net.sf.jclal.evaluation.method.HoldOut;
import net.sf.jclal.listener.GraphicalReporterListener;
import net.sf.jclal.sampling.supervised.Resample;
import net.sf.jclal.util.random.RanecuFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

/**
 * Example of hold out experiment that uses the entropy sampling as query
 * strategy, pool-base as scenario and the classical AL process.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 *
 */
public class HoldOutExample {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		String fileName = "datasets/iris/iris.arff";

		// The initial labeled set from the training set is randomly
		// selected
		Resample sampling = new Resample();

		sampling.setNoReplacement(false);
		sampling.setInvertSelection(false);

		sampling.setPercentageInstancesToLabelled(5);

		// Set the scenario to use
		PoolBasedSamplingScenario scenario = new PoolBasedSamplingScenario();
		QBestBatchMode batchMode = new QBestBatchMode();
		batchMode.setBatchSize(1);

		scenario.setBatchMode(batchMode);

		// Set the oracle
		SimulatedOracle oracle = new SimulatedOracle();

		scenario.setOracle(oracle);

		// Set the query strategy to use
		IQueryStrategy queryStrategy = new EntropySamplingQueryStrategy();

		// Set the base classifier to use in the query strategy
		IClassifier model = new WekaClassifier();

		Classifier classifier = new NaiveBayes();

		((WekaClassifier) model).setClassifier(classifier);

		// Set the model into the query strategy
		queryStrategy.setClassifier(model);

		// Set the query strategy into the scenario
		scenario.setQueryStrategy(queryStrategy);

		// Set the algorithm's listeners
		GraphicalReporterListener visual = new GraphicalReporterListener();
		visual.setReportOnFile(false);
		visual.setShowSeparateWindow(true);
		visual.setReportFrequency(1);

		// Construct the AL algorithm
		ClassicalALAlgorithm algorithm = new ClassicalALAlgorithm();

		// Set the listener for the algorithm
		algorithm.addListener(visual);

		// Set the stop criteria

		MaxIteration stop1 = new MaxIteration();
		stop1.setMaxIteration(45);

		UnlabeledSetEmpty stop2 = new UnlabeledSetEmpty();

		algorithm.addStopCriterion(stop1);
		algorithm.addStopCriterion(stop2);

		// Set the scenario into the algorithm
		algorithm.setScenario(scenario);

		// Set the evaluation method to use
		HoldOut method = new HoldOut();

		// Set the sampling strategy into the algorithm
		method.setSamplingStrategy(sampling);

		// Set the path of the dataset
		method.setFileDataset(fileName);

		// Set the 66% of the total of instances to train the model
		method.setPercentageToSplit(66);

		RanecuFactory random = new RanecuFactory();
		random.setSeed(9871234);

		method.setRandGenFactory(random);

		// Set the algorithm into the evaluation method
		method.setAlgorithm(algorithm);

		// To evaluate the algorithm
		method.evaluate();

	}
}
