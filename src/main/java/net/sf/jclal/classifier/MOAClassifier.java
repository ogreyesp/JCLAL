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
package net.sf.jclal.classifier;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.classifiers.Classifier;
import mulan.data.InvalidDataFormatException;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.evaluation.measure.SingleLabelEvaluation;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class MOAClassifier extends AbstractClassifier {

	private static final long serialVersionUID = -1013631694911281355L;

	private MOAWrapper classifier;

	/**
	 *
	 * @return The MOA wrapper
	 */
	public MOAWrapper getInternalClassifier() {
		return classifier;
	}

	/**
	 *
	 * @param classifier
	 *            The MOA classifier to use
	 */
	public void setClassifier(MOAWrapper classifier) {
		this.classifier = classifier;
	}

	@Override
	public void buildClassifier(IDataset instances) throws Exception {
		classifier.buildClassifier(instances.getDataset());
		
	}
	
	public void updateClassifier(IDataset instances) throws Exception {
		
		for (int i = 0; i < instances.getNumInstances(); i++)
			classifier.updateClassifier(instances.instance(i));
		
	}

	@Override
	public double[] distributionForInstance(Instance instance) {

		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			Logger.getLogger(MOAClassifier.class.getName()).log(Level.SEVERE,
					null, e);
		}
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public AbstractEvaluation testModel(IDataset instances) {

		try {

			// test the current classifier with the test set
			Evaluation evaluator = new Evaluation(new Instances(
					instances.getDataset(), 0));

			evaluator.evaluateModel(classifier, instances.getDataset());

			SingleLabelEvaluation sleval = new SingleLabelEvaluation();

			sleval.setEvaluation(evaluator);

			return sleval;

		} catch (IllegalArgumentException e) {

			Logger.getLogger(MOAClassifier.class.getName()).log(Level.SEVERE,
					null, e);

		} catch (InvalidDataFormatException e) {

			Logger.getLogger(MOAClassifier.class.getName()).log(Level.SEVERE,
					null, e);
		} catch (Exception e) {

			Logger.getLogger(MOAClassifier.class.getName()).log(Level.SEVERE,
					null, e);
		}

		return null;

	}

	@Override
	public String toString() {

		String st = classifier.getClass().getSimpleName();

		return st;
	}

	/**
	 * Method to read the object's configuration
	 * @param configuration
	 *            The configuration of MOA classifier.
	 *
	 *            The XML labels supported are:
	 *
	 *            <ul>
	 *            <li>
	 *            <b>classifier type= class</b>
	 *            <p>
	 *            Package:
	 *            </p>
	 *            moa.classifiers
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
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
			setClassifier(new MOAWrapper(classifierClass.newInstance()));

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
