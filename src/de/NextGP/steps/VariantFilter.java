package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;


public class VariantFilter {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	static private Logger logger = LoggerFactory.getLogger(VariantCaller.class);
	GetOptions options;
	LoadConfig config; 
	Combined combined;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public VariantFilter(GetOptions options, LoadConfig config, Combined combined) {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.combined = combined;




	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	// run hard filtering for variant calls (panel)
	public void hardFiltering() {

		// make log entry
		Log.logger(logger, "Preparing hard filtering");


		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantFiltering();
		String subDir = "SNPs";
		String outFileSnpSet = outDir + sep + subDir + sep + "raw_snps.vcf";
		String logExtractSnpSet = outDir + sep + subDir + sep + "raw_snps.log";

		//////// extract all SNPs

		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T SelectVariants");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-V " + combined.getRawVCF());
		cmd.add("-selectType SNP");
		cmd.add("-o " + outFileSnpSet);
		cmd.add("-log " + logExtractSnpSet);

		// save command
		combined.setExtractSnpSet(cmd);


		//////// Apply filter to SNP call set

		// initialize and gather variables
		cmd = new ArrayList<>();
		String outFileFilterSnps = outDir + sep + subDir + sep +"filtered_snps.vcf";
		String logFilterSnpSet = outDir + sep + subDir + sep + "filtered_snp.log";


		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T VariantFiltration");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-V " + outFileSnpSet);
		cmd.add("--filterExpression \"QD < 2\"");
		cmd.add("--filterName \"QDFilter\"");
		cmd.add("--filterExpression \"FS > 200.0\"");
		cmd.add("--filterName \"FSFilter\"");
		cmd.add("--filterExpression \"MQRankSum < -12.5\"");
		cmd.add("--filterName \"MQRankSumFilter\"");
		cmd.add("--filterExpression \"ReadPosRankSum < -20.0\"");
		cmd.add("--filterName \"ReadPosFilter\"");
		cmd.add("-o " + outFileFilterSnps);
		cmd.add("-log " + logFilterSnpSet);



		// save command
		combined.setHardFiterSnpSet(cmd);



		//////// extract all Indel

		// initialize and gather variables
		cmd = new ArrayList<>();
		subDir = "indels";
		String outExtractIndelSet = outDir + sep + subDir + sep + "raw_indel.vcf";
		String logExtractIndelSet = outDir + sep + subDir + sep + "raw_indel.log";

		// prepare command		
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T SelectVariants");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-V " + combined.getRawVCF());
		cmd.add("-selectType INDEL");
		cmd.add("-o " + outExtractIndelSet);
		cmd.add("-log " + logExtractIndelSet);


		// save command
		combined.setExtractIndelSet(cmd);





		//////// apply filter to indel call set

		// initialize and gather variables
		cmd = new ArrayList<>();
		subDir = "indels";
		String outFilterIndel = outDir + sep + subDir + sep + "filtered_indel.vcf";
		String logFilterIndel = outDir + sep + subDir + sep + "filtered_indel.log";

		// prepare command		
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T VariantFiltration");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-V " + outExtractIndelSet);
		cmd.add("--filterExpression \" QD < 2.0 \"");
		cmd.add("--filterName \"QDFilter\"");
		cmd.add("--filterExpression \" FS > 200 \"");
		cmd.add("--filterName \"FSFilter\"");
		cmd.add("--filterExpression \" ReadPosRankSum < -20.0 \"");
		cmd.add("--filterName \"ReadPosRankSumFilter\"");
		cmd.add("-o " + outFilterIndel);
		cmd.add("-log " + logFilterIndel);


		// save command
		combined.setHardFilterIndelSet(cmd);


		
		
		
		
		
		//////// combine variants
		// initialize and gather variables
		cmd = new ArrayList<>();
		String outFileCombined = outDir + sep + "filter_marked.vcf";
		String logCombined = outDir + sep + "combineVariants.log";
		
		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T CombineVariants");
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("--variant " + outFileFilterSnps);
		cmd.add("--variant " + outFilterIndel);
		cmd.add("--genotypemergeoption UNSORTED");
		cmd.add("-o " + outFileCombined);
		cmd.add("-log " + logCombined);
		
		
		// store command
		combined.setCombineVariants(cmd);
		
		
		
		
		//////// remove filtered reads
		// initialize and gather variables
		cmd = new ArrayList<>();
		String outFileFiltered = outDir + sep + "combined_filtered";
		
		// prepare command
		cmd.add(config.getVcfTools());
		cmd.add("--vcf " + outFileCombined);
		cmd.add("--remove-filtered-all");
		cmd.add("--recode");
		cmd.add("--out " + outFileFiltered);
		
		
		// store command
		combined.setRemoveFilteredReads(cmd);
		combined.setLastOutFile(outFileFiltered + ".recode.vcf");
//		combined.setLastOutFile(outFileFiltered);
		

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
