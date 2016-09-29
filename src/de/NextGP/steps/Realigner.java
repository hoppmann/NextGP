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

public class Realigner {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static Logger logger = LoggerFactory.getLogger(Realigner.class);
	private LoadConfig config;
	private GetOptions options;
	private String outDir;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Realigner(Map<String, Patients> patients, LoadConfig config, GetOptions options) {

		// retrieve variables
		this.config = config;
		this.options = options;
		this.outDir = options.getOutDir();

		// run realign target creator
		Log.logger(logger, "Prparing indel realigner");
		targetCreator(patients);

//		// run indel raligner
//		Log.logger(logger, "Preparing IndelRealigner");
//		realign(patients);

	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////


	// prepare realign target creator
	private void targetCreator(Map<String, Patients> patients){

		for (String curPat : patients.keySet()){

			// define input / output / logfile names
			String sep = File.separator;
			String input = patients.get(curPat).getLastOutFile();
//			String input = outDir + sep + config.getAlignment() + sep + curPat + ".bam";
			String outputCreator  = outDir + sep + config.getRealignment() + sep + curPat + sep + curPat +".list";
			String logfile = outDir + sep + config.getRealignment() + sep + curPat + sep + curPat + ".creator.log";

			//////////////
			//////// TargetCreator
			
			//prepare command 
			ArrayList<String> cmd = new ArrayList<>();

			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T RealignerTargetCreator" );
			cmd.add("-l INFO");
			cmd.add("-nt " + options.getCpu());
			cmd.add("-R " + config.getHg19Fasta());
			cmd.add("-known " + config.getIndelMills());
			cmd.add("-known " + config.getIndel1kgp());
			cmd.add("--filter_mismatching_base_and_quals");
			cmd.add("--filter_bases_not_stored");
			cmd.add("--filter_reads_with_N_cigar");
			cmd.add("-I " + input);
			cmd.add("-o " + outputCreator);
			cmd.add("-log " + logfile);


			// add command in Patient object
			patients.get(curPat).setRealignmentTargetCreator(cmd);

			
			
			//////////////
			//////// Realigner
			
			// define input / output / logfile names
			String outDir = options.getOutDir();
			String outFile  = outDir + sep + config.getRealignment() + sep + curPat +".bam";
			String logfileRealign = outDir + sep + config.getRealignment() + sep + curPat + sep + curPat + ".realigner.log";

			//prepare command
			cmd = new ArrayList<>();
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T IndelRealigner");
			cmd.add("-l INFO");
			cmd.add("--filter_mismatching_base_and_quals");
			cmd.add("--filter_bases_not_stored");
			cmd.add("--filter_reads_with_N_cigar");
			cmd.add("-targetIntervals " + outputCreator);
			cmd.add("-R " + config.getHg19Fasta());
			cmd.add("-I " + input);
			cmd.add("-o " + outFile);
			cmd.add("-log " + logfileRealign);
			
			// save cmd in patient object
			patients.get(curPat).setIndelRealigner(cmd);
			patients.get(curPat).setLastOutFile(outFile);

		}
	}






	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
