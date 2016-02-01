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
 * <p>RandLux is an advanced pseudo-random number generator based on the RCARRY
 * algorithm proposed in 1991 by Marsaglia and Zaman.</p>
 *
 * <p>RCARRY used a subtract-and-borrow algorithm with a period on the order of
 * 10<SUP>171</SUP> but still had detectable correlations between numbers.
 * Martin Luescher proposed the "RANLUX" algorithm in 1993; this algorithm
 * generates pseudo-random numbers using RCARRY, but throws away numbers to
 * destroy correlations. Thus RANLUX trades execution speed for quality.</p>
 *
 * <p>Choosing a larger luxury setting one gets better random numbers slower. By
 * the tests available at the time it was proposed, RANLUX at default luxury
 * setting appears to be a significant advance quality over previous generators.</p>
 *
 */
public class Ranlux extends AbstractRandGen {
    ///////////////////////////////////////////////////////////////////
    // ----------------------------------------- Serialization constant
    ///////////////////////////////////////////////////////////////////

    private static final long serialVersionUID = 7707398773259479291L;

    ///////////////////////////////////////////////////////////////////
    // ------------------------------------------------------ Constants
    ///////////////////////////////////////////////////////////////////
    static final int GIGA = 1000000000;
    static final int TWOP12 = 4096;
    static final int ITWO24 = 1 << 24;
    static final int ICONS = 2147483563;
    static final int[] ND_SKIP = {0, 24, 73, 199, 365};
    static final int[] NEXT
            = {
                0, 24, 1, 2, 3,
                4, 5, 6, 7, 8,
                9, 10, 11, 12, 13,
                14, 15, 16, 17, 18,
                19, 20, 21, 22, 23
            };

    ///////////////////////////////////////////////////////////////////
    // ----------------------------------------------------- Atributtes
    ///////////////////////////////////////////////////////////////////
    /**
     * Fixed in initialization method
     */
    private int nskip;

    /**
     * Fixed in initialization method
     */
    private float twom24;

    /**
     * Fixed in initialization method
     */
    private float twom12;

    private int i24;
    private int j24;
    private int in24;
    private int kount;
    private float carry;
    private float[] seeds;

    ///////////////////////////////////////////////////////////////////
    // --------------------------------------------------- Constructors
    ///////////////////////////////////////////////////////////////////
    /**
     * Empty constructor
     */
    protected Ranlux() {
        super();
    }

    /**
     * Default constructor. Used by the RandGen factory.
     *
     * @param luxlev Luxury level
     * @param seed First seed
     */
    public Ranlux(int luxlev, int seed) {
        super();
        rluxgo(luxlev, seed);
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
        int i;
        float uni, out;

        uni = seeds[j24] - seeds[i24] - carry;
        if (uni < (float) 0.0) {
            uni = uni + (float) 1.0;
            carry = twom24;
        } else {
            carry = (float) 0.0;
        }

        seeds[i24] = uni;

        i24 = NEXT[i24];
        j24 = NEXT[j24];

        out = uni;

        if (uni < twom12) {
            out += twom24 * seeds[j24];
        }

        /* zero is forbidden in case user wants logarithms */
        if (out == 0.0) {
            out = twom24 * twom24;
        }

        in24++;

        if (in24 == 24) {
            in24 = 0;
            kount += nskip;
            for (i = 1; i <= nskip; i++) {
                uni = seeds[j24] - seeds[i24] - carry;
                if (uni < (float) 0.0) {
                    uni = uni + (float) 1.0;
                    carry = twom24;
                } else {
                    carry = (float) 0.0;
                }

                seeds[i24] = uni;

                i24 = NEXT[i24];
                j24 = NEXT[j24];
            }
        }

        kount++;
        if (kount >= GIGA) {
            kount -= GIGA;
        }
        return out;
    }

    ////////////////////////////////////////////////////////////////////
    // ------------------------------ Overwrite java.lang.Object methods
    ////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object other) {
        if (other instanceof Ranlux) {
            Ranlux o = (Ranlux) other;
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(this.nskip, o.nskip);
            eb.append(this.twom24, o.twom24);
            eb.append(this.twom12, o.twom12);
            eb.append(this.i24, o.i24);
            eb.append(this.j24, o.j24);
            eb.append(this.in24, o.in24);
            eb.append(this.kount, o.kount);
            eb.append(this.carry, o.carry);
            eb.append(this.seeds, o.seeds);
            return eb.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.nskip;
        hash = 53 * hash + Float.floatToIntBits(this.twom24);
        hash = 53 * hash + Float.floatToIntBits(this.twom12);
        hash = 53 * hash + this.i24;
        hash = 53 * hash + this.j24;
        hash = 53 * hash + this.in24;
        hash = 53 * hash + this.kount;
        hash = 53 * hash + Float.floatToIntBits(this.carry);
        hash = 53 * hash + Arrays.hashCode(this.seeds);
        return hash;
    }

    /////////////////////////////////////////////////////////////////
    // ---------------------------------------------- Private methods
    /////////////////////////////////////////////////////////////////
    /**
     * Initialization method.
     *
     * @param lux Luxury level
     * @param ins Seeds generator
     */
    private final void rluxgo(int lux, int ins) {
        // Check luxury level
        if (lux < 0 || lux > 4) {
            throw new IllegalArgumentException("Luxury level must be an integer in the range [0,4]");
        }
        // Init luxury level
        nskip = ND_SKIP[lux];
        // Init in24
        in24 = 0;
        // Init seeds		
        if (ins <= 0) {
            throw new IllegalArgumentException("ins must be a positive integer");
        }
        // Init twom24
        twom24 = (float) 1.0;
        // Prepare seeds initialization
        int[] iseeds = new int[24 + 1];
        int aux = ins;
        for (int i = 1; i <= 24; i++) {
            twom24 = twom24 * (float) 0.5;
            int k = aux / 53668;
            aux = 40014 * (aux - k * 53668) - k * 12211;
            if (aux < 0) {
                aux = aux + ICONS;
            }
            iseeds[i] = aux % ITWO24;
        };
        // Init twom12
        twom12 = twom24 * 4096;
        // Init seeds
        seeds = new float[24 + 1];
        for (int i = 1; i <= 24; i++) {
            seeds[i] = iseeds[i] * twom24;
        };
        // Init i24
        i24 = 24;
        // Init j24
        j24 = 10;
        // Init carry
        carry = (float) 0.0;
        if (seeds[24] == 0.0) {
            carry = twom24;
        }
        // Init kount
        kount = 0;
    }
}
