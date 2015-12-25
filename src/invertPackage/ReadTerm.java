package invertPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

public class ReadTerm {

	/**
	 * Read the content of cran.txt starting from I. and till it finds next I.
	 * write the document ID in the outFile.txt
	 */

	public void readCran(final File srcPath, String s) throws IOException {
		Scanner input = null;
		PrintWriter out = null;
		String word;
		String stemOut;
		Stemmer stemObj = new Stemmer();
		TreeSet<Integer> allDocIds = new TreeSet<Integer>();
		try {
			input = new Scanner(new FileReader(srcPath + "//input//cran.all.1400"));
			File stopWordFile = new File(srcPath + "//input//stopWords.txt");
			/********* CREATE ARRAYLIST OF STOPWORDS FROM STOP.TXT START ********/
			if (!stopWordFile.exists()) {
				throw new RuntimeException("File Not Found");
			}
			BufferedReader reader = null;
			StringBuilder stopWords = new StringBuilder();
			try {
				reader = new BufferedReader(new FileReader(stopWordFile));
				String line;
				while ((line = reader.readLine()) != null) {
					stopWords.append(line + " ");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			String[] arrStopWords = stopWords.toString().split("\\s+");
			HashMap<String, HashMap<Integer, Integer>> allTerms = new HashMap<String,  HashMap<Integer, Integer>>();
			HashMap<Integer, Integer> getDocTerm = new HashMap<Integer, Integer>();
			HashMap<Integer, Double> docWd = new HashMap<Integer, Double>();
			HashMap<String, Integer> singleDocTermCount = new HashMap<String, Integer>();
			HashMap<Integer, Double> similarity = null;
			QueryManipulation queryObj = new QueryManipulation();
			int docId = 0;
			int prevCountDoc = 0;
			double wd = 0;
			/**
			 *  CREATE ARRAYLIST OF STOPWORDS FROM STOP.TXT STOP *******
			 *  */
			while (input.hasNext()) {
				word = input.next();
				/**	
				 * Content of a document
				 */
				if(!word.equals(s)){
					if (!Arrays.asList(arrStopWords).contains(word)) {
						stemOut = stemObj.steamWord(word); // stem this word
						/**
						 * Outer Hash Start allTerms
						 */
						if(!allTerms.containsKey(stemOut)){
							allTerms.put(stemOut, new HashMap<Integer, Integer>());
							allTerms.get(stemOut).put(docId, 1); 
							/** hashmap that maintains the termcount for a document **/
							singleDocTermCount.put(stemOut, 1);
							/** end hashmap that maintains the termcount for a document **/
						}else{
							//get value of the current hash map
							if(allTerms.get(stemOut).containsKey(docId)){
								prevCountDoc = allTerms.get(stemOut).get(docId);
							}else{
								prevCountDoc = 0;
							}
							allTerms.get(stemOut).put(docId, prevCountDoc+1);
							/** hashmap that maintains the termcount for a document **/
							singleDocTermCount.put(stemOut, prevCountDoc+1);
							/** end hashmap that maintains the termcount for a document **/
						}
						/** allTerms hashMap **/
					}
				}else{
					/**
					 * New Document start with .I
					 */
					docId = input.nextInt();
					allDocIds.add(docId);
					prevCountDoc = 0;
					getDocTerm.clear();
					wd = Math.sqrt(queryObj.setWd(singleDocTermCount));
					docWd.put(docId-1, wd);
					//clears the singleDoc content for new doc
					singleDocTermCount.clear();
					
				}
			}
			//start this is used to store the value for last I.1400 as the above for loop when I.1 its wd = 0 and when I.2 wd = wd of 1 so when I.1400 wd = wd of I.1399 
			//this below statement helps to calculate wd of I.1400
			//in the above loop at docID(else) is executed then if is executed which maintains singleDocTermCount
			wd = Math.sqrt(queryObj.setWd(singleDocTermCount));
			docWd.put(docId, wd);
			similarity = queryObj.query(allTerms, allDocIds);
			queryObj.orderBy(docWd, similarity);
			//end 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			input.close();
		}
	}
	
	/**
	 * This method prints the terms in the file
	 * @param out - Output printwriter stream
	 * @param i - Count for the line
	 * @param termCount - Termcount map to keep track of the terms
	 */
	private void printTerms(PrintWriter out, int i,
			HashMap<String, Integer> termCount) {
		List<String> sortedKeys = new ArrayList<String>(termCount.keySet());
		if (i - 1 > 0) {
			out.println("Doc ID: " + (i - 1) + "\n"
					+ "Total Unique Term Count: " + Term.terms.size()); // Initially
																		// doc-id
																		// = 1
																		// term
																		// count
																		// 0;
		}
		Collections.sort(sortedKeys);
		for (String str : sortedKeys) {
			out.println("Term: " + str + " Count: "
					+ termCount.get(str).intValue());
		}
		Term.terms.clear();
		termCount.clear();
		sortedKeys.clear();
		out.flush();
	}

	/**
	 * returns true if its a stopword else false
	 * 
	 * passed
	 * 
	 * @param srcPath
	 * @param checkWord
	 * @return
	 */
	public boolean isStopWord(final File srcPath, String checkWord) {
		File stopWordFile = new File(srcPath + "//input//stopWords.txt");
		if (!stopWordFile.exists()) {
			throw new RuntimeException("File Not Found");
		}
		BufferedReader reader = null;
		StringBuilder stopWords = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(stopWordFile));
			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.append(line + " ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String notStopWord = "";
		String[] arrStopWords = stopWords.toString().split("\\s+");
		if (!Arrays.asList(arrStopWords).contains(checkWord)) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		Path currentRelativePath = Paths.get("");
		String getProjPath = currentRelativePath.toAbsolutePath().toString();
		final File srcPath = new File(getProjPath + "//src");
		ReadTerm readTermObj = new ReadTerm();
		readTermObj.readCran(srcPath, ".I");
		QueryManipulation queryObj = new QueryManipulation();
		Relevancy relObj = new Relevancy();
		relObj.setRelevancy();
	}
}
