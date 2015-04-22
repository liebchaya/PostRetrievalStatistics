package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;


import obj.ClarityScore;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import qe.QeTreatment;

import scorers.ClarityScorer;
import scorers.NQCScorer;
import scorers.QFScorer;
import scorers.WigScorer;




public class QeScoredFilesExp {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String targetTermsFile ="F:\\ScoredFile\\FinalTestSet_morph.txt";
		File postScoredFile = new File("F:\\ScoredFileTest\\WikiTop50Exp5\\Post\\Test");
		String expFile = "F:\\tfIdfTestWikiTopDocs50.txt";
		int expNum = 5;
		
		if(!postScoredFile.exists())
			postScoredFile.mkdirs();
		
		int lineCount = 0;
//		String targetTermFile ="F:\\ScoredFile\\CleanTrainSet_morph.txt";
		// open Lucene index
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  
		
		QeTreatment qe = new QeTreatment();
		qe.loadQeTerms(new File(targetTermsFile),new File(expFile), expNum);

		HashMap<String,String> expandedQueriesMap = new HashMap<String, String>();
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermsFile));
		String line = fileReader.readLine();
		while (line != null) {
			String targetTerm = line.split("\t")[0];
			expandedQueriesMap.put(targetTerm, line+qe.getQeTermsLine(targetTerm));
			line = fileReader.readLine();
		}
		fileReader.close();
		
		TreeSet<String> sortedSet = new TreeSet<String>();
		sortedSet.addAll(expandedQueriesMap.keySet());
		
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\expansions.exp"));
		for(String target:sortedSet)
			fileWriter.write(expandedQueriesMap.get(target)+"\n");
		fileWriter.close();
		
		fileWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\confData.conf"));
		fileWriter.write("Expansions file: " + expFile + "\n");
		fileWriter.write("Expansions number: " + expNum + "\n");
		fileWriter.write("Expansions filter: " + "None" + "\n");
		fileWriter.close();
		
		NQCScorer nqc = new NQCScorer(searcher);
		fileWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\NQC.txt"));
		lineCount = 0;
		for(String target:sortedSet) {
			lineCount++;
			fileWriter.write(target + "\t" + nqc.score(expandedQueriesMap.get(target),5,2) + "\n");
			if (lineCount%50==0){
				System.out.println("NqcScorer: "+ lineCount);
				fileWriter.flush();
			}
		}
		fileWriter.close();
		
		WigScorer wig = new WigScorer(searcher);
		fileWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\WIG.txt"));
		lineCount = 0;
		for(String target:sortedSet) {
			lineCount++;
			fileWriter.write(target + "\t" + wig.score(expandedQueriesMap.get(target),5,2) + "\n");
			if (lineCount%50==0){
				System.out.println("WigScorer: "+ lineCount);
				fileWriter.flush();
			}
		}
		fileWriter.close();
		
		BufferedWriter topClarWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\topClarity.info"));
		fileWriter = new BufferedWriter(new FileWriter(postScoredFile.getAbsolutePath()+"\\CLARITY.txt"));
		ClarityScorer clarity = new ClarityScorer(searcher);
		lineCount = 0;
		for (String target:sortedSet) {
			lineCount++;
			double score = clarity.score(expandedQueriesMap.get(target),5,2,1);
			topClarWriter.write(target);
			Iterator<ClarityScore> it = clarity.getTopTermsClarityScores();
			String str = "";
			while(it.hasNext()) {
				ClarityScore cs = it.next();
				str = str + "\t" + cs.getTerm() + "#" + cs.getScore(); 
	    	}
			topClarWriter.write("\t"+str.trim()+"\n");
			QFScorer qf = new QFScorer(searcher, clarity.getTopTermsClarityScores());
			fileWriter.write(target+"\t"+score+"\t"+qf.score(expandedQueriesMap.get(target),50,2,5)+"\n");					
			if (lineCount%50==0){
				System.out.println("ClarityScorer: "+ lineCount);
				fileWriter.flush();
			}
		}
		reader.close();
		directory.close();
		fileWriter.close();
		fileReader.close();
		topClarWriter.close();

	}

}
