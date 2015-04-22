package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;


import obj.ClarityScore;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import scorers.ClarityScorer;
import scorers.NQCScorer;
import scorers.QFScorer;
import scorers.WigScorer;




public class CreateScoredFiles {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int lineCount = 0;
//		String targetTermFile ="F:\\ScoredFile\\CleanTrainSet_morph.txt";
		// open Lucene index
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  

		
		String targetTermFile ="F:\\ScoredFile\\FinalTrainSet_morph.txt";
		File preTestScored = new File("F:\\ScoredFile\\Post\\Train");
//		String targetTermFile ="F:\\ScoredFile\\Try_morph.txt";
//		File preTestScored = new File("F:\\TestScoredFile");
		
//		String targetTermFile ="F:\\ScoredFile\\FinalTestSet_morph.txt";
//		File preTestScored = new File("F:\\ScoredFile\\Post\\Test");
		
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermFile));
//		BufferedWriter writer = new BufferedWriter(new FileWriter(preTestScored.getAbsolutePath()+"\\topClarity.txt"));
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(preTestScored.getAbsolutePath()+"\\NQC.txt"));
		// initialize scorers
//		ClarityScorer clarity = new ClarityScorer(searcher);
//		WigScorer wig = new WigScorer(searcher);
		NQCScorer nqc = new NQCScorer(searcher);
		
		String line = fileReader.readLine();
		while (line != null) {
			lineCount++;
//			String str = ""; 
			String query = line;
//			double score = clarity.score(query,5,2,1);
//			writer.write(line.split("\t")[0]);
//			Iterator<ClarityScore> it = clarity.getTopTermsClarityScores();
//			while(it.hasNext()) {
//				ClarityScore cs = it.next();
//				str = str + "\t" + cs.getTerm() + "#" + cs.getScore(); 
//	    	}
//			writer.write("\t"+str.trim());
//			
//			QFScorer qf = new QFScorer(searcher, clarity.getTopTermsClarityScores());
////			score = qf.score(query,100,2,5);
//			fileWriter.write(line.split("\t")[0]+"\t"+score+"\t"+qf.score(query,50,2,5)+"\n");					
////			fileWriter.write(line.split("\t")[0]+"\t"+score+"\n");
//			writer.newLine();
			
			double score = nqc.score(query, 5, 2);
			fileWriter.write(line.split("\t")[0]+"\t"+score+"\n");
			line = fileReader.readLine();
			if (lineCount%25==0){
				System.out.println("lineCount: "+ lineCount);
				fileWriter.flush();
			}
		}
		reader.close();
		directory.close();
		fileWriter.close();
		fileReader.close();
//		writer.close();

	}

}
