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

import java.util.logging.Level;
import java.util.logging.Logger;
import mulan.classifier.InvalidDataException;
import mulan.classifier.ModelInitializationException;
import mulan.classifier.MultiLabelLearnerBase;
import mulan.classifier.MultiLabelOutput;
import net.sf.jclal.classifier.MulanClassifier;
import net.sf.jclal.core.IConfigure;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.core.Instance;
import weka.core.Utils;

/**
 * Implementation of 3Dimensional strategy.
 *
 * In this moment only the CMN and CAN strategies are supported.
 *
 * See for more information Esuli, A., and Sebastiani, F. (2009). Active
 * Learning Strategies for Multi-Label Text Classification, 102-113.
 *
 * @author Oscar Gabriel Reyes Pupo
 *
 */
public class MultiLabel3DimensionalQueryStrategy extends AbstractMultiLabelQueryStrategy implements IConfigure {

	private static final long serialVersionUID = 1L;

	/*
	 * The evidence dimension has to do with the type of evidence we decide to
	 * use as a basis for ranking the unlabeled documents. This variable can
	 * store the following values:
	 * 
	 * C (Min Confidence): this corresponds to the notion of uncertainty
	 * sampling
	 * 
	 * S (MaxScore): This corresponds to the notion of relevance sampling
	 */
	private char evidenceDimension;

	/*
	 * The class dimension represents a policy on how to generate one
	 * class-independent piece of evidence from the l class-specific ones.This
	 * variable can store the following values:
	 * 
	 * M (Min/Max): The rationale of this policy is that we want the manual
	 * annotator to concentrate on the documents that are deemed to be extremely
	 * valuable at least for one class. We call this choice Min/Max (M).
	 * 
	 * A (Average): to average all values across all labels
	 * 
	 * R (Round Robin): the top-ranked examples for each class are picked, so
	 * that each class will be adequately championed in the resulting rank.
	 */
	private char classDimension;

	/*
	 * The weight dimension has to do with the fact that, in ranking the
	 * unlabeled documents, it might or it might not be desirable to treat all
	 * classes equally. This variable can store the following values:
	 * 
	 * N (No weighting): to treat all classes alike.
	 * 
	 * W (Weighting): One choice is to give more weight to those classes on
	 * which the current classifier is still performing badly, so as to prefer
	 * those documents that are likely to bring about an improvement where it is
	 * most needed.
	 */
	private char weightDimension;

	private MultiLabelLearnerBase classifier;

