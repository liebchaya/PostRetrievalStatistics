package scorers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import lm.AccurateDocsLengthLMResults;
import lm.LanguageModelResults;
import lm.LuceneLMResults;

import obj.ClarityScore;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;

import com.aliasi.util.BoundedPriorityQueue;

import utils.MathUtils;
import utils.StringUtils;
import utils.Utils;


public class ClarityScorer{
	
	public ClarityScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	/**
	 * query-clarity = SUM_w{P(w|Q)*log(P(w|Q)/P(w))}
     * P(w)=cf(w)/|C|
	 * @param queryLine input query
	 * @param topDocs 
	 * @param type type - 1 = Lucene, 2 = accurate length (from Hadas)
	 * @param period 1 = mixed, 2 = modern (wikipedia)
	 * @return
	 * @throws IOException
	 */
	public double score(String queryLine, int topDocs, int type, int period) throws IOException {
		HashSet<String> querySet = StringUtils.String2PhraseSet(queryLine);
		queue = new BoundedPriorityQueue<ClarityScore>(ClarityScore.ClarityScoreComparator, maxSize);
		double sum_w = 0;
		TopDocs td;
		LanguageModelResults lm;
		if(type ==1){
//			lm = new LuceneTermLMResults();
			lm = new LuceneLMResults(m_searcher);
			td = lm.searchLM(queryLine, topDocs);
		} else {
			lm = new AccurateDocsLengthLMResults(period);
			td = lm.searchLM(queryLine, topDocs);
		}
		
		m_searcher = lm.getIndexSearcher();
		long C= m_searcher.collectionStatistics(Constants.field).sumTotalTermFreq();

		HashMap<Integer,Double> priors = new HashMap<Integer, Double>();
		HashMap<String,HashMap<Integer,Long>> freqData = new HashMap<String, HashMap<Integer,Long>>();
		for (ScoreDoc scoreDoc : td.scoreDocs) {
			int docId = scoreDoc.doc;
			priors.put(scoreDoc.doc, (double)scoreDoc.score);
//			System.out.println(scoreDoc.score);
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
		
		for (String t:freqData.keySet()){
		    Term trm = new Term(Constants.field,t);
			TermStatistics ts = m_searcher.termStatistics(trm, TermContext.build(m_searcher.getIndexReader().getContext(), trm));
			double p_w = (double) ts.totalTermFreq()/(double)C;
			HashMap<Integer, Long> freqMap = freqData.get(t);
			double t_sum = 0;
			for(int docId:freqMap.keySet()){
				double p = priors.get(docId);
				int d = Integer.parseInt(m_searcher.getIndexReader().document(docId).get("LENGTH"));
				double p_t = (double)freqMap.get(docId)/d;
				t_sum += (p_t*p);
			}
			double w_clarity = t_sum*MathUtils.Log(t_sum/p_w,2);
			if (!(querySet.contains(t) || t.contains("הפניה"))){
				// t = t.replaceAll("\\*|\\(|\\)|-|\\+|$|@|#|%|\"|!|:", "");
				// t = t.replace("?", "");
				t = t.replaceAll("\\p{Punct}", "");
				queue.offer(new ClarityScore(t, w_clarity));
			}
			sum_w += w_clarity;
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
//private double lambda = 0.5;
private int maxSize = 30;
private BoundedPriorityQueue<ClarityScore> queue;

}
