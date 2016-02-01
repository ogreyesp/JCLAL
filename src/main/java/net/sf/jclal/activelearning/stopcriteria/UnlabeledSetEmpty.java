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

import java.io.Serializable;

import net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm;
import net.sf.jclal.activelearning.querystrategy.AbstractQueryStrategy;
import net.sf.jclal.core.IAlgorithm;
import net.sf.jclal.core.IStopCriterion;

/**
 * Class that represents a stop criterion related with whether the unlabeled set
 * is empty or not.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */

public class UnlabeledSetEmpty implements IStopCriterion, Serializable {

	private static final long serialVersionUID = 826780526319932955L;

	@Override
	public boolean stop(IAlgorithm algorithm) {

		if (((AbstractQueryStrategy) ((ClassicalALAlgorithm) algorithm).getScenario().getQueryStrategy())
				.getUnlabelledData().isEmpty()) {
			return true;
		}

		return false;
	}
}