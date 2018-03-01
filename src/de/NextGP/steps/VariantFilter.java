package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;


public class VariantFilter {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	static private Logger logger = LoggerFactory.getLogger(VariantCaller.class);
	private GetOptions options;
	private LoadConfig config; 
	private Combined combined;
	private int first;
	private int last;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public VariantFilter(GetOptions options, LoadConfig config, Combined combined, String caller) {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();
		
		// make log entry
		Log.logger(logger, "Preparing hard filtering for " + caller);

	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	// run hard filtering for variant calls (panel)
	public void hardFiltering() {



		// initialize and gather variables
		String sep = File.separator;
		String outDir = options.getTempDir() + sep + config.getVariantCalling() + sep + "gatk" + sep + "filtered";
		String subDir = "SNPs";
		String outFileSnpSet = outDir + sep + subDir + sep + "raw_snps.vcf";
		String logExtractSnpSet = outDir + sep + subDir + sep + "raw_snps.log";
		String input = combined.getCombinedVcfFile();
		if (input == null || input.isEmpty()) {
			input = options.getOutDir() + sep + config.getVariantCalling() + sep + "combined.raw.vcf";
		}

		//////// extract all SNPs
		ArrayList<String> extractSnpSetCmd = new ArrayList<>();
		extractSnpSetCmd.add(config.getJava());
		extractSnpSetCmd.add("-jar " + config.getGatk());
		extractSnpSetCmd.add("-T SelectVariants");
		extractSnpSetCmd.add("-R " + config.getHg19Fasta());
		extractSnpSetCmd.add("-V " + input);
		extractSnpSetCmd.add("-selectType SNP");
		extractSnpSetCmd.add("-o " + outFileSnpSet);
		extractSnpSetCmd.add("-log " + logExtractSnpSet);

		// save command
		Integer step = options.getSteps().get("calling");
		
		if (first <= step && last >= step ) {
			combined.addCmd03(extractSnpSetCmd);
		}

		//////// Apply filter to SNP call set

		// initialize and gather variables
		ArrayList<String> hardFilterCmd = new ArrayList<>();
		String outFileFilterSnps = outDir + sep + subDir + sep +"filtered_snps.vcf";
		String logFilterSnpSet = outDir + sep + subDir + sep + "filtered_snp.log";


		// prepare command
		hardFilterCmd.add(config.getJava());
		hardFilterCmd.add("-jar " + config.getGatk());
		hardFilterCmd.add("-T VariantFiltration");
		hardFilterCmd.add("-R " + config.getHg19Fasta());
		hardFilterCmd.add("-V " + outFileSnpSet);
		hardFilterCmd.add("--filterExpression \"QD < 2.0\"");
		hardFilterCmd.add("--filterName \"QDFilter\"");
		hardFilterCmd.add("--filterExpression \"FS > 200.0\"");
		hardFilterCmd.add("--filterName \"FSFilter\"");
		hardFilterCmd.add("--filterExpression \"MQRankSum < -12.5\"");
		hardFilterCmd.add("--filterName \"MQRankSumFilter\"");
		hardFilterCmd.add("--filterExpression \"ReadPosRankSum < -20.0\"");
		hardFilterCmd.add("--filterName \"ReadPosFilter\"");
		hardFilterCmd.add("-o " + outFileFilterSnps);
		hardFilterCmd.add("-log " + logFilterSnpSet);



		// save command
		if (first <= step && last >= step ) {
			combined.addCmd03(hardFilterCmd);
		}


		//////// extract all Indel

		// initialize and gather variables
		ArrayList<String> extractIndelCmd = new ArrayList<>();
		subDir = "indels";
		String outExtractIndelSet = outDir + sep + subDir + sep + "raw_indel.vcf";
		String logExtractIndelSet = outDir + sep + subDir + sep + "raw_indel.log";

		// prepare command		
		extractIndelCmd.add(config.getJava());
		extractIndelCmd.add("-jar " + config.getGatk());
		extractIndelCmd.add("-T SelectVariants");
		extractIndelCmd.add("-R " + config.getHg19Fasta());
		extractIndelCmd.add("-V " + input);
		extractIndelCmd.add("-selectType INDEL");
		extractIndelCmd.add("-o " + outExtractIndelSet);
		extractIndelCmd.add("-log " + logExtractIndelSet);


		// save command
		if (first <= step && last >= step ) {
			combined.addCmd03(extractIndelCmd);
		}




		//////// apply filter to indel call set

		// initialize and gather variables
		ArrayList<String> filterIndelCmd = new ArrayList<>();
		subDir = "indels";
		String outFilterIndel = outDir + sep + subDir + sep + "filtered_indel.vcf";
		String logFilterIndel = outDir + sep + subDir + sep + "filtered_indel.log";

		// prepare command		
		filterIndelCmd.add(config.getJava());
		filterIndelCmd.add("-jar " + config.getGatk());
		filterIndelCmd.add("-T VariantFiltration");
		filterIndelCmd.add("-R " + config.getHg19Fasta());
		filterIndelCmd.add("-V " + outExtractIndelSet);
		filterIndelCmd.add("--filterExpression \" FS > 200.0 \"");
		filterIndelCmd.add("--filterName \"FSFilter\"");
		filterIndelCmd.add("--filterExpression \" ReadPosRankSum < -20.0 \"");
		filterIndelCmd.add("--filterName \"ReadPosRankSumFilter\"");
		filterIndelCmd.add("-o " + outFilterIndel);
		filterIndelCmd.add("-log " + logFilterIndel);


		// save command
		if (first <= step && last >= step ) {
			combined.addCmd03(filterIndelCmd);
		}

		
		
		
		
		
		//////// combine variants
		// initialize and gather variables
		ArrayList<String> combineVariantsCmd = new ArrayList<>();
		String outFileCombined = outDir + sep + "filter_marked.vcf";
		String logCombined = outDir + sep + "combineVariants.log";
		
		// prepare command
		combineVariantsCmd.add(config.getJava());
		combineVariantsCmd.add("-jar " + config.getGatk());
		combineVariantsCmd.add("-T CombineVariants");
		combineVariantsCmd.add("-R " + config.getHg19Fasta());
		combineVariantsCmd.add("--variant " + outFileFilterSnps);
		combineVariantsCmd.add("--variant " + outFilterIndel);
		combineVariantsCmd.add("--genotypemergeoption UNSORTED");
		combineVariantsCmd.add("-o " + outFileCombined);
		combineVariantsCmd.add("-log " + logCombined);
		
		
		// store command
		if (first <= step && last >= step ) {
			combined.addCmd03(combineVariantsCmd);
		}		
		
		
		//	remove filtered reads
		String outFileFiltered = outDir + sep + "combined_filtered";
		ArrayList<String> removeFilteredReadsCmd = new ArrayList<>();
		removeFilteredReadsCmd = removeFiltered(outFileFiltered, input);
		
		// store command
		if (first <= step && last >= step ) {
			combined.setCombinedVcfFile(outFileFiltered + ".recode.vcf");
					combined.addCmd03(removeFilteredReadsCmd);
		}
		
	}


	// remove filtered reads
	
	public ArrayList<String> removeFiltered(String output, String input) {

		
		////////remove filtered reads
		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		
		// prepare command
		cmd.add(config.getVcfTools());
		cmd.add("--vcf " + input);
		cmd.add("--remove-filtered-all");
		cmd.add("--recode-INFO-all");
		cmd.add("--recode");
		cmd.add("--out " + output);
		
		
		// store command
		return cmd;
	}
	
	

	// run VQSR on called variants (WES)
	public void vqsr() {

		// make log entry
		Log.logger(logger, "Preparing VQSR");


		// initialize and gather variables


		// prepare command



		// save command
	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
