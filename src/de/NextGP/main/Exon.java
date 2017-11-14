package de.NextGP.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Exon {

	//////////////
	//// Variables
	GetOptions options;
	LoadConfig config;
	Combined combined;

	private static Logger logger = LoggerFactory.getLogger(Exon.class);


	// run pipeline corresponding to an Illumina Panel

	public Exon(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();
		
		// make log entry for starting bam pipeline
		Log.logger(logger, "Preparing bam based PE batch files.");

		// initialize pipeline class to start desired steps
		GeneralPipeline pipeline = new GeneralPipeline(options, config);

		// check for pipeline type (solid or illumina)
		String sequencer;
		if (options.isSolid()) {
			sequencer = "SOLiD";
		} else {
			sequencer = "Illumina";
		}
		
		// if not bam option chosen start with fastq file
		if (!options.isBam()) {
			// read input file
			pipeline.readFastqList();

			// check if input is given
			pipeline.checkPatMap();
	
			// prepare bed file
			pipeline.prepareBedFile();

			// pre-processing with AfterQC
			pipeline.preprocess();
			
			// 01 align reads
			pipeline.align(sequencer);

			// 02 addReplaceReadgroups
			pipeline.addReplaceReadgroups(sequencer, config.getAlignment());
		
		// start with import of bam list if bam option is chosen
		} else if (options.isBam()) {

			// read input bam list
			pipeline.readBamList(options.getBamList());

		}

		
		// prepare bed file
		pipeline.prepareBedFile();
		
		// 02 markDuplicates
		pipeline.markDuplicates();
		pipeline.addReplaceReadgroups("SOLiD", config.getDuplicates());
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		boolean isSolid = options.isSolid();
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
