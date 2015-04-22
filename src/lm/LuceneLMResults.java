package lm;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import scorers.Constants;
import utils.StringUtils;

public class LuceneLMResults implements LanguageModelResults {

	public LuceneLMResults(IndexSearcher searcher){
		 Similarity sim = new LMDirichletSimilarity(1000);
		 searcher.setSimilarity(sim);
		 m_searcher = searcher;
	}
	
	@Override
	public TopDocs searchLM(String queryLine, int topDocs ) throws IOException {
		Query query;
		List<Set<String>> queryList = StringUtils.String2ListSet(queryLine);
		if (queryList.size() == 1){
			if(queryList.get(0).size() == 1)
				query = new TermQuery(new Term(Constants.field,queryLine));
			else {
				query = new BooleanQuery();
				for (String t:queryList.get(0))
					((BooleanQuery) query).add(new BooleanClause(new TermQuery(new Term(Constants.field,t)), Occur.SHOULD));
			}
		}
		
		else {
		// start with the query
		// extract the set of documents containing term t
		SpanQuery[] andQ = new SpanQuery[queryList.size()];
		int i=0;
		for(Set<String> queryTerms:queryList){
			if (queryTerms.size() > 1) {
				SpanOrQuery orQ = new SpanOrQuery();
				for(String q:queryTerms)
					orQ.addClause(new SpanTermQuery(new Term(Constants.field,q)));
				andQ[i] = orQ;
			} else {
				SpanQuery tq = new SpanTermQuery(new Term(Constants.field,(String)queryTerms.toArray()[0]));
				andQ[i] = tq;
			}
			i++;
		}
		SpanNearQuery nearQ = new SpanNearQuery(andQ,0,true);
//		TermQuery nearQ = new TermQuery(new Term(Constants.field,queryLine));
		query = nearQ;
		}
		System.out.println(query);
		TopDocs td = m_searcher.search(query,topDocs);
		return td;
	}
	
	private IndexSearcher m_searcher;

	@Override
	public IndexSearcher getIndexSearcher() {
		return m_searcher;
	}
}
