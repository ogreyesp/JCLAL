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
package net.sf.jclal.util.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jclal.util.file.FileUtil;
import net.sf.jclal.util.sort.Container;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.IndexSearcher;
import weka.core.Instances;

/**
 * It converts a Lucene index file to a weka file, the document indexes must
 * have fields called "class" and "content". WARNING: The fields must not
 * contain any punctuation sign.
 *
 * The Instances are SparseInstance.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Maria del Carmen Rodriguez Hernandez
 * @author Eduardo Perez Perdomo
 */
public class LuceneIndexToWekaDataSet {

	/**
	 * Field content
	 */
	private String content = "content";
	/**
	 * Field class
	 */
	private String classF = "class";
	private String doubleLine = "\n\n";

	/**
	 * It converts a index file of Lucene to a arff weka file for classification. The
	 * weka file class are nominal. The classifiers will work with nominal
	 * class.
	 *
	 * @param wekaFileName
	 *            Path of weka file.
	 * @param indexFile
	 *            Path of index file based on Lucene. The document indexes must
	 *            have fields called "class" and "content". WARNING: The fields
	 *            must not contains any puntuaction sign.
	 *
	 * @return Instances of weka. The instances are sparse since it is about
	 *         text information.
	 *
	 * @throws FileNotFoundException
	 *             If the file does not exists.
	 * @throws IOException
	 *             If happens a error while writing the file.
	 */
	public Instances convertLuceneToWekaClassification(String wekaFileName, String indexFile)
			throws FileNotFoundException, IOException {
		File nuevo = new File(wekaFileName);

		if (!verify(nuevo)) {
			return null;
		}

		FileUtil.writeFile(nuevo, "@RELATION " + nuevo.getName() + doubleLine);

		IndexSearcher searcher = new IndexSearcher(indexFile);

		IndexReader reader = searcher.getIndexReader();

		int total = reader.maxDoc();

		HashMap<String, Integer> terms = new HashMap<String, Integer>(total * 2);
		Set<String> labels = new HashSet<String>(total * 2);

		int i;
		for (int l = 0; l < total; l++) {
			if (!reader.isDeleted(l)) {
				TermFreqVector vector = reader.getTermFreqVector(l, content);

				Document doc = reader.document(l);

				String current = doc.getField(classF).stringValue();

				labels.add(current);

				if (vector != null) {
					String listosI[] = vector.getTerms();
					for (i = 0; i < listosI.length; i++) {
						if (!terms.containsKey(listosI[i])) {
							terms.put(listosI[i], terms.size());
						}

					}
				}
			}
		}

		String[] labelReady = new String[labels.size()];
		int posLabel = 0;
		for (String string : labels) {
			labelReady[posLabel] = string;
			posLabel++;
		}

		Container[] terminos = convertir(terms);
		Arrays.sort(terminos);

		for (int j = 0; j < terminos.length; j++) {
			FileUtil.writeFile(nuevo, "@ATTRIBUTE " + (int) terminos[j].getKey() + " NUMERIC" + "\n");
		}

		FileUtil.writeFile(nuevo, "@ATTRIBUTE class {");
		for (int j = 0; j < labelReady.length - 1; j++) {
			FileUtil.writeFile(nuevo, labelReady[j] + ",");
		}
		FileUtil.writeFile(nuevo, labelReady[labelReady.length - 1] + "}" + doubleLine);

		FileUtil.writeFile(nuevo, "@DATA\n");

		for (int pos = 0; pos < searcher.maxDoc(); pos++) {

			if (!reader.isDeleted(pos)) {

				TermFreqVector vector = reader.getTermFreqVector(pos, content);

				if (vector != null) {
					int[] origen = vector.getTermFrequencies();
					String[] termsI = vector.getTerms();

					int[] positions = new int[origen.length];

					for (int k = 0; k < origen.length; k++) {
						positions[k] = terms.get(termsI[k]);
					}

					Container[] escribir = convertir(positions, origen);
					Arrays.sort(escribir);

					FileUtil.writeFile(nuevo, "{");
					for (int j = 0; j < escribir.length; j++) {
						FileUtil.writeFile(nuevo, (int) escribir[j].getKey() + " " + escribir[j].getValue() + ",");
					}

					FileUtil.writeFile(nuevo,
							terms.size() + " " + searcher.doc(pos).getField(classF).stringValue() + "}\n");
				}

			}
		}

		// close files
		closeReaders(searcher, reader);

		// Test if the weka file works
		Instances test = testWekaFile(wekaFileName);

		return test;
	}

