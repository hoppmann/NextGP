package de.NextGP.general;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		// create directory for saving slurm scripts
		new File(slurmDir).mkdirs();
		
		// gather general variables
		masterScript = slurmDir + sep + "01-master.sh";
		combFileOut = slurmDir + sep + "combinded.sh";

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

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	///////////////////////
	//////// for each patient save prepared commands in slurm file
	@SuppressWarnings("unchecked")
	public void saveSinglePatCommands() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


		//		LinkedList<Method> list = new LinkedList<>();
		for (String curPat : patients.keySet()) {

			Patients curPatObject = patients.get(curPat);

			// Create batch file to write commands in.
			// prepare writer			
			String outFile = slurmDir + sep + curPat + ".sh";
			Writer batch = new Writer();
			batch.openWriter(outFile);


			// write first/general lines of batch file
			batch.writeLine("#!/bin/bash");
			batch.writeLine("#SBATCH --cpus-per-task=4");


			//////// for each command created write in batch file
			for (Method getCurCmd : curPatObject.getCommands()) {

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


		// enter first line 
		master.writeLine("#!/bin/bash");


		// enter command to create folder
		for (String mkdirCmd : combined.getMkDirs()){
			master.writeLine(mkdirCmd);
		}


		// prepare wait command for execution of combined steps after
		// single steps are done
		String waitCommand = "\nsbatch -p genepi -d afterok";



		// for each patient get write sbatch command and add to wait command

		for (String curPat : patients.keySet()) {

			// prepare variables
			String outFile = slurmDir + sep + curPat + ".sh";

			// write bash execution in master file and prepare wait command
			String curPatVar = "Pat_" + curPat;
			master.writeLine(curPatVar + "=$(sbatch -p genepi " + outFile + ")");
			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			waitCommand += ":$" + curPatVar;

		}


		///////////////////
		//////// finish master batch
		// write wait command in master file
		master.writeLine(waitCommand + " " + combFileOut);

		// close master file
		master.close();
		
		
	}








	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
