	package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Annotate {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

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

		this.tmpDir = config.getAnnotation() + File.separator + options.getOutDir();
		System.out.println("preparing anotations");

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
		System.out.println("preparing annotaions using VEP");
		
		
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
		 */
		
		
		String headerNames = ",SIFT_score,SIFT_pred"
				+ ",Polyphen2_HVAR_score,Polyphen2_HVAR_pred"
				+ ",MetaLR_score,MetaLR_pred,MetaSVM_score,MetaSVM_pred"
				+ ",REVEL_score"
				+ ",FATHMM_score,FATHMM_pred"
				+ ",PROVEAN_score,PROVEAN_pred";
		
		// prepare command
		vepCmd.add(config.getVep());
		vepCmd.add("-i " + outVtMaster);
		vepCmd.add("-o " + outVEP);
		vepCmd.add("-fork " + options.getCpu());
		vepCmd.add("--cache");
		vepCmd.add("--merged");
		vepCmd.add("--offline");
		vepCmd.add("--dir_cache " + config.getVepCache());
		vepCmd.add("--cache_version " + config.getVepCacheVersion());
		vepCmd.add("--assembly GRCh37");
		vepCmd.add("--species homo_sapiens");
		vepCmd.add("--plugin dbNSFP," + config.getDbNSFP() + headerNames);
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
		
		System.out.println("Bgzipping files.");
		
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
		System.out.println("Indexing files.");
		
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
	
	

}