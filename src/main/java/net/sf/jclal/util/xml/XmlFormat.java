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
package net.sf.jclal.util.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.sf.jclal.util.file.FileUtil;

/**
 * Utility class to format (indent) a xml configuration file.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class XmlFormat {

    /**
     * Utility method to format a xml file
     *
     * @param xmlConfiguration The xml configuration file
     * @param newXmlConfiguration The new xml configuration file
     * @param replace If the old xml file must be replaced
     * @param identationSymbol Example: (1) For tab: "\t", (2) Space: " "
     * @throws FileNotFoundException The exception that will be launched
     * @throws IOException The exception that will be launched
     * @throws Exception The exception that will be launched
     */
    public static void formatXmlFile(File xmlConfiguration,
            File newXmlConfiguration, boolean replace, String identationSymbol)
            throws FileNotFoundException, IOException, Exception {

        FileUtil.createFile(newXmlConfiguration, replace);

        BufferedReader read = new BufferedReader(FileUtil.stringReader(xmlConfiguration));

        int level = 0;
        String line, initTag, endTag;
        while ((line = read.readLine()) != null) {

            initTag = tag(line);
            endTag = "</" + initTag;
            if (line.startsWith(endTag)) {
                --level;
            }

            //write in the new xml file
            for (int i = 0; i < level; i++) {
                FileUtil.writeFile(newXmlConfiguration, identationSymbol);
            }
            FileUtil.writeFile(newXmlConfiguration, line);
            FileUtil.writeFile(newXmlConfiguration, "\n");

            if (!endInLine(initTag, line)) {
                ++level;
            }

        }

        read.close();
    }

    /**
     * Return a xml tag
     *
     * @param line the line
     * @return a xml tag
     */
    public static String tag(String line) {
        String tag = "";
        char lineChar;
        cicle:
        for (int i = 0; i < line.length(); i++) {
            lineChar = line.charAt(i);
            switch (lineChar) {
                case '>':
                case ' ':
                    break cicle;
                case '<':
                case '/':
                    break;
                default:
                    tag += lineChar;
                    break;
            }
        }
        return tag;
    }

    /**
     * Return true if the tag ends in the current line
     *
     * @param initTag The initial tag
     * @param line The line
     * @return true if the tag ends in the current line
     */
    public static boolean endInLine(String initTag, String line) {
        if (line.endsWith("/>")) {
            return true;
        }
        String endTag = "</" + initTag + ">";
        if (line.endsWith(endTag)) {
            return true;
        }
        if (initTag.startsWith("?")) {
            return true;
        }
        return false;
    }
}
