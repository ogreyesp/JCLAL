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

import net.sf.jclal.core.IConfigure;
import net.sf.jclal.core.IRandGenFactory;

import org.apache.commons.configuration.Configuration;

/**
 * IRandGenFactory abstract implementation...
 *
 * @author Sebastian Ventura
 */
@SuppressWarnings("serial")
public abstract class AbstractRandGenFactory implements IRandGenFactory, IConfigure {
    /////////////////////////////////////////////////////////////////
    // ------------------------------------------- Internal variables
    /////////////////////////////////////////////////////////////////

    /**
     * Seeds generator
     */
    protected SeedGenerator seedGenerator = new SeedGenerator();

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------- Constructors
    /////////////////////////////////////////////////////////////////
    /**
     * Empty (default) constructor
     */
    public AbstractRandGenFactory() {
        super();
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------------- Setting and getting properties
    /////////////////////////////////////////////////////////////////
    /**
     * Access to actual generators seed.
     *
     * @return Actual generators seed
     */
    public int getSeed() {
        return seedGenerator.getRow();
    }

    /**
     * Sets actual generators seed.
     *
     * @param seed New generators seed
     */
    public void setSeed(int seed) {
        seedGenerator.setRow(seed);
    }

    /////////////////////////////////////////////////////////////////
    // ---------------------------- Implementing IConfigure interface
    /////////////////////////////////////////////////////////////////
    /**
     * Configuration method.
     *
     * Configuration parameters for this object are...
     *
     * @param settings Configuration settings
     */
    @Override
    public void configure(Configuration settings) {
        // Getting seed parameter
        int seed = settings.getInt("[@seed]", 1234567890);
        // Setting seed
        setSeed(seed);
    }
}
