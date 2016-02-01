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
package net.sf.jclal.sampling;

import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.core.IRandGen;
import net.sf.jclal.core.ISampling;
import net.sf.jclal.core.ISystem;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Abstract class for sampling, the sampling methods are used to select the
 * initial labeled set from the training set
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractSampling implements ISampling, IConfigure {

	private static final long serialVersionUID = -1882444747969343513L;

	/**
	 * Pointer to labeled dataset
	 */
	private IDataset labeledData;

	/**
	 * Pointer to unlabeled dataset
	 */
	private IDataset unlabeledData;

	/**
	 * It represents the percentage of instances used to construct the initial
	 * labeled set. By default the 10 percent is selected
	 */
	private double percentageInstancesToLabelled;

	/**
	 * Random generator used
	 */
	private IRandGen randgen;

	/**
	 * Empty(default) constructor.
	 */
	public AbstractSampling() {

		super();
	}

	/**
	 * Get labeled data
	 *
	 * @return The labeled data.
	 */
	public IDataset getLabeledData() {
		return labeledData;
	}

	/**
	 * Set labeled data
	 * 
	 * @param initialLabeledData
	 *            The labeled data.
	 */
	public void setLabeledData(IDataset initialLabeledData) {
		this.labeledData = initialLabeledData;
	}

	/**
	 * Get unlabeled data
	 * 
	 * @return The unlabeled data.
	 */
	public IDataset getUnlabeledData() {
		return unlabeledData;
	}

	/**
	 * Set the unlabeled data
	 * 
	 * @param unlabeledData
	 *            The unlabeled data.
	 */
	public void setUnlabeledData(IDataset unlabeledData) {
		this.unlabeledData = unlabeledData;
	}

	/**
	 * @param configuration
	 *            The configuration object for Abstract sampling.
	 *            <p>
	 *            <b>percentage-to-select= double</b>
	 *            </p>
	 */
	@Override
	public void configure(Configuration configuration) {

		double percentageInstancesToLabelledTemp = configuration.getDouble("percentage-to-select", 10);
		String perc = "\n<percentage-to-select>" + percentageInstancesToLabelledTemp + "</percentage-to-select>";
		if (percentageInstancesToLabelledTemp <= 0) {
			throw new ConfigurationRuntimeException(perc + ". percentage-to-select > 0");
		}
		if (percentageInstancesToLabelledTemp > 100) {
			throw new ConfigurationRuntimeException(perc + ". percentage-to-select <= 100");
		}
		setPercentageInstancesToLabelled(percentageInstancesToLabelledTemp);

	}

	/**
	 * Get the percentage of instances to form the labelled set
	 * 
	 * @return The percentage of instances to labeled.
	 */
	public double getPercentageInstancesToLabelled() {
		return percentageInstancesToLabelled;
	}

	/**
	 * Set the percentage of instances to form the labelled set
	 *
	 * @param percentageInstancesToLabelled
	 *            The percentage of instances to labeled.
	 */
	public void setPercentageInstancesToLabelled(double percentageInstancesToLabelled) {
		this.percentageInstancesToLabelled = percentageInstancesToLabelled;
	}

	/**
	 * Establishes parameters for context in the sampling
	 *
	 * @param context
	 *            The context to use
	 */
	@Override
	public void contextualize(ISystem context) {

		// Attach a random generator to this object
		this.setRandgen(context.createRandGen());

	}

	/**
	 * Return the random number generator
	 * 
	 * @return The random generator.
	 */
	public IRandGen getRandgen() {
		return randgen;
	}

	/**
	 * Set the random number generator
	 * 
	 * @param randgen
	 *            The random generator.
	 */
	public void setRandgen(IRandGen randgen) {
		this.randgen = randgen;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
