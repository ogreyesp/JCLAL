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
package net.sf.jclal.activelearning.singlelabel.querystrategy;

import net.sf.jclal.core.ISystem;
import net.sf.jclal.core.ITool;
import net.sf.jclal.core.IRandGen;
import weka.core.Instance;

/**
 * Implementation of Random selection query strategy. The examples are chosen in
 * randomly manner.
 *
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 *
 */
public class RandomSelectionQueryStrategy extends AbstractSingleLabelQueryStrategy implements ITool {

    private static final long serialVersionUID = 4341080288965932110L;

    /**
     * The random numbers generator.
     */
    private IRandGen random;

    /**
     * Stores the random values, is needed when is used a parallel context to
     * access in the 'IRandGen'
     */
    private double[] randV;

    /**
     * Empty (default) constructor.
     */
    public RandomSelectionQueryStrategy() {
    }

    @Override
    public double[] testUnlabeledData() {
        randV = new double[getUnlabelledData().getNumInstances()];
        for (int i = 0; i < randV.length; i++) {
            randV[i] = random.raw();
        }

        return super.testUnlabeledData();
    }

    /**
     * Returns any instance according to the generator.
     *
     * @param instance The instance to query.
     * @return The utility of the instance.
     */
    @Override
    public double utilityInstance(Instance instance) {

        return randV[getUnlabelledData().getDataset().indexOf(instance)];

    }

    /**
     * Contextualize the random generator.
     *
     * @param context The context to use.
     */
    @Override
    public void contextualize(ISystem context) {
        random = context.createRandGen();
    }
}
