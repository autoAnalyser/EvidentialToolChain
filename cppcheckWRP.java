package etc.wrappers;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

import etc.utils.Utils;

public class cppcheckWRP {
    
    public static String filterErrorType(String Type) throws IOException {
        switch (Type) {
            case "nullPointer":  return "NULL DEREFERENCE";
            case "RESOURCE_LEAK":  return "RESOURCE LEAK";
            case "memleak":  return "MEMORY LEAK";
            case "unreadVariable":  return "Unread Variable";
            case "unusedFunction":  return "Unused Function";
            case "missingIncludeSystem":  return "Missing Include System";
           
	    case "missingInclude":  return "Missing Include";
 	   default:
                System.out.println("ERROR: Unknown case for CppCheck wrapper!"+Type);
                System.exit(0);
        }
        return "";
    }
    
    public static void apply(String FILEBASE, String FILEDIR, String TEMPDIR){
        Utils.runCMD0("cd " + FILEDIR + " && cppcheck --xml-version=1 --enable=all " + FILEBASE + " 2> cppcheck.xml");
        String xmlStrOUT = Utils.getFileContent(FILEDIR + "/cppcheck.xml");
        String JsonFile = TEMPDIR + "/cppcheck.json";
        
        String jsonStrOUT = Utils.xml2json(xmlStrOUT);
        JSONParser parser = new JSONParser();
        try {
            Object ObjOUT = parser.parse(jsonStrOUT);
            JSONObject jsonObjOUT = (JSONObject) ObjOUT;
            
            JSONObject JsonOut = (JSONObject) jsonObjOUT.get("results");
            JSONArray Errors = (JSONArray) JsonOut.get("error");
            
            JSONArray PartErrors = new JSONArray();
            Iterator<JSONObject> iterator = (Iterator<JSONObject>) Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type0 = (String) Error.get("id");
                String type = filterErrorType(type0);
                String file = (String) Error.get("file");
                Long line = (Long) Error.get("line");
                String qualifier = (String) Error.get("msg");
                JSONObject PartError = new JSONObject();
                
                PartError.put("type", type);
                PartError.put("file", file);
                PartError.put("line", line);
                PartError.put("procedure", " ");
                PartError.put("qualifier", qualifier);
                
                if (type.equals("NULL DEREFERENCE")){
                    Pattern pattern = Pattern.compile("Possible null pointer dereference: (?<var>...)");
                    Matcher m = pattern.matcher(qualifier);
                    if (m.matches()) {
                        String nullVar = m.group("var");
                        PartError.put("variable", nullVar);
                    }
                    else {
                        PartError.put("variable", "*****");
                    }
                }
                else{
                    PartError.put("variable", "*none");
                }
                PartErrors.add(PartError);
            }
            
            FileWriter fw = new FileWriter(JsonFile);
            fw.write(PartErrors.toJSONString());
            fw.flush();
            fw.close();
            
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
        
        Utils.runCMD0("cd " + FILEDIR + " && cppcheck --xml-version=1 --enable=all " + FILEBASE + " 2> cppcheck.xml");
        String xmlStrOUT = Utils.getFileContent(FILEDIR + "/cppcheck.xml");
        
        String jsonStrOUT = Utils.xml2json(xmlStrOUT);
        JSONParser parser = new JSONParser();
        
        //JSONObject CppCheckErrorsObj = new JSONObject();
        JSONArray PartErrors = new JSONArray();
        
        try {
            Object ObjOUT = parser.parse(jsonStrOUT);
            JSONObject jsonObjOUT = (JSONObject) ObjOUT;
            
            JSONObject JsonOut = (JSONObject) jsonObjOUT.get("results");
            JSONArray Errors = (JSONArray) JsonOut.get("error");
            
            Iterator<JSONObject> iterator = (Iterator<JSONObject>) Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type0 = (String) Error.get("id");
                String type = filterErrorType(type0);
                String file = (String) Error.get("file");
                Long line = (Long) Error.get("line");
                String qualifier = (String) Error.get("msg");
                JSONObject PartError = new JSONObject();
                
                PartError.put("type", type);
                PartError.put("file", file);
                PartError.put("line", line);
                PartError.put("procedure", " ");
                PartError.put("qualifier", qualifier);
                
                if (type.equals("NULL DEREFERENCE")){
                    Pattern pattern = Pattern.compile("Possible null pointer dereference: (?<var>...)");
                    Matcher m = pattern.matcher(qualifier);
                    if (m.matches()) {
                        String nullVar = m.group("var");
                        PartError.put("variable", nullVar);
                    }
                    else {
                        PartError.put("variable", "*****");
                    }
                }
                else{
                    PartError.put("variable", "*none");
                }
                PartErrors.add(PartError);
            }
            
            //CppCheckErrorsObj.put("tool", "cppcheck");
            //CppCheckErrorsObj.put("errors", PartErrors);
            //System.out.println("*** CppCheck *** " + CppCheckErrorsObj.toJSONString());
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return PartErrors;

    }

}
