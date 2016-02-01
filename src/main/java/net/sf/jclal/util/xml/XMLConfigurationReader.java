/*
 * Copyright (C)
 *
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
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

/**
 * Utility class to read a xml configuration file.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class XMLConfigurationReader extends AbstractXMLConfiguration {

    /**
     * Empty constructor
     */
    public XMLConfigurationReader() {
    }

    /**
     * Constructor
     *
     * @param filePath The file path of the xml configuration.
     * @throws Exception The exception that will be launched.
     * 
     */
    public XMLConfigurationReader(String filePath)
            throws Exception {
        file = new File(filePath);

        initReader();
    }

    /**
     * Constructor
     * 
     * @param file The file path of the xml configuration.
     * @throws Exception The exception that will be launched
     */
    public XMLConfigurationReader(File file)
            throws Exception {
        this.file = file.getAbsoluteFile();

        initReader();
    }

    private void initReader() {
        experiment = new DefaultConfigurationBuilder(file);
    }

    /**
     * Load a XML file
     *
     * @throws ConfigurationException The exception that will be launched
     */
    public void loadXmlFile() throws ConfigurationException {
        experiment.load();
    }

    /**
     * Load a XML file
     *
     * @param newXml New file destination of the xml configuration.
     * @throws ConfigurationException The exception that will be launched
     */
    public void loadXmlFile(File newXml) throws ConfigurationException, Exception {
        file = newXml;
        loadXmlFile();
    }

    //*************************Definition of elements*****************
    public String readEvaluationMethodType() {
        return experiment.getString(textEvaluationMethodType());
    }

    public String readAlgorithmType() {
        return experiment.getString(textAlgorithmType());
    }

    //Rand-factory
    public String readRandFactoryType() {
        return experiment.getString(textRandFactoryType());
    }

    public String readRandFactorySeed() {
        return experiment.getString(textRandFactorySeed());
    }
    //************

    //files
    public String readFileDataset() {
        return experiment.getString(textFileDataset());
    }

    public String readFileTrain() {
        return experiment.getString(textFileTrain());
    }

    public String readFileTest() {
        return experiment.getString(textFileTest());
    }

    public String readFileLabeled() {
        return experiment.getString(textFileLabeled());
    }

    public String readFileUnlabeled() {
        return experiment.getString(textFileUnlabeled());
    }

    public String readClassAttribute() {
        return experiment.getString(textClassAttribute());
    }
    //**************

    //Listener
    public List readListenerTypeList() {
        return experiment.getList(textListenerTypeList());
    }

    public String readListenerType(int numOfListener) {
        return experiment.getString(textListenerType(numOfListener));
    }

    public String readListenerReportFrequency(int numOfListener) {
        return experiment.getString(textListenerReportFrequency(numOfListener));
    }

    public String readListenerReportOnFile(int numOfListener) {
        return experiment.getString(textListenerReportOnFile(numOfListener));
    }

    public String readListenerReportOnConsole(int numOfListener) {
        return experiment.getString(textListenerReportOnConsole(numOfListener));
    }

    public String readListenerReportTitle(int numOfListener) {
        return experiment.getString(textListenerReportTitle(numOfListener));
    }
    
    public String readListenerReportDirectory(int numOfListener) {
        return experiment.getString(textListenerReportDirectory(numOfListener));
    }

    public String readListenerShowWindow(int numOfListener) {
        return experiment.getString(textListenerShowWindow(numOfListener));
    }

    public String readListenerShowPassiveLearning(int numOfListener) {
        return experiment.getString(textListenerShowPassiveLearning(numOfListener));
    }
    //************

    //send mail
    public String readListenerSendMailType(int numOfListener) {
        return experiment.getString(textListenerSendMailType(numOfListener));
    }

    public String readListenerSendMailSmtpHost(int numOfListener) {
        return experiment.getString(textListenerSendMailSmtpHost(numOfListener));
    }

    public String readListenerSendMailSmtpPort(int numOfListener) {
        return experiment.getString(textListenerSendMailSmtpPort(numOfListener));
    }

    public List readListenerSendMailToList(int numOfParentListener) {
        return experiment.getList(textListenerSendMailToList(numOfParentListener));
    }
    
    public String readListenerSendMailTo(int numOfParentListener, int numOfRecipient) {
        return experiment.getString(textListenerSendMailTo(numOfParentListener, numOfRecipient));
    }

    public String readListenerSendMailFrom(int numOfListener) {
        return experiment.getString(textListenerSendMailFrom(numOfListener));
    }

    public String readListenerSendMailAttachReportFile(int numOfListener) {
        return experiment.getString(textListenerSendMailAttachReportFile(numOfListener));
    }

    public String readListenerSendMailUser(int numOfListener) {
        return experiment.getString(textListenerSendMailUser(numOfListener));
    }

    public String readListenerSendMailPassword(int numOfListener) {
        return experiment.getString(textListenerSendMailPassword(numOfListener));
    }
    //***********

    public String readMaxIteration() {
        return experiment.getString(textMaxIteration());
    }

    //HoldOut method
    public String readPercentageSplit() {
        return experiment.getString(textPercentageSplit());
    }

    //kFold method
    public String readStratity() {
        return experiment.getString(textStratity());
    }

    public String readNumFolds() {
        return experiment.getString(textNumFolds());
    }
    //***********

    //sampling
    public String readSamplingType() {
        return experiment.getString(textSamplingType());
    }

    public String readSamplingPercentageToSelect() {
        return experiment.getString(textSamplingPercentageToSelect());
    }

    public String readSamplingNoReplacement() {
        return experiment.getString(textSamplingNoReplacement());
    }

    public String readSamplingInvertSelection() {
        return experiment.getString(textSamplingInvertSelection());
    }

    public String readSamplingBiasToUniformClass() {
        return experiment.getString(textSamplingBiasToUniformClass());
    }
    //**********

    //scenario
    public String readScenarioType() {
        return experiment.getString(textScenarioType());
    }

    public String readScenarioStreamThreshold() {
        return experiment.getString(textScenarioStreamThreshold());
    }

    //batch-mode
    public String readBatchModeType() {
        return experiment.getString(textBatchModeType());
    }

    public String readBatchSize() {
        return experiment.getString(textBatchSize());
    }

    //query strategy
    public String readQueryStrategyType() {
        return experiment.getString(textQueryStrategyType());
    }

    public String readWrapperClassifierType() {
        return experiment.getString(textWrapperClassifierType());
    }

    public List readClassifierTypeList() {
        return experiment.getList(textClassifierTypeList());
    }
    
    public String readClassifierType(int numOfClassifier) {
        return experiment.getString(textClassifierType(numOfClassifier));
    }

    //oracle
    public String readOracleType() {
        return experiment.getString(textOracleType());
    }

    //stop criterion
    public List readStopCriterionTypeList() {
        return experiment.getList(textStopCriterionTypeList());
    }
    
    public String readStopCriterionType(int numOfStopCriterion) {
        return experiment.getString(textStopCriterionType(numOfStopCriterion));
    }

    public String readStopCriterionDisjunctionForm(int numOfStopCriterion) {
        return experiment.getString(textStopCriterionDisjunctionForm(numOfStopCriterion));
    }

    public List readStopCriterionMeasureNameList(int numOfParentStopCriterion) {
        return experiment.getList(textStopCriterionMeasureNameList(numOfParentStopCriterion));
    }
    
    public String readStopCriterionMeasureName(int numOfParentStopCriterion, int numOfStopCriterionMeasure) {
        return experiment.getString(textStopCriterionMeasureName(numOfParentStopCriterion,
                numOfStopCriterionMeasure));
    }

    public String readStopCriterionMeasureMaximal(int numOfParentStopCriterion, int numOfStopCriterionMeasure) {
        return experiment.getString(textStopCriterionMeasureMaximal(numOfParentStopCriterion,
                numOfStopCriterionMeasure));
    }
    //*************

    //sub query
    public String readSubQueryStrategyType() {
        return experiment.getString(textSubQueryStrategyType());
    }

    public String readSubQueryWrapperClassifierType() {
        return experiment.getString(textSubQueryWrapperClassifierType());
    }

    public List readSubQueryClassifierTypeList() {
        return experiment.getList(textSubQueryClassifierTypeList());
    }
    
    public String readSubQueryClassifierType(int numOfSubQueryClassifier) {
        return experiment.getString(textSubQueryClassifierType(numOfSubQueryClassifier));
    }
    //****************

    //density diversity
    public String readQueryStrategyImportanceDensity() {
        return experiment.getString(textQueryStrategyImportanceDensity());
    }

    public String readQueryStrategyDistanceFunctionType() {
        return experiment.getString(textQueryStrategyDistanceFunctionType());
    }
    //******************

    //variance reduction
    public String readQueryStrategyEpsilon() {
        return experiment.getString(textQueryStrategyEpsilon());
    }
    
    public String readQueryStrategyMaxEpsilonIteration() {
        return experiment.getString(textQueryStrategyMaxEpsilonIteration());
    }

    public String readQueryStrategyFactorRegularization() {
        return experiment.getString(textQueryStrategyFactorRegularization());
    }
    
    public String readQueryStrategyMatrixFile() {
        return experiment.getString(textQueryStrategyMatrixFile());
    }
    //******************

    //sub - variance reduction
    public String readSubQueryStrategyEpsilon() {
        return experiment.getString(textSubQueryStrategyEpsilon());
    }
    
    public String readSubQueryStrategyMaxEpsilonIteration() {
        return experiment.getString(textSubQueryStrategyMaxEpsilonIteration());
    }

    public String readSubQueryStrategyFactorRegularization() {
        return experiment.getString(textSubQueryStrategyFactorRegularization());
    }
    
    public String readSubQueryStrategyMatrixFile() {
        return experiment.getString(textSubQueryStrategyMatrixFile());
    }
    //******************

    //multi-label***********
    /**
     * Used in multi-label return experiments.
     *
     *@return A string
     */
    public String readMultiLabel() {
        return experiment.getString(textMultiLabel());
    }

    /**
     * Used in multi-label return experiments.
     * @return A xml file as string
     */
    public String readFileXml() {
        return experiment.getString(textFileXml());
    }

    /**
     * Used in multi-label return experiments.
     * @param numOfParentClassifier The number of Parent Classifier
     * @return A list
     */
    public List readBaseClassifierTypeList(int numOfParentClassifier) {
        return experiment.getList(textBaseClassifierTypeList(numOfParentClassifier));
    }
    
    /**
     * Used in multi-label return experiments.
     * @param numOfParentClassifier The number of parent classifier
     * @param numOfBaseClassifier  The number of base classifier
     * @return The base classifier as string
     *
     */
    public String readBaseClassifierType(int numOfParentClassifier, int numOfBaseClassifier) {
        return experiment.getString(textBaseClassifierType(numOfParentClassifier, numOfBaseClassifier));
    }
    
    /**
     * Used in multi-label return experiments.
     * @param numOfParentClassifier The number of parent classifier
     * @return A list
     */
    public List readSubQueryBaseClassifierTypeList(int numOfParentClassifier) {
        return experiment.getList(textSubQueryBaseClassifierTypeList(numOfParentClassifier));
    }
    
    /**
     * Used in multi-label return experiments.
     * 
     * @param numOfParentClassifier The number of parent classifiers
     * @param numOfBaseClassifier The number of base classifiers
     * 
     * @return A string
     *
     */
    public String readSubQueryBaseClassifierType(int numOfParentClassifier, int numOfBaseClassifier) {
        return experiment.getString(textSubQueryBaseClassifierType(numOfParentClassifier, numOfBaseClassifier));
    }

    /**
     * Used in multi-label return experiments.
     *
     *@return The evidence strategy used
     */
    public String readQueryStrategyEvidenceDimension() {
        return experiment.getString(textQueryStrategyEvidenceDimension());
    }

    /**
     * Used in multi-label return experiments.
     * 
     * @return The class dimension used
     *
     */
    public String readQueryStrategyClassDimension() {
        return experiment.getString(textQueryStrategyClassDimension());
    }

    /**
     * Used in multi-label return experiments.
     * 
     * @return The weight dimension used
     *
     */
    public String readQueryStrategyWeightDimension() {
        return experiment.getString(textQueryStrategyWeightDimension());
    }
    //*******************************************
}
