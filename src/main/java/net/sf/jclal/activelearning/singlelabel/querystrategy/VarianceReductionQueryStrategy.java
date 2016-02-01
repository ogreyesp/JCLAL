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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.util.dataset.DatasetUtils;
import org.apache.commons.configuration.Configuration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

/**
 * Implementation of Variance Reduction query strategy.
 *
 * It reduces generalization error by minimizing output variance.
 *
 * Burr Settles. Active Learning Literature Survey. Computer Sciences Technical
 * Report 1648, University of Wisconsin–Madison. 2009.
 *
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 */
public class VarianceReductionQueryStrategy extends AbstractSingleLabelQueryStrategy {

	private static final long serialVersionUID = -8682210344709120584L;
	/**
	 * It adds to matrixFisher: (the matrix Identity for the regularization
	 * factor) The regularization factor influences enormously the algorithm. A
	 * high value zooms in the precision or accuracy. There comes a moment in
	 * which the increase of the variable does not improve the precision.
	 *
	 */
	private double factorRegularization = -1;
	/**
	 * Epsilon it affects in the quantity of iterations of the algorithm. A low
	 * value guarantees several iterations. The variable alone does not improve
	 * the precision or accuracy. The variable merged with the factor of
	 * regularization (FR) improves the algorithm. The algorithm with high value
	 * of FR and under value of epsilon it is better that the algorithm with
	 * alone high value of FR.
	 */
	private double epsilon = -1;
	/**
	 * To control the iterations that are realized internally on having tried to
	 * find differences minor than epsilon.
	 */
	private int maxEpsilonIteration = Integer.MAX_VALUE;
	/**
	 * To control the iterations that are realized internally on having tried to
	 * find differences minor than epsilon.
	 */
	private int currentEpsilonIteration;
	/**
	 * To optimization.
	 */
	private double[] tempValues;
	/**
	 * To control temporary values used for optimization.
	 */
	private int unlabelledSize = -1;

