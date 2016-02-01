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

import java.util.Arrays;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Mersenne-Twister random generator.
 *
 * @author Sebastian Ventura
 */
public class Ranmt extends AbstractRandGen {

    private static final long serialVersionUID = 7341251121666067202L;
    /////////////////////////////////////////////////////////////////
    // ---------------------------------------------------- Constants
    /////////////////////////////////////////////////////////////////
    private static final int N = 624;
    private static final int M = 397;
    // private static final * constant vector a
    private static final int MATRIX_A = 0x9908b0df;
    // most significant w-r bits
    private static final int UPPER_MASK = 0x80000000;
    // least significant r bits
    private static final int LOWER_MASK = 0x7fffffff;
    // Tempering parameters
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;
    private static final int[] mag01 = new int[]{0x0, MATRIX_A};
    /////////////////////////////////////////////////////////////////
    // --------------------------------------------------- Attributes
    /////////////////////////////////////////////////////////////////
    /**
     * mti==N+1 means mt[N] is not initialized
     */
    private int mti;
    /**
     * the array for the state vector
     */
    private int mt[];

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------- Constructors
    /////////////////////////////////////////////////////////////////
    /**
     * Empty constructor.
     */
    protected Ranmt() {
        super();
    }

    /**
     * Default constructor.
     *
     * @param seed The seed to use
     */
    public Ranmt(int seed) {
        // Call super constructor
        super();
        // Initialize random generator
        setup(seed);
    }

    ////////////////////////////////////////////////////////////////////
    // ------------------------------ Overwrite java.lang.Object methods
    ////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object other) {
        if (other instanceof Ranmt) {
            Ranmt o = (Ranmt) other;
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(this.mti, o.mti);
            eb.append(this.mt, o.mt);
            return eb.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.mti;
        hash = 41 * hash + Arrays.hashCode(this.mt);
        return hash;
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------------ Implementing IRandGen interface
    /////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     *
     * @return The number generated.
     */
    @Override
    public final double raw() {
        int y, z;

        if (mti >= N) { // generate N words at one time

            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

        if (mti >= N) { // generate N words at one time
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (z >>> 1) ^ mag01[z & 0x1];
            }
            for (; kk < N - 1; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (z >>> 1) ^ mag01[z & 0x1];
            }
            z = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (z >>> 1) ^ mag01[z & 0x1];

            mti = 0;
        }

        z = mt[mti++];
        z ^= z >>> 11; // TEMPERING_SHIFT_U(z)
        z ^= (z << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(z)
        z ^= (z << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(z)
        z ^= (z >>> 18); // TEMPERING_SHIFT_L(z)

        /* derived from nextDouble documentation in jdk 1.2 docs, see top */
        return ((((long) (y >>> 6)) << 27) + (z >>> 5)) / (double) (1L << 53);
    }

    /////////////////////////////////////////////////////////////////
    // -------------------------------------- Private (setup) methods
    /////////////////////////////////////////////////////////////////
    /**
     * Inicializa el generador utilizando como semilla el valor que se le pasa
     * como argumento.
     *
     * @param seed Semilla que se pasa como argumento al generador.
     */
    private void setup(final long seed) {
        mt = new int[N];
        mt[0] = (int) (seed & 0xfffffff);
        for (mti = 1; mti < N; mti++) {
            mt[mti] = (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
            // See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier.
            // In the previous versions, MSBs of the seed affect
            // only MSBs of the array mt[].
            // 2002/01/09 modified by Makoto Matsumoto
            mt[mti] &= 0xffffffff;
            //for >32 bit machines
        }
    }
}
