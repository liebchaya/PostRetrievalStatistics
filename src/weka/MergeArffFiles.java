package weka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class MergeArffFiles {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader readerPre = new BufferedReader(new FileReader("F:\\resultPre.arff"));
		BufferedReader readerPost = new BufferedReader(new FileReader("F:\\resultPost.arff"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("F:\\resultMerge.arff"));
		writer.write("@RELATION PredictionMeasures\n\n");
		readerPre.readLine();
		readerPost.readLine();
		readerPre.readLine();
		readerPost.readLine();
		String line= readerPre.readLine();
		while (line.startsWith("@ATTRIBUTE")){
			writer.write(line+"\n");
			line= readerPre.readLine();
		}
		String line2= readerPost.readLine();
		while (line2.startsWith("@ATTRIBUTE")){
			writer.write(line2+"\n");
			line2= readerPost.readLine();
		}
		readerPre.readLine();
		readerPost.readLine();
		writer.write("\n@DATA\n");
		HashMap<String,String> dataMap = new HashMap<String, String>();
		line= readerPre.readLine();
		while (line!=null){
			dataMap.put(line.substring(line.lastIndexOf(",")+1), line.substring(0,line.lastIndexOf(",")));
			line= readerPre.readLine();
		}
		int counter = 0;
		line2= readerPost.readLine();
		while (line2!=null){
			String term = line2.substring(line2.lastIndexOf(",")+1);
			String prevLine = dataMap.get(term);
			writer.write(prevLine+","+line2.substring(0,line2.lastIndexOf(","))+",");
			if (counter<15)
				writer.write("0\n");
			else
				writer.write("1\n");
			counter++;
			line2= readerPost.readLine();
		}
		writer.close();
		readerPost.close();
		readerPre.close();
	}

}
