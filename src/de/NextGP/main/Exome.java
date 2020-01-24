package de.NextGP.main;

import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Exome {

	//////////////
	//// Variables
	private GetOptions options;
	private LoadConfig config;
	private GeneralPipeline pipeline;


	// run pipeline corresponding to an Illumina Panel

	public Exome(GetOptions options, LoadConfig config) {

		System.out.println( "Preparing exon based pipeline");
		
		//prepare and initialize variables
		this.options = options;
		this.config = config;
		
		// initialize pipeline class to start desired steps
		pipeline = new GeneralPipeline(options, config);

		
		// check for pipeline type (solid or illumina)
				String sequencer;
				if (options.isSolid()) {
					sequencer = "SOLiD";
				} else {
					sequencer = "Illumina";
				}
		
		if (options.isFastq()) {
			
			startingWithFastq(sequencer);
			runBamToVcf(sequencer);
			runVcfTillEnd();
			
			
		} else if (options.isBam()) {
			
			startingWithBam();
			runBamToVcf(sequencer);
			runVcfTillEnd();
			
			
		} else if (options.isVcf()) {
			
			
			
			startingWithVcf();
			runVcfTillEnd();
			
		}
			
		
		
				
		

	}
	
	
	
	
	private void startingWithFastq (String sequencer) {

		

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

	}

	
	
	private void startingWithBam () {

		// read input bam list
		pipeline.readBamList(options.getBamList());

		// prepare bed file
		pipeline.prepareBedFile();
		
	}
	
	
	private void runBamToVcf (String sequencer) {
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

	}

	
	
	
	
	
	private void startingWithVcf () {
		
		pipeline.readVcfList(options.getVcfList());
		pipeline.mergeVcf();
		
		
	}

	
	
	
	
	
	private void runVcfTillEnd () {
		// 09 annotate
				pipeline.annotate();

				// 10  gemini
				pipeline.loadInGemini();
				
				// 11 annotate with hgmd and alamut
				pipeline.annotateHGMD();

				// 12 filter Gemini variants
				pipeline.filterGemini();
				
				// save commands
				pipeline.saveCommands();
	}
	
	
	

}
