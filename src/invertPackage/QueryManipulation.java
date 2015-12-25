package invertPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class QueryManipulation {

	public HashMap<Integer, Double> query(
			HashMap<String, HashMap<Integer, Integer>> allTerms,
			TreeSet<Integer> allDocIds) {
		Path currentRelativePath = Paths.get("");
		String getProjPath = currentRelativePath.toAbsolutePath().toString();
		final File srcPath = new File(getProjPath + "//src");
		Stemmer stemObj = new Stemmer();
		ReadTerm readObj = new ReadTerm();
		HashMap<Integer, Double> similarity = new HashMap<Integer, Double>();
		String stemWord;
		String word;
		String s = ".I";
		try {
			Scanner input = new Scanner(new FileReader(srcPath
					+ "//input//cran.qry"));
			// check for the stemmed query word in allterms hash table and the
			// set of alldocIds
			// here i represents each single document
			while (input.hasNext()) {
				word = input.next();
				// START single query processing
				if (!word.equals(s)) {
					if (!readObj.isStopWord(srcPath, word)) {
						stemWord = stemObj.steamWord(word); // stem this words
						for (int i = 1; i <= allDocIds.size(); i++) {
							int docCount = 0;
							double termFreq = 0;
							double idf = 0;
							double docQuerySimilarity = 0;
							double prevQuerySimilarity = 0;
							if (allTerms.containsKey(stemWord)) {
								idf = Math
										.log(1 + ((float) allDocIds.size() / allTerms
												.get(stemWord).size()));
								if (allTerms.get(stemWord).containsKey(i)) {
									docCount = allTerms.get(stemWord).get(i);
									termFreq = 1 + Math.log(docCount);
								}
							}else{
								termFreq = 0;
								idf = 1;
							}
							if (similarity.containsKey(i))
								prevQuerySimilarity = similarity.get(i);
							docQuerySimilarity = prevQuerySimilarity + termFreq
									* idf;
							similarity.put(i, docQuerySimilarity);
						}
					}
				} else {
					word = input.next();
				}
				// END
			}// while
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return similarity;
	}

	/** calculate Wd=sqroot of sum of sq. tf of each term of a document **/
	public double setWd(HashMap<String, Integer> singleDocTermCount) {
		Set<String> singleDocHashKey = singleDocTermCount.keySet();
		ArrayList<String> termsKeyArray = new ArrayList<String>();
		termsKeyArray.addAll(singleDocHashKey);
		double termFreqSq;
		double sum = 0;
		int docCount;
		for (String term : termsKeyArray) {
			String termName = term.toString();
			docCount = singleDocTermCount.get(termName);
			termFreqSq = (1 + Math.log(docCount)) * (1 + Math.log(docCount));
			sum += termFreqSq;
		}
		return sum;
	}

	/** Sort Hashmap **/
	public void orderBy(HashMap<Integer, Double> docWd,
			HashMap<Integer, Double> similarity) {
		Set<Integer> allDocIds = similarity.keySet();
		Path currentRelativePath = Paths.get("");
		String getProjPath = currentRelativePath.toAbsolutePath().toString();
		final File srcPath = new File(getProjPath + "//src");
		ArrayList<Integer> idsArray = new ArrayList<Integer>();
		idsArray.addAll(allDocIds);
		ArrayList<SortHash> sortObjArr = new ArrayList<SortHash>();
		for (Integer id : idsArray) {
			double prev = similarity.get(id);
			double wd = docWd.get(id);
			similarity.put(id, (float) prev / wd);
			SortHash sortObj = new SortHash(id, similarity.get(id));
			sortObjArr.add(sortObj);
		}

		Collections.sort(sortObjArr);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(srcPath
					+ "//output//outFile.txt"));
			for (SortHash sh : sortObjArr) {
				out.println(sh);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
}
