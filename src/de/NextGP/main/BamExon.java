package de.NextGP.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class BamExon {

	//////////////
	//// Variables
	GetOptions options;
	LoadConfig config;
	Combined combined;

	private static Logger logger = LoggerFactory.getLogger(BamExon.class);


	// run pipeline corresponding to an Illumina Panel

	public BamExon(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();
		
		// make log entry for starting bam pipeline
		Log.logger(logger, "Preparing bam based PE batch files.");

		// initialize pipeline class to start desired steps
		GeneralPipeline pipeline = new GeneralPipeline(options, config);

		// read input bam list
		pipeline.readBamList(options.getBamList());

		// prepare bed file
		pipeline.prepareBedFile();
		
		// 02 markDuplicates
		pipeline.markDuplicates();
		pipeline.addReplaceReadgroups("SOLiD", config.getDuplicates());
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		boolean isSolid = true;
		pipeline.bqsr(isSolid);

		// 05 create metrics
		pipeline.metrices();

		// 06 variant calling using hard filtering
		pipeline.panelVariantCalling();
		
		// 09 annotate
		pipeline.annotate();

		// 10  gemini
		pipeline.loadInGemini();

		// save commands
		pipeline.saveCommands();

	}


}
