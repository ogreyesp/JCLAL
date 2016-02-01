package net.sf.jclal.classifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import mulan.transformations.BinaryRelevanceTransformation;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Parallel implementation of the Binary Relevance approach.
 *
 * @author Oscar Gabriel Reyes Pupo
 */
@SuppressWarnings("serial")
public class ParallelBinaryRelevance extends BinaryRelevance {

	/**
	 * The ensemble of binary relevance models. These are Weka Classifier
	 * objects.
	 */
	protected WekaClassifierThread[] ensembleThread;

	private transient ExecutorService threadExecutor;

	/**
	 * The ensemble of binary relevance models. These are Weka Classifier
	 * objects.
	 */
	protected Classifier[] ensemble;
	/**
	 * The correspondence between ensemble models and labels
	 */
	protected String[] correspondence;
	protected BinaryRelevanceTransformation brt;

	/**
	 * Create a new instance
	 *
	 * @param classifier
	 *            the base-level classification algorithm that will be used for
	 *            training each of the binary models
	 */
	public ParallelBinaryRelevance(Classifier classifier) {
		super(classifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildInternal(MultiLabelInstances train) throws Exception {
		try {

			ensemble = null;

			threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

			ensembleThread = new WekaClassifierThread[numLabels];

			correspondence = new String[numLabels];

			brt = new BinaryRelevanceTransformation(train);

			for (int i = 0; i < numLabels; i++) {

				correspondence[i] = train.getDataSet().attribute(labelIndices[i]).name();

				Instances shell = brt.transformInstances(i);

				ensembleThread[i] = new WekaClassifierThread(
						weka.classifiers.AbstractClassifier.makeCopy(baseClassifier), shell);

				threadExecutor.execute(ensembleThread[i]);
			}

			threadExecutor.shutdown();

			if (!threadExecutor.awaitTermination(30, TimeUnit.DAYS)) {
				System.out.println("Threadpool timeout occurred");
			}

		} catch (InterruptedException ie) {
			System.out.println("Threadpool prematurely terminated due to interruption in thread that created pool");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MultiLabelOutput makePredictionInternal(Instance instance) {

		threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		boolean[] bipartition = new boolean[numLabels];
		double[] confidences = new double[numLabels];

		try {

			for (int counter = 0; counter < numLabels; counter++) {

				Instance transformedInstance = brt.transformInstance(instance, counter);

				double distribution[];

				// To test the instance
				ensembleThread[counter].setAction(1);

				ensembleThread[counter].setCurrentInstance(transformedInstance);

				try {

					threadExecutor.execute(ensembleThread[counter]);

				} catch (Exception e) {
					System.out.println(e);
					return null;
				}
			}

			threadExecutor.shutdown();

			if (!threadExecutor.awaitTermination(30, TimeUnit.DAYS)) {
				System.out.println("Threadpool timeout occurred");
			}
		} catch (InterruptedException ie) {
			System.out.println("Threadpool prematurely terminated due to interruption in thread that created pool");
		}

		for (int counter = 0; counter < numLabels; counter++) {

			double distribution[] = ensembleThread[counter].getDistributionForInstance();

			int maxIndex = (distribution[0] > distribution[1]) ? 0 : 1;

			// Ensure correct predictions both for class values {0,1} and {1,0}
			bipartition[counter] = (maxIndex == 1);

			// The confidence of the label being equal to 1
			confidences[counter] = distribution[1];
		}

		MultiLabelOutput mlo = new MultiLabelOutput(bipartition, confidences);

		return mlo;
	}

	/**
	 * {@inheritDoc}
	 */
	public Classifier getModel(String labelName) {
		for (int i = 0; i < numLabels; i++) {
			if (correspondence[i].equals(labelName)) {
				return ensembleThread[i].getClassifier();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Classifier[] getEnsemble() {

		Classifier ensembleWeka[] = new Classifier[ensembleThread.length];

		for (int i = 0; i < numLabels; i++) {
			ensembleWeka[i] = ensembleThread[i].getClassifier();
		}

		return ensembleWeka;
	}

	/**
	 * {@inheritDoc}
	 */
	public BinaryRelevanceTransformation getBrt() {
		return brt;
	}
}
