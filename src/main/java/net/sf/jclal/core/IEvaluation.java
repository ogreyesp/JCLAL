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

/**
 * It represents a interface for all evaluation metrics.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public interface IEvaluation {

	/**
	 * Returns the metric names
	 * 
	 * @return The names of the metrics.
	 */
	public String[] getMetricNames();

	/**
	 * String representation of the object
	 * 
	 * @return The evaluations codified as strings
	 */
	@Override
	public String toString();

	/**
	 * Return the value of the given metric
	 *
	 * @param metricName
	 *            The name of a metric.
	 * @return The value associate with the metric's name.
	 */
	public double getMetricValue(String metricName);

	/**
	 * Set the value of the given metric
	 *
	 * @param metricName
	 *            The name of the metric.
	 * @param value
	 *            The value of the metric
	 */
	public void setMetricValue(String metricName, double value);

	/**
	 * Load the metrics from a string.
	 *
	 * @param stringEvaluation
	 *            It is used when the evaluation is loaded from a file or a
	 *            string.
	 */
	public void loadMetrics(String stringEvaluation);
}
