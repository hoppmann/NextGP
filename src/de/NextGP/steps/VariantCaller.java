package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class VariantCaller {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private static Logger logger = LoggerFactory.getLogger(VariantCaller.class);
	private GetOptions options;
	private LoadConfig config;
	private Map<String, Patients> patients;
	private Combined combined; 
	private int first;
	private int last;




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public VariantCaller(GetOptions options, LoadConfig config, 
			Map<String, Patients> patients, Combined combined)  {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.patients = patients;
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();

		// make log entry
		Log.logger(logger, "Preparing variant calling");

	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// run haplotype caller
	public void runHaplotypeCaller() {


		// run for each patient
		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + "gatk" + sep + "unfiltered";
			String output = outDir + sep + curPat + sep + curPat + ".raw.g.vcf";
			String logfile = outDir + sep + curPat + sep + curPat + ".log";
			String input = patients.get(curPat).getBam();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			// prepare command

			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T HaplotypeCaller");
			cmd.add("-nct " + options.getCpu());
			cmd.add("-l INFO");
			cmd.add("-R " + config.getHg19Fasta());
			cmd.add("-D " + config.getDbsnp());
			cmd.add("-L " + options.getBedFile());
			cmd.add("-ERC GVCF");
			cmd.add("-I " + input);
			cmd.add("-o " + output);
			cmd.add("-log " + logfile);



			// save command
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setHaplotypeCaller(cmd);
			}
			patients.get(curPat).setVcfFiles("gatk", output);
			patients.get(curPat).setLastOutFile(output);

		}
	}


	// run Genotype GVCF
	public void runGenotypeGVCF() {

		// init and gather variables
		ArrayList<String> genotypeGvcfCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + "gatk" + sep + "unfiltered";
		String outFile = outDir + sep + "combined.raw.vcf";
		String logfile = outDir + sep + "GenotypeGVCF.log";

		// prepare command
		genotypeGvcfCmd.add(config.getJava());
		genotypeGvcfCmd.add(options.getXmx());
		genotypeGvcfCmd.add("-jar " + config.getGatk());
		genotypeGvcfCmd.add("-T GenotypeGVCFs");
		genotypeGvcfCmd.add("-nt " + options.getCpu());
		genotypeGvcfCmd.add("-R " + config.getHg19Fasta());
		genotypeGvcfCmd.add("-D " + config.getDbsnp());
		genotypeGvcfCmd.add("-stand_call_conf 30 ");

		for (String curPat : patients.keySet()) {
			genotypeGvcfCmd.add("-V " + patients.get(curPat).getLastOutFile());
		}

		genotypeGvcfCmd.add("-o " + outFile);
		genotypeGvcfCmd.add("-log " + logfile);



		// save command
		if (first <= 6 && last >= 6 ) {
//			combined.setGenotypeGVCF(genotypeGvcfCmd);
			combined.addCmd03(genotypeGvcfCmd);
		}
		combined.setCombinedVcfFile(outFile);
	}


	// run samtools mpileup
	public void runMpileup() {


		// general varialbes and mkdir
		String caller = "samtools";
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller;

		// save mkdir command
		combined.mkdir(outDir );



		// run for each patient

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String fileOut = outDir + sep + curPat + ".vcf";
			String logfile = outDir + sep + curPat + ".log";
			String input = patients.get(curPat).getBam();


			// prepare command
			cmd.add(config.getSamtools() + " mpileup");
			cmd.add("--uncompressed");
			cmd.add("--VCF");
			cmd.add("--fasta-ref " + config.getHg19Fasta());
			cmd.add("--positions " + options.getBedFile());
			cmd.add(input);
			cmd.add("| " + config.getBcftools() + " call ");
			cmd.add("-f GQ -vmO z");
			cmd.add("--output-type v");
			cmd.add("-o " + fileOut);
			cmd.add("> " + logfile);

			// save command 
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setMpileup(cmd);
			}

			// save command
			patients.get(curPat).setVcfFiles("samtools", fileOut);


		}
	}


	// run platypus
	public void runPlatypus() {

		// init object
		VariantFilter filter = new VariantFilter(options, config, combined, "platypus");

		// general varialbes and mkdir
		String caller = "platypus";
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep;

		// save mkdir command
		combined.mkdir(outDir);
		combined.mkdir(outDir + "unfiltered");


		// run for each patient

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep + "unfiltered";
			String output = outDir + sep + curPat + ".vcf";
			String logfile = outDir + sep + curPat + ".log";
			String input = patients.get(curPat).getBam();


			// prepare command
			cmd.add("python " + config.getPlatypus());
			cmd.add("callVariants");
			cmd.add("--bamFiles=" + input);
			cmd.add("--refFile=" + config.getHg19Fasta());
			cmd.add("--regions=" + options.getBedFile());
			cmd.add("--nCPU=" + options.getCpu());
			cmd.add("--mergeClusteredVariants=1");
			cmd.add("--output=" + output);
			cmd.add("> " + logfile);



			// save command
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setPlatypus(cmd);
			}



			// filter variants
			input = output;
			outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller ;
			output = outDir + sep + curPat + ".vcf";

			// prepare command
			ArrayList<String> filterCmd =  filter.removeFiltered(output, input);

			// save command
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setFilterPlatypus(filterCmd);
			}

			// sort VCF file to get rid of contig order error
			String fileIn = output + ".recode.vcf";
			String fileOut = output + "-sorted.vcf";
			String bamFile = patients.get(curPat).getBam();

			ArrayList<String> sortCmd = sortVcf(curPat, fileIn, fileOut, bamFile);

			// save command
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setSortPlatypus(sortCmd);
			}
			patients.get(curPat).setVcfFiles(caller, fileOut);

		}
	}


	// run freebayes
	public void runFreebayes() {

		// general varialbes and mkdir
		String caller = "freebayes";
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller;

		// save mkdir
		combined.mkdir(outDir );


		// run for each patient
		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String fileOut = outDir + sep + curPat + "-presorted.vcf";
			String input = patients.get(curPat).getBam();


			// prepare command
			cmd.add(config.getFreebayes());
			cmd.add("-f " + config.getHg19Fasta());
			cmd.add("-C " + options.getCpu());
			cmd.add("--genotype-qualities");
			cmd.add(input);
			cmd.add("| " + config.getVcfFilter());
			cmd.add("-f \"QUAL > 20\"");
			cmd.add("> " + fileOut);


			// sort VCF file to get rid of contig order error
			String fileIn = fileOut;
			fileOut = outDir + sep + curPat + "-sorted.vcf";
			String bamFile = patients.get(curPat).getBam();

			ArrayList<String> sortCmd = sortVcf(curPat, fileIn, fileOut, bamFile);





			// save commands
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setFreebayes(cmd);
				patients.get(curPat).setSortFreebayes(sortCmd);
			}
			patients.get(curPat).setVcfFiles(caller, fileOut);


		}
	}


	// extract individuals
	public void extractInd() {

		String caller = "gatk";

		for (String curPat : patients.keySet()){
			// gather and initialize variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + caller;
			String fileOut = outDir + sep + curPat;

			// prepare command

			cmd.add(config.getVcfTools());
			cmd.add("--recode");
			cmd.add("--indv " + curPat);
			cmd.add("--vcf " + combined.getCombinedVcfFile());
			cmd.add("--out " + fileOut);

			// store commands
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setExtractInd(cmd);
			}
			patients.get(curPat).setVcfFiles(caller, fileOut + ".recode.vcf");


		}

	}


	// rename sampleNames
	public void renameSampleNames() {
		Picard readgroups = new Picard(config, options, patients);

		// for each used variant caller extract name of that caller an rename sample name accordingly
		for (String curPat : patients.keySet()){


			for (String callerName : patients.get(curPat).getVcfFiles().keySet() ){

				// run replacement of sample name with caller name
				ArrayList<String> cmd = readgroups.replaceSampleName(callerName, curPat);

				if (first <= 6 && last >= 6 ) {
					// for each patient save commands of current caller
					if (callerName.equals("gatk")) {
						patients.get(curPat).setSampleNameModGATK(cmd);
					} else if (callerName.equals("platypus")) {
						patients.get(curPat).setSampleNameModPlatypus(cmd);
					} else if (callerName.equals("freebayes")) {
						patients.get(curPat).setSampleNameModFreebayes(cmd);
					} else if (callerName.equals("samtools")) {
						patients.get(curPat).setSampleNameModSamtools(cmd);
					}
				}
			}
		}
	}


	// merge variants
	public void mergeDifCallerVariants() {

		// general varialbes
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + "preConsensus";
		combined.mkdir(outDir);


		for (String curPat : patients.keySet()) {
			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String output = outDir + sep + curPat + ".vcf";




			// prepare command
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T CombineVariants");
			cmd.add("-R " + config.getHg19Fasta());

			for (String callerName : patients.get(curPat).getVcfFiles().keySet()){
				String input = patients.get(curPat).getVcfFiles().get(callerName);
				cmd.add("--variant " + input);
			}


			cmd.add("-genotypeMergeOptions Unsorted");
			cmd.add("-o " + output);
			//			cmd.add("-U ALLOW_SEQ_DICT_INCOMPATIBILITY");


			// store commands
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setCombineVariants(cmd);
			}
			patients.get(curPat).setLastOutFile(output);
		}
	}


	// consensus call
	public void runConsensus() {

		// general variables
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() ;
		combined.mkdir(outDir);
		
		// for each patient merge files from different caller using GATK CombineVariants
		for (String curPat : patients.keySet()) {

			// init cmd array and general variabels
			ArrayList<String> cmd = new ArrayList<>();
			String output = outDir + sep + curPat + ".vcf";

			
			
			
			// prepare command
			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T CombineVariants");
			cmd.add("-R " + config.getHg19Fasta());

			for (String callerName : patients.get(curPat).getVcfFiles().keySet()){
				String input = patients.get(curPat).getVcfFiles().get(callerName);
				cmd.add("--variant:" + callerName + " " + input);
			}
			cmd.add("--rod_priority_list gatk,freebayes,samtools,platypus");
			cmd.add("--minimumN " + options.getMinConsCall());
			cmd.add("-genotypeMergeOptions PRIORITIZE ");
			cmd.add("--excludeNonVariants ");
			cmd.add("-o " + output);

			
			// store command
			if (first <= 6 && last >= 6 ) {
				patients.get(curPat).setConsensus(cmd);
			}
			patients.get(curPat).setLastOutFile(output);
		}
		
	}


	// merge variants from different patients after consensus calling
	public void mergeConsensusVariants() {

		// init and gather variables
		ArrayList<String> combindeCallinCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling() ;
		String output = outDir + sep + "consensus.vcf";


		// prepare command
		combindeCallinCmd.add(config.getJava());
		combindeCallinCmd.add(options.getXmx());
		combindeCallinCmd.add("-jar " + config.getGatk());
		combindeCallinCmd.add("-T CombineVariants");
		combindeCallinCmd.add("-R " + config.getHg19Fasta());
		for (String curPat : patients.keySet()){

			String input = patients.get(curPat).getLastOutFile();
			combindeCallinCmd.add("--variant " + input);
		}

		combindeCallinCmd.add("-genotypeMergeOptions Unsorted");
		combindeCallinCmd.add("-o " + output);


		// store commands
		if (first <= 6 && last >= 6 ) {
//			combined.setCombineSampleVariants(combindeCallinCmd);
			combined.addCmd05(combindeCallinCmd);
		}
		combined.setLastOutFile(output);
		
		// save vcf-file for later annotation with alamut
		combined.setVcfForAnnotation(output);



	}


	// sort vcf-file
	public ArrayList<String> sortVcf(String curPat, String fileIn, String fileOut, String bamFile) {

		// gater and initialize variables
		ArrayList<String> cmd = new ArrayList<>();


		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getPicard());
		cmd.add("SortVcf");
		cmd.add("I=" + fileIn);
		cmd.add("O=" + fileOut);
		cmd.add("SEQUENCE_DICTIONARY=" + bamFile);


		// return ready command
		return cmd;

	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}