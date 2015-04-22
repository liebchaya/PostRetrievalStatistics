package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.LMDirichletSimilarityAccurateDocLength;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CheckIndexes {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Directory dir = FSDirectory.open(new File("F:/Responsa/indexes/unigDirichletAccurate"));
		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher m_unigSearcher = new IndexSearcher(reader);
		Similarity sim = new LMDirichletSimilarityAccurateDocLength(1000);
		m_unigSearcher.setSimilarity(sim);
		
		dir = FSDirectory.open(new File("F:/Responsa/indexes/bigramDirichletAccurate"));
		reader = DirectoryReader.open(dir);
		IndexSearcher m_bigramSearcher = new IndexSearcher(reader);
		sim = new LMDirichletSimilarityAccurateDocLength(1000);
		m_bigramSearcher.setSimilarity(sim);
		
		dir = FSDirectory.open(new File("F:/Responsa/indexes/triDirichletAccurate"));
		reader = DirectoryReader.open(dir);
		IndexSearcher m_trigramSearcher = new IndexSearcher(reader);
		sim = new LMDirichletSimilarityAccurateDocLength(1000);
		m_trigramSearcher.setSimilarity(sim);
		
		dir = FSDirectory.open(new File("F:/Responsa/indexes/fourDirichletAccurate"));
		reader = DirectoryReader.open(dir);
		IndexSearcher m_fourgramSearcher = new IndexSearcher(reader);
		sim = new LMDirichletSimilarityAccurateDocLength(1000);
		m_fourgramSearcher.setSimilarity(sim); 
		
		int docId = 2000;
		Document d1 = m_unigSearcher.doc(docId);
		System.out.println(d1.get("ID")+ "\t" + d1.get("TERM_VECTOR"));
		Document d2 = m_bigramSearcher.doc(docId);
		System.out.println(d2.get("ID")+ "\t" + d2.get("TERM_VECTOR"));
		Document d3 = m_trigramSearcher.doc(docId);
		System.out.println(d3.get("ID")+ "\t" + d3.get("TERM_VECTOR"));
		Document d4 = m_fourgramSearcher.doc(docId);
		System.out.println(d4.get("ID")+ "\t" + d4.get("TERM_VECTOR"));

	}

}
