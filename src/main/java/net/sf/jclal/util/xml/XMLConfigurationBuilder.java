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
import net.sf.jclal.core.IXMLConfigurationBuilder;
import net.sf.jclal.util.file.FileUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

/**
 * Utility class to construct a xml configuration file to execute an experiment.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class XMLConfigurationBuilder extends AbstractXMLConfiguration implements IXMLConfigurationBuilder {

	/**
	 * Empty constructor
	 */
	public XMLConfigurationBuilder() {
		initBuilder();
	}

	/**
	 * Constructor
	 * 
	 * @param filePath
	 *            The file path of the xml configuration.
	 * @param replace
	 *            If the file exists, replace with the new one.
	 * @throws java.lang.Exception
	 *             The exception that will be launched
	 */
	public XMLConfigurationBuilder(String filePath, boolean replace) throws Exception {
		file = new File(filePath);

		FileUtil.createFile(file, replace);

		initBuilder();
	}

	/**
	 * Constructor
	 *
	 * @param file
	 *            The file path of the xml configuration.
	 * @param replace
	 *            If the file exists, replace with the new one.
	 * @throws Exception
	 *             The exception that will be launched
	 */
	public XMLConfigurationBuilder(File file, boolean replace) throws Exception {
		this.file = file.getAbsoluteFile();

		FileUtil.createFile(file, replace);

		initBuilder();
	}

	private void initBuilder() {
		experiment = new DefaultConfigurationBuilder();
		experiment.setRootElementName(rootElementName);
	}

	/**
	 * Write the XML file
	 *
	 * @throws ConfigurationException
	 *             The exception that will be launched
	 */
	@Override
	public void writeXmlFile() throws ConfigurationException {
		experiment.save(file);
	}

	/**
	 * Write the XML file
	 * 
	 * @param newXml
	 *            New file destination of the xml configuration.
	 * @param replace
	 *            If the file exists, replace with the new one.
	 * @throws ConfigurationException
	 *             The exception that will be launched
	 */
	@Override
	public void writeXmlFile(File newXml, boolean replace) throws ConfigurationException, Exception {
		file = newXml;
		FileUtil.createFile(file, replace);
		writeXmlFile();
	}

	// *************************Definition of elements*****************
	public void defineEvaluationMethodType(String newEvaluationMethodType) {
		experiment.setProperty(textEvaluationMethodType(), newEvaluationMethodType);
	}

	public void defineAlgorithmType(String newAlgorithmType) {
		experiment.setProperty(textAlgorithmType(), newAlgorithmType);
	}

	// Rand-factory
	public void defineRandFactoryType(String newRandFactoryType) {
		experiment.setProperty(textRandFactoryType(), newRandFactoryType);
	}

	public void defineRandFactorySeed(String newRandFactorySeed) {
		experiment.setProperty(textRandFactorySeed(), newRandFactorySeed);
	}
	// ************

	// files
	public void defineFileDataset(String newFileDataset) {
		experiment.setProperty(textFileDataset(), newFileDataset);
	}

	public void defineFileTrain(String newFileTrain) {
		experiment.setProperty(textFileTrain(), newFileTrain);
	}

	public void defineFileTest(String newFileTest) {
		experiment.setProperty(textFileTest(), newFileTest);
	}

	public void defineFileLabeled(String newFileLabeled) {
		experiment.setProperty(textFileLabeled(), newFileLabeled);
	}

	public void defineFileUnlabeled(String newFileUnlabeled) {
		experiment.setProperty(textFileUnlabeled(), newFileUnlabeled);
	}

	public void defineClassAttribute(int newClassAttribute) {
		experiment.setProperty(textClassAttribute(), newClassAttribute);
	}
	// **************

	// Listener
	public void defineListenerType(String newListenerType, int numOfListener) {
		experiment.setProperty(textListenerType(numOfListener), newListenerType);
	}

	public void defineListenerReportFrequency(int newListenerReportFrequency, int numOfListener) {
		experiment.setProperty(textListenerReportFrequency(numOfListener), newListenerReportFrequency);
	}

	public void defineListenerReportOnFile(boolean newListenerReportOnFile, int numOfListener) {
		experiment.setProperty(textListenerReportOnFile(numOfListener), newListenerReportOnFile);
	}

	public void defineListenerReportOnConsole(boolean newListenerReportOnConsole, int numOfListener) {
		experiment.setProperty(textListenerReportOnConsole(numOfListener), newListenerReportOnConsole);
	}

	public void defineListenerReportTitle(String newListenerReportTitle, int numOfListener) {
		experiment.setProperty(textListenerReportTitle(numOfListener), newListenerReportTitle);
	}

	public void defineListenerReportDirectory(String newListenerReportDirectory, int numOfListener) {
		experiment.setProperty(textListenerReportDirectory(numOfListener), newListenerReportDirectory);
	}

	public void defineListenerShowWindow(boolean newListenerShowWindow, int numOfListener) {
		experiment.setProperty(textListenerShowWindow(numOfListener), newListenerShowWindow);
	}

	public void defineListenerShowPassiveLearning(boolean newListenerShowPassiveLearning, int numOfListener) {
		experiment.setProperty(textListenerShowPassiveLearning(numOfListener), newListenerShowPassiveLearning);
	}
	// ************

	// send mail
	public void defineListenerSendMailType(String newListenerSendMailType, int numOfListener) {
		experiment.setProperty(textListenerSendMailType(numOfListener), newListenerSendMailType);
	}

	public void defineListenerSendMailSmtpHost(String newListenerSendMailSmtpHost, int numOfListener) {
		experiment.setProperty(textListenerSendMailSmtpHost(numOfListener), newListenerSendMailSmtpHost);
	}

	public void defineListenerSendMailSmtpPort(int newListenerSendMailSmtpPort, int numOfListener) {
		experiment.setProperty(textListenerSendMailSmtpPort(numOfListener), newListenerSendMailSmtpPort);
	}

	public void defineListenerSendMailTo(String newListenerSendMailTo, int numOfParentListener, int numOfRecipient) {
		experiment.setProperty(textListenerSendMailTo(numOfParentListener, numOfRecipient), newListenerSendMailTo);
	}

	public void defineListenerSendMailFrom(String newListenerSendMailFrom, int numOfListener) {
		experiment.setProperty(textListenerSendMailFrom(numOfListener), newListenerSendMailFrom);
	}

	public void defineListenerSendMailAttachReportFile(boolean newListenerSendMailAttachReportFile, int numOfListener) {
		experiment.setProperty(textListenerSendMailAttachReportFile(numOfListener),
				newListenerSendMailAttachReportFile);
	}

	public void defineListenerSendMailUser(String newListenerSendMailUser, int numOfListener) {
		experiment.setProperty(textListenerSendMailUser(numOfListener), newListenerSendMailUser);
	}

	public void defineListenerSendMailPassword(String newListenerSendMailPassword, int numOfListener) {
		experiment.setProperty(textListenerSendMailPassword(numOfListener), newListenerSendMailPassword);
	}
	// ***********

	public void defineMaxIteration(int newMaxIteration) {
		experiment.setProperty(textMaxIteration(), newMaxIteration);
	}

	// HoldOut method
	public void definePercentageSplit(double newPercentageSplit) {
		experiment.setProperty(textPercentageSplit(), newPercentageSplit);
	}

	// kFold method
	public void defineStratity(boolean newSetStratify) {
		experiment.setProperty(textStratity(), newSetStratify);
	}

	public void defineNumFolds(int newNumFolds) {
		experiment.setProperty(textNumFolds(), newNumFolds);
	}
	// ***********

	// sampling
	public void defineSamplingType(String newSamplingType) {
		experiment.setProperty(textSamplingType(), newSamplingType);
	}

	public void defineSamplingPercentageToSelect(double newSamplingPercentageToSelect) {
		experiment.setProperty(textSamplingPercentageToSelect(), newSamplingPercentageToSelect);
	}

	public void defineSamplingNoReplacement(boolean newSamplingNoReplacement) {
		experiment.setProperty(textSamplingNoReplacement(), newSamplingNoReplacement);
	}

	public void defineSamplingInvertSelection(boolean newSamplingInvertSelection) {
		experiment.setProperty(textSamplingInvertSelection(), newSamplingInvertSelection);
	}

	public void defineSamplingBiasToUniformClass(double newSamplingBiasToUniformClass) {
		experiment.setProperty(textSamplingBiasToUniformClass(), newSamplingBiasToUniformClass);
	}
	// **********

	// scenario
	public void defineScenarioType(String newScenarioType) {
		experiment.setProperty(textScenarioType(), newScenarioType);
	}

	public void defineScenarioStreamThreshold(double newScenarioStreamThreshold) {
		experiment.setProperty(textScenarioStreamThreshold(), newScenarioStreamThreshold);
	}

	// batch-mode
	public void defineBatchModeType(String newBatchModeType) {
		experiment.setProperty(textBatchModeType(), newBatchModeType);
	}

	public void defineBatchSize(int newBatchSize) {
		experiment.setProperty(textBatchSize(), newBatchSize);
	}

	// query strategy
	public void defineQueryStrategyType(String newQueryStrategyType) {
		experiment.setProperty(textQueryStrategyType(), newQueryStrategyType);
	}

	public void defineWrapperClassifierType(String newWrapperClassifierType) {
		experiment.setProperty(textWrapperClassifierType(), newWrapperClassifierType);
	}

	public void defineClassifierType(String newClassifierType, int numOfClassifier) {
		experiment.setProperty(textClassifierType(numOfClassifier), newClassifierType);
	}

	// oracle
	public void defineOracleType(String newOracleType) {
		experiment.setProperty(textOracleType(), newOracleType);
	}

	// stop criterion
	public void defineStopCriterionType(String newStopCriterionType, int numOfStopCriterion) {
		experiment.setProperty(textStopCriterionType(numOfStopCriterion), newStopCriterionType);
	}

	public void defineStopCriterionDisjunctionForm(boolean newStopCriterionDisjunctionForm, int numOfStopCriterion) {
		experiment.setProperty(textStopCriterionDisjunctionForm(numOfStopCriterion), newStopCriterionDisjunctionForm);
	}

	public void defineStopCriterionMeasureName(String newStopCriterionMeasureName, int numOfParentStopCriterion,
			int numOfStopCriterionMeasure) {
		experiment.setProperty(textStopCriterionMeasureName(numOfParentStopCriterion, numOfStopCriterionMeasure),
				newStopCriterionMeasureName);
	}

	public void defineStopCriterionMeasureMaximal(boolean newStopCriterionMeasureMaximal, int numOfParentStopCriterion,
			int numOfStopCriterionMeasure) {
		experiment.setProperty(textStopCriterionMeasureMaximal(numOfParentStopCriterion, numOfStopCriterionMeasure),
				newStopCriterionMeasureMaximal);
	}
	// *************

	// sub query
	public void defineSubQueryStrategyType(String newSubQueryStrategyType) {
		experiment.setProperty(textSubQueryStrategyType(), newSubQueryStrategyType);
	}

	public void defineSubQueryWrapperClassifierType(String newSubQueryWrapperClassifierType) {
		experiment.setProperty(textSubQueryWrapperClassifierType(), newSubQueryWrapperClassifierType);
	}

	public void defineSubQueryClassifierType(String newSubQueryClassifierType, int numOfSubQueryClassifier) {
		experiment.setProperty(textSubQueryClassifierType(numOfSubQueryClassifier), newSubQueryClassifierType);
	}
	// ****************

	// density diversity
	public void defineQueryStrategyImportanceDensity(double newQueryImportanceDensity) {
		experiment.setProperty(textQueryStrategyImportanceDensity(), newQueryImportanceDensity);
	}

	public void defineQueryStrategyDistanceFunctionType(String newQueryStrategyDistanceFunctionType) {
		experiment.setProperty(textQueryStrategyDistanceFunctionType(), newQueryStrategyDistanceFunctionType);
	}
	// ******************

	// variance reduction
	public void defineQueryStrategyEpsilon(double newQueryEpsilon) {
		experiment.setProperty(textQueryStrategyEpsilon(), newQueryEpsilon);
	}

	public void defineQueryStrategyMaxEpsilonIteration(int newQueryMaxEpsilonIteration) {
		experiment.setProperty(textQueryStrategyMaxEpsilonIteration(), newQueryMaxEpsilonIteration);
	}

	public void defineQueryStrategyFactorRegularization(double newQueryStrategyFactorRegularization) {
		experiment.setProperty(textQueryStrategyFactorRegularization(), newQueryStrategyFactorRegularization);
	}

	public void defineQueryStrategyMatrixFile(boolean newQueryStrategyMatrixFile) {
		experiment.setProperty(textQueryStrategyMatrixFile(), newQueryStrategyMatrixFile);
	}
	// ******************

	// sub - variance reduction
	public void defineSubQueryStrategyEpsilon(double newSubQueryEpsilon) {
		experiment.setProperty(textSubQueryStrategyEpsilon(), newSubQueryEpsilon);
	}

	public void defineSubQueryStrategyMaxEpsilonIteration(int newSubQueryMaxEpsilonIteration) {
		experiment.setProperty(textSubQueryStrategyMaxEpsilonIteration(), newSubQueryMaxEpsilonIteration);
	}

	public void defineSubQueryStrategyFactorRegularization(double newSubQueryStrategyFactorRegularization) {
		experiment.setProperty(textSubQueryStrategyFactorRegularization(), newSubQueryStrategyFactorRegularization);
	}

	public void defineSubQueryStrategyMatrixFile(boolean newSubQueryStrategyMatrixFile) {
		experiment.setProperty(textSubQueryStrategyMatrixFile(), newSubQueryStrategyMatrixFile);
	}
	// ******************

	// multi-label***********
	/**
	 * Used in multi-label experiments.
	 *
	 * @param newMultiLabel
	 *            Flag that indicates if the dataset is multi-label
	 *
	 */
	public void defineMultiLabel(boolean newMultiLabel) {
		experiment.setProperty(textMultiLabel(), newMultiLabel);
	}

	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newFileXml
	 *            The file name
	 *
	 */
	public void defineFileXml(String newFileXml) {
		experiment.setProperty(textFileXml(), newFileXml);
	}

	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newBaseClassifierType
	 *            The base classifier to use
	 * @param numOfParentClassifier
	 *            The number of classifier to use
	 * @param numOfBaseClassifier
	 *            The number of base classifier to use
	 *
	 */
	public void defineBaseClassifierType(String newBaseClassifierType, int numOfParentClassifier,
			int numOfBaseClassifier) {
		experiment.setProperty(textBaseClassifierType(numOfParentClassifier, numOfBaseClassifier),
				newBaseClassifierType);
	}

	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newSubQueryBaseClassifierType
	 *            The base classifier to use
	 * @param numOfParentClassifier
	 *            The number of classifier to use
	 * @param numOfBaseClassifier
	 *            The number of base classifier to use
	 *
	 */
	public void defineSubQueryBaseClassifierType(String newSubQueryBaseClassifierType, int numOfParentClassifier,
			int numOfBaseClassifier) {
		experiment.setProperty(textSubQueryBaseClassifierType(numOfParentClassifier, numOfBaseClassifier),
				newSubQueryBaseClassifierType);
	}

	// in query strategy
	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newQueryStrategyEvidenceDimension
	 *            The evidence dimension to use
	 *
	 */
	public void defineQueryStrategyEvidenceDimension(char newQueryStrategyEvidenceDimension) {
		experiment.setProperty(textQueryStrategyEvidenceDimension(), newQueryStrategyEvidenceDimension);
	}

	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newQueryStrategyClassDimension
	 *            The class dimension to use
	 *
	 */
	public void defineQueryStrategyClassDimension(char newQueryStrategyClassDimension) {
		experiment.setProperty(textQueryStrategyClassDimension(), newQueryStrategyClassDimension);
	}

	/**
	 * Used in multi-label experiments.
	 * 
	 * @param newQueryStrategyWeightDimension
	 *            The Weight dimension to use
	 *
	 */
	public void defineQueryStrategyWeightDimension(char newQueryStrategyWeightDimension) {
		experiment.setProperty(textQueryStrategyWeightDimension(), newQueryStrategyWeightDimension);
	}
	// *******************************************
}
