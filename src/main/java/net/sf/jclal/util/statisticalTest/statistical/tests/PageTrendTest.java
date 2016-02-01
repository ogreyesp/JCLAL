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

package net.sf.jclal.util.statisticalTest.statistical.tests;

import java.util.*;

import net.sf.jclal.util.file.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.*;

/**
 * Class for running the Page trend's test. This class was adapted from the
 * available code for the paper:
 * 
 * Derrac, J., García, S., Hui, S., Suganthan, P. N., and Herrera, F. (2014).
 * Analyzing convergence performance of evolutionary algorithms: A statistical
 * approach. Information Sciences, 289, 41–58. doi:10.1016/j.ins.2014.06.009
 * 
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 * 
 */

public class PageTrendTest {

	public static final int UNDEFINED = 999;

	public static double results[][][];
	public static String names[];
	public static int nAlgs;
	public static int nDatasets;
	public static int nCuts;
	public static double ranks[][][][];
	public static double sumRanks[][][];
	public static double L[][];
	public static double pValue[][];
	public static double differences[][];
	public static int min = 1;
	public static String measure = null;

	public static DecimalFormat nf1, nf4, nf6;
	public static DecimalFormatSymbols dfs;
	public static String dir;

	/**
	 * Main method
	 * 
	 * @param args
	 *            The main arguments
	 * 
	 */
	public static void main(String[] args) {

		StringBuilder text = new StringBuilder("");

		setDecimalFormat();

		// Read arguments

		if (args.length < 6) {
			System.out.println("Error: Wrong parameters");
			System.out.println(
					"Usage: Page <nAlgorithms> <nDatasets> <nCutpoints> <min> [<Alg1ResultsFile> <Alg2ResultsFile>]...");

			System.out.println(
					"Set true for the min argument to specify that a minimal evaluation metric is evaluated, false otherwise");

			System.exit(0);
		}

		nAlgs = Integer.parseInt(args[0]);
		nDatasets = Integer.parseInt(args[1]);
		nCuts = Integer.parseInt(args[2]);
		min = (Boolean.parseBoolean(args[3])) ? 1 : -1;
		measure = args[4];
		dir = args[5].substring(0, args[5].lastIndexOf("/"));

		// Read average results of each algorithm

		results = new double[nAlgs][nDatasets][nCuts];
		names = new String[nAlgs];

		for (int i = 0; i < nAlgs; i++) {
			names[i] = args[i + 5];
			text = readFile(names[i]);
			copyResults(i, text.toString());
			names[i] = names[i].substring(names[i].lastIndexOf("/") + 1, names[i].length()).replaceAll("_", "-");
		}

		ranks = new double[nAlgs][nAlgs][nDatasets][nCuts];
		sumRanks = new double[nAlgs][nAlgs][nCuts];
		L = new double[nAlgs][nAlgs];
		pValue = new double[nAlgs][nAlgs];

		// Perform the test for each pair of algorithms
		for (int i = 0; i < nAlgs; i++) {
			for (int j = 0; j < nAlgs; j++) {

				if (i != j) {

					computeDifferences(i, j);

					// original version

					computeRanks(i, j);

					printRanking(i, j);

					computeLStatistic(i, j);

					computePValue(i, j);

				}

			}
		}

		// Print final results

		text = new StringBuilder("");

		text.append(printHeader());

		text.append(printResults());

		text.append(printFoot());

		try {

			File output = new File(dir + "/Page test-" + measure + ".tex");

			BufferedWriter writer = Files.newBufferedWriter(output.toPath(), Charset.defaultCharset());

			writer.write(text.toString());

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void printRanking(int algA, int algB) {
		try {

			File file = new File(dir + "/Ranking matrix: " + names[algA] + " vs " + names[algB] + ".txt");

			StringBuilder st = new StringBuilder();

			for (int i = 0; i < nDatasets; i++) {
				for (int j = 0; j < nCuts; j++) {
					st.append(ranks[algA][algB][i][j] + ",");
				}

				st.deleteCharAt(st.length() - 1).append("\n");
			}

			FileUtil.writeFile(file, st.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Print the header of the results file
	 * 
	 */
	private static String printHeader() {

		StringBuilder text = new StringBuilder("");

		text.append("\\documentclass[a4paper,10pt]{article}\n");
		text.append("\\title{Results of the Page test}\n");
		text.append("\\date{\\today}\n");
		text.append("\\begin{document}\n");
		text.append("\\oddsidemargin 0in \\topmargin 0in");
		text.append("\\maketitle\n");

		text.append("\\begin{itemize}\n");

		text.append("\\item Ranks are computed from 1 (larger differences) to N (smaller differences).\n");

		text.append(
				"\\item Ties are handled using the midrank method (i.e. a tie in ranks 2 and 3 will be represented as 2.5, 2.5).\n");

		text.append("\\item The L statistic is computed as\n");

		text.append("\\begin{equation}\n");
		text.append(" L = \\sum_{j=1}^{n}Y_{j}R_{j}\n");
		text.append("\\end{equation}\n");

		text.append(
				"\\noindent where $Y_{j}$ is the hypothetical ranking of the \\textit{jth} treatment (cutpoint, in this case) and $R_{j}$ is the average ranking computed for this treatment.\n");

		text.append("\\item The asymptotic $p$-values are computed using the statistic\n");

		text.append("\\begin{equation}\n");
		text.append("Z = \\frac{12(L-0.5)-3kn(n+1)^{2}}{n(n+1)\\sqrt{k(n-1)}}\n");
		text.append("\\end{equation}\n");

		text.append(
				"\\noindent which includes a continuity correction. This statistic is approximately standard normal and the appropriate rejection region is right tail (Gibbons,2010).\n");
		text.append("\\item Tables containing exact $p$-values for small samples can be found in (Page,1963).\n");

		text.append("\\end{itemize}\n");

		text.append("\\section*{References}\n");

		text.append(
				"* J.D. Gibbons, S. Chakraborti (2010), Nonparametric Statistical Inference, 5th edition, Chapman \\& Hall.\n");

		text.append("\n\n\\vspace{4 mm}\n\n");

		text.append(
				"\\noindent * E.P. Page (1963), Ordered hypotheses for multiple treatments: A significance test for linear ranks, Journal of the American Statistical Association, 58, 216--230.\n");

		text.append("\\clearpage\n");

		return text.toString();
	}

	/**
	 * Print the foot of the results file
	 * 
	 */
	private static String printFoot() {

		String text = "";

		text += "\\end{document}\n";

		return text;
	}

	/**
	 * Print the results of the basic version
	 * 
	 */
	private static String printResults() {

		StringBuilder text = new StringBuilder("");

		text.append("\\section*{Results obtained}\n");
		text.append("\\begin{table}[h]\n");
		text.append("\\begin{center}\n");
		text.append("\\begin{tabular}{|l|c|r|}\n");
		text.append("\\hline\n");
		text.append("Algorithms & L Statistic & $p$-value \\\\ \n");
		text.append("\\hline\n");

		for (int i = 0; i < nAlgs; i++) {
			for (int j = 0; j < nAlgs; j++) {

				if (i != j) {
					text.append(names[i] + " VS " + names[j] + " & " + nf4.format(L[i][j]) + " & "
							+ nf6.format(pValue[i][j]) + " \\\\ \n");
				}

			}
			text.append("\\hline\n");
		}

		text.append("\\end{tabular}\n");
		text.append("\\end{center}\n");
		text.append("\\end{table}\n");

		text.append("\\clearpage\n");

		text.append("\\section*{Sum of ranks}\n");

		text.append("\\begin{table}[h]\n");
		text.append("\\begin{center}\n");
		text.append("\\begin{tabular}{|l|");

		for (int k = 0; k < nCuts; k++) {
			text.append("c|");
		}

		text.append("}\n");
		text.append("\\hline\n");
		text.append("Algorithms & ");

		for (int k = 0; k < nCuts - 1; k++) {
			text.append("C" + (k + 1) + " & ");
		}

		text.append("C" + (nCuts) + " \\\\ \n");
		text.append("\\hline\n");

		for (int i = 0; i < nAlgs; i++) {

			for (int j = 0; j < nAlgs; j++) {

				if (i != j) {
					text.append(names[i] + " VS " + names[j] + " & ");

					for (int k = 0; k < nCuts - 1; k++) {
						text.append(nf1.format(sumRanks[i][j][k]) + " & ");
					}

					text.append(nf1.format(sumRanks[i][j][nCuts - 1]) + " \\\\");
				}

			}

			text.append("\\hline\n");
		}

		text.append("\\end{tabular}\n");
		text.append("\\end{center}\n");
		text.append("\\end{table}\n");

		text.append("\\clearpage\n");

		return text.toString();
	}

	/**
	 * Format of decimal values
	 * 
	 */
	private static void setDecimalFormat() {

		nf1 = (DecimalFormat) DecimalFormat.getInstance();
		nf1.setMaximumFractionDigits(4);
		nf1.setMinimumFractionDigits(0);
		nf1.setGroupingUsed(false);

		dfs = nf1.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf1.setDecimalFormatSymbols(dfs);

		nf4 = (DecimalFormat) DecimalFormat.getInstance();
		nf4.setMaximumFractionDigits(4);
		nf4.setMinimumFractionDigits(0);
		nf4.setGroupingUsed(false);

		dfs = nf4.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf4.setDecimalFormatSymbols(dfs);

		nf6 = (DecimalFormat) DecimalFormat.getInstance();
		nf6.setMaximumFractionDigits(6);
		nf6.setMinimumFractionDigits(6);
		nf6.setGroupingUsed(false);

		dfs = nf6.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf6.setDecimalFormatSymbols(dfs);

	}

	/**
	 * Computation of L statistic
	 * 
	 */
	private static void computeLStatistic(int algA, int algB) {

		L[algA][algB] = 0.0;

		/**
		 * It is expected that, differences in the fitness values among two
		 * strategies will increase as active learning process continues.
		 */

		for (int j = nCuts - 1; j >= 0; j--) {
			L[algA][algB] += (sumRanks[algA][algB][j] * (nCuts - j));
		}
	}

	/**
	 * Computation of asymptotic p-value
	 * 
	 */
	private static void computePValue(int algA, int algB) {

		pValue[algA][algB] = computeAsymptoticProbability(nCuts, nDatasets, L[algA][algB]);

	}

	/**
	 * Compute ranks for the Page test (1..n, k rows). Ties are handled by the
	 * midrank method
	 */
	private static void computeRanks(int algA, int algB) {

		double rank, min;
		double newRank;
		int count;

		// For each function
		for (int i = 0; i < differences.length; i++) {

			Arrays.fill(ranks[algA][algB][i], -1.0);

			rank = 1.0;

			do {

				min = Double.MAX_VALUE;
				count = 0;

				// For each cut point
				// The minimum value and the number for which this value appears
				// in the nCuts cut-points are computed
				for (int j = 0; j < differences[i].length; j++) {
					if ((ranks[algA][algB][i][j] == -1.0) && (differences[i][j] == min)) {
						count += 1;
					}
					if ((ranks[algA][algB][i][j] == -1.0) && (differences[i][j] < min)) {
						min = differences[i][j];
						count = 1;
					}
				}

				// The rank value is computed, in the case of ties the rank is
				// computed in a different manner
				if (count == 1) {
					newRank = rank;
				} else {
					newRank = 0.0;
					for (int k = 0; k < count; k++) {
						newRank += (rank + k);
					}

					newRank /= (double) count;
				}

				// The rank value is assigned to those values equal to the
				// minimum.
				for (int j = 0; j < differences[i].length; j++) {

					if (differences[i][j] == min) {
						ranks[algA][algB][i][j] = newRank;
					}
				}

				rank += count;

			} while (rank <= differences[0].length);

		}

		Arrays.fill(sumRanks[algA][algB], 0);

		// The sum of rank values in each cut point across every dataset is
		// computed

		for (int i = 0; i < nDatasets; i++) {
			for (int j = 0; j < nCuts; j++) {
				sumRanks[algA][algB][j] += ranks[algA][algB][i][j];
			}

		}
	}

	/**
	 * Compute differences between two algorithms' average results
	 *
	 */
	private static void computeDifferences(int algA, int algB) {

		differences = new double[nDatasets][nCuts];

		// The cases maximum and minimum are taken into account
		for (int i = 0; i < nDatasets; i++) {
			for (int j = 0; j < nCuts; j++) {
				differences[i][j] = min * (results[algA][i][j] - results[algB][i][j]);
			}
		}
	}

	/**
	 * Fill results matrixes
	 *
	 */
	private static void copyResults(int alg, String text) {

		StringTokenizer lines, tokens;
		String line, token;

		lines = new StringTokenizer(text, "\n\r");

		for (int i = 0; i < nDatasets; i++) {
			line = lines.nextToken();
			tokens = new StringTokenizer(line, ",\t");

			for (int j = 0; j < nCuts; j++) {
				token = tokens.nextToken();
				results[alg][i][j] = Double.parseDouble(token);
			}

		}
	}

	/**
	 * Read a file in text mode
	 *
	 */
	private static StringBuilder readFile(String name) {

		StringBuilder text = new StringBuilder();

		try {
			BufferedReader fileReader = Files.newBufferedReader(new File(name).toPath(), Charset.defaultCharset());

			String line;

			do {
				line = fileReader.readLine();
				text.append(line).append("\n");
			} while (line != null);

			fileReader.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return text;
	}

	/**
	 * Computes asymptotic p-value of the Page distribution.
	 * 
	 * @param n
	 *            number of columns
	 * @param k
	 *            number of rows
	 * @param L
	 *            Page statistic
	 * @return p-value computed
	 */
	public static double computeAsymptoticProbability(int n, int k, double L) {

		double Z, numerator, denominator;

		numerator = 12.0 * (L - 0.5) - (3.0 * k * n * (n + 1.0) * (n + 1.0));
		denominator = n * (n + 1) * Math.sqrt(k * (n - 1.0));

		Z = numerator / denominator;

		return getTipifiedProbability(Z, true);

	}

	/**
	 * Computes cumulative N(0,1) distribution.
	 * 
	 * Based on Algorithm AS66 Applied Statistics (1973) vol 22 no.3
	 * 
	 * @param z
	 *            x value
	 * @param upper
	 *            A boolean value, if true the integral is evaluated from z to
	 *            infinity, from minus infinity to z otherwise
	 * @return The value of the cumulative N(0,1) distribution for z
	 */
	public static double getTipifiedProbability(double z, boolean upper) {
		// Algorithm AS 66: "The Normal Integral"
		// Applied Statistics
		double ltone = 7.0, utzero = 18.66, con = 1.28, a1 = 0.398942280444, a2 = 0.399903438504, a3 = 5.75885480458,
				a4 = 29.8213557808, a5 = 2.62433121679, a6 = 48.6959930692, a7 = 5.92885724438, b1 = 0.398942280385,
				b2 = 3.8052e-8, b3 = 1.00000615302, b4 = 3.98064794e-4, b5 = 1.986153813664, b6 = 0.151679116635,
				b7 = 5.29330324926, b8 = 4.8385912808, b9 = 15.1508972451, b10 = 0.742380924027, b11 = 30.789933034,
				b12 = 3.99019417011;

		double y, alnorm;

		if (z < 0) {
			upper = !upper;
			z = -z;
		}

		if (z <= ltone || upper && z <= utzero) {
			y = 0.5 * z * z;
			if (z > con) {
				alnorm = b1 * Math.exp(-y)
						/ (z - b2 + b3 / (z + b4 + b5 / (z - b6 + b7 / (z + b8 - b9 / (z + b10 + b11 / (z + b12))))));
			} else {
				alnorm = 0.5 - z * (a1 - a2 * y / (y + a3 - a4 / (y + a5 + a6 / (y + a7))));
			}
		} else {
			alnorm = 0;
		}
		if (!upper) {
			alnorm = 1 - alnorm;
		}

		return (alnorm);

	}

	/**
	 * It runs the Page trend's test for a pair of algorithms. It returns the
	 * p-values computed.
	 * 
	 * @param data
	 *            The results for the two strategies in each dataset
	 * @return Returns the p-values computed.
	 * 
	 */
	public static double doPage(double[][][] data) {

		setDecimalFormat();

		nAlgs = 2;
		nDatasets = data[0].length;
		nCuts = data[0][0].length;
		min = (net.sf.jclal.util.statisticalTest.statistical.Configuration.getObjective() == 2) ? 1 : -1;

		// Read average results of each algorithm

		results = data;

		ranks = new double[nAlgs][nAlgs][nDatasets][nCuts];
		sumRanks = new double[nAlgs][nAlgs][nCuts];
		L = new double[nAlgs][nAlgs];
		pValue = new double[nAlgs][nAlgs];

		computeDifferences(0, 1);

		// original version

		computeRanks(0, 1);

		computeLStatistic(0, 1);

		computePValue(0, 1);

		return pValue[0][1];

	}
}
