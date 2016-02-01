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
package net.sf.jclal.util.xml;

import java.io.File;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

/**
 * Utility class to construct or read a xml configuration file to execute an
 * experiment.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public abstract class AbstractXMLConfiguration {

    protected File file;
    protected DefaultConfigurationBuilder experiment;

    //configuration elements
    protected String xmlSeparator = ".";
    protected String rootElementName = "experiment";
    protected String type = "type";

    //process
    protected String process = "process";
    protected String evaluationMethodType = "evaluation-method-type";

    protected String algorithm = "algorithm";

    //random generator
    protected String randFactory = "rand-gen-factory";
    protected String seed = "seed";

    //files
    protected String fileDataset = "file-dataset";
    protected String fileTrain = "file-train";
    protected String fileTest = "file-test";
    protected String fileLabeled = "file-labeled";
    protected String fileUnlabeled = "file-unlabeled";
    protected String classAttribute = "class-attribute";

    //listener
    protected String listener = "listener";
    protected String reportFrequency = "report-frequency";
    protected String reportOnFile = "report-on-file";
    protected String reportOnConsole = "report-on-console";
    protected String reportTitle = "report-title";
    protected String showWindow = "show-window";
    protected String showPassiveLearning = "show-passive-learning";
    protected String reportDirectory = "report-directory";

    //send mail
    protected String sendEmail = "send-email";
    protected String smtpHost = "smtp-host";
    protected String smtpPort = "smtp-port";
    protected String to = "to";
    protected String from = "from";
    protected String attachReportFile = "attach-report-file";
    protected String user = "user";
    protected String pass = "pass";

    protected String maxIteration = "max-iteration";

    //HoldOut method
    protected String percentageSplit = "percentage-split";

    //kFold method
    protected String stratify = "stratify";
    protected String numFolds = "num-folds";

    //sampling
    protected String samplingMethod = "sampling-method";
    protected String percentageToSelect = "percentage-to-select";
    protected String noReplacement = "no-replacement";
    protected String invertSelection = "invert-selection";
    protected String biasToUniformClass = "bias-to-uniform-class";

    //scenario
    protected String scenario = "scenario";
    protected String threshold = "threshold";

    //batch
    protected String batchMode = "batch-mode";
    protected String batchSize = "batch-size";

    //query strategy
    protected String queryStrategy = "query-strategy";
    protected String wrapperClassifier = "wrapper-classifier";
    protected String classifier = "classifier";

    //oracle
    protected String oracle = "oracle";

    //stop criterion
    protected String stopCriterion = "stop-criterion";
    protected String disjunctionForm = "disjunction-form";
    protected String measure = "measure";
    protected String maximal = "maximal";

    //sub query
    protected String subQueryStrategy = "sub-query-strategy";

    //density diversity
    protected String importanceDensity = "importance-density";
    protected String distanceFunction = "distance-function";

    //variance reduction
    protected String epsilon = "epsilon";
    protected String maxEpsilonIteration = "epsilon-iteration";
    protected String factorRegularization = "factor-regularization";
    protected String matrixFile = "matrix-file";

    //multi-label
    protected String multiLabel = "multi-label";

    protected String fileXml = "file-xml";

    protected String baseClassifier = "base-classifier";

    //in query strategy
    protected String evidenceDimension = "evidence-dimension";
    protected String classDimension = "class-dimension";
    protected String weightDimension = "weight-dimension";

    public String getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(String rootElementName) {
        this.rootElementName = rootElementName;
        experiment.setRootElementName(rootElementName);
    }

    //*************************Definition of elements*****************
    public String textEvaluationMethodType() {
        return process + "[@" + evaluationMethodType + "]";
    }

    public String textAlgorithmType() {
        return process + xmlSeparator + algorithm + "[@" + type + "]";
    }

    //Rand-factory
    public String textRandFactoryType() {
        return process + xmlSeparator + randFactory + "[@" + type + "]";
    }

    public String textRandFactorySeed() {
        return process + xmlSeparator + randFactory + "[@" + seed + "]";
    }
    //************

    //files
    public String textFileDataset() {
        return process + xmlSeparator + fileDataset;
    }

    public String textFileTrain() {
        return process + xmlSeparator + fileTrain;
    }

    public String textFileTest() {
        return process + xmlSeparator + fileTest;
    }

    public String textFileLabeled() {
        return process + xmlSeparator + fileLabeled;
    }

    public String textFileUnlabeled() {
        return process + xmlSeparator + fileUnlabeled;
    }

    public String textClassAttribute() {
        return process + xmlSeparator + classAttribute;
    }
    //**************

    //Listener
    public String textListenerTypeList() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + "[@" + type + "]";
    }

    public String textListenerType(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + "[@" + type + "]";
    }

    public String textListenerReportFrequency(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + reportFrequency;
    }

    public String textListenerReportOnFile(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + reportOnFile;
    }

    public String textListenerReportOnConsole(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + reportOnConsole;
    }

    public String textListenerReportTitle(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + reportTitle;
    }

    public String textListenerShowWindow(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + showWindow;
    }

    public String textListenerReportDirectory(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + reportDirectory;
    }

    public String textListenerShowPassiveLearning(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + showPassiveLearning;
    }
    //************

    //send mail
    public String textListenerSendMailType(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + "[@" + type + "]";
    }

    public String textListenerSendMailSmtpHost(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + xmlSeparator + smtpHost;
    }

    public String textListenerSendMailSmtpPort(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + xmlSeparator + smtpPort;
    }

    public String textListenerSendMailToList(int numOfParentListener) {
        String pNum = "(" + String.valueOf(numOfParentListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + pNum + xmlSeparator
                + sendEmail + xmlSeparator + to;
    }

    public String textListenerSendMailTo(int numOfParentListener, int numOfRecipient) {
        String pNum = "(" + String.valueOf(numOfParentListener) + ")";
        String rNum = "(" + String.valueOf(numOfRecipient) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + pNum + xmlSeparator
                + sendEmail + xmlSeparator + to + rNum;
    }

    public String textListenerSendMailFrom(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + xmlSeparator + from;
    }

    public String textListenerSendMailAttachReportFile(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator + sendEmail + xmlSeparator
                + attachReportFile;
    }

    public String textListenerSendMailUser(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + xmlSeparator + user;
    }

    public String textListenerSendMailPassword(int numOfListener) {
        String sNum = "(" + String.valueOf(numOfListener) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + listener + sNum + xmlSeparator
                + sendEmail + xmlSeparator + pass;
    }
    //***********

    public String textMaxIteration() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + maxIteration;
    }

    //HoldOut method
    public String textPercentageSplit() {
        return process + xmlSeparator + percentageSplit;
    }

    //kFold method
    public String textStratity() {
        return process + xmlSeparator + stratify;
    }

    public String textNumFolds() {
        return process + xmlSeparator + numFolds;
    }
    //***********

    //sampling
    public String textSamplingType() {
        return process + xmlSeparator
                + samplingMethod + "[@" + type + "]";
    }

    public String textSamplingPercentageToSelect() {
        return process + xmlSeparator + samplingMethod + xmlSeparator
                + percentageToSelect;
    }

    public String textSamplingNoReplacement() {
        return process + xmlSeparator + samplingMethod + xmlSeparator
                + noReplacement;
    }

    public String textSamplingInvertSelection() {
        return process + xmlSeparator + samplingMethod + xmlSeparator
                + invertSelection;
    }

    public String textSamplingBiasToUniformClass() {
        return process + xmlSeparator + samplingMethod + xmlSeparator
                + biasToUniformClass;
    }
    //**********

    //scenario
    public String textScenarioType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + "[@" + type + "]";
    }

    public String textScenarioStreamThreshold() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + threshold;
    }

    //batch-mode
    public String textBatchModeType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator
                + batchMode + "[@" + type + "]";
    }

    public String textBatchSize() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + batchMode + xmlSeparator
                + batchSize;
    }

    //query strategy
    public String textQueryStrategyType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator
                + queryStrategy + "[@" + type + "]";
    }

    public String textWrapperClassifierType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + wrapperClassifier
                + "[@" + type + "]";
    }

    public String textClassifierTypeList() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + "[@" + type + "]";
    }

    public String textClassifierType(int numOfClassifier) {
        String sNum = "(" + String.valueOf(numOfClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + sNum + "[@" + type + "]";
    }

    //oracle
    public String textOracleType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + oracle + "[@" + type + "]";
    }

    //stop criterion
    public String textStopCriterionTypeList() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + "[@" + type + "]";
    }

    public String textStopCriterionType(int numOfStopCriterion) {
        String sNum = "(" + String.valueOf(numOfStopCriterion) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + sNum + "[@" + type + "]";
    }

    public String textStopCriterionDisjunctionForm(int numOfStopCriterion) {
        String sNum = "(" + String.valueOf(numOfStopCriterion) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + sNum + xmlSeparator
                + disjunctionForm;
    }

    public String textStopCriterionMeasureNameList(int numOfParentStopCriterion) {
        String pNum = "(" + String.valueOf(numOfParentStopCriterion) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + pNum + xmlSeparator
                + measure;
    }

    public String textStopCriterionMeasureName(int numOfParentStopCriterion,
            int numOfStopCriterionMeasure) {
        String pNum = "(" + String.valueOf(numOfParentStopCriterion) + ")";
        String mNum = "(" + String.valueOf(numOfStopCriterionMeasure) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + pNum + xmlSeparator
                + measure + mNum;
    }

    public String textStopCriterionMeasureMaximal(int numOfParentStopCriterion,
            int numOfStopCriterionMeasure) {
        String pNum = "(" + String.valueOf(numOfParentStopCriterion) + ")";
        String mNum = "(" + String.valueOf(numOfStopCriterionMeasure) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + stopCriterion + pNum + xmlSeparator
                + measure + mNum + "[@" + maximal + "]";
    }
    //*************

    //sub query
    public String textSubQueryStrategyType() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + subQueryStrategy + "[@" + type + "]";
    }

    public String textSubQueryWrapperClassifierType() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator
                + subQueryStrategy + xmlSeparator + wrapperClassifier
                + "[@" + type + "]";
    }

    public String textSubQueryClassifierTypeList() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + subQueryStrategy
                + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + "[@" + type + "]";
    }

    public String textSubQueryClassifierType(int numOfSubQueryClassifier) {
        String sNum = "(" + String.valueOf(numOfSubQueryClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + subQueryStrategy
                + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + sNum + "[@" + type + "]";
    }
    //****************

    //density diversity
    public String textQueryStrategyImportanceDensity() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + importanceDensity;
    }

    public String textQueryStrategyDistanceFunctionType() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + distanceFunction + "[@" + type + "]";
    }
    //******************

    //variance reduction
    public String textQueryStrategyEpsilon() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + epsilon;
    }
    
    public String textQueryStrategyMaxEpsilonIteration() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + maxEpsilonIteration;
    }

    public String textQueryStrategyFactorRegularization() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + factorRegularization;
    }

    public String textQueryStrategyMatrixFile() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + matrixFile;
    }
    //******************

    //sub - variance reduction
    public String textSubQueryStrategyEpsilon() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator + subQueryStrategy + xmlSeparator
                + epsilon;
    }
    
    public String textSubQueryStrategyMaxEpsilonIteration() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator + subQueryStrategy + xmlSeparator
                + maxEpsilonIteration;
    }

    public String textSubQueryStrategyFactorRegularization() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator + subQueryStrategy + xmlSeparator
                + factorRegularization;
    }

    public String textSubQueryStrategyMatrixFile() {
        return process + xmlSeparator + algorithm
                + xmlSeparator + scenario + xmlSeparator
                + queryStrategy + xmlSeparator + subQueryStrategy + xmlSeparator
                + matrixFile;
    }
    //******************
    //multi-label***********

    public String textMultiLabel() {
        return process + xmlSeparator + multiLabel;
    }

    public String textFileXml() {
        return process + xmlSeparator + fileXml;
    }

    public String textBaseClassifierTypeList(int numOfParentClassifier) {
        String pNum = "(" + String.valueOf(numOfParentClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + pNum + xmlSeparator
                + baseClassifier + "[@" + type + "]";
    }

    public String textBaseClassifierType(int numOfParentClassifier, int numOfBaseClassifier) {
        String pNum = "(" + String.valueOf(numOfParentClassifier) + ")";
        String bNum = "(" + String.valueOf(numOfBaseClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + pNum + xmlSeparator + baseClassifier
                + bNum + "[@" + type + "]";
    }

    public String textSubQueryBaseClassifierTypeList(int numOfParentClassifier) {
        String pNum = "(" + String.valueOf(numOfParentClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + subQueryStrategy
                + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + pNum + xmlSeparator
                + baseClassifier + "[@" + type + "]";
    }

    public String textSubQueryBaseClassifierType(int numOfParentClassifier, int numOfBaseClassifier) {
        String pNum = "(" + String.valueOf(numOfParentClassifier) + ")";
        String bNum = "(" + String.valueOf(numOfBaseClassifier) + ")";
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator + queryStrategy + xmlSeparator + subQueryStrategy
                + xmlSeparator + wrapperClassifier
                + xmlSeparator + classifier + pNum + xmlSeparator + baseClassifier
                + bNum + "[@" + type + "]";
    }

    //in query strategy
    public String textQueryStrategyEvidenceDimension() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + evidenceDimension;
    }

    public String textQueryStrategyClassDimension() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + classDimension;
    }

    public String textQueryStrategyWeightDimension() {
        return process + xmlSeparator + algorithm + xmlSeparator
                + scenario + xmlSeparator
                + queryStrategy + xmlSeparator
                + weightDimension;
    }
    //*******************************************
}
