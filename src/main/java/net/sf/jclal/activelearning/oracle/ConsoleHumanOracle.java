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

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import net.sf.jclal.activelearning.multilabel.querystrategy.AbstractMultiLabelQueryStrategy;
import net.sf.jclal.activelearning.querystrategy.AbstractQueryStrategy;
import net.sf.jclal.activelearning.singlelabel.querystrategy.AbstractSingleLabelQueryStrategy;
import net.sf.jclal.core.IQueryStrategy;
import net.sf.jclal.util.dataset.DatasetUtils;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents a human oracle that is queried by a console.
 *
 * For each selected instances the human annotator is queried.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ConsoleHumanOracle extends AbstractOracle {

	private static final long serialVersionUID = 7031732222323102868L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void labelInstances(IQueryStrategy queryStrategy) {

		// label according to the type of learning
		if (queryStrategy instanceof AbstractMultiLabelQueryStrategy) {
			labelMultiLabelInstances(queryStrategy);
		} else if (queryStrategy instanceof AbstractSingleLabelQueryStrategy) {
			labelSingleLabelInstances(queryStrategy);
		}

		System.out.println("Back to active learning process. Please wait...");
	}

	/**
	 * Method for the specific case of a multi-label instance.
	 *
	 * @param queryStrategy
	 *            The query strategy to use
	 */
	private void labelMultiLabelInstances(IQueryStrategy queryStrategy) {

		AbstractMultiLabelQueryStrategy multiLabelQueryStrategy = (AbstractMultiLabelQueryStrategy) queryStrategy;

		ArrayList<Integer> selected = multiLabelQueryStrategy.getSelectedInstances();

		lastLabeledInstances = new ArrayList<String>();

		// Object to read from the console
		Scanner scanner = new Scanner(new BufferedInputStream(System.in));

		ArrayList<String> labels = new ArrayList<String>(multiLabelQueryStrategy.getLabelsMetaData().getLabelNames());

		System.out.println("\nHuman oracle.");

		// For each selected instance
		for (int i : selected) {

			// Ask to the oracle about the class of the instance
			Instance instance = multiLabelQueryStrategy.getUnlabelledData().instance(i);

			System.out.println("\nWhat are the labels of this multi-label instance?");

			System.out.println("Instance:" + instance.toString() + "\n");

			String[] values;

			System.out.println("Label-Label name");
			System.out.println("------------------");

			int index = 0;

			for (String label : labels) {

				System.out.println((index++) + "-" + label);
			}

			System.out.println(
					"\nType the indexes of the labels that the instance belongs separated by a colon, or type -1 if you want to skip this instance.");

			do {

				System.out.print("indexes >> ");

				values = scanner.nextLine().split(",");

				if (values.length == 0 || values[0].trim().equals(""))
					System.out.println("Wrong value");

				if (values.length == 1 && values[0].equals("-1"))
					break;

			} while (values.length == 0 || values[0].trim().equals(""));

			if (values[0].equals("-1"))
				continue;

			// Reset the labels
			for (int labelIndex = 0; labelIndex < multiLabelQueryStrategy.getNumLabels(); labelIndex++) {
				instance.setValue(multiLabelQueryStrategy.getLabelIndices()[labelIndex], 0);
			}

			for (String value : values) {

				int labelIndex = Integer.valueOf(value);

				instance.setValue(multiLabelQueryStrategy.getLabelIndices()[labelIndex], 1);

			}

			System.out.println();

			lastLabeledInstances.add(instance.toString());
		}
	}

	/**
	 * Method for the specific case of a single-label instance.
	 *
	 * @param queryStrategy
	 *            The query strategy to use
	 */
	private void labelSingleLabelInstances(IQueryStrategy queryStrategy) {

		// Object to read from the console
		Scanner scanner = new Scanner(new BufferedInputStream(System.in));

		ArrayList<Integer> selected = ((AbstractQueryStrategy) queryStrategy).getSelectedInstances();

		lastLabeledInstances = new ArrayList<String>();

		// In the labeled dataset must be defined all the possible classes
		Instances labeled = queryStrategy.getLabelledData().getDataset();

		String[] valueClass = DatasetUtils.valueClasses(labeled);

		System.out.println("\nHuman oracle.");

		// For each selected instance
		for (int i : selected) {

			// Ask to the tagger about the class of the instance
			Instance instance = queryStrategy.getUnlabelledData().instance(i);

			System.out.println("What is the class of this instance?");

			System.out.println("Instance: " + instance.toString());

			int classSelected = 0;

			System.out.println("\nIndex: Class name");
			System.out.println("-------------------");

			for (int index = 0; index < valueClass.length; index++) {
				System.out.println(index + ": " + valueClass[index]);
			}

			System.out.println("\nType the index of the class or type -1 if you want skip this instance");

			do {

				System.out.print("index >> ");
				classSelected = scanner.nextInt();

				if (classSelected >= valueClass.length) {
					System.out.println("Wrong index.");
				}

				if (classSelected == -1)
					break;

			} while (classSelected >= valueClass.length);

			if (classSelected == -1)
				continue;

			instance.setClassValue(classSelected);

			lastLabeledInstances.add(instance.toString());

			System.out.println();
		}

	}
}