package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class AddReaplaceReadgroups {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	Logger logger = LoggerFactory.getLogger(AddReaplaceReadgroups.class);
	private LoadConfig config;
	private GetOptions options;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public AddReaplaceReadgroups(LoadConfig config, GetOptions options, Map<String, Patients> patients, String platform) {

		// retrieve variables
		this.config = config;
		this.options = options;

		// make log entry
		Log.logger(logger, "Preparing AddOrReplaceReadgroups");
		addReadgroups(patients, platform);
	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void addReadgroups(Map<String, Patients> patients, String platform) {

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
			patients.get(curPat).setAddOrReplaceReadgroups(cmd);
			patients.get(curPat).setLastOutFile(outputFile);

		}

	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
