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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.util.matrixFile.Matrix;

/**
 * Example where a matrix handled over a file is created.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class CreateMatrixFile {

    /**
     * Two arguments are needed, first the number of rows, then the number of
     * columns. Example: java -jar program 10000 10000
     *
     * @param args the command line arguments
     * @throws IOException The exception that will be launched
     */
    public static void main(String[] args) throws IOException {
        try {
            int rows = 10000;
            int columns = 10000;

            if (args.length > 0) {
                rows = Integer.parseInt(args[0]);
            }
            if (args.length > 1) {
                columns = Integer.parseInt(args[1]);
            }

            double elements = rows * columns;
            double discSpaceMb = (double) ((elements * 8) / 1024) / 1024;

            System.out.println("The program need " + discSpaceMb + " Mb of disc space to create a "
                    + "matrix with " + elements + " doubles");

            Matrix matrix = new Matrix(rows, columns, true);

            //Filling the matrix
            Random random = new Random();

            System.out.println("Filling the matrix...");

            int rValues[] = new int[10000];
            int cValues[] = new int[10000];

            for (int i = 0; i < 10000; i++) {

                rValues[i] = random.nextInt(rows);
                cValues[i] = random.nextInt(columns);

                double v = random.nextDouble();

                System.out.println("[" + rValues[i] + "," + cValues[i] + "]:" + v);

                matrix.set(rValues[i], cValues[i], v);
            }

            //Reading the matrix
            System.out.println("Reading the matrix...");

            for (int i = 0; i < 10000; i++) {

                System.out.println("Read value..[" + rValues[i] + "," + cValues[i] + "] " + matrix.get(rValues[i], cValues[i]));
            }

            System.out.println("Deleting matrix...");

            matrix.destroy();

        } catch (Exception ex) {
            Logger.getLogger(CreateMatrixFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
