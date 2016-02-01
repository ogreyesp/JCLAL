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
package net.sf.jclal.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Utils;

/**
 * Utility class to handle a file.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class FileUtil {

	/**
	 * It allows to realize a copy of an object
	 *
	 * @param x
	 *            The object to copy
	 * @param copy
	 *            The file where the object will be saved
	 * @throws IOException
	 *             Launch an exception in case that an error occurs
	 * @throws ClassNotFoundException
	 *             Launch an exception in case that an error occurs
	 */
	public static void saveObjectFile(Object x, File copy) throws IOException, ClassNotFoundException {

		Path p = copy.toPath();

		ObjectOutputStream objOut = new ObjectOutputStream(
				Files.newOutputStream(p, StandardOpenOption.WRITE, StandardOpenOption.APPEND));

		objOut.writeObject(x);
		objOut.flush();
		objOut.close();
		objOut = null;
	}

	/**
	 * It allows to load an object
	 *
	 * @param file
	 *            The file where the object was saved
	 * 
	 * @return The object loaded
	 * 
	 * @throws IOException
	 *             Launch an exception in case that an error occurs
	 * @throws ClassNotFoundException
	 *             Launch an exception in case that an error occurs
	 */
	public static Object loadObjectFile(File file) throws IOException, ClassNotFoundException {

		Path p = file.toPath();

		ObjectInputStream objIn = new ObjectInputStream(Files.newInputStream(p, StandardOpenOption.READ));

		Object dev = objIn.readObject();
		objIn.close();

		objIn = null;

		p = null;

		return dev;
	}

	/**
	 * Delete the files that belong to fold executions
	 * 
	 * @param generalResults
	 *            Cross-validation general file reports
	 * @param endsWith
	 *            Name or title and extension of the file reports
	 */
	public static void deleteFoldsFiles(File generalResults, String endsWith) {

		if (generalResults == null || generalResults.getParentFile() == null
				|| generalResults.getParentFile().listFiles() == null) {
			return;
		}

		for (File report1 : generalResults.getParentFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return isReport(pathname);
			}
		})) {

			if (generalResults.getAbsolutePath().equals(report1.getAbsolutePath())) {
				continue;
			}
			if (report1.exists() && report1.getName().endsWith(endsWith)) {
				report1.delete();
			}
		}

	}

	/**
	 * Create a file with the extension of the standard reports.
	 *
	 * @param pathName
	 *            Pathname of the file.
	 * @return A file with the extension of the standard reports.
	 */
	public static File reportFile(String pathName) {
		return new File(pathName + ".report.txt");
	}

	/**
	 * Return true if the report file is already finished, a report is finished
	 * when the words 'Time end' appear.
	 *
	 * @param reportFile
	 *            The pathname of a report to analyze.
	 * @return If the reports is finished.
	 * @throws IOException
	 *             Launch an exception in case that an error occurs
	 */
	public static boolean reportFinish(File reportFile) throws IOException {
		BufferedReader in = Files.newBufferedReader(reportFile.toPath());
		String line;
		boolean result = false;
		while ((line = in.readLine()) != null) {
			if (line.contains("Time end")) {
				result = true;
				break;
			}
		}
		in.close();
		in = null;
		return result;
	}

	/**
	 * Creates a file for which its name will be extracted from the file model
	 * if the defaultName is null or empty, and will be created in the same
	 * level of sourceFile.
	 *
	 * @param source
	 *            The source file
	 * @param defaultName
	 *            The default name of the file
	 * @return The progressive file
	 */
	public static File createProgressiveFile(File source, String defaultName) {
		File exit;
		if (defaultName == null || defaultName.isEmpty()) {

			String name = source.getName();
			exit = new File(source.getParentFile(), name);

			int c = 0;
			while (exit.exists()) {
				exit = new File(source.getParentFile(), name + c++);
			}

			name = null;
		} else {
			exit = new File(defaultName);
		}

		return exit;
	}

	/**
	 * Create a file
	 *
	 * @param file
	 *            The file to create
	 * @param replace
	 *            If the file must be replaced.
	 * @throws Exception
	 *             Launch an exception in case that some error occurs.
	 */
	public static void createFile(File file, boolean replace) throws Exception {

		if (file == null) {
			return;
		}

		if (file.exists()) {
			if (replace) {
				file.delete();
				file.createNewFile();
			} else {
				throw new Exception("The file " + file.getAbsolutePath() + " already exists.");
			}
		} else {
			file.createNewFile();
		}
	}

	/**
	 * Creates a temporal file
	 *
	 * @param prefix
	 *            The prefix of the file
	 * @param suffix
	 *            The suffix of the file
	 * @return The temporal file created
	 */
	public static File createTempFile(String prefix, String suffix) {

		try {

			Date date = new Date(System.currentTimeMillis());
			String time = date.toString().replaceAll(" ", "_").replaceAll(":", "-");
			String newName = prefix + "_" + time;

			return File.createTempFile(newName, suffix);

		} catch (IOException ex) {
			Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * Utility method to obtain all the files of a directory that fulfill with a
	 * condition.
	 *
	 * @param directory
	 *            The directory that will be analyzed.
	 * @param filter
	 *            The filter that will be used.
	 * @param listToFill
	 *            The list where there will be stored the files that expire with
	 *            the filter.
	 */
	public static void getFilesInDirectory(File directory, FilenameFilter filter, List<File> listToFill) {

		if (directory.exists()) {

			if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				for (File file : files) {
					getFilesInDirectory(file, filter, listToFill);
				}
			} else if (directory.isFile()) {
				if (filter.accept(directory, directory.getName())) {
					listToFill.add(directory);
				}
			}

		}
	}

	/**
	 * Order files by path
	 * 
	 * @param files
	 *            The files to order.
	 * @param ascendentOrder
	 *            True if the files will be ordered in ascendent order according
	 *            with the path, false otherwise.
	 */
	public static void orderFilesByPathName(List<File> files, final boolean ascendentOrder) {
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				int value = file1.getPath().compareToIgnoreCase(file2.getPath());
				if (ascendentOrder) {
					return value;
				}
				return -value;
			}
		});
	}

	/**
	 * Write a file with the specified content
	 * 
	 * @param file
	 *            The file where the content will be stored, create a new file
	 *            if it does not exist, the content will be written to the end
	 *            of the file
	 * 
	 * @param file
	 *            The file where the content will be written
	 * @param content
	 *            The content to write
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static void writeFile(File file, String content) throws IOException {
		Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE);
	}

	/**
	 * Read a file
	 * 
	 * @param file
	 *            The file to read
	 * @return The content of the file
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static String readFile(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	/**
	 * Read a file
	 * 
	 * @param file
	 *            The file to read
	 * @return The string reader
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static StringReader stringReader(File file) throws IOException {
		return new StringReader(readFile(file));
	}

	/**
	 * Method that allows to load a file that is located inside the source code
	 * of the program, e.g. config/util.props
	 *
	 * @param fileSourcePath
	 *            Path to the source file
	 * @return The source file
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 * @throws URISyntaxException
	 *             Launch an exception in case that some error occurs.
	 */
	public static File loadFileSourceCode(String fileSourcePath) throws URISyntaxException, IOException {
		Utils util = new Utils();
		URL url = util.getClass().getClassLoader().getResource(fileSourcePath);
		File x;
		try {
			x = new File(url.toURI());
		} catch (Exception e) {

			InputStream input = url.openStream();

			File temp = new File(fileSourcePath);
			x = File.createTempFile(temp.getName(), ".temp");

			Files.copy(input, x.toPath(), StandardCopyOption.REPLACE_EXISTING);

			input.close();
			input = null;
		}

		// clean
		util = null;
		url = null;

		return x;
	}

	/**
	 * Delete a file if it was created in the temporal directory of the system
	 *
	 * @param file
	 *            The file to delete
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static void deleteFileIfTemp(File file) throws IOException {
		File test = File.createTempFile("test", null);
		if (test.getParentFile().equals(file.getParentFile())) {
			file.delete();
		}
		test.delete();
		test = null;
	}

	/**
	 * Make a copy of a file in the temporal folder of the system
	 *
	 * @param original
	 *            The original file
	 * @return A copy of the original file
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static File createTempCopyFile(File original) throws IOException {
		File n = File.createTempFile("copy" + original.getName(), null);
		copyFile(original, n);
		return n;
	}

	/**
	 * Make a copy of a file, if the destination file exists, then the
	 * destination file is replaced
	 *
	 * @param origin
	 *            The file to copy
	 * @param destination
	 *            The destination where the file will be copied
	 * @throws IOException
	 *             Launch an exception in case that some error occurs.
	 */
	public static void copyFile(File origin, File destination) throws IOException {
		Files.copy(origin.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static File fileDiferentParent(File old, File newOne, File fileOld) {
		File fileNewOne = new File(cleanFilePath(fileOld.getAbsolutePath())
				.replaceFirst(cleanFilePath(old.getAbsolutePath()), cleanFilePath(newOne.getAbsolutePath())));

		return fileNewOne;
	}

	/**
	 * Replace '\' for '/'.
	 *
	 * @param x
	 *            The string to clean.
	 * @return The string.
	 */
	public static String cleanFilePath(String x) {
		return x.replace('\\', '/');
	}

	/**
	 * List the directories that exist into a parent directory
	 * 
	 * @param source
	 *            The parent directory
	 * @return The array of directories
	 */
	public static File[] listDirs(File source) {
		return source.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
	}

	/**
	 * List the reports that exist into a parent directory
	 * 
	 * @param parent
	 *            The parent directory
	 * @return The list of files
	 */
	public static List<File> listReports(File parent) {
		return Arrays.asList(parent.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && isReport(pathname);
			}
		}));
	}

	/**
	 * Return if the specified file is a report
	 * 
	 * @param x
	 *            The file
	 * @return True if the specified file is a report, false otherwise
	 */
	public static boolean isReport(File x) {
		BufferedReader read = null;
		boolean c = false;
		try {
			read = Files.newBufferedReader(x.toPath(), Charset.defaultCharset());

			String line;
			while ((line = read.readLine()) != null) {
				if (line.contains("Iteration:")) {
					c = true;
					break;
				}
			}

		} catch (Exception e) {
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException ex) {
				}
				read = null;
			}
		}

		return c;
	}

	/**
	 * Extract the report name
	 *
	 * @param report
	 *            The file to analyze.
	 * @return The report name.
	 */
	public static String nameReport(File report) {
		return report.getName().substring(report.getName().indexOf('-') + 1);
	}

	/**
	 * List the reports that exit into a directory.
	 *
	 * @param dir
	 *            The directory
	 * @param listToFill
	 *            The list of files.
	 */
	public static void getReportsInDirectory(File dir, List<File> listToFill) {

		if (dir.exists()) {

			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File fileC : files) {
					getReportsInDirectory(fileC, listToFill);
				}
			} else if (dir.isFile()) {
				if (FileUtil.isReport(dir)) {
					listToFill.add(dir);
				}
			}

		}
	}
}
