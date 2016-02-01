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
import net.sf.jclal.util.sort.Container;

/**
 * Interface for batch mode active learning. It selects Q instances from the
 * total of instances. It is used internally by a scenario.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public interface IBatchMode extends JCLAL {

    /**
     * Select q instances taking into account their utility and the query
     * strategy
     *
     * @param queryStrategy The query strategy used
     * @param intanceUtilities Array that stores the utility of each unlabeled
     * instance
     * @return A list that contains the indexes of the selected instances
     */
    public List<Integer> instancesSelection(IQueryStrategy queryStrategy, List<Container> intanceUtilities);

    /**
     * String representation.
     *
     * @return The string of the object.
     */
    @Override
    public String toString();

}
