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
	

	// alignment variables
	private String fastqList;
	private String bamList;
	
	// pipeline parts
	private boolean bwa;
	private boolean illuminaPanel;
	private boolean ionExon;
	private boolean bamExon;
	private String customPipeline;
	private boolean custom;
	
	

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
		
		
		
		//////// general options
		if (cmd.hasOption("help")) {
			new SetOptions().callHelp();
			System.exit(0);
		}
		
		
		// define out dir
		if (cmd.hasOption("outDir")) {
			outDir = cmd.getOptionValue("outDir");
		}
		
		// set number of possible CPU per thread
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
//			config.setTargetBED(cmd.getOptionValue("bedFile"));
		}
		
		//////// get alignment specific options
		if (cmd.hasOption("fastqList")) {
			fastqList = cmd.getOptionValue("fastqList");
		}
		
		if (cmd.hasOption("bamList")) {
			bamList = cmd.getOptionValue("bamList");
		}
		
		
		
		//////// pipeline parts
		
		if (cmd.hasOption("bwa")) {
			bwa = true;
		}
		
		if (cmd.hasOption("illuminaPanel")) {
			illuminaPanel = true;
		}
		
		if (cmd.hasOption("ionExon")) {
			ionExon = true;
		}
		
		if (cmd.hasOption("bamExon")) {
			bamExon = true;
		}
		
		if (cmd.hasOption("customPipeline")) {
			customPipeline = cmd.getOptionValue("customPipeline");
			custom = true;
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

	public String getBedFile() {
		if (bedFile == null || bedFile.isEmpty()){
			Log.error(logger, "No bed file chosen. Use bedFile option.");
			System.exit(3);
		}
		return bedFile;
	}
	
	
	
	
	
	
	
	
}
