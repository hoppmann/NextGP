package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class ExtractIndividuals {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static Logger logger = LoggerFactory.getLogger(ExtractIndividuals.class);
	private GetOptions options;
	private LoadConfig config;
	private Map<String, Patients> patients;
	private Combined combined;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ExtractIndividuals(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined) {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.patients = patients;
		this.combined = combined;


	}






	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void extractInd() {


		for (String curPat : patients.keySet()){
			// gather and initialize variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getExtractInd();
			String outFile = outDir + sep + curPat;
			
			// prepare command

			cmd.add(config.getVcfTools());
			cmd.add("--recode");
			cmd.add("--indv " + curPat);
			cmd.add("--vcf" + combined.getLastOutFile());
			cmd.add("--out " + outFile);


			// store command
			combined.setExtractInd(cmd);


		}
	}






	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////







}
