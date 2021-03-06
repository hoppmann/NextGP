package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.options.GetOptions;

public class Preprocess {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	GetOptions options;
	Combined combined; 
	Map<String, Patients> patients;
	private int first;
	private int last;




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Preprocess(GetOptions options, Map<String, Patients> patients, Combined combined) {

		this.options = options;
		this.patients = patients;
		this.first = options.getFirst();
		this.last = options.getLast();
		this.combined = combined;

		// make log entry
		System.out.println("Prepareing preprocessing commands.");

	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	
	public void fastp() {

		for (String curPat : patients.keySet()) {
			// prepare variables
			String forward = patients.get(curPat).getForward();
			String reverse = patients.get(curPat).getReverse();
			String sep = File.separator;
			String outDir = options.getConfig().getLocalTmp() + sep + options.getOutDir() + sep
					+ options.getConfig().getAlignment() + sep + "Fastp";

			String forwardClean = outDir + sep + new File(forward).getName();
			String reverseClean = outDir + sep + new File(reverse).getName();
			
			
			
			
			String reportOut = options.getOutDir() + sep + options.getConfig().getMetrices() + sep + "Fastp";
			String htmlOut = reportOut + sep + curPat + ".html";
			
			// prepare command
			ArrayList<String> cmd = new ArrayList<>();
			cmd.add(options.getConfig().getFastp());

			// add input
			cmd.add("-i " + forward);
			cmd.add("-I " + reverse);

			// add output
			cmd.add("-o " + forwardClean);
			cmd.add("-O " + reverseClean);
			
			// other options
			cmd.add("--detect_adapter_for_pe");
			cmd.add("-h " + htmlOut);
			
			
			
			
			
			
			
			
			
			// set fastq path to new path
			patients.get(curPat).setForward(forwardClean);
			patients.get(curPat).setReverse(reverseClean);

			// save command in patients object
			Integer step = options.getSteps().get("preprocess");

			combined.mkdir(outDir);
			combined.mkdir(reportOut);

			if (first <= step && last >= step) {
				
				patients.get(curPat).addCmd02(cmd);
			}
		}
	}
	
	
	
	
	
	
	
	public void afterQc() {

		for (String curPat : patients.keySet()) {


			// prepare variables
			String forward = patients.get(curPat).getForward();
			String reverse = patients.get(curPat).getReverse();
			String sep = File.separator;
			String outDir = options.getConfig().getLocalTmp() + sep + options.getOutDir() + sep +
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
			 * that for check weather the file has the ending fastq of fq. And if is zipped or not. 
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
