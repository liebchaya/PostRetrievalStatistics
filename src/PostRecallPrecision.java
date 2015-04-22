

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


public class PostRecallPrecision {
	private static HashMap<String,Boolean> annoMap = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		loadTargetTermsAnnotation();
		
		File postDir = new File("F:\\ScoredFileTest\\WikiTitleExp5\\Post");
		for(File evalDir:postDir.listFiles()){
			if (!evalDir.isDirectory()||evalDir.getName().equals("Curves"))
				continue;
			String evalFileName = postDir+"\\"+evalDir.getName()+".eval"; 
			BufferedWriter writer = new BufferedWriter(new FileWriter(evalFileName));
			for (File f:evalDir.listFiles()){
				if (!f.getName().endsWith(".txt"))
					continue;
			 if (!f.isDirectory() && !f.getName().contains("CLARITY")){
				 ScoredPrecisionRecallEvaluation eval = new ScoredPrecisionRecallEvaluation();
				 BufferedReader reader = new BufferedReader(new FileReader(f));
				 String line = reader.readLine();
				 while (line!=null){
					 String[] tokens = line.split("\t");
					 String targetTerm = tokens[0];
					 Double score = Double.parseDouble(tokens[1]);
					 if (score.isNaN()){
						 if(f.getName().contains("NQC"))
							 score = -1.0;
						 if(f.getName().contains("WIG"))
							 score = Double.NEGATIVE_INFINITY;
					 }
					 Boolean anno = annoMap.get(targetTerm);
//					 System.out.println(f.getName()+"\t"+targetTerm);
					 eval.addCase(anno, score);
					 line = reader.readLine();
				 }
				 reader.close();
				 
				File curvesDir = new File(postDir+"\\"+evalDir.getName()+"\\Curves");
				if (!curvesDir.exists())
					curvesDir.mkdir();
				BufferedWriter curveWriter = new BufferedWriter(new FileWriter(curvesDir+"\\"+f.getName()));
				double[][] pr = eval.prScoreCurve(false);
				for(int i=0;i<eval.numCases();i++){
					for(int j=0;j<3;j++)
						curveWriter.write((pr[i][j]+ "\t"));
					curveWriter.write("\n");
				}
				curveWriter.close();
				writer.write(f.getName()+ "\t" + eval.averagePrecision() + "\n");
			} else  if (!f.isDirectory() && f.getName().equals("CLARITY.txt")) {
				 ScoredPrecisionRecallEvaluation evalCla = new ScoredPrecisionRecallEvaluation();
				 ScoredPrecisionRecallEvaluation evalQf = new ScoredPrecisionRecallEvaluation();
				 BufferedReader reader = new BufferedReader(new FileReader(f));
				 String line = reader.readLine();
				 while (line!=null){
					 String[] tokens = line.split("\t");
					 String targetTerm = tokens[0];
					 Double scoreCla = Double.parseDouble(tokens[1]);
					 Double scoreQf = Double.parseDouble(tokens[2]);
//					 if (scoreQf.isNaN())
//						 scoreQf = 0.0;
					 Boolean anno = annoMap.get(targetTerm);
//					 System.out.println(f.getName()+"\t"+targetTerm);
					 evalCla.addCase(anno, scoreCla);
					 evalQf.addCase(anno, scoreQf);
					 line = reader.readLine();
				 }
				 reader.close();
				 
				File curvesDir = new File(postDir+"\\"+evalDir.getName()+"\\Curves");
				if (!curvesDir.exists())
					curvesDir.mkdir();
				BufferedWriter curveWriter = new BufferedWriter(new FileWriter(curvesDir+"\\CLARITY.txt"));
				double[][] pr = evalCla.prScoreCurve(false);
				for(int i=0;i<evalCla.numCases();i++){
					for(int j=0;j<3;j++)
						curveWriter.write((pr[i][j]+ "\t"));
					curveWriter.write("\n");
				}
				curveWriter.close();
				curveWriter = new BufferedWriter(new FileWriter(curvesDir+"\\QF.txt"));
				pr = evalQf.prScoreCurve(false);
				for(int i=0;i<evalQf.numCases();i++){
					for(int j=0;j<3;j++)
						curveWriter.write((pr[i][j]+ "\t"));
					curveWriter.write("\n");
				}
				curveWriter.close();
				writer.write("CLARITY.txt\t" + evalCla.averagePrecision() + "\n");
				writer.write("QF\t" + evalQf.averagePrecision() + "\n");
			}
		}
		writer.close();
		}
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
