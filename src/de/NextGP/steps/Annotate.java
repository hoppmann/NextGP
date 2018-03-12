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
	private String tmpDir;


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

		this.tmpDir = options.getTempDir() + File.separator + options.getOutDir();
		Log.logger(logger, "preparing anotations");

		// make directory
		mkDir();
		
	}


	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	
	// create folder
	private void mkDir(){
		
		String sep = File.separator;
		String cmd = tmpDir + sep + config.getAnnotation();
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
		
		// get name for out file
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = tmpDir + sep + config.getAnnotation() + sep + name + "-sed-norm-decomp.vcf";
		
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
		Integer step = options.getSteps().get("annotate");

		if (first <= step && last >= step ) {
			combined.addCmd05(vtCmd);
			
		}		
	}
	
	
	
	//// prepare VEP annotation
	
	public void vep(String vcfFile) {
		
		//////// running VEP
		
		// initialize and gather variables
		ArrayList<String> vepCmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = tmpDir + sep + config.getAnnotation();
		
		// get name for outfile
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + name + "-sed-norm-decomp.vcf";

		outVEP = outDir + sep + name + "-vep.vcf" ;
		
		/*
		 * get columns of dbNSFP to be used for annotation
		 * the current dbNSFP version is 2.9.3 since it is the last one on hg19
		 */
		
		// extract header names
//		ArrayList<String> headerCmd = new ArrayList<>(); 
//		headerCmd.add("header=\"$(zcat " + config.getDbNSFP() + " | head -n 1 | cut -f 11-999 | sed 's/\\t/,/g' | sed 's/ //g')\" ");
		
		String header = "dbNSFP,/data/ngs/resources/dbNSFP/2.9.3/dbNSFP2.9.3_hg19.gz,cds_strand,"
				+ "SIFT_score,SIFT_converted_rankscore,SIFT_pred,"
				+ "Polyphen2_HDIV_score,Polyphen2_HDIV_rankscore,Polyphen2_HDIV_pred,"
				+ "Polyphen2_HVAR_score,Polyphen2_HVAR_rankscore,Polyphen2_HVAR_pred,"
				+ "LRT_score,LRT_converted_rankscore,LRT_pred,"
				+ "MutationTaster_score,MutationTaster_converted_rankscore,MutationTaster_pred,"
				+ "MutationAssessor_score,MutationAssessor_rankscore,MutationAssessor_pred,"
				+ "FATHMM_score,FATHMM_rankscore,FATHMM_pred,"
				+ "MetaSVM_score,MetaSVM_rankscore,MetaSVM_pred,"
				+ "MetaLR_score,MetaLR_rankscore,MetaLR_pred,"
				+ "Reliability_index,"
				+ "VEST3_score,VEST3_rankscore,"
				+ "PROVEAN_score,PROVEAN_converted_rankscore,PROVEAN_pred,"
				+ "M-CAP_score,M-CAP_rankscore,M-CAP_pred,"
				+ "REVEL_score,REVEL_rankscore,"
				+ "MutPred_score,MutPred_rankscore,"
				+ "Eigen-raw,Eigen-phred,"
				+ "Eigen-PC-raw,Eigen-PC-phred,Eigen-PC-raw_rankscore,"
				+ "CADD_raw,CADD_raw_rankscore,CADD_phred,"
				+ "GERP++_NR,GERP++_RS,GERP++_RS_rankscore,"
				+ "phyloP46way_primate,phyloP46way_primate_rankscore,"
				+ "phyloP46way_placental,phyloP46way_placental_rankscore,"
				+ "phyloP100way_vertebrate,phyloP100way_vertebrate_rankscore,"
				+ "phastCons46way_primate,phastCons46way_primate_rankscore,"
				+ "phastCons46way_placental,phastCons46way_placental_rankscore,"
				+ "phastCons100way_vertebrate,phastCons100way_vertebrate_rankscore,"
				+ "SiPhy_29way_pi,SiPhy_29way_logOdds,SiPhy_29way_logOdds_rankscore";
		
		// prepare command
		vepCmd.add(config.getVep());
		vepCmd.add("-i " + outVtMaster);
		vepCmd.add("-o " + outVEP);
		vepCmd.add("-fork " + options.getCpu());
		vepCmd.add("--cache");
		vepCmd.add("--merged");
		vepCmd.add("--offline");
		vepCmd.add("--plugin dbNSFP," + config.getDbNSFP() + header);
//		vepCmd.add("--plugin dbNSFP," + config.getDbNSFP() + ",SIFT_pred,Polyphen2_HDIV_pred,Polyphen2_HVAR_pred,LRT_pred,MutationTaster_pred,MutationAssessor_pred,FATHMM_pred,MetaSVM_pred,MetaLR_pred,PROVEAN_pred,M-CAP_pred,REVEL_score,clinvar_clnsig,clinvar_trait");
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
		Integer step = options.getSteps().get("annotate");

		if (first <= step && last >= step ) {
//			combined.addCmd05(headerCmd);
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
		snpEffCmd.add("-jar " +  config.getSnpEff());
		snpEffCmd.add("GRCh37.75");
		snpEffCmd.add("-classic");
		snpEffCmd.add("-formatEff");
		snpEffCmd.add("-noStats");
		snpEffCmd.add(outVEP);
		snpEffCmd.add("> " + outSnpEff);
		
		
		// store command
		Integer step = options.getSteps().get("annotate");

		if (first <= step && last >= step ) {
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
		Integer step = options.getSteps().get("annotate");

		if (first <= step && last >= step ) {
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
		Integer step = options.getSteps().get("annotate");

		if (first <= step && last >= step ) {
			combined.addCmd05(tabixCmd);
		}
		
	}
	
	

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////



}