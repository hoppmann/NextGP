package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class FilterGemini {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	GetOptions options; 
	LoadConfig config;
	Combined combined;
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public FilterGemini(GetOptions options, LoadConfig config, Combined combined) {
		
		this.options = options;
		this.config = config;
		this.combined = combined;
		
		
		
		
		// make log entry
		System.out.println("Filtering variants in Gemini database");
		
		filterVariants();
		
	}
	
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////
	
	
	
	
	
	
	
	private void filterVariants () {
		String sep = File.separator;
		String outdir = options.getOutDir() + sep + config.getFilter();

		ArrayList<String> cmd = new ArrayList<>();
		String filterScriptPath = config.getFilterScript();
		
		cmd.add(filterScriptPath + " " + outdir);
		
		
		// save commands
		Integer step = options.getSteps().get("filter");
		
		System.out.println(options.getFirst());
		
		if ( options.getFirst() <= step && options.getLast() >= step ) {
			if (! options.isSkiptAlamutBatch()) {
				combined.addUpdateGeminiCmd(cmd);
			}
		}
		
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
