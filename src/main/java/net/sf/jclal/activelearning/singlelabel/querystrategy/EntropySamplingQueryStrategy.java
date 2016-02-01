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

/**
 * Implementation of Entropy Sampling Strategy (Uncertainty Sampling) query
 * strategy.
 *
 * An uncertainty sampling query strategy that uses entropy as an uncertainty
 * measure
 *
 * Burr Settles. Active Learning Literature Survey. Computer Sciences Technical
 * Report 1648, University ofWisconsin–Madison. 2009.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public class EntropySamplingQueryStrategy extends UncertaintySamplingQueryStrategy {

	private static final long serialVersionUID = 3469267143262432871L;

	/**
	 * Empty(default) constructor.
	 */
	public EntropySamplingQueryStrategy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		double sumatoria = 0;

		double[] probabilities = distributionForInstance(instance);

		double log;

		for (double current : probabilities) {

			if (current != 0) {
				log = logbase2(current);
				sumatoria += current * log;
			}
		}

		return (sumatoria != 0) ? (-sumatoria) : 0;
	}

	private double logbase2(double d) {
		return Math.log(d) / Math.log(2);
	}
}