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

import java.io.Serializable;

/**
 * Abstract class for Hamming Distance.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractHammingDistance implements Serializable {

	private static final long serialVersionUID = -1315600488926739311L;

	/**
	 * Variable a.
	 */
	protected double a;

	/**
	 * Variable b.
	 */
	protected double b;

	/**
	 * Variable c.
	 */
	protected double c;

	/**
	 * Variable d.
	 */
	protected double d;

	/**
	 * Number of labels.
	 */
	protected int numLabels;

	/**
	 * Empty(default) constructor.
	 */
	public AbstractHammingDistance() {
	}

	/**
	 * Compute the distance
	 * 
	 * @param i1
	 *            Instance i1.
	 * @param i2
	 *            Instance i2.
	 * @return The Hamming distance.
	 */
	public abstract double distance(boolean[] i1, boolean[] i2);

	/**
	 * 
	 * Compute the contingency table
	 * 
	 * @param i1
	 *            Instance i1.
	 * @param i2
	 *            Instance i2.
	 */
	public void contingencyTable(boolean[] i1, boolean[] i2) {

		boolean c1;
		boolean c2;

		// set default values
		a = b = c = d = 0;

		numLabels = i1.length;

		for (int l = 0; l < numLabels; l++) {

			c1 = i1[l];

			c2 = i2[l];

			// a case
			if (c1 == c2 && c1) {
				++a;
			} // d case
			else if (c1 == c2 && !c1) {
				++d;
			} // b case
			else if (c1 != c2 && c1) {
				++b;
			} // c case
			else if (c1 != c2 && !c1) {
				++c;
			}
		}

	}

}
