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
package net.sf.jclal.activelearning.scenario;

import java.util.ArrayList;
import java.util.List;
import net.sf.jclal.activelearning.querystrategy.AbstractQueryStrategy;
import net.sf.jclal.util.sort.Container;

/**
 * Implementation of Pool-Based Sampling scenario.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public class PoolBasedSamplingScenario extends AbstractScenario {

	private static final long serialVersionUID = 1L;
	/**
	 * To store the values of all instance according to the query strategy used
	 */
	private double values[];

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void instancesSelection() {

		// found values of all unlabeled instances
		values = getQueryStrategy().testUnlabeledData();

		int unlabeledSize = getQueryStrategy().getUnlabelledData().getNumInstances();

		List<Container> seleccion = new ArrayList<Container>(unlabeledSize);

		for (int i = 0; i < unlabeledSize; i++) {

			seleccion.add(new Container(values[i], i));

		}

		((AbstractQueryStrategy) getQueryStrategy()).getSelectedInstances()
				.addAll(getBatchMode().instancesSelection(getQueryStrategy(), seleccion));
	}

}