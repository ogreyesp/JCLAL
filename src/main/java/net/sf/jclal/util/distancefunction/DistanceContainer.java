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
import net.sf.jclal.util.matrixFile.Matrix;
import net.sf.jclal.util.sort.Container;
import net.sf.jclal.util.sort.IndexValueContainer;
import weka.core.Instances;
import weka.core.NormalizableDistance;

/**
 * Implementation of DistanceContainer.
 *
 * Class to store the distance between a set of instances
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public class DistanceContainer extends IndexValueContainer {

	/**
	 * distance matrix
	 */
	private double[][] distance;
	/**
	 * distance matrix stored over file
	 */
	private Matrix distanceMatrix;

	/**
	 * Number of attributes
	 */
	private int numAttributes;

	private double maxDistance;

	private double minDistance;

	/**
	 * It stores whether the matrix used is stored over a file or the main
	 * memory
	 */
	private boolean matrixOverFile = false;

	/**
	 * Get the distance matrix
	 * 
	 * @return The distance matrix
	 */
	public double[][] getDistance() {
		return distance;
	}

	/**
	 * Set the distance matrix
	 * 
	 * @param distance
	 *            the distance matrix
	 */
	public void setDistance(double[][] distance) {
		this.distance = distance;
	}

	/**
	 * Get the number of attributes
	 * 
	 * @return get the number of attributes
	 */
	public int getNumAttributes() {
		return numAttributes;
	}

	/**
	 * Set the number of attributes
	 * 
	 * @param numAttributes
	 *            Number of attributes
	 */
	public void setNumAttributes(int numAttributes) {
		this.numAttributes = numAttributes;
	}

	/**
	 * Default constructor
	 *
	 * @param instances
	 *            The set of instances
	 * @param distanceFunction
	 *            The distance function used to calculate the distance among two
	 *            instances
	 */
	public DistanceContainer(Instances instances, NormalizableDistance distanceFunction) {

		size = instances.numInstances();

		indexesChanges = new int[size];

		acumulativeValue = new double[size];

		numAttributes = instances.numAttributes();

		int m = size - 1;

		distance = new double[m][];

		int temp;

		double valueTemp;

		maxDistance = Double.MIN_VALUE;
		minDistance = Double.MAX_VALUE;

		for (int i = 0; i < m; ++i) {

			distance[i] = new double[size - i - 1];

			// In the begining the index and the value are equals
			indexesChanges[i] = i;

			for (int j = i + 1; j < size; ++j) {
				temp = j - i - 1;

				valueTemp = distanceFunction.distance(instances.instance(i), instances.instance(j));

				if (valueTemp > maxDistance)
					maxDistance = valueTemp;

				if (valueTemp < minDistance)
					minDistance = valueTemp;

				setStoreDistance(i, temp, valueTemp);

				// accumulative distance
				// acumulativeValue[i] += valueTemp;
				// acumulativeValue[j] += valueTemp;

			}
		}

		indexesChanges[size - 1] = size - 1;

		scaleMinMax();
	}

	/**
	 * Constructor by default
	 *
	 * @param instances
	 *            dataset
	 * @param distanceFunction
	 *            The distance function used to calculate the distance
	 * @param matrixOverFile
	 *            Whether the matrix will be stored into a file
	 * @throws java.lang.Exception
	 *             The exception that will be launched
	 */
	public DistanceContainer(Instances instances, NormalizableDistance distanceFunction, boolean matrixOverFile)
			throws Exception {

		this.matrixOverFile = matrixOverFile;

		size = instances.numInstances();

		indexesChanges = new int[size];

		acumulativeValue = new double[size];

		numAttributes = instances.numAttributes();

		int m = size - 1;

		if (matrixOverFile) {
			distanceMatrix = new Matrix(size, size, true);
		} else {
			distance = new double[m][];
		}

		maxDistance = Double.MIN_VALUE;
		minDistance = Double.MAX_VALUE;

		int temp;
		double valueTemp;
		for (int i = 0; i < m; ++i) {

			if (!matrixOverFile) {
				distance[i] = new double[size - i - 1];
			}

			// In the begining the index and the value are equals
			indexesChanges[i] = i;

			for (int j = i + 1; j < size; ++j) {

				temp = j - i - 1;

				valueTemp = distanceFunction.distance(instances.instance(i), instances.instance(j));

				if (valueTemp > maxDistance)
					maxDistance = valueTemp;

				if (valueTemp < minDistance)
					minDistance = valueTemp;

				setStoreDistance(i, temp, valueTemp);

				// acumulative distance
				// acumulativeValue[i] += valueTemp;
				// acumulativeValue[j] += valueTemp;
			}
		}

		indexesChanges[size - 1] = size - 1;

		scaleMinMax();
	}

	// To scale all distances to [0,1] range
	private void scaleMinMax() {

		int m = size - 1;
		double range = maxDistance - minDistance;

		for (int i = 0; i < m; ++i) {

			for (int j = i + 1; j < size; ++j) {

				int temp = j - i - 1;

				double newDistance = (getStoreDistance(i, temp) - minDistance) / range;

				setStoreDistance(i, temp, newDistance);

				// accumulative distance
				acumulativeValue[i] += newDistance;
				acumulativeValue[j] += newDistance;

			}
		}
	}

	/**
	 * Set the distance value into row and column
	 *
	 * @param r
	 *            row
	 * @param c
	 *            column
	 * @param value
	 *            distance value
	 */
	protected void setStoreDistance(int r, int c, double value) {
		if (matrixOverFile) {
			distanceMatrix.set(r, c, value);
		} else {
			distance[r][c] = value;
		}
	}

	/**
	 * Get the distance value stored into row and column
	 *
	 * @param r
	 *            row
	 * @param c
	 *            column
	 * @return the distance value
	 */
	protected double getStoreDistance(int r, int c) {
		if (matrixOverFile) {
			return distanceMatrix.get(r, c);
		} else {
			return distance[r][c];
		}
	}

	/**
	 * Returns the value of the distance between x and y
	 *
	 * @param indexX
	 *            The index of the instance x.
	 * @param indexY
	 *            The index of the instance y.
	 * @return Distance value among x and y.
	 */
	public double getDistance(int indexX, int indexY) {

		int posX = indexesChanges[indexX];

		int posY = indexesChanges[indexY];

		if (posX == posY) {
			return 0;
		}

		int r = Math.min(posX, posY);
		int c = Math.max(posX, posY);

		double similarityValue = getStoreDistance(r, c - r - 1);

		return similarityValue;
	}

	/**
	 * Update the indexes. It is used to accelerate the process.
	 *
	 * @param removedIndexes
	 *            Array that contains the indexes that will be removed
	 */
	public void updateIndexes(ArrayList<Integer> removedIndexes) {

		ArrayList<Container> ordered = obtainOrdered(removedIndexes);

		for (Container indexToRemove : ordered) {

			updateIndex(Integer.parseInt(indexToRemove.getValue().toString()));

		}

	}

	/**
	 * Updates the accumulative value of the index
	 *
	 * @param index
	 *            The index of the instance
	 */
	public void updateIndex(int index) {

		for (int i = 0; i < index; i++) {
			setAcumulativeValue(i, getAcumulativeValue(i) - getDistance(index, i));
		}

		for (int i = index + 1; i < size; i++) {
			setAcumulativeValue(i, getAcumulativeValue(i) - getDistance(index, i));
		}

		// delete index
		deleteIndex(index);
	}

	/**
	 * Free the memory
	 */
	public void destroy() {
		if (distanceMatrix != null) {
			distanceMatrix.destroy();
		}

		acumulativeValue = null;
		indexesChanges = null;
		distance = null;
		distanceMatrix = null;
	}
}
