

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.aliasi.classify.ScoredPrecisionRecallEvaluation;


public class PostAveragePrecision {
	private static HashMap<String,Boolean> annoMap = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		loadTargetTermsAnnotation();
		File postDir = new File("F:\\ScoredFile\\WikiQF\\Train");
		String evalFileName = "F:\\ScoredFile\\WikiQF\\Train.eval";
		BufferedWriter writer = new BufferedWriter(new FileWriter(evalFileName)); 
		for(File f:postDir.listFiles()){
			if(!f.getName().contains("top")) {
				 ScoredPrecisionRecallEvaluation eval = new ScoredPrecisionRecallEvaluation();
				 BufferedReader reader = new BufferedReader(new FileReader(f));
				 String line = reader.readLine();
				 while (line!=null){
					 String[] tokens = line.split("\t");
					 String targetTerm = tokens[0];
					 Double score = Double.parseDouble(tokens[1]);
					 Boolean anno = annoMap.get(targetTerm);
//					 System.out.println(f.getName()+"\t"+targetTerm);
					 eval.addCase(anno, score);
					 line = reader.readLine();
				 }
				 reader.close();
				 writer.write(f.getName()+ "\t" + eval.averagePrecision() + "\n");
			}
		}
		writer.close();
	}
		
		 	
private static void loadTargetTermsAnnotation() throws IOException{
	 annoMap = new HashMap<String, Boolean>();
	 BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_orig.txt"));
	 String line = reader.readLine();
	 while (line!=null){
		 annoMap.put(line.split("\t")[0], (line.split("\t")[1].equals("0")?false:true));
		 line = reader.readLine();
	 }
	 reader.close();
	 
	 reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTestSet_orig.txt"));
	 line = reader.readLine();
	 while (line!=null){
		 annoMap.put(line.split("\t")[0], (line.split("\t")[1].equals("0")?false:true));
		 line = reader.readLine();
	 }
	 reader.close();
	
}
}
