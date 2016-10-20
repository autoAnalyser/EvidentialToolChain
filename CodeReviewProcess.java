package etc.workflows;

import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import java.util.Scanner;
import java.util.Iterator;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import etc.utils.Utils;

import etc.workflows.StaticAnalysis;
import etc.workflows.ModelChecking;
import etc.utils.Report;

public class CodeReviewProcess {
    String Config;
    int configCount;
    String SourceFile;
    //String StaticAnalysisTools[];
    String StaticAnalysisTools;
    String ModelCheckingTool;
    String WPGenerationTool;
    String BaseDirPath;
    String TempDirPath;
    String ConfigDirPath;
    JSONArray ToolsErrors;
    JSONArray MergedErrors;
    JSONArray RefinedErrors;
    
    public CodeReviewProcess(String SourceFile, String InConfig, int configCount, String TempDirPath) {
        this.BaseDirPath = System.getProperty("user.dir");
        this.SourceFile = SourceFile;
        this.Config = InConfig.replaceAll("\\s+$", "");
        this.configCount = configCount;
        instantiate();
        this.TempDirPath = TempDirPath;
        this.ConfigDirPath = TempDirPath + "/Config" + configCount;
        this.ToolsErrors = new JSONArray();
        this.MergedErrors = new JSONArray();
        this.RefinedErrors = new JSONArray();
        set();
    }

    private void instantiate() {
        String ConfigParts[] = Config.split("\"");
        System.out.print("Instantiating tools...");
        
        int allSet = 0;
        if (ConfigParts.length == 4) {
            if (ConfigParts[0].replaceAll("\\s+","").equals("-sa")){
                //StaticAnalysisTools = ConfigParts[1].split(" ");
                StaticAnalysisTools = ConfigParts[1];
                if (StaticAnalysisTools.equals("")){
                    System.out.println("ERROR: No static analysis tool given!");
                    System.exit(0);
                }
                else {
                    allSet++;
                }
            }
            if (ConfigParts[2].replaceAll("\\s+","").equals("-v")){
                ModelCheckingTool = ConfigParts[3];
                if (ModelCheckingTool.split(" ").length == 1) {
                    allSet++;
                }
                else {
                    System.out.println("ERROR: No or more than one model checking tool(s) given!");
                    System.exit(0);
                }
            }
            WPGenerationTool = "";
            if (allSet < 2){
                System.out.println("ERROR. Missing argument(s) in configuration!");
                System.exit(0);
            }
        }
        else if (ConfigParts.length == 6) {
            if (ConfigParts[0].replaceAll("\\s+","").equals("-sa")){
                //StaticAnalysisTools = ConfigParts[1].split(" ");
                StaticAnalysisTools = ConfigParts[1];
                if (StaticAnalysisTools.equals("")){
                    System.out.println("ERROR: No static analysis tool given!");
                    System.exit(0);
                }
                else {
                    allSet++;
                }
            }
            if (ConfigParts[2].replaceAll("\\s+","").equals("-v")){
                ModelCheckingTool = ConfigParts[3];
                if (ModelCheckingTool.split(" ").length == 1) {
                    allSet++;
                }
                else {
                    System.out.println("ERROR: No or more than one model checking tool(s) given!");
                    System.exit(0);
                }
            }
            if (ConfigParts[4].replaceAll("\\s+","").equals("-wp")){
                WPGenerationTool = ConfigParts[5];
                if (WPGenerationTool.split(" ").length == 1) {
                    allSet++;
                }
                else {
                    System.out.println("ERROR: No or more than one WP generation tool(s) given!");
                    System.exit(0);
                }
            }
            if (allSet < 3){
                System.out.println("ERROR. Missing argument(s) in configuration!");
                System.exit(0);
            }
        }
        else {
            System.out.println("ERROR. Unrecognized configuration!");
            System.exit(0);
        }
        
        System.out.println("done.");
    }
    
    public void print(){
        //System.out.println("Config: " + Config);
        System.out.println("SourceFile: " + SourceFile);
        //for (int i = 0; i < StaticAnalysisTools.length; i++) System.out.println("StaticAnalysisTools[" + i + "]: " + StaticAnalysisTools[i]);
        System.out.println("StaticAnalysisTools: " + StaticAnalysisTools);
        System.out.println("ModelCheckingTool: " + ModelCheckingTool);
        System.out.println("WPGenerationTool: " + WPGenerationTool);
        //System.out.println("BaseDirPath: " + BaseDirPath);
        System.out.println("ToolsErrors: " + ToolsErrors.toJSONString());
        System.out.println("MergedErrors: " + MergedErrors.toJSONString());
        System.out.println("RefinedErrors: " + RefinedErrors.toJSONString());
        
    }
    
