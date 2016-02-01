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

/**
 * This class minimizes the expected log-loss, which is equivalent to reducing
 * the expected entropy over unlabeled set.
 *
 * Burr Settles. Active Learning Literature Survey. Computer Sciences Technical
 * Report 1648, University of Wisconsin–Madison. 2009.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ExpectedLogLossQueryStrategy extends ErrorReductionQueryStrategy {

	private static final long serialVersionUID = -3730291633567592831L;

	/**
	 * Empty(default) contructor.
	 */
	public ExpectedLogLossQueryStrategy() {
		super();
	}

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

				double currConf = probabilities[i];

				double currLoss = expectedLogLoss(k, i);

				double currentResult = currConf * currLoss;

				result += currentResult;
			}

		} catch (Exception ex) {
			Logger.getLogger(ExpectedLogLossQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	/**
	 * Returns the expected log loss, the lower the value the greater the
	 * information
	 *
	 * @param instanceToAdd
	 *            The instance to add
	 * @param classValue
	 *            The class value
	 * @return the expected log loss
	 */
	public double expectedLogLoss(int instanceToAdd, int classValue) {

		double sum = 0;

		try {
			// Make a copy of the labeled and unlabeled sets
			IDataset labeledCopy = new WekaDataset(getLabelledData());

			IDataset unlabeledCopy = new WekaDataset(getUnlabelledData());

			// the class settles down according to the classifier
			Instance copy = unlabeledCopy.instance(instanceToAdd);

			unlabeledCopy.remove(instanceToAdd);

			copy.setClassValue(classValue);

			labeledCopy.add(copy);

			// To train the classifier with the new labeled set
			IClassifier clasificadorTemp = getClassifier().makeCopy();
			clasificadorTemp.buildClassifier(labeledCopy);

			Iterator<Instance> instanceIterator = unlabeledCopy.getDataset().iterator();

			Instance current;
			while (instanceIterator.hasNext()) {

				current = instanceIterator.next();

				double[] probabilities = clasificadorTemp.distributionForInstance(current);

				for (int i = 0; i < probabilities.length; i++) {

					if (probabilities[i] != 0) {

						double tempValue = probabilities[i] * logbase2(probabilities[i]);

						sum += tempValue;
					}
				}
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
			Logger.getLogger(ExpectedLogLossQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}

		return -sum;
	}

	/**
	 * Compute the logarithmic based 2.
	 * 
	 * @param d
	 *            the number
	 * @return the logarithmic based 2.
	 */
	public double logbase2(double d) {
		return Math.log(d) / Math.log(2);
	}

}
