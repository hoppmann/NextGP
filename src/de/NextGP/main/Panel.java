package de.NextGP.main;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Panel {

	//////////////
	//// Variables
	GetOptions options;
	LoadConfig config;
	Combined combined;



	// run pipeline corresponding to an Illumina Panel

	public Panel(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();


		// make log entry for starting Illumina pipeline
		System.out.println("Preparing Illumina batch files.");


		// choose different steps needed to execute pipeline
		GeneralPipeline pipeline = new GeneralPipeline(options, config);

		// if not bam option chosen start with fastq file
		if (!options.isBam()) {
			// read input file
			pipeline.readFastqList();

			// check if input is given
			pipeline.checkPatMap();

			// prepare bed file
			pipeline.prepareBedFile();

			// pre-process reads
			pipeline.preprocess();
			
			// 01 align reads 
			pipeline.align("Illumina");

			// 02 addReplaceReadgroups
			pipeline.addReplaceReadgroups("Illumina", config.getAlignment());
		
			
			
			
			
			
			
		// start with import of bam list if bam option is chosen
		} else if (options.isBam()) {

			// read input bam list
			pipeline.readBamList(options.getBamList());

		}
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		boolean isSolid = false;
		pipeline.bqsr(isSolid);

		// 05 create metrics
		pipeline.metrices();

		// 06 Variant calling
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
