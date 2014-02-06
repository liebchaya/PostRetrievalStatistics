package scorers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import obj.ClarityScore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;


public class QFScorer{
	
	public QFScorer(IndexSearcher searcher, Iterator<ClarityScore> iter){
		m_searcher = searcher;
		m_iter = iter;
	}

	
	/**
	 * QF
	 * 1. The input is the query, the channel is the search system, and the set of
	 * results is the noisy output of the channel
     * 2. A new query is generated from the list of results, using the terms with
     * maximal contribution to the Clarity score, and then a second list of results
     * is retrieved for that query
     * The overlap between the two lists is used as a robustness score. 
    
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public double score(List<Set<String>> queryList) throws IOException {
		// start with the query
		// extract the set of documents containing term t
		SpanQuery[] andQ = new SpanQuery[queryList.size()];
		int i=0;
		for(Set<String> queryTerms:queryList){
			SpanOrQuery orQ = new SpanOrQuery();
			for(String q:queryTerms)
				orQ.addClause(new SpanTermQuery(new Term(Constants.field,q)));
			andQ[i] = orQ;
			i++;
		}
		SpanNearQuery nearQ = new SpanNearQuery(andQ,0,true);
		TopDocs td = m_searcher.search(nearQ, topDocs);
		
		// generate the expanded query
		BooleanQuery query = new BooleanQuery();
		while(m_iter.hasNext()){
			ClarityScore cs = m_iter.next();
			TermQuery tq = new TermQuery(new Term(Constants.field,cs.getTerm()));
			tq.setBoost((float) cs.getScore());
			query.add(new BooleanClause(tq, Occur.SHOULD));
		}
		System.out.println(query);
		TopDocs tdEq = m_searcher.search(query, topDocs);
		
		// compate the retrieved sets
		HashSet<Integer> idsSet = new HashSet<Integer>();
		for(ScoreDoc score:td.scoreDocs)
			idsSet.add(score.doc);
		
		int overlapCount = 0;
		for(ScoreDoc score:tdEq.scoreDocs)
			if (idsSet.contains(score.doc))
				overlapCount++;
			
		double RFscore = (double)overlapCount/td.totalHits;
		return RFscore;
			
		}
			

	public String getName() {
		return "QF";
	}

	private IndexSearcher m_searcher;
	Iterator<ClarityScore> m_iter;
	int topDocs = 20;

}
