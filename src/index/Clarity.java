package index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Clarity {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String clarityCommand = "C:/Program Files/Indri/Indri 5.5/bin/clarity";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
			    new FileInputStream("C:/query.xml"), "UTF-8"));
//		BufferedReader reader = new BufferedReader(new FileReader("C:/query.xml"));
		String query = reader.readLine();
		System.out.println(query);
		
//		String query = "��";
//		byte[] b = query.getBytes("UTF-8");
//		ByteBuffer b = Charset.forName("cp1255").encode(query);
		String hex = "\u05dc\u05d0";
//		String s = new String(b.array(), "cp1255");
		
//		System.out.println(hex);
		String index = "C:/indriInd";
		
		
//		String cmd = clarityCommand+" -query="+ hex+ " -index=" + index + " -documents=2 -terms=5";
//		System.out.println(cmd);
//		Process p = Runtime.getRuntime().exec(cmd);
		
		
//		ProcessBuilder pb = new ProcessBuilder(clarityCommand, "-query="+query, "-index="+index, "-documents=2", "-terms=5");
//		pb.environment().put("LANG", "he_IL.UTF-8");
//		System.out.println(pb.command());
//		Process p = pb.start();

		String[] cmd = new String[]{clarityCommand, "-query=\u05dc\u05d0", "-index="+index, "-documents=2", "-terms=5"};        
		Map<String, String> newEnv = new HashMap<String, String>();
		newEnv.putAll(System.getenv());
		String[] i18n = new String[cmd.length + 2];
		i18n[0] = "cmd";
		i18n[1] = "/C";
		i18n[2] = cmd[0];
		for (int counter = 1; counter < cmd.length; counter++)
		{
		    String envName = "JENV_" + counter;
		    i18n[counter + 2] = "%" + envName + "%";
		    newEnv.put(envName, cmd[counter]);
		}
		cmd = i18n;

		ProcessBuilder pb = new ProcessBuilder(cmd);
		Map<String, String> env = pb.environment();
		env.putAll(newEnv);
		final Process p = pb.start();
		
		
		//Read out dir output
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n",
                clarityCommand);
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        
        //Wait to get exit value
        try {
            int exitValue = p.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	// convert from internal Java String format -> UTF-8
	static public String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(array);
    }
	static public String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }
	
}
