package de.NextGP.initialize.options;

import org.apache.commons.cli.Options;

public class OptionValue {


	//////////////////
	//// set variables 
	private String shortcut;
	private String description;
	private Boolean argumentRequired;



	////////////////
	//// constructor

	public OptionValue(Options opts, String shortcut,  Boolean argReq, String description) {


		// retriev values
		this.shortcut = shortcut; 
		this.description = description;
		this.argumentRequired = argReq;


		// add options
		opts.addOption(getShortcut(), isArgumentRequired(), getDescription());


	}


	///////////
	//// getter
	public String getShortcut() {
		return shortcut;
	}

	public String getDescription() {
		return description;
	}

	public Boolean isArgumentRequired() {
		return argumentRequired;
	}



}
