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

public class VariantCaller {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private static Logger logger = LoggerFactory.getLogger(VariantCaller.class);
	private GetOptions options;
	private LoadConfig config;
	private Map<String, Patients> patients;




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public VariantCaller(GetOptions options, LoadConfig config, Map<String, Patients> patients)  {

		// retrieve variables
		this.options = options;
		this.config = config;
		this.patients = patients;

		// make log entry
		Log.logger(logger, "preparing variant calling");
		
	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// run haplotype caller
	public void runHaplotypeCaller() {


		// run for each patient
		for (String curPat : patients.keySet()){

			// init and gather variables
			ArrayList<String> cmd = new ArrayList<>();
			String sep = File.separator;
			String outDir = options.getOutDir() + sep + config.getVariantCalling();
			String output = outDir + sep + curPat + sep + curPat + ".raw.g.vcf";
			String logfile = outDir + sep + curPat + sep + curPat + ".log";
			String input = patients.get(curPat).getLastOutFile();
			if (input == null || input.isEmpty()) {
				input = options.getOutDir() + sep + config.getBaseReacalibration() + sep + curPat + ".bam";
			}

			// prepare command

			cmd.add(config.getJava());
			cmd.add(options.getXmx());
			cmd.add("-jar " + config.getGatk());
			cmd.add("-T HaplotypeCaller");
			cmd.add("-nct " + options.getCpu());
			cmd.add("-l INFO");
			cmd.add("-R " + config.getHg19Fasta());
			cmd.add("-D " + config.getDbsnp());
			cmd.add("-L " + options.getBedFile());
			cmd.add("-ERC GVCF");
//			cmd.add("-variant_index_type LINEAR");
//			cmd.add("-variant_index_parameter 128000");
			cmd.add("-I " + input);
			cmd.add("-o " + output);
			cmd.add("-log " + logfile);



			// save command
			patients.get(curPat).setHaplotypeCaller(cmd);
			patients.get(curPat).setLastOutFile(output);

		}
	}

	
	// run Genotype GVCF
	public void runGenotypeGVCF(Combined combined) {
		
		// init and gather variables
		ArrayList<String> cmd = new ArrayList<>();
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + config.getVariantCalling();
		String outFile = outDir + sep + "combined.raw.vcf";
		String logfile = outDir + sep + "GenotypeGVCF.log";
		
		// prepare command
		cmd.add(config.getJava());
		cmd.add(options.getXmx());
		cmd.add("-jar " + config.getGatk());
		cmd.add("-T GenotypeGVCFs");
		cmd.add("-nt " + options.getCpu());
		cmd.add("-R " + config.getHg19Fasta());
		cmd.add("-D " + config.getDbsnp());
		cmd.add("-stand_call_conf 30 ");
		cmd.add("-stand_emit_conf 30");
		
		for (String curPat : patients.keySet()) {
			cmd.add("-V " + patients.get(curPat).getLastOutFile());
		}
		
		cmd.add("-o " + outFile);
		cmd.add("-log " + logfile);
		
		
		
		// save command
		combined.setGenotypeGVCF(cmd);
		combined.setLastOutFile(outFile);
	}
	




	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}