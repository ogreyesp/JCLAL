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
package net.sf.jclal.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.jclal.experiment.RunExperiment;
import net.sf.jclal.util.xml.XMLConfigurationBuilder;
import net.sf.jclal.util.xml.XmlFormat;

/**
 * Example where a xml configuration is created.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class CreateXmlConfigurationFile {

    /**
     * The main method
     * 
     * @param args the command line arguments
     * @throws FileNotFoundException The exception that will be launched
     * @throws IOException The exception that will be launched
     * @throws Exception The exception that will be launched
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        File test = new File("examples/xmlExample.cfg");
        File identatedXml = new File("examples/xmlFormat.cfg");

        XMLConfigurationBuilder experiment = new XMLConfigurationBuilder(test, true);

        experiment.defineEvaluationMethodType("net.sf.jclal.evaluation.method.HoldOut");

        experiment.defineRandFactoryType("net.sf.jclal.util.random.RanecuFactory");
        experiment.defineRandFactorySeed("1299961164");

        experiment.defineFileDataset("datasets/iris/iris.arff");
        experiment.definePercentageSplit(66);

        experiment.defineSamplingType("net.sf.jclal.sampling.unsupervised.Resample");
        experiment.defineSamplingPercentageToSelect(10);

        experiment.defineAlgorithmType("net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm");

        experiment.defineListenerType("net.sf.jclal.listener.GraphicalReporterListener", 0);
        experiment.defineListenerReportFrequency(1, 0);
        experiment.defineListenerReportOnFile(true, 0);
        experiment.defineListenerReportOnConsole(false, 0);
        experiment.defineListenerReportTitle("Example Xml Configuration", 0);
        experiment.defineListenerShowWindow(true, 0);
        experiment.defineListenerShowPassiveLearning(true, 0);
        experiment.defineListenerSendMailTo("a@gmail.com", 0, 0);
        experiment.defineListenerSendMailTo("b@gmail.com", 0, 1);

        experiment.defineStopCriterionType("net.sf.jclal.activelearning.stopcriterion.PassiveLearningMeasureStopCriterion", 0);
        experiment.defineStopCriterionDisjunctionForm(true, 0);
        experiment.defineStopCriterionMeasureName("Correctly Classified Instances", 0, 0);
        experiment.defineStopCriterionMeasureMaximal(true, 0, 0);

        experiment.defineMaxIteration(50);

        experiment.defineScenarioType("net.sf.jclal.activelearning.scenario.PoolBasedSamplingScenario");

        experiment.defineBatchModeType("net.sf.jclal.activelearning.batchmode.QBestBatchMode");
        experiment.defineBatchSize(1);

        experiment.defineQueryStrategyType("net.sf.jclal.activelearning.singlelabel.querystrategy.VoteEntropyQueryStrategy");
        experiment.defineWrapperClassifierType("net.sf.jclal.classifier.WekaComitteClassifier");
        experiment.defineClassifierType("weka.classifiers.bayes.NaiveBayes", 0);
        experiment.defineClassifierType("weka.classifiers.functions.SMO", 1);

        experiment.defineOracleType("net.sf.jclal.activelearning.oracle.SimulatedOracle");

        experiment.writeXmlFile();

        XmlFormat.formatXmlFile(test, identatedXml, true, "\t");

        String[] arg = {"-cfg=" + identatedXml.getAbsolutePath()};

        RunExperiment.main(arg);

        System.out.println("Done.");
    }
}
