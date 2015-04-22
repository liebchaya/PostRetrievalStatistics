package scorers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import lm.AccurateDocsLengthLMResults;
import lm.LanguageModelResults;
import lm.LuceneLMResults;

import obj.ClarityScore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;


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
     * Specifically, the percentage of documents in the top
	 * K documents of L that are also present in the top K documents in L’.
	 * @param queryLine
	 * @param topDocs
	 * @param type see {@link ClarityScorer#score(String, int, int)}
	 * @param expNum
	 * @return
	 * @throws IOException
	 */
	public double score(String queryLine, int topDocs, int type, int expNum) throws IOException {
//		Query queryTerm;
//		List<Set<String>> queryList = StringUtils.String2ListSet(queryLine);
//		if (queryList.size() == 1){
//			if(queryList.get(0).size() == 1)
//				queryTerm = new TermQuery(new Term(Constants.field,queryLine));
//			else {
//				queryTerm = new BooleanQuery();
//				for (String t:queryList.get(0))
//					((BooleanQuery) queryTerm).add(new BooleanClause(new TermQuery(new Term(Constants.field,t)), Occur.SHOULD));
//			}
//		}
//		
//		else {
//		// start with the query
//		// extract the set of documents containing term t
//		SpanQuery[] andQ = new SpanQuery[queryList.size()];
//		int i=0;
//		for(Set<String> queryTerms:queryList){
//			if (queryTerms.size() > 1) {
//				SpanOrQuery orQ = new SpanOrQuery();
//				for(String q:queryTerms)
//					orQ.addClause(new SpanTermQuery(new Term(Constants.field,q)));
//				andQ[i] = orQ;
//			} else {
//				SpanQuery tq = new SpanTermQuery(new Term(Constants.field,(String)queryTerms.toArray()[0]));
//				andQ[i] = tq;
//			}
//			i++;
//		}
//		SpanNearQuery nearQ = new SpanNearQuery(andQ,0,true);
////		TermQuery nearQ = new TermQuery(new Term(Constants.field,queryLine));
//		queryTerm = nearQ;
//		}
//		TopDocs td = m_searcher.search(queryTerm,topDocs);
		
		TopDocs td;
		LanguageModelResults lm;
		if(type ==1){
			lm = new LuceneLMResults(m_searcher);
			td = lm.searchLM(queryLine, topDocs);
		} else {
			lm = new AccurateDocsLengthLMResults(1);
			td = lm.searchLM(queryLine, topDocs);
		}
		m_searcher = lm.getIndexSearcher();
		
		int counter = 0;
		// generate the expanded query
		BooleanQuery query = new BooleanQuery();
		while(m_iter.hasNext() && counter < expNum){
			ClarityScore cs = m_iter.next();
			TermQuery tq = new TermQuery(new Term(Constants.field,cs.getTerm()));
			tq.setBoost((float) cs.getScore());
			query.add(new BooleanClause(tq, Occur.SHOULD));
			counter++;
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

}
