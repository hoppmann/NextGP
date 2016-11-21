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
		opts.put(general, new OptionValue(options, "outDir", true, "Name of directory for output. (default = out)"));
		opts.put(general, new OptionValue(options, "CPU", true, "Number of CPU per thread (default:4)"));
		opts.put(general, new OptionValue(options, "mem", true, "Amount of memory allowed per thread in Gb (default 10Gb)"));
		opts.put(general, new OptionValue(options, "bedFile", true, "Path to bed file containing covered regions"));
		opts.put(general, new OptionValue(options, "slurmDir", true, "Set directory slurm commands will be written to."));
		opts.put(general, new OptionValue(options, "consensus", true, "Sets the threshold of how many caller need to call a variant before it's accepted. (default 2)"));
		
		// alignment specific options
		opts.put(alignment, new OptionValue(options, "fastqList", true, "List containing name of forward and backward read files."));
		opts.put(alignment, new OptionValue(options, "bamList", true, "List containing bamfiles. (needed if alignment not done in pipeline)"));
				
		// pipeline parts
		opts.put(pipeline, new OptionValue(options, "alignment", false, "if chosen alignment part of pipeline is executed."));
		opts.put(pipeline, new OptionValue(options, "illuminaPanel", false, "if chosen runs all steps necessary for analysis of an Illumina panel."));
		opts.put(pipeline, new OptionValue(options, "ionExon", false, "Executes the Ion Torrent exone pipeline"));
		opts.put(pipeline, new OptionValue(options, "bamExon", false, "run an exon starting with a bam file"));
		opts.put(pipeline, new OptionValue(options, "customPipeline", true, "Path to file containing information for custom pipeline."));
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
		}
	
	
	///////////////
	//////// getter


	public Options getOptions() {
		return options;
	}






}