    public void integratedAnalysis() {
        String AnalysisTools[] = StaticAnalysisTools.split(" ");
        for (int i = 0 ; i < AnalysisTools.length ; i++ ){
            JSONArray ToolErrors = new JSONArray();
            ToolErrors = StaticAnalysis.apply(AnalysisTools[i], SourceFile);
            if (!ToolErrors.toJSONString().equals("[]")){
                Report.mergeErrors(MergedErrors, ToolErrors);
                JSONObject ToolErrorsObj = new JSONObject();
                ToolErrorsObj.put("tool", AnalysisTools[i]);
                ToolErrorsObj.put("errors", ToolErrors);
                ToolsErrors.add(ToolErrorsObj);
            }

        }
    }
    
    public void modelCheck(){
        ModelChecking m1 = new ModelChecking(ModelCheckingTool, MergedErrors, SourceFile, ConfigDirPath);
        m1.apply();
        RefinedErrors = m1.getRefErrors();
    }
    
    public void getReport() {
        String ReportTemplateDirPath = BaseDirPath + "/ReportTemplate";
        String ReportDirPath = ConfigDirPath + "/Report";
        File ReportTemplateDir = new File(ReportTemplateDirPath);
        File ReportDir = new File(ReportDirPath);
    
        try{
            FileUtils.copyDirectory(ReportTemplateDir, ReportDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String AllConfigTools = "\\underline{\\bf Configuration " + configCount + " detail: } \n \\vspace{5mm}";
        AllConfigTools = AllConfigTools + "\n\n{\\bf Static Analysis Tool(s) : } " + StaticAnalysisTools + "\n\n{\\bf Model Checking Tool : } " + ModelCheckingTool;
        if (!WPGenerationTool.equals(""))
            AllConfigTools = AllConfigTools + "\n\n{\\bf WP Generation Tool : } " + WPGenerationTool + "\n \n";
        
        String ToTexReport = AllConfigTools + Report.addToolsToReport(ToolsErrors) + Report.addToolsToReport(MergedErrors, false) + Report.addToolsToReport(RefinedErrors, true);
        //System.out.println(ToTexReport);
        
        String OutFile = ReportDirPath + "/reps.tex";
        try{
            FileWriter fw = new FileWriter(OutFile);
            fw.write(ToTexReport);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("Generating PDF reports...");
        Utils.runCMD0("cd " + ReportDirPath + " && make clean && make && cp main.pdf " + TempDirPath + "/report"+ configCount + ".pdf && make clean");
        System.out.println("done");
        //System.out.print("\u001B[1mDONE:\u001B[0m");
        //System.out.println("review written to " + TempDirPath + "/report"+ configCount + ".pdf");
        System.out.println("\u001B[1m\u001B[34m==> review written to " + TempDirPath + "/report"+ configCount + ".pdf \u001B[0m");
        Utils.runCMD0("cd " + TempDirPath + " && rm -rf " + ReportDirPath);
        
        
    }
    
    private void set() {
        //TempDirPath = System.getProperty("user.dir") + "/TEMP";
        File ConfigDir = new File(ConfigDirPath);
        if (ConfigDir.exists()) {
            //Utils.delete(TempDir);
            try{
                FileUtils.deleteDirectory(ConfigDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("CONF Directory deleted");
        }
        if (!ConfigDir.mkdir()) {
            System.out.println("ERROR: Can not creat CONF directory: " + ConfigDirPath);
            System.exit(0);
        }
        
        //System.out.println("CONF directory created");
        
    }
    
    private void clean() {
        File ConfigDir = new File(ConfigDirPath);
        try{
            FileUtils.deleteDirectory(ConfigDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("CONF Directory cleaned");
    }
    
    public void run(){
        integratedAnalysis();
        //String ToolsErrorsFile = TempDirPath + "/ToolsErrors" + configCount;
        String ToolsErrorsFile = ConfigDirPath + "/ToolsErrors" + configCount;
        Report.writeToFile(ToolsErrors, ToolsErrorsFile);
        //String MergedErrorsFile = TempDirPath + "/MergedErrors" + configCount;
        String MergedErrorsFile = ConfigDirPath + "/MergedErrors" + configCount;
        Report.writeToFile(MergedErrors, MergedErrorsFile);
        modelCheck();
        //String RefinedErrorsFile = TempDirPath + "/RefinedErrors" + configCount;
        String RefinedErrorsFile = ConfigDirPath + "/RefinedErrors" + configCount;
        Report.writeToFile(MergedErrors, RefinedErrorsFile);
        getReport();
        //clean();
    }

}
