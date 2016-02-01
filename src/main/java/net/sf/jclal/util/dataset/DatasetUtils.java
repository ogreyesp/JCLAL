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
package net.sf.jclal.util.dataset;

import mulan.data.IterativeStratification;
import mulan.data.LabelPowersetStratification;
import mulan.data.MultiLabelInstances;
import mulan.data.Statistics;
import mulan.data.Stratification;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.core.IRandGen;
import net.sf.jclal.dataset.MulanDataset;
import net.sf.jclal.dataset.WekaDataset;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.MultiInstanceWrapper;

/**
 * Utility class for handling a dataset.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class DatasetUtils {

    /**
     * Load a WEKA dataset
     *
     * @param arffFilePath The file path of the weka dataset.
     * @return The Weka dataset.
     */
    public static WekaDataset loadWekaDataSet(String arffFilePath) {

        return new WekaDataset(arffFilePath);
    }

    /**
     * Load a MULAN dataset
     *
     * @param arffFilePath The file path of the mulan dataset.
     * @param xmlPath The file path of the xml.
     * @return The Mulan dataset.
     */
    public static MulanDataset loadMulanDataSet(String arffFilePath, String xmlPath) {
        return new MulanDataset(arffFilePath, xmlPath);
    }

    /**
     * Shuffles the instances in the set so that they are ordered randomly.
     *
     * @param randGen The method to generate random numbers
     * @param instances The dataset to randomize
     */
    public static void randomize(IRandGen randGen, IDataset instances) {

        int numInstances = instances.getNumInstances();

        for (int i = numInstances - 1; i > 0; i--) {
            int j = randGen.choose(0, i + 1);

            Instance in = instances.instance(i);
            instances.set(i, instances.instance(j));
            instances.set(j, in);
        }
    }

    /**
     * Copy the attributes of an instance without including the class attribute
     *
     * @param source Numeric attributes of a instance.
     * @param classIndex Class index of the dataset.
     * @return Attributes without the class attribute.
     */
    public static double[] copyFeatures(double[] source, int classIndex) {

        double[] vectorXFeatures = new double[source.length - 1];

        System.arraycopy(source, 0, vectorXFeatures, 0, classIndex);

        System.arraycopy(source, classIndex + 1, vectorXFeatures, classIndex,
                source.length - (classIndex + 1));

        return vectorXFeatures;
    }

    /**
     * Stratify a multi-label dataset
     *
     * @param numFolds the number of folds
     * @param dataset The dataset to stratify
     * @return The stratified dataset
     */
    public static MulanDataset[] stratifyMultiLabelDataSet(int numFolds, MulanDataset dataset) {

        Stratification stratification;

        Statistics sta = new Statistics();

        sta.calculateStats(dataset.getMultiLabelDataset());

        double ratio = sta.labelCombCount().size()
                / (double) dataset.getNumInstances();

        if (ratio <= 0.1) {
            stratification = new LabelPowersetStratification();
        } else {
            stratification = new IterativeStratification();
        }

        MultiLabelInstances[] stratifiedFolds = stratification.stratify(
                dataset.getMultiLabelDataset(), numFolds);

        MulanDataset[] mulanDatasets = new MulanDataset[stratifiedFolds.length];

        int index = 0;

        for (MultiLabelInstances mlDataset : stratifiedFolds) {
            mulanDatasets[index++] = new MulanDataset(mlDataset);
        }

        //Clean up
        for (MultiLabelInstances mlDataset : stratifiedFolds) {
            mlDataset.getDataSet().clear();
        }

        stratifiedFolds = null;

        return mulanDatasets;
    }

    /**
     * Stratify a single-label dataset
     *
     * @param numFolds The number of folds
     * @param dataSet the dataset to stratify
     */
    public static void stratifySingleLabelDataSet(int numFolds, WekaDataset dataSet) {
        dataSet.getDataset().stratify(numFolds);
    }

    /**
     * Return a training set that will be used in the k-fold cross validation
     *
     * @param dataSet the dataset
     * @param numFolds the number of folds
     * @param fold the current fold
     * @return the trainset
     */
    public static IDataset trainCV(IDataset dataSet, int numFolds, int fold) {

        return new WekaDataset(dataSet.getDataset().trainCV(numFolds, fold));
    }

    /**
     * Return a test set that will be used in the k-fold cross validation
     *
     * @param dataSet The dataset to use.
     * @param numFolds The number of folds.
     * @param fold The number of the current fold.
     * @return The test set.
     */
    public static IDataset testCV(IDataset dataSet, int numFolds, int fold) {
        return new WekaDataset(dataSet.getDataset().testCV(numFolds, fold));
    }

    /**
     * Return a training set that will be used in the fold cross validation
     *
     * @param dataSets The dataset to use.
     * @param fold The index of folds.
     * @return The training set.
     */
    public static IDataset trainCV(MulanDataset[] dataSets, int fold) {

        Instances trainDataSet = new Instances(dataSets[0].getDataset(),
                dataSets[0].getDataset().numInstances() * (dataSets.length - 1));

        for (int i = 0; i < dataSets.length; i++) {
            if (i != fold) {
                trainDataSet.addAll(dataSets[i].getDataset());
            }
        }

        return new MulanDataset(trainDataSet, dataSets[0].getLabelsMetaData());
    }

    /**
     * Return a test set that will be used in the k-fold cross validation
     *
     * @param dataSets The dataset to use.
     * @param fold The number of folds.
     * @return The test set.
     */
    public static IDataset testCV(MulanDataset[] dataSets, int fold) {

        Instances testDataSet = new Instances(dataSets[fold].getDataset());

        return new MulanDataset(testDataSet, dataSets[fold].getLabelsMetaData());

    }

    /**
     *
     * Wrapper method, it modifies the Multi-Instance data by assigning the bag
     * label to each instance of the corresponding bag and then builds a
     * classifier based on the modified data (DONG, Lin, 2006, A Comparison of
     * Multi-instance Learning Algorithms, University of Waikato)
     *
     * @param source An array of instances
     * @return The dataset
     * @throws Exception The exception to launch
     */
    public static Instances multiInstanceWrapperFormat(Instances source[])
            throws Exception {

        int size = 0;
        for (Instances instances : source) {
            size += instances.numInstances();
        }

        Instances all = new Instances(source[0], size);
        for (Instances curr : source) {
            all.addAll(curr);
        }

        MultiInstanceWrapper wrapper = new MultiInstanceWrapper();
        wrapper.setInputFormat(all);
        Instances process = MultiInstanceWrapper.useFilter(all, wrapper);

        //clean
        all.delete();
        all = null;
        wrapper = null;

        return process;
    }

    /**
     * Extract the classes from single-label dataset.
     *
     * @param labeled The labeled set
     * @return An array of string
     */
    public static String[] valueClasses(Instances labeled) {

        int classIndex = labeled.classIndex();
        String[] valueClasses = new String[labeled.attribute(classIndex).numValues()];

        for (int i = 0; i < valueClasses.length; i++) {
            valueClasses[i] = labeled.attribute(classIndex).value(i);
        }

        return valueClasses;
    }
}
