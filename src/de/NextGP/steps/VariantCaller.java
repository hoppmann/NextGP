package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class VariantCaller {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetOptions options;
	private LoadConfig config;
	private Map<String, Patients> patients;
	private Combined combined; 
	private int first;
	private int last;
	private String[] caller = {"gatk", "freebayes", "platypus", "samtools"};
	private String tmpDir;




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
		this.tmpDir = options.getTempDir() + File.separator + options.getOutDir();

		// make log entry
		Log.logger( "Preparing variant calling");

		// prepare out directories
		makeDirs();
		
	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	
	public void makeDirs() {
		
		for (String curCaller : caller) {
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getVariantCalling() + sep + curCaller;
			combined.mkdir(outDir);
		}
		
		
	}
	
	
	
	
	
	
	
	// run haplotype caller
	public void runHaplotypeCaller() {


		// run for each patient
		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = tmpDir + sep + config.getVariantCalling() + sep + "gatk" + sep + "unfiltered";
			String output = outDir + sep + curPat + sep + curPat + ".raw.g.vcf";
			String logfile = outDir + sep + curPat + sep + curPat + ".log";
			String input = patients.get(curPat).getBam();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			
			
			
			
			// prepare command

			cmd.add(config.getJava());
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
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
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
		String outDir = tmpDir + sep + config.getVariantCalling() + sep + "gatk" + sep + "unfiltered";
		String outFile = outDir + sep + "combined.raw.vcf";
		String logfile = outDir + sep + "GenotypeGVCF.log";

		// prepare command
		genotypeGvcfCmd.add(config.getJava());
		genotypeGvcfCmd.add("-jar " + config.getGatk());
		genotypeGvcfCmd.add("-T GenotypeGVCFs");
		
		/* --nt excluded for producing an error of to many open file handles
		 * runs rather quick anyways
		 */
		// genotypeGvcfCmd.add("-nt " + options.getCpu());
		genotypeGvcfCmd.add("-R " + config.getHg19Fasta());
		genotypeGvcfCmd.add("-D " + config.getDbsnp());
		genotypeGvcfCmd.add("-stand_call_conf 30 ");

		for (String curPat : patients.keySet()) {
			genotypeGvcfCmd.add("-V " + patients.get(curPat).getLastOutFile());
		}

		genotypeGvcfCmd.add("-o " + outFile);
		genotypeGvcfCmd.add("-log " + logfile);



		// save command
		Integer step = options.getSteps().get("calling");
		
		if (first <= step && last >= step ) {
			combined.addCmd03(genotypeGvcfCmd);
		}
		
		combined.setCombinedVcfFile(outFile);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

	// run samtools mpileup
	public void runMpileup() {


		// general varialbes and mkdir
		String caller = "samtools";
		String sep = File.separator;
		String outDir = tmpDir + sep + config.getVariantCalling() + sep + caller;

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
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}

			
			
			//////// normalize using vt
			input = fileOut;
			fileOut = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep + curPat + ".vcf";

			
			ArrayList<String> vtCmd = normalize(input, fileOut);
			
			
			// save command 
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(vtCmd);
			}

			// store vcf name for later use
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
		String outDir = tmpDir + sep + config.getVariantCalling() + sep + caller;
		String outDirUnfilt = outDir + sep + "unfiltered";
		
		// save mkdir command
		combined.mkdir(outDir);
		combined.mkdir(outDirUnfilt);


		// run for each patient

		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String output = outDirUnfilt + sep + curPat + ".vcf";
			String logfile = outDirUnfilt + sep + curPat + ".log";
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
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}


			///////////////////
			//////// filter variants ////////

			input = output;
			output = outDir + sep + curPat;

			// prepare command
			ArrayList<String> filterCmd =  filter.removeFiltered(output, input);

			// save command
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(filterCmd);
			}

			// sort VCF file to get rid of contig order error
			String fileIn = output + ".recode.vcf";
			String fileOut = output + "-sorted.vcf";
			String bamFile = patients.get(curPat).getBam();

			ArrayList<String> sortCmd = sortVcf(curPat, fileIn, fileOut, bamFile);

			// save command
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(sortCmd);
			}

			
			
			
			
			//////// normalize variants with vt
			
			input = fileOut;
			output = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep + curPat + ".vcf";
			
			ArrayList<String> vtCmd = normalize(input, output);
			
			//save command
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(vtCmd);
			}
			
			// store vcf name for later use
			patients.get(curPat).setVcfFiles(caller, output);

			
			
		}
	}

	
	
	
	
	
	
	
	
	
	
	/////////////////////
	//////// run freebayes
	public void runFreebayes() {

		// general varialbes and mkdir
		String caller = "freebayes";
		String sep = File.separator;
		String outDir = tmpDir + sep + config.getVariantCalling() + sep + caller;

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
			cmd.add("-t " + options.getBedFile());
			cmd.add("--genotype-qualities");
			cmd.add(input);
			cmd.add("| " + config.getVcfFilter());
			cmd.add("-f \"QUAL > 10\"");
			cmd.add("> " + fileOut);


			// sort VCF file to get rid of contig order error
			String fileIn = fileOut;
			fileOut = outDir + sep + curPat + "-sorted.vcf";
			String bamFile = patients.get(curPat).getBam();

			ArrayList<String> sortCmd = sortVcf(curPat, fileIn, fileOut, bamFile);



			// normalize variants for later consensus vcf
			fileIn = fileOut;
			fileOut = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep + curPat + ".vcf";
			ArrayList<String> normCmd = normalize(fileIn, fileOut);
			
			


			// save commands
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
				patients.get(curPat).addCmd02(sortCmd);
				patients.get(curPat).addCmd02(normCmd);
			}
			patients.get(curPat).setVcfFiles(caller, fileOut);


		}
	}

	
	
	
	
	
	

	/* 
	 * extract individuals
	 * GATK performs combined calls. For consensus calling individual vcf-files are needed.
	 * Thus extract a vcf-file for each patient
	 */
	public void extractInd() {

		String caller = "gatk";

		for (String curPat : patients.keySet()){
			// gather and initialize variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = tmpDir + sep + config.getVariantCalling() + sep + caller;
			String fileOut = outDir + sep + curPat;

			// prepare command

			cmd.add(config.getVcfTools());
			cmd.add("--recode");
			cmd.add("--indv " + curPat);
			cmd.add("--vcf " + combined.getCombinedVcfFile());
			cmd.add("--out " + fileOut);

			
			
			
			//////// normalize variants
			String fileIn = fileOut + ".recode.vcf";
			fileOut = options.getOutDir() + sep + config.getVariantCalling() + sep + caller + sep + curPat + ".vcf";
			ArrayList<String> normCmd = normalize(fileIn, fileOut);
			
			
			// store commands
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd04(cmd);
				patients.get(curPat).addCmd04(normCmd);
				
			}
			patients.get(curPat).setVcfFiles(caller, fileOut);


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

				Integer step = options.getSteps().get("calling");
				
				if (first <= step && last >= step ) {
					// for each patient save commands of current caller
					if (callerName.equals("gatk")) {
						patients.get(curPat).addCmd04(cmd);
					} else if (callerName.equals("platypus")) {
						patients.get(curPat).addCmd04(cmd);
					} else if (callerName.equals("freebayes")) {
						patients.get(curPat).addCmd04(cmd);
					} else if (callerName.equals("samtools")) {
						patients.get(curPat).addCmd04(cmd);
					}
				}
			}
		}
	}


	
	
	
	
	// merge variants
	public void mergeDifCallerVariants() {

		// general varialbes
		String sep = File.separator;
		String outDir = tmpDir + sep + config.getVariantCalling() + sep + "preConsensus";
		combined.mkdir(outDir);


		for (String curPat : patients.keySet()) {
			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String output = outDir + sep + curPat + ".vcf";




			// prepare command
			cmd.add(config.getJava());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T CombineVariants");
			cmd.add("-R " + config.getHg19Fasta());

			for (String callerName : patients.get(curPat).getVcfFiles().keySet()){
				String input = patients.get(curPat).getVcfFiles().get(callerName);
				cmd.add("--variant " + input);
			}


			cmd.add("-genotypeMergeOptions Unsorted");
			cmd.add("-o " + output);


			// store commands
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd04(cmd);
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
			String output = outDir + sep + curPat + "-consensus.vcf";

			
			
			
			// prepare command
			cmd.add(config.getJava());
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

			
			
			
			
			
			/*
			 * exclude variants with to few calls
			 * these can happen here due to a bug in GATK
			 * 
			 */
			String fileIn = output;
			output = outDir + sep + curPat + ".vcf";
			
			ArrayList<String> addSetCmd = new ArrayList<>();
			addSetCmd.add(config.getJava() + " -jar");
			addSetCmd.add(config.getAddSetInfo());
			addSetCmd.add(fileIn);
			addSetCmd.add(output);
			addSetCmd.add(Integer.toString(caller.length));
			addSetCmd.add(Integer.toString(options.getMinConsCall()));
			
			
			
			
			
			
			
			// store command
			Integer step = options.getSteps().get("calling");
			
			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd04(cmd);
				patients.get(curPat).addCmd04(addSetCmd);
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
		Integer step = options.getSteps().get("calling");
		
		if (first <= step && last >= step ) {
			combined.addCmd05(combindeCallinCmd);
		}
		combined.setLastOutFile(output);
		
		// save vcf-file for later annotation with alamut
		combined.setVcfForAnnotation(output);



	}


	//////////////////
	//////// sort vcf-file
	public ArrayList<String> sortVcf(String curPat, String fileIn, String fileOut, String bamFile) {

		// gater and initialize variables
		ArrayList<String> cmd = new ArrayList<>();


		// prepare command
		cmd.add(config.getJava());
		cmd.add("-jar " + config.getPicard());
		cmd.add("SortVcf");
		cmd.add("I=" + fileIn);
		cmd.add("O=" + fileOut);
		cmd.add("TMP_DIR=" + config.getLocalTmp() );
		cmd.add("SEQUENCE_DICTIONARY=" + bamFile);


		// return ready command
		return cmd;

	}


	
	//////////////////////////
	//////// normalize variants using VT
	
	public ArrayList<String> normalize(String fileIn, String fileOut) {
		
		// init and gathe variable
		ArrayList<String> cmd = new ArrayList<>();
		String vt = config.getVtMaster();
		String refFasta = config.getHg19Fasta();
		
		// prepare command
		cmd.add("sed 's/ID=GQ,Number=1,Type=Integer/ID=GQ,Number=1,Type=Float/' " + fileIn + " |");
		cmd.add(vt + " decompose -s - |");
		cmd.add(vt + " normalize - -q");
		cmd.add("-r " + refFasta);
		cmd.add("-o " + fileOut);
		
		
		// return ready command
		return cmd;
		
		
	}
	
	



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}