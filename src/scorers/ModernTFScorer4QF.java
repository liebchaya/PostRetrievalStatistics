package scorers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import obj.ClarityScore;
import com.aliasi.util.BoundedPriorityQueue;

public class ModernTFScorer4QF {
	public Iterator<ClarityScore> getTopTermsTfScores(String term){
		return dataMap.get(term).iterator();
	}
	
	
	public void loadQeTerms(File termsFile, int topTerms) throws IOException {
		maxSize = topTerms;
		dataMap = new HashMap<String, LinkedList<ClarityScore>>();
		BufferedReader fileReader = new BufferedReader(new FileReader(termsFile));
		String line = fileReader.readLine();
		while (line!=null){
			String[] tokens = line.split("\t");
			if (tokens.length > 1) {
				LinkedList<ClarityScore> expSet = new LinkedList<ClarityScore>();
				for(int i=0; i<maxSize; i++){
					if (tokens.length > i){
						ClarityScore cs = new ClarityScore(tokens[i].split("#")[0], Double.parseDouble(tokens[i].split("#")[1]));
						expSet.add(cs);
					}
				}
				dataMap.put(tokens[0],expSet);
			}
			line = fileReader.readLine();
		}
		fileReader.close();

	}
	
	private int maxSize = 30;
	HashMap<String, LinkedList<ClarityScore>> dataMap = null;
}
