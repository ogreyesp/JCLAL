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

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.core.IEvaluation;

/**
 * Abstract class for evaluation measures.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class AbstractEvaluation implements IEvaluation {

	/**
	 * Represent the labeled set size.
	 */
	private int iteration;

	/**
	 * Represent the labeled set size.
	 */
	private int labeledSetSize;

	/**
	 * Represent the unlabeled set size.
	 */
	private int unlabeledSetSize;

	/**
	 * It is used to accelerate the searching of values and to store the metric
	 * values loaded from a report file.
	 */
	protected HashMap<String, Double> metrics;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getMetricNames() {

		String[] metricNames = new String[metrics.size()];
		int index = 0;

		for (String key : metrics.keySet()) {

			metricNames[index++] = key;
		}

		Arrays.sort(metricNames);

		return metricNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getMetricValue(String metricName) {
		try {
			if (metrics.containsKey(metricName)) {
				return metrics.get(metricName);
			} else {
				throw new Exception("The measure name was not found");
			}
		} catch (Exception ex) {
			Logger.getLogger(AbstractEvaluation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMetricValue(String metricName, double value) {

		metrics.put(metricName, value);
	}

	/**
	 * Get the current iteration of the experiment.
	 * 
	 * @return The current iteration of the experiment.
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Set the current iteration of the experiment
	 * 
	 * @param iteration
	 *            The current iteration of the experiment.
	 */
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	/**
	 * Get the number of labelled instances
	 * 
	 * @return The size of the set of labeled instances.
	 */
	public int getLabeledSetSize() {
		return labeledSetSize;
	}

	/**
	 * Set the number of labelled instances
	 * 
	 * @param labeledSetSize
	 *            The size of the set of labeled instances.
	 */
	public void setLabeledSetSize(int labeledSetSize) {
		this.labeledSetSize = labeledSetSize;
	}

	/**
	 * Get the number of the unlabeled instances
	 *
	 * @return The size of the set of unlabeled instances.
	 */
	public int getUnlabeledSetSize() {
		return unlabeledSetSize;
	}

	/**
	 * Set the number of the unlabeled instances
	 * 
	 * @param unlabeledSetSize
	 *            The size of the set of unlabeled instances.
	 */
	public void setUnlabeledSetSize(int unlabeledSetSize) {
		this.unlabeledSetSize = unlabeledSetSize;
	}

	/**
	 * To extract the evaluation measurements from a text. In the first line the
	 * iteration is defined. In the second line the number of labeled instances
	 * is specified. In the third line the number of not labeled instances. Next
	 * the evaluation measures appears.
	 *
	 * @param stringEvaluation
	 *            The evaluation in string format
	 */
	@Override
	public void loadMetrics(String stringEvaluation) {

		if (metrics == null) {
			metrics = new HashMap<String, Double>();
		}

		String array[] = stringEvaluation.split("\n");

		iteration = Integer.valueOf(array[0].split(":")[1].trim());
		labeledSetSize = Integer.valueOf(array[1].split(":")[1].trim());
		unlabeledSetSize = Integer.valueOf(array[2].split(":")[1].trim());

		for (int i = 4; i < array.length; i++) {

			if (array[i].equals("\t\t")) {
				continue;
			}

			String[] metric = array[i].split(":");

			metrics.put(metric[0].trim(), Double.valueOf(metric[1].replace(",", ".").trim()));
		}
	}

	@Override
	public String toString() {

		StringBuilder st = new StringBuilder();

		st.append("Iteration: ").append(getIteration()).append("\n");
		st.append("Labeled set size: ").append(getLabeledSetSize()).append("\n");
		st.append("Unlabelled set size: ").append(getUnlabeledSetSize()).append("\n");
		st.append("\t\n");

		for (String key : metrics.keySet()) {

			st.append(key).append(": ").append(metrics.get(key)).append("\n");
		}

		st.append("\t\t\n");

		return st.toString();
	}
}