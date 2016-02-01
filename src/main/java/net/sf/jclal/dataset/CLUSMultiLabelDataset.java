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
import mulan.data.ConverterCLUS;
import mulan.data.MultiLabelInstances;

/**
 * Class that represents a CLUS multilabel dataset. It converts a CLUS dataset
 * to a Mulan dataset
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class CLUSMultiLabelDataset extends MulanDataset {

	private static final long serialVersionUID = 6816732263238574672L;

	/**
	 * Constructs a LibSVM MultiLabel Dataset
	 *
	 * @param sourceFilename
	 *            the name of the source file
	 */
	public CLUSMultiLabelDataset(String sourceFilename) {

		super();

		try {

			ConverterCLUS.convert(sourceFilename, sourceFilename + ".arff", sourceFilename + ".xml");

			setDataset(new MultiLabelInstances(sourceFilename + ".arff", sourceFilename + ".xml"));

		} catch (Exception ex) {
			Logger.getLogger(CLUSMultiLabelDataset.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}