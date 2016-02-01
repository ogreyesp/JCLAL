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

package net.sf.jclal.util.distancefunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.jclal.util.sort.Container;
import net.sf.jclal.util.sort.OrderUtils;
import weka.core.Instances;
import weka.core.NormalizableDistance;

/**
 * This class extends DistanceContainer class and allows to store the distance
 * between a particular instance and its k-nearest neighbors.
 * 
 * @author Oscar Gabriel Reyes Pupo
 *
 */

public class KNearestDistanceContainer extends DistanceContainer {

	private double[] accumulativeDistanceKNearest;
	private Set<Integer>[] kNearest;

	private int k;

	/**
	 * Constructor
	 * 
	 * @param instances
	 *            The instances
	 * @param distanceFunction
	 *            The distance function to be used
	 * @param k
	 *            The number of k nearest neighbors
	 */
	public KNearestDistanceContainer(Instances instances, NormalizableDistance distanceFunction, int k) {

		super(instances, distanceFunction);

		accumulativeDistanceKNearest = new double[size];
		kNearest = new HashSet[size];
		this.k = k;

		// Compute the k-nearest neighbors of each instance

		for (int i = 0; i < size; i++) {

			ArrayList<Container> array = new ArrayList<Container>(k);
			kNearest[i] = new HashSet<Integer>();

			for (int j = 0; j < size; j++) {

				if (i != j)
					array.add(new Container<Integer>(getDistance(i, j), j));

			}

			OrderUtils.mergeSort(array, false);

			// Fill the distances of the k-nearest neighbors of i

			for (int pos = 0; pos < k; pos++) {
				accumulativeDistanceKNearest[i] += array.get(pos).getKey();
				kNearest[i].add(Integer.parseInt(array.get(pos).getValue().toString()));
			}
		}
	}

	/**
	 * Constructor
	 * 
	 * @param instances
	 *            The instances
	 * @param distanceFunction
	 *            The distance function to be used
	 * @param matrixOverFile
	 *            It indicates whether the matrix is stored over a file or the
	 *            main memory
	 * @throws Exception
	 *             Launch an exception in case that an error occurs.
	 */
	public KNearestDistanceContainer(Instances instances, NormalizableDistance distanceFunction, boolean matrixOverFile)
			throws Exception {

		super(instances, distanceFunction, matrixOverFile);

	}

	/**
	 * {@inheritDoc}
	 */
	public void updateIndexes(ArrayList<Integer> removedIndexes) {

		for (int index : removedIndexes) {

			int originalIndex = index(index);

			kNearest[originalIndex] = null;

			for (int u = 0; u < size; u++) {

				int originalU = index(u);

				if (kNearest[originalU] != null) {
					if (kNearest[originalU].contains(originalIndex)) {
						kNearest[originalU].remove(originalIndex);
						accumulativeDistanceKNearest[originalU] -= getDistance(u, index);
					}
				}
			}
		}

		super.updateIndexes(removedIndexes);
	}

	/**
	 * Get the distance between the given instance (index) and its k nearest
	 * neighbors
	 * 
	 * @param index
	 *            The index of the instance
	 * @return The distance between the given instance and its k nearest
	 *         neighbors
	 */
	public double getAccumulativeDistanceKNearest(int index) {

		int originalIndex = index(index);
		return accumulativeDistanceKNearest[originalIndex] / k;
	}
}
