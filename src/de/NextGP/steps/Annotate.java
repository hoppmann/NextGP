	package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Annotate {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static  Logger logger = LoggerFactory.getLogger(Annotate.class);
	private GetOptions options;
	private LoadConfig config;
	private Combined combined;
	private String outVEP;
	private String outSnpEff;
	private int first;
	private int last;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	public Annotate(GetOptions options, LoadConfig config, Combined combined){

		// retrieve variables
		this.options = options;
		this.config = config;
		this.combined = combined;
		this.first = options.getFirst();
		this.last = options.getLast();

		Log.logger(logger, "preparing anotations");

		// make directory
		mkDir();
		
	}


	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	
	// create folder
	private void mkDir(){
		
		String outDir = options.getTempDir();
		String sep = File.separator;
		String cmd = outDir + sep + config.getAnnotation();
		combined.mkdir(cmd);
		
	}
	
	
	//// prepare VEP annotation using vt-master
	public void vtMaster(String vcfFile) {

		// make log entry
		Log.logger(logger, "preparing annotaions using VEP");
		
		
		//////// running vt-master
		
		// initialize and gather variants
		ArrayList<String> vtCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getTempDir();

		
		// get name for out file
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + config.getAnnotation() + sep + name + "-sed-norm-decomp.vcf";
		
		// prepare command
		vtCmd.add("zless " + vcfFile);
		vtCmd.add ("| sed 's/ID=AD,Number=./ID=AD,Number=R/'");
		vtCmd.add("| " +config.getVtMaster() + " decompose ");
		vtCmd.add("-s -");
		vtCmd.add("| " + config.getVtMaster() + " normalize");
		vtCmd.add("-r " + config.getHg19Fasta());
		vtCmd.add("-o " + outVtMaster);
		vtCmd.add("-");
		
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.addCmd05(vtCmd);
			
		}		
	}
	
	
	
	//// prepare VEP annotation
	
	public void vep(String vcfFile) {
		
		//////// running VEP
		
		// initialize and gather variables
		ArrayList<String> vepCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getTempDir() + sep + config.getAnnotation();
		
		// get name for outfile
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + name + "-sed-norm-decomp.vcf";

		outVEP = outDir + sep + name + "-vep.vcf" ;
		
		// prepare command
		vepCmd.add(config.getVep());
		vepCmd.add("-i " + outVtMaster);
		vepCmd.add("-o " + outVEP);
		vepCmd.add("-fork " + options.getCpu());
		vepCmd.add("--cache");
		vepCmd.add("--merged");
		vepCmd.add("--offline");
		vepCmd.add("--plugin dbNSFP,/data/ngs/resources/dbNSFP/2.9.3/dbNSFP2.9.3_hg19.gz,SIFT_pred,Polyphen2_HDIV_pred,Polyphen2_HVAR_pred,LRT_pred,MutationTaster_pred,MutationAssessor_pred,FATHMM_pred,MetaSVM_pred,MetaLR_pred,PROVEAN_pred,M-CAP_pred,REVEL_score,clinvar_clnsig,clinvar_trait");
		vepCmd.add("--species homo_sapiens");
		vepCmd.add("--dir_cache " + config.getVepCache());
		vepCmd.add("--cache_version 89");
		vepCmd.add("--assembly GRCh37");
		vepCmd.add("--force_overwrite");
		vepCmd.add("--sift b");
		vepCmd.add("--polyphen b");
		vepCmd.add("--symbol");
		vepCmd.add("--numbers");
		vepCmd.add("--biotype");
		vepCmd.add("--total_length");
		vepCmd.add("--check_existing");
		vepCmd.add("--canonical");
		vepCmd.add("--pubmed");
		vepCmd.add("--vcf");

		// store command
		if (first <= 7 && last >= 7 ) {
			combined.addCmd05(vepCmd);
		}
		combined.setLastOutFile(outVEP);
	}


	
	//// annotate variants using SNPeff
	
	public void snpEff (){
		
		// get name for outfile
		outSnpEff = outVEP.split("\\.")[0] + "-snpEff.vcf"; 
		
		
		// prepare command
		ArrayList<String> snpEffCmd = new ArrayList<>();
		
		
		snpEffCmd.add(config.getJava());
//		snpEffCmd.add(options.getXmx());
		snpEffCmd.add("-jar " +  config.getSnpEff());
		snpEffCmd.add("GRCh37.75");
		snpEffCmd.add("-classic");
		snpEffCmd.add("-formatEff");
		snpEffCmd.add("-noStats");
		snpEffCmd.add(outVEP);
		snpEffCmd.add("> " + outSnpEff);
		
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.addCmd05(snpEffCmd);
			
		}
		combined.setLastOutFile(outSnpEff);
		
	}
	
	
	
	public void bgzip() {
		
		Log.logger(logger, "Bgzipping files.");
		
		// preparing command
		ArrayList<String> bgZipCmd = new ArrayList<>();
		bgZipCmd.add(config.getBgzip());
		bgZipCmd.add("-c " + outSnpEff);
		bgZipCmd.add("> " + outSnpEff + ".gz");
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.addCmd05(bgZipCmd);
		}
		
	}
	
	public void tabix() {
		Log.logger(logger, "Indexing files.");
		
		// preparing command
		ArrayList<String> tabixCmd = new ArrayList<>();
		tabixCmd.add(config.getTabix());
		tabixCmd.add("-f ");
		tabixCmd.add("-p vcf");
		tabixCmd.add(outSnpEff + ".gz");
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.addCmd05(tabixCmd);
		}
		
	}
	
	

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


	public String getOutVEP() {
		return outVEP;
	}





}