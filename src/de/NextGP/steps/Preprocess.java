package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.options.GetOptions;

public class Preprocess {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static Logger logger = LoggerFactory.getLogger(Process.class);
	GetOptions options;
	Map<String, Patients> patients;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Preprocess(GetOptions options, Map<String, Patients> patients) {

		this.options = options;
		this.patients = patients;
		
		// make log entry
		Log.logger(logger, "Prepareing AfterQC commands.");
		
	}
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////


	public void afterQc() {
		
		for (String curPat : patients.keySet()) {
			
			
			// prepare variables
			String forward = patients.get(curPat).getForward();
			String reverse = patients.get(curPat).getReverse();
			String sep = File.separator;
			String outDir = options.getIntermediateDir() + sep + options.getOutDir() + sep +
					options.getConfig().getAlignment() + sep + "AfterQC";
			String goodOut= outDir + sep + "good";
			String badOut = outDir + sep + "bad";
			String reportOut = options.getOutDir() + sep + options.getConfig().getMetrices();

			
			// prepare command
			ArrayList<String> cmd = new ArrayList<>();
			cmd.add ("python " + options.getConfig().getAfterQC());
			// add forward and reverse reads
			cmd.add("-1 " + forward);
			cmd.add("-2 " + reverse);
			
			// add pathes for good/bad files in tempdir and for report in metric folder
			cmd.add("-g " + goodOut);
			cmd.add("-b " + badOut);
			cmd.add("-r " + reportOut + sep + curPat + sep + "AfterQC");
			
			
			
			// modify forward and reverse name to match AfterQC output
			forward = goodOut + sep + new File(forward).getName().replace(".fastq.gz", ".good.fastq.gz");
			reverse = goodOut + sep + new File(reverse).getName().replace(".fastq.gz", ".good.fastq.gz");

			// set fastq path to new path
			patients.get(curPat).setForward(forward);
			patients.get(curPat).setReverse(reverse);
			
			
			// save command in patients object
			patients.get(curPat).setAfterQC(cmd);
		
		}
		
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////









}
