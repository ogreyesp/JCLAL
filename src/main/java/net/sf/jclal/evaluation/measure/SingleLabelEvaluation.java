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
package net.sf.jclal.evaluation.measure;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;

/**
 * Evaluation measures of single-label instances.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class SingleLabelEvaluation extends AbstractEvaluation {

	/**
	 * Evaluation from Weka.
	 */
	private Evaluation evaluation;

	/**
	 * Get the evaluation
	 * 
	 * @return The evaluation
	 */
	public Evaluation getEvaluation() {
		return evaluation;
	}

	/**
	 * Set the evaluation metrics
	 * 
	 * @param evaluation
	 *            The evaluation
	 */
	public void setEvaluation(Evaluation evaluation) {

		try {
			this.evaluation = evaluation;
			StringBuilder st = new StringBuilder();

			st.append("Iteration: ").append(getIteration()).append("\n");
			st.append("Labeled set size: ").append(getLabeledSetSize()).append("\n");
			st.append("Unlabelled set size: ").append(getUnlabeledSetSize()).append("\n");
			st.append("\t\n");

			st.append("Correctly Classified Instances: ").append(evaluation.pctCorrect()).append("\n");
			st.append("Incorrectly Classified Instances: ").append(evaluation.pctIncorrect()).append("\n");
			st.append("Kappa statistic: ").append(evaluation.kappa()).append("\n");
			st.append("Mean absolute error: ").append(evaluation.meanAbsoluteError()).append("\n");
			st.append("Root mean squared error: ").append(evaluation.rootMeanSquaredError()).append("\n");

			st.append("Relative absolute error: ").append(evaluation.relativeAbsoluteError()).append("\n");
			st.append("Root relative squared error: ").append(evaluation.rootRelativeSquaredError()).append("\n");
			st.append("Coverage of cases: ").append(evaluation.coverageOfTestCasesByPredictedRegions()).append("\n");
			st.append("Mean region size: ").append(evaluation.sizeOfPredictedRegions()).append("\n");

			st.append("Weighted Precision: ").append(evaluation.weightedPrecision()).append("\n");
			st.append("Weighted Recall: ").append(evaluation.weightedRecall()).append("\n");
			st.append("Weighted FMeasure: ").append(evaluation.weightedFMeasure()).append("\n");
			st.append("Weighted TruePositiveRate: ").append(evaluation.weightedTruePositiveRate()).append("\n");
			st.append("Weighted FalsePositiveRate: ").append(evaluation.weightedFalsePositiveRate()).append("\n");
			st.append("Weighted MatthewsCorrelation: ").append(evaluation.weightedMatthewsCorrelation()).append("\n");
			st.append("Weighted AreaUnderROC: ").append(evaluation.weightedAreaUnderROC()).append("\n");
			st.append("Weighted AreaUnderPRC: ").append(evaluation.weightedAreaUnderPRC()).append("\n");

			st.append("\t\t\n");

			loadMetrics(st.toString());

		} catch (Exception e) {
			Logger.getLogger(SingleLabelEvaluation.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
