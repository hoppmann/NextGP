package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Picard {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
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
		System.out.println("Preparing AddOrReplaceReadgroups");
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
		System.out.println("Preparing markDuplicates");
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
			String sep = File.separator;
			String tempDir = config.getLocalTmp() + sep + options.getOutDir();
			
			
			// prepare variables
			String rgbl = curPat;
			String rgpl = platform;
			String rgpu = curPat;
			String rgsm = curPat;
			String outputFile = tempDir + sep + outDir + sep + curPat + ".bam"; 



			// retrive variables
			ArrayList<String> cmd = new ArrayList<>();

			cmd.add(config.getJava());
			cmd.add("-jar " + config.getPicard());
			cmd.add("AddOrReplaceReadGroups");
			cmd.add("INPUT=" + patients.get(curPat).getLastOutFile());
			cmd.add("OUTPUT=" + outputFile);
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			cmd.add("RGLB=" + rgbl);
			cmd.add("RGPL=" + rgpl);
			cmd.add("RGPU=" + rgpu);
			cmd.add("RGSM=" + rgsm);
			cmd.add("CREATE_INDEX=true");
			cmd.add("VALIDATION_STRINGENCY=LENIENT");


			// add command to patient object
			Integer step = options.getSteps().get("alignment");
			Integer step2 = options.getSteps().get("duplicates");

			if ((first <= step && last >= step) ||(first <= step2 && last >= step2)  ) {
				patients.get(curPat).addCmd02(cmd);
			}
			patients.get(curPat).setLastOutFile(outputFile);
		}
	}

	
	// modify sample name
	public ArrayList<String> replaceSampleName(String newName, String curPat){
		
			// get directory for intermediate files
			String sep = File.separator;
			String interDir = options.getTempDir() + sep + options.getOutDir();
			
			
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
		String sep = File.separator;
		String tempDir = config.getLocalTmp() + sep + options.getOutDir();
		String dupOutDir = tempDir + sep + config.getDuplicates();
		combined.mkdir(dupOutDir);
		
		// prepare command
		for (String curPat : patients.keySet()){
			ArrayList<String> cmd = new ArrayList<>();
			
			// prepare our file names
			String outFile = dupOutDir + "/" + curPat + ".dup.bam";
			String metricOutFile = dupOutDir + "/" + curPat + ".metrics.txt";
			
			cmd.add(config.getJava());
			cmd.add("-jar " + config.getPicard());
			cmd.add("MarkDuplicates");
			cmd.add("INPUT=" + patients.get(curPat).getLastOutFile());
			cmd.add("OUTPUT=" + outFile);
			cmd.add("METRICS_FILE=" + metricOutFile);
			cmd.add("VALIDATION_STRINGENCY=LENIENT");
			cmd.add("TMP_DIR=" + config.getLocalTmp() );
			
			
			// add command to patient object
			Integer step = options.getSteps().get("duplicates");

			if (first <= step && last >= step ) {
				patients.get(curPat).addCmd02(cmd);
			}
			patients.get(curPat).setLastOutFile(outFile);
		}
		
	}
	


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
