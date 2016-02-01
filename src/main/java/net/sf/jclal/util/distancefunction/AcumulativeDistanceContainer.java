/*
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jclal.util.distancefunction;

import java.util.ArrayList;
import net.sf.jclal.util.sort.Container;
import net.sf.jclal.util.sort.IndexValueContainer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;

/**
 * Utility class to store efficiently the distance between a set of instances.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class AcumulativeDistanceContainer extends IndexValueContainer {

    /**
     * The distance function used 
     */
    private NormalizableDistance distanceFunction;

    /**
     * Constructor by default.
     *
     * @param objectiveColumns The objetive colums
     * @param rows The rows
     * @param distanceFunction The distance function used to calculate the
     * similarity
     */
    public AcumulativeDistanceContainer(Instances objectiveColumns, Instances rows,
            NormalizableDistance distanceFunction) {
        this.distanceFunction = distanceFunction;

        indexesChanges = new int[rows.numInstances()];
        for (int i = 0; i < indexesChanges.length; i++) {
            indexesChanges[i] = i;
        }
        size = indexesChanges.length;

        acumulativeValue = new double[rows.numInstances()];
        for (int i = 0; i < acumulativeValue.length; i++) {
            for (int j = 0; j < objectiveColumns.numInstances(); j++) {
                acumulativeValue[i] += distanceFunction.distance(rows.instance(i),
                        objectiveColumns.instance(j));
            }
        }
    }

    /**
     * Update the indexes
     *
     * @param fromRowToColumn The fromRowToColumn
     * @param rows The rows
     */
    public void updateIndexes(ArrayList<Integer> fromRowToColumn, Instances rows) {

        ArrayList<Container> order = obtainOrdered(fromRowToColumn);

        for (Container container : order) {
            updateIndex(Integer.parseInt(container.getValue().toString()), rows);
        }
    }

    /**
     * The values are updated
     *
     * @param index The index to update
     * @param rows The instances
     */
    public void updateIndex(int index, Instances rows) {
        Instance x = rows.instance(index);

        for (int i = 0; i < index; i++) {
            setAcumulativeValue(i, getAcumulativeValue(i)
                    + distanceFunction.distance(x, rows.instance(i)));
        }

        for (int i = index + 1; i < size; i++) {
            setAcumulativeValue(i, getAcumulativeValue(i)
                    + distanceFunction.distance(x, rows.instance(i)));
        }

        //delete index
        deleteIndex(index);
    }
}
