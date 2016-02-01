package net.sf.jclal.classifier;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;

/**
 * Updateable Binary Relevance. It must be run with an UpdateableClassifier base
 * classifier.
 * 
 * @author Oscar Gabriel Reyes Pupo
 */

public class BinaryRelevanceUpdateable extends BinaryRelevance implements UpdateableClassifier {

	public BinaryRelevanceUpdateable(Classifier classifier) {
		super(classifier);
	}

	public BinaryRelevanceUpdateable(MOAWrapper classifier) {
		super(classifier);
	}

	private static final long serialVersionUID = 6705611077773512052L;

	@Override
	public String globalInfo() {
		return "Updateable BR\nMust be run with an Updateable base classifier.";
	}

	@Override
	public void updateClassifier(Instance instance) throws Exception {

		for (int counter = 0; counter < numLabels; counter++) {

			Instance transformedInstance = getBrt().transformInstance(instance, counter);

			((UpdateableClassifier) getEnsemble()[counter]).updateClassifier(transformedInstance);
		}
	}
}