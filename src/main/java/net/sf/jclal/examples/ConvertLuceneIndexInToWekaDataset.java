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
import java.io.FileReader;
import java.io.IOException;
import net.sf.jclal.util.dataset.LuceneIndexToWekaDataSet;
import weka.core.Instances;

/**
 * Example where a Lucene's index is converted to a Weka dataset.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class ConvertLuceneIndexInToWekaDataset {

    /**
     * 
     * Main menthod
     * 
     * @param args the command line arguments
     * @throws FileNotFoundException The exception that will be launched
     * @throws IOException The exception that will be launched
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File test = new File("datasets/test.arff");

        if (test.exists()) {
            test.delete();
        }

        LuceneIndexToWekaDataSet convert = new LuceneIndexToWekaDataSet();

        convert.convertLuceneToWekaClassification("datasets/test.arff", "datasets/indiceCreado_datasetdensityDiversity");

        Instances aa = new Instances(new FileReader("datasets/test.arff"));

        System.out.println(aa.numInstances());

    }
}