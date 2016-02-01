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
package net.sf.jclal.core;

import java.util.List;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import weka.core.Instance;

/**
 * Interface for Active Learning Query Strategies.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public interface IQueryStrategy extends JCLAL {

	/**
	 * Set if the query strategy is maximal or not
	 *
	 * @param max
	 *            if the query strategy is maximal or not
	 */
	public void setMaximal(boolean max);

	/**
	 * Return if the query strategy is maximal or not
	 *
	 * @return maximal or not otherwise
	 */
	public boolean isMaximal();

	/**
	 * Test the instance according to the query strategy. Return the utility of
	 * the instance according to the query strategy
	 *
	 * @param instance
	 *            the instance to compute the utility.
	 * @return the value calculated for the query strategy for the instance
	 */
	public double utilityInstance(Instance instance);

	/**
	 * Return the probabilities of belonging to the classes
	 *
	 * @param instance
	 *            Instance for classify.
	 * @return The distribution probabilities
	 */
	public double[] distributionForInstance(Instance instance);

	/**
	 * Get the unlabelled data
	 *
	 * @return Unlabelled data.
	 */
	public IDataset getUnlabelledData();

	/**
	 * Set the unlabelled data
	 *
	 * @param unlabelledData
	 *            unlabeled instances
	 */
	public void setUnlabelledData(IDataset unlabelledData);

	/**
	 * Get the unlabeled data
	 * 
	 * @return pointer to Labeled data.
	 */
	public IDataset getLabelledData();

	/**
	 * Set the labeled data
	 *
	 * @param labelledData
	 *            Labeled data.
	 */
	public void setLabelledData(IDataset labelledData);

	/**
	 * Training the current classifier on labeled set
	 */
	public void training();

	/**
	 * Test the current model on test set
	 *
	 */
	public void testModel();

	/**
	 * Test each unlabeled instance, it returns an array with the utility of
	 * each instance.
	 *
	 * @return The utility of each unlabeled instance.
	 */
	public double[] testUnlabeledData();

	/**
	 * Set the test set.
	 *
	 * @param testData
	 *            The test dataset to test the performance of the algorithm.
	 */
	public void setTestData(IDataset testData);

	/**
	 * @return Get the test set.
	 */
	public IDataset getTestData();

	/**
	 * Set the classifier to use on query strategy.
	 *
	 * @param classifier
	 *            The base classifier.
	 */
	public void setClassifier(IClassifier classifier);

	/**
	 * Return the base classifier.
	 *
	 * @return The classifier.
	 */
	public IClassifier getClassifier();

	/**
	 * Return the list of evaluations.
	 *
	 * @return The evaluations of the experiment.
	 */
	public List<AbstractEvaluation> getEvaluations();

	/**
	 * Set the evaluations.
	 *
	 * @param evaluations
	 *            The evaluations of the experiment.
	 */
	public void setEvaluations(List<AbstractEvaluation> evaluations);

	/**
	 * String representation
	 * 
	 * @return The representation of the query strategy like a string.
	 */
	@Override
	public String toString();

	/**
	 * Update the labeled data. The selected instances are removed from
	 * unlabeled set and added to labeled set
	 */
	public void updateLabeledData();

	/**
	 * Used after completing the algorithm for the execution of any necessary
	 * actions, e.g., free memory, delete temporary files, etc.
	 */
	public void algorithmFinished();
}
