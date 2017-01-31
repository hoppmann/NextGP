package de.NextGP.general;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	private String combFileOut;
	private String combGenoOut;
	private String masterScript;
	private Combined combined;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public SlurmWriter(Map<String, Patients> patients, Combined combined, GetOptions options) {

		this.patients = patients;
		this.combined = combined;
		this.slurmDir = options.getSlurmDir();

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
	@SuppressWarnings("unchecked")
	public void saveSinglePatCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// Create batch file to write commands in.
			// prepare writer			
			String outFile = slurmDir + sep + "02-" + curPat + ".sh";
			Writer batch = new Writer();
			batch.openWriter(outFile);


			// write first/general lines of batch file
			batch.writeLine("#!/bin/bash");
			batch.writeLine("#SBATCH --cpus-per-task=4");


			//////// for each command created write in batch file
			for (Method getCurCmd : curPatObject.getPrimaryCommands()) {

				// retrieve command
				Object returnValue = getCurCmd.invoke(curPatObject);
				ArrayList<String> cmd = (ArrayList<String>) returnValue;

				// write in file
				batch.writeCmd(cmd);

			}



			// close Writer
			batch.close();
		}

	}



	///////////////////
	//////// prepare patient file post gatk calling

	@SuppressWarnings("unchecked")
	public void saveSecondaryCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// create batch file to write commands in
			// prepare writer
			String outFile = slurmDir + sep + "04-" + curPat + ".sh";
			Writer secondary = new Writer();
			secondary.openWriter(outFile);

			// Write first/genral lines of batch file
			secondary.writeLine("#!/bin/bash");
			secondary.writeLine("#SBATCH --cpus-per-task=4");

			//////// foreach command created write in batch file
			for (Method getCurCommand : curPatObject.getSecondaryCommands()){

				// retrieve command
				Object returnValue = getCurCommand.invoke(curPatObject);
				ArrayList<String> cmd = (ArrayList<String>) returnValue;

				// write in file
				secondary.writeCmd(cmd);

			}

			// close writer
			secondary.close();

		}



	}









	///////////////////////
	//////// save command from combined genotyping 
	@SuppressWarnings("unchecked")
	public void savecombinedGenotyping() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// create File
		Writer combGeno = new Writer();
		combGeno.openWriter(combGenoOut);


		// add first line to file
		combGeno.writeLine("#!/bin/bash");
		combGeno.writeLine("#SBATCH --cpus-per-task=4");


		// for each entered command save in combined file
		for (Method getCmd : combined.getGenotypeCommands()) {


			ArrayList<String> cmd = (ArrayList<String>) getCmd.invoke(combined);
			combGeno.writeCmd(cmd);

		}

		combGeno.close();

	}











	//////////////////
	//////// save commands to be run on combined file

	@SuppressWarnings("unchecked")
	public void saveCombinedCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// create file
		Writer comb = new Writer();
		comb.openWriter(combFileOut);

		// add first line to combined commands
		comb.writeLine("#!/bin/bash");

		// for each entered command save in combined file
		for (Method getCmd : combined.getCommands()) {

			ArrayList<String> cmd = (ArrayList<String>) getCmd.invoke(combined);
			comb.writeCmd(cmd);

		}




		comb.close();
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
		for (String mkdirCmd : combined.getMkDirs()){
			master.writeLine("mkdir -p " + mkdirCmd);
		}
		
		// enter empty line for better visibility
		master.writeLine("");

		// prepare wait command for execution of combined steps after
		// single steps are done
		String waitCombinedGenotypePrep ="";

		
		// single patient files
		// for each patient get write sbatch command and add to wait command
		for (String curPat : patients.keySet()) {

			// prepare variables
			String outFile = slurmDir + sep + "02-" + curPat + ".sh";

			// write bash execution in master file and prepare wait command
			String curPatVar = "Pat_" + curPat;
			master.writeLine(curPatVar + "=$(sbatch -p genepi " + outFile + ")");
			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitCombinedGenotypePrep += ":$" + curPatVar;

		}

		
		
		
		// create slurm command for the combined genotyping step
		String waitCombinedGenotype = "\ncomb=$(sbatch -p genepi -d afterok" + waitCombinedGenotypePrep + " " + combGenoOut + ")";

		// write wait command in master file
		master.writeLine(waitCombinedGenotype);
		master.writeLine("comb=$(echo $comb | sed 's/Submitted batch job //')");








		////////////////////////////////
		//////// post consensus ////////
		////////////////////////////////

		// add emtpy lines for better visibility
		master.writeLine("\n");
		

		// prepare wait command for second combined steps
		String waitPostCombinedGenotype ="\nsbatch -p genepi -d afterok";


		for (String curPat : patients.keySet()) {

			// prepare name of patient file and other variables
			String outFile = slurmDir + sep + "04-" + curPat + ".sh";
			String curPatVar = "Pat2_" + curPat;

			// write bash execution in master file and prepare wait command
			master.writeLine(curPatVar + "=$(sbatch -p genepi -d afterok:$comb " + outFile + ")");
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
