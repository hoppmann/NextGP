package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
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
	private int first;
	private int last;
	
	
	private static Logger logger = LoggerFactory.getLogger(SamToBam.class);

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public SamToBam(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

		// accept variables
		this.config = config;
		this.patients = patients;
		this.outDir = options.getIntermediateDir() + File.separator + options.getOutDir() + File.separator + config.getAlignment();
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();
		
		// make log entry
		Log.logger(logger, "Converting sam to bam");

		// create directory
		mkdir();

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
		 String cmd = out;
		 combined.mkdir(cmd);
		 
	 }
	
	
	
	
	//////////
	//// prepare command to convert sam to bam using picard
	private void prepareCmd() {
		// get program path
		String picard = config.getPicard();

		
		// prepare command for each patient separately, save in Map and execute all together
		for (String curPat : patients.keySet()){

			// prepare command
			String outBam = outDir + File.separator + "bam" + File.separator +curPat + ".bam";
			
			ArrayList<String> cmd = new ArrayList<>();
			
			cmd.add(config.getJava());
			cmd.add(config.getJavaTmpOption());
			cmd.add("-jar " + picard);
			cmd.add("SortSam");
			cmd.add("SO=coordinate");
			cmd.add("INPUT=" + outDir + File.separator + "sam" + File.separator + curPat + ".sam");
			cmd.add("OUTPUT=" + outBam);
			
			// save cmd in patient object
			if (first <= 1 && last >= 1 ) {
				patients.get(curPat).setSamToBam(cmd);
			}
			// save name of bam files
			patients.get(curPat).setLastOutFile(outBam);
		}		

	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
}
