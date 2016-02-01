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

package net.sf.jclal.util.statisticalTest.statistical;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

public final class CSVFileFilter extends FileFilter {

    private Vector<String> extensions = new Vector<String>();
    private String filterName = null;

    /**
     * Set the filter name
     * @param fn New name of the filter
     */
    public void setFilterName(String fn) {
        filterName = new String(fn);
    }

    /**
     * Adds an extension to the filter
     * @param ex Nex extension to add
     */
    public void addExtension(String ex) {
        extensions.add(new String(ex));
    }

    /**
     * Test if the file is accepted
     * @param f File to be tested
     * @return True if the file is accepted, false if not
     */
    public boolean accept(File f) {
        String filename = f.getName();

        for (int i = 0; i < extensions.size(); i++) {
            if (filename.endsWith(extensions.elementAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the filter name
     * @return The filter name
     */
    public String getDescription() {
        return filterName;
    }
}
