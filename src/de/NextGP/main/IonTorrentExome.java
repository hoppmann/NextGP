package de.NextGP.main;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Combined;
import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class IonTorrentExome {

	//////////////
	//// Variables
	GetOptions options;
	Map<String, Patients> patients;
	LoadConfig config;
	Combined combined;
	GeneralPipeline pipeline;

	private static Logger logger = LoggerFactory.getLogger(IonTorrentExome.class);


	// run pipeline corresponding to an Illumina Panel

	public IonTorrentExome(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();

		// init class to adress pipeline parts
		pipeline = new GeneralPipeline(options, config);
		
		// make log entry for starting IonProton pipeline
		Log.logger(logger, "Preparing IonProton batch files.");

		// read input file
		patients = pipeline.readFastqFile();

		// step 01 align reads
		pipeline.align("ionTorrent");
		
		// step 02 prepare indel realignment
		pipeline.indelRealigner();
		
		// step 03 prepare BQSR
		pipeline.bqsr();
		
		// step 04 create metrics
		pipeline.metrices();
		
		// step 05 prepare variant calling
		pipeline.gatkHapCaller();
		
		// step 06 Variant hard filtering
		pipeline.hardFilter();
		
		// step 07 annotate variants
		pipeline.annotate();
		
		// step 08 add to gemini database
		pipeline.loadInGemini();
		
		
		// save commands
		pipeline.saveCommands();
		
		
	}


}
