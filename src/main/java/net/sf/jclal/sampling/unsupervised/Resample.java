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
package net.sf.jclal.sampling.unsupervised;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.jclal.core.IDataset;
import net.sf.jclal.dataset.MulanDataset;
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
 * dataset may be specified. It is an adaptation of
 * weka.filters.unsupervised.instance.Resample class of Weka.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class Resample extends AbstractSampling {

	/**
	 * for serialization
	 */
	static final long serialVersionUID = 3119607037607101160L;

	/**
	 * Whether to perform sampling with replacement or without
	 */
	protected boolean noReplacement = true;

	/**
	 * Whether to invert the selection (only if instances are drawn WITHOUT
	 * replacement)
	 *
	 */
	protected boolean invertSelection = false;

	/**
	 * Whether to perform sampling with replacement or without
	 * 
	 * @return Whether the replacement is used or not.
	 */
	public boolean isNoReplacement() {
		return noReplacement;
	}

	/**
	 * Whether to perform sampling with replacement or without
	 * 
	 * @param noReplacement
	 *            the replacement is used or not.
	 * 
	 */
	public void setNoReplacement(boolean noReplacement) {
		this.noReplacement = noReplacement;
	}

	/**
	 * Whether to invert the selection (only if instances are drawn WITHOUT
	 * replacement)
	 * 
	 * @return whether the invert selection is used or not
	 *
	 */
	public boolean isInvertSelection() {
		return invertSelection;
	}

	/**
	 * Whether to invert the selection (only if instances are drawn WITHOUT
	 * replacement)
	 * 
	 * @param invertSelection
	 *            Set the invert selection.
	 *
	 */
	public void setInvertSelection(boolean invertSelection) {
		this.invertSelection = invertSelection;
	}

	/**
	 * Creates the subsample with replacement
	 *
	 * @param dataSet
	 *            The dataset to extract a percent of instances
	 * @param sampleSize
	 *            the size to generate
	 */
	public void createSubsampleWithReplacement(IDataset dataSet, int sampleSize) {

		int origSize = dataSet.getNumInstances();

		Set<Integer> indexes = new HashSet<Integer>();

		Instances labeledInstances = new Instances(dataSet.getDataset(), sampleSize);

		// Fill the labeled set
		for (int i = 0; i < sampleSize; i++) {
			int index = getRandgen().choose(0, origSize);
			labeledInstances.add((Instance) dataSet.instance(index).copy());
			indexes.add(index);
		}

		if (dataSet instanceof WekaDataset) {
			setLabeledData(new WekaDataset(labeledInstances));
		}

		if (dataSet instanceof MulanDataset) {
			setLabeledData(new MulanDataset(labeledInstances, ((MulanDataset) dataSet).getLabelsMetaData()));
		}

		ArrayList<Container> indexesArray = new ArrayList<Container>();

		for (Integer i : indexes) {
			indexesArray.add(new Container(i, i));
		}

		// The array is ordered in descendent order
		OrderUtils.mergeSort(indexesArray, true);

		// Copy the entire dataset into unlabeled set
		Instances unlabeledInstances = new Instances(dataSet.getDataset());

		// remove the instances that have been selected previously
		for (Container pair : indexesArray) {
			unlabeledInstances.remove(Integer.parseInt(pair.getValue().toString()));
		}

		if (dataSet instanceof WekaDataset) {
			setUnlabeledData(new WekaDataset(unlabeledInstances));
		}

		if (dataSet instanceof MulanDataset) {
			setUnlabeledData(new MulanDataset(unlabeledInstances, ((MulanDataset) dataSet).getLabelsMetaData()));
		}

		// clean up
		unlabeledInstances.clear();
		labeledInstances.clear();

		unlabeledInstances = null;
		labeledInstances = null;

		indexes.clear();
		indexesArray.clear();

		indexes = null;
		indexesArray = null;
	}

	/**
	 * Creates the subsample without replacement
	 *
	 * @param dataSet
	 *            The dataset to extract a percent of instances
	 * @param sampleSize
	 *            the size to generate
	 */
	public void createSubsampleWithoutReplacement(IDataset dataSet, int sampleSize) {

		int origSize = dataSet.getNumInstances();

		if (sampleSize > origSize) {
			sampleSize = origSize;
			System.err.println("Resampling with replacement can only use percentage <=100% - " + "Using full dataset!");
		}

		List<Integer> indixes = new ArrayList<Integer>(origSize);
		List<Integer> indixesNew = new ArrayList<Integer>(sampleSize);

		// generate list of all indices to draw from
		for (int i = 0; i < origSize; i++) {
			indixes.add(i);
		}

		// draw X random indices (selected ones get removed before next draw)
		for (int i = 0; i < sampleSize; i++) {
			int index = getRandgen().choose(0, indixes.size());
			indixesNew.add(indixes.get(index));
			indixes.remove(index);
		}

		if (isInvertSelection()) {
			List<Integer> indixesNewTemp = new ArrayList<Integer>(indixesNew);
			indixesNew = indixes;
			indixes = new ArrayList<Integer>(indixesNewTemp);
		} else {
			Collections.sort(indixesNew);
		}

		Instances labeledInstances = new Instances(dataSet.getDataset(), sampleSize);

		// Fill the labeled set
		for (int i = 0; i < indixesNew.size(); i++) {
			labeledInstances.add((Instance) dataSet.instance(indixesNew.get(i)).copy());
		}

		if (dataSet instanceof WekaDataset) {
			setLabeledData(new WekaDataset(labeledInstances));
		}

		if (dataSet instanceof MulanDataset) {
			setLabeledData(new MulanDataset(labeledInstances, ((MulanDataset) dataSet).getLabelsMetaData()));
		}

		Instances unlabeledInstances = new Instances(dataSet.getDataset(), origSize - sampleSize);

		// Fill the unlabeled set
		for (int i = 0; i < indixes.size(); i++) {
			unlabeledInstances.add((Instance) dataSet.instance(indixes.get(i)).copy());
		}

		if (dataSet instanceof WekaDataset) {
			setUnlabeledData(new WekaDataset(unlabeledInstances));
		}

		if (dataSet instanceof MulanDataset) {
			setUnlabeledData(new MulanDataset(unlabeledInstances, ((MulanDataset) dataSet).getLabelsMetaData()));
		}

		// clean up
		labeledInstances.clear();
		unlabeledInstances.clear();
		indixes.clear();
		indixesNew.clear();
		labeledInstances = null;
		unlabeledInstances = null;
		indixes = null;
		indixesNew = null;
	}

	/**
	 * Sampling instances
	 * 
	 * @param dataSet
	 *            The dataset to extract the instances.
	 */
	@Override
	public void sampling(IDataset dataSet) {

		int sampleSize = (int) (dataSet.getNumInstances() * getPercentageInstancesToLabelled() / 100);

		if (isNoReplacement()) {
			createSubsampleWithoutReplacement(dataSet, sampleSize);
		} else {
			createSubsampleWithReplacement(dataSet, sampleSize);
		}

	}

	/**
	 *
	 * @param configuration
	 *            The configuration object for Resample.
	 * 
	 *            The XML labels supported are:
	 * 
	 *            <ul>
	 *            <li><b>no-replacement= boolean</b></li>
	 *            <li><b>invert-selection= boolean</b></li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		super.configure(configuration);

		boolean noReplacementT = configuration.getBoolean("no-replacement", noReplacement);

		setNoReplacement(noReplacementT);

		boolean invert = configuration.getBoolean("invert-selection", invertSelection);

		setInvertSelection(invert);
	}
}