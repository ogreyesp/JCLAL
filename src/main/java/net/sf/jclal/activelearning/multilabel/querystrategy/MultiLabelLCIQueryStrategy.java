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

import net.sf.jclal.classifier.MulanClassifier;
import net.sf.jclal.dataset.MulanDataset;
import weka.core.Instance;

/**
 * Implementation of LCI active strategy. See for more information: Li, X. and
 * Guo, Y. (2013). Active Learning with Multi-label SVM Classification.
 *
 * @author Oscar Gabriel Reyes Pupo
 *
 */
public class MultiLabelLCIQueryStrategy extends AbstractMultiLabelQueryStrategy {

	private double labelCardinality;

	/**
	 * Empty (default) constructor
	 */
	public MultiLabelLCIQueryStrategy() {

		super();

		setMaximal(true);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public double utilityInstance(Instance instance) {

		// Predict the label set
		boolean[] categoryVector = ((MulanClassifier) getClassifier()).getBipartition(instance);

		// Count the nuber of true labels

		int positiveLabels = 0;

		for (boolean b : categoryVector) {
			positiveLabels += (b ? 1 : 0);
		}

		return Math.abs(positiveLabels - labelCardinality);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] testUnlabeledData() {

		labelCardinality = ((MulanDataset) getLabelledData()).getMultiLabelDataset().getCardinality();

		return super.testUnlabeledData();
	}
}
