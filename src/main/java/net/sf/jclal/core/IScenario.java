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
package net.sf.jclal.core;

/**
 * Interface for Active Learning Scenarios.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public interface IScenario extends JCLAL {

	/**
	 * Select the most informative instances according to the
	 * scenario, the query strategy and the batch mode. Stores the selected
	 * indexes of the instances in the query strategy.
	 */
	public void instancesSelection();

	/**
	 * Get the active learning strategy used
	 * 
	 * @return Query strategy.
	 */
	public IQueryStrategy getQueryStrategy();

	/**
	 * Set the active learning strategy used
	 * 
	 * @param queryStrategy
	 *            Query strategy.
	 */
	public void setQueryStrategy(IQueryStrategy queryStrategy);

	/**
	 * Label instances.
	 */
	public void labelInstances();

	/**
	 * Update labeled data.
	 */
	public void updateLabelledData();

	/**
	 * Evaluation test.
	 */
	public void evaluationTest();

	/**
	 * Perform the training phase.
	 */
	public void training();

	/**
	 * String representation
	 * 
	 * @return The representation of the scenario like a string.
	 */
	@Override
	public String toString();

	/**
	 * Get the oracle used. It allows to define how the selected instances will
	 * be labeled.
	 * 
	 * @return The oracle
	 */
	public IOracle getOracle();

	/**
	 * Set the oracle. It allows to define how the selected instances will be
	 * labeled.
	 * 
	 * @param oracle
	 *            The oracle to use
	 */
	public void setOracle(IOracle oracle);

	/**
	 * The batch mode strategy used, it allows to define which of the selected
	 * unlabelled instances will be labelled
	 * 
	 * @return The batch mode used
	 */
	public IBatchMode getBatchMode();

	/**
	 * The batch mode strategy used, it allows to define which of the selected
	 * unlabelled instances will be labelled.
	 * 
	 * @param batchMode
	 *            The bacth mode to use
	 */
	public void setBatchMode(IBatchMode batchMode);

	/**
	 * Used after completing the algorithm for the execution of any necessary
	 * actions, e.g., free memory, delete temporary files, etc.
	 */
	public void algorithmFinished();
}
