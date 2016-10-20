package etc.workflows;

import org.json.simple.JSONArray;
//import java.nio.file.Files;
import java.io.File;

import etc.wrappers.*;
import etc.utils.*;

public class ModelChecking {
    String Tool;
    JSONArray Errors;
    String SourceFile;
    String TEMPDIR;
    JSONArray RefErrors;

    public ModelChecking(String Tool, JSONArray Errors, String SourceFile, String TEMPDIR){
        this.Tool = Tool;
        this.Errors = Errors;
        this.SourceFile = SourceFile;
        this.TEMPDIR = TEMPDIR;
        RefErrors = new JSONArray();
    }
    /*
    public void apply(String[] Tools, String SourceFile, String TEMPDIR) {
        String MergedFile = TEMPDIR + "/merged.json";
        String RefinedFile = TEMPDIR + "/refined.json";
        String RepSourceFile = TEMPDIR + "/Review/reps.tex";
        for (int i = 0 ; i < Tools.length ; i++ ){
            System.out.print("Refining initial analysis using " + Tools[i] + "...");
            if (Tools[i].equals("cbmc")){
                cbmcWRP.apply(SourceFile, TEMPDIR, MergedFile, RefinedFile);
                Report.toMergedReport(RefinedFile, RepSourceFile, 0);
                System.out.println("done");
            }
            else {
                System.out.println("[todo]");
            }
        }
        System.out.println("Generating merged reports...done");
    }
     */
    public void apply() {
        //System.out.print("Applying model checking using " + Tool + "...");
        System.out.print("Applying model checking using \u001B[1m" + Tool + "\u001B[0m...");
        if (Tool.equals("cbmc")){
            RefErrors = cbmcWRP.apply(Errors, SourceFile, TEMPDIR);
            //System.out.println("done");
            System.out.println("\u001B[32m done\u001B[0m");
        }
        else {
            //System.out.println("[todo]");
            System.out.println("\u001B[31m[todo]\u001B[0m");
        }
    }
    
    public JSONArray getRefErrors(){
        return RefErrors;
    }
    
    public void print() {
        System.out.println("Model Checking tool: " + Tool);
        System.out.println("RefErrors: " + RefErrors.toJSONString());
    }

}
