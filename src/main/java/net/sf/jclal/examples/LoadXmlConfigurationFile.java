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
import java.util.List;
import net.sf.jclal.util.xml.XMLConfigurationReader;

/**
 * Example where a xml configuration is loaded.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class LoadXmlConfigurationFile {

    /**
     * Main method
     * 
     * @param args the command line arguments
     * 
     * @throws FileNotFoundException The exception that will be launched
     * @throws IOException The exception that will be launched
     * @throws Exception The exception that will be launched
     * 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        File identatedXml = new File("examples/xmlFormat.cfg");

        XMLConfigurationReader reader = new XMLConfigurationReader(identatedXml);

        //options listed in XMLConfigurationReader class
        reader.loadXmlFile();

        System.out.println("Evaluation Method Type: " + reader.readEvaluationMethodType());

        System.out.println("Algorithm type: " + reader.readAlgorithmType());

        System.out.println("Query Strategy: " + reader.readQueryStrategyType());

        System.out.println("Scenario: " + reader.readScenarioType());

        List lista = reader.readClassifierTypeList();

        System.out.println("Classifiers number: " + lista.size());

        System.out.println("Done.");
    }
}
