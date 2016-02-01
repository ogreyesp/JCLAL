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
package net.sf.jclal.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.XMLConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Experiments builder.
 *
 * @author Sebastian Ventura
 */
public class ExperimentBuilder {
    /////////////////////////////////////////////////////////////////
    // --------------------------------------------------- Properties
    /////////////////////////////////////////////////////////////////

    /**
     * Control the depth size
     */
    private ArrayList<Integer> combinationBase;

    /////////////////////////////////////////////////////////////////
    // -------------------------------------------------- Constructor
    /////////////////////////////////////////////////////////////////
    /**
     * Empty constructor
     */
    public ExperimentBuilder() {
    }

    /**
     * Expands the experiments for the configuration file
     *
     * @param experimentFileName The name of the experiment file.
     * @return The experiments for the configuration file.
     */
    public ArrayList<String> buildExperiment(String experimentFileName) {
        ArrayList<String> configurations = expandElements(experimentFileName);
        ArrayList<String> allCreatedExperiments = new ArrayList<String>();
        int numberExperiments = 1;

        allCreatedExperiments.addAll(configurations);

        /**
         * Expand multi-valued elements
         */
        do {
            numberExperiments = configurations.size();

            ArrayList<String> createdExperiments = new ArrayList<String>();

            for (String experiment : configurations) {
                createdExperiments.addAll(expandElements(experiment));
            }

            allCreatedExperiments.addAll(createdExperiments);

            configurations = createdExperiments;

        } while (configurations.size() != numberExperiments);

        /**
         * Expand multi-valued attributes
         */
        do {
            numberExperiments = configurations.size();

            ArrayList<String> createdExperiments = new ArrayList<String>();

            for (String experiment : configurations) {
                createdExperiments.addAll(expandAttributes(experiment));
            }

            allCreatedExperiments.addAll(createdExperiments);

            configurations = createdExperiments;

        } while (configurations.size() != numberExperiments);

        allCreatedExperiments.removeAll(configurations);

        /**
         * Remove temp files
         */
        for (String temp : allCreatedExperiments) {
            if (!temp.equals(experimentFileName)) {
                new File(temp).delete();
            }
        }

        /**
         * Move the expanded configuration files to the experiments folder
         */
        if (configurations.size() > 1) {
            File dir = new File("experiments");

            /**
             * If the directory exists, delete all files
             */
            if (dir.exists()) {
                File[] experimentFiles = dir.listFiles();
                for (File f : experimentFiles) {
                    f.delete();
                }
            } /**
             * Else, create the directory
             */
            else {
                dir.mkdir();
            }

            for (int i = 0; i < configurations.size(); i++) {
                File file = new File(configurations.get(i));
                file.renameTo(new File(dir, file.getName()));
                String[] files = configurations.get(i).split("/");
                String fileName = files[files.length - 1];
                configurations.set(i, dir.getPath() + "/" + fileName);
            }
        }
        /**
         * Return the configuration filenames
         */
        return configurations;
    }

    /**
     * Expands the multi-valued elements for the jobFilename configuration file
     * 
     * @param jobFilename The job file name to execute
     * @return An arraylist of string
     */
    private ArrayList<String> expandElements(String jobFilename) {
        combinationBase = new ArrayList<Integer>();

        ArrayList<String> configurationFileNames = new ArrayList<String>();

        // Try open job file
        File jobFile = new File(jobFilename);

        if (jobFile.exists()) {
            try {

                // Find the number of combinations to perform
                expandElementsIterateElements(new XMLConfiguration(jobFile).getDocument().getChildNodes());

                int numberCombinations = 1;

                // Calculate the amount of combinations
                for (Integer i : combinationBase) {
                    numberCombinations *= i;
                }

                int[][] configurationSchema = new int[numberCombinations][];

                for (int i = 0; i < numberCombinations; i++) {
                    configurationSchema[i] = new int[combinationBase.size()];
                }

                int consecutiveElements = numberCombinations;
                int accProduct = 1;

                // Calculate the combinations of each element
                for (int i = 0; i < combinationBase.size(); i++) {
                    consecutiveElements /= combinationBase.get(i);

                    for (int j = 0; j < combinationBase.get(i); j++) {
                        for (int k = 0; k < consecutiveElements; k++) {
                            for (int r = 0; r < accProduct; r++) {
                                int step = consecutiveElements * combinationBase.get(i);
                                configurationSchema[r * step + j * consecutiveElements + k][i] = j;
                            }
                        }
                    }

                    accProduct *= combinationBase.get(i);
                }

                XMLConfiguration[] configurations = new XMLConfiguration[numberCombinations];

                for (int i = 0; i < numberCombinations; i++) {
                    configurations[i] = new XMLConfiguration(jobFile);

                    expandElementsIterateElements(configurations[i].getDocument().getChildNodes(), configurationSchema[i].clone());
                }

                for (int i = 0; i < numberCombinations; i++) {
                    String fileName = jobFilename;

                    for (int j = 0; j < configurationSchema[i].length; j++) {
                        fileName += "_" + configurationSchema[i][j];
                    }

                    configurations[i].save(new File(fileName));

                    configurationFileNames.add(fileName);
                }
            } catch (Exception e) {
                Logger.getLogger(ExperimentBuilder.class.getName()).log(
                        Level.SEVERE, null, e);
            }
        } else {
            System.out.println("Job file not found");
            System.exit(1);
        }

        return configurationFileNames;
    }