	/**
	 * Empty constructor
	 */
	public MultiLabel3DimensionalQueryStrategy() {

		super();

		evidenceDimension = 'C';
		classDimension = 'M';
		weightDimension = 'N';

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		try {

			classifier = ((MulanClassifier) getClassifier()).getInternalClassifier();

			// Evidence dimension
			MultiLabelOutput mloutput = classifier.makePrediction(instance);

			double[] confidences = mloutput.getConfidences();
			boolean[] bipartition = mloutput.getBipartition();

			double[] evidenceValues = new double[confidences.length];

			switch (evidenceDimension) {

			case 'C':

				for (int i = 0; i < confidences.length; i++) {

					int predictedLabel = (bipartition[i]) ? 1 : 0;

					evidenceValues[i] = 1 - Math.abs(predictedLabel - confidences[i]);
				}

				break;

			case 'S':

				for (int i = 0; i < confidences.length; i++) {

					int predictedLabel = (bipartition[i]) ? 1 : 0;

					evidenceValues[i] = 1 - Math.abs(predictedLabel - confidences[i]);
				}

				break;

			default:

				// The default case is 'C'

				for (int i = 0; i < confidences.length; i++) {

					int predictedLabel = (bipartition[i]) ? 1 : 0;

					evidenceValues[i] = 1 - Math.abs(predictedLabel - confidences[i]);
				}

				break;

			}

			// Class dimension
			double classValue = 0;

			switch (classDimension) {

			case 'M':

				if (evidenceDimension == 'C') {
					classValue = evidenceValues[Utils.minIndex(evidenceValues)];
				}

				if (evidenceDimension == 'S') {
					classValue = evidenceValues[Utils.maxIndex(evidenceValues)];
				}

				break;

			case 'A':

				double ave = 0;

				for (int i = 0; i < evidenceValues.length; ++i) {
					ave += evidenceValues[i];
				}

				classValue = ave / evidenceValues.length;

				break;

			case 'R':

				System.err.println("The Round Robin (R) option is not supported yet");
				System.exit(1);

				break;

			default:

				// The default case is 'M'
				if (evidenceDimension == 'C') {
					classValue = evidenceValues[Utils.minIndex(evidenceValues)];
				}

				if (evidenceDimension == 'S') {
					classValue = evidenceValues[Utils.maxIndex(evidenceValues)];
				}

				break;
			}

			// weight dimension
			switch (weightDimension) {

			case 'N':

				// The classValue is not modified
				// Do nothing
				break;

			case 'W':

				System.err.println("The Weighting option (W) is not supported yet");
				System.exit(1);

				break;

			default:

				// The default case is 'N'
				break;
			}

			return classValue;

		} catch (InvalidDataException e) {
			Logger.getLogger(MultiLabel3DimensionalQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		} catch (ModelInitializationException e) {
			Logger.getLogger(MultiLabel3DimensionalQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
			Logger.getLogger(MultiLabel3DimensionalQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return 0;

	}

	/**
	 * Get the evidence dimension
	 *
	 * @return The evidence dimension used
	 */
	public char getEvidenceDimension() {
		return evidenceDimension;
	}

	/**
	 * Set the evidence dimension
	 * 
	 * @param evidenceDimension
	 *            The evidence dimension to use
	 */
	public void setEvidenceDimension(char evidenceDimension) {
		this.evidenceDimension = evidenceDimension;
	}

	/**
	 * Get class dimension
	 * 
	 * @return The class dimension used
	 */
	public char getClassDimension() {
		return classDimension;
	}

	/**
	 * Set class dimension
	 *
	 * @param classDimension
	 *            The class dimension to use
	 */
	public void setClassDimension(char classDimension) {
		this.classDimension = classDimension;
	}

	/**
	 * Get the weight dimension
	 *
	 * @return The weight dimension used
	 */
	public char getWeightDimension() {
		return weightDimension;
	}

	/**
	 * Set the weight dimension
	 * 
	 * @param weightDimension
	 *            The class dimension to use
	 */
	public void setWeightDimension(char weightDimension) {
		this.weightDimension = weightDimension;
	}

	/**
	 *
	 * @param configuration
	 *            The configuration object for
	 *            MultiLabel3DimensionalQueryStrategy
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li>evidence-dimension: The possible values are [C, S]</li>
	 *            <li>class-dimension: The possible values are [M, A, R]</li>
	 *            <li>weight-dimension: The possible values are [N, M]</li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		super.configure(configuration);

		evidenceDimension = configuration.getString("evidence-dimension", "C").toCharArray()[0];
		classDimension = configuration.getString("class-dimension", "M").toCharArray()[0];
		weightDimension = configuration.getString("weight-dimension", "N").toCharArray()[0];

		switch (evidenceDimension) {

		case 'C':

			setMaximal(false);

			break;

		case 'S':

			setMaximal(true);

			break;

		default:
			throw new ConfigurationRuntimeException("For the evidence dimension the options are C and S");
		}

		switch (classDimension) {

		case 'M':

			break;

		case 'A':

			break;

		case 'R':

			break;

		default:
			throw new ConfigurationRuntimeException("For the class dimension the options are M, A and R");
		}

		switch (weightDimension) {

		case 'N':

			break;

		case 'W':

			break;

		default:
			throw new ConfigurationRuntimeException("For the weight dimension the options are N and W");
		}

	}

}
