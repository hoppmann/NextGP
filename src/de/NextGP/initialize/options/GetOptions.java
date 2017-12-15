package de.NextGP.initialize.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.initialize.LoadConfig;

public class GetOptions {

	//////////////////
	//// set variables

	private static Logger logger = LoggerFactory.getLogger(GetOptions.class);
	private LoadConfig config;
	
	// general stuff
	private String[] args;
	private Options options;
	private SetOptions setOptions;
	private String progPath;
	CommandLine cmd = null;
	private String outDir = "out";
	private String cpu;
	private String mem;
	private String xmx;
	private String bedFile;
	private String slurmDir = "pipeline";
	private int minConsCall = 2;
	private String slurmPatition = "genepi";
	private String tempDir;
	private boolean isSolid = false;
	

	// alignment variables
	private String fastqList;
	private String bamList;
	
	// pipelines
	private boolean bwa;
	private boolean illuminaPanel;
	private boolean ionExon;
	private boolean ionPanel;
	private boolean bamExon;
	private String customPipeline;
	private boolean custom;
	private boolean bam;
	
	// pipeline parts
	private int first = 1;
	private int last = 9;
	private boolean email;
	

	////////////////
	//// constructor

	public GetOptions(String[] args, LoadConfig config) {

		// get arguments
		this.args = args;
		this.config = config;
		
		// get option parameters
		setOptions = new SetOptions();
		this.options = setOptions.getOptions();

		// extract path of the program
		progPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();


		// get options
		getOptions();



	}



	////////////
	//// methods

	private void getOptions() {

		// print out current job
		logger.info("Getting command line options.");


		//parse cammand line options
		CommandLineParser cmdParser = new PosixParser();
		try {
			cmd = cmdParser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			setOptions.callHelp();
			System.exit(1);
		}
		
		
		
		//// check if help is called
		if (cmd.hasOption("help")){
			setOptions.callHelp();
		}
		
		
		
		if (cmd.hasOption("CPU")) {
			cpu = cmd.getOptionValue("CPU");
		} else {
			cpu = "4";
		}
		
		// set amount of available mem per thread
		if (cmd.hasOption("mem")) {
			mem = (cmd.getOptionValue("mem"));
		} else {
			mem = "10";
		}
		
		// set bed file as reference
		if (cmd.hasOption("bedFile")) {
			bedFile = cmd.getOptionValue("bedFile");
		}
		
		
		// set slurm output directory
		if (cmd.hasOption("slurmDir")) {
			slurmDir = cmd.getOptionValue("slurmDir");
		} 

		// set output directory for pipeline results
		if (cmd.hasOption("outDir")) {
			outDir = cmd.getOptionValue("outDir");
		} 
		
		// set number of min consensus calls
		if (cmd.hasOption("consensus")) {
			minConsCall = Integer.parseInt(cmd.getOptionValue("consensus"));
		}
		
		// get name of slurm patition
		if (cmd.hasOption("slurmPatition")){
			slurmPatition = cmd.getOptionValue("slurmPatition");
		}
		
		// get name of directory for intermediate files
		if (cmd.hasOption("tempDir")){
			tempDir = cmd.getOptionValue("tempDir");
		} else {
			tempDir = config.getTempDir();
		}
		
		
		
		//////// get alignment specific options
		if (cmd.hasOption("fastqList")) {
			fastqList = cmd.getOptionValue("fastqList");
		}
		
		if (cmd.hasOption("bamList")) {
			bamList = cmd.getOptionValue("bamList");
			bam = true;
			first = 2; 
		}

	
		
		
		
		
		//////// pipelines
		
		if (cmd.hasOption("bwa")) {
			bwa = true;
		}
		
		if (cmd.hasOption("panel")) {
			illuminaPanel = true;
		}
		
		if (cmd.hasOption("ionExon")) {
			ionExon = true;
		}
		
		if (cmd.hasOption("exon")) {
			bamExon = true;
		}
		
		
		if (cmd.hasOption("ionPanel")) {
			ionPanel = true;
		}
		
		if (cmd.hasOption("solid")){
			isSolid = true;
		}
		
		
		
		//////// pipeline parts
		if (cmd.hasOption("first")){
			first = Integer.valueOf(cmd.getOptionValue("first"));
		}
		
		if (cmd.hasOption("last")){
			last = Integer.valueOf(cmd.getOptionValue("last"));
		}
		
		if (cmd.hasOption("email")) {
			email = cmd.hasOption("email");
		}
		
		
		
		
	}


	





	///////////
	//// getter

	public String[] getArgs() {
		return args;
	}

	public SetOptions getSetOptions() {
		return setOptions;
	}

	public String getProgPath() {
		return progPath;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public String getFastqList() {
		return fastqList;
	}

	public boolean isBwa() {
		return bwa;
	}
	
	public String getOutDir() {
		return outDir;
	}

	public boolean isIlluminaPanel() {
		return illuminaPanel;
	}

	public String getCpu() {
		return cpu;
	}

	public String getMem() {
		return mem;
	}

	public String getXmx() {
		xmx = "-Xmx" + mem + "g";
		return xmx;
	}

	public String getBamList() {
		return bamList;
	}

	public boolean isIonExon() {
		return ionExon;
	}

	public boolean isBamExon() {
		return bamExon;
	}

	public String getCustomPipeline() {
		return customPipeline;
	}


	public boolean isCustom() {
		return custom;
	}

	public static Logger getLogger() {
		return logger;
	}
	

	public LoadConfig getConfig() {
		return config;
	}

	public void setBedFile(String bedFile) {
		this.bedFile = bedFile;
	}

	
	public String getBedFile() {
		if (bedFile == null || bedFile.isEmpty()){
			Log.error(logger, "No bed file chosen. Use bedFile option.");
			System.exit(3);
		}
		return bedFile;
	}

	public String getSlurmDir() {
		return slurmDir;
	}

	public int getMinConsCall() {
		return minConsCall;
	}

	public boolean isIonPanel() {
		return ionPanel;
	}

	public int getFirst() {
		return first;
	}

	public int getLast() {
		return last;
	}

	public String getSlurmPatition() {
		return slurmPatition;
	}

	public boolean isBam() {
		return bam;
	}

	public String getIntermediateDir() {
		return tempDir;
	}

	public boolean isSolid() {
		return isSolid;
	}

	public String getTempDir() {
		return tempDir;
	}

	public boolean isEmail() {
		return email;
	}

	
	
	
	
	
	
	
}
