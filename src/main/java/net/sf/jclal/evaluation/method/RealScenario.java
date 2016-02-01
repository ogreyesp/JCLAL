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
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Class that represents a real scenario where a tagger labels the unlabeled
 * instances in each iteration.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class RealScenario extends AbstractEvaluationMethod {

	private static final long serialVersionUID = -6154654610498415157L;

	/**
	 * Constructor
	 *
	 * @param algorithm
	 *            The algorithm.
	 * @param dataset
	 *            The dataset.
	 * @param percentageToTrain
	 *            The percentage of instances for training the initial
	 *            classifier.
	 */
	public RealScenario(AbstractALAlgorithm algorithm, AbstractDataset dataset, double percentageToTrain) {

		super(algorithm, dataset);

	}

	/**
	 * Empty(default) constructor.
	 */
	public RealScenario() {
	}

	/**
	 * Executes the process of evaluation
	 */
	@Override
	public void evaluate() {

		try {
			// Load the initial data
			loadData();

			IAlgorithm algorithmCopy = getAlgorithm().makeCopy();

			algorithmCopy.setLabeledDataSet(getLabeledDataset());
			algorithmCopy.setUnlabeledDataSet(getUnlabeledDataset());
			algorithmCopy.setTestDataSet(getTestDataset());

			// Executes the algorithm
			algorithmCopy.execute();

			setFinalEvaluations(algorithmCopy.getScenario().getQueryStrategy().getEvaluations());

		} catch (Exception e) {
			Logger.getLogger(RealScenario.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * @param configuration
	 *            The configuration of Hold Out, support datasets and algorithm
	 *            configuration.
	 */
	@Override
	public void configure(Configuration configuration) {

		setDataConfiguration(configuration);

		super.setAlgorithmConfiguration(configuration);

	}

	@Override
	protected void setDataConfiguration(Configuration configuration) {
		// Set multiLabel flag
		boolean multi = configuration.getBoolean("multi-label", false);
		setMultiLabel(multi);

		// Set multiInstance flag
		boolean multiInstance = configuration.getBoolean("multi-instance", false);
		setMultiInstance(multiInstance);

		// Set the xml file, it is used in the case of a multi-label
		// dataset
		String xml = configuration.getString("file-xml", "");
		setXmlPath(xml);

		// the multi-label elements are verified
		if (multi && xml.isEmpty()) {
			throw new ConfigurationRuntimeException("\nThe multi-label flag is " + "enabled and the xml path is empty. "
					+ "<multi-label>true</multi-label>" + " <file-xml></file-xml>");
		}

		// Set file labeled
		String fileLabeled = configuration.getString("file-labeled", "");
		setFileLabeledDataset(fileLabeled);

		// Set file unlabeled
		String fileUnlabeled = configuration.getString("file-unlabeled", "");
		setFileUnlabeledDataset(fileUnlabeled);

		if (fileLabeled.isEmpty() || fileUnlabeled.isEmpty()) {
			throw new ConfigurationRuntimeException("\n <file-labeled> and <file-unlabeled> tags must be defined.");
		}

		// Set file test
		String fileTest = configuration.getString("file-test", "");

		if (fileTest.isEmpty()) {
			/*
			 * Logger.getLogger(RealScenario.class.getName()).log(Level.INFO,
			 * "The param <file-test> is empty, the active learning algorithm require this property "
			 * +
			 * "for evaluating the constructed model, but in real scenario is not really necessary. In this case, "
			 * + "we assign the <file-unlabeled> as <file-test>.");
			 */
			fileTest = fileUnlabeled;
		}

		setFileTestDataset(fileTest);

		// Set class attribute
		int classAttributeT = configuration.getInt("class-attribute", -1);

		setClassAttribute(classAttributeT);
	}
}
