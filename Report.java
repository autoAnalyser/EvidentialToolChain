package etc.utils;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import etc.utils.Utils;

public class Report {
    
    public static void toInitialReport(String Tool, String ToolFile, String OutFile, int first) {
        JSONParser parser = new JSONParser();
        try {
            Object JsonObj = parser.parse(new FileReader(ToolFile));
            FileWriter fw;
            if (first == 0) {
                fw = new FileWriter(OutFile);
            }
            else {
                fw = new FileWriter(OutFile, true);
            }
            
            String RepTop, RepBottom;
            if (first == 0) {
                RepTop = "\\section{Initial report from each static analysis tools} \n \\subsection{" + Tool + " report} \n";
                //RepBottom = "\\end{tabular} \n \\caption{" + Tool + " initial report } \n \\end{table*} \n \\newpage \n";
                RepBottom = "\\caption{" + Tool + " initial report } \n \\end{longtable} \n \\newpage \n";
            } else { // Inital report from the second or more tools
                RepTop = "\\subsection{" + Tool + " initial report} \n % \n";
                //RepBottom = "\\end{tabular} \n \\caption{" + Tool + " initial report } \n \\end{table*} \n \\newpage \n";
                RepBottom = "\\caption{" + Tool + " initial report } \n \\end{longtable} \n \\newpage \n";
            }
            fw.write(RepTop);
            //String TableTop = "\\begin{table*}[h!] \n \\centering \n \\begin{tabular}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
            String TableTop = "\\begin{longtable}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
            fw.write(TableTop);
            
            JSONArray Errors = (JSONArray) JsonObj;
            Iterator<JSONObject> iterator = Errors.iterator();
            
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                
                String type = (String) Error.get("type");
                String file = (String) Error.get("file");
                if (file != null) {
                    file =  file.replace("_", "\\_");
                }
                Long line = (Long) Error.get("line");
                String procedure = (String) Error.get("procedure");
                if (procedure != null){
                    procedure =  procedure.replace("_", "\\_");
                }
                String qualifier0 = (String) Error.get("qualifier");
                String  qualifier =  qualifier0.replace("_", "\\_");
                
                String ErrorEntry1 = "\\hline \n \\multirow{2}{*}{E"+ (i+1) + "} & " + type + " & $" + file + "$ & " + line  + " & $" + procedure + "$ \\\\ \n";
                String ErrorEntry2 = "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \\hline \n ";		
                
                
                fw.write(ErrorEntry1); 
                fw.write(ErrorEntry2); 
            }
            fw.write(RepBottom); 
            
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
    
    public static void toMergedReport(String JsonFile, String Report, int first) {
        JSONParser parser = new JSONParser();
        try {
            Object JsonObj = parser.parse(new FileReader(JsonFile));
            String RepTop, RepBottom;
            if (first == 0) {
                RepTop = "\\section{Refined report} \n % \n";
                //RepBottom = "\\end{tabular} \n \\caption{Refined report } \n \\end{table*} \n \\newpage \n";
                RepBottom = "\\caption{Refined report } \n \\end{longtable} \n \\newpage \n";
            } else {
                RepTop = "\\section{Merged report from all static analysis tools} \n % \n";
                //RepBottom = "\\end{tabular} \n \\caption{Merged report } \n \\end{table*} \n \\newpage \n";
                RepBottom = "\\caption{Merged report } \n \\end{longtable} \n \\newpage \n";
            }
            FileWriter fw = new FileWriter(Report, true);
            fw.write(RepTop);
            //String TableTop = "\\begin{table*}[h!] \n \\centering \n \\begin{tabular}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
            String TableTop = "\\begin{longtable}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
            fw.write(TableTop);
            
            JSONArray Errors = (JSONArray) JsonObj;
            Iterator<JSONObject> iterator = Errors.iterator();
            for (int i = 0 ; iterator.hasNext() ; ++i ){
                JSONObject Error = iterator.next();
                String type = (String) Error.get("type");
                String file = (String) Error.get("file");
                if (file != null){
                    file =  file.replace("_", "\\_");
                }
                Long line = (Long) Error.get("line");
                
                String procedure = (String) Error.get("procedure");
                if (procedure != null) {
                    procedure =  procedure.replace("_", "\\_");
                }
                String qualifier0 = (String) Error.get("qualifier");
                String  qualifier =  qualifier0.replace("_", "\\_");
                String ErrorEntry1 = "\\hline \n \\multirow{2}{*}{E"+ (i+1) + "} & " + type + " & $" + file + "$ & " + line  + " & $" + procedure + "$ \\\\ \n";
                String ErrorEntry2;
                if (first == 0) {
                    String severity = (String) Error.get("severity");
                    ErrorEntry2 = "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \n \\cline{2-5} \n & \\mcEE{" + severity + "} \\\\ \\hline \n";
                }
                else {
                    ErrorEntry2 = "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \\hline \n ";
                }
                fw.write(ErrorEntry1);
                fw.write(ErrorEntry2);
            }
            fw.write(RepBottom);
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
    
    public static void mergeJson(String ToBeMerge, String OutFile, int first){
        JSONParser parser = new JSONParser();
        try {
            Object JsonObj1 = parser.parse(new FileReader(ToBeMerge));
            JSONArray JsonArr1 = (JSONArray) JsonObj1;
            JSONArray JsonArr2;
            if (first == 0) {
                JsonArr2 = JsonArr1;
            }
            else {
                Object JsonObj2 = parser.parse(new FileReader(OutFile));
                JsonArr2 = (JSONArray) JsonObj2;
                Iterator<JSONObject> iterator = JsonArr1.iterator();
                for (int i = 0 ; iterator.hasNext() ; ++i ){
                    JSONObject Obj = iterator.next();
                    JsonArr2.add(Obj);
                }
            }
            FileWriter fw = new FileWriter(OutFile);
            fw.write(JsonArr2.toJSONString());
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

    public static void compileReport(String TEMPDIR, int confID){
        System.out.print("Generating final reports...");
        Utils.runCMD0("cd " + TEMPDIR + "/Review/ && make clean && make && cp main.pdf " + TEMPDIR + "/review"+ confID + ".pdf && make clean");
        System.out.println("done");
        System.out.println("review written to TEMP/review"+ confID + ".pdf");
    }
    
    public static void mergeErrors(JSONArray JsonArr1, JSONArray JsonArr2){
        Iterator<JSONObject> iterator = JsonArr2.iterator();
        while (iterator.hasNext()){
            JsonArr1.add(iterator.next());
        }
    }
    
    public static String addToolsToReport(JSONArray ToolsErrors) {
        
        Iterator<JSONObject> iterator0 = ToolsErrors.iterator();
        String ToTexReport = "\\section{Initial report from static analysis tools} \n";
        
        for (int i = 0 ; iterator0.hasNext() ; i++ ){
            JSONObject ToolErrorsObj = iterator0.next();
            String Tool = (String) ToolErrorsObj.get("tool");
            JSONArray Errors = (JSONArray) ToolErrorsObj.get("errors");
            ToTexReport = ToTexReport + "\\subsection{" + Tool + " report} \n";
            ToTexReport = ToTexReport + "\\begin{longtable}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
            
            Iterator<JSONObject> iterator1 = Errors.iterator();
            for (int j = 0 ; iterator1.hasNext() ; j++ ){
                JSONObject Error = iterator1.next();
                
                String type = (String) Error.get("type");
                String file = (String) Error.get("file");
                if (file != null) {
                    file =  file.replace("_", "\\_");
                }
                Long line = (Long) Error.get("line");
                String procedure = (String) Error.get("procedure");
                if (procedure != null){
                    procedure =  procedure.replace("_", "\\_");
                }
                String qualifier0 = (String) Error.get("qualifier");
                String  qualifier =  qualifier0.replace("_", "\\_");
                
                ToTexReport = ToTexReport + "\\hline \n \\multirow{2}{*}{E"+ (j+1) + "} & " + type + " & $" + file + "$ & " + line  + " & $" + procedure + "$ \\\\ \n";
                ToTexReport = ToTexReport + "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \\hline \n ";
            }
            ToTexReport = ToTexReport + "\\caption{Code analysis report from " + Tool + "} \n \\end{longtable} \n \\newpage \n";
        }
        return ToTexReport;
    }

    public static String addToolsToReport(String Header, String Caption, JSONArray Errors) {
        
        String ToTexReport = "\\section{" + Header + "} \n";
        ToTexReport = ToTexReport + "\\begin{longtable}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
        Iterator<JSONObject> iterator = Errors.iterator();
        for (int j = 0 ; iterator.hasNext() ; j++ ){
            JSONObject Error = iterator.next();
                
            String type = (String) Error.get("type");
            String file = (String) Error.get("file");
            if (file != null) {
                file =  file.replace("_", "\\_");
            }
            Long line = (Long) Error.get("line");
            String procedure = (String) Error.get("procedure");
            if (procedure != null){
                procedure =  procedure.replace("_", "\\_");
            }
            String qualifier0 = (String) Error.get("qualifier");
            String  qualifier =  qualifier0.replace("_", "\\_");
            
            ToTexReport = ToTexReport + "\\hline \n \\multirow{2}{*}{E"+ (j+1) + "} & " + type + " & $" + file + "$ & " + line  + " & $" + procedure + "$ \\\\ \n";
            ToTexReport = ToTexReport + "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \\hline \n ";
        }
        ToTexReport = ToTexReport + "\\caption{" + Caption + "} \n \\end{longtable} \n \\newpage \n";
        return ToTexReport;
    }
    
    public static String addToolsToReport(JSONArray Errors, boolean Refined) {
        if (Errors.toJSONString().equals("[]"))
            return "";
        String RepTop, RepBottom;
        if (Refined == true) {
            RepTop = "\\section{Refined report} \n % \n";
            RepBottom = "\\caption{Refined report } \n \\end{longtable} \n \\newpage \n";
        } else {
            RepTop = "\\section{Merged report} \n % \n";
            RepBottom = "\\caption{Merged report } \n \\end{longtable} \n \\newpage \n";
        }
        String TableTop = "\\begin{longtable}{|p{1cm}|p{5cm}|p{2cm}|p{1cm}|p{4cm}|} \n \\hline \n Error No. & Error Type & File & Line & Procedure \\\\ \n ";
        String Report = RepTop + TableTop;
        Iterator<JSONObject> iterator = Errors.iterator();
        for (int i = 0 ; iterator.hasNext() ; ++i ){
            JSONObject Error = iterator.next();
            String type = (String) Error.get("type");
            String file = (String) Error.get("file");
            if (file != null){
                file =  file.replace("_", "\\_");
            }
            Long line = (Long) Error.get("line");
            
            String procedure = (String) Error.get("procedure");
            if (procedure != null) {
                procedure =  procedure.replace("_", "\\_");
            }
            String qualifier0 = (String) Error.get("qualifier");
            String  qualifier =  qualifier0.replace("_", "\\_");
            String ErrorEntry1 = "\\hline \n \\multirow{2}{*}{E"+ (i+1) + "} & " + type + " & $" + file + "$ & " + line  + " & $" + procedure + "$ \\\\ \n";
            String ErrorEntry2;
            if (Refined == true) {
                String severity = (String) Error.get("severity");
                ErrorEntry2 = "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \n \\cline{2-5} \n & \\mcEE{" + severity + "} \\\\ \\hline \n";
            }
            else {
                ErrorEntry2 = "\\cline{2-5} \n & \\mcSS{" + qualifier + "} \\\\ \\hline \n ";
            }
            Report = Report + ErrorEntry1 + ErrorEntry2;
        }
        Report = Report + RepBottom;
        return Report;
    }
    
    public static void writeToFile(JSONArray Errors, String File) {
        try {
            FileWriter fw = new FileWriter(File);
            fw.write(Errors.toJSONString());
            fw.flush();
            fw.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
