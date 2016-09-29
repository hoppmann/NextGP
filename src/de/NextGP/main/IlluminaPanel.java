package de.NextGP.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.general.Writer;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.ReadInputFile;
import de.NextGP.initialize.options.GetOptions;
import de.NextGP.steps.AddReaplaceReadgroups;
import de.NextGP.steps.Annotate;
import de.NextGP.steps.BQSR;
import de.NextGP.steps.BWA;
import de.NextGP.steps.CreateMetrices;
import de.NextGP.steps.Gemini;
import de.NextGP.steps.Realigner;
import de.NextGP.steps.SamToBam;
import de.NextGP.steps.VariantCaller;
import de.NextGP.steps.VariantEval;
import de.NextGP.steps.VariantFilter;

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


		// read input file
		Log.logger(logger, "Reading input file");
		readInFile();

		// 01 align reads 
		align();

		// 03 realign
		indelRealiger();

		// 04 BQSR
		bqsr();

		// 05 create metrics
		Log.logger(logger, "Prepare metrices");
		metrices();

		// 06 Variant calling
		Log.logger(logger, "preparing variant calling");
		caller();

		// 07 Variant Filtering
		Log.logger(logger, "prepare variant filtering");
		hardFilter();
		
		// 09 annotate
		annotateVar();
		
		
		// 10  gemini
		gemini();

		// save commands
		saveCommand();

	}



	// read input file
	private void readInFile(){
		String fastq = options.getFastqList();
		// check if fastq list file exists then read in 
		if (new File(fastq).exists()){
			patients = new ReadInputFile().readMateFastqFile(fastq);

		}

	}

	// run alignment steps
	private void align(){

		//////// BWA
		// align sequences using BWA
		BWA bwa = new BWA(config, patients, options, combined);
		bwa.pairedEndMateFileCmd("illumina");

		/////// sort sam 
		// sort sam and convert to bam using picard
		new SamToBam(options, config, patients, combined);

		//////// add read groups
		new AddReaplaceReadgroups(config, options, patients, "illumina");

	}

	// run realignment
	private void indelRealiger(){

		new Realigner(patients, config, options);

	}

	// run BQSR
	private void bqsr() {

		//////// BQSR
		new BQSR(patients, config, options);
	}

	// run Metrices
	private void metrices() {

		CreateMetrices metrics = new CreateMetrices(options, config, patients, combined);
		metrics.meanDepth();
		metrics.asMetric();
		metrics.gcbMetric();
		metrics.isMetric();
		metrics.coverage();

	}

	// run variant caller
	private void caller() {

		VariantCaller caller = new VariantCaller(options, config, patients);
		caller.runHaplotypeCaller();
		caller.runGenotypeGVCF(combined);

	}

	// run hard filter
	private void hardFilter(){
		new VariantFilter(options, config, combined).hardFiltering();
		new VariantEval(options, config, combined, patients);
	}

	// annotate variants
	private void annotateVar(){
		
		String vcfFile = combined.getLastOutFile();
		ArrayList<String> cmd = new ArrayList<>();
		// get vt master command
		Annotate annotate = new Annotate(options, config, patients, combined);
		cmd = annotate.vtMaster(vcfFile);
		combined.setVtMaster(cmd);
		
		// get vep command
		cmd = annotate.vep(vcfFile);
		combined.setVepAnnotation(cmd);
		combined.setLastOutFile(annotate.getOutVEP());
	}
	
	
	// load in gemini
	private void gemini(){
		// initialize and gather variables
		String vcfFile = combined.getLastOutFile();
		ArrayList<String> cmd = new ArrayList<>();
		cmd = new Gemini(options, config, patients, combined).load(vcfFile);
		combined.setGeminiLoad(cmd);
	}
	
	
	
	
	
	

	// save command collection corresponding to patient
	private void saveCommand(){

		// create directory for master dir 
		String slurmDir = "slurm";
		new File(slurmDir).mkdirs();
				
		//// prepare names for out files
		String sep = File.separator;
		String masterScript = slurmDir + sep + "01-master.sh";
		String combFileOut = slurmDir + sep + "02-combinded.sh";

		
		/////////////
		//////// create master script
		Writer master = new Writer();
		master.openWriter(masterScript);
		
		// create header in master script
		master.writeLine("#!/bin/bash");
				

		// create folders
		for (String dir : combined.getMkDirs()) {
			master.writeLine(dir);
		}
		
		// file to prepare wait command for execution of combined steps after
		// single steps are done
		String waitCommand = "\nsbatch -p genepi -d afterok"; 

		
		
		///////////////////
		//////// create batch file for each patients 
		// for each patient create batch file containing patient specific commands
		for (String curPat : patients.keySet()){
			
			String outFile = slurmDir + sep + curPat + ".sh";
			Writer batch = new Writer();
			batch.openWriter(outFile);

			
			// write bash execution in master file and prepare wait command
			String curPatVar = "Pat_" + curPat;
			master.writeLine(curPatVar + "=$(sbatch -p genepi " + outFile + ")");
			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
			
			waitCommand += ":$" + curPatVar;
			
			// write first lien of bash file
			batch.writeLine("#!/bin/bash");
			
			// write directives in batch files
			batch.writeLine("#SBATCH --cpus-per-task=" + options.getCpu());

			
			// alignment
			batch.writeCmd(patients.get(curPat).getBwa());
			batch.writeCmd(patients.get(curPat).getSamToBam());
			batch.writeCmd(patients.get(curPat).getAddOrReplaceReadgroups());

			// indel realign
			batch.writeCmd(patients.get(curPat).getRealignmentTargetCreator());
			batch.writeCmd(patients.get(curPat).getIndelRealigner());
			
			// BQSR
			batch.writeCmd(patients.get(curPat).getBaseRecalibration_pre());
			batch.writeCmd(patients.get(curPat).getBaseRecalibration_post());
			batch.writeCmd(patients.get(curPat).getAnalyzeCovariates());
			batch.writeCmd(patients.get(curPat).getPrintReads());
			
			// metrices
			batch.writeCmd(patients.get(curPat).getStatsMeanDepth());
			batch.writeCmd(patients.get(curPat).getStatsAsMetric());
			batch.writeCmd(patients.get(curPat).getStatsGCBMetric());
			batch.writeCmd(patients.get(curPat).getStatsISMetric());
			batch.writeCmd(patients.get(curPat).getStatsCoverage());
			
			// variant calling
			batch.writeCmd(patients.get(curPat).getHaplotypeCaller());
			
			// close writer
			batch.close();
		}
		
		
		
		///////////////////
		//////// finish master batch
		// write wait command in master file
		master.writeLine(waitCommand + " " + combFileOut);
		
		// close master file
		master.close();

		
		
		
		
		
		
		/////////////////////
		//////// create batch file for combined tasks
		Writer comb = new Writer();
		comb.openWriter(combFileOut);

		
		// add first line to combined commands
		comb.writeLine("#!/bin/bash");

		
		// write commands in master script needed for all together 
		comb.writeCmd(combined.getGenotypeGVCF());

		// hard filtering
		comb.writeCmd(combined.getExtractSnpSet());
		comb.writeCmd(combined.getExtractIndelSet());
		comb.writeCmd(combined.getHardFiterSnpSet());
		comb.writeCmd(combined.getHardFilterIndelSet());
		comb.writeCmd(combined.getCombineVariants());
		comb.writeCmd(combined.getRemoveFilteredReads());
		comb.writeCmd(combined.getVarEval());
		
		// VEP annotation
		comb.writeCmd(combined.getVtMaster());
		comb.writeCmd(combined.getVepAnnotation());
		
		// load in gemini
		comb.writeCmd(combined.getGeminiLoad());
		
		// close open writer
		comb.close();

	}



}
