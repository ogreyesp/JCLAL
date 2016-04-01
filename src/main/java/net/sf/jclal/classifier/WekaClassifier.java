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
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.evaluation.measure.SingleLabelEvaluation;
import net.sf.jclal.util.thread.ThreadControl;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Represents a classifier of Weka framework.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class WekaClassifier extends AbstractClassifier {

	private static final long serialVersionUID = 1765269739169476036L;

	/**
	 * Classifier of Weka.
	 */
	private Classifier classifier;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildClassifier(IDataset instances) throws Exception {

		classifier.buildClassifier(instances.getDataset());
	}

	/**
	 * Classify the instance
	 * 
	 * @param instance
	 *            The instance to classify.
	 * @return The predicted label for the classifier.
	 * @throws Exception
	 *             The exception that will be launched.
	 */
	public double classifyInstance(Instance instance) throws Exception {
		return classifier.classifyInstance(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] distributionForInstance(Instance instance) {

		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			Logger.getLogger(WekaClassifier.class.getName()).log(Level.SEVERE, null, e);
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
			Evaluation evaluator = new Evaluation(new Instances(instances.getDataset(), 0));

			final Instances testData = instances.getDataset();

			final double[][] evaluations = new double[testData.numInstances()][];

			ThreadControl thread = ThreadControl.defaultThreadControl(isParallel());

			for (int i = 0; i < evaluations.length; i++) {

				final int j = i;

				thread.execute(new Runnable() {

					@Override
					public void run() {
						evaluations[j] = distributionForInstance(testData.instance(j));
					}
				});

			}

			thread.end();
			thread = null;

			for (int i = 0; i < evaluations.length; i++) {
				evaluator.evaluationForSingleInstance(evaluations[i], testData.instance(i), true);
			}

			SingleLabelEvaluation sleval = new SingleLabelEvaluation();

			sleval.setEvaluation(evaluator);

			return sleval;

		} catch (Exception e) {
			Logger.getLogger(WekaClassifier.class.getName()).log(Level.SEVERE, null, e);
		}

		return null;
	}

	@Override
	public String toString() {
        StringBuilder extra = new StringBuilder();
        ThreadControl c = ThreadControl.defaultThreadControl(isParallel());
        extra.append("-").append(c.getDefaultCores()).append("cores");

        return classifier.getClass().getSimpleName().concat(extra.toString());
    }

	/**
	 * Set the classifier to use.
	 *
	 * @param classifier
	 *            The weka classifier.
	 */
	public void setClassifier(Classifier classifier) {
		try {
			this.classifier = weka.classifiers.AbstractClassifier.makeCopy(classifier);
		} catch (Exception e) {
			Logger.getLogger(WekaClassifier.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Get the classifier used.
	 *
	 * @return The classifier used
	 */
	public Classifier getClassifier() {
		return classifier;
	}

	/**
	 *
	 * @param configuration
	 *            The configuration object for WekaClassifier. The XML labels
	 *            supported are:
	 *            <ul>
	 *            <li><b>classifier type= class</b>
	 *            <p>
	 *            Package: weka.classifiers
	 *            </p>
	 *            <p>
	 *            Class: All
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		super.configure(configuration);

		String classifierError = "classifier type= ";
		try {

			// classifier classname
			String classifierClassname = configuration.getString("classifier[@type]");
			classifierError += classifierClassname;
			// classifier class
			Class<? extends Classifier> classifierClass = (Class<? extends Classifier>) Class
					.forName(classifierClassname);
			// classifier instance
			Classifier classifierTemp = classifierClass.newInstance();
			// Configure classifier (if necessary)
			if (classifierTemp instanceof IConfigure) {
				((IConfigure) classifierTemp).configure(configuration.subset("classifier"));
			}
			// Add this classifier
			setClassifier(classifierTemp);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("\nIllegal classifier classname: " + classifierError, e);
		}
	}
}
