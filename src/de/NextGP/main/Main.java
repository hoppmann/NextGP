package de.NextGP.main;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Main {

	//////// Variables
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static LoadConfig config;
	
	public static void main(String[] args) {


		// read in config data
		Log.logger(logger, "Reading in config file.");
		readConfig();

		
		// get options
		GetOptions options = new GetOptions(args, config);

		// create output directory
		new File(options.getSlurmDir()).mkdirs();
		

		// run Illumina Panel if chosen
		if (options.isIlluminaPanel()){
			new Panel(options, config);
		}
		
		
		// run IonTorrentExon
		if (options.isIonExome()){
			new IonTorrent(options, config).runExon();;
		}
		
		// run IonTorrentPanel
		if (options.isIonPanel()){
			new IonTorrent(options, config).runPanel();
		}

		// run exome
		if (options.isExome()){
			new Exome(options, config);
		}

		// run custom chosen pipeline
		if (options.isCustom()) {
			
			
		}
		
		// to finish close log file
		Log.logger(logger, "NextGP succesfully finished");

	}




	// read in config data
	private static void readConfig(){

		// load config file
		try {
			config = new LoadConfig();
		} catch (IOException e) {
			Log.error(logger, "Failed reading in config file.");
			System.exit(1);
		}

	}





}
