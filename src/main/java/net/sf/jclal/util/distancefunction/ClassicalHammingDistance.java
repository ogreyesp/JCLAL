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

/**
 * Classical lHamming Distance.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ClassicalHammingDistance extends AbstractHammingDistance {

    private static final long serialVersionUID = -6007873411169318488L;

    /**
     * Empty(default) constructor.
     */
    public ClassicalHammingDistance() {

        super();
    }

    /**
     * Compute the distance among i1 and i2
     *
     * @param i1 The array of booleans
     * @param i2 The array of booleans
     * @return The distance among the vectors
     */
    @Override
    public double distance(boolean[] i1, boolean[] i2) {

        contingencyTable(i1, i2);

        return (b + c) / (double) (numLabels);

    }
}
