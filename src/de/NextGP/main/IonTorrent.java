package de.NextGP.main;

import java.util.Map;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.general.outfiles.Patients;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class IonTorrent {

	//////////////
	//// Variables
	GetOptions options;
	Map<String, Patients> patients;
	LoadConfig config;
	Combined combined;
	GeneralPipeline pipeline;



	// run pipeline corresponding to an Illumina Panel

	public IonTorrent(GetOptions options, LoadConfig config) {

		//prepare and initialize variables
		this.options = options;
		this.config = config;
		combined = new Combined();



	}


	/////////////////////////
	//////// Methods ////////
	/////////////////////////

	public void runExon() {
		// init class to adress pipeline parts
		pipeline = new GeneralPipeline(options, config);

		// make log entry for starting IonProton pipeline
		System.out.println("Preparing IonProton batch files.");

		// read input file
		patients = pipeline.readFastqList();
		// prepare bed file
		pipeline.prepareBedFile();
		
		// step 01 align reads
		pipeline.align("ionTorrent");

		// step 03 prepare indel realignment
		pipeline.indelRealigner();

		// step 04 prepare BQSR
		boolean isSolid = false;
		pipeline.bqsr(isSolid);

		// step 05 create metrics
		pipeline.metrices();

		// step 06 variant calling
		// TODO 
		// implementation of VQSR instead of hard filtering
		pipeline.panelVariantCalling();

		// step 07 annotate variants
		pipeline.annotate();

		// step 08 add to gemini database
		pipeline.loadInGemini();


		// save commands
		pipeline.saveCommands();

	}
	
	
	
	public void runPanel() {
		
		// init class to address pipeline parts
		pipeline = new GeneralPipeline(options, config);
		
		// make log entry for starting IonPanel pipeline
		System.out.println("Preparing IonPanel pipeline files");
		
		// read input file
		patients = pipeline.readFastqList();

		// check if input is given
		pipeline.checkPatMap();
		
		// 00 prepare bed file
		pipeline.prepareBedFile();
		
		// 01 align reads 
		pipeline.align("IonTorrent");
		
		// 03 realign
		pipeline.indelRealigner();

		// 04 BQSR
		boolean isSolid = false;
		pipeline.bqsr(isSolid);

		// 05 create metrics
		pipeline.metrices();

		// 06 Variant calling
		pipeline.panelVariantCalling();

		// 07 annotate
		pipeline.annotate();
		
		// 08  gemini
		pipeline.loadInGemini();

		// save commands
		pipeline.saveCommands();
		
		
	}


}
