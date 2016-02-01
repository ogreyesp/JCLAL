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

import java.util.List;

/**
 * Algorithm interface.
 *
 * @author Sebastian Ventura
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public interface IAlgorithm extends JCLAL {

	/**
	 * Execution method
	 */
	public void execute();

	/**
	 * Pause this algorithm execution
	 */
	public void pause();

	/**
	 * Terminate this algorithm execution
	 */
	public void terminate();

	// Listener registering/unregistering
	/**
	 * Adds the specified listener to receive algorithm events from this
	 * algorithm.
	 *
	 * @param listener
	 *            The listener to be added
	 */
	public void addListener(IAlgorithmListener listener);

	/**
	 * Removes the specified listener so it no longer receives events from this
	 * algorithm.
	 *
	 * @param listener
	 *            The listener to be removed
	 *
	 * @return true If the listener is correctly removed
	 */
	public boolean removeListener(IAlgorithmListener listener);

	/**
	 * Returns the listeners of the algorithm
	 *
	 * @return A array list of listeners
	 */
	public List<IAlgorithmListener> getListeners();

	/**
	 * Makes a copy of the algorithm
	 *
	 * @return A copy of the object.
	 * @throws Exception
	 *             The exception that can be launched.
	 */
	public IAlgorithm makeCopy() throws Exception;

	/**
	 * Set the test dataset
	 * 
	 * @param testDataSet
	 *            The test dataset.
	 */
	public void setTestDataSet(IDataset testDataSet);

	/**
	 * Get the test dataset
	 *
	 * @return The test dataset.
	 */
	public IDataset getTestDataSet();

	/**
	 * Set the labeled dataset
	 * 
	 * @param labeledDataSet
	 *            The labeled dataset.
	 */
	public void setLabeledDataSet(IDataset labeledDataSet);

	/**
	 * Set the unlabeled dataset
	 * 
	 * @param unlabeledDataSet
	 *            The unlabeled dataset.
	 */
	public void setUnlabeledDataSet(IDataset unlabeledDataSet);

	/**
	 * Get the lalebed dataset
	 *
	 * @return The labeled dataset.
	 */
	public IDataset getLabeledDataSet();

	/**
	 * Get the unlabeled dataset
	 *
	 * @return The unlabeled dataset.
	 */
	public IDataset getUnlabeledDataSet();

	/**
	 * Get the scenario
	 *
	 * @return The scenario used.
	 */
	public IScenario getScenario();

	/**
	 * Set the scenario
	 * 
	 * @param scenario
	 *            The scenario used.
	 */
	public void setScenario(IScenario scenario);
}