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

public class AddReaplaceReadgroups {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	Logger logger = LoggerFactory.getLogger(AddReaplaceReadgroups.class);
	private LoadConfig config;
	private GetOptions options;
	private Map<String, Patients> patients;
	private int first;
	private int last;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public AddReaplaceReadgroups(LoadConfig config, GetOptions options, Map<String, Patients> patients) {

		// retrieve variables
		this.config = config;
		this.options = options;
		this.patients = patients;
		this.first = options.getFirst();
		this.last = options.getLast();
		
		// make log entry
		Log.logger(logger, "Preparing AddOrReplaceReadgroups");
	}






	/////////////////////////
	//////// methods ////////
	/////////////////////////


	// change readgroup in sam file
	public void addReadgroups(String platform) {

		// prepare command for each patient
		for (String curPat : patients.keySet()){

			// prepare variables
			String rgbl = curPat;
			String rgpl = platform;
			String rgpu = curPat;
			String rgsm = curPat;
			String outputFile = options.getOutDir() + File.separator + config.getAlignment() + File.separator + curPat + ".bam"; 
			String logOut = options.getOutDir() + File.separator + config.getAlignment() + File.separator + curPat + ".log";


			// retrive variables
			ArrayList<String> cmd = new ArrayList<>();

			cmd.add(config.getJava());
			cmd.add(options.getXmx());
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
			if (first <= 1 && last >= 1 ) {
				patients.get(curPat).setAddOrReplaceReadgroups(cmd);
			}
			patients.get(curPat).setLastOutFile(outputFile);

		}
	}

	
	// modify sample name
	public ArrayList<String> replaceSampleName(String newName, String curPat){

		// prepare return map
//		Map<String, ArrayList<String>> allCmd = new HashMap<>();
		
			
			// init and prepare variables
			ArrayList<String> cmd = new ArrayList<>();
			String curPatFile = patients.get(curPat).getVcfFiles().get(newName);
			String outFile = curPatFile.replace(".vcf", "-renamed.vcf");
			
			// prepare command
			cmd.add("cat " + curPatFile);
			cmd.add("| awk -v curPat=\"" + newName + "\"");
			cmd.add("'{ if ($1 ~ /^#CHROM/) { print ( $1 \"\\t\" $2 \"\\t\" $3 \"\\t\" $4 \"\\t\" $5 \"\\t\" $6 \"\\t\" $7 \"\\t\" $8 \"\\t\" $9 \"\\t\" curPat ) } else print } '");
			cmd.add("> " + outFile);
			cmd.add(" && mv " + outFile + " " + curPatFile);
			
		// return commands
		return cmd;
		
		
	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
