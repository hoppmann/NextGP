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

public class Gemini {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static Logger logger = LoggerFactory.getLogger(Gemini.class);
	private GetOptions options;
	private LoadConfig config;
	private Combined combined;
	private int first;
	private int last;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Gemini(GetOptions options, LoadConfig config,
			Map<String, Patients> patients, Combined combined) {
		this.options = options;
		this.config = config;
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();
		
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

		combined.mkdir(outDir);
		
		String filterOutDir = options.getOutDir() + sep + config.getFilter();
		combined.mkdir(filterOutDir);
		
	}


	// prepare load in database
	public void load(String vcfFile) {

		// initialize and prepare command
		ArrayList<String> geminiCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getDatabase();

		// extract name of vcf file
		String[] file = vcfFile.split(File.separator);
		String name = file[file.length - 1].split("\\.")[0];

		String outDB = outDir + sep + name + ".db";


		
		// prepare command
		geminiCmd.add(config.getGemini());
		geminiCmd.add("load");
		geminiCmd.add("-t all");
		geminiCmd.add("--cores " + options.getCpu());
		geminiCmd.add("-v " + vcfFile);
		geminiCmd.add(outDB);


		// save dbName for later use when updating the database
		combined.setDbName(outDB);
		
		// return command
		Integer step = options.getSteps().get("gemini");

		if (first <= step && last >= step ) {
			combined.addCmd05(geminiCmd);
		}
		
		
		// prepare command
		ArrayList<String> chmodCmd = new ArrayList<>();

		chmodCmd.add("chmod 755 " + outDB);
		if (first <= step && last >= step ) {
			combined.addCmd05(chmodCmd);
		}
		
		
		
		
	}

	public void chmodDB(String outDB) {
		
		
		
		
	}




	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
