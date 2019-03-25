package de.NextGP.main;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Exome {

	//////////////
	//// Variables
	GetOptions options;
	LoadConfig config;
	Combined combined;


	// run pipeline corresponding to an Illumina Panel

	public Exome(GetOptions options, LoadConfig config) {

		Log.logger( "Preparing exon based pipeline");
		
		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();
		
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

			
		// start with import of bam list if bam option is chosen
		} else if (options.isBam()) {

			// read input bam list
			pipeline.readBamList(options.getBamList());
	
			// prepare bed file
			pipeline.prepareBedFile();

		}
		
		// 02 markDuplicates
		pipeline.markDuplicates();
		pipeline.addReplaceReadgroups(sequencer, config.getDuplicates());
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		pipeline.bqsr(options.isSolid());

		// 05 create metrics
		pipeline.metrices();

		// 06 variant calling using hard filtering
		pipeline.panelVariantCalling();
		
		// 09 annotate
		pipeline.annotate();

		// 10  gemini
		pipeline.loadInGemini();
		
		// 11 annotate with hgmd and alamut
		pipeline.annotateHGMD();

		// save commands
		pipeline.saveCommands();

	}


}
