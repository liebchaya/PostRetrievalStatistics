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


public class WigScorer{
	
	public WigScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	
	/**
	 * WIG(q) = 1/k*SUM_d_k(SUM_t_q(lambda(t)*log(Pr(t|d)/Pr(t|D))}
	 * estinated as: 1/(k*sqrt(|q|))*(avg(score(d))-score(D))
	 * @param queryLine
	 * @param topDocs
	 * @param type see {@link ClarityScorer#score(String, int, int)}
	 * @return
	 * @throws IOException
	 */
	public double score(String queryLine, int topDocs, int type) throws IOException {
		long C= m_searcher.collectionStatistics(Constants.field).sumTotalTermFreq();
		
//		List<Set<String>> queryList = StringUtils.String2ListSet(queryLine);
//		
//		// start with the query
//		// extract the set of documents containing term t
//		// calculate general appearences in the corpus
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
//		
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
		
//		sum = 0;
//		// calculate general appearences in the corpus
//		//this is not the best way of doing this, but it works for the example.  See http://www.slideshare.net/lucenerevolution/is-your-index-reader-really-atomic-or-maybe-slow for higher performance approaches
//		AtomicReader wrapper = SlowCompositeReaderWrapper.wrap(m_searcher.getIndexReader());
//		Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
//		Spans spans = nearQ.getSpans(wrapper.getContext(), new Bits.MatchAllBits(m_searcher.getIndexReader().numDocs()), termContexts);
//		while(spans.next())
//			sum++;
//		end = new Date().getTime();
//		System.out.println("Span sum: " + sum + " time: " + (end-start));
		
		sum = 0;
		m_searcher = lm.getIndexSearcher();
//		start = new Date().getTime();
		int size = queryLine.split("\t")[0].split(" ").length;
		for(String s:queryLine.split("\t")){
			Term trm = new Term(Constants.field,s);
			TermStatistics ts = m_searcher.termStatistics(trm, TermContext.build(m_searcher.getIndexReader().getContext(), trm));
			sum +=  ts.totalTermFreq();
		}
//		end = new Date().getTime();
//		System.out.println("TermStatistics: " +  sum + " time: " + (end-start));
		
		double scoreD = sum/C;
		double score = (1/(topDocs*Math.sqrt(size)))*(avg-scoreD);	
//		double score = 0;
		return score;
	}
		
	
	
	public String getName() {
		return "WIG";
	}

		
private IndexSearcher m_searcher;

}
