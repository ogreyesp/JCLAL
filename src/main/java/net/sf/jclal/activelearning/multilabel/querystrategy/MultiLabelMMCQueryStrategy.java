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
package net.sf.jclal.activelearning.multilabel.querystrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import mulan.transformations.BinaryRelevanceTransformation;
import net.sf.jclal.classifier.BinaryRelevance;
import net.sf.jclal.classifier.MulanClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOsync;
import weka.classifiers.functions.SimpleLogistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Implementation of MMC (Maximal loss reduction with maximal confidence) active
 * strategy.
 *
 * See for more information Yang, B., Sun, J., Wang, T., and Chen, Z. (2009).
 * Effective Multi-Label Active Learning for Text Classification. KDD-09.
 *
 * @author Oscar Gabriel Reyes Pupo
 *
 */
public class MultiLabelMMCQueryStrategy extends AbstractMultiLabelQueryStrategy {

	// LR classifier used
	private SimpleLogistic logistic;

	private Instances newDataset;

	private static final long serialVersionUID = 2286631838503958674L;

	private Classifier[] smos;

	private BinaryRelevanceTransformation brt;

	// To store the probabilities that the current instance belong to the i-th
	// label
	private double classifiersOutputs[];

	/**
	 * Empty (default) constructor
	 */
	public MultiLabelMMCQueryStrategy() {

		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void training() {

		// Train a multi-label SVM classifier based on current labeled set
		super.training();

		// Create the dataset for logistic regresion
		createDataSet();

		// Run the LR-based label prediction
		trainLogisticRegresion();

	}

	/**
	 * Create the dataset
	 */
	public void createDataSet() {

		if (!(((MulanClassifier) getClassifier()).getInternalClassifier() instanceof BinaryRelevance)) {
			System.err
					.println("The MMC query strategy must be configured with the Parallel Binary Relevance algorithm");
		}

		BinaryRelevance br = (BinaryRelevance) ((MulanClassifier) getClassifier()).getInternalClassifier();

		// One SVM classiier for each label
		smos = br.getEnsemble();

		if (!(smos[0] instanceof SMOsync)) {
			System.err.println(
					"The base classifiers of the Binary Relevance algorithm on the MMC query strategy must be SVM");
		}

		brt = br.getBrt();

		classifiersOutputs = new double[getNumLabels()];

		// To construct the new dataset
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		ArrayList<String> classes = new ArrayList<String>();

		for (int l = 0; l < getNumLabels(); l++) {

			Attribute newAtt = new Attribute("l" + l, l);

			attributes.add(newAtt);

			classes.add(String.valueOf(l));
		}

		classes.add(String.valueOf(getNumLabels()));

		attributes.add(new Attribute("LabelClass", classes, getNumLabels()));

		// The new transformed dataset
		newDataset = new Instances("TransformedDataSet", attributes, 0);

		newDataset.setClassIndex(getNumLabels());

	}

	/**
	 * Trains the logistic regression
	 */
	public void trainLogisticRegresion() {

		try {

			// To clear the current instances in the transformed dataset
			newDataset.clear();

			// Each instance is transformed according to LR-based label
			// prediction method proposed

			Instances labeledSet = getLabelledData().getDataset();

			for (Instance instanceLabeled : labeledSet) {

				Instance newInstance = convertInstance(instanceLabeled);

				newInstance.setDataset(newDataset);

				newDataset.add(newInstance);
			}

			// To train the LR classifier
			logistic = new SimpleLogistic();

			logistic.buildClassifier(newDataset);

		} catch (Exception e) {

			Logger.getLogger(MultiLabelMMCQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * Convert an instance
	 *
	 * @param instance
	 *            A multilabel instance
	 * @return The converted instance
	 */
	public Instance convertInstance(Instance instance) {

		try {

			double[] labelsProbability = new double[getNumLabels()];

			int cantLabels = 0;

			for (int l = 0; l < getNumLabels(); l++) {

				Instance transformedInstance = brt.transformInstance(instance, l);

				classifiersOutputs[l] = Math.abs(((SMOsync) smos[l]).SVMOutput(transformedInstance));

				// Probability's calculation
				labelsProbability[l] = 1 / (1 + Math.exp(classifiersOutputs[l] + 1));

				if (Utils.eq(instance.value(getLabelIndices()[l]), 1.0)) {

					++cantLabels;
				}
			}

			// Probability's normalization
			Utils.normalize(labelsProbability);

			// Order
			Arrays.sort(labelsProbability);

			double vector[] = new double[getNumLabels() + 1];

			// To store in descending order
			for (int i = 0; i < getNumLabels(); i++) {

				vector[i] = labelsProbability[getNumLabels() - i - 1];
			}

			// The class is the number of relevant labels
			vector[getNumLabels()] = cantLabels;

			DenseInstance newInstance = new DenseInstance(1.0, vector);

			return newInstance;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.getLogger(MultiLabelMMCQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		try {

			// Convert the current instance according to LR-based method
			// proposed. The classifiersOutputs array stores the probability for
			// the current instance
			Instance newInstance = convertInstance(instance);

			newInstance.setDataset(newDataset);

			// To classify the converted instance according to LR-based method
			double classes = logistic.classifyInstance(newInstance);

			double score = 0;

			int yl = 1;

			// Score calculation
			for (int l = 0; l < getNumLabels(); l++) {

				if (l > classes) {
					yl = -1;
				}

				score += (1 - yl * classifiersOutputs[l]) / 2;

			}

			return score;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.getLogger(MultiLabelMMCQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return 0;
	}
}