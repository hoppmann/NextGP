package de.NextGP.initialize.options;

import java.util.Map;

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
	private CommandLine cmd = null;
	private String outDir = "out";
	private String cpu;
	private String mem;
	private String bedFile;
	private String slurmDir = "pipeline";
	private int minConsCall = 2;
	private String slurmPartition = "";
	private String tempDir;
	private boolean isSolid = false;
	private String slurmLog = "slurmLog";
	
	
	
	// slurm based
	private String[] exclude = null;
	private String[] restrict = null;

	// alignment variables
	private String fastqList;
	private String bamList;
	
	// pipelines
	private boolean bwa;
	private boolean illuminaPanel;
	private boolean ionExome;
	private boolean ionPanel;
	private boolean exome;
	private String customPipeline;
	private boolean custom;
	private boolean bam;
	
	// pipeline parts
	private int first = 1;
	private int last = 10;
	private boolean mail = true;
	private boolean skiptAlamutBatch;
	private String afterOk;
	

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
			mem = "40";
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
		
		// get name of slurm partition
		if (cmd.hasOption("slurmPartition")){
			slurmPartition = cmd.getOptionValue("slurmPartition");
		}
		
		// get name of directory for intermediate files
		if (cmd.hasOption("tempDir")){
			tempDir = cmd.getOptionValue("tempDir");
		} else {
			tempDir = config.getTempDir();
		}
		
		
		
		//////// slurm based options
		
		
		// get folder for slurm log files
		if (cmd.hasOption("slurmLog")) {
			slurmLog = cmd.getOptionValue("slurmLog");
		}
		
		// exclude nodes from the partition
		if (cmd.hasOption("exclude")) {
			exclude = cmd.getOptionValues("exclude");
		}
		
		// restrict nodes that should be used
		if (cmd.hasOption("restrict")) {
			restrict = cmd.getOptionValues("restrict");
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
			ionExome = true;
		}
		
		if (cmd.hasOption("exome")) {
			exome = true;
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
		
		if (cmd.hasOption("noMail")) {
			mail = false;
		}
		
		if (cmd.hasOption("skip")) {
			skiptAlamutBatch = true;
		}
		
		if (cmd.hasOption("after")) {
			afterOk = cmd.getOptionValue("after");
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

	public String getBamList() {
		return bamList;
	}

	public boolean isIonExome() {
		return ionExome;
	}

	public boolean isExome() {
		return exome;
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

	public String getSlurmPartition() {
		return slurmPartition;
	}

	public boolean isBam() {
		return bam;
	}

	public boolean isSolid() {
		return isSolid;
	}

	public String getTempDir() {
		return tempDir;
	}

	public boolean isMail() {
		return mail;
	}

	public String getSlurmLog() {
		return slurmLog;
	}

	public String[] getExclude() {
		return exclude;
	}

	public String[] getRestrict() {
		return restrict;
	}

	public Map<String, Integer> getSteps() {
		
		return setOptions.getSteps();
	}

	public boolean isSkiptAlamutBatch() {
		return skiptAlamutBatch;
	}

	public String getAfterAny() {
		return afterOk;
	}

	
	
	
	
	
	
	
}
