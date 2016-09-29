package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Gemini {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static Logger logger = LoggerFactory.getLogger(Gemini.class);
	GetOptions options;
	LoadConfig config;
	Map<String, Patients> patients;
	Combined combined;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Gemini(GetOptions options, LoadConfig config,
			Map<String, Patients> patients, Combined combined) {
		this.options = options;
		this.config = config;
		this.patients = patients;
		this.combined = combined;

		// make log entry
		Log.logger(logger, "Preparing Gemini");


		// prepare outDir
		mkdir();


	} 





	/////////////////////////
	//////// methods ////////
	/////////////////////////


	////////////
	//////// mkdir

	private void mkdir() {
		// create directory for each patient
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getDatabase();
		String cmd = "mkdir -p " + outDir;
		combined.mkdir(cmd);
	}


	// prepare load in database
	public ArrayList<String> load(String vcfFile) {

		// initialize and prepare command
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getDatabase();

		// extract name of vcf file
		String[] file = vcfFile.split(File.separator);
		String name = file[file.length - 1].split("\\.")[0];

		String outDB = outDir + sep + name + ".db";



		// prepare command
		cmd.add(config.getGemini());
		cmd.add("load");
		cmd.add("-t VEP");
		cmd.add("--cores " + options.getCpu());
		cmd.add("-v " + vcfFile);
		cmd.add(outDB);




		// return command
		return cmd;

	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
