package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CheckFileReadability {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader("F:\\ScoredFile\\WikiQF\\Test\\topClarity20.txt"));
		String line = fileReader.readLine();
		while (line!=null){
			String[] tokens = line.split("\t");
			System.out.println("Target term :" + tokens[0]);
			System.out.println("First: token=" + tokens[1].split("#")[0] + " score="+ tokens[1].split("#")[1]);
			System.out.println("Second: token=" + tokens[2].split("#")[0] + " score="+ tokens[2].split("#")[1]);
			System.out.println("Third: token=" + tokens[3].split("#")[0] + " score="+ tokens[3].split("#")[1]);
			line = fileReader.readLine();
		}
		fileReader.close();
	}

}
