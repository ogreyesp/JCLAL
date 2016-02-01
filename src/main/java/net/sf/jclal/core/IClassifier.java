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
package net.sf.jclal.core;

import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import weka.core.Instance;

/**
 * Interface for the classifiers to be used in the framework. The framework
 * allows to integrate several types of classifiers.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public interface IClassifier {

    /**
     * Build the classifier.
     *
     * @param instances The instances to use
     * @throws Exception The exception that will be launched.
     */
    public abstract void buildClassifier(IDataset instances) throws Exception;

    /**
     * Return the probabilities of belonging to the classes
     * 
     * @param instance The instance
     * @return the distribution for instance
     */
    public double[] distributionForInstance(Instance instance);

    /**
     * Test the current model with a test set
     *
     * @param instances The instance set to test the classifier.
     * @return The evaluation of the classifier.
     */
    public AbstractEvaluation testModel(IDataset instances);

    /**
     * Make a copy of the current model
     * 
     * @return The copy of the IClassifier used.
     * @throws Exception The exception that will be launched.
     */
    public IClassifier makeCopy() throws Exception;

    /**
     * String representation
     *
     * @return A representation of the classifier.
     */
    @Override
    public String toString();
}
