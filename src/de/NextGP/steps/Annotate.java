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


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	public Annotate(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

		// retrieve variables
		this.options = options;
		this.config = config;
		this.combined = combined;

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
		combined.setVtMaster(cmd);
		
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
		cmd.add("--species homo_sapiens");
		cmd.add("--cache_version 77");
		cmd.add("--assembly GRCh37");
		cmd.add("--force_overwrite");
		cmd.add("--sift b");
		cmd.add("--polyphen b");
		cmd.add("--symbol");
		cmd.add("--numbers");
		cmd.add("--biotype");
		cmd.add("--total_length");
		cmd.add("--vcf");
		cmd.add("--offline");
		cmd.add("--fields Consequence,Codons,Amino_acids,Gene,SYMBOL,Feature,EXON,PolyPhen,SIFT,Protein_position,BIOTYPE");

		// store command
		combined.setVepAnnotation(cmd);
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
		cmd.add(outVEP);
		cmd.add("> " + outSnpEff);
		
		
		// store command
		combined.setSnpEffAnnotation(cmd);
		combined.setLastOutFile(outSnpEff);
		
	}
	
	
	
	public void bgzip() {
		
		Log.logger(logger, "Bgzipping files.");
		
		// preparig command
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(config.getBgzip());
		cmd.add("-c " + outSnpEff);
		cmd.add("> " + outSnpEff + ".gz");
		
		// store command
		combined.setBgzip(cmd);
		
		
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
		combined.setTabix(cmd);
		
	}
	
	
	
	

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


	public String getOutVEP() {
		return outVEP;
	}





}