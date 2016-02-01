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
package net.sf.jclal.activelearning.singlelabel.querystrategy;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.core.IClassifier;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.WekaDataset;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * The objective of this active learning strategy is to reduce the expected
 * total number of incorrect predictions.
 *
 * Burr Settles. Active Learning Literature Survey. Computer Sciences Technical
 * Report 1648, University of Wisconsin–Madison. 2009.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ExpectedCeroOneLossQueryStrategy extends ErrorReductionQueryStrategy {

	private static final long serialVersionUID = -5044425287170814499L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {
		double result = 0;

		try {

			Instances unlabelled = getUnlabelledData().getDataset();

			int k = unlabelled.indexOf(instance);

			double[] probabilities = distributionForInstance(instance);

			for (int i = 0; i < probabilities.length; i++) {

				double currProb = probabilities[i];

				double currLoss = expectedCeroOneLoss(k, i);

				double currentResult = currProb * currLoss;

				result += currentResult;
			}

		} catch (Exception ex) {
			Logger.getLogger(ExpectedCeroOneLossQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	/**
	 * Returns the expected zero one loss, the lower the value the greater the
	 * information
	 *
	 * @param instanceToAdd
	 *            The index of the instance to add
	 * @param classValue
	 *            The class value
	 * @return The expected zero one loss
	 */
	public double expectedCeroOneLoss(int instanceToAdd, int classValue) {

		double sum = 0;

		try {
			// Make a copy of the labeled and unlabeled sets
			IDataset labeledCopy = new WekaDataset(getLabelledData());

			IDataset unlabeledCopy = new WekaDataset(getUnlabelledData());

			// the class settles down according to the classifier
			Instance copy = unlabeledCopy.instance(instanceToAdd);

			// The current instance is removed from unlabeled set
			unlabeledCopy.remove(instanceToAdd);

			copy.setClassValue(classValue);

			// The current instance is added to labeled set
			labeledCopy.add(copy);

			// it trains the classifier with the new labeled set
			IClassifier clasificadorTemp = getClassifier().makeCopy();
			clasificadorTemp.buildClassifier(labeledCopy);

			Iterator<Instance> instanceIterator = unlabeledCopy.getDataset().iterator();

			Instance current;
			while (instanceIterator.hasNext()) {

				current = instanceIterator.next();

				double[] probabilities;
				probabilities = clasificadorTemp.distributionForInstance(current);

				double currConf = probabilities[Utils.maxIndex(probabilities)];

				currConf = 1.0 - currConf;

				sum += currConf;
			}

			labeledCopy.getDataset().clear();
			labeledCopy = null;
			unlabeledCopy.getDataset().clear();
			unlabeledCopy = null;
			instanceIterator = null;
			clasificadorTemp = null;
			copy = null;
			System.gc();

		} catch (Exception e) {
			Logger.getLogger(ExpectedCeroOneLossQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return sum;
	}
}
