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

import java.util.HashMap;
import java.util.Random;


public class Configuration {

	private static String path;

	private static boolean iman;
	private static boolean bonferroni;
	private static boolean holm;
	private static boolean hochberg;
	private static boolean hommel;
	private static boolean holland;
	private static boolean rom;
	private static boolean finner;
	private static boolean li;

	private static boolean nemenyi;
	private static boolean shaffer;
	private static boolean bergman;

	private static int nDatasets;
	private static int nAlgorithms;
	private static int k;
	
	public static HashMap<String, Integer> map;

	private static int objective;

	// [i,j][k][e] represents the datasets selected for the k bias and i,j pair
	// of
	// algorithms
	public static int[][][][] datasetsSelected;

	// It stores the performance of each strategy on each dataset. It is
	// filled according certain evaluation measure
	
	public static double[][][] allResults;
	
	public static Random rand;

	/**
	 * Sets the objective of the test (maximization or minimization)
	 *
	 * @param objective
	 *            Objective of the test
	 */
	public static void setObjective(int objective) {
		Configuration.objective = objective;
	}

	/**
	 * Gets the objective of the test
	 *
	 * @return Objective of the test
	 */
	public static int getObjective() {
		return objective;
	}

	/**
	 * Sets the use of Bonferroni-Dunn test
	 *
	 * @param bonferroni
	 *            True if the test is used. False, if not.
	 */
	public static void setBonferroni(boolean bonferroni) {
		Configuration.bonferroni = bonferroni;
	}

	/**
	 * Sets the use of Finner test
	 *
	 * @param finner
	 *            True if the test is used. False, if not.
	 */
	public static void setFinner(boolean finner) {
		Configuration.finner = finner;
	}

	/**
	 * Sets the use of Hochberg test
	 *
	 * @param hochberg
	 *            True if the test is used. False, if not.
	 */
	public static void setHochberg(boolean hochberg) {
		Configuration.hochberg = hochberg;
	}

	/**
	 * Sets the use of Holland test
	 *
	 * @param holland
	 *            True if the test is used. False, if not.
	 */
	public static void setHolland(boolean holland) {
		Configuration.holland = holland;
	}

	/**
	 * Sets the use of Holm test
	 *
	 * @param holm
	 *            True if the test is used. False, if not.
	 */
	public static void setHolm(boolean holm) {
		Configuration.holm = holm;
	}

	/**
	 * Sets the use of Hommel test
	 *
	 * @param hommel
	 *            True if the test is used. False, if not.
	 */
	public static void setHommel(boolean hommel) {
		Configuration.hommel = hommel;
	}

	/**
	 * Sets the use of Iman-Davenport test
	 *
	 * @param iman
	 *            True if the test is used. False, if not.
	 */
	public static void setIman(boolean iman) {
		Configuration.iman = iman;
	}

	/**
	 * Sets the use of Li test
	 *
	 * @param li
	 *            True if the test is used. False, if not.
	 */
	public static void setLi(boolean li) {
		Configuration.li = li;
	}

	/**
	 * Sets the number of algorithms of the test
	 *
	 * @param nAlgorithms
	 *            Number of algorithms of the test
	 */
	public static void setNAlgorithms(int nAlgorithms) {
		Configuration.nAlgorithms = nAlgorithms;
	}

	/**
	 * Sets the number of data sets of the test
	 *
	 * @param nDatasets
	 *            Number of data sets of the test
	 */
	public static void setNDatasets(int nDatasets) {
		Configuration.nDatasets = nDatasets;
	}

	/**
	 * Sets the path of the file to store the results of the test
	 *
	 * @param path
	 *            Path of the file to store the results of the test
	 */
	public static void setPath(String path) {
		Configuration.path = path;
	}

	/**
	 * Sets the use of Rom test
	 *
	 * @param rom
	 *            True if the test is used. False, if not.
	 */
	public static void setRom(boolean rom) {
		Configuration.rom = rom;
	}

	/**
	 * Tests if Bonferroni test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isBonferroni() {
		return bonferroni;
	}

	/**
	 * Tests if Finner test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isFinner() {
		return finner;
	}

	/**
	 * Tests if Hochberg test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isHochberg() {
		return hochberg;
	}

	/**
	 * Tests if Holland test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isHolland() {
		return holland;
	}

	/**
	 * Tests if Holm test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isHolm() {
		return holm;
	}

	/**
	 * Tests if Hommel test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isHommel() {
		return hommel;
	}

	/**
	 * Tests if Iman-Davenport test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isIman() {
		return iman;
	}

	/**
	 * Tests if Li test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isLi() {
		return li;
	}

	/**
	 * Gets the number of algorithms of the test
	 *
	 * @return Number of algorithms of the test
	 */
	public static int getNAlgorithms() {
		return nAlgorithms;
	}

	/**
	 * Gets the number of data sets of the test
	 *
	 * @return Number of data sets of the test
	 */
	public static int getNDatasets() {
		return nDatasets;
	}

	/**
	 * Gets the path of the file to store the results of the test
	 *
	 * @return Path of the file to store the results of the test
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * Tests if Rom test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isRom() {
		return rom;
	}

	/**
	 * Sets the use of Bergman test
	 *
	 * @param bergman
	 *            True if the test is used. False, if not.
	 */
	public static void setBergman(boolean bergman) {
		Configuration.bergman = bergman;
	}

	/**
	 * Sets the use of Nemenyi test
	 *
	 * @param nemenyi
	 *            True if the test is used. False, if not.
	 */
	public static void setNemenyi(boolean nemenyi) {
		Configuration.nemenyi = nemenyi;
	}

	/**
	 * Sets the use of Shaffer test
	 *
	 * @param shaffer
	 *            True if the test is used. False, if not.
	 */
	public static void setShaffer(boolean shaffer) {
		Configuration.shaffer = shaffer;
	}

	/**
	 * Tests if Bergman test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isBergman() {
		return bergman;
	}

	/**
	 * Tests if Nemenyi test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isNemenyi() {
		return nemenyi;
	}

	/**
	 * Tests if Shaffer test is used
	 *
	 * @return True if the test is used. False, if not.
	 */
	public static boolean isShaffer() {
		return shaffer;
	}

	public static int getK() {
		return k;
	}

	public static void setK(int k) {
		Configuration.k = k;
	}

}
