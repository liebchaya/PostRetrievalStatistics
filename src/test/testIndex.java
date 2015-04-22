package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import index.IndriIndexCorpora;


public class testIndex {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		 IndriIndexCorpora index = new IndriIndexCorpora();
//		 index.index("C:\\indriInd","C:\\testCorpora2");
		
		
		Directory dir = FSDirectory.open(new File("F:/Responsa/indexes/bigramDirichletAccurate"));
		DirectoryReader reader = DirectoryReader.open(dir);
		for (String s:reader.document(200).getValues("TERM_VECTOR"))
			System.out.println(s);
		
		System.out.println("**************************");
		
		dir = FSDirectory.open(new File("F:/Responsa/indexes/triDirichletAccurate"));
		DirectoryReader reader2 = DirectoryReader.open(dir);
		for (String s:reader2.document(200).getValues("TERM_VECTOR"))
			System.out.println(s);
		
		
	}

}
