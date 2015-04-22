package lm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQueryDirichletLM;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermQueryDirichletLM;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarityAccurateDocLength;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import scorers.Constants;
import utils.StringUtils;

public class LuceneTermLMResults implements LanguageModelResults {

	public LuceneTermLMResults() throws IOException{
		Directory dir = FSDirectory.open(new File("F:/Responsa/indexes/unigDirichlet"));
		DirectoryReader reader = DirectoryReader.open(dir);
		m_unigSearcher = new IndexSearcher(reader);
		Similarity sim = new LMDirichletSimilarity(1000);
		m_unigSearcher.setSimilarity(sim);
		
		dir = FSDirectory.open(new File("F:/Responsa/indexes/bigramDirichlet"));
		reader = DirectoryReader.open(dir);
		m_bigramSearcher = new IndexSearcher(reader);
		sim = new LMDirichletSimilarity(1000);
		m_bigramSearcher.setSimilarity(sim);
	}
	
	@Override
	public TopDocs searchLM(String queryLine, int topDocs) throws IOException {
		LinkedList<String> queryList = StringUtils.String2PhraseList(queryLine);
		int type = queryList.get(0).split(" ").length;
		BooleanQuery fullQuery = new BooleanQuery();
		for(String t:queryList){
			TermQuery query = new TermQuery(new Term(Constants.field,t.trim()));
			fullQuery.add(query,Occur.SHOULD);
		}
		TopDocs td;
		if (type == 1)
			td = m_unigSearcher.search(fullQuery, topDocs);
		else 
			td = m_bigramSearcher.search(fullQuery, topDocs);
		return td;
	}
	
	private IndexSearcher m_unigSearcher;
	private IndexSearcher m_bigramSearcher;
	
	@Override
	public IndexSearcher getIndexSearcher() {
		return  m_bigramSearcher;
	}
	
}

