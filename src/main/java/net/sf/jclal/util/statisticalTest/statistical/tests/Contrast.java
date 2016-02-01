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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import net.sf.jclal.util.statisticalTest.statistical.Configuration;
import net.sf.jclal.util.statisticalTest.statistical.Files;

public class Contrast {
	
	/**
	* Builder
	*/
	public Contrast(){
		
	}//end-method
	
	/**
     * <p>
     * In this method, all possible post hoc statistical test between more than three algorithms results 
     * are executed, according to the configuration file
     * @param data Array with the results of the method
     * @param algorithms A vector of String with the names of the algorithms
     * @param print If the results are printed
     * </p>
     */
	public static void doContrast(double data[][], String algorithms[], boolean print) {
		
		String outputFileName = Configuration.getPath();

        StringBuilder outputString = new StringBuilder("");
	    outputString.append(header());
	        
	    outputString.append(computeBody(data, algorithms));

	    if(print)
	    	Files.writeFile(outputFileName, outputString.toString());

	}//end-method
	
	/**
	* <p>
	* This method composes the header of the LaTeX file where the results are saved
	* </p>
	* @return A string with the header of the LaTeX file
	*/   
	private static String header() {
	        StringBuilder output = new StringBuilder("");
	        output.append("\\documentclass[a4paper,10pt]{article}\n");
	        output.append("\\usepackage{graphicx}\n");
	        output.append("\\usepackage{lscape}\n");
	        output.append("\\title{Contrast estimation.}\n");
	        output.append("\\date{\\today}\n\\author{}\n\\begin{document}\n\\begin{landscape}\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\\section{Results.}\n\n");

	        output.append("Estimation of the contrast between medians of samples of results considering all pairwise comparisons:\n\n");
	        		
	        return output.toString();

	}//end-method
	 
	/**
     * <p>
     * In this method, the contrast estimation is computed
	 *
	 * @param results Array with the results of the methods
	 * @param algorithmName Array with the name of the methods employed
	 *
	 * @return A string with the contents of the test in LaTeX format
     */
	private static String computeBody(double[][] results, String algorithmName[]) {
		
		StringBuilder output=new StringBuilder("");
		double CE [][][];
		double medians [][];
		double estimators [];
		
		DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(0);

		DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf.setDecimalFormatSymbols(dfs);

		int numAlg= algorithmName.length;
		int nDatasets = results.length;
		
		/** CONTRAST ESTIMATION *******************************************************************************************/
	    CE = new double[numAlg][numAlg][nDatasets];
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=i+1; j<numAlg; j++) {
	    		for (int k=0; k<nDatasets; k++) {
	    			CE[i][j][k] = results[k][i] - results[k][j];
	    		}
	    	}
	    }

	    medians = new double[numAlg][numAlg];
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=i+1; j<numAlg; j++) {
	    		Arrays.sort(CE[i][j]);
	    		if (CE[i][j].length % 2 == 1) {
	    			medians[i][j] = CE[i][j][nDatasets/2];
	    		} else {
	    			medians[i][j] = (CE[i][j][nDatasets/2] + CE[i][j][(nDatasets/2)-1]) / 2.0;	    			
	    		}
	    	}
	    }
	    
	    estimators = new double[numAlg];
	    Arrays.fill(estimators, 0);
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=0; j<numAlg; j++) {
		    		estimators[i] += medians[i][j] - medians[j][i];
	    	}
	    	estimators[i] /= numAlg;
	    }
		
		
		
	    /** PRINT THE CONTRAST ESTIMATION*/
		
	    output.append("\\begin{table}[!htp]\n\\centering\\scriptsize\n" + "\\begin{tabular}{\n");
        for (int i=0; i<numAlg+1; i++) {
        	output.append("|r");
        }
        output.append("|}\n\\hline\n" + " \n");
        for (int i=0; i<numAlg; i++) {
        	output.append("&" + algorithmName[i]);
        }        
        output.append("\\\\\n\\hline\n");
        for (int i=0; i<numAlg; i++) {
        	output.append(algorithmName[i]);
        	for (int j=0; j<numAlg; j++) {
        		 output.append("& "+nf.format(estimators[i] - estimators[j]));
        	}
        	output.append("\\\\\n\\hline\n");
        }

        output.append("\n" + "\\end{tabular}\n" + "\\caption{Contrast Estimation}\n\\end{table}\n");

        output.append( "\n\\end{landscape}\n\\end{document}");
        return output.toString();
		
    }//end-method

}//end-class
