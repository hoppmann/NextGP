package de.NextGP.main;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.SlurmWriter;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.ReadInputFile;
import de.NextGP.initialize.options.GetOptions;
import de.NextGP.steps.Annotate;
import de.NextGP.steps.BQSR;
import de.NextGP.steps.BWA;
import de.NextGP.steps.CreateMetrices;
import de.NextGP.steps.Gemini;
import de.NextGP.steps.Picard;
import de.NextGP.steps.PrepareBedFile;
import de.NextGP.steps.Preprocess;
import de.NextGP.steps.Realigner;
import de.NextGP.steps.SamToBam;
import de.NextGP.steps.VariantCaller;
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

		// gather and init variables
		this.options = options;
		this.config = config;
		combined = new Combined();
		patients = new HashMap <>();

	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////

	//////////////////
	//////// check existence and correctness of patients map

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

	////////////////
	//////// read in bam list and save in patient map
	
	public Map<String, Patients> readBamList(String bamList) {
		
		patients = new ReadInputFile().readBamList(bamList);
		return patients;
		
	}

	///////////////
	//////// prepare bed file ////////
	public void prepareBedFile() {
		PrepareBedFile prepare = new PrepareBedFile(options);
		prepare.createBed();
	}
		
	
	
	///////////////////
	//////// run pre processing steps
	public void preprocess() {

		Map<String, Patients> fastqList = readFastqList();
		Preprocess pre = new Preprocess(options, fastqList);
		pre.afterQc();
		
	}
	
	
	
	
	
	////////////////
	//////// run alignment steps
	public void align(String platform) {

		////// BWA
		// align sequences using BWA
		BWA bwa = new BWA(config, patients, options, combined);
		if (combined.isMatePair()){
			bwa.pairedEnd(platform);
		} else {
			bwa.singelEnd(platform);

		}


		///// sort sam 
		// sort sam and convert to bam using picard
		new SamToBam(options, config, patients, combined);


	}

	
	//////////////////
	//////// add read groups
	public void addReplaceReadgroups(String platform, String outDir) {
	
		Picard picard = new Picard(config, options, patients);
		picard.addReadgroups(platform, outDir);

	}
	
	//////////////////////////
	//////// run picard mark duplicates
	public void markDuplicates() {
		
		Picard picard = new Picard(config, options, patients, combined);
		picard.markDuplicates();
	
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

	public void bqsr(boolean isSolid) {

		BQSR bqsr = new BQSR(patients, config, options, isSolid);

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
		metrics.runFastQC();
		metrics.meanDepth();
		metrics.asMetric();
		metrics.gcbMetric();
		metrics.isMetric();
		metrics.coverage();

	}


	///////////////
	//////// run variant caller 

	public void panelVariantCalling() {
		
		////// run GATK Haplotype caller 
		VariantCaller caller = new VariantCaller(options, config, patients, combined);
		VariantFilter filter = new VariantFilter(options, config, combined);
		
		
		// call variants using Haplotype caller
		caller.runHaplotypeCaller();
		caller.runGenotypeGVCF();
		
		// filter variants called by Haplotype caller
		filter.hardFiltering();

		// call variants using samtools
		caller.runMpileup();
		
		// call variants using platypus
		caller.runPlatypus();
		
		// call variants using freebayes
		caller.runFreebayes();
		
		// extract individuals for later consensus calling
		caller.extractInd();
		
		// get consensus call
		caller.runConsensus();
		
		// merge all samples
		caller.mergeConsensusVariants();
		
		
		
	}
	
	//////////////////
	//////// annotate Variants

	public void annotate() {

		// get VCF to annotate
		String vcfFile = combined.getLastOutFile();
		if (vcfFile == null || vcfFile.isEmpty()) {

			// get general vcf-file prior steps are scipped
			String sep = File.separator;
			vcfFile = options.getOutDir() + sep + config.getVariantCalling() + sep + "consensus.vcf";
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

//		ArrayList<String> cmd = new ArrayList<>();
		new Gemini(options, config, patients, combined).load(vcfFile);
//		combined.setGeminiLoad(cmd);

	}



	////////////////
	//////// save commands

	public void saveCommands() {

		// save commands to slurm batch files
		SlurmWriter slurm = new SlurmWriter(patients, combined, options);
		slurm.generateFiles();
	}








	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
