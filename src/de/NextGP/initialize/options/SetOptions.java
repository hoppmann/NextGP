package de.NextGP.initialize.options;

import org.apache.commons.cli.Options;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public class SetOptions {


	//////////////////////
	//////// Set variables
	private Options options; 
	private Multimap<String, OptionValue> opts;

	
	// names for grouping
	String general = "general";
	String pipeline = "pipeline";
	String alignment = "alignment";
	String parts = "pipeline parts";


	////////////////////
	//////// constructor

	public SetOptions() {
		
		// create options
		makeOptions();
		
	}
	



	////////////////
	//////// Methods

	private void makeOptions() {
		
		// instantiate options object
		options = new Options();
		opts = LinkedListMultimap.create();

		
		
		// general options
		opts.put(general, new OptionValue(options, "help", false, "\tCalls this help."));
		opts.put(general, new OptionValue(options, "outDir", true, "\tName of directory for output. (default = out)"));
		opts.put(general, new OptionValue(options, "CPU", true, "\tNumber of CPU per thread (default:4)"));
		opts.put(general, new OptionValue(options, "mem", true, "\tAmount of memory allowed per thread in Gb (default 10Gb)"));
		opts.put(general, new OptionValue(options, "bedFile", true, "\tPath to bed file containing covered regions"));
		opts.put(general, new OptionValue(options, "slurmDir", true, "Set directory slurm commands will be written to."));
		opts.put(general, new OptionValue(options, "consensus", true, "Sets the threshold of how many caller need to call a variant before it's accepted. (default 2)"));
		opts.put(general, new OptionValue(options, "slurmPatition", true, "Define the slurm patition to run on."));
		opts.put(general, new OptionValue(options, "tempDir", true, "\tFolder to save intermediate steps which will not be kept in the end (defauld /tempdata/ge/NextGP/"));
		
		// alignment specific options
		opts.put(alignment, new OptionValue(options, "fastqList", true, "List containing name of forward and backward read files."));
		opts.put(alignment, new OptionValue(options, "bamList", true, "\tList containing bamfiles. (needed if alignment not done in pipeline)"));
				
		// pipeline
		opts.put(pipeline, new OptionValue(options, "panel", false, "\tif chosen runs all steps necessary for analysis of an Illumina panel."));
		opts.put(pipeline, new OptionValue(options, "ionExon", false, "\tExecutes the Ion Torrent exone pipeline"));
		opts.put(pipeline, new OptionValue(options, "ionPanel", false, "Prepares the ionTorrent panel version of the pipeline"));
		opts.put(pipeline, new OptionValue(options, "exon", false, "\trun an exon starting with a bam file"));
		opts.put(pipeline, new OptionValue(options, "solid", false, "\tUse this option if input bam files are SOLiD files. Only needed if \"bamExon\" is run"));
		
		// pipeline parts
		opts.put(parts, new OptionValue(options, "first", true, "Number of first pipeline step to start from. Default: 01. Possible steps: \n\t\t\t\t01: alignment; \n\t\t\t\t02: remove duplicates; \n\t\t\t\t03: indel realignment; \n\t\t\t\t04: base recalibration; \n\t\t\t\t05: metrices; \n\t\t\t\t06: variant calling; \n\t\t\t\t07: annotaion; \n\t\t\t\t08: database generation"));
		opts.put(parts, new OptionValue(options, "last", true, "Last step of Pipeline to run. Default 08."));
		
	}

	
	
	//display Help
		public void callHelp() {
			
			System.out.println("options");
			
			// for each group display help text
			for (String key : opts.keySet()) {

				// display group name
				System.out.println("\n\t" + key);
				
				// extract help text and display it
				for (OptionValue currentOption : opts.get(key)) {
					String shortcut = currentOption.getShortcut();
					String description = currentOption.getDescription();
					System.out.println("\t\t" + shortcut + "\t\t" + description);
				}
			}
			System.out.println("");
			System.exit(0);
		}
	
	
	///////////////
	//////// getter


	public Options getOptions() {
		return options;
	}






}
