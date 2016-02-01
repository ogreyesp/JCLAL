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
package net.sf.jclal.activelearning.multilabel.querystrategy;

import mulan.data.LabelsMetaData;
import net.sf.jclal.activelearning.querystrategy.AbstractQueryStrategy;
import net.sf.jclal.dataset.MulanDataset;
import weka.core.Instance;

/**
 * Abstract class for multi-label active learning strategies
 *
 */
public abstract class AbstractMultiLabelQueryStrategy extends AbstractQueryStrategy {

	private static final long serialVersionUID = 1L;

	/**
	 * The number of labels
	 */
	private int numLabels = -1;

	/**
	 * Array that stores the label indexes
	 */
	private int[] labelIndices;

	/**
	 * The labels metadata
	 */

	public LabelsMetaData labelsMetaData;

	/**
	 * Get the labels metadata
	 * 
	 * @return The labels metadata
	 * 
	 */
	public LabelsMetaData getLabelsMetaData() {

		if (labelsMetaData == null) {
			labelsMetaData = ((MulanDataset) getLabelledData()).getLabelsMetaData();
		}

		return labelsMetaData;
	}

	/**
	 * Get the indexes of labels
	 * 
	 * @return The indexes of labels
	 * 
	 */
	public int[] getLabelIndices() {

		if (labelIndices == null) {
			labelIndices = ((MulanDataset) getLabelledData()).getLabelIndexes();
		}

		return labelIndices;
	}

	/**
	 * Get the number of labels
	 * 
	 * @return The number of labels
	 * 
	 */
	public int getNumLabels() {

		if (numLabels == -1) {
			numLabels = ((MulanDataset) getLabelledData()).getNumLabels();
		}

		return numLabels;
	}

	/**
	 * Get the true labels of the instance
	 * 
	 * @param instance
	 *            The instance to test
	 * @return The true category vector
	 */
	public boolean[] getTrueLabels(Instance instance) {

		boolean[] trueLabels = new boolean[getNumLabels()];

		for (int counter = 0; counter < getNumLabels(); counter++) {

			int classIdx = getLabelIndices()[counter];

			String classValue = instance.attribute(classIdx).value((int) instance.value(classIdx));

			trueLabels[counter] = classValue.equals("1");
		}

		return trueLabels;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void algorithmFinished() {

		labelsMetaData = null;
		labelIndices = null;
	}

}
