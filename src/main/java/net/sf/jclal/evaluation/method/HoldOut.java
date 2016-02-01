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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.activelearning.algorithm.AbstractALAlgorithm;
import net.sf.jclal.core.IAlgorithm;
import net.sf.jclal.dataset.AbstractDataset;
import net.sf.jclal.dataset.MulanDataset;
import net.sf.jclal.dataset.WekaDataset;
import net.sf.jclal.sampling.AbstractSampling;
import net.sf.jclal.util.dataset.DatasetUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Hold Out evaluation method.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class HoldOut extends AbstractEvaluationMethod {

	private static final long serialVersionUID = -6193747210498415157L;
	/**
	 * Percentage of instances to run the training, the rest is test set
	 */
	private double percentageToSplit = 66;

	/**
	 * Constructor
	 * 
	 * @param algorithm
	 *            The algorithm.
	 * @param dataset
	 *            The dataset.
	 * @param percentageToTrain
	 *            The percentage of instances to train.
	 */
	public HoldOut(AbstractALAlgorithm algorithm, AbstractDataset dataset, double percentageToTrain) {

		super(algorithm, dataset);

		this.percentageToSplit = percentageToTrain;
	}

	/**
	 * Empty(default) constructor.
	 */
	public HoldOut() {
	}

	/**
	 * Get the percentage selected
	 *
	 * @return The percentage selected to train.
	 */
	public double getPercentageToSplit() {
		return percentageToSplit;
	}

	/**
	 * Set the percentage of instances to select
	 * 
	 * @param percent
	 *            The percentage selected to train.
	 */
	public void setPercentageToSplit(double percent) {
		this.percentageToSplit = percent;
	}

	/**
	 * Executes the process of evaluation of the experiment
	 */
	@Override
	public void evaluate() {

		try {
			// Load the initial data
			loadData();

			// One general dataset was specified, then it must be divided into
			// train and test taking
			// into account the percentage defined
			if (getDataset() != null) {

				DatasetUtils.randomize(((AbstractSampling) getSamplingStrategy()).getRandgen(), getDataset());

				int numInstances = (int) (getDataset().getNumInstances() * percentageToSplit / 100);

				if (!isMultiLabel()) {

					setTrainDataset(new WekaDataset(getDataset(), 0, numInstances));
					setTestDataset(
							new WekaDataset(getDataset(), numInstances, getDataset().getNumInstances() - numInstances));

				} else {

					setTrainDataset(new MulanDataset(getDataset(), 0, numInstances));
					setTestDataset(new MulanDataset(getDataset(), numInstances,
							getDataset().getNumInstances() - numInstances));
				}

				// Resample the instances to construct the labeled and unlabeled
				// set on the training set
				getSamplingStrategy().sampling(getTrainDataset());

				setLabeledDataset(((AbstractSampling) getSamplingStrategy()).getLabeledData());
				setUnlabeledDataset(((AbstractSampling) getSamplingStrategy()).getUnlabeledData());

			} // confirm if a training and test file were specified
			else {

				// A training dataset was specified
				if (getTrainDataset() != null) {

					// Resample the instances to construct the labeled and
					// unlabeled set
					getSamplingStrategy().sampling(getTrainDataset());

					setLabeledDataset(((AbstractSampling) getSamplingStrategy()).getLabeledData());
					setUnlabeledDataset(((AbstractSampling) getSamplingStrategy()).getUnlabeledData());
				}

				// Else, An unlabeled and labeled file were specified, then a
				// resample method is not necessary
			}

			IAlgorithm algorithmCopy = getAlgorithm().makeCopy();

			algorithmCopy.setLabeledDataSet(getLabeledDataset());
			algorithmCopy.setUnlabeledDataSet(getUnlabeledDataset());
			algorithmCopy.setTestDataSet(getTestDataset());

			// Executes the algorithm
			algorithmCopy.execute();

			setFinalEvaluations(algorithmCopy.getScenario().getQueryStrategy().getEvaluations());

		} catch (Exception e) {
			Logger.getLogger(HoldOut.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * @param configuration
	 *            The configuration of Hold Out.
	 *
	 *            The XML labels supported are:
	 *
	 *            <ul>
	 *            <li><b>percentage-split= double</b></li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		super.configure(configuration);

		// the percent of instances used to train
		double percentTrain = configuration.getDouble("percentage-split", percentageToSplit);

		String perc = "\n<percentage-split>" + percentTrain + "</percentage-split>";

		if (percentTrain <= 0) {
			throw new ConfigurationRuntimeException(perc + ". percentage-split > 0");
		}
		if (percentTrain >= 100) {
			throw new ConfigurationRuntimeException(perc + ". percentage-split < 100");
		}

		setPercentageToSplit(percentTrain);
	}
}