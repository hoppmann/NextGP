package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;


public class BWA {

	//////////////
	//// Variables
	private Map<String, Patients> patients = new HashMap<>();
	private LoadConfig config;
	private String outDir;
	private Combined combined;
	private static Logger logger = LoggerFactory.getLogger(BWA.class);
	private int first;
	private int last;
	private GetOptions options;
	
	
	// command defaults
	String cpus;


	////////////////////
	//////// Constructor



	public BWA(LoadConfig config, Map<String, Patients> patients, GetOptions opts,  Combined combined){


		
		// Retrieve values
		this.config = config;
		this.outDir = opts.getTempDir() + File.separator + opts.getOutDir() + File.separator + config.getAlignment();
		this.patients = patients;
		this.cpus = opts.getCpu();
		this.combined = combined;
		this.first = opts.getFirst();
		this.last = opts.getLast();
		this.options = opts;
		
		// write log message
		Log.logger(logger, "Preparing BWA");

		// create directory
		mkdir();

	}



	
	////////////////
	//////// make directory
	private void mkdir() {

		// prepare command
		String out = outDir + File.separator + "sam";
		combined.mkdir(out);		
	}
	
	
	////////////////
	//////// prepare mate pair command for execution
	public void singelEnd(String platform) {

		for (String curPat : patients.keySet()){
			
			// creating read groups
			// init variable
			ArrayList<String> cmd = new ArrayList<>();
			String OutFile = outDir + File.separator + "sam" + File.separator + curPat + ".sam";

			//////// command
			// add BWA path an running method
			cmd.add(config.getBwa());
			cmd.add("mem");

			// add number of threads
			cmd.add("-t " + cpus);
			
//			// specify paired end in single file
//			cmd.add("-p");
			
			// add reference
			cmd.add(config.getHg19Fasta());

			// add patient forward and backward reads
			cmd.add(patients.get(curPat).getForward());
			cmd.add("> " + OutFile);

			// save command in patient object
			Integer step = options.getSteps().get("alignment");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}
			patients.get(curPat).setLastOutFile(OutFile);
		}
	}


	////////////////
	//////// prepare mate pair command for execution
	public void pairedEnd(String platform) {

		for (String curPat : patients.keySet()){
			
			// creating read groups
			// init variable
			ArrayList<String> cmd = new ArrayList<>();
			String OutFile = outDir + File.separator + "sam" + File.separator + curPat + ".sam";

			//////// command
			// add BWA path an running method
			cmd.add(config.getBwa());
			cmd.add("mem");

			// add number of threads
			cmd.add("-t " + cpus);
			
			// add reference
			cmd.add(config.getHg19Fasta());

			// add patient forward and backward reads
			cmd.add(patients.get(curPat).getForward());
			cmd.add(patients.get(curPat).getReverse());
			cmd.add("> " + OutFile);

			// save command in patient object
			Integer step = options.getSteps().get("alignment");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}			
			patients.get(curPat).setLastOutFile(OutFile);

		}
	}

	
	
	///////////////
	//////// getter

	
	
}
