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

import net.sf.jclal.core.IRandGen;

/**
 * Base implementation of the IRandGen interface.
 *
 * @author Sebastian Ventura
 */
@SuppressWarnings("serial")
public abstract class AbstractRandGen implements IRandGen {
    /////////////////////////////////////////////////////////////////
    // ------------------------------------------- Internal variables
    /////////////////////////////////////////////////////////////////

    /**
     * Constant used in the Box-Mueller algorithm.
     */
    private Double bMoutput;

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------- Constructors
    /////////////////////////////////////////////////////////////////
    /**
     * Empty constructor.
     */
    protected AbstractRandGen() {
        super();
    }

    /////////////////////////////////////////////////////////////////
    // ----------------------------------------------- Public methods
    /////////////////////////////////////////////////////////////////
    // IRandGen interface
    /**
     * {@inheritDoc}
     */
    public void raw(double[] d, int n) {
        for (int i = 0; i < n; i++) {
            d[i] = raw();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void raw(double[] d) {
        raw(d, d.length);
    }

    /**
     * {@inheritDoc}
     */
    public final int choose(int hi) {
        return choose(1, hi);
    }

    /**
     * {@inheritDoc}
     */
    public int choose(int lo, int hi) {
        // C�lculo del valor
        int value = lo + (int) ((hi - lo) * raw());
        // Mecanismo para asegurar que un generador que produce valores en
        // el rango [0,1] no devuelva  un valor fuera del  rango que se ha
        // especificado. Si  estamos seguros de  que el generador  produce
        // valores en los rangos [0,1) or (0,1) podriamos sobreescribir el
        // metodo
        if (value > hi) {
            value = hi;
        }
        // Devuelve el resultado
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean coin() {
        return raw() <= 0.5;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean coin(double p) {
        return raw() <= p;
    }

    /**
     * {@inheritDoc}
     */
    public final double uniform(double lo, double hi) {
        return (lo + (hi - lo) * raw());
    }

    /**
     * {@inheritDoc}
     */
    public final double gaussian() {
        double out, x, y, r, z;

        if (bMoutput != null) {
            out = bMoutput.doubleValue();
            bMoutput = null;
            return (out);
        }
        do {
            x = uniform(-1, 1);
            y = uniform(-1, 1);
            r = x * x + y * y;
        } while (r >= 1.0);
        z = Math.sqrt(-2.0 * Math.log(r) / r);
        bMoutput = new Double(x * z);
        return (y * z);
    }

    /**
     * {@inheritDoc}
     */
    public final double gaussian(double sd) {
        return (gaussian() * sd);
    }

    /**
     * {@inheritDoc}
     *
     */
    public final double powlaw(double alpha, double cut) {
        return cut * Math.pow(raw(), 1.0 / (alpha + 1.0));
    }
}
