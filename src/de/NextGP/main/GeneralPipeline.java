package de.NextGP.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.general.SlurmWriter;
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


public class GeneralPipeline {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetOptions options;
	LoadConfig config;
	Combined combined;
	Map<String, Patients> patients = new HashMap<>(); 
	private static Logger logger = LoggerFactory.getLogger(GeneralPipeline.class);




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public GeneralPipeline (GetOptions options, LoadConfig config) {

		// gather and inti variables
		this.options = options;
		this.config = config;
		combined = new Combined();
		patients = new HashMap <>();

	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////

	//////////////////
	//////// check existance and corectness of patients map

	public void checkPatMap() {

		if (patients.isEmpty()) {
			Log.screen("No patients in List. Did you choose input bam/fastq files.");
			System.exit(3);
		}

	}




	////////////////
	//////// read in file containing list of fastq files

	public Map<String, Patients> readFastqList() {

		String fastqList = options.getFastqList();

		// check if fastq list option is chosen
		if (fastqList == null || fastqList.isEmpty()) {
			Log.error(logger, "No fastq input list given.");
			System.exit(1);
			
		}
		
		// check if fastq list file exists then read in 
		if (new File(fastqList).exists()){

			// check if single end or mate pair list
			// split fastqList file, if 2 entries = single ended, if 3 entries = mate pair

			// open file
			LinkedList<String> lines = new ReadInputFile().openFile(fastqList);
			ListIterator<String> iterator = lines.listIterator();

			// get first non comment line
			while (iterator.hasNext()) {
				// check if line starts with # or is empty
				String curLine = iterator.next();

				if (curLine.isEmpty() || curLine.startsWith("#")) {
					continue;
				}

				// check for number of columns
				String[] splitLine = curLine.split("\t");

				// run as single ended reads
				if (splitLine.length == 2) {
					Log.screen("Running single ended mode.");
					patients = new ReadInputFile().readSingleFastqFile(fastqList);
					combined.setMatePair(false);


					// run as paired end reads
				} else if ( splitLine.length == 3 ) {
					Log.screen("Running in paird end mode.");
					patients = new ReadInputFile().readMateFastqFile(fastqList);
					combined.setMatePair(true);

					// print out that there is an error with the list file
				} else {
					logger.error(fastqList + " incorrect!");
					System.exit(1);
				}

				// end while after first non empty non comment line
				break;


			}

			// print out that fastq list file does not exist and stop program
		} else {
			logger.error(fastqList + " does not exist!");
			System.exit(3);
		}

		return patients;

	}

	//////// read in bam list and save in patient map
	
	public Map<String, Patients> readBamList(String bamList) {
		
		patients = new ReadInputFile().readBamList(bamList);
		return patients;
		
	}


	////////////////
	//////// run alignment steps

	public void align(String platform) {

		////// BWA
		// align sequences using BWA
		BWA bwa = new BWA(config, patients, options, combined);
		if (combined.isMatePair()){
			bwa.matePairCommand(platform);
		} else {
			bwa.singelEndedCommand(platform);

		}


		///// sort sam 
		// sort sam and convert to bam using picard
		new SamToBam(options, config, patients, combined);

		//////// add read groups
		new AddReaplaceReadgroups(config, options, patients, platform);


	}



	/////////////
	//////// run indel realignment

	public void indelRealigner () {

		Realigner realigner = new Realigner(patients, config, options);

		for (String curPat : patients.keySet()){

			realigner.targetCreator(curPat);
			realigner.realigner(curPat);
		}
	}


	////////////
	//////// run BQSR

	public void bqsr() {

		BQSR bqsr = new BQSR(patients, config, options);

		// for each patient run all four steps of BQSR

		for (String curPat : patients.keySet()) {
			bqsr.baseRecal(curPat);
			bqsr.baseRecalStep2(curPat);
			bqsr.analyzeCovar(curPat);
			bqsr.printReads(curPat);
		}

	}


	///////////////
	//////// run metrices

	public void metrices() {

		CreateMetrices metrics = new CreateMetrices(options, config, patients, combined);
		metrics.meanDepth();
		metrics.asMetric();
		metrics.gcbMetric();
		metrics.isMetric();
		metrics.coverage();

	}


	///////////////
	//////// run variant caller 

	// GATK haplotype caller
	public void gatkHapCaller() {
		VariantCaller caller = new VariantCaller(options, config, patients);
		caller.runHaplotypeCaller();
		caller.runGenotypeGVCF(combined);
	}


	/////////////////
	//////// hard filtering

	public void hardFilter() {
		VariantFilter filter = new VariantFilter(options, config, combined);
		filter.hardFiltering();

		new VariantEval(options, config, combined, patients);

	}


	//////////////////
	//////// annotate Variants

	public void annotate() {

		// get VCF to annotate
		String vcfFile = combined.getLastOutFile();
		if (vcfFile == null || vcfFile.isEmpty()) {

			// get general vcf-file prior steps are scipped
			String sep = File.separator;
			vcfFile = options.getOutDir() + sep + config.getVariantFiltering() + sep + "combined_filtered.recode.vcf";
		}



		// get vt master command
		Annotate annotate = new Annotate(options, config, patients, combined);
		annotate.vtMaster(vcfFile);

		// get vep command
		annotate.vep(vcfFile);
		
		// get snpEff command
		annotate.snpEff();
		
		// bgzip file
		annotate.bgzip();
		
		// tabix file
		annotate.tabix();
		

	}



	//////// load in gemini database
	public void loadInGemini(){
		// initialize and gather variables
		String vcfFile = combined.getLastOutFile();

		// if no file transmitted use general file name
		if (vcfFile == null || vcfFile.isEmpty()) {
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getAnnotation();

			vcfFile = outDir + sep + "combined_filtered.vcf";
		}

		ArrayList<String> cmd = new ArrayList<>();
		cmd = new Gemini(options, config, patients, combined).load(vcfFile);
		combined.setGeminiLoad(cmd);

	}



	////////////////
	//////// save commands

	public void saveCommands() {

		// save commands to slurm batch files
		SlurmWriter slurm = new SlurmWriter(patients, combined);
		slurm.generateFiles();
	}





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
	//			String curPatVar = "Pat_" + curPat;
	//			master.writeLine(curPatVar + "=$(sbatch -p genepi " + outFile + ")");
	//			master.writeLine( curPatVar + "=$(echo $" + curPatVar + " | sed 's/Submitted batch job //')");
	//			
	//			waitCommand += ":$" + curPatVar;
	//			
	//			// write first lien of bash file
	//			batch.writeLine("#!/bin/bash");
	//			
	//			// write directives in batch files
	//			batch.writeLine("#SBATCH --cpus-per-task=" + options.getCpu());
	//
	//			
	//			// alignment
	//			batch.writeCmd(patients.get(curPat).getBwa());
	//			batch.writeCmd(patients.get(curPat).getSamToBam());
	//			batch.writeCmd(patients.get(curPat).getAddOrReplaceReadgroups());
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
	//
	//	
	//	
	//	
	//		
	//	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}