package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
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
	Map<String, Patients> patients;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public VariantEval(GetOptions options, LoadConfig config, Map<String, Patients> patients) {
		
		//	retrieve variables
		this.options = options;
		this.config = config;
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
		
		for (String curPat : patients.keySet()) {
		
		// initialize and gather variables
		ArrayList<String> cmd = new ArrayList<String>();
		String inputVCF = patients.get(curPat).getLastOutFile();
		
		
				
				
				
				
		String sep = File.separator;
		String outDir = options.getOutDir();
		String output = outDir + sep + config.getMetrices() + sep + curPat + sep + curPat + ".eval.xls";
		
		
		
		// prepare command
		cmd.add(config.getJava());
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
		Integer step = options.getSteps().get("calling");
		
		if (options.getFirst() <= step && options.getLast() >= step ) {
			patients.get(curPat).addCmd04(cmd);
		}
		
		}
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////



}