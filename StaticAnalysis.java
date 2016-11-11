package etc.workflows;

import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import etc.wrappers.inferWRP;
import etc.wrappers.cppcheckWRP;
import etc.utils.*;

public class StaticAnalysis {

        /*
    public static void apply(String[] Tools, String FILEBASE, String FILEDIR, String TEMPDIR) {
        String MergedFile = TEMPDIR + "/merged.json";
        String RepSourceFile = TEMPDIR + "/Review/reps.tex";
        
        for (int i = 0 ; i < Tools.length ; i++ ){
            System.out.print("Static analysis using " + Tools[i] + "...");

            if (Tools[i].equals("infer")){
                Utils.runCMD0("cd " + FILEDIR + " && make clean && infer -- make &>/dev/null");
                String InferOrigFile = FILEDIR + "/infer-out/report.json";
                String InferFile = TEMPDIR + "/infer.json";
                
                inferWRP WRP = new inferWRP();
                inferWRP.apply(InferOrigFile, InferFile);
                Report.mergeJson(InferFile, MergedFile, i);
                Report.toInitialReport("infer", InferFile, RepSourceFile, i);
                System.out.println("done");
            }
            else if (Tools[i].equals("cppcheck")){
                cppcheckWRP WRP = new cppcheckWRP();
                WRP.apply(FILEBASE, FILEDIR, TEMPDIR);
                
                String CppCheckJsonFile = TEMPDIR + "/cppcheck.json";
                Report.mergeJson(CppCheckJsonFile, MergedFile, i);
                Report.toInitialReport("cppcheck", CppCheckJsonFile, RepSourceFile, i);
                System.out.println("done");
            }
            else {
                System.out.println("[todo]");
            }
        }
    }
    

    public static JSONArray apply(String[] Tools, String SourceFilePath) {
        File SourceFile = new File(SourceFilePath);
        JSONArray AllErrors = new JSONArray();
        JSONObject ToolErrors = new JSONObject();

        for (int i = 0 ; i < Tools.length ; i++ ){
            System.out.print("Static analysis using " + Tools[i] + "...");
            if (Tools[i].equals("infer")){
                ToolErrors = inferWRP.apply(SourceFile);
                AllErrors.add(ToolErrors);
                System.out.println("done");
            }
            else if (Tools[i].equals("cppcheck")){
                ToolErrors = cppcheckWRP.apply(SourceFile);
                AllErrors.add(ToolErrors);
                System.out.println("done");
            }
            else {
                System.out.println("[todo]");
            }
        }
        return AllErrors;
        //Report.mergeJson(CppCheckJsonFile, MergedFile, i);
        //Report.toInitialReport("cppcheck", CppCheckJsonFile, RepSourceFile, i);
    }
    */
    
    public static JSONArray apply(String Tool, String SourceFilePath) {
        File SourceFile = new File(SourceFilePath);
        JSONArray ToolErrors = new JSONArray();
        
        //System.out.print("Static analysis using \u001B[32m" + Tool + "\u001B[0m...");
        System.out.print("Static analysis using \u001B[1m" + Tool + "\u001B[0m...");
        if (Tool.equals("infer")){
            ToolErrors = inferWRP.apply(SourceFile);
            //System.out.println("done");
            System.out.println("\u001B[32m done\u001B[0m");
        }
        else if (Tool.equals("cppcheck")){
            ToolErrors = cppcheckWRP.apply(SourceFile);
            //System.out.println("done");
            System.out.println("\u001B[32m done\u001B[0m");
        }
        else {
            //System.out.println("[todo]");
            System.out.println("\u001B[31m[todo]\u001B[0m");

        }
        return ToolErrors;
    }

}
