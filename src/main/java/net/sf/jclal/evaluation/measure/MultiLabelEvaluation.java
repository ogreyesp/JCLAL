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

/**
 * Evaluation measures of multi-label instances.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class MultiLabelEvaluation extends AbstractEvaluation {

	private MulanEvaluation evaluation;

	/**
	 * Get the evaluation
	 * 
	 * @return The evaluation
	 */
	public MulanEvaluation getEvaluation() {
		return evaluation;
	}

	/**
	 * Set the evaluation
	 * 
	 * @param evaluation
	 *            The evaluation
	 */
	public void setEvaluation(MulanEvaluation evaluation) {
		this.evaluation = evaluation;
		StringBuilder st = new StringBuilder();

		st.append("Iteration: ").append(getIteration()).append("\n");
		st.append("Labeled set size: ").append(getLabeledSetSize()).append("\n");
		st.append("Unlabelled set size: ").append(getUnlabeledSetSize()).append("\n");
		st.append("\t\n");
		st.append(evaluation.toString());
		st.append("\t\t\n");

		loadMetrics(st.toString());
	}
}
