package obj;

import java.util.Comparator;

public class ClarityScore implements Comparable<ClarityScore>{
	
	String term;
	double score;
	
	
	public ClarityScore(String term, double score) {
		super();
		this.term = term;
		this.score = score;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	@Override
	public int compareTo(ClarityScore arg0) {
		return Double.compare(this.score,arg0.score);
	}
	
	public static Comparator<ClarityScore> ClarityScoreComparator = new Comparator<ClarityScore>() {

		@Override
		public int compare(ClarityScore arg0, ClarityScore arg1) {
			return Double.compare(arg0.score,arg1.score);
		}



};
	
}


