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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.MulanDataset;
import net.sf.jclal.sampling.AbstractSampling;
import net.sf.jclal.util.dataset.DatasetUtils;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Stratified Sampling for Multi Label instances.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class MultiLabelResample extends AbstractSampling {

    private static final long serialVersionUID = 8593376828542984703L;

    /**
     * Empty(default) constructor.
     */
    public MultiLabelResample() {

        super();

    }

    /**
     * Sampling the instances
     *
     * @param dataSet The dataset to extract the instances.
     */
    @Override
    public void sampling(IDataset dataSet) {

        try {

            if (!(dataSet instanceof MulanDataset)) {
                throw new RuntimeException("This sample method only can be used with a Mulan Dataset");
            }

            MulanDataset mulanDataSet = (MulanDataset) dataSet;

            int numInstances = (int) (getPercentageInstancesToLabelled()
                    * mulanDataSet.getNumInstances() / 100);

            MulanDataset[] stratifiedFolds = DatasetUtils.stratifyMultiLabelDataSet(10, mulanDataSet);

            List<Instance> finalInitialLabeledInstances = new ArrayList<Instance>();
            List<Instance> finalInitialUnlabeledData = new ArrayList<Instance>();

            int fold = 0;

            while (finalInitialLabeledInstances.size() < numInstances) {

                // Taken the instances that belong to the i-th fold
                Instances initialLabeledData = stratifiedFolds[fold++]
                        .getDataset();

                int i;

                for (i = 0; i < initialLabeledData.numInstances()
                        && finalInitialLabeledInstances.size() < numInstances; i++) {

                    finalInitialLabeledInstances.add(initialLabeledData.instance(i));
                }

                // Copy the rest of the fold in unlabeled set in the case that
                // finalInitialLabeledData is full
                if (finalInitialLabeledInstances.size() == numInstances) {

                    for (int j = i; j < initialLabeledData.numInstances(); j++) {

                        finalInitialUnlabeledData.add(initialLabeledData
                                .instance(j));
                    }

                }

            }

            while (fold < 10) {

                // Taken the instances that belong to the i-th fold
                Instances initialLabeledData = stratifiedFolds[fold++]
                        .getDataset();

                for (int i = 0; i < initialLabeledData.numInstances(); i++) {

                    finalInitialUnlabeledData.add(initialLabeledData
                            .instance(i));
                }
            }

            Instances labeledInstances = new Instances(mulanDataSet.getDataset(), 0);
            labeledInstances.addAll(finalInitialLabeledInstances);

            // Set the initial instance set of labeled data
            setLabeledData(new MulanDataset(new MultiLabelInstances(labeledInstances, mulanDataSet.getLabelsMetaData())));

            Instances unlabeledInstances = new Instances(mulanDataSet.getDataset(), 0);
            unlabeledInstances.addAll(finalInitialUnlabeledData);

            // Set the initial instance set of unlabeled data
            setUnlabeledData(new MulanDataset(new MultiLabelInstances(unlabeledInstances, mulanDataSet.getLabelsMetaData())));

            labeledInstances.clear();
            unlabeledInstances.clear();
            finalInitialLabeledInstances.clear();
            finalInitialUnlabeledData.clear();

            for (int i = 0; i < 10; i++) {
                stratifiedFolds[i].getDataset().clear();
            }

            labeledInstances = null;
            unlabeledInstances = null;
            finalInitialLabeledInstances = null;
            finalInitialUnlabeledData = null;
            stratifiedFolds = null;

        } catch (InvalidDataFormatException e) {
            Logger.getLogger(MultiLabelResample.class.getName()).log(
                    Level.SEVERE, null, e);
        }

    }
}