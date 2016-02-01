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
package net.sf.jclal.util.random;

import java.util.Random;

/**
 * Java random class.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class JavaRandom extends AbstractRandGen {

	private static final long serialVersionUID = -2523842697100753877L;

	private Random random;

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            The seed to use
	 */
	public JavaRandom(int seed) {
		super();

		this.random = new Random(seed);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double raw() {
		return random.nextDouble();
	}

	/**
	 * Get the random number generator
	 * 
	 * @return The random class.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Set the random number generator
	 * 
	 * @param random
	 *            The random class.
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

}
