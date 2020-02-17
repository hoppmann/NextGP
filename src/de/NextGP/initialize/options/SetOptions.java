package de.NextGP.initialize.options;

import java.util.HashMap;
import java.util.Map;

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
	String alignment = "input";
	String misc = "misc";
	String slurm = "slurmOptions";

	
	// numbering of steps (to avoid to long list in declaration
	Map<String, Integer> steps = new HashMap<>();
	
	String numbering = "\t\t\t\tPossible steps:"
			+ "\n\t\t\t\t\t01: Preprocess"
			+ "\n\t\t\t\t\t02: alignment; "
			+ "\n\t\t\t\t\t03: remove duplicates; "
			+ "\n\t\t\t\t\t04: indel realignment; "
			+ "\n\t\t\t\t\t05: base recalibration; "
			+ "\n\t\t\t\t\t06: metrices; "
			+ "\n\t\t\t\t\t07: variant calling; "
			+ "\n\t\t\t\t\t08: annotation; "
			+ "\n\t\t\t\t\t09: database generation"
			+ "\n\t\t\t\t\t10: Alamut and other post VEP annotations"
			+ "\n\t\t\t\t\t11: filter variants";

	////////////////////
	//////// constructor

	public SetOptions() {
		
		
		// prepare Hash of steps
		steps.put("preprocess", 1);
		steps.put("alignment", 2);
		steps.put("duplicates", 3);
		steps.put("realignment", 4);
		steps.put("recalibration", 5);
		steps.put("metrices", 6);
		steps.put("calling", 7);
		steps.put("annotate", 8);
		steps.put("gemini", 9);
		steps.put("alamut", 10);
		steps.put("filter", 11);
		
		
		
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
		opts.put(general, new OptionValue(options, "bedFile", true, "\tPath to bed file containing covered regions"));
		opts.put(general, new OptionValue(options, "consensus", true, "Sets the threshold of how many caller need to call a variant before it's accepted. (default 2)"));
		opts.put(general, new OptionValue(options, "tempDir", true, "\tFolder to save intermediate steps which will not be kept in the end (defauld /tempdata/ge/NextGP/)"));

		// slurm options
		opts.put(slurm, new OptionValue(options, "slurmDir", true, "Set directory slurm commands will be written to."));
		opts.put(slurm, new OptionValue(options, "slurmPartition", true, "Define the slurm patition to run on."));
		opts.put(slurm, new OptionValue(options, "slurmLog", true, "Sets the folder where to save the slurm logs."));
		opts.put(slurm, new OptionValue(options, "CPU", true, "\tNumber of CPU per thread (default:4)"));
		opts.put(slurm, new OptionValue(options, "mem", true, "\tAmount of memory allowed per thread in Gb (default 40Gb)"));
		opts.put(slurm, new OptionValue(options, "exclude", true, "\tSet the nodes to be excluded. To exclude multiple nodes call \"exclude\" multiple times."));
		opts.put(slurm, new OptionValue(options, "restrict", true, "Sets specific nodes to be used for analysis. To set multiple nodes call \"restrict\" multiple times"));
		
		
		// alignment specific options
		opts.put(alignment, new OptionValue(options, "fastqList", true, "List containing name of forward and backward read files."));
		opts.put(alignment, new OptionValue(options, "bamList", true, "\tList containing bamfiles. (needed if alignment not done in pipeline)"));
		opts.put(alignment, new OptionValue(options, "vcfList", true, "\tList containing vcf-files for annotation."));
		
		// pipeline
		opts.put(pipeline, new OptionValue(options, "panel", false, "\tif chosen runs all steps necessary for analysis of an Illumina panel."));
		opts.put(pipeline, new OptionValue(options, "ionExon", false, "\tExecutes the Ion Torrent exone pipeline"));
		opts.put(pipeline, new OptionValue(options, "ionPanel", false, "Prepares the ionTorrent panel version of the pipeline"));
		opts.put(pipeline, new OptionValue(options, "exome", false, "\trun an whole exome analysis. Can be combined with \"bamList\""));
		opts.put(pipeline, new OptionValue(options, "solid", false, "\tUse this option if input bam files are SOLiD files. Only needed if \"exon\" is run"));
		
		
		// pipeline parts
		opts.put(misc, new OptionValue(options, "first", true, "Number of first pipeline step to start from. Default: 01.\n" + numbering));
		opts.put(misc, new OptionValue(options, "last", true, "Last step of Pipeline to run. Default 10."));
		opts.put(misc, new OptionValue(options, "noMail", false, "If set, no notice of failing or finishing will be send to the corresponding e-mail adress deposed in slurm."));
		opts.put(misc, new OptionValue(options, "skip", false, "Skips running alamut batch. Only use if an old annotation file is available."));
		opts.put(misc, new OptionValue(options, "after", true, "\":\"-seperated list of Slurm PIDs for afterok-dependency for the 05-Job."));
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




	public Map<String, Integer> getSteps() {
		return steps;
	}






}
