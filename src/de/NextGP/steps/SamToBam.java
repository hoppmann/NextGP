package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class SamToBam {


	///////////////////////////
	//////// variables ////////
	///////////////////////////

//	private GetOptions opts; 
	private LoadConfig config;
	private Map<String, Patients> patients;
	private String outDir;
	private Combined combined;
	
	
	private static Logger logger = LoggerFactory.getLogger(SamToBam.class);

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public SamToBam(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

		// accept variables
		this.config = config;
		this.patients = patients;
		this.outDir = options.getOutDir() + File.separator + config.getAlignment();
		this.combined = combined;
		
		// make log entry
		Log.logger(logger, "Converting sam to bam");

		// create directory
		mkdir();

//		// get version number of picard
//		logger.info("Picard SortSam version: " + new getVersion().getPicardVersion(config) + "\n");
		
		
		
		// prepare command

		prepareCmd();
		
	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////

	///////////////////
	//////// prepare cmd to make directory
	
	 private void mkdir() {
		 String out = outDir + File.separator + "bam";
		 String cmd = "mkdir -p " + out;
		 combined.mkdir(cmd);
		 
	 }
	
	
	
	
	//////////
	//// prepare command to convert sam to bam using picard
	private void prepareCmd() {
		// get program path
		String picard = config.getPicard();
		String java = config.getJava();

		
		// prepare command for each patient separately, save in Map and execute all together
		for (String curPat : patients.keySet()){

			// prepare command
			String outBam = outDir + File.separator + "bam" + File.separator +curPat + ".bam";
			
			ArrayList<String> cmd = new ArrayList<>();
			
			cmd.add(java);
			cmd.add("-jar " + picard);
			cmd.add("SortSam");
			cmd.add("SO=coordinate");
			cmd.add("INPUT=" + outDir + File.separator + "sam" + File.separator + curPat + ".sam");
			cmd.add("OUTPUT=" + outBam);
			
			// save cmd in patient object
			patients.get(curPat).setSamToBam(cmd);
			
			// save name of bam files
			patients.get(curPat).setLastOutFile(outBam);
		}		

	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
}