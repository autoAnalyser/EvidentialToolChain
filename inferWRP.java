package etc.wrappers;

import java.io.IOException;
import java.lang.InterruptedException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;

import etc.utils.Utils;

public class inferWRP {
    
    public static String filterErrorType(String Type) {
        switch (Type) {
            case "NULL_DEREFERENCE":
                return "NULL DEREFERENCE";
            case "RESOURCE_LEAK":
                return "RESOURCE LEAK";
            case "MEMORY_LEAK":
                return "MEMORY LEAK";
            default:
                System.out.println("ERROR: Unknown case for Infer wrapper!");
                System.exit(0);
        }
        return "";
    }
    
    public static void apply(String InferOrigFile, String OutFile){
        JSONParser parser = new JSONParser();
        try {
            Object JsonObj = parser.parse(new FileReader(InferOrigFile));
            JSONArray Errors = (JSONArray) JsonObj;
            JSONArray PartErrors = new JSONArray();
            Iterator<JSONObject> iterator = Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type0 = (String) Error.get("bug_type");
                String type = filterErrorType(type0);
                String file = (String) Error.get("file");
                Long line = (Long) Error.get("line");
                String procedure = (String) Error.get("procedure");
                String qualifier = (String) Error.get("qualifier");
                JSONObject PartError = new JSONObject();
                
                PartError.put("type", type);
                PartError.put("file", file);
                PartError.put("line", line);
                PartError.put("procedure", procedure);
                PartError.put("qualifier", qualifier);
                
                if (type.equals("NULL DEREFERENCE")){
                    JSONArray qualifier_tags = (JSONArray) Error.get("qualifier_tags");
                    JSONObject qualObj = (JSONObject) qualifier_tags.get(3);
                    String nullVar = (String) qualObj.get("value");
                    PartError.put("variable", nullVar);
                }
                else{
                    PartError.put("variable", "*none");
                }
                PartErrors.add(PartError);	
            }
            FileWriter file = new FileWriter(OutFile);
            file.write(PartErrors.toJSONString());
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }	
    }
    
    public static JSONArray apply(File SourceFile){
        
        String FILEBASE = SourceFile.getName();
        String FILEDIR = SourceFile.getParent();
        
        Utils.runCMD0("cd " + FILEDIR + " && make clean && infer -- make &>/dev/null");
        JSONParser parser = new JSONParser();
        JSONArray PartErrors = new JSONArray();
        //JSONObject InferErrorsObj = new JSONObject();

        try {
            Object JsonObj = parser.parse(new FileReader(FILEDIR + "/infer-out/report.json"));
            JSONArray Errors = (JSONArray) JsonObj;
            Iterator<JSONObject> iterator = Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type0 = (String) Error.get("bug_type");
                String type = filterErrorType(type0);
                String file = (String) Error.get("file");
                Long line = (Long) Error.get("line");
                String procedure = (String) Error.get("procedure");
                String qualifier = (String) Error.get("qualifier");
                JSONObject PartError = new JSONObject();
                
                PartError.put("type", type);
                PartError.put("file", file);
                PartError.put("line", line);
                PartError.put("procedure", procedure);
                PartError.put("qualifier", qualifier);
                
                if (type.equals("NULL DEREFERENCE")){
                    JSONArray qualifier_tags = (JSONArray) Error.get("qualifier_tags");
                    JSONObject qualObj = (JSONObject) qualifier_tags.get(3);
                    String nullVar = (String) qualObj.get("value");
                    PartError.put("variable", nullVar);
                }
                else{
                    PartError.put("variable", "*none");
                }
                PartErrors.add(PartError);
            }
            //InferErrorsObj.put("tool", "infer");
            //InferErrorsObj.put("errors", PartErrors);
            //System.out.println("*** INFER *** " + InferErrorsObj.toJSONString());
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        Utils.runCMD0("cd " + FILEDIR + " && make clean");
        return PartErrors;
    }

}
