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
package net.sf.jclal.util.matrixFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.matrix.Maths;

/**
 *
 * Utility class to handle a matrix over the main memory or over a file.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class Matrix {

	/**
	 * Path file path
	 */
	private String matrixFilePath = null;
	/**
	 * Matrix filename
	 */
	private String matrixFileName = null;
	/**
	 * The file of a matrix
	 */
	private File matrixFile;
	/**
	 * Access to the matrix
	 */
	private SeekableByteChannel fileAccessN;
	private ByteBuffer buff;
	private DoubleBuffer doubleB;

	/**
	 * The array that stores the matrix over the main memory, it is null if the
	 * matrix is stored over a file
	 */
	private double A[][];

	/**
	 * By default 8 bytes (double) will be reads or writen
	 */
	private final int bytesElements = 8;

	/**
	 * Rows
	 */
	protected long m;
	/**
	 * Columns
	 */
	protected long n;

	/**
	 * Denote if the matrix is stored over a file and not over the main memory,
	 * by default the matrix is stored over the main memory
	 */
	private boolean overFile = false;

	/**
	 * The default value of the matrix is 0. The user does not have to worry
	 * about establishing the path of the matrix file, a temporal file is
	 * automatically created.
	 *
	 * @param m
	 *            The number of rows.
	 * @param n
	 *            The number of columns.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 * @throws java.lang.Exception
	 *             The exception that will be launched.
	 */
	public Matrix(int m, int n, boolean overFile) throws Exception {

		this.m = m;
		this.n = n;
		this.overFile = overFile;

		if (overFile) {
			openFileMatrix();
		} else {
			A = new double[m][n];
		}
	}

	/**
	 * Construct an m-by-n constant matrix.
	 *
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param s
	 *            Fill the matrix with this scalar value.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 */
	public Matrix(int m, int n, double s, boolean overFile) {

		try {
			this.m = m;
			this.n = n;
			this.overFile = overFile;

			if (overFile) {
				openFileMatrix();
			} else {
				A = new double[m][n];
			}

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					set(i, j, s);
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Construct a matrix from a 2-D array.
	 *
	 * @param arr
	 *            Two-dimensional array of doubles.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 * @throws IllegalArgumentException
	 *             All rows must have the same length
	 */
	public Matrix(double[][] arr, boolean overFile) {

		try {

			m = arr.length;
			n = arr[0].length;
			this.overFile = overFile;

			for (int i = 0; i < m; i++) {
				if (arr[i].length != n) {
					throw new IllegalArgumentException("All rows must have the same length.");
				}
			}

			if (overFile) {
				openFileMatrix();
			} else {
				A = new double[(int) m][(int) n];
			}

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					set(i, j, arr[i][j]);
				}
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Construct a matrix quickly without checking arguments.
	 *
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 */
	public Matrix(double[][] A, int m, int n, boolean overFile) {

		try {
			this.m = m;
			this.n = n;
			this.overFile = overFile;

			if (overFile) {
				openFileMatrix();
			}

			for (int i = 0; i < this.m; i++) {
				for (int j = 0; j < this.n; j++) {
					set(i, j, A[i][j]);
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Construct a matrix from a 2-D array.
	 *
	 * @param arr
	 *            Two-dimensional array of doubles.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 * @throws IllegalArgumentException
	 *             All rows must have the same length
	 */
	public Matrix(Matrix arr, boolean overFile) {

		try {

			m = arr.getRowDimension();
			n = arr.getColumnDimension();
			this.overFile = overFile;

			if (overFile) {
				openFileMatrix();
			} else {
				A = new double[(int) m][(int) n];
			}

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					set(i, j, arr.get(i, j));
				}
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Construct a matrix quickly without checking arguments.
	 *
	 * @param arr
	 *            Two-dimensional array of doubles.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 */
	public Matrix(Matrix arr, int m, int n, boolean overFile) {

		try {

			this.m = m;
			this.n = n;
			this.overFile = overFile;

			if (overFile) {
				openFileMatrix();
			} else {
				A = new double[m][n];
			}

			for (int i = 0; i < this.m; i++) {
				for (int j = 0; j < this.n; j++) {
					set(i, j, arr.get(i, j));
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Construct a matrix from a one-dimensional array
	 *
	 * @param vals
	 *            One-dimensional array of doubles, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 * @throws IllegalArgumentException
	 *             Array length must be a multiple of m.
	 */
	public Matrix(double vals[], int m, boolean overFile) {

		try {
			this.m = m;
			this.overFile = overFile;

			n = (m != 0 ? vals.length / m : 0);

			if (m * n != vals.length) {
				throw new IllegalArgumentException("Array length must be a multiple of m.");
			}

			if (overFile) {
				openFileMatrix();
			} else {
				A = new double[m][(int) n];
			}

			for (int i = 0; i < this.m; i++) {
				for (int j = 0; j < n; j++) {
					set(i, j, vals[i + j * m]);
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Open the file that will store the matrix
	 *
	 * @throws FileNotFoundException
	 *             The type of exception to launch.
	 */
	private void openFileMatrix() throws FileNotFoundException {

		if (fileAccessN == null) {

			try {

				Date date = new Date(System.currentTimeMillis());
				String time = date.toString().replaceAll(" ", "_").replaceAll(":", "-");
				String newName = "matrix_" + time + ".mtx";

				File parent = new File("");

				matrixFile = File.createTempFile(newName, ".mtx", parent.getAbsoluteFile());

				RandomAccessFile fileAccess = new RandomAccessFile(matrixFile, "rw");

				long length = m * n * getBytesElements();

				fileAccess.setLength(length);
				fileAccess.close();

				Path p = FileSystems.getDefault().getPath(matrixFile.getAbsolutePath());
				fileAccessN = Files.newByteChannel(p, StandardOpenOption.WRITE, StandardOpenOption.READ);

				buff = ByteBuffer.allocateDirect(getBytesElements());
				doubleB = buff.asDoubleBuffer();

			} catch (IOException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Close the file
	 *
	 * @throws IOException
	 *             The type of exception to launch.
	 */
	protected void closeFileMatrix() throws IOException {

		if (fileAccessN != null) {

			fileAccessN.close();
			matrixFile.delete();
		}
	}

	/**
	 * By default 8 bytes (double) will be reads or writen
	 *
	 * @return Returns the number of bytes used to store the data.
	 */
	public int getBytesElements() {
		return bytesElements;
	}

	/**
	 * Get row dimension.
	 *
	 * @return m, the number of rows.
	 */
	public int getRowDimension() {
		return (int) m;
	}

	/**
	 * Get column dimension.
	 *
	 * @return n, the number of columns.
	 */
	public int getColumnDimension() {
		return (int) n;
	}

	/**
	 * The file of a matrix
	 *
	 * @return Return the matrix file.
	 */
	public File getMatrixFile() {
		return matrixFile;
	}

	/**
	 * The file of a matrix
	 *
	 * @param matrixFile
	 *            The file where the data will be stored.
	 */
	public void setMatrixFile(File matrixFile) {
		this.matrixFile = matrixFile;
	}

	/**
	 * Get the path of matrix file
	 *
	 * @return Returns the file path of the matrix file.
	 */
	public String getMatrixFilePath() {
		return matrixFilePath;
	}

	/**
	 * Set the path of the matrix file
	 *
	 * @param matrixFilePath
	 *            The path of the matrix file
	 */
	public void setMatrixFilePath(String matrixFilePath) {
		this.matrixFilePath = matrixFilePath;
	}

	/**
	 * Get the Matrix filename
	 *
	 * @return The matrix file name.
	 */
	public String getMatrixFileName() {
		return matrixFileName;
	}

	/**
	 * Set the Matrix filename
	 *
	 * @param matrixFileName
	 *            The matrix file name.
	 */
	public void setMatrixFileName(String matrixFileName) {
		this.matrixFileName = matrixFileName;
	}

	/**
	 * Make a copy of a matrix
	 *
	 * @return a copy of the matrix
	 */
	public Matrix copy() {

		Matrix x = null;

		try {

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					x.set(i, j, get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Clone the Matrix object.
	 *
	 * @return a clone of the object
	 */
	@Override
	public Object clone() {
		return this.copy();
	}

	/**
	 * Make a one-dimensional column copy of the internal array.
	 *
	 * @return Matrix elements in a one-dimensional array by columns.
	 */
	public double[] getColumnPackedCopy() {

		double[] vals = new double[getRowDimension() * getColumnDimension()];

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				vals[i + j * getRowDimension()] = get(i, j);
			}
		}

		return vals;
	}

	/**
	 * Make a one-dimensional row copy of the internal array.
	 *
	 * @return Matrix elements in a one-dimensional array by rows.
	 */
	public double[] getRowPackedCopy() {
		double[] vals = new double[getRowDimension() * getColumnDimension()];

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				vals[i * getColumnDimension() + j] = get(i, j);
			}
		}
		return vals;
	}

	/**
	 * Get a single element.
	 *
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @throws ArrayIndexOutOfBoundsException
	 *             The type of exception to launch.
	 */
	public double get(int i, int j) {

		double value = 0;

		if (!overFile) {
			value = A[i][j];
		} else {
			try {

				value = readValue(i, j);

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return value;
	}

	/**
	 * Read the value in the position (i,j)
	 *
	 * @param i
	 *            The row
	 * @param j
	 *            The column
	 * @return The double value from the matrix file
	 * @throws IOException
	 *             The exception to launch
	 */
	public double readValue(int i, int j) throws IOException {
		fileAccessN.position(pos(i, j));

		buff.clear();
		doubleB.clear();

		fileAccessN.read(buff);

		return doubleB.get();
	}

	/**
	 * Set a single element in the position (i,j)
	 *
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @throws ArrayIndexOutOfBoundsException
	 *             The type of exception to launch.
	 */
	public void set(int i, int j, double s) {

		if (!overFile) {

			A[i][j] = s;

		} else {

			try {

				writeValue(i, j, s);

			} catch (FileNotFoundException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}

	/**
	 * Write the value in the position (i,j)
	 * 
	 * @param i
	 *            The row
	 * @param j
	 *            The column
	 * @param s
	 *            The value to write in the matrix file
	 * @throws IOException
	 *             The exception to launch
	 */
	public void writeValue(int i, int j, double s) throws IOException {
		fileAccessN.position(pos(i, j));

		buff.clear();
		doubleB.clear();

		doubleB.put(s);

		fileAccessN.write(buff);
	}

	/**
	 * Get a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public Matrix getMatrix(int i0, int i1, int j0, int j1) {

		Matrix x = null;

		try {

			x = new Matrix(i1 - i0 + 1, j1 - j0 + 1, overFile);

			try {
				for (int i = i0; i <= i1; i++) {
					for (int j = j0; j <= j1; j++) {
						x.set(i - i0, j - j0, get(i, j));
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public Matrix getMatrix(int[] r, int[] c) {

		Matrix x = null;

		try {
			x = new Matrix(r.length, c.length, overFile);

			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = 0; j < c.length; j++) {

						x.set(i, j, get(r[i], c[j]));
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public Matrix getMatrix(int i0, int i1, int[] c) {

		Matrix x = null;

		try {

			x = new Matrix(i1 - i0 + 1, c.length, overFile);

			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {

					x.set(i - i0, j, get(i, c[j]));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public Matrix getMatrix(int[] r, int j0, int j1) {

		Matrix x = null;

		try {

			x = new Matrix(r.length, j1 - j0 + 1, overFile);

			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = j0; j <= j1; j++) {
						x.set(i, j - j0, get(r[i], j));
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Set a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param x
	 *            A(i0:i1,j0:j1)
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public void setMatrix(int i0, int i1, int j0, int j1, Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {

					set(i, j, x.get(i - i0, j - j0));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            A(r(:),c(:))
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public void setMatrix(int[] r, int[] c, Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {

					set(r[i], c[j], x.get(i, j));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param x
	 *            A(r(:),j0:j1)
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public void setMatrix(int[] r, int j0, int j1, Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {

					set(r[i], j, x.get(i, j - j0));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            A(i0:i1,c(:))
	 * @throws ArrayIndexOutOfBoundsException
	 *             Submatrix indices
	 */
	public void setMatrix(int i0, int i1, int[] c, Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {

					set(i, c[j], x.get(i - i0, j));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Returns true if the matrix is symmetric. (FracPete: taken from old
	 * weka.core.Matrix class)
	 *
	 * @return boolean true if matrix is symmetric.
	 */
	public boolean isSymmetric() {

		int nr = getRowDimension(), nc = getColumnDimension();

		if (nr != nc) {
			return false;
		}

		for (int i = 0; i < nc; i++) {
			for (int j = 0; j < i; j++) {

				if (get(i, j) != get(j, i)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Matrix transpose.
	 *
	 * @return A'
	 */
	public Matrix transpose() {

		Matrix x = null;

		try {

			x = new Matrix(getColumnDimension(), getRowDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {

					x.set(j, i, get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * One norm
	 *
	 * @return maximum column sum.
	 */
	public double norm1() {

		double f = 0;

		for (int j = 0; j < getColumnDimension(); j++) {

			double s = 0;

			for (int i = 0; i < getRowDimension(); i++) {

				s += Math.abs(get(i, j));
			}

			f = Math.max(f, s);
		}

		return f;
	}

	/**
	 * Infinity norm
	 *
	 * @return maximum row sum.
	 */
	public double normInf() {
		double f = 0;
		for (int i = 0; i < getRowDimension(); i++) {
			double s = 0;
			for (int j = 0; j < getColumnDimension(); j++) {
				s += Math.abs(get(i, j));
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Frobenius norm
	 *
	 * @return sqrt of sum of squares of all elements.
	 */
	public double normF() {
		double f = 0;
		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				f = Maths.hypot(f, get(i, j));
			}
		}
		return f;
	}

	/**
	 * Check if size(A) == size(B)
	 *
	 * @param B
	 *            The other matrix
	 */
	public void checkMatrixDimensions(Matrix B) {
		if (B.getRowDimension() != getRowDimension() || B.getColumnDimension() != getColumnDimension()) {
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

	/**
	 * Unary minus
	 *
	 * @return -A
	 */
	public Matrix uminus() {

		Matrix x = null;

		try {

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					x.set(i, j, -get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * C = A + B
	 *
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
	public Matrix plus(Matrix B) {

		Matrix x = null;

		try {
			checkMatrixDimensions(B);

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					x.set(i, j, get(i, j) + B.get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * A = A + B
	 *
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
	public Matrix plusEquals(Matrix B) {

		checkMatrixDimensions(B);

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				set(i, j, get(i, j) + B.get(i, j));
			}
		}

		return this;
	}

	/**
	 * C = A - B
	 *
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
	public Matrix minus(Matrix B) {

		Matrix x = null;

		try {
			checkMatrixDimensions(B);

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					x.set(i, j, get(i, j) - B.get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * A = A - B
	 *
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
	public Matrix minusEquals(Matrix B) {

		checkMatrixDimensions(B);

		for (int i = 0; i < getRowDimension(); i++) {

			for (int j = 0; j < getColumnDimension(); j++) {
				set(i, j, get(i, j) - B.get(i, j));
			}
		}
		return this;
	}

	/**
	 * Element-by-element multiplication, C = A.*B
	 *
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */
	public Matrix arrayTimes(Matrix B) {

		Matrix x = null;

		try {
			checkMatrixDimensions(B);

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {

					x.set(i, j, get(i, j) * B.get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Element-by-element multiplication in place, A = A.*B
	 *
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */
	public Matrix arrayTimesEquals(Matrix B) {

		checkMatrixDimensions(B);

		for (int i = 0; i < getRowDimension(); i++) {

			for (int j = 0; j < getColumnDimension(); j++) {

				set(i, j, get(i, j) * B.get(i, j));
			}
		}

		return this;
	}

	/**
	 * Element-by-element right division, C = A./B
	 *
	 * @param B
	 *            another matrix
	 * @return A./B
	 */
	public Matrix arrayRightDivide(Matrix B) {

		Matrix x = null;

		try {

			checkMatrixDimensions(B);

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					x.set(i, j, get(i, j) / B.get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Element-by-element right division in place, A = A./B
	 *
	 * @param B
	 *            another matrix
	 * @return A./B
	 */
	public Matrix arrayRightDivideEquals(Matrix B) {

		checkMatrixDimensions(B);

		for (int i = 0; i < getRowDimension(); i++) {

			for (int j = 0; j < getColumnDimension(); j++) {

				set(i, j, get(i, j) / B.get(i, j));
			}
		}
		return this;
	}

	/**
	 * Element-by-element left division, C = A.\B
	 *
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */
	public Matrix arrayLeftDivide(Matrix B) {

		Matrix x = null;

		try {
			checkMatrixDimensions(B);

			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {

					x.set(i, j, B.get(i, j) / get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Element-by-element left division in place, A = A.\B
	 *
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */
	public Matrix arrayLeftDivideEquals(Matrix B) {

		checkMatrixDimensions(B);

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				set(i, j, B.get(i, j) / get(i, j));
			}
		}

		return this;
	}

	/**
	 * Multiply a matrix by a scalar, C = s*A
	 *
	 * @param s
	 *            scalar
	 * @return s*A
	 */
	public Matrix times(double s) {

		Matrix x = null;

		try {
			x = new Matrix(getRowDimension(), getColumnDimension(), overFile);

			for (int i = 0; i < getRowDimension(); i++) {
				for (int j = 0; j < getColumnDimension(); j++) {

					x.set(i, j, s * get(i, j));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Multiply a matrix by a scalar in place, A = s*A
	 *
	 * @param s
	 *            scalar
	 * @return replace A by s*A
	 */
	public Matrix timesEquals(double s) {

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {

				set(i, j, s * get(i, j));
			}
		}
		return this;
	}

	/**
	 * Linear algebraic matrix multiplication, A * B
	 *
	 * @param B
	 *            another matrix
	 * @return Matrix product, A * B
	 * @throws IllegalArgumentException
	 *             Matrix inner dimensions must agree.
	 */
	public Matrix times(Matrix B) {

		Matrix x = null;

		try {

			if (B.getRowDimension() != getColumnDimension()) {
				throw new IllegalArgumentException("Matrix inner dimensions must agree.");
			}

			x = new Matrix(getRowDimension(), B.getColumnDimension(), overFile);

			double[] Bcolj = new double[getColumnDimension()];

			for (int j = 0; j < B.getColumnDimension(); j++) {

				for (int k = 0; k < getColumnDimension(); k++) {

					Bcolj[k] = B.get(k, j);
				}

				for (int i = 0; i < getRowDimension(); i++) {

					double s = 0;

					for (int k = 0; k < n; k++) {

						s += get(i, k) * Bcolj[k];
					}

					x.set(i, j, s);
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return x;
	}

	/**
	 * Matrix trace.
	 *
	 * @return sum of the diagonal elements.
	 */
	public double trace() {
		double t = 0;
		for (int i = 0; i < Math.min(getRowDimension(), getColumnDimension()); i++) {
			t += get(i, i);
		}
		return t;
	}

	/**
	 * Generate matrix with random elements
	 *
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param overFile
	 *            Indicates if the matrix will be stored over a file
	 * @return An m-by-n matrix with uniformly distributed random elements.
	 */
	public static Matrix random(int m, int n, boolean overFile) {

		Matrix a = null;

		try {

			a = new Matrix(m, n, overFile);

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					a.set(i, j, Math.random());
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return a;
	}

	/**
	 * Generate identity matrix
	 *
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param overFile
	 *            Indicates if the matrix is stored over the a file or over the
	 *            main memory
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */
	public static Matrix identity(int m, int n, boolean overFile) {

		Matrix a = null;

		try {

			a = new Matrix(m, n, overFile);

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					a.set(i, j, (i == j ? 1.0 : 0.0));
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
		}

		return a;
	}

	/**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 *
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */
	public void print(PrintWriter output, NumberFormat format, int width) {
		output.println(); // start on new line.

		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				String s = format.format(get(i, j)); // format the number
				int padding = Math.max(1, width - s.length()); // At _least_ 1
																// space
				for (int k = 0; k < padding; k++) {
					output.print(' ');
				}
				output.print(s);
			}
			output.println();
		}
		output.println(); // end with blank line.
	}

	/**
	 * Destroy the matrix
	 */
	public void destroy() {

		if (overFile) {
			try {
				closeFileMatrix();
			} catch (IOException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		A = null;

	}

	/**
	 * Destroy the matrix
	 */
	public void destroyOnExit() {

		if (overFile) {
			try {
				closeFileMatrix();
			} catch (IOException ex) {
				Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		A = null;

	}

	/**
	 * Verifies if the dimensions are in the correct range
	 *
	 * @param row
	 *            The row
	 * @param column
	 *            The column
	 * @return Returns if the dimensions are in the correct range.
	 */
	public boolean verifyRange(int row, int column) {
		return row >= 0 && row < this.m && column >= 0 && column < this.n;
	}

	/**
	 * It finds the position where the value is in the file
	 *
	 * @param row
	 *            The row
	 * @param column
	 *            The column
	 * @return The position where the value is in the file.
	 */
	public long pos(int row, int column) {
		return (row * this.n + column) * getBytesElements();
	}

	/**
	 * Denote if the matrix is stored over a file and not over the main memory,
	 * by default the the matrix is stored over the main memory
	 *
	 * @return Whether the stored data is over file or not/
	 */
	public boolean isOverFile() {
		return overFile;
	}

	/**
	 * Denote if the matrix is stored over a file and not over the main memory,
	 * by default the the matrix is stored over the main memory
	 *
	 * @param overFile
	 *            Set whether the data will be stored over a file or main
	 *            memory.
	 */
	public void setOverFile(boolean overFile) {
		this.overFile = overFile;
	}
}