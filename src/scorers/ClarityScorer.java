package scorers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import obj.ClarityScore;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import com.aliasi.util.BoundedPriorityQueue;

import utils.MathUtils;
import utils.Utils;


public class ClarityScorer{
	
	public ClarityScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	
	/**
	 * query-clarity = SUM_w{P(w|Q)*log(P(w|Q)/P(w))}
     * P(w)=cf(w)/|C|
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public double score(List<Set<String>> queryList) throws IOException {
		queue = new BoundedPriorityQueue<ClarityScore>(ClarityScore.ClarityScoreComparator, maxSize);
		double sum_w = 0;
		long C= m_searcher.collectionStatistics(Constants.field).sumTotalTermFreq();
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
		HashMap<Integer,Double> priors = new HashMap<Integer, Double>();
		HashMap<String,HashMap<Integer,Long>> freqData = new HashMap<String, HashMap<Integer,Long>>();
		for (ScoreDoc scoreDoc : td.scoreDocs) {
			int docId = scoreDoc.doc;
			priors.put(scoreDoc.doc, (double)scoreDoc.score);
			Fields termVector = m_searcher.getIndexReader().getTermVectors(docId);
			Terms terms = termVector.terms(Constants.field);
			TermsEnum te = terms.iterator(null);
			while (te.next() != null) {
				String t = te.term().utf8ToString();
				if (freqData.containsKey(t)){
					freqData.get(t).put(docId, (long) te.totalTermFreq());
				} else {
					HashMap<Integer,Long> freqMap = new HashMap<Integer, Long>();
					freqMap.put(docId, (long) te.totalTermFreq());
					freqData.put(t, freqMap);
				}
			}
		}
		
		
		// posterior
		_logtoposterior(priors);
		
		Fields fields = MultiFields.getFields(m_searcher.getIndexReader());
		Terms terms = fields.terms(Constants.field);
		if (terms != null) {
			TermsEnum termsEnum = null;
			termsEnum = terms.iterator(termsEnum);
			while (termsEnum.next() != null){
				double p_w = (double)termsEnum.totalTermFreq()/(double)C;
				String t = termsEnum.term().utf8ToString();
				if (!freqData.containsKey(t))
					continue;
				HashMap<Integer, Long> freqMap = freqData.get(t);
				double t_sum = 0;
				for(int docId:freqMap.keySet()){
					double p = priors.get(docId);
					int d = Integer.parseInt(m_searcher.getIndexReader().document(docId).get("LENGTH"));
					double p_t = (double)freqMap.get(docId)/d;
					t_sum += (p_t*p);
				}
				double w_clarity = t_sum*MathUtils.Log(t_sum/p_w,2);
				queue.offer(new ClarityScore(t, w_clarity));
				sum_w += w_clarity;
				}
			}
			return sum_w;
		}
		

	
	public String getName() {
		return "clarity";
	}

	static void _logtoposterior(HashMap<Integer,Double> res) {
		 if (res.size() == 0)
			 return;
		 HashMap<Integer, Double> keys = (HashMap<Integer, Double>) Utils.sortByComparator(res,true);
		 double K = 0;
		 double sum=0;
		 boolean bFirst = true;
		 for(int docId:keys.keySet()){
			 if (bFirst){
				// first is max
				K = res.get(docId);
				bFirst = false;
			 }
			double newPost = Math.exp(res.get(docId) - K);
			res.put(docId,newPost);
		    sum += newPost;
		  }
		 for(int docId:keys.keySet()){
			 double newPost = res.get(docId)/sum;
			 res.put(docId,newPost);
		 }
		}

	public Iterator<ClarityScore> getTopTermsClarityScores(){
		return queue.iterator();
	}
	
private IndexSearcher m_searcher;
private int topDocs = 5;
//private double lambda = 0.5;
private int maxSize = 30;
private BoundedPriorityQueue<ClarityScore> queue;

}
