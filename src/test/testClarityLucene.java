package test;

import index.MyLMJelinekMercerSimilarity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import scorers.ClarityScorer;
import scorers.QFScorer;

public class testClarityLucene {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		config.setSimilarity(new LMJelinekMercerSimilarity((float) 0.5));
		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "time time");
		addDoc(w, "only time");
		addDoc(w, "watch now");
		w.close();
		
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		LMJelinekMercerSimilarity sim = new LMJelinekMercerSimilarity((float) 0.5);
		searcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.5));
		TermQuery q = new TermQuery(new Term("TERM_VECTOR","time"));
	
		Set<String> queryTerms = new HashSet<String>();
		queryTerms.add("time");
		ClarityScorer scorer = new ClarityScorer(searcher);
		System.out.println(scorer.score(queryTerms));
		
		QFScorer scorer2 = new QFScorer(searcher,scorer.getTopTermsClarityScores());
		System.out.println(scorer2.score(queryTerms));
//		TopDocs tp = searcher.search(q, 10);
//		System.out.println(tp.getMaxScore());
//		System.out.println(searcher.explain(q, 1));
//		System.out.println(searcher.explain(q, 0));
	}

	private static void addDoc(IndexWriter w, String title) throws IOException {
		final FieldType BodyOptions = new FieldType();
		BodyOptions.setIndexed(true);
		BodyOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		BodyOptions.setStored(true);
		BodyOptions.setStoreTermVectors(true);
		  Document doc = new Document();
		  doc.add(new Field("TERM_VECTOR", title, BodyOptions));
		  doc.add(new IntField("LENGTH", title.split(" ").length,Store.YES));
		  w.addDocument(doc);
		}

}
