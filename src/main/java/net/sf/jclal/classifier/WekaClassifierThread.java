package net.sf.jclal.classifier;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Classifier thread class
 *
 * @author Oscar Gabriel Reyes Pupo
 */
public class WekaClassifierThread extends Thread implements Serializable{

	private static final long serialVersionUID = -7142881199377451483L;

	private Classifier classifier;

    private Instances dataset;

    //It represents the current instance for testing
    private Instance currentInstance;

    //It represents the distribution for the current instance
    private double[] distributionForInstance;

    //It represents the action to do. For default the model is constructed
    private int action = 0;

    public double[] getDistributionForInstance() {
        return distributionForInstance;
    }

    public void setDistributionForInstance(double[] distributionForInstance) {
        this.distributionForInstance = distributionForInstance;
    }

    public Instance getCurrentInstance() {
        return currentInstance;
    }

    public void setCurrentInstance(Instance currentInstance) {
        this.currentInstance = currentInstance;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public Instances getDataset() {
        return dataset;
    }

    public void setDataset(Instances dataset) {
        this.dataset = dataset;
    }

    public WekaClassifierThread(Classifier cl, Instances instances) {
        this.classifier = cl;
        this.dataset = instances;
    }

    @Override
    public void run() {
        try {

            switch (action) {

                //Build the classifier
                case 0:
                    classifier.buildClassifier(dataset);
                    break;

                case 1:
                    distributionForInstance= classifier.distributionForInstance(currentInstance);
                    break;

                default:
                    classifier.buildClassifier(dataset);
                    break;
            }

        } catch (Exception ex) {
            Logger.getLogger(WekaClassifierThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
