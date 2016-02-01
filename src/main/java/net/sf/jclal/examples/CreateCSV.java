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
package net.sf.jclal.examples;

import java.io.IOException;
import net.sf.jclal.util.learningcurve.LearningCurveUtility;

/**
 * Example to create a CSV file from the experimental results located in
 * respots_CSV folder.
 *
 * @author Eduardo Perez Perdomo
 * @author Oscar Gabriel Reyes Pupo
 */
public class CreateCSV {

    /**
     *
     * @param args the command line arguments
     * @throws IOException The exception that will be launched
     */
    public static void main(String[] args) throws IOException {
        LearningCurveUtility.csvAULCFileReports("reports_CSV", args[0], null,"false");
    }
}
