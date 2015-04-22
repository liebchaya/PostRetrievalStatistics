package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
//import scorers.QFScorer;
import scorers.WigScorer;

public class testClarityLucene {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
//		Directory index = new RAMDirectory();
//
//		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
//		config.setSimilarity(new LMDirichletSimilarity(1000));
//		IndexWriter w = new IndexWriter(index, config);
//		addDoc(w, "new time"); 
//		addDoc(w, "only when");
//		addDoc(w, "watch now when");
//		w.close();
//		
//		IndexReader reader = IndexReader.open(index);
		
		FSDirectory dir = FSDirectory.open(new File("F:/Responsa/indexes/unigDirichlet"));
		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

//		String queryTerms = "new time\tnewwer time\tn timer";
//		String queryTerms = "שלום בית\tהשלום הבית";
		String queryTerms = "שלום בית\tהשלום הבית\tשלום הבית\tהשלום בית";
//		ClarityScorer scorer = new ClarityScorer(searcher);
//		System.out.println(scorer.score(queryTerms,5,1));
////		
//		QFScorer scorer2 = new QFScorer(scorer.getSearcher(),scorer.getTopTermsClarityScores());
//		System.out.println(scorer2.score(queryTerms,1000,1));
		WigScorer wig = new WigScorer(searcher);
		System.out.println(wig.score(queryTerms,100,2));
		
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
