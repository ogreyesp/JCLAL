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
package net.sf.jclal.gui.view.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import net.sf.jclal.experiment.Experiment;
import net.sf.jclal.experiment.ExperimentBuilder;
import net.sf.jclal.experiment.ExperimentThread;
import net.sf.jclal.util.gui.UtilsSwing;
import net.sf.jclal.util.xml.XMLConfigurationBuilder;
import net.sf.jclal.util.xml.XMLConfigurationReader;
import net.sf.jclal.util.xml.XmlFormat;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.tabbedui.VerticalLayout;

/**
 * Visual Component to create xml configuration files.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class GUIXml extends JFrame {

	private static final long serialVersionUID = -524114449535595618L;
	protected JPanel content1;
    protected JPanel content2;
    protected JPanel content3;

    protected String windowsTitle;
    protected JMenuBar menubar;

    protected JButton filesButton;

    protected JComboBox evaluationMethodTypeCombo;
    protected JComboBox randGenFactoryTypeCombo;
    protected JComboBox samplingMethodTypeCombo;
    protected JComboBox algorithmTypeCombo;
    protected JComboBox scenarioTypeCombo;
    protected JComboBox batchModeTypeCombo;
    protected JComboBox oracleTypeCombo;
    protected JComboBox queryTypeCombo;
    protected JComboBox subQueryTypeCombo;
    protected JComboBox wrapperClassifierTypeCombo;

    protected JComboBox<ListenerContainer> listenerCombo;
    protected JComboBox<StopCriterionContainer> stopCriterionCombo;

    protected JTextField seedEntry;
    protected JTextField fileDatasetEntry;
    protected JTextField fileTrainEntry;
    protected JTextField fileTestEntry;
    protected JTextField fileLabeledEntry;
    protected JTextField fileUnLabeledEntry;
    protected JTextField fileXmlEntry;
    protected JTextField classAttribute;
    protected JTextField percentageSplit;
    protected JTextField percentageSelect;
    protected JTextField m_BiasToUniformClass;
    protected JTextField maxIteration;
    protected JTextField batchSize;
    protected JTextField threshold;
    protected JTextField numFoldText;

    protected JCheckBox stratify;
    protected JCheckBox no_replacement;
    protected JCheckBox invert_selection;
    protected JCheckBox subQueryCheck;
    protected JCheckBox multiLabel;

    protected List<JDialog> all = new LinkedList<JDialog>();
    protected JDialog filesFrame;
    protected JDialog randFrame;
    protected JDialog samplingFrame;
    protected JDialog algorithmFrame;
    protected JDialog listenerFrame;
    protected JDialog stopCriterionFrame;
    protected JDialog scenarioFrame;
    protected JDialog batchModeFrame;
    protected JDialog oracleFrame;
    protected JDialog queryFrame;
    protected JDialog wrapperFrame;
    protected JDialog subQueryFrame;
    protected JDialog aboutFrame;
    protected JDialog runFrame;

    protected JPanel classifierContainerPanel;
    protected QueryElements queryPanel;
    protected QueryElements subQueryPanel;

    protected int countListener = 1;
    protected int countStopCriterion = 1;
    protected JButton addListener;
    protected JButton addStopCriterion;
    protected JButton subQueryButton;

    protected JButton runExperiment;
    protected JButton viewExperiment;
    protected boolean run = true;
    protected JTable runTable;
    protected LinkedList<ExperimentThreadTable> runExperiments = new LinkedList<ExperimentThreadTable>();

    protected boolean filesEnable = true;
    protected boolean classAttributeEnable = true;

    /**
     * It establishes that the configuration window.
     * 
     * @param x the flag.
     */
    public void setRunExperiment(boolean x) {
        run = x;
        runExperiment.setEnabled(run);
        viewExperiment.setEnabled(run);
    }

    /**
     *
     * @param windowsTitleParam The title of the window.
     */
    public GUIXml(String windowsTitleParam) {

        menubar = new JMenuBar();
        menubar.add(createMenu());
        menubar.add(createMenuHelp());

        if (windowsTitleParam == null || windowsTitleParam.isEmpty()) {
            windowsTitle = "XML Configuration";
        } else {
            windowsTitle = windowsTitleParam;
        }

        setJMenuBar(menubar);
        setTitle(windowsTitle);
        setContentPane(initContentPanel());

        initRunFrame();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GUIXml.this.addWindowListener(new WindowAdapter() {

            @Override
            public synchronized void windowClosing(WindowEvent e) {
                for (ExperimentThreadTable experiment : runExperiments) {
                    if (experiment.isAlive()) {
                        //do something
                    }
                }
            }
        });
    }

    protected void closeAll() {
        for (JDialog jFrame : all) {
            jFrame.dispose();
        }

        for (int i = 0; i < listenerCombo.getItemCount(); i++) {
            listenerCombo.getItemAt(i).destroy();
        }

        for (int i = 0; i < stopCriterionCombo.getItemCount(); i++) {
            stopCriterionCombo.getItemAt(i).destroy();
        }
    }

    protected JPanel initContentPanel() {
        JPanel content = new JPanel(new BorderLayout());

        content1 = new JPanel(new VerticalLayout());
        content2 = new JPanel(new VerticalLayout());
        content3 = new JPanel(new VerticalLayout());

        JPanel temp1 = new JPanel(new BorderLayout());
        temp1.add(content1, BorderLayout.WEST);
        temp1.add(content2, BorderLayout.EAST);

        content.add(content3, BorderLayout.NORTH);
        content.add(temp1, BorderLayout.CENTER);

        content.add(runExperiment(), BorderLayout.SOUTH);

        content1.add(randGenFactory());
        content1.add(files());

        content2.add(samplingMethod());
        content2.add(algorithm());

        content3.add(evaluationMethod());

        initRandFrame();
        initFilesFrame();
        initSamplingMethodFrame();
        initAlgorithmFrame();
        initListenerFrame();
        initStopCriterionFrame();
        initScenarioFrame();
        initBatchModeFrame();
        initOracleFrame();
        initQueryFrame();
        initWrapperFrame();
        initSubQueryFrame();
        initAboutFrame();

        all.add(randFrame);
        all.add(filesFrame);
        all.add(samplingFrame);
        all.add(algorithmFrame);
        all.add(listenerFrame);
        all.add(stopCriterionFrame);
        all.add(scenarioFrame);
        all.add(batchModeFrame);
        all.add(oracleFrame);
        all.add(queryFrame);
        all.add(wrapperFrame);
        all.add(subQueryFrame);
        all.add(aboutFrame);

        return content;
    }

    protected void packAll() {
        for (JDialog jFrame : all) {
            UtilsSwing.initFrame(jFrame);
        }

        GUIXml.this.pack();
    }

    /**
     *
     * @param args NOT IN USE.
     */
    public static void main(String[] args) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIXml.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIXml.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIXml.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIXml.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                GUIXml demo = new GUIXml("XML Configuration");

                Dimension e = demo.getLayout().preferredLayoutSize(demo.getContentPane());
                demo.setSize(e);

                demo.pack();
                RefineryUtilities.centerFrameOnScreen(demo);
                demo.setVisible(true);

            }
        });

    }

    protected void newXmlConfiguration() {
        countListener = 1;
        countStopCriterion = 1;

        GUIXml.this.closeAll();
        GUIXml.this.setContentPane(initContentPanel());
        System.gc();
        GUIXml.this.packAll();

        UtilsSwing.initFrame(GUIXml.this);
    }

    protected void validateFields() {
        GUIXml.this.setRunExperiment(run);
        GUIXml.this.enableClassAttributeField(classAttributeEnable);
        GUIXml.this.enableFileFields(filesEnable);
    }

    /**
     *
     * @return Create the menu
     */
    protected JMenu createMenu() {

        final JMenu fileMenu = new JMenu("Files");

        fileMenu.setToolTipText("The XML configurations from a file");

        fileMenu.add("New XML Configuration").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                newXmlConfiguration();
                validateFields();
            }
        }
        );

        final JFileChooser openXml = new JFileChooser();
        fileMenu.add("Open XML Configuration").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                int action = openXml.showOpenDialog(fileMenu);

                if (action == JFileChooser.APPROVE_OPTION) {
                    XMLConfigurationReader reader;
                    try {
                        reader = new XMLConfigurationReader(openXml.getSelectedFile());
                        reader.loadXmlFile();

                        newXmlConfiguration();

                        //to read a xml file, and add elements without appereance
                        //in the GUI interface
                        readEvaluationMethod(reader);
                        readRandomGenerator(reader);
                        readFiles(reader);
                        readClassAttribute(reader);
                        readPercentageSplit(reader);
                        readStratify(reader);
                        readSamplingMethod(reader);
                        readAlgorithm(reader);

                        validateFields();

                        packAll();
                    } catch (Exception ex) {
                        Logger.getLogger(GUIXml.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }

        });

        final JFileChooser saveXml = new JFileChooser();
        fileMenu.add("Save XML Configuration").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                saveXml.setApproveButtonText("Save");

                saveXml.setDialogTitle("Save as ...");

                int action = saveXml.showOpenDialog(fileMenu);

                if (action == JFileChooser.APPROVE_OPTION) {
                    File selected = saveXml.getSelectedFile();
                    boolean go = true;
                    if (selected.exists()) {
                        int option = JOptionPane.showConfirmDialog(saveXml, "The file selected already exists."
                                + "\nDo you want to replace this file?", "File exists", JOptionPane.OK_OPTION);
                        if (option != 0) {
                            go = false;
                        }
                    }
                    if (go) {
                        try {
                            if (!selected.exists()) {
                                if (!selected.getName().endsWith(".cfg")) {
                                    selected = new File(selected.getAbsolutePath() + ".cfg");
                                }
                            }
                            File temp = File.createTempFile("jclal_config", ".cfg");

                            writeCurrExperiment(temp);

                            XmlFormat.formatXmlFile(temp, selected, true, "\t");

                            temp.deleteOnExit();

                        } catch (Exception ex) {
                            Logger.getLogger(GUIXml.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        }
        );

        return fileMenu;
    }

    /**
     * Utilitarian method that allows to write in the allocated file the
     * configuration that is configured at present in the visual interface.
     *
     * @param temp The file to keep the configuration.
     * @throws Exception The exception that will be launched.
     */
    public void writeCurrExperiment(File temp) throws Exception {
        XMLConfigurationBuilder builder = new XMLConfigurationBuilder(temp, true);

        writeEvaluationMethod(builder);
        writeRandomGenerator(builder);
        writeFiles(builder);
        writeClassAttribute(builder);
        writePercentage(builder);
        writeStratify(builder);
        writeSamplingMethod(builder);
        writeAlgorithm(builder);

        builder.writeXmlFile();
    }

    protected JMenu createMenuHelp() {

        final JMenu helpMenu = new JMenu("Help");

        helpMenu.setToolTipText("The XML configurations from a file");

        helpMenu.add("About...").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                GUIXml.this.aboutFrame.setVisible(true);

            }
        }
        );

        return helpMenu;
    }

    //read-methods*********************************
    protected void readEvaluationMethod(XMLConfigurationReader reader) {
        String evaluationMethod = reader.readEvaluationMethodType();
        UtilsSwing.addComboElement(evaluationMethodTypeCombo, evaluationMethod);
    }

    protected void readRandomGenerator(XMLConfigurationReader reader) {
        String randomType = reader.readRandFactoryType();
        UtilsSwing.addComboElement(randGenFactoryTypeCombo, randomType);

        String seed = reader.readRandFactorySeed();
        UtilsSwing.initJTextField(seedEntry, seed);
    }

    protected void readFiles(XMLConfigurationReader reader) {
        String fileD = reader.readFileDataset();
        UtilsSwing.initJTextField(fileDatasetEntry, fileD);

        String fileT = reader.readFileTrain();
        UtilsSwing.initJTextField(fileTrainEntry, fileT);

        String fileTe = reader.readFileTest();
        UtilsSwing.initJTextField(fileTestEntry, fileTe);

        String fileL = reader.readFileLabeled();
        UtilsSwing.initJTextField(fileLabeledEntry, fileL);

        String fileU = reader.readFileUnlabeled();
        UtilsSwing.initJTextField(fileUnLabeledEntry, fileU);

        String fileX = reader.readFileXml();
        UtilsSwing.initJTextField(fileXmlEntry, fileX);
    }

    protected void readClassAttribute(XMLConfigurationReader reader) {
        String fileC = reader.readClassAttribute();
        UtilsSwing.initJTextField(classAttribute, fileC);

        String multi = reader.readMultiLabel();
        UtilsSwing.initJCheckBox(multiLabel, multi);

        classAttribute.setEnabled(!multiLabel.isSelected());
    }

    protected void readPercentageSplit(XMLConfigurationReader reader) {
        String fileP = reader.readPercentageSplit();
        UtilsSwing.initJTextField(percentageSplit, fileP);
    }

    protected void readStratify(XMLConfigurationReader reader) {
        String strat = reader.readStratity();
        UtilsSwing.initJCheckBox(stratify, strat);

        String numFold = reader.readNumFolds();
        UtilsSwing.initJTextField(numFoldText, numFold);
    }

    protected void readSamplingMethod(XMLConfigurationReader reader) {
        String samplingMethodType = reader.readSamplingType();
        UtilsSwing.addComboElement(samplingMethodTypeCombo, samplingMethodType);

        String percSelect = reader.readSamplingPercentageToSelect();
        UtilsSwing.initJTextField(percentageSelect, percSelect);

        String biasToUniform = reader.readSamplingBiasToUniformClass();
        UtilsSwing.initJTextField(m_BiasToUniformClass, biasToUniform);

        String no_remp = reader.readSamplingNoReplacement();
        no_replacement.setSelected(Boolean.parseBoolean(no_remp));

        String inver_selection = reader.readSamplingInvertSelection();
        invert_selection.setSelected(Boolean.parseBoolean(inver_selection));
    }

    protected void readAlgorithm(XMLConfigurationReader reader) {
        String algorithmType = reader.readAlgorithmType();
        UtilsSwing.addComboElement(algorithmTypeCombo, algorithmType);

        String maxI = reader.readMaxIteration();
        UtilsSwing.initJTextField(maxIteration, maxI);

        //listener
        List<String> listenerType = reader.readListenerTypeList();
        int pos = 0;
        for (String type : listenerType) {
            addListenerAction();
            ListenerContainer lisContainer = listenerCombo.getItemAt(listenerCombo.getItemCount() - 1);
            UtilsSwing.addComboElement(lisContainer.listenerTypeCombo, type);

            String title = reader.readListenerReportTitle(pos);
            UtilsSwing.initJTextField(lisContainer.reportTitle, title);

            String directory = reader.readListenerReportDirectory(pos);
            UtilsSwing.initJTextField(lisContainer.directory, directory);

            String frequency = reader.readListenerReportFrequency(pos);
            UtilsSwing.initJTextField(lisContainer.reportFrequency, frequency);

            String reportFile = reader.readListenerReportOnFile(pos);
            UtilsSwing.initJCheckBox(lisContainer.onFile, reportFile);

            String reportConsole = reader.readListenerReportOnConsole(pos);
            UtilsSwing.initJCheckBox(lisContainer.onConsole, reportConsole);

            String showW = reader.readListenerShowWindow(pos);
            UtilsSwing.initJCheckBox(lisContainer.showWindow, showW);

            String showPL = reader.readListenerShowPassiveLearning(pos);
            UtilsSwing.initJCheckBox(lisContainer.showPassiveLearning, showPL);

            //email
            String smtpHost = reader.readListenerSendMailSmtpHost(pos);
            UtilsSwing.initJTextField(lisContainer.smtpHostText, smtpHost);

            String smtpPort = reader.readListenerSendMailSmtpPort(pos);
            UtilsSwing.initJTextField(lisContainer.smtpPortText, smtpPort);

            String smtpFrom = reader.readListenerSendMailFrom(pos);
            UtilsSwing.initJTextField(lisContainer.fromText, smtpFrom);

            String attachFile = reader.readListenerSendMailAttachReportFile(pos);
            UtilsSwing.initJCheckBox(lisContainer.attachReportFile, attachFile);

            String user = reader.readListenerSendMailUser(pos);
            UtilsSwing.initJTextField(lisContainer.userText, user);

            String pass = reader.readListenerSendMailPassword(pos);
            UtilsSwing.initJTextField(lisContainer.passPass, pass);
            UtilsSwing.initJTextField(lisContainer.passCPass, pass);

            List<String> email = reader.readListenerSendMailToList(pos);

            for (String e : email) {
                lisContainer.addEmail(new Object[]{e});
            }

            ++pos;
            //end-email
        }
        //end-listener
        //stop-criterion
        List<String> stopCriterionType = reader.readStopCriterionTypeList();
        pos = 0;
        for (String type : stopCriterionType) {
            addStopCriterionAction();
            StopCriterionContainer stopCriterionContainer = stopCriterionCombo.getItemAt(stopCriterionCombo.getItemCount() - 1);
            UtilsSwing.addComboElement(stopCriterionContainer.stopCriterionType, type);

            String disjunctionForm = reader.readStopCriterionDisjunctionForm(pos);
            UtilsSwing.initJCheckBox(stopCriterionContainer.disjunctiveForm, disjunctionForm);

            //measure
            List<String> measure = reader.readStopCriterionMeasureNameList(pos);

            int pos2 = 0;
            for (String name : measure) {
                StopCriterionContainer.MeasurePanel curr = stopCriterionContainer.add();
                UtilsSwing.addComboElement(curr.measureName, name);

                String maximal = reader.readStopCriterionMeasureMaximal(pos, pos2);
                UtilsSwing.initJCheckBox(curr.maximal, maximal);

                ++pos2;
            }

            ++pos;
            //end-measure
        }
        //end-stop-criterion

        //scenario
        String scenarioType = reader.readScenarioType();
        UtilsSwing.addComboElement(scenarioTypeCombo, scenarioType);

        String batchModeType = reader.readBatchModeType();
        UtilsSwing.addComboElement(batchModeTypeCombo, batchModeType);

        String batchSizeText = reader.readBatchSize();
        UtilsSwing.initJTextField(this.batchSize, batchSizeText);

        String oracleType = reader.readOracleType();
        UtilsSwing.addComboElement(this.oracleTypeCombo, oracleType);

        String thresholdSize = reader.readScenarioStreamThreshold();
        UtilsSwing.initJTextField(this.threshold, thresholdSize);

        //query
        String queryType = reader.readQueryStrategyType();
        UtilsSwing.addComboElement(this.queryTypeCombo, queryType);

        String epsilon = reader.readQueryStrategyEpsilon();
        UtilsSwing.initJTextField(this.queryPanel.epsilonTextField, epsilon);

        String epsilonIteration = reader.readQueryStrategyMaxEpsilonIteration();
        UtilsSwing.initJTextField(this.queryPanel.epsilonIterationTextField, epsilonIteration);

        String factorR = reader.readQueryStrategyFactorRegularization();
        UtilsSwing.initJTextField(this.queryPanel.factorRTextField, factorR);

        String matrixFile = reader.readQueryStrategyMatrixFile();
        UtilsSwing.initJCheckBox(this.queryPanel.matrixFileCheck, matrixFile);

        String importanceD = reader.readQueryStrategyImportanceDensity();
        UtilsSwing.initJTextField(this.queryPanel.importanceDTextField, importanceD);

        String distanceType = reader.readQueryStrategyDistanceFunctionType();
        if (distanceType == null || distanceType.isEmpty()) {
            this.queryPanel.distance.setSelected(false);
            this.queryPanel.distanceFunctionTypeCombo.setEnabled(false);
        } else {
            this.queryPanel.distance.setSelected(true);
            this.queryPanel.distanceFunctionTypeCombo.setEnabled(true);
            UtilsSwing.addComboElement(this.queryPanel.distanceFunctionTypeCombo, distanceType);
        }

        String subQuery = reader.readSubQueryStrategyType();
        String wrapperClassifierType;
        if (subQuery == null || subQuery.isEmpty()) {
            subQueryCheck.setSelected(false);
            subQueryButton.setEnabled(false);

            wrapperClassifierType = reader.readWrapperClassifierType();

        } else {
            subQueryCheck.setSelected(true);
            subQueryButton.setEnabled(true);

            UtilsSwing.addComboElement(subQueryTypeCombo, subQuery);

            wrapperClassifierType = reader.readSubQueryWrapperClassifierType();

            String subEpsilon = reader.readSubQueryStrategyEpsilon();
            UtilsSwing.initJTextField(this.subQueryPanel.epsilonTextField, subEpsilon);

            String subEpsilonIteration = reader.readSubQueryStrategyMaxEpsilonIteration();
            UtilsSwing.initJTextField(this.subQueryPanel.epsilonIterationTextField, subEpsilonIteration);

            String subFactorR = reader.readSubQueryStrategyFactorRegularization();
            UtilsSwing.initJTextField(this.subQueryPanel.factorRTextField, subFactorR);

            String subMatrixFile = reader.readSubQueryStrategyMatrixFile();
            UtilsSwing.initJCheckBox(this.subQueryPanel.matrixFileCheck, subMatrixFile);
        }
        UtilsSwing.addComboElement(this.wrapperClassifierTypeCombo, wrapperClassifierType);

        List classifiers;
        if (subQueryCheck.isSelected()) {
            classifiers = reader.readSubQueryClassifierTypeList();
        } else {
            classifiers = reader.readClassifierTypeList();
        }

        for (int classifier = 0; classifier < classifiers.size(); classifier++) {
            ClassifierPanel curr = new ClassifierPanel();

            String currClassifier;

            if (subQueryCheck.isSelected()) {
                currClassifier = reader.readSubQueryClassifierType(classifier);
            } else {
                currClassifier = reader.readClassifierType(classifier);
            }
            UtilsSwing.addComboElement(curr.classifierTypeCombo, currClassifier);

            List base;
            if (subQueryCheck.isSelected()) {
                base = reader.readSubQueryBaseClassifierTypeList(classifier);
            } else {
                base = reader.readBaseClassifierTypeList(classifier);
            }

            if (base.size() > 0) {
                //just one base classifier
                String baseType;
                if (subQueryCheck.isSelected()) {
                    baseType = reader.readSubQueryBaseClassifierType(classifier, 0);
                } else {
                    baseType = reader.readBaseClassifierType(classifier, 0);
                }

                UtilsSwing.addComboElement(curr.baseClassifierTypeCombo, baseType);

                curr.baseClassifier.setSelected(true);
                curr.baseClassifierTypeCombo.setEnabled(true);
            }

            classifierContainerPanel.add(curr);
            wrapperFrame.pack();
        }
        //end-query
        //end-scenario
    }

    //end-read-methods
    //write-methods
    protected void writeRandomGenerator(XMLConfigurationBuilder builder) {
        if (randGenFactoryTypeCombo.getSelectedIndex() != -1) {
            builder.defineRandFactoryType(GUIXmlConfig.write(randGenFactoryTypeCombo.getSelectedItem()));
        }
        if (!seedEntry.getText().isEmpty()) {
            builder.defineRandFactorySeed(seedEntry.getText());
        }
    }

    protected void writeEvaluationMethod(XMLConfigurationBuilder builder) {
        if (evaluationMethodTypeCombo.getSelectedIndex() != -1) {
            builder.defineEvaluationMethodType(GUIXmlConfig.write(evaluationMethodTypeCombo.getSelectedItem()));
        }
    }

    protected void writeFiles(XMLConfigurationBuilder builder) {
        if (!fileDatasetEntry.getText().isEmpty()) {
            builder.defineFileDataset(fileDatasetEntry.getText());
        }
        if (!fileTrainEntry.getText().isEmpty()) {
            builder.defineFileTrain(fileTrainEntry.getText());
        }
        if (!fileTestEntry.getText().isEmpty()) {
            builder.defineFileTest(fileTestEntry.getText());
        }
        if (!fileLabeledEntry.getText().isEmpty()) {
            builder.defineFileLabeled(fileLabeledEntry.getText());
        }
        if (!fileUnLabeledEntry.getText().isEmpty()) {
            builder.defineFileUnlabeled(fileUnLabeledEntry.getText());
        }
        if (!fileXmlEntry.getText().isEmpty()) {
            builder.defineFileXml(fileXmlEntry.getText());
        }
    }

    protected void writeClassAttribute(XMLConfigurationBuilder builder) {
        if (!multiLabel.isSelected() && !classAttribute.getText().isEmpty()) {
            try {
                int result = (int) UtilsSwing.validateJTextField(classAttribute, this);
                builder.defineClassAttribute(result);
            } catch (NumberFormatException e) {
            }
        }

        if (multiLabel.isSelected()) {
            builder.defineMultiLabel(true);
        }
    }

    protected void writePercentage(XMLConfigurationBuilder builder) {
        if (!percentageSplit.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(percentageSplit, this);
                builder.definePercentageSplit(result);
            } catch (NumberFormatException e) {
            }
        }
    }

    protected void writeStratify(XMLConfigurationBuilder builder) {
        if (stratify.isSelected()) {
            builder.defineStratity(stratify.isSelected());
        }

        if (!numFoldText.getText().isEmpty()) {
            try {
                int result = (int) UtilsSwing.validateJTextField(numFoldText, this);
                builder.defineNumFolds(result);
            } catch (NumberFormatException e) {
            }
        }
    }

    protected void writeSamplingMethod(XMLConfigurationBuilder builder) {
        if (samplingMethodTypeCombo.getSelectedIndex() != -1) {
            builder.defineSamplingType(GUIXmlConfig.write(samplingMethodTypeCombo.getSelectedItem()));
        }

        if (!percentageSelect.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(percentageSelect, this);
                builder.defineSamplingPercentageToSelect(result);
            } catch (NumberFormatException e) {
            }
        }

        if (!m_BiasToUniformClass.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(m_BiasToUniformClass, this);
                builder.defineSamplingBiasToUniformClass(result);
            } catch (NumberFormatException e) {
            }
        }

        builder.defineSamplingNoReplacement(no_replacement.isSelected());

        builder.defineSamplingInvertSelection(invert_selection.isSelected());
    }

    protected void writeAlgorithm(XMLConfigurationBuilder builder) {
        if (algorithmTypeCombo.getSelectedIndex() != -1) {
            builder.defineAlgorithmType(GUIXmlConfig.write(algorithmTypeCombo.getSelectedItem()));
        }

        if (!maxIteration.getText().isEmpty()) {
            try {
                int result = (int) UtilsSwing.validateJTextField(maxIteration, this);
                builder.defineMaxIteration(result);
            } catch (NumberFormatException e) {
            }
        }

        //listener
        for (int listener = 0; listener < listenerCombo.getItemCount(); listener++) {
            ListenerContainer curr = listenerCombo.getItemAt(listener);

            if (curr.listenerTypeCombo.getSelectedIndex() != -1) {
                builder.defineListenerType(GUIXmlConfig.write(curr.listenerTypeCombo.getSelectedItem()), listener);
            }
            if (!curr.reportTitle.getText().isEmpty()) {
                builder.defineListenerReportTitle(curr.reportTitle.getText(), listener);

            }
            if (!curr.reportFrequency.getText().isEmpty()) {
                try {
                    int result = (int) UtilsSwing.validateJTextField(curr.reportFrequency, this);
                    builder.defineListenerReportFrequency(result, listener);
                } catch (NumberFormatException e) {
                }
            }
            if (!curr.directory.getText().isEmpty()) {
                builder.defineListenerReportDirectory(curr.directory.getText(), listener);
            }

            builder.defineListenerReportOnConsole(curr.onConsole.isSelected(), listener);
            builder.defineListenerReportOnFile(curr.onFile.isSelected(), listener);
            builder.defineListenerShowPassiveLearning(curr.showPassiveLearning.isSelected(), listener);
            builder.defineListenerShowWindow(curr.showWindow.isSelected(), listener);

            //email
            if (!curr.smtpHostText.getText().isEmpty()) {
                builder.defineListenerSendMailSmtpHost(curr.smtpHostText.getText(), listener);
            }

            if (!curr.smtpPortText.getText().isEmpty()) {
                try {
                    int result = (int) UtilsSwing.validateJTextField(curr.smtpPortText, this);
                    builder.defineListenerSendMailSmtpPort(result, listener);
                } catch (NumberFormatException e) {
                }
            }

            if (!curr.fromText.getText().isEmpty()) {
                builder.defineListenerSendMailFrom(curr.fromText.getText(), listener);
            }

            if (curr.attachReportFile.isSelected()) {
                builder.defineListenerSendMailAttachReportFile(true, listener);
            }

            if (!curr.userText.getText().isEmpty()) {
                builder.defineListenerSendMailUser(curr.userText.getText(), listener);
            }

            String pass = UtilsSwing.passString(curr.passPass.getPassword());
            String passC = UtilsSwing.passString(curr.passCPass.getPassword());

            if (!pass.isEmpty()) {
                if (pass.equals(passC)) {
                    builder.defineListenerSendMailPassword(pass, listener);
                } else {
                    JOptionPane.showMessageDialog(this, "The password do not match with confirm password.",
                            "Password Error.", JOptionPane.ERROR_MESSAGE);
                }
            }

            JTable email = curr.email;
            for (int j = 0; j < email.getModel().getRowCount(); j++) {
                String currTo = (String) email.getModel().getValueAt(j, 0);
                if (currTo != null && !currTo.isEmpty()) {
                    builder.defineListenerSendMailTo(currTo, listener, j);
                }
            }
            //end-email
        }
        //end-listener
        //stop-criterion
        for (int i = 0; i < stopCriterionCombo.getItemCount(); i++) {
            StopCriterionContainer curr = stopCriterionCombo.getItemAt(i);

            if (curr.stopCriterionType.getSelectedIndex() != -1) {
                builder.defineStopCriterionType(GUIXmlConfig.write(curr.stopCriterionType.getSelectedItem()), i);
            }

            builder.defineStopCriterionDisjunctionForm(curr.disjunctiveForm.isSelected(), i);

            Component[] measure = curr.containerMeasure.getComponents();
            for (int measurePos = 0; measurePos < measure.length; measurePos++) {
                if (measure[measurePos] instanceof StopCriterionContainer.MeasurePanel) {
                    StopCriterionContainer.MeasurePanel measu = (StopCriterionContainer.MeasurePanel) measure[measurePos];

                    if (measu.measureName.getSelectedIndex() != -1) {
                        builder.defineStopCriterionMeasureName(GUIXmlConfig.write(measu.measureName.getSelectedItem()), i, measurePos);
                    }

                    boolean maximal = measu.maximal.isSelected();
                    builder.defineStopCriterionMeasureMaximal(maximal, i, measurePos);
                }
            }

        }
        //end-stop-criterion

        //scenario
        if (scenarioTypeCombo.getSelectedIndex() != -1) {
            builder.defineScenarioType(GUIXmlConfig.write(scenarioTypeCombo.getSelectedItem()));
        }

        if (!threshold.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(threshold, this);
                builder.defineScenarioStreamThreshold(result);
            } catch (NumberFormatException e) {
            }
        }

        if (batchModeTypeCombo.getSelectedIndex() != -1) {
            builder.defineBatchModeType(GUIXmlConfig.write(batchModeTypeCombo.getSelectedItem()));
        }

        if (!batchSize.getText().isEmpty()) {
            try {
                int result = (int) UtilsSwing.validateJTextField(batchSize, this);
                builder.defineBatchSize(result);
            } catch (NumberFormatException e) {
            }
        }

        if (oracleTypeCombo.getSelectedIndex() != -1) {
            builder.defineOracleType(GUIXmlConfig.write(oracleTypeCombo.getSelectedItem()));
        }

        //query
        if (queryTypeCombo.getSelectedIndex() != -1) {
            builder.defineQueryStrategyType(GUIXmlConfig.write(queryTypeCombo.getSelectedItem()));
        }

        if (!queryPanel.epsilonTextField.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(queryPanel.epsilonTextField, this);
                builder.defineQueryStrategyEpsilon(result);
            } catch (NumberFormatException e) {
            }
        }

        if (!queryPanel.epsilonIterationTextField.getText().isEmpty()) {
            try {
                int result = (int) UtilsSwing.validateJTextField(queryPanel.epsilonIterationTextField, this);
                builder.defineQueryStrategyMaxEpsilonIteration(result);
            } catch (NumberFormatException e) {
            }
        }

        if (!queryPanel.factorRTextField.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(queryPanel.factorRTextField, this);
                builder.defineQueryStrategyFactorRegularization(result);
            } catch (NumberFormatException e) {
            }
        }

        if (queryPanel.matrixFileCheck.isSelected()) {
            builder.defineQueryStrategyMatrixFile(true);
        }

        if (!queryPanel.importanceDTextField.getText().isEmpty()) {
            try {
                double result = UtilsSwing.validateJTextField(queryPanel.importanceDTextField, this);
                builder.defineQueryStrategyImportanceDensity(result);
            } catch (NumberFormatException e) {
            }
        }

        if (queryPanel.distance.isSelected()) {
            if (queryPanel.distanceFunctionTypeCombo.getSelectedIndex() != -1) {
                builder.defineQueryStrategyDistanceFunctionType(GUIXmlConfig.write(queryPanel.distanceFunctionTypeCombo.getSelectedItem()));
            }
        }

        if (subQueryCheck.isSelected()) {
            if (subQueryTypeCombo.getSelectedIndex() != -1) {
                builder.defineSubQueryStrategyType(GUIXmlConfig.write(subQueryTypeCombo.getSelectedItem()));
            }

            if (wrapperClassifierTypeCombo.getSelectedIndex() != -1) {
                builder.defineSubQueryWrapperClassifierType(GUIXmlConfig.write(wrapperClassifierTypeCombo.getSelectedItem()));
            }

            if (!subQueryPanel.epsilonTextField.getText().isEmpty()) {
                try {
                    double result = UtilsSwing.validateJTextField(subQueryPanel.epsilonTextField, this);
                    builder.defineSubQueryStrategyEpsilon(result);
                } catch (NumberFormatException e) {
                }
            }

            if (!subQueryPanel.epsilonIterationTextField.getText().isEmpty()) {
                try {
                    int result = (int) UtilsSwing.validateJTextField(subQueryPanel.epsilonIterationTextField, this);
                    builder.defineSubQueryStrategyMaxEpsilonIteration(result);
                } catch (NumberFormatException e) {
                }
            }

            if (!subQueryPanel.factorRTextField.getText().isEmpty()) {
                try {
                    double result = UtilsSwing.validateJTextField(subQueryPanel.factorRTextField, this);
                    builder.defineSubQueryStrategyFactorRegularization(result);
                } catch (NumberFormatException e) {
                }
            }

            if (subQueryPanel.matrixFileCheck.isSelected()) {
                builder.defineSubQueryStrategyMatrixFile(true);
            }

        } else {
            if (wrapperClassifierTypeCombo.getSelectedIndex() != -1) {
                builder.defineWrapperClassifierType(GUIXmlConfig.write(wrapperClassifierTypeCombo.getSelectedItem()));
            }

        }

        Component[] classifiers = classifierContainerPanel.getComponents();
        for (int classifier = 0; classifier < classifiers.length; classifier++) {
            ClassifierPanel curr = (ClassifierPanel) classifiers[classifier];
            JComboBox typeCombo = curr.classifierTypeCombo;
            if (typeCombo.getSelectedIndex() != -1) {

                if (subQueryCheck.isSelected()) {
                    builder.defineSubQueryClassifierType(GUIXmlConfig.write(typeCombo.getSelectedItem()),
                            classifier);
                } else {
                    builder.defineClassifierType(GUIXmlConfig.write(typeCombo.getSelectedItem()),
                            classifier);
                }

            }

            if (curr.baseClassifier.isSelected()) {
                JComboBox baseTypeCombo = curr.baseClassifierTypeCombo;
                if (baseTypeCombo.getSelectedIndex() != -1) {

                    if (subQueryCheck.isSelected()) {
                        builder.defineSubQueryBaseClassifierType(GUIXmlConfig.write(baseTypeCombo.getSelectedItem()),
                                classifier, 0);
                    } else {
                        builder.defineBaseClassifierType(GUIXmlConfig.write(baseTypeCombo.getSelectedItem()),
                                classifier, 0);
                    }
                }
            }
        }
        //end-query
        //end-scenario
    }
    //end-write-methods

    //run-experiment
    public ArrayList<String> experimentReadyToRun() throws IOException, Exception {
        ExperimentBuilder builder = new ExperimentBuilder();

        File temp = File.createTempFile("jclal_running", ".temp");

        writeCurrExperiment(temp);

        ArrayList<String> dev = builder.buildExperiment(temp.getAbsolutePath());

        temp.deleteOnExit();

        return dev;
    }
    //end-run-experiment

    //panels**************************************
    protected JPanel runExperiment() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setBorder(BorderFactory.createTitledBorder("Run Experiments"));

        runExperiment = new JButton("Run experiment");
        runExperiment.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(GUIXml.this,
                        "Are you sure to run the experiment ?",
                        "Confirm Run Experiment", JOptionPane.OK_OPTION);

                if (option == 0) {
                    try {

                        ExperimentThreadTable experiment = new ExperimentThreadTable(new Experiment(), experimentReadyToRun(), runTable, GUIXml.this);
                        runExperiments.add(experiment);
                        experiment.start();
                    } catch (Exception ex) {
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(GUIXml.this,
                                "Error during the execution of the experiment: ",
                                "Experiment running error.", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

        container.add(runExperiment);

        viewExperiment = new JButton("View experiments...");
        viewExperiment.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(runFrame, viewExperiment);

                UtilsSwing.initFrame(runFrame);
                runFrame.setVisible(true);
            }
        });

        container.add(viewExperiment);

        return container;
    }

    protected JPanel evaluationMethod() {

        JPanel container = new JPanel(new VerticalLayout());
        container.setBorder(BorderFactory.createTitledBorder("Evaluation method"));

        //evaluation method
        JPanel evaluationType = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel evaluationMethodLabel = new JLabel("Evaluation method type");

        Object[] evaluationMethodItems = GUIXmlConfig.loadEvaluationMethodType();

        evaluationMethodTypeCombo = new JComboBox(evaluationMethodItems);

        evaluationType.add(evaluationMethodLabel);
        evaluationType.add(evaluationMethodTypeCombo);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelClassAttribute = new JLabel("Class Attribute");
        classAttribute = new JTextField(3);

        multiLabel = new JCheckBox("Multi-label");
        multiLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classAttribute.setEnabled(!multiLabel.isSelected());
            }
        });

        a1.add(labelClassAttribute);
        a1.add(classAttribute);
        a1.add(new JSeparator(JSeparator.VERTICAL));
        a1.add(multiLabel);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelPercentage = new JLabel("Percentage split (train set)");
        percentageSplit = new JTextField(5);

        a2.add(labelPercentage);
        a2.add(percentageSplit);

        JPanel a3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stratify = new JCheckBox("Stratify", false);

        numFoldText = new JTextField(3);

        a3.add(new JLabel("Num folds"));
        a3.add(numFoldText);
        a3.add(new JSeparator(JSeparator.VERTICAL));
        a3.add(stratify);

        //
        container.add(evaluationType);
        container.add(a1);
        container.add(a3);
        container.add(a2);

        return container;
    }

    protected JPanel randGenFactory() {
        JPanel randGenContainer = new JPanel(new FlowLayout());
        randGenContainer.setBorder(BorderFactory.createTitledBorder("Random Generator"));

        final JButton randButton = new JButton("Random Factory...");
        randGenContainer.add(randButton);

        randButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(randFrame, randButton);

                randButton.setEnabled(false);

                UtilsSwing.initFrame(randFrame);
                randFrame.setVisible(true);
            }
        });

        return randGenContainer;
    }

    protected JPanel files() {

        //files
        JPanel filesContainer = new JPanel(new FlowLayout());
        filesContainer.setBorder(BorderFactory.createTitledBorder("Files selection"));

        filesButton = new JButton("Files Options...");
        filesContainer.add(filesButton);

        filesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(filesFrame, filesButton);

                filesButton.setEnabled(false);

                UtilsSwing.initFrame(filesFrame);
                filesFrame.setVisible(true);
            }
        });

        return filesContainer;
    }

    protected JPanel samplingMethod() {
        JPanel samplingMethodContainer = new JPanel(new FlowLayout());
        samplingMethodContainer.setBorder(BorderFactory.createTitledBorder("Sampling Method"));

        final JButton samplingButton = new JButton("Sampling Method...");
        samplingMethodContainer.add(samplingButton);

        samplingButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(samplingFrame, samplingButton);

                samplingButton.setEnabled(false);

                UtilsSwing.initFrame(samplingFrame);
                samplingFrame.setVisible(true);
            }
        });

        return samplingMethodContainer;
    }

    protected JPanel algorithm() {
        JPanel algorithmContainer = new JPanel(new FlowLayout());
        algorithmContainer.setBorder(BorderFactory.createTitledBorder("Algorithm"));

        final JButton algorithmButton = new JButton("Algorithm...");
        algorithmContainer.add(algorithmButton);

        algorithmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(algorithmFrame, algorithmButton);

                algorithmButton.setEnabled(false);

                UtilsSwing.initFrame(algorithmFrame);
                algorithmFrame.setVisible(true);
            }
        });

        return algorithmContainer;
    }
    //end-panels**************************************

    //frame*******************************************
    protected void initFilesFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        filesFrame = new JDialog(GUIXml.this, "Files");
        filesFrame.setContentPane(container);

        JPanel a1 = new JPanel(new BorderLayout());
        JLabel fileDataset = new JLabel("File dataset");
        fileDatasetEntry = new JTextField(10);

        final JButton botonDataset = UtilsSwing.buttonFileSelect(fileDatasetEntry);

        a1.add(fileDataset, BorderLayout.WEST);
        a1.add(fileDatasetEntry, BorderLayout.CENTER);
        a1.add(botonDataset, BorderLayout.EAST);

        JPanel a2 = new JPanel(new BorderLayout());
        JLabel fileTrain = new JLabel("File train");
        fileTrainEntry = new JTextField(10);
        final JButton botonTrain = UtilsSwing.buttonFileSelect(fileTrainEntry);

        a2.add(fileTrain, BorderLayout.WEST);
        a2.add(fileTrainEntry, BorderLayout.CENTER);
        a2.add(botonTrain, BorderLayout.EAST);

        JPanel a3 = new JPanel(new BorderLayout());
        JLabel fileTest = new JLabel("File test");
        fileTestEntry = new JTextField(10);
        final JButton botonTest = UtilsSwing.buttonFileSelect(fileTestEntry);

        a3.add(fileTest, BorderLayout.WEST);
        a3.add(fileTestEntry, BorderLayout.CENTER);
        a3.add(botonTest, BorderLayout.EAST);

        JPanel a4 = new JPanel(new BorderLayout());
        JLabel fileL = new JLabel("File labeled");
        fileLabeledEntry = new JTextField(10);
        final JButton botonL = UtilsSwing.buttonFileSelect(fileLabeledEntry);

        a4.add(fileL, BorderLayout.WEST);
        a4.add(fileLabeledEntry, BorderLayout.CENTER);
        a4.add(botonL, BorderLayout.EAST);

        JPanel a5 = new JPanel(new BorderLayout());
        JLabel fileU = new JLabel("File unlabeled");
        fileUnLabeledEntry = new JTextField(10);
        final JButton botonU = UtilsSwing.buttonFileSelect(fileUnLabeledEntry);

        a5.add(fileU, BorderLayout.WEST);
        a5.add(fileUnLabeledEntry, BorderLayout.CENTER);
        a5.add(botonU, BorderLayout.EAST);

        JPanel a6 = new JPanel(new BorderLayout());
        JLabel fileX = new JLabel("File xml");
        fileXmlEntry = new JTextField(10);
        final JButton botonX = UtilsSwing.buttonFileSelect(fileXmlEntry);

        a6.add(fileX, BorderLayout.WEST);
        a6.add(fileXmlEntry, BorderLayout.CENTER);
        a6.add(botonX, BorderLayout.EAST);

        container.add(a1);
        container.add(a2);
        container.add(a3);
        container.add(a4);
        container.add(a5);
        container.add(a6);

        UtilsSwing.initFrame(filesFrame);
    }

    protected void initRandFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        JPanel a1 = new JPanel();
        JLabel randGenFactoryLabel = new JLabel("Rand Gen Factory Type");
        Object[] randGenFactoryItems = GUIXmlConfig.loadRandGenFactoryType();
        randGenFactoryTypeCombo = new JComboBox(randGenFactoryItems);

        a1.add(randGenFactoryLabel);
        a1.add(randGenFactoryTypeCombo);

        JPanel a2 = new JPanel();
        JLabel seed = new JLabel("Seed");
        seedEntry = new JTextField(10);

        a2.add(seed);
        a2.add(seedEntry);

        container.add(a1);
        container.add(a2);

        randFrame = new JDialog(GUIXml.this, "Random Factory");
        randFrame.setContentPane(container);

        UtilsSwing.initFrame(randFrame);
    }

    protected void initSamplingMethodFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        JPanel a1 = new JPanel();
        JLabel samplingMethodLabel = new JLabel("Sampling Method Type");
        Object[] samplingMethodItems = GUIXmlConfig.loadSamplingMethodType();
        samplingMethodTypeCombo = new JComboBox(samplingMethodItems);

        a1.add(samplingMethodLabel);
        a1.add(samplingMethodTypeCombo);

        JPanel a2 = new JPanel();
        JLabel percentageSe = new JLabel("Percentage to select (labeled set)");
        percentageSelect = new JTextField(5);

        a2.add(percentageSe);
        a2.add(percentageSelect);

        JPanel a3 = new JPanel();
        JLabel m_bias = new JLabel("Bias To Uniform Class");
        m_BiasToUniformClass = new JTextField(5);

        a3.add(m_bias);
        a3.add(m_BiasToUniformClass);

        JPanel a4 = new JPanel();
        no_replacement = new JCheckBox("No replacement");
        a4.add(no_replacement);
        invert_selection = new JCheckBox("Invert Selection");
        a4.add(invert_selection);

        container.add(a1);
        container.add(a2);
        container.add(a3);
        container.add(a4);

        samplingFrame = new JDialog(GUIXml.this, "Sampling Method");
        samplingFrame.setContentPane(container);

        UtilsSwing.initFrame(samplingFrame);
    }

    protected void initAlgorithmFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel algorithmLabel = new JLabel("Algorithm Type");
        Object[] algorithmItems = GUIXmlConfig.loadAlgorithmType();
        algorithmTypeCombo = new JComboBox(algorithmItems);

        a1.add(algorithmLabel);
        a1.add(algorithmTypeCombo);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel maxI = new JLabel("Max Iteration");
        maxIteration = new JTextField(5);

        a2.add(maxI);
        a2.add(maxIteration);

        final JButton listenerButton = new JButton("Listeners...");

        listenerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(listenerFrame, listenerButton);

                listenerButton.setEnabled(false);

                UtilsSwing.initFrame(listenerFrame);
                listenerFrame.setVisible(true);
            }
        });

        final JButton stopCriterionButton = new JButton("Stop Criterion...");

        stopCriterionButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(stopCriterionFrame, stopCriterionButton);

                stopCriterionButton.setEnabled(false);

                UtilsSwing.initFrame(stopCriterionFrame);
                stopCriterionFrame.setVisible(true);
            }
        });

        final JButton scenarioButton = new JButton("Scenario...");

        scenarioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(scenarioFrame, scenarioButton);

                scenarioButton.setEnabled(false);

                UtilsSwing.initFrame(scenarioFrame);
                scenarioFrame.setVisible(true);
            }
        });

        container.add(a1);
        container.add(a2);
        container.add(listenerButton);
        container.add(stopCriterionButton);
        container.add(scenarioButton);

        algorithmFrame = new JDialog(GUIXml.this, "Algorithm");
        algorithmFrame.setContentPane(container);

        UtilsSwing.initFrame(algorithmFrame);
    }

    protected void initListenerFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        listenerFrame = new JDialog(GUIXml.this, "Listener");
        listenerFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout());
        JLabel listener = new JLabel("Listener type list ");
        a1.add(listener);
        listenerCombo = new JComboBox<ListenerContainer>();
        a1.add(listenerCombo);

        addListener = new JButton("Add listener");

        addListener.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addListenerAction();
            }
        });

        JButton editListener = new JButton("Edit listener");

        editListener.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (UtilsSwing.exist(listenerCombo)) {
                    ListenerContainer x = (ListenerContainer) listenerCombo.getSelectedItem();
                    x.show();
                }
            }
        });

        final JButton removeListener = new JButton("Remove listener");

        removeListener.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (UtilsSwing.exist(listenerCombo)) {
                    ListenerContainer x = (ListenerContainer) listenerCombo.getSelectedItem();
                    x.destroy();

                    listenerCombo.removeItemAt(listenerCombo.getSelectedIndex());
                    listenerFrame.pack();
                }
            }
        });

        container.add(a1);

        JSeparator sep = new JSeparator();
        container.add(sep);

        container.add(addListener);
        container.add(editListener);
        container.add(removeListener);

        UtilsSwing.initFrame(listenerFrame);
    }

    protected void initStopCriterionFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        stopCriterionFrame = new JDialog(GUIXml.this, "Stop Criterion");
        stopCriterionFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout());
        JLabel stop = new JLabel("Stop Criterion list ");
        a1.add(stop);
        stopCriterionCombo = new JComboBox<StopCriterionContainer>();
        a1.add(stopCriterionCombo);

        addStopCriterion = new JButton("Add stop criterion");

        addStopCriterion.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addStopCriterionAction();
            }
        });

        JButton editStop = new JButton("Edit stop criterion");

        editStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (UtilsSwing.exist(stopCriterionCombo)) {
                    StopCriterionContainer x = (StopCriterionContainer) stopCriterionCombo.getSelectedItem();
                    x.show();
                }
            }
        });

        final JButton removeStop = new JButton("Remove stop criterion");

        removeStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (UtilsSwing.exist(stopCriterionCombo)) {
                    StopCriterionContainer x = (StopCriterionContainer) stopCriterionCombo.getSelectedItem();
                    x.destroy();

                    stopCriterionCombo.removeItemAt(stopCriterionCombo.getSelectedIndex());
                    stopCriterionFrame.pack();
                }
            }
        });

        container.add(a1);

        JSeparator sep = new JSeparator();
        container.add(sep);

        container.add(addStopCriterion);
        container.add(editStop);
        container.add(removeStop);

        UtilsSwing.initFrame(stopCriterionFrame);
    }

    protected void initScenarioFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        scenarioFrame = new JDialog(GUIXml.this, "Scenario");
        scenarioFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Scenario type list ");
        scenarioTypeCombo = new JComboBox(GUIXmlConfig.loadScenarioType());
        a1.add(type);
        a1.add(scenarioTypeCombo);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel threshold = new JLabel("Threshold ");
        this.threshold = new JTextField(5);
        a2.add(threshold);
        a2.add(this.threshold);

        final JButton batchModeButton = new JButton("Batch mode...");

        batchModeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(batchModeFrame, batchModeButton);

                batchModeButton.setEnabled(false);

                UtilsSwing.initFrame(batchModeFrame);
                batchModeFrame.setVisible(true);
            }
        });

        final JButton oracleButton = new JButton("Oracle...");

        oracleButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(oracleFrame, oracleButton);

                oracleButton.setEnabled(false);

                UtilsSwing.initFrame(oracleFrame);
                oracleFrame.setVisible(true);
            }
        });

        final JButton queryButton = new JButton("Query Strategy...");

        queryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(queryFrame, queryButton);

                queryButton.setEnabled(false);

                UtilsSwing.initFrame(queryFrame);
                queryFrame.setVisible(true);
            }
        });

        container.add(a1);
        container.add(a2);
        container.add(batchModeButton);
        container.add(oracleButton);
        container.add(queryButton);

        UtilsSwing.initFrame(scenarioFrame);
    }

    protected void initBatchModeFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        batchModeFrame = new JDialog(GUIXml.this, "Batch mode");
        batchModeFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Batch mode type list ");
        batchModeTypeCombo = new JComboBox(GUIXmlConfig.loadBatchModeType());
        a1.add(type);
        a1.add(batchModeTypeCombo);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel batch = new JLabel("Batch Size");
        batchSize = new JTextField(5);

        a2.add(batch);
        a2.add(batchSize);

        container.add(a1);
        container.add(a2);

        UtilsSwing.initFrame(batchModeFrame);
    }

    protected void initOracleFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        oracleFrame = new JDialog(GUIXml.this, "Oracle");
        oracleFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Oracle type list ");
        oracleTypeCombo = new JComboBox(GUIXmlConfig.loadOracleType());
        a1.add(type);
        a1.add(oracleTypeCombo);

        container.add(a1);

        UtilsSwing.initFrame(oracleFrame);
    }

    protected void initQueryFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        queryFrame = new JDialog(GUIXml.this, "Query Strategy");
        queryFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Query Strategy type list ");
        queryTypeCombo = new JComboBox(GUIXmlConfig.loadQueryStrategyType());
        a1.add(type);
        a1.add(queryTypeCombo);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subQueryCheck = new JCheckBox("Sub-query");

        subQueryTypeCombo = new JComboBox(GUIXmlConfig.loadQueryStrategyType());
        subQueryTypeCombo.setEnabled(false);

        subQueryButton = new JButton("Sub-Query Strategy...");
        subQueryButton.setEnabled(false);

        subQueryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (subQueryCheck.isSelected()) {
                    UtilsSwing.jointButton(subQueryFrame, subQueryButton, subQueryCheck);

                    subQueryButton.setEnabled(false);
                    UtilsSwing.initFrame(subQueryFrame);
                    subQueryFrame.setVisible(true);
                }
            }
        });

        subQueryCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (subQueryCheck.isSelected()) {
                    subQueryButton.setEnabled(true);
                } else {
                    subQueryButton.setEnabled(false);
                }
            }
        });

        a2.add(subQueryCheck);
        a2.add(subQueryButton);

        final JButton wrapperButton = new JButton("Wrapper classifier...");

        wrapperButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UtilsSwing.jointButton(wrapperFrame, wrapperButton);

                wrapperButton.setEnabled(false);

                UtilsSwing.initFrame(wrapperFrame);
                wrapperFrame.setVisible(true);
            }
        });

        container.add(a1);
        container.add(a2);

        queryPanel = new QueryElements(true);
        container.add(queryPanel);
        container.add(wrapperButton);

        UtilsSwing.initFrame(queryFrame);
    }

    protected class QueryElements extends JPanel {

        boolean isQuery;

        JTextField epsilonTextField;
        JTextField epsilonIterationTextField;

        JTextField factorRTextField;
        JCheckBox matrixFileCheck;

        JTextField importanceDTextField;
        JComboBox distanceFunctionTypeCombo;
        JCheckBox distance;

        public QueryElements(boolean isQuery) {
            this.isQuery = isQuery;

            setLayout(new VerticalLayout());

            JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel epsilon = new JLabel("Epsilon");
            epsilonTextField = new JTextField(5);
            a1.add(epsilon);
            a1.add(epsilonTextField);

            JPanel a6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel epsilonIteration = new JLabel("Max Epsilon Iteration");
            epsilonIterationTextField = new JTextField(5);
            a6.add(epsilonIteration);
            a6.add(epsilonIterationTextField);

            JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel factorR = new JLabel("Factor Regularization");
            factorRTextField = new JTextField(5);
            a2.add(factorR);
            a2.add(factorRTextField);

            JPanel a3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            matrixFileCheck = new JCheckBox("Matrix file");
            a3.add(matrixFileCheck);

            add(a1);
            add(a6);
            add(a2);
            add(a3);

            if (isQuery) {
                JPanel a4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel importanceD = new JLabel("Importance of density");
                importanceDTextField = new JTextField(3);
                a4.add(importanceD);
                a4.add(importanceDTextField);

                JPanel a5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                distance = new JCheckBox("Distance");

                distance.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (distance.isSelected()) {
                            distanceFunctionTypeCombo.setEnabled(true);
                        } else {
                            distanceFunctionTypeCombo.setEnabled(false);
                        }
                    }
                });

                distanceFunctionTypeCombo = new JComboBox(GUIXmlConfig.loadDistanceFunctionType());
                distanceFunctionTypeCombo.setEnabled(false);

                a5.add(distance);
                a5.add(distanceFunctionTypeCombo);

                add(a4);
                add(a5);
            }
        }

    }

    protected void initSubQueryFrame() {
        JPanel container = new JPanel(new VerticalLayout());

        subQueryFrame = new JDialog(GUIXml.this, "Sub Query Strategy");
        subQueryFrame.setContentPane(container);

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Sub-Query Strategy type list ");

        subQueryTypeCombo = new JComboBox(GUIXmlConfig.loadQueryStrategyType());
        subQueryTypeCombo.setEnabled(true);

        a2.add(type);
        a2.add(subQueryTypeCombo);

        container.add(a2);
        subQueryPanel = new QueryElements(false);
        container.add(subQueryPanel);

        UtilsSwing.initFrame(subQueryFrame);
    }

    protected void initWrapperFrame() {
        final JPanel container = new JPanel(new BorderLayout());

        wrapperFrame = new JDialog(GUIXml.this, "Wrapper classifier");
        wrapperFrame.setContentPane(container);

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel type = new JLabel("Wrapper classifier type list ");
        wrapperClassifierTypeCombo = new JComboBox(GUIXmlConfig.loadWrapperClassifierType());
        a1.add(type);
        a1.add(wrapperClassifierTypeCombo);

        classifierContainerPanel = new JPanel(new VerticalLayout());
        JScrollPane scroll = new JScrollPane(classifierContainerPanel);

        JButton addClassifier = new JButton("Add classifier");
        addClassifier.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                classifierContainerPanel.add(new ClassifierPanel());

                wrapperFrame.pack();
            }
        });

        container.add(a1, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        container.add(addClassifier, BorderLayout.SOUTH);

        UtilsSwing.initFrame(wrapperFrame);
    }

    public class ClassifierPanel extends JPanel {

        JCheckBox baseClassifier;
        JComboBox classifierTypeCombo;
        JComboBox baseClassifierTypeCombo;
        JButton delete;

        public ClassifierPanel() {
            setLayout(new BorderLayout());

            JPanel a1 = new JPanel(new BorderLayout());
            classifierTypeCombo = new JComboBox(GUIXmlConfig.loadClassifierType());
            delete = new JButton("Delete classifier");

            a1.add(classifierTypeCombo, BorderLayout.CENTER);
            a1.add(delete, BorderLayout.EAST);

            delete.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ClassifierPanel.this.setEnabled(false);
                    ClassifierPanel.this.getParent().remove(ClassifierPanel.this);

                    wrapperFrame.pack();
                }
            });

            JPanel a2 = new JPanel(new BorderLayout());
            baseClassifier = new JCheckBox("base-classifier");

            baseClassifierTypeCombo = new JComboBox(GUIXmlConfig.loadBaseClassifierType());
            baseClassifierTypeCombo.setEnabled(false);

            baseClassifier.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (baseClassifier.isSelected()) {
                        baseClassifierTypeCombo.setEnabled(true);
                    } else {
                        baseClassifierTypeCombo.setEnabled(false);
                    }
                }
            });

            a2.add(baseClassifier, BorderLayout.CENTER);
            a2.add(baseClassifierTypeCombo, BorderLayout.EAST);

            add(a1, BorderLayout.NORTH);
            add(a2, BorderLayout.SOUTH);

        }

    }

    protected void initAboutFrame() {
        final JPanel container = new JPanel(new BorderLayout());

        aboutFrame = new JDialog(GUIXml.this, "About...");
        aboutFrame.setContentPane(container);

        JPanel authors = new JPanel(new VerticalLayout());
        authors.setBorder(BorderFactory.createTitledBorder("About the authors..."));

        JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        a1.add(new JLabel("Oscar Gabriel Reyes Pupo (oreyesp@facinf.uho.edu.cu)"));

        JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        a2.add(new JLabel("Eduardo Perez Perdomo (eperezp@facinf.uho.edu.cu)"));

        authors.add(a1);
        authors.add(a2);

        JPanel program = new JPanel(new VerticalLayout());
        program.setBorder(BorderFactory.createTitledBorder("About the program..."));

        JPanel a3 = new JPanel(new VerticalLayout());
        a3.add(new JLabel("Product Version: 1.0"));
        a3.add(new JLabel("System: Linux, Windows, Macintosh"));

        program.add(a3);

        container.add(authors, BorderLayout.CENTER);
        container.add(program, BorderLayout.SOUTH);

        UtilsSwing.initFrame(aboutFrame);
    }

    protected void initRunFrame() {
        final JPanel container = new JPanel(new BorderLayout());

        runFrame = new JDialog(GUIXml.this, "View experiments running...");
        runFrame.setContentPane(container);

        runTable = runTable();

        JScrollPane scroll = new JScrollPane(runTable);

        scroll.setPreferredSize(new Dimension(600, 300));

        container.add(scroll);

        UtilsSwing.initFrame(runFrame);
    }

    protected JTable runTable() {
        JTable jTable1 = new JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Experiment", "Running", "Finish", "Days", "Hours",
                    "Minutes", "Seconds", "Millis"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        jTable1.getTableHeader().setReorderingAllowed(false);

        return jTable1;
    }

    //end-frame*******************************************
    //listener******************************************
    protected void addListenerAction() {
        listenerCombo.addItem(new ListenerContainer());
        listenerCombo.setSelectedIndex(listenerCombo.getItemCount() - 1);

        listenerFrame.pack();
    }

    protected class ListenerContainer {

        int num;
        JDialog listenerFrame;
        JComboBox listenerTypeCombo;
        JTextField reportTitle;
        JTextField directory;
        JTextField reportFrequency;

        JCheckBox onFile;
        JCheckBox onConsole;
        JCheckBox showWindow;
        JCheckBox showPassiveLearning;

        JTable email;
        JTextField smtpHostText;
        JTextField smtpPortText;
        JTextField fromText;
        JCheckBox attachReportFile;
        JTextField userText;
        JPasswordField passPass;
        JPasswordField passCPass;

        JDialog emailFrame;

        public ListenerContainer() {
            num = countListener++;

            initEmailFrame();
            initListenerFrame();
        }

        void show() {
            UtilsSwing.initFrame(listenerFrame);
            listenerFrame.setVisible(true);
        }

        void destroy() {
            listenerFrame.dispose();
            emailFrame.dispose();
        }

        @Override
        public String toString() {
            return "Listener: " + num;
        }

        void initListenerFrame() {
            listenerFrame = new JDialog(GUIXml.this, toString());
            JPanel container = new JPanel(new VerticalLayout());

            JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel listenerLabel = new JLabel("Listener Type list ");
            Object[] listenerItems = GUIXmlConfig.loadListenerType();
            listenerTypeCombo = new JComboBox(listenerItems);
            a1.add(listenerLabel);
            a1.add(listenerTypeCombo);

            JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel frec = new JLabel("Report frequency");
            reportFrequency = new JTextField(3);

            a2.add(frec);
            a2.add(reportFrequency);

            JPanel a3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel title = new JLabel("Report title");
            reportTitle = new JTextField(15);

            a3.add(title);
            a3.add(reportTitle);

            JPanel a4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel repor_di = new JLabel("Report directory");
            directory = new JTextField(10);
            final JButton botonDirectory = UtilsSwing.buttonFolderSelect(directory);

            a4.add(repor_di);
            a4.add(directory);
            a4.add(botonDirectory);

            JPanel a5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            onFile = new JCheckBox("Report on file");
            a5.add(onFile);

            onConsole = new JCheckBox("Report on console");
            a5.add(onConsole);

            JPanel a6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            showWindow = new JCheckBox("Show window");
            a6.add(showWindow);

            showPassiveLearning = new JCheckBox("Show passive learning");
            a6.add(showPassiveLearning);

            final JButton configEmail = new JButton("Configure Send Email...");
            configEmail.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    configEmail.setEnabled(false);

                    UtilsSwing.initFrame(emailFrame);
                    emailFrame.setVisible(true);
                }
            });
            UtilsSwing.jointButton(emailFrame, configEmail);

            container.add(a3);
            container.add(a1);
            container.add(a2);
            container.add(a4);
            container.add(a5);
            container.add(a6);
            container.add(configEmail);

            listenerFrame.add(container);
            UtilsSwing.initFrame(listenerFrame);
        }

        void addEmail(Object[] columns) {
            DefaultTableModel modelo = (DefaultTableModel) email.getModel();
            modelo.addRow(columns);
        }

        protected void initEmailFrame() {
            emailFrame = new JDialog(GUIXml.this, toString() + ": email");
            JPanel container = new JPanel(new VerticalLayout());

            JPanel a1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel smtpHost = new JLabel("Smtp host");
            smtpHostText = new JTextField(8);
            a1.add(smtpHost);
            a1.add(smtpHostText);

            JPanel a2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel smtpPort = new JLabel("Smtp port");
            smtpPortText = new JTextField(3);
            a2.add(smtpPort);
            a2.add(smtpPortText);

            JPanel a3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel from = new JLabel("From");
            fromText = new JTextField(8);
            a3.add(from);
            a3.add(fromText);

            JPanel a4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            attachReportFile = new JCheckBox("Attach report file");
            a4.add(attachReportFile);

            JPanel a5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel user = new JLabel("User");
            userText = new JTextField(8);
            a5.add(user);
            a5.add(userText);

            JPanel a6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel pass = new JLabel("Password");
            passPass = new JPasswordField(8);
            a6.add(pass);
            a6.add(passPass);

            JPanel a7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel passC = new JLabel("Confirm_P");
            passCPass = new JPasswordField(8);
            a7.add(passC);
            a7.add(passCPass);

            email = emailTable();
            JScrollPane s = new JScrollPane(email);
            s.setPreferredSize(new Dimension(200, 100));

            JButton addEmail = new JButton("Add email");
            addEmail.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    addEmail(new Object[]{null});
                }
            });

            container.add(a1);
            container.add(a2);
            container.add(a3);
            container.add(a4);
            container.add(a5);
            container.add(a6);
            container.add(a7);
            container.add(s);
            container.add(addEmail);

            emailFrame.add(container);
            UtilsSwing.initFrame(emailFrame);
        }

        protected JTable emailTable() {
            JTable jTable1 = new JTable();

            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "email"
                    }
            ) {
                Class[] types = new Class[]{
                    java.lang.String.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }
            });

            return jTable1;
        }
    }

    //end-listener***************************************
    //stop criterion******************************************
    protected void addStopCriterionAction() {
        stopCriterionCombo.addItem(new StopCriterionContainer());
        stopCriterionCombo.setSelectedIndex(stopCriterionCombo.getItemCount() - 1);

        stopCriterionFrame.pack();
    }

    protected class StopCriterionContainer {

        int num;
        JDialog stopCriterionFrame;
        JCheckBox disjunctiveForm;
        JPanel containerMeasure;

        JComboBox stopCriterionType;

        MeasurePanel lastAdd;

        public StopCriterionContainer() {
            num = countStopCriterion++;

            initStopCriterionFrame();
        }

        void show() {
            UtilsSwing.initFrame(stopCriterionFrame);
            stopCriterionFrame.setVisible(true);
        }

        void destroy() {
            stopCriterionFrame.dispose();
        }

        @Override
        public String toString() {
            return "Stop Criterion: " + num;
        }

        MeasurePanel add() {
            MeasurePanel a = new MeasurePanel();
            containerMeasure.add(a);
            return a;
        }

        protected void initStopCriterionFrame() {
            stopCriterionFrame = new JDialog(GUIXml.this, toString());
            JPanel container = new JPanel(new BorderLayout());

            JPanel a1a = new JPanel(new FlowLayout(FlowLayout.LEFT));
            disjunctiveForm = new JCheckBox("Disjunction-form");
            a1a.add(disjunctiveForm);

            JPanel a1b = new JPanel(new FlowLayout(FlowLayout.LEFT));
            stopCriterionType = new JComboBox(GUIXmlConfig.loadStopCriterionType());
            JLabel type = new JLabel("Stop criterion type list: ");
            a1b.add(type);
            a1b.add(stopCriterionType);

            JPanel a1 = new JPanel(new VerticalLayout());
            a1.add(a1b);
            a1.add(a1a);

            container.add(a1, BorderLayout.NORTH);

            containerMeasure = new JPanel(new VerticalLayout());
            JScrollPane scroll = new JScrollPane(containerMeasure);

            container.add(scroll, BorderLayout.CENTER);

            JButton addMeasure = new JButton("Add measure");
            addMeasure.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    containerMeasure.add(new MeasurePanel());

                    stopCriterionFrame.pack();
                }
            });

            container.add(addMeasure, BorderLayout.SOUTH);

            stopCriterionFrame.add(container);
            UtilsSwing.initFrame(stopCriterionFrame);
        }

        protected void deleteMeasurePanel(JPanel num) {

            containerMeasure.remove(num);
            stopCriterionFrame.pack();
        }

        public class MeasurePanel extends JPanel {

            JCheckBox maximal;
            JComboBox measureName;
            JButton delete;

            public MeasurePanel() {
                setLayout(new BorderLayout());
                maximal = new JCheckBox("maximal");
                measureName = new JComboBox(GUIXmlConfig.loadMeasureName());
                delete = new JButton("Delete measure");

                delete.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MeasurePanel.this.setEnabled(false);
                        deleteMeasurePanel(MeasurePanel.this);
                    }
                });

                add(maximal, BorderLayout.WEST);
                add(measureName, BorderLayout.CENTER);
                add(delete, BorderLayout.EAST);

            }

        }

    }
    //end-stop criterion***************************************

    protected static class ExperimentThreadTable extends ExperimentThread {

        int pos;
        DefaultTableModel modelo;
        JFrame y;

        public ExperimentThreadTable(Experiment experiment, ArrayList<String> jobFile, JTable x, JFrame y) {
            super(experiment, jobFile);
            modelo = (DefaultTableModel) x.getModel();
            pos = modelo.getRowCount();
            this.y = y;
            //name, start-stop, finished
            modelo.addRow(new Object[]{(pos + 1), false, false});
        }

        @Override
        public void run() {
            modelo.setValueAt(true, pos, 1);

            try {
                super.run();
            } catch (Exception e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(y,
                        "There is a error during the execution of the experiment",
                        "Experiment running error.", JOptionPane.ERROR_MESSAGE);
            }

            modelo.setValueAt(true, pos, 2);
            modelo.setValueAt(false, pos, 1);
            Time time = new Time(getExperiment().getRuntime());
            modelo.setValueAt(time.days, pos, 3);
            modelo.setValueAt(time.hours, pos, 4);
            modelo.setValueAt(time.minutes, pos, 5);
            modelo.setValueAt(time.seconds, pos, 6);
            modelo.setValueAt(time.mili, pos, 7);
        }

        static class Time {

            long days = 0;
            long hours = 0;
            long minutes = 0;
            long seconds = 0;
            long mili = 0;
            long constante = 1000 * 60 * 60 * 24;

            public Time(long time) {
                days = time / constante;
                time = time % constante;

                constante /= 24;
                hours = time / constante;
                time = time % constante;

                constante /= 60;
                minutes = time / constante;
                time = time % constante;

                constante /= 60;
                seconds = time / constante;
                time = time % constante;

                mili = time;
            }

            @Override
            public String toString() {
                return "days=" + days + ", hours=" + hours + ", min=" + minutes + ",+\n sec=" + seconds + ", mill=" + mili;
            }
        }

    }

    //********others
    public void setClassAttribute(int classIndex) {
        classAttribute.setText(String.valueOf(classIndex));
    }

    public void enableClassAttributeField(boolean value) {
        classAttributeEnable = value;
        classAttribute.setEnabled(value);
    }

    public void setFileDataset(String file) {
        fileDatasetEntry.setText(file);
    }

    public void enableFileFields(boolean value) {
        filesEnable = value;
        filesButton.setEnabled(value);
    }
}
