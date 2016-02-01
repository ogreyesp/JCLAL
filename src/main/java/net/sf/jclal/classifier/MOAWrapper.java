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

/**
 * Class that wrappers a class of MOA framework. 
 * 
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */

package net.sf.jclal.classifier;

import weka.classifiers.UpdateableClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.Capabilities.Capability;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import moa.classifiers.Classifier;
import net.sf.jclal.core.IConfigure;

public class MOAWrapper extends weka.classifiers.AbstractClassifier implements UpdateableClassifier, IConfigure {

	/** for serialization. */
	private static final long serialVersionUID = 2605797948130310166L;

	/** the actual MOA classifier to use for learning. */
	protected Classifier classifier;

	public MOAWrapper(Classifier classifier) {
		super();
		this.classifier = classifier;
	}

	public MOAWrapper() {
		super();
	}

	/**
	 * Set the classifier
	 * 
	 * @param classifier
	 *            The MOA classifier
	 */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * Returns the MOA classifier in use.
	 *
	 * @return the classifier in use
	 */
	public Classifier getClassifier() {
		return classifier;
	}

	/**
	 * Returns the Capabilities of this classifier. Maximally permissive
	 * capabilities are allowed by default. MOA doesn't specify what
	 *
	 * @return the capabilities of this object
	 * @see Capabilities
	 */
	public Capabilities getCapabilities() {
		Capabilities result = new Capabilities(this);

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void buildClassifier(Instances data) throws Exception {

		getCapabilities().testWithFail(data);

		data = new Instances(data);
		data.deleteWithMissingClass();

		classifier.resetLearning();

		for (int i = 0; i < data.numInstances(); i++)
			updateClassifier(data.instance(i));

	}

	/**
	 * {@inheritDoc}
	 */
	public void updateClassifier(Instance instance) throws Exception {
		classifier.trainOnInstance(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] result;

		result = classifier.getVotesForInstance(instance);
		// ensure that the array has as many elements as there are
		// class values!
		if (result.length < instance.numClasses()) {
			double[] newResult = new double[instance.numClasses()];
			System.arraycopy(result, 0, newResult, 0, result.length);
			result = newResult;
		}

		try {
			Utils.normalize(result);
		} catch (Exception e) {
			result = new double[instance.numClasses()];
		}

		return result;
	}

	/**
	 * Returns the revision string.
	 *
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision$");

	}

	public String toString() {
		StringBuilder result;

		result = new StringBuilder();
		classifier.getDescription(result, 0);

		return result.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(Configuration configuration) {
		
		String classifierError = "classifier type= ";
		try {
			
			// classifier classname
			String classifierClassname = configuration
					.getString("classifier[@type]");
			
			classifierError += classifierClassname;
			
			// classifier class
			Class<? extends Classifier> classifierClass = (Class<? extends Classifier>) Class
					.forName(classifierClassname);

			// Add this classifier
			setClassifier(classifierClass.newInstance());

		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException(
					"\nIllegal classifier classname: " + classifierError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException(
					"\nIllegal classifier classname: " + classifierError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException(
					"\nIllegal classifier classname: " + classifierError, e);
		}
		
	}

}
