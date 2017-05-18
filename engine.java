import java.util.Scanner;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.json.simple.parser.ParseException;

import etc.utils.Utils;
import etc.framework.Framework;
import etc.workflows.CodeReviewProcess;


// java engine -src test/src/example.c -wf "CodeReview" -conf test/configFile.txt
public class engine {

    Framework FW;
    String WorkFlow;
    String SourceFile;
    File ConfFile;
    String TempDirPath;
    
    public engine(String args[]){
        FW = new Framework(System.getProperty("user.dir"));
        instantiate(args);
        set();
    }
    
    private void instantiate(String args[]) {
        if (args.length == 1) {
            if (args[0].equals("-help") || args[0].equals("-h")){
                FW.help();
            }
            else if (args[0].equals("-all-wfs")){
                FW.print_params(0);
            }
            else if (args[0].equals("-all-tools")){
                FW.print_params(1);
            }
            else {
                System.out.println("ERROR. Unknown option: " + args[0]);
            }
            System.exit(0);
        }
        else if (args.length == 2) {
            if (args[0].equals("-add-wfs")){
                FW.add_params(args[1], 0);
                FW.save();
            }
            else if (args[0].equals("-add-tools")){
                FW.add_params(args[1], 1);
                FW.save();
            }
            else if (args[0].equals("-rm-wfs")){
                FW.remove_params(args[1], 0);
                FW.save();
            }
            else if (args[0].equals("-rm-tools")){
                FW.remove_params(args[1], 1);
                FW.save();
            }
            else {
                System.out.println("ERROR. Unknown option pairs: " + args[0] + " " + args[1]);
            }
            System.exit(0);
        }
        else if (args.length == 6) {
            int allSet = 0;
            if (args[0].equals("-src")){
                this.SourceFile = args[1];
                File SourceFileObj = new File(args[1]);
                //this.SourceFile = new File(args[1]);
                if (SourceFileObj.exists())
                    allSet++;
                else {
                    System.out.println("ERROR: Problem with the source file!");
                    System.exit(0);
                }
            }
            if (args[2].equals("-wf")){
                this.WorkFlow = args[3];
                allSet++;
            }
            if (args[4].equals("-conf")) {
                this.ConfFile = new File(args[5]);
                if (ConfFile.exists())
                    allSet++;
                else {
                    System.out.println("ERROR: Problem with the configuration file!");
                    System.exit(0);
                }
            }
            if (allSet < 3){
                System.out.println("ERROR. Missing Workflow argument(s)");
                System.exit(0);
            }
        }
        else {
            System.out.println("ERROR. Incorrect Workflow argument(s) structure");
            System.exit(0);
        }
    }
    
    public void run(Scanner scan){
        int configCount =1;
        if (WorkFlow.equals("CodeReview")) {
            while(scan.hasNextLine()){
                System.out.println("============================== ");
                System.out.println("\u001B[1m Engine starting CONFIG : " + configCount + "\u001B[0m");
                System.out.println("============================== ");
                CodeReviewProcess p1 = new CodeReviewProcess(SourceFile, scan.nextLine(), configCount, TempDirPath);
                p1.run();
                configCount++;
            }
        }
        else {
            System.out.println("UNDEFINED workflow : " + WorkFlow);
        }
    }
    
    public void run() {
        if (FW.getWorkFlows().contains(WorkFlow)){
            System.out.println("Found the " + WorkFlow + " workflow");
            Scanner scan = new Scanner("");
            try{
                scan = new Scanner(ConfFile);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            run(scan);
        }
        else
            System.out.println("Unrecognized workflow : " + WorkFlow);
    }
    
    private void set() {
        TempDirPath = System.getProperty("user.dir") + "/TEMP";
        File TempDir = new File(TempDirPath);
        if (TempDir.exists()) {
            try{
                FileUtils.deleteDirectory(TempDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!TempDir.mkdir()) {
            System.out.println("ERROR: Can not creat directory: " + TempDirPath);
            System.exit(0);
        }
    }
    
    public static void main(String args[]){
        engine e1 = new engine(args);
        e1.run();
    }
    
}
