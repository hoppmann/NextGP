package de.NextGP.general;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.options.GetOptions;

public class SlurmWriter {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private Map<String, Patients> patients;
	private static Logger logger = LoggerFactory.getLogger(SlurmWriter.class);
	private String sep = File.separator;
	private String slurmDir;
	private String combFileOutName;
	private String combFileOutFile;
	private String combGenoOutName;
	private String combGenoOutFile;
	private String masterScript;
	private Combined combined;
	private String slurmPatition;
	private int maxCPU;
	private int maxMem;
	private GetOptions options;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public SlurmWriter(Map<String, Patients> patients, Combined combined, GetOptions options) {

		this.patients = patients;
		this.combined = combined;
		this.slurmDir = options.getSlurmDir();
		this.slurmPatition = options.getSlurmPatition();
		this.maxCPU = Integer.valueOf(options.getCpu());
		this.maxMem = Integer.valueOf(options.getMem());
		this.options = options;

		// make log entry
		logger.info("Writeing commands in batch files");

		// gather general variables
		masterScript = slurmDir + sep + "01-master.sh";
		combGenoOutName = "03-gatkGenotyping";
		combGenoOutFile = slurmDir + sep + combGenoOutName + ".sh";
		combFileOutName = "05-combined";
		combFileOutFile = slurmDir + sep + combFileOutName + ".sh";


	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////



	////////////////////////
	//////// generate all files needed

	public void generateFiles() {



		try {
			saveSinglePatCommands();
			saveCombinedCommands();
			saveMasterScript();
			savecombinedGenotyping();
			saveSecondaryCommands();

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	///////////////////////
	//////// 02 files
	//////// for each patient save prepared pre-GATK calling commands in slurm file
	public void saveSinglePatCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// Create batch file to write commands in.
			// prepare writer
			String outPrefix = "02-" + curPat;
			String outFile = slurmDir + sep + 	outPrefix + ".sh";
			String slurmLogName = outPrefix;
			Writer primaryCmds = new Writer();
			primaryCmds.openWriter(outFile);


			// write first/general lines of batch file
			commonHeader(primaryCmds, false, slurmLogName);			


			//////// for each command created write in batch file
			
			for (ArrayList<String> cmd : curPatObject.getCmds02()) {
				
				// write in file
				ArrayList<String> date = new ArrayList<>();
				date.add("date");
				primaryCmds.writeCmd(date);
				primaryCmds.writeCmd(cmd);
				primaryCmds.writeCmd(date);
				
			}
			

			primaryCmds.writeLine("echo \"script finished\"");
			// close Writer
			primaryCmds.close();
		}

	}



	///////////////////
	//////// 04 files
	//////// prepare patient file post gatk calling

	public void saveSecondaryCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// create batch file to write commands in
			// prepare writer
			String outPrefix =  "04-" + curPat;
			String outFile = slurmDir + sep + outPrefix + ".sh";
			String slurmLogName = outPrefix;
			Writer secondary = new Writer();
			secondary.openWriter(outFile);

			// Write first/genral lines of batch file
			commonHeader(secondary, false, slurmLogName);
			
			//////// foreach command created write in batch file
			for (ArrayList<String> cmd : curPatObject.getCmds04()) {
				
				// write in file
				secondary.writeCmd(cmd);
				ArrayList<String> date = new ArrayList<>();
				date.add("date");
				secondary.writeCmd(date);
				
			}
			

			// make comment to see if script finished
			secondary.writeLine("echo \"script finished\"");
			
			// close writer
			secondary.close();

		}



	}









	///////////////////////
	//////// save command from 03 combined genotyping 
	public void savecombinedGenotyping() {

		// create File
		String outFileName = slurmDir + sep + combGenoOutName + ".sh";
		String slurmLogName = combGenoOutName;
		Writer combGeno = new Writer();
		combGeno.openWriter(outFileName);
		


		// add first line to file
		commonHeader(combGeno, false, slurmLogName);
		
		// for each entered command save in combined file
		for (ArrayList<String> curCmd : combined.getCmds03()) {
			
			combGeno.writeCmd(curCmd);
			ArrayList<String> date = new ArrayList<>();
			date.add("date");
			combGeno.writeCmd(date);
			
		}

		// make comment to see if script finished
		combGeno.writeLine("echo \"script finished\"");

		combGeno.close();

	}











	//////////////////
	//////// 05 save commands to be run on combined file

	public void saveCombinedCommands() {

		// create file
		String fileOut = slurmDir + sep + combFileOutName + ".sh";
		String slurmLogName = combFileOutName;
		Writer comb = new Writer();
		comb.openWriter(fileOut);

		// add first line to combined commands
		commonHeader(comb, true, slurmLogName);
		
		// for each entered command save in combined file
		for (ArrayList<String> curCmd : combined.getCmds05()) {
			
			comb.writeCmd(curCmd);
			ArrayList<String> date = new ArrayList<>();
			date.add("date");
			comb.writeCmd(date);
		}
		
		// make comment to see if script finished
		comb.writeLine("echo \"script finished\"");
		
		comb.close();
	}


	
	
	//// prepare common header
	private void commonHeader(Writer outFile, boolean finalFile, String slurmLogName) {
		
		    
		
	    
	    ////////////////////////////////
	    ///////// prepare slurm log name
	    
	    slurmLogName = slurmLogName + "_%j.log";
	    
		///////////////////////////////
		//////// add slurm based options ////////
		
		outFile.writeLine("#!/bin/bash");
		outFile.writeOption("--cpus-per-task=" + maxCPU);
		outFile.writeOption("--mem=" + maxMem + "G");
		outFile.writeOption("--output " + options.getSlurmLog() + sep + slurmLogName);
		
		
		
		// exclude nodes if set
		if (options.getExclude() != null ) {
			outFile.writeOption("--exclude=" + concatString(options.getExclude()));
		}
		
		// specify specific nodes to use
		if (options.getRestrict() != null ) {
			outFile.writeOption("--nodelist=" + concatString(options.getRestrict()));
		}
		
		// add mail option if set
		if (finalFile && options.isMail()) {
			outFile.writeOption("--mail-type=FAIL");
			outFile.writeOption("--mail-type=END");
		} else if (options.isMail()) {
			outFile.writeOption("--mail-type=FAIL");
		}
		
		// add afterok option if specific PIDs given
		if (options.getAfterOk() != null) {
			outFile.writeOption("--dependency=afterok:" + options.getAfterOk());
			
		}
		
	}
	
	
	
	
	
	
	
	//////// concatenate string to fit in exclude and nodelist option
	private String concatString(String[] string) {
		
		StringBuilder sb = new StringBuilder();
		for (String str : string) {
			sb.append(str.toString()).append(",");
		}

		// return concatenated String
		return sb.substring(0, sb.length() -1 );
		
	}
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////
	//////// 01 prepare master script to start pipeline

	public void saveMasterScript() {

		// create master file
		Writer master = new Writer();
		master.openWriter(masterScript);


		///////////////////////////////
		//////// Pre consensus //////// 
		///////////////////////////////


		// enter first line 
		master.writeLine("#!/bin/bash");


		// enter command to create folder
		// create folder for slurm logs
		master.writeLine("mkdir -p " + options.getSlurmLog());
		
		for (String mkdirCmd : combined.getMkDirs()){
			master.writeLine("mkdir -p " + mkdirCmd);
		}
		
		// enter empty line for better visibility
		master.writeLine("");

		// prepare wait command for execution of combined steps after
		// single steps are done
		String waitCombinedGenotypePrep = "";

		
		// single patient files
		// for each patient get write sbatch command and add to wait command
		for (String curPat : patients.keySet()) {

			// prepare variables
			String outFile = slurmDir + sep + "02-" + curPat + ".sh";

			// write bash execution in master file and prepare wait command
			String curPatVar = "Pat_" + curPat;
			
			// check if partition is given else use slurm default
			if (slurmPatition.equals("")) {
				master.writeLine(curPatVar + "=$(sbatch " + outFile + ")");
			} else {
				master.writeLine(curPatVar + "=$(sbatch -p " + slurmPatition + " " + outFile + ")");
			}
			
			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitCombinedGenotypePrep += ":$" + curPatVar;

		}

		
		
		
		
		// create slurm command for the combined genotyping step
		String waitCombinedGenotype;
		if (slurmPatition.equals("")) {
			 waitCombinedGenotype = "\ncomb=$(sbatch -d afterok" + waitCombinedGenotypePrep + " " + combGenoOutFile + ")";
		} else {
			waitCombinedGenotype = "\ncomb=$(sbatch -p " + slurmPatition + 
				" -d afterok" + waitCombinedGenotypePrep + " " + combGenoOutFile + ")";
		}
		// write wait command in master file
		master.writeLine(waitCombinedGenotype);
		master.writeLine("comb=$(echo $comb | sed 's/Submitted batch job //')");








		////////////////////////////////
		//////// post consensus ////////
		////////////////////////////////

		// add emtpy lines for better visibility
		master.writeLine("\n");
		

		// prepare wait command for second combined steps
		String waitPostCombinedGenotype;
		if (slurmPatition.equals("")) {
			waitPostCombinedGenotype ="\nsbatch -d afterok";
		} else {
			waitPostCombinedGenotype ="\nsbatch -p " + slurmPatition + " -d afterok";
		}

		for (String curPat : patients.keySet()) {

			// prepare name of patient file and other variables
			String outFile = slurmDir + sep + "04-" + curPat + ".sh";
			String curPatVar = "Pat2_" + curPat;

			// write bash execution in master file and prepare wait command
			
			if (slurmPatition.equals("")) {
				master.writeLine(curPatVar + "=$(sbatch -d afterok:$comb " + outFile + ")");
			} else {
				master.writeLine(curPatVar + "=$(sbatch -p " + slurmPatition + " -d afterok:$comb " + outFile + ")");
			}
			master.writeLine(curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitPostCombinedGenotype += ":$" + curPatVar;

		}

		// write wait command in master file
		master.writeLine(waitPostCombinedGenotype + " " + combFileOutFile);

		




		// close master file
		master.close();


	}








	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
