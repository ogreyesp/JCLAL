/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jclal.activelearning.oracle;

import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IOracle;

import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;

/**
 * Abstract class that represents an Oracle
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public abstract class AbstractOracle implements IOracle, IConfigure {

	private static final long serialVersionUID = -56938218241429612L;

	/**
	 * Keep the last labeled instances.
	 */
	protected ArrayList<String> lastLabeledInstances;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(Configuration settings) {
		// do nothing
	}

	/**
	 * Get the last queried instances.
	 * 
	 * @return The last queried instances
	 */
	public ArrayList<String> getLastLabeledInstances() {
		return lastLabeledInstances;
	}

	/**
	 * Set the last queried instances.
	 * 
	 * @param lastInstances The last labeled instances 
	 */
	public void setLastLabeledInstances(ArrayList<String> lastInstances) {
		this.lastLabeledInstances = lastInstances;
	}
}