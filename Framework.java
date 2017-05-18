package etc.framework;

import java.util.*;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.json.simple.parser.ParseException;
import java.util.Iterator;

import etc.utils.Utils;
import etc.utils.Report;
import etc.workflows.StaticAnalysis;
import etc.workflows.ModelChecking;

public class Framework {
    ArrayList<String> WorkFlows = new ArrayList<String>();;
    ArrayList<String> Tools = new ArrayList<String>();;
    String BaseDir;
    String ParamsFile;
    
    public Framework(String Dir) {
        BaseDir = Dir;
        ParamsFile = BaseDir + "/params.json";
        JSONObject JsonObj = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JsonObj = (JSONObject) parser.parse(new FileReader(ParamsFile));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        JSONArray AllWFs = (JSONArray) JsonObj.get("workflows");
        for (int i=0; i < AllWFs.size(); i++) {
            WorkFlows.add(AllWFs.get(i).toString());
        }
        JSONArray AllTools = (JSONArray) JsonObj.get("tools");
        for (int i=0; i < AllTools.size(); i++) {
            Tools.add(AllTools.get(i).toString());
        }
    }
    
    public String getBaseDir(){
        return BaseDir;
    }
    
    public ArrayList<String> getWorkFlows(){
        return WorkFlows;
    }
    
    public void help() {
        System.out.println("");
        System.out.println("* *          ETC 1.0 - Copyright (C) 2016 (Linux 64-bit version)          * *");
        //System.out.println("* *                    Tewodros A. Beyene, Harald Ruess                   * *");
        System.out.println("* *    fortiss — An-Institut Technische Universitaet Muenchen, Germany    * *");
        System.out.println("* *                             beyene@fortiss.org                        * *\n");
        System.out.println("Usage:                                                 Purpose:");
        System.out.println("engine [-h] [--help]                                     show this help menu");
        System.out.println("engine -src <file> -wf <Workflow> -conf <config.file>    runs the framework on a given source file by applying a predefined workflow using given configuration(s) \n");
        System.out.println("Analysis inputs:");
        System.out.println("-src           specifies source file");
        System.out.println("-wf            workflow to execute");
        System.out.println("-conf          file containing configuration(s) for the corresponding workflow\n");
        System.out.println("Backend options");
        System.out.println("-all-wfs       returns the list of all workflows available in the framework");
        System.out.println("-all-tools     returns the list of all available software analysis tools in the framework");
        System.out.println("-add-wfs       adds one or more workflows to the framework");
        System.out.println("-add-tools     adds one or more tools to the framework");
        System.out.println("-rm-wfs        removes one or more workflows from the framework");
        System.out.println("-rm-tools      removes one or more tools from the framework\n");
        
        //print_params();
    }

    public void print_params(int... Type) {
        if (Type.length == 1 && Type[0] == 0 || Type.length == 0){
            System.out.println("\n* * " + WorkFlows.size() + " available workflow(s) * * \n");
            for (int i = 0 ; i < WorkFlows.size() ; i++ ){
                System.out.println("workflow" + (i+1) + " : " + WorkFlows.get(i));
            }
            System.out.println("");
        }
        if (Type.length == 1 && Type[0] == 1 || Type.length == 0){
            System.out.println("\n* * " + Tools.size() + " available tool(s) * * \n");
            for (int i = 0 ; i < Tools.size() ; i++ ){
                System.out.println("tool" + (i+1) + " : " + Tools.get(i));
            }
            System.out.println("");
        }
    }

    public void add_params(String Components, int ID) {
        String[] Params = Components.split(" ");
        if (ID == 0) {
            for (int i = 0; i< Params.length; i++) WorkFlows.add(Params[i]);
        }
        else {
            for (int i = 0; i< Params.length; i++) Tools.add(Params[i]);
        }
    }

    public void remove_params(String StrParams, int ID) {
        String[] Params = StrParams.split(" ");
        String Removed = "";
        String Type = "workflow";
        if (ID == 0) {
            for (int i = 0; i < Params.length; i++) {
                int ParamI = Integer.parseInt(Params[i]);
                Removed = Removed + WorkFlows.get(ParamI - (i + 1)) + " ";
                WorkFlows.remove(ParamI - (i + 1));
            }
        }
        else {
            for (int i = 0; i< Params.length; i++) {
                int ParamI = Integer.parseInt(Params[i]);
                Removed = Removed + Tools.get(ParamI - (i + 1)) + " ";
                Tools.remove(ParamI - (i + 1));
            }
            Type = "tool";
        }
        System.out.println("\nremoved " + Type + "(s) : " + Removed);
        
    }

    public void save() {
        JSONParser parser = new JSONParser();
        JSONObject JsonObj = new JSONObject();
        try {
            JSONArray WorkFlowsObj = new JSONArray();
            JSONArray ToolsObj = new JSONArray();
            for (int i = 0; i < WorkFlows.size(); i++) WorkFlowsObj.add(WorkFlows.get(i));
            for (int i = 0; i < Tools.size(); i++) ToolsObj.add(Tools.get(i));
            JSONObject NewObj = new JSONObject();
            
            NewObj.put("workflows", WorkFlowsObj);
            NewObj.put("tools", ToolsObj);
            
            FileWriter fw = new FileWriter(ParamsFile);
            fw.write(NewObj.toJSONString());
            fw.flush();
            fw.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
