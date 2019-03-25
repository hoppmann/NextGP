package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class SamToBam {


	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private LoadConfig config;
	private Map<String, Patients> patients;
	private String outDir;
	private int first;
	private int last;
	private GetOptions options;
	
	

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public SamToBam(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

		// accept variables
		this.config = config;
		this.patients = patients;
		this.outDir = config.getLocalTmp() + File.separator + options.getOutDir() + File.separator + config.getAlignment();
		this.first = options.getFirst();
		this.last = options.getLast();
		this.options = options;
		
		
		// make log entry
		Log.logger("Preparing sam to bam convertion.");

		// prepare command

		prepareCmd();
		
	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////

	///////////////////
	//////// prepare cmd to make directory
	
	 private void mkdir(String curPat) {
		 String out = outDir + File.separator + "bam";
		 ArrayList<String> mkdirCmd = new ArrayList<>();
		 mkdirCmd.add("mkdir -p " + out);
		 patients.get(curPat).addCmd02(mkdirCmd);
	 }
	
	
	
	
	//////////
	//// prepare command to convert sam to bam using picard
	private void prepareCmd() {
		// get program path
		String picard = config.getPicard();

		
		// prepare command for each patient separately, save in Map and execute all together
		for (String curPat : patients.keySet()){

			// prepare mkdir cmd
			mkdir(curPat);
			
			// prepare command
			String outBam = outDir + File.separator + "bam" + File.separator +curPat + ".bam";
			
			ArrayList<String> cmd = new ArrayList<>();
			
			cmd.add(config.getJava());
			cmd.add("-jar " + picard);
			cmd.add("SortSam");
			cmd.add("SO=coordinate");
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			cmd.add("INPUT=" + patients.get(curPat).getLastOutFile());
			cmd.add("OUTPUT=" + outBam);
			
			// save cmd in patient object
			Integer step = options.getSteps().get("alignment");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);;
			}
			// save name of bam files
			patients.get(curPat).setLastOutFile(outBam);
		}		

	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
}
