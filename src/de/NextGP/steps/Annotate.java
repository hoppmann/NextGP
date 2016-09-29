package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Annotate {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private static  Logger logger = LoggerFactory.getLogger(Annotate.class);
	private GetOptions options;
	private LoadConfig config;
//	private Map<String, Patients> patients;
	private Combined combined;
	private String outVEP;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	public Annotate(GetOptions options, LoadConfig config, Map<String, Patients> patients, Combined combined){

		// retrieve variables
		this.options = options;
		this.config = config;
//		this.patients = patients;
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
		combined.mkdir("mkdir -p " + cmd);
		
	}
	
	
	//// prepare VEP annotation using vt-master
	public ArrayList<String> vtMaster(String vcfFile) {

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
		
		
		// return command
		return cmd;
		
	}
	
	
	
	//// prepare VEP annotation
	
	public ArrayList<String> vep(String vcfFile) {
		
		//////// running VEP
		
		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getAnnotation();
		
		// get name for outfile
		String[] splitFile = vcfFile.split(File.separator);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		String outVtMaster = outDir + sep + name + "-sed-norm-decomp.vcf";

		cmd = new ArrayList<>();
		outVEP = outDir + sep + name + ".vcf" ;
		
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

		// return command
		return cmd;
		
	}




	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


	public String getOutVEP() {
		return outVEP;
	}





}