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

public class Relevancy {
	
	public void setRelevancy(){
		Path currentRelativePath = Paths.get("");
		String getProjPath = currentRelativePath.toAbsolutePath().toString();
		final File srcPath = new File(getProjPath + "//src");
		HashMap<Integer, ArrayList<Integer>> relevantHash = new HashMap<Integer, ArrayList<Integer>> ();
		ArrayList<Integer> relDoc = new ArrayList<Integer>();
		Scanner input = null;
		try {
			input = new Scanner(new FileReader(srcPath + "//input//cranqrel"));
			Integer key = 0;
			while( input.hasNext() ){
				int temp = key;
				key = input.nextInt();
				if(key != temp){
					relDoc.clear();
				}
				relDoc.add(input.nextInt());
				relevantHash.put(key, relDoc);
				input.nextInt();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			input.close();
		}
		getPrecisionRecal(relevantHash);
	}
	
	public void getPrecisionRecal( HashMap<Integer, ArrayList<Integer>> relevantHash ){
		Set<Integer> relevantHashKey = relevantHash.keySet();
		ArrayList<Integer> relQueryKeyArray = new ArrayList<Integer>();
		relQueryKeyArray.addAll(relevantHashKey);
		Collections.sort(relQueryKeyArray);
		double precision;
		double recal;
		int count = 0;
		PrintWriter out = null;
		Path currentRelativePath = Paths.get("");
		String getProjPath = currentRelativePath.toAbsolutePath().toString();
		final File srcPath = new File(getProjPath + "//src");
		try {
			out = new PrintWriter(new FileWriter(srcPath
					+ "//output//outRelevant.txt"));
			for (Integer quer: relQueryKeyArray){
				out.println("Processing query "+quer);
	            int total = relevantHash.get(quer).size();  //total no. of relevant for that query
	            for( int i = 1; i<=1400; i++ ){
	            	if(relevantHash.get(quer).contains(i)){
	            		count++;
	            	}
	            	precision = (float)count/i;
	            	recal = (float)count/total;
	            	out.println("precision "+precision+" recal"+recal);
	            }
	            count = 0;
	            out.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			out.close();
		}
	}
}