    private int expandElementsIterateElements(NodeList elements) {
        for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i) instanceof Element) {
                Element element = (Element) elements.item(i);

                if (expandElementsIterateAttributes(element) == 1) {
                    return 1;
                }

                expandElementsIterateElements(element.getChildNodes());
            }
        }

        return 0;
    }

    private int expandElementsIterateAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);

            if (attribute.getNodeName().equals("multi")) {
                combinationBase.add(element.getElementsByTagName(element.getNodeName()).getLength());
                return 1;
            }
        }

        return 0;
    }

    private int expandElementsIterateElements(NodeList elements, int[] configurationSchema) {
        for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i) instanceof Element) {
                Element element = (Element) elements.item(i);

                if (expandElementsIterateAtributes(element, configurationSchema) == 1) {
                    return 1;
                }

                expandElementsIterateElements(element.getChildNodes(), configurationSchema);
            }
        }

        return 0;
    }

    private int expandElementsIterateAtributes(Element element, int[] configurationSchema) {
        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);

            if (attribute.getNodeName().equals("multi")) {
                NodeList list = element.getElementsByTagName(element.getNodeName());

                for (int j = 0; j < configurationSchema.length; j++) {
                    if (configurationSchema[j] != -1) {
                        element.getParentNode().replaceChild(list.item(configurationSchema[j]), element);

                        configurationSchema[j] = -1;
                        break;
                    }
                }

                list = element.getChildNodes();

                for (int j = 0; j < list.getLength(); j++) {
                    if (list.item(j).getNodeName().equals(attribute.getNodeName())) {
                        element.removeChild(list.item(j));
                    }
                }

                return 1;
            }
        }

        return 0;
    }

    private ArrayList<String> expandAttributes(String jobFilename) {
        combinationBase = new ArrayList<Integer>();

        ArrayList<String> configurationFileNames = new ArrayList<String>();

        // Try open job file
        File jobFile = new File(jobFilename);

        if (jobFile.exists()) {
            try {

                // Find the number of combinations to perform
                expandAttributesIterateElements(new XMLConfiguration(jobFile).getDocument().getChildNodes());

                int numberCombinations = 1;

                // Calculate the amount of combinations
                for (Integer i : combinationBase) {
                    numberCombinations *= i;
                }

                int[][] configurationSchema = new int[numberCombinations][];

                for (int i = 0; i < numberCombinations; i++) {
                    configurationSchema[i] = new int[combinationBase.size()];
                }

                int consecutiveElements = numberCombinations;
                int accProduct = 1;

                // Calculate the combinations of each element
                for (int i = 0; i < combinationBase.size(); i++) {
                    consecutiveElements /= combinationBase.get(i);

                    for (int j = 0; j < combinationBase.get(i); j++) {
                        for (int k = 0; k < consecutiveElements; k++) {
                            for (int r = 0; r < accProduct; r++) {
                                int step = consecutiveElements * combinationBase.get(i);
                                configurationSchema[r * step + j * consecutiveElements + k][i] = j;
                            }
                        }
                    }

                    accProduct *= combinationBase.get(i);
                }

                XMLConfiguration[] configurations = new XMLConfiguration[numberCombinations];

                for (int i = 0; i < numberCombinations; i++) {
                    configurations[i] = new XMLConfiguration(jobFile);

                    //Build the document for each combination
                    expandAttributesIterateElements(configurations[i].getDocument().getChildNodes(), configurationSchema[i].clone());
                }

                for (int i = 0; i < numberCombinations; i++) {
                    String fileName = jobFilename;

                    for (int j = 0; j < configurationSchema[i].length; j++) {
                        fileName += "_" + configurationSchema[i][j];
                    }

                    configurations[i].save(new File(fileName));

                    configurationFileNames.add(fileName);
                }
            } catch (Exception e) {
                Logger.getLogger(ExperimentBuilder.class.getName()).log(
                        Level.SEVERE, null, e);
            }
        } else {
            System.out.println("Job file not found");
            System.exit(1);
        }

        return configurationFileNames;
    }

    private int expandAttributesIterateElements(NodeList elements) {
        for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i) instanceof Element) {
                Element element = (Element) elements.item(i);

                if (expandAttributesIterateAttributes(element) == 1) {
                    return 1;
                }

                expandAttributesIterateElements(element.getChildNodes());
            }
        }

        return 0;
    }

    private int expandAttributesIterateAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);

            if (attribute.getNodeValue().equals("multi")) {
                combinationBase.add(element.getElementsByTagName(attribute.getNodeName()).getLength());
                return 1;
            }
        }

        return 0;
    }

    private int expandAttributesIterateElements(NodeList elements, int[] configurationSchema) {
        for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i) instanceof Element) {
                Element element = (Element) elements.item(i);

                if (expandAttributesIterateAtributes(element, configurationSchema) == 1) {
                    return 1;
                }

                expandAttributesIterateElements(element.getChildNodes(), configurationSchema);
            }
        }

        return 0;
    }

    private int expandAttributesIterateAtributes(Element element, int[] configurationSchema) {
        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);

            if (attribute.getNodeValue().equals("multi")) {
                NodeList list = element.getElementsByTagName(attribute.getNodeName());

                for (int j = 0; j < configurationSchema.length; j++) {
                    if (configurationSchema[j] != -1) {
                        attribute.setNodeValue(list.item(configurationSchema[j]).getFirstChild().getNodeValue());
                        configurationSchema[j] = -1;
                        break;
                    }
                }

                list = element.getChildNodes();

                for (int j = 0; j < list.getLength(); j++) {
                    if (list.item(j).getNodeName().equals(attribute.getNodeName())) {
                        element.removeChild(list.item(j));
                    }
                }

                return 1;
            }
        }

        return 0;
    }

}
