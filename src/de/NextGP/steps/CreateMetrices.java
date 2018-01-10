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

public class CreateMetrices {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private static Logger logger = LoggerFactory.getLogger(CreateMetrices.class);
	private GetOptions options;
	private LoadConfig config;
	private Map<String, Patients> patients;
	private Combined combined;
	private int first;
	private int last;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public CreateMetrices(GetOptions options, LoadConfig config, 
			Map<String, Patients> patients, Combined combined) {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.patients = patients;
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();

		// make log etntry
		Log.logger(logger, "Preapring metrics");

		// create directory
		mkdir();

	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	////////////
	//////// mkdir

	private void mkdir() {
		// create directory for each patient
		for (String curPat : patients.keySet()) {
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();
			String cmd = outDir + sep + curPat;
			combined.mkdir(cmd); 
		}
	}


	/////////////
	//////// mean depth

	public void meanDepth() {

		// make log entry
		Log.logger(logger, "Preparing mean Depth");


		for (String curPat : patients.keySet()){


			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();
			String depthOut = outDir + sep + curPat + sep + curPat + ".meanDepth.txt";
			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}


			// prepare command
			cmd.add(config.getSamtools());
			cmd.add("depth");
			cmd.add("-b " + options.getBedFile());
			cmd.add(input);
			cmd.add("| awk '{sum+=$3;cnt++} END {print \"" + patients.get(curPat).getLastOutFile() + "\\t\"sum/cnt}'");
			cmd.add("> " + depthOut);


			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}

		}
	}



	/////////////
	//////// AS Metric 
	public void asMetric(){

		// make log entry
		Log.logger(logger, "Preparing alignment summary metrics");


		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();

			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			String asOut = outDir + sep + curPat + sep + curPat + ".ASMetric.txt";
			String asLog = outDir + sep + curPat + sep + curPat + ".ASMetric.log";


			// prepare command
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectAlignmentSummaryMetrics");
			cmd.add("REFERENCE_SEQUENCE=" + config.getHg19Fasta());
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT=" + asOut);
			cmd.add("2> " + asLog);


			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}
	}



	/////////////
	//////// GcBias Metric 
	public void gcbMetric() {

		// make log entry
		Log.logger(logger, "Preparing mean GC bias metrics");

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();
			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			String gcMetricChartOut = outDir + sep + curPat + sep + curPat + ".GcBMetrics.pdf";
			String gcMetricSummaryOut = outDir + sep + curPat + sep + curPat + ".GcBMetrics.txt";
			String gcMetricLog = outDir + sep + curPat + sep + curPat + ".GcBMetric.log";
			String output = outDir + sep + curPat + sep + curPat + ".GcB.txt";

			// prepare command
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectGcBiasMetrics");
			cmd.add("REFERENCE_SEQUENCE=" + config.getHg19Fasta());
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT=" + output);
			cmd.add("CHART_OUTPUT=" + gcMetricChartOut);
			cmd.add("SUMMARY_OUTPUT=" + gcMetricSummaryOut);
			cmd.add("2>" + gcMetricLog);


			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}

	}


	/////////////
	//////// InsertSize Metric 
	public void isMetric(){

		// make log entry
		Log.logger(logger, "Preparing insert size metrics");

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();
			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			String isOut = outDir +  sep + curPat + sep + curPat + ".GCBMetric.txt";
			String isHistOut = outDir + sep + curPat + sep + curPat + ".ISMetric.pdf";
			String isLog = outDir + sep + curPat + sep + curPat + ".isMetrics.log";


			// prepare command
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectInsertSizeMetrics");
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT= " + isOut);
			cmd.add("HISTOGRAM_FILE=" + isHistOut);
			cmd.add("2> " + isLog);


			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}
	}


	//////// InsertSize Metric for small 
	//TODO


	/////////////
	//////// coverage Metric 

	public void coverage(){

		// make log entry
		Log.logger(logger, "Preparing coverage metrics");

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getMetrices();
			String covOut = outDir + sep + curPat + sep + curPat + ".converageBed.hist";
			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}


			// prepare command
			cmd.add(config.getBedTools() + sep + "coverageBed");
			cmd.add("-abam " + input);
			cmd.add("-b " + options.getBedFile());
			cmd.add("-hist");
			cmd.add("> " + covOut);


			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}

		}
	}

	/////////////
	//////// fastQC
	public void runFastQC() {


		// init varialbes
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getMetrices() + sep + "fastQC";
		
		
		// create dir command
		combined.mkdir(outDir);

		// iterate for each patient
		for (String curPat : patients.keySet()){

			// init command array
			ArrayList<String> cmd = new ArrayList<>();
			
			// prepare command
			cmd.add(config.getFastQC());
			cmd.add("-t " + options.getCpu());
			cmd.add("-o " + outDir);
			cmd.add(patients.get(curPat).getBam());

			
			// save command
			if (first <= 5 && last >= 5 ) {
				patients.get(curPat).addCmd02(cmd);
			}

			
		}
		
		
		
 
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
