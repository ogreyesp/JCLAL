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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.classifier.AbstractClassifier;
import net.sf.jclal.classifier.WekaComitteClassifier;
import weka.core.Instance;

/**
 * Implementation of Query By Committee query strategy, variant:
 * Kullback-Leibler (KL) divergence.
 *
 *
 * A. McCallum and K. Nigam. Employing EM in pool-based active learning for text
 * classification. In Proceedings of the International Conference on Machine
 * Learning (ICML), pages 359–367. Morgan Kaufmann, 1998.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public class KullbackLeiblerDivergenceQueryStrategy extends QueryByCommittee {

	private static final long serialVersionUID = -8576549899843425912L;

	/**
	 * Empty constructor
	 */
	public KullbackLeiblerDivergenceQueryStrategy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		try {

			int committeeSize = ((AbstractClassifier) getClassifier()).getNumberClassifiers();

			double sumatoria = 0;

			// consensus probabilities
			double[] cprobs = getClassifier().distributionForInstance(instance);

			for (int i = 0; i < committeeSize; i++) {

				double sumInter = 0;

				double[] probs = ((WekaComitteClassifier) getClassifier()).distributionForInstanceByMember(instance, i);

				int p = 0;

				for (double prob : probs) {

					if (prob != 0) {
						sumInter += (prob * logbase2(prob / cprobs[p++]));
					}
				}

				sumatoria += sumInter;
			}

			sumatoria /= committeeSize;

			return sumatoria;

		} catch (Exception e) {

			Logger.getLogger(KullbackLeiblerDivergenceQueryStrategy.class.getName()).log(Level.SEVERE, null, e);
		}
		return 0;

	}

	/**
	 * Compute the logarithmic based 2.
	 * 
	 * @param d
	 *            The number
	 * @return The logarithmic based 2.
	 */
	public double logbase2(double d) {
		return Math.log(d) / Math.log(2);
	}
}
