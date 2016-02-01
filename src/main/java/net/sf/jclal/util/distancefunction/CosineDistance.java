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

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.Utils;
import weka.core.neighboursearch.PerformanceStats;

/**
 * Cosine Distance.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class CosineDistance extends NormalizableDistance {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an Cosine Distance object, Instances must be still set.
     */
    public CosineDistance() {
        super();
    }

    /**
     * Constructs an Cosine Distance object and automatically initializes the
     * ranges.
     *
     * @param data the instances the distance function should work on
     */
    public CosineDistance(Instances data) {
        super(data);
    }

    /**
     * Calculates the distance between two instances.
     *
     * @param first the first instance
     * @param second the second instance
     * @return the distance between the two given instances
     */
    @Override
    public double distance(Instance first, Instance second) {
        return 1 - distance(first, second, Double.POSITIVE_INFINITY, null);
    }

    /**
     *  Get the revision of the class
     *
     * @return The revision
     */
    @Override
    public String getRevision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Calculates the distance between two instances. Offers speed up (if the
     * distance function class in use supports it) in nearest neighbor search
     * by taking into account the cutOff or maximum distance. Depending on the
     * distance function class, post processing of the distances by
     * postProcessDistances(double []) may be required if this function is used.
     *
     * @param first the first instance
     * @param second the second instance
     * @param cutOffValue If the distance being calculated becomes larger than
     * cutOffValue then the rest of the calculation is discarded.
     * @param stats the performance stats object
     * @return the distance between the two given instances or
     * Double.POSITIVE_INFINITY if the distance being calculated becomes larger
     * than cutOffValue.
     */
    @Override
    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
        double distance = 0;
        int firstI, secondI;
        int firstNumValues = first.numValues();
        int secondNumValues = second.numValues();
        int numAttributes = m_Data.numAttributes();
        int classIndex = m_Data.classIndex();

        double norm2First = 0, norm2Second = 0;

        validate();

        for (int p1 = 0, p2 = 0; p1 < firstNumValues || p2 < secondNumValues;) {
            if (p1 >= firstNumValues) {
                firstI = numAttributes;
            } else {
                firstI = first.index(p1);
            }

            if (p2 >= secondNumValues) {
                secondI = numAttributes;
            } else {
                secondI = second.index(p2);
            }

            if (firstI == classIndex) {
                p1++;
                continue;
            }
            if ((firstI < numAttributes) && !m_ActiveIndices[firstI]) {
                p1++;
                continue;
            }

            if (secondI == classIndex) {
                p2++;
                continue;
            }
            if ((secondI < numAttributes) && !m_ActiveIndices[secondI]) {
                p2++;
                continue;
            }

            double diff;

            if (firstI == secondI) {
                diff = localSimilarity(firstI,
                        first.valueSparse(p1),
                        second.valueSparse(p2));

                norm2First += localSimilarity(firstI, first.valueSparse(p1), first.valueSparse(p1));
                norm2Second += localSimilarity(secondI, second.valueSparse(p2), second.valueSparse(p2));

                p1++;
                p2++;
            } else if (firstI > secondI) {

                diff = localSimilarity(secondI,
                        0, second.valueSparse(p2));

                norm2Second += localSimilarity(secondI, second.valueSparse(p2), second.valueSparse(p2));

                p2++;
            } else {

                diff = localSimilarity(firstI,
                        first.valueSparse(p1), 0);

                norm2First += localSimilarity(firstI, first.valueSparse(p1), first.valueSparse(p1));

                p1++;
            }
            if (stats != null) {
                stats.incrCoordCount();
            }

            distance = updateDistance(distance, diff);
            if (distance > cutOffValue) {
                return Double.POSITIVE_INFINITY;
            }
        }

        return distance / (Math.sqrt(norm2First * norm2Second));
    }

    /**
     * Computes the similarity between two given attribute values.
     *
     * @param index	the attribute index
     * @param val1	the first value
     * @param val2	the second value
     * @return	the similarity
     */
    protected double localSimilarity(int index, double val1, double val2) {

        switch (m_Data.attribute(index).type()) {

            case Attribute.NOMINAL:

                if (Utils.isMissingValue(val1)
                        || Utils.isMissingValue(val2)
                        || ((int) val1 != (int) val2)) {
                return 0;
            } else {

                if (m_Data.attribute(index).numValues() == 2) {

                    //binary case
                    if ((m_Data.attribute(index).value(0).equals("0") && m_Data.attribute(index).value(1).equals("1"))
                            || (m_Data.attribute(index).value(0).equals("1") && m_Data.attribute(index).value(1).equals("0"))) {

                        if (m_Data.attribute(index).value((int) val1).equals("1")) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }

                //nominal case 
                return 1;
            }

            case Attribute.NUMERIC:

                if (Utils.isMissingValue(val1)
                        || Utils.isMissingValue(val2)) {

                return 0;
            } else {
                return (!m_DontNormalize)
                        ? (norm(val1, index) * norm(val2, index))
                        : (val1 * val2);
            }

            default:
                return 0;
        }

    }

    /**
     * Updates the current distance calculated so far with the new difference
     * between two attributes. The difference between the attributes was
     * calculated with the difference(int,double,double) method.
     *
     * @param currDist	the current distance calculated so far
     * @param diff	the difference between two new attributes
     * @return	the update distance
     * @see	#difference(int, double, double)
     */
    @Override
    protected double updateDistance(double currDist, double diff) {

        return (currDist + diff);
    }

    @Override
    public String globalInfo() {
        return "Cosine distance implementation";
    }
}
