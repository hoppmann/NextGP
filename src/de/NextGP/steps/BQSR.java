package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class BQSR {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private Map<String, Patients> patients;
	private GetOptions options; 
	private LoadConfig config; 
	private String sep;
	private String localOutDir;
	private String outDir;
	private String outputPre;
	private String outputPost;
	private String finalOutDir;
	private int first;
	private int last;
	private boolean isSolid;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public BQSR(Map<String, Patients> patients, LoadConfig config, GetOptions options, boolean isSolid, Combined combined) {

		// retrieve variables
		this.patients = patients;
		this.config = config;
		this.options = options;
		this.first = options.getFirst();
		this.last = options.getLast();
		this.isSolid = isSolid;

		// make log entry
		System.out.println( "Preparing BQSR");

		// prepare general variables
//		tempDir = options.getTempDir();
		sep = File.separator;
		localOutDir = config.getLocalTmp() + sep + options.getOutDir() + sep + config.getBaseReacalibration();
		outDir = options.getTempDir() + sep + options.getOutDir() + sep + config.getBaseReacalibration();
		finalOutDir = options.getOutDir() + sep + config.getBaseReacalibration();
		
		combined.mkdir(finalOutDir);
		combined.mkdir(outDir);
		
		
	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	
	
	///////////////
	//////// prepare step 1

	public void baseRecal(String curPat){

		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String input = patients.get(curPat).getLastOutFile();
		if (input == null || input.isEmpty()) {
			input = options.getOutDir() + sep + config.getRealignment() + sep + curPat + ".bam";
		}
		
		String subFolder = "pre";
		outputPre = localOutDir + sep + subFolder + sep + curPat + ".recal.grp";
		String logfile = outDir + sep + subFolder + sep + curPat + ".recal.log";


		// prepare command
		cmd.add(config.getJava());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-l INFO");
		cmd.add("-T BaseRecalibrator");
		cmd.add("-nct " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		
		// add option specific to SOLiD data
		if (isSolid){
			cmd.add("-solid_nocall_strategy PURGE_READ -sMode SET_Q_ZERO_BASE_N");
		}
		
		cmd.add("-knownSites " + config.getDbsnp());
		cmd.add("-knownSites " + config.getIndelMills());
		cmd.add("-knownSites " + config.getIndel1kgp());
		cmd.add("-I " + input);
		cmd.add("-o " + outputPre);
		cmd.add("-log " + logfile);

		
		// save command in patient object
		Integer step = options.getSteps().get("recalibration");

		if (first <= step && last >= step ) {
			patients.get(curPat).addCmd02(cmd);
		}
	}


	
	
	////////////////
	//////// prepare step 2

	public void baseRecalStep2(String curPat) {



		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String input = patients.get(curPat).getLastOutFile();
		if (input == null || input.isEmpty()) {
			input = options.getOutDir() + sep + config.getRealignment() + sep + curPat + ".bam";
		}

		String bqsrIn = outputPre;
		String subFolder = "post";
		outputPost = localOutDir + sep + subFolder + sep + curPat + ".recal.grp";
		String logfile = outDir + sep + subFolder + sep + curPat + ".recal.log";
		cmd = new ArrayList<>();

		// prepare command
		cmd.add(config.getJava());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T BaseRecalibrator");
		cmd.add("-l INFO");
		cmd.add("-nct " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		
		// add option specific to SOLiD data
		if (isSolid){
			cmd.add("-solid_nocall_strategy PURGE_READ -sMode SET_Q_ZERO_BASE_N");
		}
		
		cmd.add("-knownSites " + config.getDbsnp());
		cmd.add("-knownSites " + config.getIndelMills());
		cmd.add("-knownSites " + config.getIndel1kgp());
		cmd.add("-BQSR " + bqsrIn);
		cmd.add("-I " + input);
		cmd.add("-o " + outputPost);
		cmd.add("-log " + logfile);

		// save command in patients object
		Integer step = options.getSteps().get("recalibration");

		if (first <= step && last >= step ) {
			patients.get(curPat).addCmd02(cmd);
		}

	}

	
	////////////////
	//////// prepare analyze covariates command
	
	public void analyzeCovar (String curPat) {

		////////////////
		//////// Analyze Covariates

		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String subFolder = "AnalyzeCovariates";
		String logfile = outDir + sep + subFolder + sep + curPat + ".log";
		String plotsOut = outDir + sep + subFolder + sep + curPat + ".pdf";

		// prepare command
		cmd.add(config.getJava());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T AnalyzeCovariates");
		cmd.add("-l INFO");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-before " + outputPre);
		cmd.add("-after " + outputPost);
		cmd.add("-plots " + plotsOut);
		cmd.add("-log " + logfile);

		// save command
		Integer step = options.getSteps().get("recalibration");

		if (first <= step && last >= step ) {
			patients.get(curPat).addCmd02(cmd);
		}


	}

///////////////
//////// prepare print reads command

	public void printReads(String curPat) {
		//////////////
		//////// print Reads

		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String output = finalOutDir + sep + curPat + ".bam";
		String logfile = outDir + sep + curPat + ".log";
		
		String bqsrIn = outputPre;
		String input = patients.get(curPat).getLastOutFile();
		if (input == null || input.isEmpty()) {
			input = options.getOutDir() + sep + config.getRealignment() + sep + curPat + ".bam";
		}


		// prepare command
		cmd.add(config.getJava());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T PrintReads");
		cmd.add("-l INFO");
		cmd.add("-nct " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-BQSR " + bqsrIn);
		cmd.add("-I " + input);
		cmd.add("-o " + output);
		cmd.add("-log " + logfile);

		// save command
		Integer step = options.getSteps().get("recalibration");

		if (first <= step && last >= step ) {
			patients.get(curPat).addCmd02(cmd);
		}
		patients.get(curPat).setLastOutFile(output);
		patients.get(curPat).setBam(output);





	}





	
/////////////////////////////////
//////// getter / setter ////////
/////////////////////////////////


}












