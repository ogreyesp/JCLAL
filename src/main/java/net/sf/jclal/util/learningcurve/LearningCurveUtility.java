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
package net.sf.jclal.util.learningcurve;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import net.sf.jclal.evaluation.measure.AbstractEvaluation;
import net.sf.jclal.util.dataset.LoadDataFromReporterFile;
import net.sf.jclal.util.file.FileUtil;
import net.sf.jclal.util.statisticalTest.statistical.tests.PageTrendTest;
import weka.core.Utils;

/**
 * Utility class for extracting information from the learning curves.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class LearningCurveUtility {

	private static DecimalFormat nf;

	/**
	 * @param args
	 *            The command line arguments
	 *
	 *            Case 1: -csvAULC -- If is selected then -inputDirectory String
	 *            : input directory which contains the reports -measure String :
	 *            measure to analyze [-outputDirectory] String : output
	 *            directory [-min] false (default)|true : indicates whether the
	 *            measure is minimal or maximal
	 *
	 *            Case 2: -pageTest -- If is selected then -inputDirectory
	 *            String : input directory which contains the reports -measure
	 *            String : measure to analyze [-min] false (default)|true :
	 *            indicates whether the measure is minimal or maximal
	 *            [-outputDirectory] String : output directory
	 *
	 *            Case 3: -reportsRange -- If is selected then -inputDirectory
	 *            [String] : input directory which contains the reports
	 *            [-outputDirectory] String : output directory -ranges
	 *            "range1 range2": the ranges to analyze (can be more than one),
	 *            e.g. '34-56' 'first-45' '34-last';
	 *
	 *            Case 4: -csvForGraphic -- If is selected then -inputDirectory
	 *            [String] : input directory which contains the reports
	 *            [-outputDirectory] String : output directory -measure String :
	 *            measure to analyze
	 * 
	 * @throws IOException
	 *             Launch an exception in case that a error occurs
	 * @throws IOException
	 *             Launch an exception in case that a error occurs
	 *
	 */
	public static void main(String[] args) throws IOException, Exception {

		if (args == null || args.length == 0) {
			System.out.println("\n Case 1: -csvAULC -- If is selected then "
					+ "\n-inputDirectory String : input directory which contains the reports"
					+ "\n-measure String : measure to analyse" + "\n[-outputDirectory] String : output directory"
					+ "\n [-min] false (default)|true : indicates whether the measure is minimal or maximal"
					+ "\n\n Case 2: -pageTest -- If is selected then "
					+ "\n-inputDirectory String : input directory wich contains the reports"
					+ "\n -measure String : measure to analyse"
					+ "\n [-min] false (default)|true : indicates whether the measure is minimal or maximal"
					+ "\n[-outputDirectory] String : output directory"
					+ "\n\n Case 3: -reportsRange -- If is selected then "
					+ "\n-inputDirectory [String] : input directory which contains the reports"
					+ "\n [-outputDirectory] String  : output directory"
					+ "\n-ranges \"range1 range2\": the ranges to analyse (can be more than one), e.g. '34-56' 'first-45' '34-last'"
					+ "\n\n Case 4: -csvForGraphic -- If is selected then "
					+ "\n-inputDirectory String : input directory wich contains the reports"
					+ "\n -measure String : measure to analyse" + "\n[-outputDirectory] String : output directory");
		}

		setNumberFormat();

		String action = "";

		String inputDirectory = Utils.getOption("inputDirectory", args);
		String measure = Utils.getOption("measure", args);
		String out = Utils.getOption("outputDirectory", args);
		String outputDirectory = out.isEmpty() ? null : out;

		String min = String.valueOf(Utils.getFlag("min", args));

		String ranges = Utils.getOption("ranges", args);
		ArrayList<Range> rangesSeveral = new ArrayList<Range>(args.length);

		// Extract the arguments
		action = args[0];

		if (!ranges.isEmpty()) {
			StringTokenizer token = new StringTokenizer(ranges);
			String to;
			while (token.hasMoreTokens()) {
				to = token.nextToken();
				try {
					rangesSeveral.add(new Range(to));
				} catch (Exception e) {
					System.out.println("The range - " + to + " - is invalid.");
				}
			}
		}

		// Execute the action
		if (action.equalsIgnoreCase("-csvAULC")) {

			System.out.println("Starts preparation of CSV_AULC report files...");
			File r = csvAULCFileReports(inputDirectory, measure, outputDirectory, min);
			System.out.println("End preparation of CSV_AULC files. FilePath-> " + r.getAbsolutePath());

		}

		if (action.equalsIgnoreCase("-pageTest")) {

			System.out.println("Starting Page trend test");
			File r = pageTrendTest(inputDirectory, measure, min, outputDirectory);
			System.out.println("Ending Page trend test. FilePath-> " + r.getAbsolutePath());

		}

		if (action.equalsIgnoreCase("-reportsRange")) {

			System.out.println("Starts preparation of reports with range of iteration...");
			File dir = reportsRange(inputDirectory, outputDirectory, rangesSeveral);
			System.out.println("End preparation of reports. FilePath-> " + dir.getAbsolutePath());

		}

		if (action.equalsIgnoreCase("-csvForGraphic")) {

			File dir = createSingleCSVForGraphic(inputDirectory, measure, outputDirectory);

			System.out.println("End preparation of reports. FilePath-> " + dir.getAbsolutePath());
		}
	}

	/**
	 * It allows to construct a CSV file with the experimental results on
	 * several datasets. An input directory with the experimental results
	 * organized for each dataset is needed. It also can create the
	 * corresponding files with latex tables.
	 *
	 * @param inputDirectory
	 *            The input directory which contain the experimental results for
	 *            each dataset
	 * @param measure
	 *            The measure to analyze
	 * @param outputDirectory
	 *            The output directory (optional)
	 * @param min
	 *            Indicate if the evaluation measure is minimal or maximal
	 * @return The file created
	 * @throws IOException
	 *             An IO exception is launched if an error occur.
	 */
	public static File csvAULCFileReports(String inputDirectory, final String measure, String outputDirectory,
			String min) throws IOException {

		File source = new File(inputDirectory);

		File output = FileUtil.createProgressiveFile(source, outputDirectory);

		output.mkdirs();

		// Structure to store the results
		HashMap<String, HashMap<String, Double>> structure = new HashMap<String, HashMap<String, Double>>();

		File[] dirs = FileUtil.listDirs(source);

		if (dirs == null) {
			System.out.println("ALERT: Must create directories with the names "
					+ "of the datasets and into them, for each strategy, the experimental results must be copied.");
		}

		ArrayList<String> algNames = new ArrayList<String>();

		ArrayList<String> dataNames = new ArrayList<String>();

		// The structure is filled
		for (File dir : dirs) {

			if (!structure.containsKey(dir.getName())) {
				structure.put(dir.getName(), new HashMap<String, Double>());
			}

			// The list of the reports
			List<File> reports = FileUtil.listReports(dir);

			if (algNames.isEmpty()) {

				for (File fileReport : reports) {
					algNames.add(FileUtil.nameReport(fileReport));
				}
			}

			dataNames.add(dir.getName());

			for (File fileReport : reports) {

				String algorithmName = FileUtil.nameReport(fileReport);

				structure.get(dir.getName()).put(algorithmName, getAreaUnderLearningCurve(fileReport, measure));
			}
		}

		Collections.sort(algNames);
		Collections.sort(dataNames);

		File csv = new File(output, "AULC-" + measure + ".csv");

		BufferedWriter writer = Files.newBufferedWriter(csv.toPath(), Charset.defaultCharset());

		StringBuilder build = new StringBuilder();

		build.append("Dataset");

		for (String name : algNames) {

			build.append(",").append(name);
		}

		build.append("\n");

		// Writing to the output file
		for (String datasetName : dataNames) {

			build.append(datasetName);

			for (String algName : algNames) {

				build.append(",").append(nf.format(structure.get(datasetName).get(algName)));

			}

			build.append("\n");

		}

		writer.write(build.toString());
		writer.flush();
		writer.close();
		writer = null;

		createLatexTable(csv, min);

		structure.clear();
		structure = null;

		return csv;
	}

	/**
	 * Execute the Page trend 's test. For each pair of strategy a file with the
	 * average rankings is created.
	 *
	 * @param inputDirectory
	 *            The directory which contains the experimental results. The
	 *            experimental results must be organized for each dataset.
	 * @param measure
	 *            The measure to analyze
	 * @param outputDirectory
	 *            The outputDirectory (optional)
	 * @param min
	 *            Flag that indicates if the measure is minimal or maximal
	 * @return The output directory
	 * @throws IOException
	 *             An IO exception is launched if an error occur.
	 * @throws Exception
	 *             An exception is launched if an error occur.
	 */
	public static File pageTrendTest(String inputDirectory, final String measure, String min, String outputDirectory)
			throws IOException, Exception {

		File source = new File(inputDirectory);

		File output = FileUtil.createProgressiveFile(source, outputDirectory);

		output.mkdirs();

		// Structure to fill
		HashMap<String, HashMap<String, List<AbstractEvaluation>>> structure = new HashMap<String, HashMap<String, List<AbstractEvaluation>>>();

		ArrayList<String> algNames = new ArrayList<String>();

		ArrayList<String> dataNames = new ArrayList<String>();

		File[] dirs = FileUtil.listDirs(source);

		if (dirs == null) {
			System.out.println("ALERT: Must create directories with the names "
					+ "of the datasets and into them, for each strategy, the experimental results must be copied.");
		}

		for (File dir : dirs) {

			dataNames.add(dir.getName());

			List<File> reports = FileUtil.listReports(dir);

			if (algNames.isEmpty()) {

				for (File fileReport : reports) {
					algNames.add(FileUtil.nameReport(fileReport));
				}
			}

			for (File fileReport : reports) {

				String algorithmName = FileUtil.nameReport(fileReport);

				if (!structure.containsKey(algorithmName)) {
					structure.put(algorithmName, new HashMap<String, List<AbstractEvaluation>>());
				}

				LoadDataFromReporterFile fileInput = new LoadDataFromReporterFile(fileReport);

				structure.get(algorithmName).put(dir.getName(), fileInput.getEvaluations());
			}
		}

		Collections.sort(algNames);
		Collections.sort(dataNames);

		File readme = new File(output, output.getName() + "_readme.txt");
		FileUtil.createFile(readme, true);

		BufferedWriter writerR = Files.newBufferedWriter(readme.toPath(), Charset.defaultCharset());
		writerR.append("Rows");
		writerR.newLine();

		boolean first = true;

		// For each algorithm a csv file is created
		for (String algorithmName : algNames) {

			File inform = new File(output, algorithmName);

			BufferedWriter writer = Files.newBufferedWriter(inform.toPath(), Charset.defaultCharset());

			HashMap<String, List<AbstractEvaluation>> datasets = structure.get(algorithmName);

			// For each dataset
			for (String dataset : dataNames) {

				if (first) {
					writerR.append(dataset);
					writerR.newLine();
				}

				StringBuilder build = new StringBuilder();

				for (AbstractEvaluation column : datasets.get(dataset)) {

					build.append(",").append(nf.format(column.getMetricValue(measure)));

				}

				writer.append(build.toString().substring(1));
				writer.newLine();
			}

			first = false;

			writer.flush();
			writer.close();
		}

		writerR.flush();
		writerR.close();

		// Executing the Page Trend test
		String[] args = new String[algNames.size() + 5];

		// num of algorithms
		args[0] = String.valueOf(algNames.size());

		// num of datasets
		args[1] = String.valueOf(dataNames.size());

		// number of evaluations
		args[2] = String.valueOf(structure.get(algNames.get(0)).get(dataNames.get(0)).size());

		// maximal or minimal process
		args[3] = min;

		// The name of the measure
		args[4] = measure;

		int pos = 5;

		for (String alg : algNames) {
			args[pos++] = output.getAbsolutePath() + "/" + alg;
		}

		PageTrendTest.main(args);

		structure.clear();
		structure = null;

		return output;
	}

	/**
	 * It modifies a set of reports leaving only the iterations of the interest
	 * for the researcher
	 *
	 * @param inputDirectory
	 *            Directory with a set of reports
	 *
	 * @param outputDirectory
	 *            The output directory (optional)
	 * @param rangesSeveral
	 *            The ranges to analyze.
	 * @return The output directory
	 * @throws IOException
	 *             An IOException is launched if an error occur.
	 */
	public static File reportsRange(String inputDirectory, String outputDirectory, ArrayList<Range> rangesSeveral)
			throws IOException {
		File source = new File(inputDirectory);

		File exit = FileUtil.createProgressiveFile(source, outputDirectory);

		exit.mkdirs();

		List<File> listReports = new ArrayList<File>();

		FileUtil.getReportsInDirectory(source, listReports);

		for (File file : listReports) {

			BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.defaultCharset());

			File newOne = FileUtil.fileDiferentParent(source, exit, file);
			newOne.getParentFile().mkdirs();
			newOne.createNewFile();

			BufferedWriter writer = Files.newBufferedWriter(newOne.toPath(), Charset.defaultCharset());

			boolean go = true;
			String line;
			boolean ok;

			while ((line = reader.readLine()) != null) {
				if (line.contains("Iteration:")) {
					int iteration = getFileIteration(line);
					ok = false;
					for (Range range : rangesSeveral) {
						if (range.test(iteration)) {
							ok = true;
							break;
						}
					}

					go = ok;
				}

				if (go || line.contains("Time end:")) {
					writer.append(line);
					writer.newLine();
				}
			}

			// close
			reader.close();
			reader = null;
			writer.close();
			writer = null;
		}

		return exit;
	}

	public static int getFileIteration(String line) {
		return Integer.parseInt(line.split(":")[1].trim());
	}

	/**
	 * It returns the values for all iterations given a specific measure
	 * 
	 * @param tcurve
	 *            The list of evaluations
	 * @param measureName
	 *            The name of the measure
	 * 
	 * @return Returns the values for all iterations given a specific measure
	 */
	public static double[] getValues(List<AbstractEvaluation> tcurve, String measureName) {

		double[] values = new double[tcurve.size()];

		for (int i = 0; i < tcurve.size(); i++) {
			values[i] = tcurve.get(i).getMetricValue(measureName);
		}

		return values;
	}

	/**
	 * Calculate the area under the learning curve (ALC).
	 *
	 * @param tcurve
	 *            a list of evaluations
	 * @param measureName
	 *            The measure to use.
	 * @return the area under learning curve
	 */
	public static double getAreaUnderLearningCurve(List<AbstractEvaluation> tcurve, String measureName) {

		final int n = tcurve.size();

		if (n == 0) {
			return Double.NaN;
		}

		// The x-axis represents the number of labeled instances
		final int[] xVals = new int[tcurve.size()];

		// The y-axis represents the values of the metric
		final double[] yVals = new double[tcurve.size()];

		// fill the xvals and yvals
		for (int i = 0; i < xVals.length; i++) {

			AbstractEvaluation eval = tcurve.get(i);

			xVals[i] = eval.getLabeledSetSize();
			yVals[i] = eval.getMetricValue(measureName);

		}

		double area = 0;
		double xlast = xVals[n - 1];

		double total = 0;

		for (int i = n - 2; i >= 0; i--) {
			double xDelta = Math.abs(xVals[i] - xlast);
			total += xDelta;
			area += (yVals[i] * xDelta);

			xlast = xVals[i];
		}

		if (area == 0) {
			return Utils.missingValue();
		}

		return area / total;
	}

	/**
	 * Calculate the area under the learning curve (ALC).
	 *
	 * @param reportFileCurve
	 *            The active learning report
	 * @param measureName
	 *            The measure to use.
	 * @return The area under learning curve
	 */
	public static double getAreaUnderLearningCurve(File reportFileCurve, String measureName) {
		LoadDataFromReporterFile fileInput = new LoadDataFromReporterFile(reportFileCurve);

		return getAreaUnderLearningCurve(fileInput.getEvaluations(), measureName);
	}

	/**
	 * Internal class to create a range of iterations
	 */
	public static class Range {

		private int[] values;

		public Range(String x) {
			values = new int[2];
			String[] div = x.split("-");
			for (int i = 0; i < 2; i++) {
				if (div[i].equals("first")) {
					values[i] = Integer.MIN_VALUE;
				} else if (div[i].equals("last")) {
					values[i] = Integer.MAX_VALUE;
				} else {
					values[i] = Integer.parseInt(div[i]);
				}
			}
		}

		public Range(int a, int b) {
			values[0] = a;
			values[1] = b;
		}

		public boolean test(double x) {
			return x >= values[0] && x <= values[1];
		}

		public double getA() {
			return values[0];
		}

		public void setA(int a) {
			this.values[0] = a;
		}

		public double getB() {
			return values[1];
		}

		public void setB(int b) {
			this.values[1] = b;
		}

	}

	/**
	 * Set the number format
	 */
	public static void setNumberFormat() {

		nf = (DecimalFormat) DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
		nf.setGroupingUsed(false);

		DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf.setDecimalFormatSymbols(dfs);
	}
	
	/**
	 * Create the table in latex format
	 * @param csv The CSV file
	 * @param min True if the evaluation measure is minimal, false otherwise
	 */
	public static void createLatexTable(File csv, String min) {

		try {

			Scanner fileInput = new Scanner(csv);
			StringBuilder tex = new StringBuilder();

			String headers[] = fileInput.nextLine().split(",");

			StringBuilder hearderColumn = new StringBuilder();
			StringBuilder hearderTableNames = new StringBuilder();

			for (int i = 0; i < headers.length; i++) {
				hearderColumn.append("c|");

				if (i != 0) {
					hearderTableNames.append("\\textbf{").append(headers[i]).append("} & ");
				}
			}

			hearderColumn.deleteCharAt(hearderColumn.length() - 1);

			hearderTableNames.delete(hearderTableNames.length() - 2, hearderTableNames.length() - 1);

			// Header
			tex.append("\\documentclass[a4paper,10pt]{article}\n");
			tex.append("\\usepackage{multirow}\n");
			tex.append("\\title{Area under learning curves}\n");
			tex.append("\\date{\\today}\n");
			tex.append("\\begin{document}\n");
			tex.append("\\maketitle\n");

			tex.append("\\begin{table}\n");
			tex.append("\\footnotesize\n");
			tex.append("\\centering\n");
			tex.append("\\begin{tabular}{").append(hearderColumn).append("}\n");

			tex.append("\\hline\n");
			tex.append("\\multirow{2}{*}{\\textbf{Dataset}} & \\multicolumn{" + (headers.length - 1)
					+ "}{c}{\\textbf{Multi-label AL strategy}}\\tabularnewline\n");
			tex.append("\\cline{2-" + headers.length + "} & \n");

			tex.append(hearderTableNames).append("\\tabularnewline\n");

			tex.append("\\hline\n");

			while (fileInput.hasNextLine()) {

				String arr[] = fileInput.nextLine().split(",");

				// the name of dataset
				tex.append(arr[0]).append(" & ");

				// Determine the min or max value
				double better = minmax(arr, min.equals("true"));

				for (int i = 1; i < arr.length - 1; i++) {

					if (better == Double.parseDouble(arr[i])) {
						tex.append("\\textbf{").append(arr[i]).append("} & ");
					} else {
						tex.append(arr[i]).append(" & ");
					}

				}

				if (better == Double.parseDouble(arr[arr.length - 1])) {
					tex.append("\\textbf{").append(arr[arr.length - 1]).append("}  ");
				} else {
					tex.append(arr[arr.length - 1]);
				}

				tex.append(" \\tabularnewline\n");

			}

			tex.append("\\hline\n");
			tex.append("\\end{tabular}\n");
			tex.append("\\caption{ALC results for the selected measure}\n");
			tex.append("\\label{}\n");
			tex.append("\\end{table}\n");

			// Footer
			tex.append("\\end{document}\n");

			fileInput.close();

			String nameLatexFile = csv.getPath().substring(0, csv.getPath().length() - 3) + "tex";

			File fileLatex = new File(nameLatexFile);

			BufferedWriter fileOutput = Files.newBufferedWriter(fileLatex.toPath(), Charset.defaultCharset());

			fileOutput.write(tex.toString());
			fileOutput.flush();
			fileOutput.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static double minmax(String args[], boolean minimum) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int i = 1; i < args.length; i++) {

			double temp = Double.parseDouble(args[i]);

			if (min > temp) {
				min = temp;
			}
			if (max < temp) {
				max = temp;
			}
		}

		return (minimum) ? min : max;
	}

	/**
	 * Create a single CSV file which contains pairs (x=number of labeled
	 * instances,y=measure value) for each report file contained in the source
	 * directory. These CSV files can after be used for plotting with GNUPlot.
	 *
	 * @param inputDirectory
	 *            The directory which contains the reports
	 * @param measureName
	 *            The measure to use.
	 * @param outputDirectory
	 *            The output directory. By default, it is created a directory
	 *            with the same name as the input directory and a consecutive
	 *            number.
	 * 
	 * @return The CSV file
	 */
	public static File createSingleCSVForGraphic(String inputDirectory, String measureName, String outputDirectory) {

		try {

			File source = new File(inputDirectory);

			File output = FileUtil.createProgressiveFile(source, outputDirectory);

			output.mkdirs();

			File[] dirs = FileUtil.listDirs(source);

			if (dirs == null) {
				System.out.println("ALERT: The input directory is empty!!!");
			}

			// For each directory
			for (File dir : dirs) {

				File out = new File(output, dir.getName());

				out.mkdir();

				// The list of the reports
				List<File> reports = FileUtil.listReports(dir);

				for (File fileReport : reports) {

					String algorithmName = FileUtil.nameReport(fileReport);

					LoadDataFromReporterFile fileInput = new LoadDataFromReporterFile(fileReport);

					StringBuilder str = new StringBuilder();

					str.append(
							"# The values reached in each iteration. The first column is the number of labeled instances and the second column represents the feature value\n");

					List<AbstractEvaluation> evaluation = fileInput.getEvaluations();

					for (AbstractEvaluation eval : evaluation) {

						str.append(eval.getLabeledSetSize()).append(" ")
								.append(nf.format(eval.getMetricValue(measureName))).append("\n");
					}

					File csv = new File(out, algorithmName + "-" + measureName + ".csv");

					BufferedWriter writer;

					writer = Files.newBufferedWriter(csv.toPath(), Charset.defaultCharset());

					writer.write(str.toString());
					writer.close();

				}
			}

			return output;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Calculates the area under the learning curve (ALC).
	 *
	 * @param ds
	 *            an array of values
	 * @param xDelta
	 *            The step
	 * 
	 * @return The area under learning curve
	 */
	public static double getAreaUnderLearningCurve(double[] ds, double xDelta) {

		final int n = ds.length;

		if (n == 0) {
			return Double.NaN;
		}

		double area = 0;

		double total = 0;

		for (int i = n - 2; i >= 0; i--) {
			total += xDelta;
			area += (ds[i] * xDelta);
		}

		if (area == 0) {
			return Utils.missingValue();
		}

		return area / total;
	}
}
