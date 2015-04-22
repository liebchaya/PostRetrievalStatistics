package scorers;

import java.io.IOException;

import lm.AccurateDocsLengthLMResults;
import lm.LanguageModelResults;
import lm.LuceneTermLMResults;


import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;


public class NQCScorer{
	
	public NQCScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	
	/**
	 * NQC(q) = sqrt(1/k*exp(sum(score(d)-mu)))/score(D)
	 * mu = mean
	 * @param queryLine
	 * @param topDocs
	 * @param type see {@link ClarityScorer#score(String, int, int)}
	 * @return
	 * @throws IOException
	 */
	public double score(String queryLine, int topDocs, int type) throws IOException {
		long C= m_searcher.collectionStatistics(Constants.field).sumTotalTermFreq();
//		// start with the query
//		// extract the set of documents containing term t
//		// calculate general appearences in the corpus
//		List<Set<String>> queryList = StringUtils.String2ListSet(queryLine);
//		SpanQuery[] andQ = new SpanQuery[queryList.size()];
//		int i=0;
//		for(Set<String> queryTerms:queryList){
//			SpanOrQuery orQ = new SpanOrQuery();
//			for(String q:queryTerms)
//				orQ.addClause(new SpanTermQuery(new Term(Constants.field,q)));
//			andQ[i] = orQ;
//			i++;
//		}
//		SpanNearQuery nearQ = new SpanNearQuery(andQ,0,true);

		TopDocs td;
		LanguageModelResults lm;
		if(type ==1){
			lm = new LuceneTermLMResults();
			td = lm.searchLM(queryLine, topDocs);
		} else {
			lm = new AccurateDocsLengthLMResults(1);
			td = lm.searchLM(queryLine, topDocs);
		}
		
//		TopDocs td = m_searcher.search(nearQ, topDocs);
		// average relevant documents' scores
		double sum = 0;
		for (ScoreDoc scoreD : td.scoreDocs) 
			sum += scoreD.score;
		double avg = sum/td.scoreDocs.length;
		
		double sigma = 0;
		for (ScoreDoc scoreD : td.scoreDocs)
			sigma += Math.pow(scoreD.score-avg,2);
		
		sum = 0;
		m_searcher = lm.getIndexSearcher();
//		// calculate general appearences in the corpus
//		//this is not the best way of doing this, but it works for the example.  See http://www.slideshare.net/lucenerevolution/is-your-index-reader-really-atomic-or-maybe-slow for higher performance approaches
//		AtomicReader wrapper = SlowCompositeReaderWrapper.wrap(m_searcher.getIndexReader());
//		Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
//		Spans spans = nearQ.getSpans(wrapper.getContext(), new Bits.MatchAllBits(m_searcher.getIndexReader().numDocs()), termContexts);
//		while(spans.next())
//			sum++;
		for(String s:queryLine.split("\t")){
			Term trm = new Term(Constants.field,s);
			TermStatistics ts = m_searcher.termStatistics(trm, TermContext.build(m_searcher.getIndexReader().getContext(), trm));
			sum +=  ts.totalTermFreq();
		}
		
		double scoreD = sum/C;
		double score = Math.sqrt((1/(double)topDocs)*sigma)/scoreD;	
		
		return score;
	}
		
	
	
	public String getName() {
		return "WIG";
	}

		
private IndexSearcher m_searcher;

}
