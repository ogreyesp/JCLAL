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
package weka.classifiers.functions;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Utils;

/**
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 * @author Luis David Gonzalez Orozco
 */
public class SMOsync extends weka.classifiers.functions.SMO {

	private static final long serialVersionUID = -7282394644176040926L;

	/**
	 * Empty constructor
	 */
	public SMOsync() {

		super();

		setBuildLogisticModels(true);
		setC(1.0);
	}

	/**
	 *
	 * @return The array of Binary SMO
	 */
	public BinarySMO[][] getM_classifiers() {
		return m_classifiers;
	}

	/**
	 *
	 * @param m_classifiers
	 *            the array of binary SMO
	 */
	public void setM_classifiers(BinarySMO[][] m_classifiers) {
		this.m_classifiers = m_classifiers;

	}

	public double SVMOutput(Instance instance) {
		try {
			return m_classifiers[0][1].SVMOutput(-1, instance);
		} catch (Exception ex) {
			Logger.getLogger(SMO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized double[] distributionForInstance(Instance inst) throws Exception {

		// Filter instance
		if (!m_checksTurnedOff) {
			m_Missing.input(inst);
			m_Missing.batchFinished();
			inst = m_Missing.output();
		}

		if (m_NominalToBinary != null) {
			m_NominalToBinary.input(inst);
			m_NominalToBinary.batchFinished();
			inst = m_NominalToBinary.output();
		}

		if (m_Filter != null) {
			m_Filter.input(inst);
			m_Filter.batchFinished();
			inst = m_Filter.output();
		}

		notify();

		if (!m_fitLogisticModels) {
			double[] result = new double[inst.numClasses()];
			for (int i = 0; i < inst.numClasses(); i++) {
				for (int j = i + 1; j < inst.numClasses(); j++) {
					if ((m_classifiers[i][j].m_alpha != null) || (m_classifiers[i][j].m_sparseWeights != null)) {
						double output = m_classifiers[i][j].SVMOutput(-1, inst);
						if (output > 0) {
							result[j] += 1;
						} else {
							result[i] += 1;
						}
					}
				}
			}
			Utils.normalize(result);
			return result;
		} else {

			// We only need to do pairwise coupling if there are more
			// then two classes.
			if (inst.numClasses() == 2) {
				double[] newInst = new double[2];
				newInst[0] = m_classifiers[0][1].SVMOutput(-1, inst);
				newInst[1] = Utils.missingValue();
				double[] value;
				value = m_classifiers[0][1].m_logistic.distributionForInstance(new DenseInstance(1, newInst));
				notify();
				return value;
			}
			double[][] r = new double[inst.numClasses()][inst.numClasses()];
			double[][] n = new double[inst.numClasses()][inst.numClasses()];
			for (int i = 0; i < inst.numClasses(); i++) {
				for (int j = i + 1; j < inst.numClasses(); j++) {
					if ((m_classifiers[i][j].m_alpha != null) || (m_classifiers[i][j].m_sparseWeights != null)) {
						double[] newInst = new double[2];
						newInst[0] = m_classifiers[i][j].SVMOutput(-1, inst);
						newInst[1] = Utils.missingValue();

						r[i][j] = m_classifiers[i][j].m_logistic
								.distributionForInstance(new DenseInstance(1, newInst))[0];
						notify();

						n[i][j] = m_classifiers[i][j].m_sumOfWeights;
					}
				}
			}
			return weka.classifiers.meta.MultiClassClassifier.pairwiseCoupling(n, r);
		}

	}

}
