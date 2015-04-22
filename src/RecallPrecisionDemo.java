

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.aliasi.classify.ScoredPrecisionRecallEvaluation;


public class RecallPrecisionDemo {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ScoredPrecisionRecallEvaluation eval = new ScoredPrecisionRecallEvaluation();
		eval.addCase(true, 7);
		eval.addCase(true, 15);
		eval.addCase(true, 13);
		eval.addCase(false, 14);
		eval.addCase(true, 12);
		eval.addCase(true, 10);
		eval.addCase(false, 10);
		eval.addCase(true, 9);
		eval.addCase(false, 9);
		eval.addCase(false, 8);
		
		double[][] pr = eval.prScoreCurve(false);
		for(int i=0;i<eval.numCases();i++){
			System.out.println();
			for(int j=0;j<3;j++)
				System.out.print(pr[i][j]+ "\t");
		}
		PrintWriter pwFile = new PrintWriter("F:\\testCurve.csv");
		ScoredPrecisionRecallEvaluation.printScorePrecisionRecallCurve(pr, pwFile);
		pwFile.close();
		System.out.println(eval.averagePrecision());
	}
		
		 	

}
