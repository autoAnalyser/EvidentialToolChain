package etc.utils;

import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.XML;
import java.util.Scanner;
import java.lang.InterruptedException;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String xml2json(String xml) {
	try {
	    org.json.JSONObject jsonObj = XML.toJSONObject(xml);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    PrintStream old = System.out;
	    System.setOut(ps);
	    System.out.println(jsonObj);
	    System.out.flush();
	    System.setOut(old);	    
	    return baos.toString();
	    
	} catch(Exception e) {
	    e.printStackTrace();
	    return "";
        }
    }
    
    public static String getFileContent(String fileName) {
        String s, Out = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while((s = br.readLine()) != null) {
            Out = Out + "\n" + s;
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Out;
    }

    public static void runCMD0(String cmd0){
        Runtime run = Runtime.getRuntime();
        try {
            String[] cmd = { "/bin/sh", "-c", cmd0 };
            Process pr = run.exec(cmd);
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String runCMD1(String cmd0){
        Runtime run = Runtime.getRuntime();
        String Out = "";
        try {
            String[] cmd = { "/bin/sh", "-c", cmd0 };
            Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line=buf.readLine())!=null) {
                Out = Out + "\n" + line;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Out;
    }
    
    public static void annotateFile(String InFile, String assertion, Long line, String OutFile) {
        try {
            Scanner scan = new Scanner(new File(InFile));
            FileWriter fw = new FileWriter(OutFile);
            fw.write("#include <assert.h>\n");
            int lcount = 1;
            while(scan.hasNextLine()){
                String lineStr = scan.nextLine();
                if (lcount == line)
                    fw.write(assertion + "\n" + lineStr + "\n");
                else
                    fw.write(lineStr + "\n");
                lcount++;
            }
            fw.flush();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) delete(c);
        }
        if (!f.delete()) {
            System.out.println("Failed to delete file: " + f.getAbsolutePath());
            System.exit(0);
        }
    }
    
}
