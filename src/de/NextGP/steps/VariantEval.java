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

public class VariantEval {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private static Logger logger = LoggerFactory.getLogger(VariantCaller.class);
	GetOptions options;
	LoadConfig config;
	Combined combined;
	Map<String, Patients> patients;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public VariantEval(GetOptions options, LoadConfig config, 
			Combined combined, Map<String, Patients> patients) {
		
		//	retrieve variables
		this.options = options;
		this.config = config;
		this.combined = combined;
		this.patients = patients;
		
		// make log entry
		Log.logger(logger, "preparing variant evalutaion");
		// prepare command
		varEval();
		
	}
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void varEval(){
		
		
		
		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<String>();
		String inputVCF = combined.getLastOutFile();
		String sep = File.separator;
		String outDir = options.getOutDir();
		String output = outDir + sep + config.getVariantCalling() +sep + "gatk" + sep + "filtered" + sep + "eval.xls";
		
		
		
		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T VariantEval");
		cmd.add("-nt " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-D " + config.getDbsnp());
		cmd.add("-ST Sample");
		cmd.add("-noST");
		cmd.add("-noEV");
		cmd.add("-EV CountVariants");
		cmd.add("-EV TiTvVariantEvaluator");
		cmd.add("-EV CompOverlap");
		cmd.add("--eval:set1 " + inputVCF);
		cmd.add("-o " + output);
		
		
		
		
		// store command
		combined.setVarEval(cmd);
		
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////



}