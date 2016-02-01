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

/**
 * Root for the random hierarchy.
 *
 * @author Sebastian Ventura
 */
public interface IRandGen extends JCLAL {

    /**
     * Return a double value in the range [0,1]. This method will be defined to
     * make a working IRandGen.
     *
     * @return a random double in the range [0,1]
     */
    public double raw();

    /**
     * Fill part or all of an array with doubles.
     *
     * @param d array to be filled with doubles.
     * @param n number of doubles to generate.
     */
    public void raw(double d[], int n);

    /**
     * Fill an entire array with doubles.
     *
     * @param d array to be filled with doubles.
     */
    public void raw(double d[]);

    /**
     * Return an integer random value in the range [1, ...hi)
     *
     * @param hi upper limit of range.
     *
     * @return a random integer in the range.
     */
    public int choose(int hi);

    /**
     * Return an integer random value in the range [lo, ...hi)
     *
     * @param lo lower limit of range
     * @param hi upper limit of range
     *
     * @return a random integer in the range.
     */
    public int choose(int lo, int hi);

    /**
     * Return a boolean that's true 0.5 of the time. This method call is
     * equivalent to coin(0.5).
     *
     * @return a boolean that's true 0.5 of the time.
     */
    public boolean coin();

    /**
     * Return a boolean that's true p of the time.
     *
     * @param p probability that function will return true.
     *
     * @return a boolean that's true p of the time.
     */
    public boolean coin(double p);

    /**
     * Return a uniform random real in the range [lo, hi].
     *
     * @param lo lower limit of range.
     * @param hi upper limit of range.
     *
     * @return a uniform random real in the range.
     */
    public double uniform(double lo, double hi);

    /**
     * Uses the Box-Muller algorithm to transform raw's into gaussian deviates.
     *
     * @return a random real with a gaussian distribution and unitary standard
     * deviation.
     */
    public double gaussian();

    /**
     * Return a gaussian distributed random real value with standard deviation
     * "sd".
     *
     * @param sd standard deviation.
     *
     * @return a random real with gaussian distribution and standard deviation
     * sd.
     */
    public double gaussian(double sd);

    /**
     * Generate a "power-law distribution" with exponent "alpha" and lower
     * cutoff "cut".
     *
     * @param alpha the exponent.
     * @param cut the lower cutoff.
     * @return random
     */
    public double powlaw(double alpha, double cut);
}
