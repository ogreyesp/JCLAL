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
package net.sf.jclal.dataset;

import java.util.logging.Level;
import java.util.logging.Logger;
import mulan.data.ConverterLibSVM;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;

/**
 * Class that represents a LibSVM multilabel dataset. It converts the LibSVM dataset to a MULAN dataset
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class LibSVMMultiLabelDataset extends MulanDataset {

	private static final long serialVersionUID = -3732802677044804225L;

	/**
     * Constructs a LibSVM MultiLabel Dataset
     *
     * @param sourceFilename the name of the source file
     */
    public LibSVMMultiLabelDataset(String sourceFilename) {
        
        super();
        try {
            ConverterLibSVM.convertFromLibSVM("", sourceFilename, sourceFilename, sourceFilename);
            
            setDataset(new MultiLabelInstances(sourceFilename+".arff", sourceFilename+".xml"));
        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(LibSVMMultiLabelDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}