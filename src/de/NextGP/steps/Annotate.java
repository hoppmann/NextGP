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
	public Annotate(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

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
		
		String outDir = options.getOutDir();
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
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir();

		
		// get name for outfile
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + config.getAnnotation() + sep + name + "-sed-norm-decomp.vcf";

		
		// prepare command
		cmd.add("zless " + vcfFile);
		cmd.add ("| sed 's/ID=AD,Number=./ID=AD,Number=R/'");
		cmd.add("| " +config.getVtMaster() + " decompose ");
		cmd.add("-s -");
		cmd.add("| " + config.getVtMaster() + " normalize");
		cmd.add("-r " + config.getHg19Fasta());
		cmd.add("-o " + outVtMaster);
		cmd.add("-");
		
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.setVtMaster(cmd);
		}		
	}
	
	
	
	//// prepare VEP annotation
	
	public void vep(String vcfFile) {
		
		//////// running VEP
		
		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getAnnotation();
		
		// get name for outfile
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + name + "-sed-norm-decomp.vcf";

		outVEP = outDir + sep + name + "-vep.vcf" ;
		
		// prepare command
		cmd.add(config.getVep());
		cmd.add("-i " + outVtMaster);
		cmd.add("-o " + outVEP);
		cmd.add("-fork " + options.getCpu());
		cmd.add("--cache");
		cmd.add("--merged");
		cmd.add("--offline");
		cmd.add("--plugin dbNSFP,/data/ngs/resources/dbNSFP/2.9.3/dbNSFP2.9.3_hg19.gz,SIFT_pred,Polyphen2_HDIV_pred,Polyphen2_HVAR_pred,LRT_pred,MutationTaster_pred,MutationAssessor_pred,FATHMM_pred,MetaSVM_pred,MetaLR_pred,PROVEAN_pred,M-CAP_pred,REVEL_score,clinvar_clnsig,clinvar_trait");
		cmd.add("--species homo_sapiens");
		cmd.add("--dir_cache" + config.getVepCache());
		cmd.add("--cache_version 89");
		cmd.add("--assembly GRCh37");
		cmd.add("--force_overwrite");
		cmd.add("--sift b");
		cmd.add("--polyphen b");
		cmd.add("--symbol");
		cmd.add("--numbers");
		cmd.add("--biotype");
		cmd.add("--total_length");
		cmd.add("--check_existing");
		cmd.add("--canonical");
		cmd.add("--pubmed");
		cmd.add("--vcf");

		// store command
		if (first <= 7 && last >= 7 ) {
			combined.setVepAnnotation(cmd);
		}
		combined.setLastOutFile(outVEP);
	}


	
	//// annotate variants using SNPeff
	
	public void snpEff (){
		
		// get name for outfile
		outSnpEff = outVEP.split("\\.")[0] + "-snpEff.vcf"; 
		
		
		// prepare command
		ArrayList<String> cmd = new ArrayList<>();
		
		
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " +  config.getSnpEff());
		cmd.add("GRCh37.75");
		cmd.add("-classic");
		cmd.add("-formatEff");
		cmd.add("-noStats");
		cmd.add(outVEP);
		cmd.add("> " + outSnpEff);
		
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.setSnpEffAnnotation(cmd);
		}
		combined.setLastOutFile(outSnpEff);
		
	}
	
	
	
	public void bgzip() {
		
		Log.logger(logger, "Bgzipping files.");
		
		// preparing command
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(config.getBgzip());
		cmd.add("-c " + outSnpEff);
		cmd.add("> " + outSnpEff + ".gz");
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.setBgzip(cmd);
		}
		
	}
	
	public void tabix() {
		Log.logger(logger, "Indexing files.");
		
		// preparing command
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(config.getTabix());
		cmd.add("-f ");
		cmd.add("-p vcf");
		cmd.add(outSnpEff + ".gz");
		
		// store command
		if (first <= 7 && last >= 7 ) {
			combined.setTabix(cmd);
		}
		
	}
	
	
	
	

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


	public String getOutVEP() {
		return outVEP;
	}





}