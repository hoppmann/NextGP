package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class CreateMetrices {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
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
		System.out.println( "Preapring metrics");

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
		System.out.println("Preparing mean Depth");


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
			Integer step = options.getSteps().get("metrices");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}

		}
	}



	/////////////
	//////// AS Metric 
	public void asMetric(){

		// make log entry
		System.out.println("Preparing alignment summary metrics");


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
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectAlignmentSummaryMetrics");
			cmd.add("REFERENCE_SEQUENCE=" + config.getHg19Fasta());
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT=" + asOut);
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			cmd.add("2> " + asLog);


			// save command
			Integer step = options.getSteps().get("metrices");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}
	}



	/////////////
	//////// GcBias Metric 
	public void gcbMetric() {

		// make log entry
		System.out.println("Preparing mean GC bias metrics");

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
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectGcBiasMetrics");
			cmd.add("REFERENCE_SEQUENCE=" + config.getHg19Fasta());
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT=" + output);
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			cmd.add("CHART_OUTPUT=" + gcMetricChartOut);
			cmd.add("SUMMARY_OUTPUT=" + gcMetricSummaryOut);
			cmd.add("2>" + gcMetricLog);


			// save command
			Integer step = options.getSteps().get("metrices");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}
		}

	}


	/////////////
	//////// InsertSize Metric 
	public void isMetric(){

		// make log entry
		System.out.println("Preparing insert size metrics");

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
			cmd.add("-jar " + config.getPicard());
			cmd.add("CollectInsertSizeMetrics");
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("INPUT=" + input);
			cmd.add("OUTPUT= " + isOut);
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			cmd.add("HISTOGRAM_FILE=" + isHistOut);
			cmd.add("2> " + isLog);


			// save command
			Integer step = options.getSteps().get("metrices");

			if (first <= step && last >= step ) {
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
			Integer step = options.getSteps().get("metrices");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}

			
		}
		
		
		
 
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
