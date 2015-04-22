package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import scorers.ClarityScorer;
import scorers.NQCScorer;
import scorers.QFScorer;
import scorers.WigScorer;
import weka.ArffFile;

public class PostRetrievalArff {

	/**
	 * @param args
	 * args[0] - input terms
	 * args[1] - index directory
	 * args[2] = output file
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// open input file
//		BufferedReader bReader = new BufferedReader(new FileReader(args[0]));
		BufferedReader bReader = new BufferedReader(new FileReader("F:\\1_morph.txt"));
		// open Lucene index
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
//		Directory directory = FSDirectory.open(new File(args[1]));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  
		
		// initialize scorers
		ClarityScorer clarity = new ClarityScorer(searcher);
		WigScorer wig = new WigScorer(searcher);
		NQCScorer nqc = new NQCScorer(searcher);
		
		// arff header
//		ArffFile arff = new ArffFile(args[2]);
		ArffFile arff = new ArffFile("F:\\resultPost2.arff");
		arff.setRelation("PostRetrievalMeasures");
		arff.addAttribute("Clarity", "REAL");
		arff.addAttribute("QF", "REAL");
		arff.addAttribute("Wig", "REAL");
		arff.addAttribute("NQC", "REAL");
		arff.addAttribute("DUNNY", "REAL");
		
		arff.writeArffHeader();
		
		String line = bReader.readLine();
		while(line!=null){
			String dataLine = "";
			
			String query = line.substring(line.indexOf("\t")+1);
			dataLine += clarity.score(query,2,2,1) + ",";
			QFScorer qf = new QFScorer(searcher, clarity.getTopTermsClarityScores());
			dataLine += qf.score(query,2,2,1) + ",";
			dataLine +=  wig.score(query,2,2) + ",";
			dataLine += nqc.score(query,50,2) + ",";
						
			dataLine += line.split("\t")[1];
			System.out.println(dataLine);
			arff.writeDataLine(dataLine);
			line = bReader.readLine();
		}
		bReader.close();
		arff.close();
		

	}
	

}
