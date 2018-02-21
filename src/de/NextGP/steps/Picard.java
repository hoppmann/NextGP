package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.ui.StandardGradientPaintTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Picard {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	Logger logger = LoggerFactory.getLogger(Picard.class);
	private LoadConfig config;
	private GetOptions options;
	private Map<String, Patients> patients;
	private int first;
	private int last;
	private Combined combined;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	// constructor for addReplaceReadgroups
	public Picard(LoadConfig config, GetOptions options, Map<String, Patients> patients) {

		// retrieve variables
		this.config = config;
		this.options = options;
		this.patients = patients;
		this.first = options.getFirst();
		this.last = options.getLast();
		
		// make log entry
		Log.logger(logger, "Preparing AddOrReplaceReadgroups");
	}

	// constructor for markDuplicates
	public Picard(LoadConfig config, GetOptions options, Map<String, Patients> patients, Combined combined) {

		// retrieve variables
		this.config = config;
		this.options = options;
		this.patients = patients;
		this.first = options.getFirst();
		this.last = options.getLast();
		this.combined = combined;
		
		// make log entry
		Log.logger(logger, "Preparing markDuplicates");
	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////

	/////////////////////////
	//////// change readgroup in sam file
	public void addReadgroups(String platform, String outDir) {

		// prepare command for each patient
		for (String curPat : patients.keySet()){

			// get directory for intermediate files
			String interDir = options.getIntermediateDir();
			String sep = File.separator;
			
			
			// prepare variables
			String rgbl = curPat;
			String rgpl = platform;
			String rgpu = curPat;
			String rgsm = curPat;
			String outputFile = interDir + sep + options.getOutDir() + sep + outDir + sep + curPat + ".bam"; 
			String logOut = interDir + sep + options.getOutDir() + sep + outDir + sep + curPat + ".log";


			// retrive variables
			ArrayList<String> cmd = new ArrayList<>();

			cmd.add(config.getJava());
			cmd.add("-jar " + config.getPicard());
			cmd.add("AddOrReplaceReadGroups");
			cmd.add("INPUT=" + patients.get(curPat).getLastOutFile());
			cmd.add("OUTPUT=" + outputFile);
			cmd.add("RGLB=" + rgbl);
			cmd.add("RGPL=" + rgpl);
			cmd.add("RGPU=" + rgpu);
			cmd.add("RGSM=" + rgsm);
			cmd.add("CREATE_INDEX=true");
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("2> " + logOut);


			// add command to patient object
			if ((first <= 1 && last >= 1) ||(first <= 2 && last >= 2)  ) {
				patients.get(curPat).addCmd02(cmd);
			}
			patients.get(curPat).setLastOutFile(outputFile);
		}
	}

	
	// modify sample name
	public ArrayList<String> replaceSampleName(String newName, String curPat){
		
			// get directory for intermediate files
			String interDir = options.getIntermediateDir();
			String sep = File.separator;
			
			
			// init and prepare variables
			ArrayList<String> cmd = new ArrayList<>();
			String curPatFile = patients.get(curPat).getVcfFiles().get(newName);
			String outFile = interDir + sep + curPatFile.replace(".vcf", "-renamed.vcf");
			
			// prepare command
			cmd.add("cat " + curPatFile);
			cmd.add("| awk -v curPat=\"" + newName + "\"");
			cmd.add("'{ if ($1 ~ /^#CHROM/) { print ( $1 \"\\t\" $2 \"\\t\" $3 \"\\t\" $4 \"\\t\" $5 \"\\t\" $6 \"\\t\" $7 \"\\t\" $8 \"\\t\" $9 \"\\t\" curPat ) } else print } '");
			cmd.add("> " + outFile);
			cmd.add(" && mv " + outFile + " " + curPatFile);
			
		// return commands
		return cmd;
		
		
	}

	
	
	
	
	///////////////////
	//////// mark Duplicates
	
	
	public void markDuplicates() {
		
		// create out dir
		String interDir = options.getIntermediateDir();
		String outDir = options.getOutDir();
		String sep = File.separator;
		String dupOutDir = interDir + sep + outDir + sep + config.getDuplicates();
		combined.mkdir(dupOutDir);
		
		// prepare command
		for (String curPat : patients.keySet()){
			ArrayList<String> cmd = new ArrayList<>();
			
			// prepare our file names
			String outFile = dupOutDir + "/" + curPat + ".dup.bam";
			String metricOutFile = dupOutDir + "/" + curPat + ".metrics.txt";
			String logOut = dupOutDir + "/" + curPat + ".dup.log";
			
			cmd.add(config.getJava());
			cmd.add("-jar " + config.getPicard());
			cmd.add("MarkDuplicates");
			cmd.add("INPUT=" + patients.get(curPat).getLastOutFile());
			cmd.add("OUTPUT=" + outFile);
			cmd.add("METRICS_FILE=" + metricOutFile);
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("TMP_DIR=/scratch/local/tmp/" );
			cmd.add("2> " + logOut);
			
			
			// add command to patient object
			if (first <= 2 && last >= 2 ) {
				patients.get(curPat).addCmd02(cmd);
			}
			patients.get(curPat).setLastOutFile(outFile);
		}
		
	}
	


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
