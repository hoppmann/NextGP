package de.NextGP.main;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class IlluminaPanel {

	//////////////
	//// Variables
	GetOptions options;
	Map<String, Patients> patients;
	LoadConfig config;
	Combined combined;

	private static Logger logger = LoggerFactory.getLogger(IlluminaPanel.class);


	// run pipeline corresponding to an Illumina Panel

	public IlluminaPanel(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();

		
		// make log entry for starting Illumina pipeline
		Log.logger(logger, "Preparing Illumina batch files.");


		// choose different steps needed to execute pipeline
		GeneralPipeline pipeline = new GeneralPipeline(options, config);
		
		// read input file
		pipeline.readFastqFile();

		
		// check if input is given
		pipeline.checkPatMap();
		
		
		
		// 01 align reads 
		pipeline.align("Illumina");
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		pipeline.bqsr();

		// 05 create metrics
		pipeline.metrices();

		// 06 Variant calling
		pipeline.gatkHapCaller();

		// 07 Variant Filtering
		pipeline.hardFilter();
		
		// 09 annotate
		pipeline.annotate();
		
		// 10  gemini
		pipeline.loadInGemini();

		// save commands
		pipeline.saveCommands();

	}
}
