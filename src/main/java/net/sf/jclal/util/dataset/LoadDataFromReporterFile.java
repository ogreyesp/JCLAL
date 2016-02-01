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
package net.sf.jclal.util.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.file.FileUtil;

/**
 * Class used to load the evaluations of the experiments from files.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class LoadDataFromReporterFile {

    /**
     * File Reader
     */
    private BufferedReader reader;

    /**
     * Stores the evaluations of AL process
     */
    private ArrayList<AbstractEvaluation> evaluations;

    /**
     * The properties
     */
    private Properties properties;

    /**
     * Get the evauations
     *
     * @return The evaluations's measures.
     */
    public ArrayList<AbstractEvaluation> getEvaluations() {
        return evaluations;
    }

    /**
     * Set the evaluation measures to use
     *
     * @param evaluations The evaluations's measures.
     */
    public void setEvaluations(ArrayList<AbstractEvaluation> evaluations) {
        this.evaluations = evaluations;
    }

    /**
     * Get the properties
     *
     * @return The properties.
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Set the properties
     *
     * @param properties The properties.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Load the data from the active learning stored in a report file
     *
     * @param path The file path.
     */
    public LoadDataFromReporterFile(String path) {

        try {

            reader = new BufferedReader(FileUtil.stringReader(new File(path)));

            evaluations = new ArrayList<AbstractEvaluation>();

            properties = new Properties();

            loadData();

        } catch (FileNotFoundException e) {
            Logger.getLogger(LoadDataFromReporterFile.class.getName()).log(
                    Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(LoadDataFromReporterFile.class.getName()).log(
                    Level.SEVERE, null, e);
        }

    }

    /**
     * Load the data from the active learning stored in a report file
     *
     * @param file The file.
     */
    public LoadDataFromReporterFile(File file) {

        try {

            reader = new BufferedReader(FileUtil.stringReader(file));

            evaluations = new ArrayList<AbstractEvaluation>();

            properties = new Properties();

            loadData();

        } catch (FileNotFoundException e) {
            Logger.getLogger(LoadDataFromReporterFile.class.getName()).log(
                    Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(LoadDataFromReporterFile.class.getName()).log(
                    Level.SEVERE, null, e);
        }

    }

    /**
     * Load the information's measures from the file and the properties are
     * filled.
     */
    public void loadData() {

        try {
            String line;

            // Reading the header of the reporter file. The data are store as
            // properties
            line = reader.readLine();

            while ((line = reader.readLine()) != null && !(line).equals("\t\t")) {

                String tokens[] = line.split(":");

                properties.setProperty(tokens[0].trim(), tokens[1].trim());

            }

            StringBuilder cadenaEvaluation = new StringBuilder();

            while ((line = reader.readLine()) != null && !(line).startsWith("Time end")) {

                if (line.startsWith("Iteration:")) {

                    cadenaEvaluation = new StringBuilder();

                }

                if (line.equals("\t\t")) {

                    AbstractEvaluation eval = new AbstractEvaluation();

                    eval.loadMetrics(cadenaEvaluation.toString());

                    evaluations.add(eval);

                }

                cadenaEvaluation.append(line).append("\n");

            }

            if (line != null) {
                String tokens[] = line.split(":");

                properties.setProperty(tokens[0].trim(), tokens[1].trim());
            }
            
            reader.close();

        } catch (IOException e) {
            Logger.getLogger(LoadDataFromReporterFile.class.getName()).log(
                    Level.SEVERE, null, e);
        }
    }

}
