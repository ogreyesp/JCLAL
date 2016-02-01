/*
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
package net.sf.jclal.activelearning.oracle;

import java.util.ArrayList;

import net.sf.jclal.activelearning.multilabel.querystrategy.AbstractMultiLabelQueryStrategy;
import net.sf.jclal.activelearning.querystrategy.AbstractQueryStrategy;
import net.sf.jclal.activelearning.singlelabel.querystrategy.AbstractSingleLabelQueryStrategy;
import net.sf.jclal.core.IQueryStrategy;
import org.apache.commons.configuration.Configuration;
import weka.core.Instance;

/**
 * Class that represents an oracle in a simulated way. The class of the selected
 * instances are known previously to the AL process. The labels of the instances
 * are hidden and the oracle only reveals the labels.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 *
 */
public class SimulatedOracle extends AbstractOracle {

	private static final long serialVersionUID = 3176021836539069855L;

	private boolean showOnConsole = false;

	/**
	 * Do nothing due to the label of the selected instances are known. The
	 * labels are only reveal.
	 *
	 * @param queryStrategy
	 *            The query strategy to use.
	 */
	@Override
	public void labelInstances(IQueryStrategy queryStrategy) {

		ArrayList<Integer> selected = ((AbstractQueryStrategy) queryStrategy).getSelectedInstances();

		lastLabeledInstances = new ArrayList<String>();

		if (isShowOnConsole())
			System.out.println("\nSimulated oracle");

		// For each selected instance
		for (int i : selected) {

			// Ask to the oracle about the class of the instance
			Instance instance = queryStrategy.getUnlabelledData().instance(i);

			if (isShowOnConsole()) {

				// label according to the type of learning
				if (queryStrategy instanceof AbstractMultiLabelQueryStrategy) {

					int[] labels = ((AbstractMultiLabelQueryStrategy) queryStrategy).getLabelIndices();
					showMultiLabelInstance(instance, labels);
				} else if (queryStrategy instanceof AbstractSingleLabelQueryStrategy) {
					showSingleLabelInstance(instance);
				}

			}

			lastLabeledInstances.add(instance.toString());
		}

	}

	/**
	 * If the instances that are labeled are showed by console.
	 * 
	 * @return It shows if the results are showed by console
	 */
	public boolean isShowOnConsole() {
		return showOnConsole;
	}

	/**
	 * Set if the instances that are labeled are showed by console.
	 * 
	 * @param showOnConsole
	 *            Set if the results are showed by console
	 */
	public void setOnConsole(boolean showOnConsole) {
		this.showOnConsole = showOnConsole;
	}

	/**
	 * @param settings
	 *            Configuration object for the oracle.
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li><b>show-on-console= true</b></li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		boolean console = settings.getBoolean("show-on-console", isShowOnConsole());
		setOnConsole(console);
	}

	public void showSingleLabelInstance(Instance instance) {

		System.out.println("\nInstance selected: " + instance.toString());
		System.out.println("Class: " + instance.stringValue(instance.classIndex()));
	}

	public void showMultiLabelInstance(Instance instance, int[] labels) {

		System.out.println("\nInstance selected: " + instance.toString());

		System.out.println("Labels: ");

		for (int l = 0; l < labels.length; l++) {
			System.out.println(l + ":" + instance.stringValue(labels[l]));
		}
	}
}