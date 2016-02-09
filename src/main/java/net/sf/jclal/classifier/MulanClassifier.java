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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mulan.classifier.MultiLabelLearnerBase;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.transformation.TransformationBasedMultiLabelLearner;
import mulan.data.InvalidDataFormatException;
import mulan.evaluation.Evaluator;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.MulanDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.evaluation.measure.MulanEvaluation;
import net.sf.jclal.evaluation.measure.MultiLabelEvaluation;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 * Represent a classifier of MULAN framework
 * 
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class MulanClassifier extends AbstractClassifier {

	private static final long serialVersionUID = -1013631694911281355L;

	private MultiLabelLearnerBase classifier;

	/**
	 * Get the classifier
	 * 
	 * @return The MultiLabelLearnerClassifier
	 */
	public MultiLabelLearnerBase getInternalClassifier() {
		return classifier;
	}

	/**
	 * Set the classifier
	 *
	 * @param classifier
	 *            The multilabel classifier to use
	 */
	public void setClassifier(MultiLabelLearnerBase classifier) {
		this.classifier = classifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildClassifier(IDataset instances) throws Exception {

		classifier.build(((MulanDataset) instances).getMultiLabelDataset());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] distributionForInstance(Instance instance) {

		try {
			return classifier.makePrediction(instance).getConfidences();

		} catch (Exception e) {
			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return the bipartition of the label space
	 *
	 * @param instance
	 *            The instance to test
	 * @return the bipartition of the label space
	 */
	public boolean[] getBipartition(Instance instance) {

		try {
			return classifier.makePrediction(instance).getBipartition();
		} catch (Exception e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return a MultiLabelOutput object
	 *
	 * @param instance
	 *            The instance to test
	 * @return a MultiLabelOutput object
	 */
	public MultiLabelOutput makePrediction(Instance instance) {

		try {
			return classifier.makePrediction(instance);
		} catch (Exception e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return a ranking of labels according their relevance
	 *
	 * @param instance
	 *            The instance to test
	 * @return the ranking of the labels.
	 */
	public int[] getRanking(Instance instance) {

		try {
			return classifier.makePrediction(instance).getRanking();

		} catch (Exception e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractEvaluation testModel(IDataset instances) {

		try {
			// test phase with the actual model

			Evaluator evaluator = new Evaluator();

			MultiLabelEvaluation mleval = new MultiLabelEvaluation();

			MulanEvaluation eval = new MulanEvaluation(
					evaluator.evaluate(classifier, ((MulanDataset) instances).getMultiLabelDataset()),
					((MulanDataset) instances).getMultiLabelDataset());

			mleval.setEvaluation(eval);

			return mleval;

		} catch (IllegalArgumentException e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);

		} catch (InvalidDataFormatException e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {

			Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, e);
		}

		return null;

	}

	@Override
	public String toString() {

		String st = classifier.getClass().getSimpleName();

		if (classifier instanceof TransformationBasedMultiLabelLearner) {
			st += "("
					+ ((TransformationBasedMultiLabelLearner) classifier).getBaseClassifier().getClass().getSimpleName()
					+ ")";
		}

		return st;
	}

	/**
	 *
	 * @param configuration
	 *            The configuration of MULAN classifier.
	 *
	 *            The XML labels supported are:
	 *
	 *            <ul>
	 *            <li><b>classifier type= class</b>
	 *            <p>
	 *            Package:
	 *            </p>
	 *            mulan.classifier
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            If the defined classifier is instance of
	 *            mulan.classifier.transformation, then a base-classifier must
	 *            be configured
	 *            <ul>
	 *            <li><b>base-classifier type= class</b> All weka classifier are
	 *            supported</li>
	 *            </ul>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		String classifierError = "classifier type= ";
		try {
			// classifier classname
			String classifierClassname = configuration.getString("classifier[@type]");
			classifierError += classifierClassname;
			// classifier class
			Class<? extends MultiLabelLearnerBase> classifierClass = (Class<? extends MultiLabelLearnerBase>) Class
					.forName(classifierClassname);

			MultiLabelLearnerBase multiLabelClassifier = null;

			// If the multi label learner is a problem transformation method
			// then a base classifier must be configured
			if (TransformationBasedMultiLabelLearner.class.isAssignableFrom(classifierClass)) {

				String baseError = "base-classifier type= ";
				try {

					Configuration conf = configuration.subset("classifier");

					// classifier classname
					String baseClassifier = conf.getString("base-classifier[@type]");
					baseError += baseClassifier;
					// classifier class
					Class<? extends Classifier> baseClassifierClass = (Class<? extends Classifier>) Class
							.forName(baseClassifier);

					// classifier instance
					Classifier baseClassifierInstance = baseClassifierClass.newInstance();

					// Check if the base classifier is a MOA Classifier
					if (MOAWrapper.class.isAssignableFrom(baseClassifierClass)) {

						if (baseClassifierInstance instanceof IConfigure) {
							((IConfigure) baseClassifierInstance).configure(conf.subset("base-classifier"));
						}

						multiLabelClassifier = classifierClass.getConstructor(new Class<?>[] { MOAWrapper.class })
								.newInstance(baseClassifierInstance);

					} else {
						multiLabelClassifier = classifierClass.getConstructor(new Class<?>[] { Classifier.class })
								.newInstance(baseClassifierInstance);
					}

				} catch (IllegalArgumentException e) {
					throw new ConfigurationRuntimeException("\nIllegal base classifier: " + baseError, e);
				} catch (InvocationTargetException e) {
					throw new ConfigurationRuntimeException("\nIllegal base classifier: " + baseError, e);
				} catch (SecurityException e) {
					throw new ConfigurationRuntimeException("\nIllegal base classifier: " + baseError, e);
				} catch (NoSuchMethodException ex) {
					Logger.getLogger(MulanClassifier.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else {
				multiLabelClassifier = classifierClass.newInstance();
			}
			// Add this classifier
			setClassifier(multiLabelClassifier);

		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		}
	}

}