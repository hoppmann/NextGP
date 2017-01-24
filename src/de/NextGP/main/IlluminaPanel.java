package de.NextGP.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class IlluminaPanel {

	//////////////
	//// Variables
	GetOptions options;
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
		pipeline.readFastqList();

		// check if input is given
		pipeline.checkPatMap();
		
		// prepare bed file
		pipeline.prepareBedFile();
		
		// 01 align reads 
		pipeline.align("Illumina");
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		pipeline.bqsr();

		// 05 create metrics
		pipeline.metrices();

		// 06 Variant calling
		pipeline.panelVariantCalling();

		// 07 Variant Filtering
//		pipeline.hardFilter();
		
		// 09 annotate
		pipeline.annotate();
		
		// 10  gemini
		pipeline.loadInGemini();

		// save commands
		pipeline.saveCommands();

	}
}