	/**
	 *
	 * Default constructor
	 */
	public VarianceReductionQueryStrategy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] testUnlabeledData() {
		Instances unlabeled = getUnlabelledData().getDataset();

		if (unlabelledSize != unlabeled.numInstances()) {
			unlabelledSize = unlabeled.numInstances();

			// it is initialized q_sub_i
			int n = unlabeled.numInstances();
			double[] q = new double[n];
			// 1. q_sub_i = 1/n, i = 1, 2, ..., n
			// Arrays.fill(q, 1.0 / n);
			// further on it fills, to optimize

			// it is initialized pi_sub_i
			// 2. pi_sub_i
			double[] piSubI = getPiSubI(unlabeled);

			// to create the Fisher matrix
			int dimensionMatrix = unlabeled.numAttributes() - 1;
			int classIndex = unlabeled.classIndex();

			Matrix matrixFisher = null;
			try {
				matrixFisher = new Matrix(dimensionMatrix, dimensionMatrix);
			} catch (Exception ex) {
				Logger.getLogger(VarianceReductionQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
			}

			for (int i = 0; i < piSubI.length; i++) {
				double mult = piSubI[i] * (1 - piSubI[i]);

				// the values of the instance are had
				double[] atributos = unlabeled.instance(i).toDoubleArray();

				// the attribute class is eliminated, only the features are left
				double[] vectorX = DatasetUtils.copyFeatures(atributos, classIndex);

				Matrix current = null;
				try {
					current = new Matrix(vectorX.length, vectorX.length);
				} catch (Exception ex) {
					Logger.getLogger(VarianceReductionQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
				}

				productVector(current, vectorX);

				// it multiplies current * multi
				current.timesEquals(mult);

				// it adds current to matrixFisher
				// plusEquals saves the result in matrixFisher
				matrixFisher.plusEquals(current);

			}

			double factorRegularizationValue = getFactorRegularization();

			Matrix identity = Matrix.identity(dimensionMatrix, dimensionMatrix);

			identity.timesEquals(factorRegularizationValue);

			// the result joins to matrixFisher
			matrixFisher.plusEquals(identity);

			// do eigen decomposition
			EigenvalueDecomposition eigen = matrixFisher.eig();

			// in case of file, the matrix v takes the matrix file from eigen
			// in this case eigen cant not be destroy for the moment
			Matrix v = eigen.getV();

			double[] landa = eigen.getRealEigenvalues();

			double epsilonValue = getEpsilon();

			// variable copies of q to know if there has been some change
			double[] copiaQ = new double[q.length];
			Arrays.fill(copiaQ, 1.0 / n);

			// while it finds change in q, it keeps on iterating
			currentEpsilonIteration = 0;
			do {
				++currentEpsilonIteration;
				// the value of q is updated
				// in the first iteration it fills with 1.0/n
				System.arraycopy(copiaQ, 0, q, 0, q.length);

				// process of finding f_sub_i
				double[] f = new double[landa.length];
				for (int j = 0; j < f.length; j++) {
					f[j] = 0;

					for (int i = 0; i < n; i++) {
						double mult = q[i] * piSubI[i] * (1 - piSubI[i]);

						// the values of the instance are had
						double[] atributos = unlabeled.instance(i).toDoubleArray();

						// the attribute class is eliminated, only the features
						// are left
						double[] vectorX = DatasetUtils.copyFeatures(atributos, classIndex);

						// it multiplies vector_x with vector_columna of V
						// vector_x it is: 1 X n
						// vector_de_V it is: n X 1
						// result: a number
						double multVectores = 0;
						for (int k = 0; k < vectorX.length; k++) {
							multVectores += vectorX[k] * v.get(k, j);
						}

						// the result rises up to the square
						multVectores *= multVectores;

						// it joins to f[j]
						f[j] += mult * multVectores;
					}
				}

				// the first process of finding q of the current iteration
				for (int i = 0; i < n; i++) {
					double mult = copiaQ[i] * copiaQ[i] * piSubI[i] * (1 - piSubI[i]);

					// the values of the instance are had
					double[] atributos = unlabeled.instance(i).toDoubleArray();

					// the attribute class is eliminated, only the features are
					// left
					double[] vectorX = DatasetUtils.copyFeatures(atributos, classIndex);

					// the following ∑ is realized
					double sumatoria = 0;
					for (int j = 0; j < landa.length; j++) {

						// it multiplies vector_x with vector_columna of V
						// vector_x is: 1 X n
						// vector_de_V is: n X 1
						// result: a number
						double multVectores = 0;
						for (int k = 0; k < vectorX.length; k++) {
							multVectores += vectorX[k] * v.get(k, j);
						}

						// the result multiplies with landa[j]
						multVectores *= landa[j];

						// it rises up to the square
						multVectores *= multVectores;

						// it splits between the square of f [j]
						multVectores /= f[j] * f[j];

						// the sumatoria is added
						sumatoria += multVectores;
					}

					// the value of copia_q [i] is: mult * sumatoria
					copiaQ[i] = mult * sumatoria;
				}

				// the second step to find q in the iteration

				/*
				 * the sum must be out, if it was inside and with copia_q then
				 * one would give priority to the last instance and the last one
				 * would be always chosen
				 */
				double suma = 0;
				for (int j = 0; j < n; j++) {
					suma += copiaQ[j];
				}

				for (int i = 0; i < n; i++) {
					copiaQ[i] = copiaQ[i] / suma;
				}

			} while (change(q, copiaQ, epsilonValue));

			// the values are saved
			tempValues = new double[copiaQ.length];

			System.arraycopy(copiaQ, 0, tempValues, 0, copiaQ.length);

		}

		return super.testUnlabeledData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double utilityInstance(Instance instance) {

		Instances unlabeled = getUnlabelledData().getDataset();

		int indice = unlabeled.indexOf(instance);

		return tempValues[indice];
	}

	/**
	 * Analyzes whether there was some significant change in accordance with
	 * epsilon.
	 *
	 * @param q
	 *            The q value.
	 * @param copiaQ
	 *            The copy of q.
	 * @param epsilon
	 *            The epsilon value.
	 * @return Return whether there was some significant change in accordance
	 *         with epsilon.
	 */
	public boolean change(double[] q, double[] copiaQ, double epsilon) {
		if (currentEpsilonIteration > maxEpsilonIteration) {
			return false;
		}
		for (int i = 0; i < copiaQ.length; i++) {
			if (Math.abs(q[i] - copiaQ[i]) > epsilon) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the probability of the most probable class for each instance.
	 *
	 * @param unlabeled
	 *            The unlabeled set
	 * @return The probability of the most probable class for each instance.
	 */
	private double[] getPiSubI(Instances unlabeled) {
		double[] piSubI = new double[unlabeled.numInstances()];

		for (int i = 0; i < piSubI.length; i++) {
			try {
				double[] pro = distributionForInstance(unlabeled.instance(i));

				piSubI[i] = pro[Utils.maxIndex(pro)];
			} catch (Exception ex) {
				Logger.getLogger(VarianceReductionQueryStrategy.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		return piSubI;
	}

	/**
	 * Compute the product among the matrix and the vector
	 *
	 * @param current
	 *            The matrix.
	 * @param vectorX
	 *            The vector.
	 */
	public void productVector(Matrix current, double[] vectorX) {

		for (int m = 0; m < vectorX.length; m++) {
			for (int nn = 0; nn < vectorX.length; nn++) {
				current.set(m, nn, vectorX[m] * vectorX[nn]);
			}
		}
	}

	/**
	 * Set the regularization factor
	 *
	 * @param factorRegularization
	 *            The factor of regularization. Used in Variance Reduction.
	 */
	public void setFactorRegularization(double factorRegularization) {
		this.factorRegularization = factorRegularization;
	}

	/**
	 * Get the regularization factor
	 * 
	 * @return The factor of regularization. Used in Variance Reduction.
	 */
	public double getFactorRegularization() {
		if (factorRegularization == -1) {
			factorRegularization = 1000;
		}
		return factorRegularization;
	}

	/**
	 * Set epsilon
	 * 
	 * @param epsilon
	 *            Epsilon. Used in Variance Reduction.
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * Get epsilon
	 * 
	 * @return Epsilon. Used in Variance Reduction.
	 */
	public double getEpsilon() {
		if (epsilon == -1) {
			epsilon = 1.0 / ((getUnlabelledData().getNumInstances() + getLabelledData().getNumInstances()) * 100);
		}
		return epsilon;
	}

	/**
	 * It determines what is the maximum number of iterations to run without
	 * finding a change minor than epsilon.
	 *
	 * @return The maximun number of iterations.
	 */
	public int getMaxEpsilonIteration() {
		return maxEpsilonIteration;
	}

	/**
	 * It determines which it is the maximum number of iterations that is run
	 * before finding a change minor than epsilon.
	 *
	 * @param maxEpsilonIteration
	 *            The maximum number of iterations.
	 *
	 */
	public void setMaxEpsilonIteration(int maxEpsilonIteration) {
		this.maxEpsilonIteration = maxEpsilonIteration;
	}

	/**
	 *
	 * @param configuration
	 *            Configuration for variance reduction strategy.
	 *
	 *            The XML labels supported are:
	 *
	 *            <ul>
	 *            <li>epsilon= double</li>
	 *            <li>epsilon-iteration= int</li>
	 *            <li>factor-regularization= double</li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {
		super.configure(configuration);

		// Set epsilon
		double currentEpsilon = configuration.getDouble("epsilon", epsilon);
		setEpsilon(currentEpsilon);

		// Set epsilon iteration
		int currentMaxEpsilonI = configuration.getInt("epsilon-iteration", maxEpsilonIteration);
		setMaxEpsilonIteration(currentMaxEpsilonI);

		// Set factor regularization
		double currentFactorRegularization = configuration.getDouble("factor-regularization", factorRegularization);
		setFactorRegularization(currentFactorRegularization);

	}
}