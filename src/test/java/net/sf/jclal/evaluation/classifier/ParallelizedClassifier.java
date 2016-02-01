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
package net.sf.jclal.evaluation.classifier;

import java.io.File;
import java.io.FileFilter;

import net.sf.jclal.classifier.WekaClassifier;
import net.sf.jclal.dataset.WekaDataset;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.dataset.DatasetUtils;
import net.sf.jclal.util.time.TimeControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOsync;
import weka.core.Instances;

/**
 * Control the execution of threads.
 *
 * @author Eduardo Perez Perdomo
 * @author Oscar Gabriel Reyes Pupo
 */
public class ParallelizedClassifier {

	public ParallelizedClassifier() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	//////////////////////////////
	/**
	 * Classifiers tested: weka.classifiers.functions.Logistic
	 * weka.classifiers.rules.JRip weka.classifiers.bayes.NaiveBayes
	 * weka.classifiers.lazy.IBk weka.classifiers.trees.J48
	 * weka.classifiers.trees.SimpleCart net.sf.jclal.classifier.AdaBoostM1C45
	 *
	 * @throws Exception
	 *             Launch an exception in case that an error occurs
	 */
	@Test
	public void testClassifier() throws Exception {
		System.setProperty("cores-peer-processors", "all");

		int folds = 10;
		Classifier c = new SMOsync();

		File[] list = new File("datasets").listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".arff");
			}
		});

		String[] datasets = new String[list.length];

		for (int i = 0; i < datasets.length; i++) {
			datasets[i] = list[i].getAbsolutePath();
		}

		TimeControl control = new TimeControl(datasets.length * folds * 2);

		int contS = 0;
		long timeS, timeP;
		int contP = 0;
		for (String data : datasets) {
			System.out.println("begin: " + data);
			Instances x = DatasetUtils.loadWekaDataSet(data).getDataset();

			x.setClassIndex(x.numAttributes() - 1);
			x.stratify(folds);

			//////////////////////////////////
			for (int i = 0; i < folds; i++) {
				Instances train = x.trainCV(folds, i);
				Instances test = x.testCV(folds, i);

				WekaClassifier simpleClassifier = new WekaClassifier();
				simpleClassifier.setClassifier(c);
				simpleClassifier.buildClassifier(new WekaDataset(train));
				control.mark();
				AbstractEvaluation sC = simpleClassifier.testModel(new WekaDataset(test));
				control.mark();
				timeS = control.timeLastOnes();

				WekaClassifier parallelClassifier = new WekaClassifier();
				parallelClassifier.setParallel(true);
				parallelClassifier.setClassifier(c);
				parallelClassifier.buildClassifier(new WekaDataset(train));
				control.mark();
				AbstractEvaluation pC = parallelClassifier.testModel(new WekaDataset(test));
				control.mark();
				timeP = control.timeLastOnes();

				Assert.assertEquals(sC, pC);
				if (timeS > timeP) {
					contP++;
				} else if (timeS < timeP) {
					contS++;
				}
			}

			System.out.println("wins" + "\nsimple: " + contS + "\nparallel: " + contP);

		}

		if (contS > contP) {
			System.out.println("simple WON");
		} else if (contS < contP) {
			System.out.println("parallel won");
		} else {
			System.out.println("tied");
		}
		System.out.println("wins" + "\nsimple: " + contS + "\nparallel: " + contP);
	}
}
