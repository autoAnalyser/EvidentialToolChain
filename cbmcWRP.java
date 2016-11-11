package etc.wrappers;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

import org.apache.commons.io.FileUtils;

import etc.utils.Utils;

public class cbmcWRP {
    
    public static String[] getProcedures(String File, String TEMPDIR){
        
        File SourceFileObj = new File(File);
        String SourceFileBase = SourceFileObj.getName();
        String SourceDirPath = SourceFileObj.getParent();
        
        //String ProcList0 = Utils.runCMD1("echo $(cd " + TEMPDIR + " && gcc -c " + File + " -o temp.o && nm temp.o | awk '$2 == \"T\" {print $3}')");
        String ProcList0 = Utils.runCMD1("echo $(cd " + SourceDirPath + " && gcc -c " + SourceFileBase + " -o temp.o && nm temp.o | awk '$2 == \"T\" {print $3}')");
        //System.out.println("ProcList0 : " + ProcList0);

        //System.out.println("ProcList0 : 02");

        String[] ProcList = (ProcList0.replace("\n","")).split(" ");
        for (int i=0; i<ProcList.length; i++){
            //System.out.println("ProcList [" + i + "] : " + ProcList[i].substring(1));
            ProcList[i] = ProcList[i].substring(1);
        }
        return ProcList;
    }
    
    public static boolean cbmcCheck(String SourceFile, String TEMPDIR, String assertion, Long line, String annotID, String procedure){
        File SourceFileObj = new File(SourceFile);
        String SourceFileBase = SourceFileObj.getName();
        String SourceDirPath = SourceFileObj.getParent();
        File SourceDir = new File(SourceDirPath);
        
        String AnnotDirPath = TEMPDIR + "/AnnotDir" + annotID;
        File AnnotDir = new File(AnnotDirPath);
        
        try{
            FileUtils.copyDirectory(SourceDir, AnnotDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Utils.runCMD0("cd " + AnnotDirPath + " && make clean && rm Makefile "+ SourceFileBase);
        
        String annotProgPath = AnnotDirPath + "/" + SourceFileBase;
        Utils.annotateFile(SourceFile, assertion, line, annotProgPath);
        String cbmcOUT = Utils.runCMD1("echo $(timeout 1 cbmc --function " + procedure + " " + annotProgPath + ") | grep -q 'VERIFICATION FAILED' && echo $?");
        cbmcOUT = (cbmcOUT.replace("\n","")).replace(" ","");
        
        return cbmcOUT.equals("0");
    }
    
    public static void cbmcRefine(String SourceFile, String TEMPDIR, String assertion, Long line, int i, JSONObject Error){
        String[] ProcList = getProcedures(SourceFile, TEMPDIR);
        for (int j = 0; j < ProcList.length; j++){
            String annotID = "" + (i+1) + (j+1);
            if (cbmcCheck(SourceFile, TEMPDIR, assertion, line, annotID, ProcList[j])){
                Error.put("severity", "Verified as real error [multi-proc]");
                Error.put("status", 0);
                break; //TODO: More precise analysis
            }
        }
        Error.put("severity", "Verified as false alarm [multi-proc]");
        Error.put("status", 1);
    }
    
    public static void cbmcRefine(String SourceFile, String TEMPDIR, String assertion, Long line, int i, String procedure, JSONObject Error){
        String annotID = "" + (i+1);
        if (cbmcCheck(SourceFile, TEMPDIR, assertion, line, annotID, procedure)){
            Error.put("severity", "Verified as real error [def-proc]");
            Error.put("status", 0);
        }
        else {
            Error.put("severity", "Verified as false alarm");
            Error.put("status", 1);
        }
    }
    
    public static JSONArray apply(JSONArray Errors, String SourceFile, String TEMPDIR){
        JSONArray RefErrors = new JSONArray();
        Iterator<JSONObject> iterator = Errors.iterator();
        for (int i = 0 ; iterator.hasNext() ; ++i ){
            JSONObject Error = iterator.next();
            String type = (String) Error.get("type");
            Long line = (Long) Error.get("line");
            String procedure = (String) Error.get("procedure");
            
            //System.out.println("Error : " + Error.toJSONString());
            //System.out.println("procedure : " + procedure);

            if (type.equals("NULL DEREFERENCE")){
                String nullVar = (String) Error.get("variable");
                String assertion = "assert(" + nullVar + "!= NULL);";
                if (procedure.equals(" ")) {
                    cbmcRefine(SourceFile, TEMPDIR, assertion, line, i, Error);
                }
                else {
                    cbmcRefine(SourceFile, TEMPDIR, assertion, line, i, procedure, Error);
                }
            }
            else {
                Error.put("severity", "[TODO] for " + type);
                Error.put("status", 2);
            }
            RefErrors.add(Error);
        }
        return RefErrors;
    }
    
}
