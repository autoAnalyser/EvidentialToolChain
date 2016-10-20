package etc.wrappers;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

import etc.utils.Utils;

public class cbmcWRP {
    
    public static String[] getProcedures(String File, String TEMPDIR){
        String ProcList0 = Utils.runCMD1("echo $(cd " + TEMPDIR + " && gcc -c " + File + " -o temp.o && nm temp.o | awk '$2 == \"T\" {print $3}')");
        System.out.println("ProcList0 : " + ProcList0);

        String[] ProcList = (ProcList0.replace("\n","")).split(" ");
        for (int i=0; i<ProcList.length; i++){
            System.out.println("ProcList [" + i + "] : " + ProcList[i]);
            ProcList[i] = ProcList[i].substring(1);
        }
        return ProcList;
    }
    
    public static boolean cbmcCheck(String SourceFile, String TEMPDIR, String assertion, Long line, String annotID, String procedure){
        String annotPath = TEMPDIR + "/annotProg" + annotID + ".c";
        Utils.annotateFile(SourceFile, assertion, line, annotPath);
        String cbmcOUT = Utils.runCMD1("echo $(timeout 1 cbmc --function " + procedure + " " + annotPath + ") | grep -q 'VERIFICATION FAILED' && echo $?");
        cbmcOUT = (cbmcOUT.replace("\n","")).replace(" ","");
        
        Utils.runCMD0("rm -rf " + annotPath);
        
        return cbmcOUT.equals("0");
    }
    
    public static void cbmcRefine(String SourceFile, String TEMPDIR, String assertion, Long line, int i, JSONObject Error){
        boolean realError = false;
        String[] ProcList = getProcedures(SourceFile, TEMPDIR);
        for (int j = 0; j < ProcList.length; j++){
            String annotID = "" + (i+1) + (j+1);
            if (cbmcCheck(SourceFile, TEMPDIR, assertion, line, annotID, ProcList[j])){
                realError = true;
                Error.put("severity", "Verified as real error [multi-proc]");
                Error.put("status", 0);
                break;
            }
        }
        if (realError == false){
            Error.put("severity", "Verified as false alarm [multi-proc]");
            Error.put("status", 1);
        }
    }
    
    public static void cbmcRefine(String SourceFile, String TEMPDIR, String assertion, Long line, int i, String procedure, JSONObject Error){
        String annotID = "" + (i+1);
        if (cbmcCheck(SourceFile, TEMPDIR, assertion, line, annotID, procedure)){
            Error.put("severity", "Verified as real error [multi-proc]");
            Error.put("status", 0);
        }
        else {
            Error.put("severity", "Verified as false alarm");
            Error.put("status", 1);
        }
    }

    public static void apply(String SourceFile, String TEMPDIR, String ErrorsFile, String RefErrorsFile){ // (SourceFile, TEMPDIR, ErrorsFile, RefErrorsFile)
        JSONParser parser = new JSONParser();
        try {
            Object ErrorsObj = parser.parse(new FileReader(ErrorsFile));
            JSONArray Errors = (JSONArray) ErrorsObj;
            JSONArray RefErrors = new JSONArray();
            Iterator<JSONObject> iterator = Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type = (String) Error.get("type");
                Long line = (Long) Error.get("line");
                String procedure = (String) Error.get("procedure");
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
            FileWriter fw = new FileWriter(RefErrorsFile);
            fw.write(RefErrors.toJSONString());
            fw.flush();
            fw.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray apply(JSONArray Errors, String SourceFile, String TEMPDIR){
        //JSONArray Errors = (JSONArray) ErrorsObj;
        JSONArray RefErrors = new JSONArray();
        Iterator<JSONObject> iterator = Errors.iterator();
        for (int i = 0 ; iterator.hasNext() ; ++i ){
            JSONObject Error = iterator.next();
            String type = (String) Error.get("type");
            Long line = (Long) Error.get("line");
            String procedure = (String) Error.get("procedure");
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
