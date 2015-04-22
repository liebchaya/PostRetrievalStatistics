package lm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQueryDirichletLM;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQueryDirichletLM;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.LMDirichletSimilarityAccurateDocLength;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import scorers.Constants;
import utils.StringUtils;

public class AccurateDocsLengthLMResults implements LanguageModelResults {

	public AccurateDocsLengthLMResults(int type) throws IOException{
		if (type == 1) {
			Directory dir = FSDirectory.open(new File("F:/Responsa/indexes/unigDirichletAccurate"));
			DirectoryReader reader = DirectoryReader.open(dir);
			m_unigSearcher = new IndexSearcher(reader);
			Similarity sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_unigSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("F:/Responsa/indexes/bigramDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_bigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_bigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("F:/Responsa/indexes/triDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_trigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_trigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("F:/Responsa/indexes/fourDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_fourgramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_fourgramSearcher.setSimilarity(sim);
		} else if(type == 2) {
			Directory dir = FSDirectory.open(new File("H:/Prediction/Responsa/indexes/unigWikiDirichletAccurate"));
			DirectoryReader reader = DirectoryReader.open(dir);
			m_unigSearcher = new IndexSearcher(reader);
			Similarity sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_unigSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("H:/Prediction/Responsa/indexes/bigramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_bigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_bigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("H:/Prediction/Responsa/indexes/trigramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_trigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_trigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File("H:/Prediction/Responsa/indexes/fourgramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_fourgramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_fourgramSearcher.setSimilarity(sim);
		}
	}
	
	@Override
	public TopDocs searchLM(String queryLine, int topDocs) throws IOException {
		LinkedList<String> queryList = StringUtils.String2PhraseList(queryLine);
		int type = queryList.get(0).split(" ").length;
		BooleanQueryDirichletLM fullQuery = new BooleanQueryDirichletLM();
		for(String t:queryList){
			TermQueryDirichletLM query = new TermQueryDirichletLM(new Term(Constants.field,t.trim()));
			fullQuery.add(query,Occur.SHOULD);
		}
		TopDocs td;
		if (type == 1){
			td = m_unigSearcher.search(fullQuery, topDocs);
			m_currSearcher = m_unigSearcher;
		}
		else if (type == 2) {
			td = m_bigramSearcher.search(fullQuery, topDocs);
			m_currSearcher = m_bigramSearcher;
		}
		else if (type == 3){
			td = m_trigramSearcher.search(fullQuery, topDocs);
			m_currSearcher = m_trigramSearcher;
		}
		else {
			td = m_fourgramSearcher.search(fullQuery, topDocs);
			m_currSearcher = m_fourgramSearcher;
		}
		return td;
	}
	
	private IndexSearcher m_unigSearcher;
	private IndexSearcher m_bigramSearcher;
	private IndexSearcher m_trigramSearcher;
	private IndexSearcher m_fourgramSearcher;
	private IndexSearcher m_currSearcher;
	
	@Override
	public IndexSearcher getIndexSearcher() {
		return  m_currSearcher;
	}
	
}

