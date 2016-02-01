/*
 *
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
package net.sf.jclal.util.sort;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to store values in accordance with an index and a value, for the
 * specific case of that the elements change their position and it is necessary
 * to preserve their original position.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class IndexValueContainer {

	/**
	 * It stores a pair of values that represents the new index (key) of the
	 * current index of the array, and the old index (value) of an instance with
	 * the values stored in the array. It accelerates the process to obtain the
	 * similarity value between two instances.
	 */
	protected int[] indexesChanges;

	/**
	 * Keep the current valid number of elements.
	 */
	protected int size;

	/**
	 * To store the accumulative value.
	 */
	protected double[] acumulativeValue;

	/**
	 * It takes whether is necessary to determine the maximum accumulative value
	 */
	protected boolean updateMaxAcumulativeValue = true;

	/**
	 * The max accumulative value
	 */
	protected double maxAcumulativeValue;

	/**
	 * Delete the index of a instance, updates the remaining indexes
	 *
	 * @param pos
	 *            The index of the instance to delete
	 */
	public void deleteIndex(int pos) {

		if (!updateMaxAcumulativeValue) {
			updateMaxAcumulativeValue = true;
		}

		indexesChanges[pos] = -1;

		int temp;
		int next;

		while ((next = pos + 1) < size) {
			temp = indexesChanges[pos];
			indexesChanges[pos] = indexesChanges[next];
			indexesChanges[next] = temp;
			++pos;
		}
		--size;
	}

	/**
	 * Get the actual pos.
	 * 
	 * @param pos
	 *            Must be minor than the changeable size.
	 * @return the actual pos.
	 */
	public int index(int pos) {
		if (pos >= size) {
			try {
				throw new Exception("Value 'pos' out of range");
			} catch (Exception ex) {
				Logger.getLogger(IndexValueContainer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return indexesChanges[pos];
	}

	/**
	 * Set the accumulative value for an instance
	 *
	 * @param pos
	 *            Must be minor than the changeable size.
	 * @param value
	 *            The accumulative value
	 */
	public void setAcumulativeValue(int pos, double value) {
		acumulativeValue[index(pos)] = value;
	}

	/**
	 * Get the accumulative value.
	 * 
	 * @param pos
	 *            Must be minor than the changeable size.
	 * @return The accumulative value for an instance.
	 */
	public double getAcumulativeValue(int pos) {
		return acumulativeValue[index(pos)];
	}

	/**
	 * The elements are located in descendant order.
	 *
	 * @param removedIndexes
	 *            The indexes of the removed instances.
	 * @return An arraylist with the elements in order.
	 */
	protected ArrayList<Container> obtainOrdered(ArrayList<Integer> removedIndexes) {

		ArrayList<Container> ordered = new ArrayList<Container>();

		for (int index : removedIndexes) {
			ordered.add(new Container(index, index));
		}

		// To order the array of bigger to smaller
		OrderUtils.mergeSort(ordered, true);
		return ordered;
	}

	/**
	 * It stores a pair of values that represents the new index (key) with the
	 * current index of the array, and the old index (value) of an instance with
	 * the values stored in the array.
	 * 
	 * @return the indexes changes.
	 */
	public int[] getIndexesChanges() {
		return indexesChanges;
	}

	/**
	 * It stores a pair of values that represents the new index (key) with the
	 * current index of the array, and the old index (value) of an instance with
	 * the values stored in the array.
	 * 
	 * @param indexesChanges
	 *            The indexes that were changed.
	 */
	public void setIndexesChanges(int[] indexesChanges) {
		this.indexesChanges = indexesChanges;
		size = indexesChanges.length;
		for (int i : indexesChanges) {
			if (i < 0) {
				--size;
			}
		}
	}

	/**
	 * Get the size.
	 *
	 * @return The current size. The size changes from calling to the method
	 *         deleteIndex(int index).
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns whether it is necessary to determine the maximum accumulative
	 * value
	 * 
	 * @return The flag of max acumulative value.
	 */
	public boolean isUpdateMaxAcumulativeValue() {
		return updateMaxAcumulativeValue;
	}

	/**
	 * Set whether it is necessary to determine the maximum accumulative value
	 * 
	 * @param updateMaxAcumulativeValue
	 *            Flag that indicates whether the maximum accumulative vale is
	 *            updated
	 */
	public void setUpdateMaxAcumulativeValue(boolean updateMaxAcumulativeValue) {
		this.updateMaxAcumulativeValue = updateMaxAcumulativeValue;
	}

	/**
	 * Obtain the maximum stored value
	 *
	 * @return The max accumulative value.
	 */
	public double getMaxAcumulativeValue() {
		if (updateMaxAcumulativeValue) {
			updateMaxAcumulativeValue = false;
			int pos = OrderUtils.maxIndex(acumulativeValue, 0, size);
			maxAcumulativeValue = acumulativeValue[pos];
		}
		return maxAcumulativeValue;
	}

	/**
	 * Set the maximum stored value
	 *
	 * @param maxAcumulativeValue
	 *            The accumulative value
	 */
	public void setMaxAcumulativeValue(double maxAcumulativeValue) {
		this.maxAcumulativeValue = maxAcumulativeValue;
	}

}
