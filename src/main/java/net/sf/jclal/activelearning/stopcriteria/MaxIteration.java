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

package net.sf.jclal.activelearning.stopcriteria;

import org.apache.commons.configuration.Configuration;
import net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm;
import net.sf.jclal.core.IAlgorithm;
import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IStopCriterion;

/**
 * Class that represents a stop criterion related with the maximum of iterations.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */

public class MaxIteration implements IStopCriterion, IConfigure {

	private static final long serialVersionUID = 8148193972540461736L;
	/**
	 * Max of iterations, by default it is equal to 50
	 */
	private int maxIteration = 50;

	@Override
    public boolean stop(IAlgorithm algorithm) {
            // If maximum number of iterations is exceeded, the algorithm is
            // finished

        return ((ClassicalALAlgorithm) algorithm).getIteration() > maxIteration;
    }

	/**
	 * @param settings
	 *            The configuration object for the MaxIteration stop criterion
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li>
	 *            <p>
	 *            <b>max-iteration= int</b>
	 *            </p>
	 *            </li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration settings) {

		// Set max iteration
		int maxIterationT = settings.getInt("max-iteration", maxIteration);
		setMaxIteration(maxIterationT);

	}

	/**
	 * Get the maximum number of iterations
	 * 
	 * @return The max iteration
	 */
	public int getMaxIteration() {
		return maxIteration;
	}

	/**
	 * Set the maximum number of iterations
	 *
	 * @param maxIteration
	 *            Set the max iteration
	 */
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}
}
