package de.NextGP.general;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.print.DocFlavor.STRING;

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
	private String combFileOut;
	private String combGenoOut;
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
		combGenoOut = slurmDir + sep + "03-gatkGenotyping.sh";
		combFileOut = slurmDir + sep + "05-combined.sh";


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
	//////// for each patient save prepared pre-GATK calling commands in slurm file
	public void saveSinglePatCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// Create batch file to write commands in.
			// prepare writer			
			String outFile = slurmDir + sep + "02-" + curPat + ".sh";
			Writer primaryCmds = new Writer();
			primaryCmds.openWriter(outFile);


			// write first/general lines of batch file
			commonHeader(primaryCmds, false);			


			//////// for each command created write in batch file
			
			for (ArrayList<String> cmd : curPatObject.getCmds02()) {
				
				// write in file
				primaryCmds.writeCmd(cmd);
				
			}
			

			primaryCmds.writeLine("echo \"script finished\"");
			// close Writer
			primaryCmds.close();
		}

	}



	///////////////////
	//////// prepare patient file post gatk calling

	public void saveSecondaryCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// create batch file to write commands in
			// prepare writer
			String outFile = slurmDir + sep + "04-" + curPat + ".sh";
			Writer secondary = new Writer();
			secondary.openWriter(outFile);

			// Write first/genral lines of batch file
			commonHeader(secondary, false);
			
			//////// foreach command created write in batch file
			for (ArrayList<String> cmd : curPatObject.getCmds04()) {
				
				// write in file
				secondary.writeCmd(cmd);
				
			}
			

			// make comment to see if script finished
			secondary.writeLine("echo \"script finished\"");
			
			// close writer
			secondary.close();

		}



	}









	///////////////////////
	//////// save command from combined genotyping 
	public void savecombinedGenotyping() {

		// create File
		Writer combGeno = new Writer();
		combGeno.openWriter(combGenoOut);


		// add first line to file
		commonHeader(combGeno, false);
		
		// for each entered command save in combined file
		for (ArrayList<String> curCmd : combined.getCmds03()) {
			
			combGeno.writeCmd(curCmd);
			
			
		}

		// make comment to see if script finished
		combGeno.writeLine("echo \"script finished\"");

		combGeno.close();

	}











	//////////////////
	//////// save commands to be run on combined file

	public void saveCombinedCommands() {

		// create file
		Writer comb = new Writer();
		comb.openWriter(combFileOut);

		// add first line to combined commands
		commonHeader(comb, true);
		
		// for each entered command save in combined file
		for (ArrayList<String> curCmd : combined.getCmds05()) {
			
			comb.writeCmd(curCmd);
			
		}
		
		// make comment to see if script finished
		comb.writeLine("echo \"script finished\"");

		comb.close();
	}


	
	
	//// prepare common header
	private void commonHeader(Writer outFile, boolean finalFile) {
		
		outFile.writeLine("#!/bin/bash");
		outFile.writeOption("--cpus-per-task=" + maxCPU);
		outFile.writeOption("--mem=" + maxMem + "G");
		outFile.writeOption("--output " + options.getSlurmLog() + sep + "slurm-%A.log");
		

		// exclude nodes if set
		if (options.getExclude() != null) {
			outFile.writeOption("--exclude=" + String.join(",", options.getExclude()));
		}
		
		// specify specific nodes to use
		if (options.getRestrict() != null) {
			outFile.writeOption("--nodelist=" + String.join(",", options.getRestrict()));
		}
		
		// add mail option if set
		if (finalFile && options.isMail()) {
			outFile.writeOption("--mail-type=FAIL");
			outFile.writeOption("--mail-type=END");
		} else if (options.isMail()){
			outFile.writeOption("--mail-type=FAIL");
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////
	//////// prepare master script to start pipeline

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
			master.writeLine(curPatVar + "=$(sbatch -p " + slurmPatition + " " + outFile + ")");
			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitCombinedGenotypePrep += ":$" + curPatVar;

		}

		
		
		
		// create slurm command for the combined genotyping step
		String waitCombinedGenotype = "\ncomb=$(sbatch -p " + slurmPatition + " -d afterok" + waitCombinedGenotypePrep + " " + combGenoOut + ")";

		// write wait command in master file
		master.writeLine(waitCombinedGenotype);
		master.writeLine("comb=$(echo $comb | sed 's/Submitted batch job //')");








		////////////////////////////////
		//////// post consensus ////////
		////////////////////////////////

		// add emtpy lines for better visibility
		master.writeLine("\n");
		

		// prepare wait command for second combined steps
		String waitPostCombinedGenotype ="\nsbatch -p " + slurmPatition + " -d afterok";


		for (String curPat : patients.keySet()) {

			// prepare name of patient file and other variables
			String outFile = slurmDir + sep + "04-" + curPat + ".sh";
			String curPatVar = "Pat2_" + curPat;

			// write bash execution in master file and prepare wait command
			master.writeLine(curPatVar + "=$(sbatch -p " + slurmPatition + " -d afterok:$comb " + outFile + ")");
			master.writeLine(curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitPostCombinedGenotype += ":$" + curPatVar;

		}

		// write wait command in master file
		master.writeLine(waitPostCombinedGenotype + " " + combFileOut);

		




		// close master file
		master.close();


	}








	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
