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
package net.sf.jclal.sampling.supervised;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.WekaDataset;
import net.sf.jclal.sampling.AbstractSampling;
import net.sf.jclal.util.sort.Container;
import net.sf.jclal.util.sort.OrderUtils;
import org.apache.commons.configuration.Configuration;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Produces a random subsample of a dataset using either sampling with
 * replacement or without replacement. The number of instances in the generated
 * dataset may be specified. The dataset must have a nominal class attribute. If
 * not, use the unsupervised version. The filter can be made to maintain the
 * class distribution in the subsample, or to bias the class distribution toward
 * a uniform distribution. This class must be used on single-label data.
 *
 * It is an adaptation of weka.filters.supervised.instance.Resample class of
 * Weka.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class Resample extends AbstractSampling {

    static final long serialVersionUID = 3119607037607101160L;

    /**
     * Whether to perform sampling with replacement or without
     */
    protected boolean noReplacement = true;

    /**
     * The degree of bias towards uniform (nominal) class distribution.
     *
     * Bias factor towards uniform class distribution. 0 = distribution in input
     * data -- 1 = uniform distribution. (default 0)
     *
     */
    protected double biasToUniformClass = 0;

    /**
     * Whether to invert the selection (only if instances are drawn WITHOUT
     * replacement)
     *
     * @see #noReplacement
     */
    protected boolean invertSelection = false;

    /**
     * The degree of bias towards uniform (nominal) class distribution.
     *
     * Bias factor towards uniform class distribution. 0 = distribution in input
     * data -- 1 = uniform distribution. (default 0)
     * 
     * @return Returns the bias used.
     *
     */
    public double getBiasToUniformClass() {
        return biasToUniformClass;
    }

    /**
     * The degree of bias towards uniform (nominal) class distribution.
     *
     * Bias factor towards uniform class distribution. 0 = distribution in input
     * data -- 1 = uniform distribution. (default 0)
     * 
     * @param biasToUniformClass The bias to use.
     *
     */
    public void setBiasToUniformClass(double biasToUniformClass) {
        this.biasToUniformClass = biasToUniformClass;
    }

    /**
     * Whether to perform sampling with replacement or without
     * 
     * @return Returns whether the sampling is with replacement or not.
     */
    public boolean isNoReplacement() {
        return noReplacement;
    }

    /**
     * Whether to perform sampling with replacement or without
     * 
     * @param noReplacement The variable that indicates the replacement or not.
     */
    public void setNoReplacement(boolean noReplacement) {
        this.noReplacement = noReplacement;
    }

    /**
     * Whether to invert the selection (only if instances are drawn WITHOUT
     * replacement)
     *
     *  
     * @return Returns if the flag of invert selection is activated.
     */
    public boolean isInvertSelection() {
        return invertSelection;
    }

    /**
     * Whether to invert the selection (only if instances are drawn WITHOUT
     * replacement)
     * 
     * @param invertSelection The flag indicating if the invert selection is activated.
     * 
     */
    public void setInvertSelection(boolean invertSelection) {
        this.invertSelection = invertSelection;
    }

    /**
     * Create the subsample with replacement
     *
     * @param dataSet The dataset to extract a percent of instances
     * @param sampleSize the size to generate
     * @param actualClasses The actual classes
     * @param classIndices The indexes of the classes
     */
    public void createSubsampleWithReplacement(WekaDataset dataSet,
            int sampleSize, int actualClasses, int[] classIndices) {

        int originalSize = dataSet.getNumInstances();

        Set<Integer> indexes = new HashSet<Integer>();

        Instances labeledInstances = new Instances(dataSet.getDataset(), sampleSize);

        for (int i = 0; i < sampleSize; i++) {

            int index = 0;

            if (getRandgen().uniform(0, 1) < biasToUniformClass) {

                // Pick a random class (of those classes that actually appear)
                int cIndex = getRandgen().choose(0, actualClasses);

                for (int j = 0, k = 0; j < classIndices.length - 1; j++) {
                    if ((classIndices[j] != classIndices[j + 1]) && (k++ >= cIndex)) {
                        // Pick a random instance of the designated class
                        index = classIndices[j]
                                + getRandgen().choose(0, classIndices[j + 1] - classIndices[j]);
                        break;
                    }
                }
            } else {
                index = getRandgen().choose(0, originalSize);
            }

            labeledInstances.add((Instance) dataSet.instance(index).copy());
            indexes.add(index);
        }

        setLabeledData(new WekaDataset(labeledInstances));

        ArrayList<Container> indexesArray = new ArrayList<Container>();

        for (Integer i : indexes) {
            indexesArray.add(new Container(i, i));
        }

        //The array is ordered in descendent order
        OrderUtils.mergeSort(indexesArray, true);

        //Copy the entire dataset into unlabeled set
        Instances unlabeledInstances = new Instances(dataSet.getDataset());

        //remove the instances that have been selected previously
        for (Container pair : indexesArray) {
            unlabeledInstances.remove(Integer.parseInt(pair.getValue().toString()));
        }

        setUnlabeledData(new WekaDataset(unlabeledInstances));

        //clean up
        labeledInstances.clear();
        unlabeledInstances.clear();
        indexes.clear();
        indexesArray.clear();

        labeledInstances = null;
        unlabeledInstances = null;
        indexes = null;
        indexesArray = null;

    }

    /**
     * Create the subsample without replacement
     *
     * @param dataSet The dataset to extract a percent of instances
     * @param sampleSize The size to generate
     * @param actualClasses The actual classes
     * @param classIndices The indexes of the classes
     */
    public void createSubsampleWithoutReplacement(WekaDataset dataSet,
            int sampleSize, int actualClasses, int[] classIndices) {

        int origSize = dataSet.getNumInstances();

        if (sampleSize > origSize) {
            sampleSize = origSize;
            System.err.println(
                    "Resampling without replacement can only use percentage <=100% - "
                    + "Using full dataset!");
        }

        List<Integer>[] indices = new ArrayList[classIndices.length - 1];
        List<Integer>[] indicesNew = new ArrayList[classIndices.length - 1];

        // generate list of all indices to draw from
        for (int i = 0; i < classIndices.length - 1; i++) {

            indices[i] = new ArrayList<Integer>(classIndices[i + 1] - classIndices[i]);
            indicesNew[i] = new ArrayList<Integer>(indices[i].size());

            for (int n = classIndices[i]; n < classIndices[i + 1]; n++) {
                indices[i].add(n);
            }
        }

        // draw X samples
        int currentSize = origSize;

        for (int i = 0; i < sampleSize; i++) {

            int index = 0;

            if (getRandgen().uniform(0, 1) < biasToUniformClass) {

                // Pick a random class (of those classes that actually appear)
                int cIndex = getRandgen().choose(0, actualClasses);

                for (int j = 0, k = 0; j < classIndices.length - 1; j++) {

                    if ((classIndices[j] != classIndices[j + 1]) && (k++ >= cIndex)) {
                        // no more indices for this class left, try again
                        if (indices[j].isEmpty()) {
                            i--;
                            break;
                        }

                        // Pick a random instance of the designated class
                        index = getRandgen().choose(0, indices[j].size());
                        indicesNew[j].add(indices[j].get(index));
                        indices[j].remove(index);

                        break;
                    }
                }
            } else {

                index = getRandgen().choose(0, currentSize);

                for (int n = 0; n < actualClasses; n++) {
                    if (index < indices[n].size()) {
                        indicesNew[n].add(indices[n].get(index));
                        indices[n].remove(index);
                        break;
                    } else {
                        index -= indices[n].size();
                    }
                }

                currentSize--;
            }
        }

        // sort indices
        if (isInvertSelection()) {

            //Copy indicesNew into indicesNewTemp
            List<Integer>[] indicesNewTemp = new ArrayList[indicesNew.length];

            int index = 0;

            for (List<Integer> list : indicesNew) {
                indicesNewTemp[index++] = new ArrayList(list);
            }

            //Copy indices into indicesNew
            indicesNew = new ArrayList[indices.length];

            index = 0;

            for (List<Integer> list : indices) {
                indicesNew[index++] = new ArrayList(list);
            }

            //Copy indicesNewTemp into indices
            indices = indicesNewTemp;

        } else {
            for (int i = 0; i < indicesNew.length; i++) {
                Collections.sort(indicesNew[i]);
            }
        }

        Instances labeledInstances = new Instances(dataSet.getDataset(), sampleSize);

        // addAll to ouput
        for (int i = 0; i < indicesNew.length; i++) {
            for (int n = 0; n < indicesNew[i].size(); n++) {
                labeledInstances.add((Instance) dataSet.instance(indicesNew[i].get(n)).copy());
            }
        }

        setLabeledData(new WekaDataset(labeledInstances));

        Instances unlabeledInstances = new Instances(dataSet.getDataset(), origSize - sampleSize);

        // addAll to ouput
        for (int i = 0; i < indices.length; i++) {
            for (int n = 0; n < indices[i].size(); n++) {
                unlabeledInstances.add((Instance) dataSet.instance(indices[i].get(n)).copy());
            }
        }

        setUnlabeledData(new WekaDataset(unlabeledInstances));

        // clean up
        for (int i = 0; i < indices.length; i++) {
            indices[i].clear();
            indicesNew[i].clear();
        }

        indices = null;
        indicesNew = null;

        labeledInstances.clear();
        unlabeledInstances.clear();

        labeledInstances = null;
        unlabeledInstances = null;

    }

    /**
     * Sampling the instances
     * 
     * @param dataSet The dataset to extract the instances.
     */
    @Override
    public void sampling(IDataset dataSet) {

        if (!(dataSet instanceof WekaDataset)) {
            throw new RuntimeException("This sample method only can be used with a single label weka dataset");
        }

        WekaDataset wekaDataSet = (WekaDataset) dataSet;

        int origSize = wekaDataSet.getNumInstances();
        int sampleSize = (int) (origSize * getPercentageInstancesToLabelled() / 100);

        // Subsample that takes class distribution into consideration
        // Sort according to class attribute.
        wekaDataSet.getDataset().sort(wekaDataSet.getClassIndex());

        // Create an index of where each class value starts
        int[] classIndices = new int[wekaDataSet.getNumClasses() + 1];

        int currentClass = 0;

        classIndices[currentClass] = 0;

        for (int i = 0; i < dataSet.getNumInstances(); i++) {
            Instance current = dataSet.instance(i);

            if (current.classIsMissing()) {
                for (int j = currentClass + 1; j < classIndices.length; j++) {
                    classIndices[j] = i;
                }
                break;
            } else if (current.classValue() != currentClass) {

                for (int j = currentClass + 1; j <= current.classValue(); j++) {
                    classIndices[j] = i;
                }

                currentClass = (int) current.classValue();
            }
        }

        if (currentClass <= wekaDataSet.getNumClasses()) {

            for (int j = currentClass + 1; j < classIndices.length; j++) {
                classIndices[j] = dataSet.getNumInstances();
            }
        }

        int actualClasses = 0;

        for (int i = 0; i < classIndices.length - 1; i++) {
            if (classIndices[i] != classIndices[i + 1]) {
                actualClasses++;
            }
        }

        // Convert pending input instances
        if (isNoReplacement()) {
            createSubsampleWithoutReplacement(
                    wekaDataSet, sampleSize, actualClasses, classIndices);
        } else {
            createSubsampleWithReplacement(
                    wekaDataSet, sampleSize, actualClasses, classIndices);
        }
    }

    /**
     *
     * @param configuration The configuration object for Resample.
     * The XML labels supported are:
     * <ul>
     * <li><b>no-replacement= boolean</b></li>
     * <li><b>invert-selection= boolean</b></li>
     * <li><b>m_BiasToUniformClass= double</b></li>
     * </ul>
     */
    @Override
    public void configure(Configuration configuration) {

        super.configure(configuration);

        boolean noReplacementT = configuration.getBoolean("no-replacement", noReplacement);

        setNoReplacement(noReplacementT);

        boolean invert = configuration.getBoolean("invert-selection", invertSelection);

        setInvertSelection(invert);

        double mBias = configuration.getDouble("bias-to-uniform-class", biasToUniformClass);

        setBiasToUniformClass(mBias);
    }
}
