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
	private int first;
	private int last;




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Preprocess(GetOptions options, Map<String, Patients> patients) {

		this.options = options;
		this.patients = patients;
		this.first = options.getFirst();
		this.last = options.getLast();

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

			// make output zipped independent if input was zipped or not.
			cmd.add("--gzip");

			// add pathes for good/bad files in tempdir and for report in metric folder
			cmd.add("-g " + goodOut);
			cmd.add("-b " + badOut);
			cmd.add("-r " + reportOut + sep + curPat + sep + "AfterQC");



			/* 
			 * modify forward and reverse name to match AfterQC output
			 * that for check weather the file has the ending fastq of fq. And if is sipped or not. 
			 */

			if (forward.contains(".fastq.gz")) {
				if(forward.contains(".gz")){
					forward = goodOut + sep + new File(forward).getName().replace(".fastq.gz", ".good.fq.gz");
					reverse = goodOut + sep + new File(reverse).getName().replace(".fastq.gz", ".good.fq.gz");
				} else {
					forward = goodOut + sep + new File(forward).getName().replace(".fastq", ".good.fq.gz");
					reverse = goodOut + sep + new File(reverse).getName().replace(".fastq", ".good.fq.gz");
				}

			} else if (forward.contains(".fq.gz")) {
				if (forward.contains(".gz")) {
					forward = goodOut + sep + new File(forward).getName().replace(".fq.gz", ".good.fq.gz");
					reverse = goodOut + sep + new File(reverse).getName().replace(".fq.gz", ".good.fq.gz");
				} else {
					forward = goodOut + sep + new File(forward).getName().replace(".fastq", ".good.fq.gz");
					reverse = goodOut + sep + new File(reverse).getName().replace(".fastq", ".good.fq.gz");

				}
			}

			// set fastq path to new path
			patients.get(curPat).setForward(forward);
			patients.get(curPat).setReverse(reverse);

			// save command in patients object
			Integer step = options.getSteps().get("preprocess");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}



	}




	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////









}
