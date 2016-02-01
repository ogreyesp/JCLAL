/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jclal.dataset;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import mulan.data.InvalidDataFormatException;
import mulan.data.LabelsMetaData;
import mulan.data.MultiLabelInstances;
import net.sf.jclal.core.IDataset;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class that represents a MULAN dataset
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class MulanDataset extends AbstractDataset {

	private static final long serialVersionUID = 2649132022453312474L;
	private MultiLabelInstances multiLabelDataset;

	/**
	 * Constructs a empty Mulan Dataset
	 *
	 */
	public MulanDataset() {
	}

	/**
	 * Constructs a Mulan Dataset
	 *
	 * @param arffFilePath
	 *            The path to the arff file
	 * @param xmlPath
	 *            The path to the xml file
	 */
	public MulanDataset(String arffFilePath, String xmlPath) {
		try {
			multiLabelDataset = new MultiLabelInstances(arffFilePath, xmlPath);
		} catch (InvalidDataFormatException ex) {
			Logger.getLogger(MulanDataset.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Creates a Mulan Dataset from an MultiLabelInstances object
	 *
	 * @param dataset
	 *            The dataset to use
	 */
	public MulanDataset(MultiLabelInstances dataset) {

		multiLabelDataset = dataset.clone();

	}

	/**
	 * Creates a Mulan Dataset from other Mulan Dataset.
	 *
	 * @param dataset
	 *            The dataset to use.
	 */
	public MulanDataset(IDataset dataset) {
		multiLabelDataset = ((MulanDataset) dataset).getMultiLabelDataset().clone();
	}

	/**
	 * Creates a Mulan Dataset from an Instance object and the corresponding
	 * labelsMetaData
	 *
	 * @param dataset
	 *            The Instances object
	 * @param labelsMetaData
	 *            The LabelsMetaData object
	 */
	public MulanDataset(Instances dataset, LabelsMetaData labelsMetaData) {
		try {
			multiLabelDataset = new MultiLabelInstances(new Instances(dataset), labelsMetaData);
		} catch (InvalidDataFormatException ex) {
			Logger.getLogger(MulanDataset.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Creates a Mulan Dataset from a portion of the IDataset object
	 *
	 * @param dataset
	 *            The dataset
	 * @param first
	 *            The position of the first instance to copy
	 * @param toCopy
	 *            The number of instances to copy
	 */
	public MulanDataset(IDataset dataset, int first, int toCopy) {
		try {
			Instances instances = new Instances(dataset.getDataset(), first, toCopy);
			multiLabelDataset = new MultiLabelInstances(instances, ((MulanDataset) dataset).getLabelsMetaData());
		} catch (InvalidDataFormatException ex) {
			Logger.getLogger(MulanDataset.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Creates a empty Mulan Dataset from a MultiLabelInstances object
	 *
	 * @param dataset
	 *            The dataset
	 * @param initialCapacity
	 *            The initial capacity
	 */
	public MulanDataset(IDataset dataset, int initialCapacity) {
		try {
			Instances instances = new Instances(dataset.getDataset(), initialCapacity);
			multiLabelDataset = new MultiLabelInstances(instances, ((MulanDataset) dataset).getLabelsMetaData());
		} catch (InvalidDataFormatException ex) {
			Logger.getLogger(MulanDataset.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumAttributes() {
		return multiLabelDataset.getDataSet().numAttributes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset copy() {
		return new MulanDataset(multiLabelDataset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(IDataset dataset) {
		multiLabelDataset.getDataSet().addAll(dataset.getDataset());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumInstances() {
		return multiLabelDataset.getNumInstances();
	}

	/**
	 * Get the number of labels
	 * 
	 * @return The number of labels
	 */
	public int getNumLabels() {
		return multiLabelDataset.getNumLabels();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instance instance(int index) {

		return multiLabelDataset.getDataSet().instance(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, Instance instance) {

		multiLabelDataset.getDataSet().set(index, instance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instances getDataset() {

		return multiLabelDataset.getDataSet();
	}

	/**
	 * Set the multilabel dataset
	 * 
	 * @param dataset
	 *            The multilabel dataset
	 * 
	 */
	public void setDataset(MultiLabelInstances dataset) {

		multiLabelDataset = dataset;
	}

	/**
	 * Get the multilabel datset
	 * 
	 * @return The multilabel dataset
	 */
	public MultiLabelInstances getMultiLabelDataset() {

		return multiLabelDataset;
	}

	/**
	 * Get the metadata of labels
	 * 
	 * @return The labels metadata
	 */

	public LabelsMetaData getLabelsMetaData() {
		return multiLabelDataset.getLabelsMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Instance instance) {
		multiLabelDataset.getDataSet().add(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(int index) {
		multiLabelDataset.getDataSet().remove(index);
	}

	/**
	 * Get the indexes of the labels
	 * 
	 * @return The indexes of the labels
	 */
	public int[] getLabelIndexes() {
		return multiLabelDataset.getLabelIndices();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		multiLabelDataset.getDataSet().delete();
		multiLabelDataset = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDataset removeAllIndexes(int[] indexes) {

		IDataset dTemp = new MulanDataset(this, 0);

		// asc order
		Arrays.sort(indexes);

		for (int i = indexes.length - 1; i >= 0; i--) {

			dTemp.add((Instance) getDataset().instance(indexes[i]).copy());

			remove(indexes[i]);

		}

		return dTemp;
	}

}