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


		// make log entry for starting IonProton pipeline
		Log.logger(logger, "Preparing IonProton batch files.");

		// initialize pipeline class to start desired steps
		GeneralPipeline pipeline = new GeneralPipeline(options, config);

		// 01 read input bam list
		pipeline.readBamList(options.getBamList());

		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		pipeline.bqsr();

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



	//	// read input file
	//	private void readInFile(){
	//		String bam = options.getBamList();
	//		// check if fastq list file exists then read in 
	//		if (new File(bam).exists()){
	//			patients = new ReadInputFile().readBamList(bam);
	//
	//		}
	//
	//	}
	//
	//
	//	// run realignment
	//	private void indelRealiger(){
	//
	//		new Realigner(patients, config, options);
	//
	//	}
	//
	//	// run BQSR
	//	private void bqsr() {
	//
	//		//////// BQSR
	//		new BQSR(patients, config, options);
	//	}
	//
	//	// run Metrices
	//	private void metrices() {
	//
	//		CreateMetrices metrics = new CreateMetrices(options, config, patients, combined);
	//		metrics.meanDepth();
	//		metrics.asMetric();
	//		metrics.gcbMetric();
	//		metrics.isMetric();
	//		metrics.coverage();
	//
	//	}
	//
	//	// run variant caller
	//	private void caller() {
	//
	//		VariantCaller caller = new VariantCaller(options, config, patients);
	//		caller.runHaplotypeCaller();
	//		caller.runGenotypeGVCF(combined);
	//
	//	}
	//
	//	// run hard filter
	//	private void hardFilter(){
	//		new VariantFilter(options, config, combined).hardFiltering();
	//		new VariantEval(options, config, combined, patients);
	//	}
	//
	//	// annotate variants
	//	private void annotateVar(){
	//		
	//		String vcfFile = combined.getLastOutFile();
	//		ArrayList<String> cmd = new ArrayList<>();
	//		// get vt master command
	//		Annotate annotate = new Annotate(options, config, patients, combined);
	//		cmd = annotate.vtMaster();
	//		
	//		// get vep command
	//		cmd = annotate.vep();
	//	}
	//	
	//	
	//	// load in gemini
	//	private void gemini(){
	//		// initialize and gather variables
	//		String vcfFile = combined.getLastOutFile();
	//		ArrayList<String> cmd = new ArrayList<>();
	//		cmd = new Gemini(options, config, patients, combined).load(vcfFile);
	//		combined.setGeminiLoad(cmd);
	//	}
	//	
	//	
	//	
	//	
	//	
	//	
	//
	//	// save command collection corresponding to patient
	//	private void saveCommand(){
	//
	//		// create directory for master dir 
	//		String slurmDir = "slurm";
	//		new File(slurmDir).mkdirs();
	//				
	//		//// prepare names for out files
	//		String sep = File.separator;
	//		String masterScript = slurmDir + sep + "01-master.sh";
	//		String combFileOut = slurmDir + sep + "02-combinded.sh";
	//
	//		
	//		/////////////
	//		//////// create master script
	//		Writer master = new Writer();
	//		master.openWriter(masterScript);
	//		
	//		// create header in master script
	//		master.writeLine("#!/bin/bash");
	//				
	//		// write directives in master script
	//		master.writeLine("#SBATCH --cpus-per-task=" + options.getCpu());
	//
	//		// create folders
	//		for (String dir : combined.getMkDirs()) {
	//			master.writeLine(dir);
	//		}
	//		
	//		// file to prepare wait command for execution of combined steps after
	//		// single steps are done
	//		String waitCommand = "\nsbatch -p genepi -d afterok"; 
	//
	//		
	//		
	//		///////////////////
	//		//////// create batch file for each patients 
	//		// for each patient create batch file containing patient specific commands
	//		for (String curPat : patients.keySet()){
	//			
	//			String outFile = slurmDir + sep + curPat + ".sh";
	//			Writer batch = new Writer();
	//			batch.openWriter(outFile);
	//
	//			
	//			// write bash execution in master file and prepare wait command
	//			master.writeLine(curPat + "=$(sbatch -p genepi " + outFile + ")");
	//			master.writeLine(curPat + "=$(echo $" + curPat + " | sed 's/Submitted batch job //')");
	//			
	//			waitCommand += ":$" + curPat;
	//			
	//			// write first lien of bash file
	//			batch.writeLine("#!/bin/bash");
	//			
	//			// indel realign
	//			batch.writeCmd(patients.get(curPat).getRealignmentTargetCreator());
	//			batch.writeCmd(patients.get(curPat).getIndelRealigner());
	//			
	//			// BQSR
	//			batch.writeCmd(patients.get(curPat).getBaseRecalibration_pre());
	//			batch.writeCmd(patients.get(curPat).getBaseRecalibration_post());
	//			batch.writeCmd(patients.get(curPat).getAnalyzeCovariates());
	//			batch.writeCmd(patients.get(curPat).getPrintReads());
	//			
	//			// metrices
	//			batch.writeCmd(patients.get(curPat).getStatsMeanDepth());
	//			batch.writeCmd(patients.get(curPat).getStatsAsMetric());
	//			batch.writeCmd(patients.get(curPat).getStatsGCBMetric());
	//			batch.writeCmd(patients.get(curPat).getStatsISMetric());
	//			batch.writeCmd(patients.get(curPat).getStatsCoverage());
	//			
	//			// variant calling
	//			batch.writeCmd(patients.get(curPat).getHaplotypeCaller());
	//			
	//			// close writer
	//			batch.close();
	//		}
	//		
	//		
	//		
	//		///////////////////
	//		//////// finish master batch
	//		// write wait command in master file
	//		master.writeLine(waitCommand + " " + combFileOut);
	//		
	//		// close master file
	//		master.close();
	//
	//		
	//		
	//		
	//		
	//		
	//		
	//		/////////////////////
	//		//////// create batch file for combined tasks
	//		Writer comb = new Writer();
	//		comb.openWriter(combFileOut);
	//
	//		
	//		// add first line to combined commands
	//		comb.writeLine("#!/bin/bash");
	//
	//		
	//		// write commands in master script needed for all together 
	//		comb.writeCmd(combined.getGenotypeGVCF());
	//
	//		// hard filtering
	//		comb.writeCmd(combined.getExtractSnpSet());
	//		comb.writeCmd(combined.getExtractIndelSet());
	//		comb.writeCmd(combined.getHardFiterSnpSet());
	//		comb.writeCmd(combined.getHardFilterIndelSet());
	//		comb.writeCmd(combined.getCombineVariants());
	//		comb.writeCmd(combined.getRemoveFilteredReads());
	//		comb.writeCmd(combined.getVarEval());
	//		
	//		// VEP annotation
	//		comb.writeCmd(combined.getVtMaster());
	//		comb.writeCmd(combined.getVepAnnotation());
	//		
	//		// load in gemini
	//		comb.writeCmd(combined.getGeminiLoad());
	//		
	//		// close open writer
	//		comb.close();
	//
	//	}



}
