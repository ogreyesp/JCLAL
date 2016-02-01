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
package net.sf.jclal.activelearning.batchmode;

import net.sf.jclal.core.IBatchMode;
import net.sf.jclal.core.IConfigure;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Abstract class for batch mode active learning.
 *
 * Batch-mode active learning allows the learner to query instances in groups,
 * which is better suited to parallel labeling environments or models with slow
 * training procedures.
 *
 * See for more information: Settles, B. (2010). Active Learning Literature
 * Survey (p. 67). Section 6.1.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractBatchMode implements IBatchMode, IConfigure {

	/**
	 * Number of instances to select in each iteration. By default is 1.
	 */
	private int batchSize = 1;

	/**
	 * 
	 * @return The number of instances to select in each iteration
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * Set the batch size
	 * 
	 * @param batchSize
	 *            The number of instances to select in each iteration
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 *
	 * @param configuration
	 *            The configuration of Abstract Batch Mode.
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li>batch-size= int</li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		// Set batchSize
		int batchT = configuration.getInt("batch-size", batchSize);
		if (batchT <= 0) {
			throw new ConfigurationRuntimeException(
					"\nIllegal batch size: <batch-size>" + batchT + "</batch-size>" + ". Batch size > 0");
		}
		setBatchSize(batchT);
	}

	@Override
	public String toString() {

		return this.getClass().getSimpleName();
	}
}
