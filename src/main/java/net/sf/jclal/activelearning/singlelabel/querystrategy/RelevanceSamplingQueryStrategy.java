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
package net.sf.jclal.activelearning.singlelabel.querystrategy;

import weka.core.Instance;
import weka.core.Utils;

/**
 * Implementation of Relevance Sampling query strategy.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public class RelevanceSamplingQueryStrategy extends AbstractSingleLabelQueryStrategy {

	private static final long serialVersionUID = -2735889232662367116L;

	/**
	 * Manufacturer for defect.
	 */
	public RelevanceSamplingQueryStrategy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		double[] probs = distributionForInstance(instance);

		double mayor = probs[Utils.maxIndex(probs)];

		return mayor;

	}
}
