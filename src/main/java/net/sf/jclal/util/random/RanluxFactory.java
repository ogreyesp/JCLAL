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

import org.apache.commons.configuration.Configuration;

import net.sf.jclal.core.IRandGen;

/**
 * Ranlux generators factory.
 *
 * @author Sebastian Ventura
 */
public class RanluxFactory extends AbstractRandGenFactory {
    /////////////////////////////////////////////////////////////////
    // --------------------------------------- Serialization constant
    /////////////////////////////////////////////////////////////////

    private static final long serialVersionUID = 9166246301476739303L;
    /////////////////////////////////////////////////////////////////
    // --------------------------------------------------- Properties
    /////////////////////////////////////////////////////////////////
    /**
     * Luxury level for all generators created (default = 3)
     */
    private int luxuryLevel = 3;

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------- Constructors
    /////////////////////////////////////////////////////////////////
    /**
     * Empty constructor.
     */
    public RanluxFactory() {
        super();
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------------- Getting and setting properties 
    /////////////////////////////////////////////////////////////////
    /**
     * Access to luxuryLevel property.
     *
     * @return Actual luxury level
     */
    public int getLuxuryLevel() {
        return luxuryLevel;
    }

    /**
     * Sets luxury level property.
     *
     * @param luxuryLevel New luxury level value
     */
    public void setLuxuryLevel(int luxuryLevel) {
        this.luxuryLevel = luxuryLevel;
    }

    /////////////////////////////////////////////////////////////////
    // ----------------------------------------- Configuration method 
    /////////////////////////////////////////////////////////////////
    /**
     *
     * @param configuration The configuration of RanluxFactory.
     * <p>
     * luxury-level= int</p>
     */
    @Override
    public void configure(Configuration configuration) {
        // Call super method
        super.configure(configuration);
        // Initialize luxury level
        int luxuryLevelN = configuration.getInt("[@luxury-level]", 3);
        setLuxuryLevel(luxuryLevelN);
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------- Implementing IRandGenFactory methods 
    /////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     *
     * @return The random generator.
     */
    @Override
    public IRandGen createRandGen() {
        return new Ranlux(luxuryLevel, seedGenerator.nextSeed());
    }
}
