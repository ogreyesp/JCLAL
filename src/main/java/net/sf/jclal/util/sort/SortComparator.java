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
package net.sf.jclal.util.sort;

import java.util.Comparator;

/**
 * Implementation of SortComparator to sort an array.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Pérez Perdomo
 *
 */
public class SortComparator implements Comparator<Container> {

	/**
	 * Flags to determine if the array must be ordered in descendant or
	 * ascendent order.
	 */
	private boolean maxMin = false;

	/**
	 *
	 * @param maxMin
	 *            It determines how the selection should be ordered.
	 *
	 *            If maxMin = true The selection was ordered of bigger to
	 *            smaller.
	 *
	 *            If maxMin = false The selection was ordered of smaller to
	 *            bigger.
	 *
	 */
	public SortComparator(boolean maxMin) {
		this.maxMin = maxMin;
	}

	/**
	 * It determines how the selection should be ordered.
	 *
	 * If maxMin = false The selection was ordered of smaller to bigger.
	 *
	 */
	public SortComparator() {
	}

	/**
	 * Compare according with the keys in the order specified by maxMin
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return The result of the comparison
	 */
	@Override
	public int compare(Container o1, Container o2) {
		if (o1.getKey() == o2.getKey()) {
			return 0;
		}
		// if is ordered in descendent order
		if (maxMin) {
			if (o1.getKey() < o2.getKey()) {
				return 1;
			}
			if (o1.getKey() > o2.getKey()) {
				return -1;
			}
		}

		// if is ordered in ascendent order
		if (o1.getKey() > o2.getKey()) {
			return 1;
		}

		// o1.value < o2.value
		return -1;
	}

	/**
	 *
	 * @return How the selection is ordered.
	 *
	 *         If maxMin = true The selection was ordered of bigger to smaller.
	 *
	 *         If maxMin = false The selection was ordered of smaller to bigger.
	 *
	 */
	public boolean isMaxMin() {
		return maxMin;
	}

	/**
	 *
	 * @param maxMin
	 *            It determines how the selection should be ordered.
	 *
	 *            If maxMin = true The selection was ordered of bigger to
	 *            smaller.
	 *
	 *            If maxMin = false The selection was ordered of smaller to
	 *            bigger.
	 */
	public void setMaxMin(boolean maxMin) {
		this.maxMin = maxMin;
	}
}
