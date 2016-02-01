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

import java.io.*;

public class Files{

    /**
     * <p>
     * Read a file and returns the content
     * </p>
     * @param fileName Name of the file to read
     * @return A string with the content of the file
     */
    public static String readFile(String fileName) {
        String content = "";
        try {
                FileInputStream fis = new FileInputStream(fileName);
                byte[] piece = new byte[4096];
                int readBytes = 0;
                while (readBytes != -1) {
				readBytes = fis.read(piece);
				if (readBytes != -1) {
					content += new String(piece, 0, readBytes);
				}
		}
		fis.close();
	    }
	catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }

        return content;
    }


    /**
     * <p>
     * Writes data in the file, overwriting previous content 
     * </p>
     * @param fileName Name of the file to read
     * @param content The content to be written
     */
    public static void writeFile (String fileName, String content) {
        try {
                FileOutputStream f = new FileOutputStream(fileName);
                DataOutputStream fis = new DataOutputStream((OutputStream) f);
                fis.writeBytes(content);
                fis.close();
	    }
        catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }
    }


    /**
     * <p>
     * Adds data in the file, avoiding overwrite previous content 
     * </p>
     * @param fileName Name of the file to read
     * @param content The content to be written
     */
    public static void addToFile (String fileName, String content) {
        try {
                RandomAccessFile fis = new RandomAccessFile(fileName, "rw");
                fis.seek(fis.length());
                fis.writeBytes(content);
                fis.close();
            }
        catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
    }
    
}

