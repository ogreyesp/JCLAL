/*
 * Copyright (C) 2014
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
package net.sf.jclal.gui.view.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import net.sf.jclal.util.file.FileUtil;
import weka.core.Utils;

/**
 * Utility class to load the properties needed to construct experiments.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class GUIXmlConfig {

    protected static final String dir = "net/sf/jclal/gui/view/xml/config/";

    protected static String evaluationMethodTypeString = "EvaluationMethodType";
    protected static String evaluationMethodType = dir + "EvaluationMethodType.props";

    protected static String randGenFactoryTypeString = "RandGenFactoryType";
    protected static String randGenFactoryType = dir + "RandGenFactoryType.props";

    protected static String samplingMethodTypeString = "SamplingMethodType";
    protected static String samplingMethodType = dir + "SamplingMethodType.props";

    protected static String algorithmTypeString = "AlgorithmType";
    protected static String algorithmType = dir + "AlgorithmType.props";

    protected static String listenerTypeString = "ListenerType";
    protected static String listenerType = dir + "ListenerType.props";

    protected static String measureNameString = "MeasureName";
    protected static String stopCriterionTypeString = "StopCriterionType";
    protected static String stopCriterion = dir + "StopCriterion.props";

    protected static String scenarioTypeString = "ScenarioType";
    protected static String scenario = dir + "ScenarioType.props";

    protected static String batchModeTypeString = "BatchModeType";
    protected static String batchModeType = dir + "BatchModeType.props";

    protected static String oracleTypeString = "OracleType";
    protected static String oracleType = dir + "OracleType.props";

    protected static String queryStrategyTypeString = "QueryStrategyType";
    protected static String queryStrategyType = dir + "QueryStrategyType.props";

    protected static String wrapperClassifierTypeString = "WrapperClassifierType";
    protected static String wrapperClassifierType = dir + "WrapperClassifierType.props";

    protected static String classifierTypeString = "ClassifierType";
    protected static String classifierType = dir + "ClassifierType.props";

    protected static String baseClassifierTypeString = "BaseClassifierType";
    protected static String baseClassifierType = dir + "BaseClassifierType.props";

    protected static String distanceFunctionTypeString = "DistanceFunctionType";
    protected static String distanceFunctionType = dir + "DistanceFunctionType.props";

    public static String cleanStringFromFile(String fromFile) {
        String dev = fromFile.trim();
        return dev;
    }

    /**
     * Return an entry stream from a file of configuration that is in the.jar.
     *
     * @param objective The objective
     * @return A list of entry stream
     * @throws IOException The exception that will be launched.
     */
    public static List<InputStream> readyLoadConfiguration(String objective) throws IOException {
        Utils utils = new Utils();
        Enumeration<URL> urls = utils.getClass().getClassLoader().getResources(objective);

        LinkedList<InputStream> dev = new LinkedList<InputStream>();

        while (urls.hasMoreElements()) {
            dev.add(urls.nextElement().openStream());
        }

        return dev;
    }

    /**
     * Used to load data from the configuration files.
     *
     * @param fileElement The file where is going to find.
     * @param seek The element to located.
     * @return An array of object.
     */
    public static Object[] loadElement(String fileElement, String seek) {
        Object[] dev = new Object[]{};
        try {
            String line;
            List<InputStream> list = readyLoadConfiguration(fileElement);
            if (list.isEmpty()) {
                return dev;
            }

            BufferedReader read = new BufferedReader(new InputStreamReader(list.get(0)));
            while ((line = read.readLine()) != null && !line.startsWith(seek)) {
            }

            LinkedList temp = new LinkedList();
            while ((line = read.readLine()) != null && !(line = cleanStringFromFile(line)).isEmpty()) {
                int pos = line.indexOf(';');
                if (pos == -1) {
                    temp.add(new ComboElement(line));
                } else {
                    String[] div = line.split(";");
                    if (div.length > 1) {
                        temp.add(new ComboElement(div[0], div[1]));
                    }
                }
            }

            dev = new Object[temp.size()];
            for (int i = 0; i < dev.length; i++) {
                dev[i] = temp.get(i);
            }

            read.close();
        } catch (Exception e) {
        }
        return dev;
    }

    /**
     * Used to load data from the configuration files.
     *
     * @param fileElement The file where is going to find.
     * @param seek The element to located.
     * @return The array of objects.
     */
    public static Object[] loadElement(File fileElement, String seek) {
        Object[] dev = new Object[]{};
        try {
            String line;
            BufferedReader read
                    = new BufferedReader(FileUtil.stringReader(fileElement));
            
            while ((line = read.readLine()) != null && !line.startsWith(seek)) {
            }

            LinkedList temp = new LinkedList();
            while ((line = read.readLine()) != null
                    && !(line = cleanStringFromFile(line)).isEmpty()) {

                int pos = line.indexOf(';');
                if (pos == -1) {
                    temp.add(new ComboElement(line));
                } else {
                    String[] div = line.split(";");
                    if (div.length > 1) {
                        temp.add(new ComboElement(div[0], div[1]));
                    }
                }
            }

            dev = new Object[temp.size()];
            for (int i = 0; i < dev.length; i++) {
                dev[i] = temp.get(i);
            }

            read.close();
            
        } catch (Exception e) {
        }
        return dev;
    }

    /**
     * Utility class to manipulate combo elements.
     */
    public static class ComboElement {

        String show;
        String classElement;

        public ComboElement(String classElement) {
            this.classElement = cleanStringFromFile(classElement);
            int pos = classElement.lastIndexOf('.');
            try {
                this.show = classElement.substring(pos + 1);
            } catch (Exception e) {
                this.show = classElement;
            }
        }

        public ComboElement(String classElement, String show) {
            this.show = cleanStringFromFile(show);
            this.classElement = cleanStringFromFile(classElement);
        }

        @Override
        public String toString() {
            return show;
        }

        @Override
        public boolean equals(Object obj) {
            try {
                ComboElement other = (ComboElement) obj;
                return this.classElement.equals(other.classElement);
            } catch (Exception e) {
                return false;
            }
        }

    }

    public static String write(Object obj) {
        try {
            ComboElement other = (ComboElement) obj;
            return other.classElement;
        } catch (Exception e) {
            return obj.toString();
        }
    }

    public static Object[] loadEvaluationMethodType() {
        return loadElement(evaluationMethodType, evaluationMethodTypeString);
    }

    public static Object[] loadRandGenFactoryType() {
        return loadElement(randGenFactoryType, randGenFactoryTypeString);
    }

    public static Object[] loadSamplingMethodType() {
        return loadElement(samplingMethodType, samplingMethodTypeString);
    }

    public static Object[] loadAlgorithmType() {
        return loadElement(algorithmType, algorithmTypeString);
    }

    public static Object[] loadListenerType() {
        return loadElement(listenerType, listenerTypeString);
    }

    public static Object[] loadMeasureName() {
        return loadElement(stopCriterion, measureNameString);
    }

    public static Object[] loadStopCriterionType() {
        return loadElement(stopCriterion, stopCriterionTypeString);
    }

    public static Object[] loadScenarioType() {
        return loadElement(scenario, scenarioTypeString);
    }

    public static Object[] loadBatchModeType() {
        return loadElement(batchModeType, batchModeTypeString);
    }

    public static Object[] loadOracleType() {
        return loadElement(oracleType, oracleTypeString);
    }

    public static Object[] loadQueryStrategyType() {
        return loadElement(queryStrategyType, queryStrategyTypeString);
    }

    public static Object[] loadWrapperClassifierType() {
        return loadElement(wrapperClassifierType, wrapperClassifierTypeString);
    }

    public static Object[] loadClassifierType() {
        return loadElement(classifierType, classifierTypeString);
    }

    public static Object[] loadBaseClassifierType() {
        return loadElement(baseClassifierType, baseClassifierTypeString);
    }

    public static Object[] loadDistanceFunctionType() {
        return loadElement(distanceFunctionType, distanceFunctionTypeString);
    }
}