	/**
	 * It converts a index file of Lucene to a arff weka file for regression. The
	 * weka file class are real. The used classifiers will work with numeric
	 * real classe.
	 *
	 * @param wekaFileName
	 *            Path of weka file.
	 * @param indexFile
	 *            Path of index file based on Lucene. The document indexes must
	 *            have fields called "class" and "content". WARNING: The fields
	 *            must not contains any puntuaction sign.
	 *
	 * @return Instances of weka. The instances are sparse since it is about
	 *         text information.
	 *
	 * @throws FileNotFoundException
	 *             If the file does not exists.
	 * @throws IOException
	 *             If happens a error while writing the file.
	 */
	public Instances convertLuceneToWekaRegression(String wekaFileName, String indexFile)
			throws FileNotFoundException, IOException {
		File nuevo = new File(wekaFileName);

		if (!verify(nuevo)) {
			return null;
		}

		FileUtil.writeFile(nuevo, "@RELATION " + nuevo.getName() + doubleLine);

		IndexSearcher searcher = new IndexSearcher(indexFile);

		IndexReader reader = searcher.getIndexReader();

		int total = reader.maxDoc();

		HashMap<String, Integer> terms = new HashMap<String, Integer>(total * 2);
		HashMap<String, Integer> labels = new HashMap<String, Integer>(total * 2);

		int i;
		for (int l = 0; l < total; l++) {
			if (!reader.isDeleted(l)) {
				TermFreqVector vector = reader.getTermFreqVector(l, content);

				Document doc = reader.document(l);

				String current = doc.getField(classF).stringValue();

				if (!labels.containsKey(current)) {
					labels.put(current, labels.size());
				}

				if (vector != null) {
					String listosI[] = vector.getTerms();
					for (i = 0; i < listosI.length; i++) {
						if (!terms.containsKey(listosI[i])) {
							terms.put(listosI[i], terms.size());
						}

					}
				}
			}
		}

		Container[] terminos = convertir(terms);
		Arrays.sort(terminos);

		for (int j = 0; j < terminos.length; j++) {
			FileUtil.writeFile(nuevo, "@ATTRIBUTE " + (int) terminos[j].getKey() + " NUMERIC" + "\n");
		}

		FileUtil.writeFile(nuevo, "@ATTRIBUTE class REAL [0.0,");

		FileUtil.writeFile(nuevo, (labels.size() - 1) + ".0]" + doubleLine);

		FileUtil.writeFile(nuevo, "@DATA\n");

		for (int pos = 0; pos < searcher.maxDoc(); pos++) {

			if (!reader.isDeleted(pos)) {

				TermFreqVector vector = reader.getTermFreqVector(pos, content);

				if (vector != null) {
					int[] origen = vector.getTermFrequencies();
					String[] termsI = vector.getTerms();

					int[] positions = new int[origen.length];

					for (int k = 0; k < origen.length; k++) {
						positions[k] = terms.get(termsI[k]);
					}

					Container[] escribir = convertir(positions, origen);
					Arrays.sort(escribir);

					FileUtil.writeFile(nuevo, "{");
					for (int j = 0; j < escribir.length; j++) {
						FileUtil.writeFile(nuevo, (int) escribir[j].getKey() + " " + escribir[j].getValue() + ",");
					}

					FileUtil.writeFile(nuevo, terms.size() + " "
							+ labels.get(searcher.doc(pos).getField(classF).stringValue()) + ".0}\n");
				}

			}
		}

		// close files
		closeReaders(searcher, reader);

		// Test if the weka file works
		Instances test = testWekaFile(wekaFileName);

		return test;
	}

	private boolean verify(File fileNew) {
		if (fileNew.exists()) {
			return false;
		}

		try {
			if (!fileNew.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			Logger.getLogger(LuceneIndexToWekaDataSet.class.getName()).log(Level.SEVERE, null, e);
			return false;
		}
		return true;
	}

	private Container[] convertir(Map terms) {
		Container[] dev = new Container[terms.size()];
		Iterator<Entry<String, Integer>> iterator = terms.entrySet().iterator();
		int pos = 0;
		while (iterator.hasNext()) {
			Entry<String, Integer> dist = iterator.next();
			dev[pos] = new Container(dist.getValue(), dist.getKey());
			pos++;
		}
		return dev;
	}

	private Container[] convertir(int[] origen, int[] termsI) {
		Container[] dev = new Container[origen.length];
		for (int i = 0; i < dev.length; i++) {
			dev[i] = new Container(origen[i], termsI[i]);
		}
		return dev;
	}

	/**
	 * Close the file readers
	 *
	 * @param searcher
	 *            The searcher to use.
	 * @param reader
	 *            The reader to use.
	 * @throws IOException
	 *             The exception that can be launched.
	 */
	public static void closeReaders(IndexSearcher searcher, IndexReader reader) throws IOException {
		searcher.close();
		reader.close();
	}

	/**
	 * Test if a Weka dataset was created satisfactorily
	 *
	 * @param wekaFileName
	 *            The name of the Weka dataset.
	 * @return The instances.
	 */
	public static Instances testWekaFile(String wekaFileName) {
		try {
			Instances test = new Instances(new BufferedReader(new FileReader(wekaFileName)));
			test.setClassIndex(test.numAttributes() - 1);
			return test;
		} catch (IOException e) {
			Logger.getLogger(LuceneIndexToWekaDataSet.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClassF() {
		return classF;
	}

	public void setClassF(String classF) {
		this.classF = classF;
	}
}
