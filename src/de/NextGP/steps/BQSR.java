package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class BQSR {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private Map<String, Patients> patients;
	private GetOptions options; 
	private LoadConfig config; 
	private static Logger logger = LoggerFactory.getLogger(BQSR.class); 
	private String sep;
	private String outDir;
	private String outputPre;
	private String outputPost;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public BQSR(Map<String, Patients> patients, LoadConfig config, GetOptions options) {

		// retrieve variables
		this.patients = patients;
		this.config = config;
		this.options = options;

		// make log entry
		Log.logger(logger, "Preparing BQSR");

		// prepare general variables
		sep = File.separator;
		outDir = options.getOutDir() + sep + config.getBaseReacalibration();
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
		outputPre = outDir + sep + subFolder + sep + curPat + ".recal.grp";
		String logfile = outDir + sep + subFolder + sep + curPat + ".recal.log";


		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-l INFO");
		cmd.add("-T BaseRecalibrator");
		cmd.add("-nct " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-knownSites " + config.getDbsnp());
		cmd.add("-knownSites " + config.getIndelMills());
		cmd.add("-knownSites " + config.getIndel1kgp());
		cmd.add("-I " + input);
		cmd.add("-o " + outputPre);
		cmd.add("-log " + logfile);

		// save command in patient object
		patients.get(curPat).setBaseRecalibration_pre(cmd);

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
		outputPost = outDir + sep + subFolder + sep + curPat + ".recal.grp";
		String logfile = outDir + sep + subFolder + sep + curPat + ".recal.log";
		cmd = new ArrayList<>();

		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T BaseRecalibrator");
		cmd.add("-l INFO");
		cmd.add("-nct " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-knownSites " + config.getDbsnp());
		cmd.add("-knownSites " + config.getIndelMills());
		cmd.add("-knownSites " + config.getIndel1kgp());
		cmd.add("-BQSR " + bqsrIn);
		cmd.add("-I " + input);
		cmd.add("-o " + outputPost);
		cmd.add("-log " + logfile);

		// save command in patients object
		patients.get(curPat).setBaseRecalibration_post(cmd);


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
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T AnalyzeCovariates");
		cmd.add("-l INFO");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-before " + outputPre);
		cmd.add("-after " + outputPost);
		cmd.add("-plots " + plotsOut);
		cmd.add("-log " + logfile);

		// save command
		patients.get(curPat).setAnalyzeCovariates(cmd);



	}

///////////////
//////// prepare print reads command

	public void printReads(String curPat) {
		//////////////
		//////// print Reads

		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String output = outDir + sep + curPat + ".bam";
		String logfile = outDir + sep + curPat + ".log";
		String bqsrIn = outputPre;
		String input = patients.get(curPat).getLastOutFile();
		if (input == null || input.isEmpty()) {
			input = options.getOutDir() + sep + config.getRealignment() + sep + curPat + ".bam";
		}


		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
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
		patients.get(curPat).setPrintReads(cmd);
		patients.get(curPat).setLastOutFile(output);





	}


}





/////////////////////////////////
//////// getter / setter ////////
/////////////////////////////////







