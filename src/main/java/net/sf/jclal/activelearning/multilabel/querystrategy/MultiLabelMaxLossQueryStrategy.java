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
import mulan.transformations.BinaryRelevanceTransformation;
import net.sf.jclal.classifier.BinaryRelevance;
import net.sf.jclal.classifier.MulanClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOsync;
import weka.core.Instance;
import weka.core.Utils;

/**
 * Implementation of Max Loss active strategy.
 *
 * See for more information Li, X., Wcing, L., and Sung, E. (2004). Multi-label
 * svm active learning for image classification, 2207-2210.
 *
 * @author Oscar Gabriel Reyes Pupo
 *
 */
public class MultiLabelMaxLossQueryStrategy extends AbstractMultiLabelQueryStrategy {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty (default constructor)
	 */
	public MultiLabelMaxLossQueryStrategy() {

		super();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		try {

			if (!(((MulanClassifier) getClassifier()).getInternalClassifier() instanceof BinaryRelevance)) {
				System.err.println(
						"The Max Loss query strategy must be configured with the Paralle Binary Relevance algorithm");
			}

			BinaryRelevance learner = (BinaryRelevance) ((MulanClassifier) getClassifier()).getInternalClassifier();

			// One SVM classifier for each label
			Classifier[] smos = learner.getEnsemble();

			if (!(smos[0] instanceof SMOsync)) {
				System.err.println(
						"The base classifiers of the Binary Relevance algorithm on the Max Loss query strategy must be SVM");
			}

			BinaryRelevanceTransformation brt = learner.getBrt();

			int sum = 0;

			int maxConfidenceClass;

			maxConfidenceClass = Utils.maxIndex(learner.makePrediction(instance).getConfidences());

			for (int l = 0; l < getNumLabels(); l++) {

				Instance transformedInstance = brt.transformInstance(instance, l);

				double result = Math.abs(((SMOsync) smos[l]).SVMOutput(transformedInstance));

				int mjl = -1;

				if (maxConfidenceClass == l) {
					mjl = 1;
				}

				sum += Math.max(1 - mjl * result, 0);

			}

			return sum;

		} catch (InvalidDataException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(MultiLabelMaxLossQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		} catch (ModelInitializationException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(MultiLabelMaxLossQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.getLogger(MultiLabelMaxLossQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return 0;
	}
}
