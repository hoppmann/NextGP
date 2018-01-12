package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class AlamutHgmd {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private static  Logger logger = LoggerFactory.getLogger(AlamutHgmd.class);

	GetOptions options; 
	LoadConfig config;
	Combined combined;
	String alamutOut;
	String bedFile;
	String bedFileZip;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public AlamutHgmd(GetOptions options, LoadConfig config, Combined combined) {

		this.options = options;
		this.config = config;
		this.combined = combined;


		// make log entry
		Log.logger(logger, "Preparing annotation with alamut");


		//// execute all parts needed to annotate with alamut
		runAlamut();
		prepareBedFile();
		tabixBed();
		prepareColumns();
		updateGemini();


	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	//////// annotate the unannotated  variants with Alamut and HGMD
	public void runAlamut() {


		//// prepare out file name
		String sep = File.separator;
		String outDir = config.getTempDir() + sep + config.getAnnotation() + sep + "Alamut";
		combined.mkdir(outDir);

		String vcfFile = combined.getVcfForAnnotation();
		String[] splitFile = vcfFile.split(sep);
		String name = splitFile[splitFile.length - 1].split("\\.")[0];
		alamutOut = outDir + sep + name + ".alamut";
		String alamutLog = outDir + sep + name + ".log";



		//////// prepare command
		ArrayList<String> alamutCmd = new ArrayList<>();



		alamutCmd.add(config.getAlamut());
		alamutCmd.add("--outputannonly");
		alamutCmd.add("--in " + combined.getVcfForAnnotation());
		alamutCmd.add("--hgmdUser hoppmann");
		alamutCmd.add("--hgmdPasswd KiKli2016");
		alamutCmd.add("--ann " + alamutOut);
		alamutCmd.add("--unann "  + alamutLog);

		if ( options.getFirst() <= 9 && options.getLast() >= 9 ) {
			combined.addCmd05(alamutCmd);
		}

	}



	//////// create .bed-file out of Alamut annotation
	public void prepareBedFile() {

		// prepare bed file name
		bedFile = alamutOut.replace("alamut", "bed");
		bedFile = bedFile + ".gz";
		
		
		// preparing command
		ArrayList<String> formBedCmd = new ArrayList<>();

		formBedCmd.add("cat " + alamutOut + " | awk '{ \n" + 
				"	if (FNR == 1) { \n" + 
				"		gsub(\"\\t\", \"\\talamut_\");\n" + 
				"		print \"#CHROM\\tSTART\\tSTOP\\t\" $0\n" + 
				"	} else { \n" + 
				"		print $2 \"\\t\" ($3 - 1) \"\\t\" $3 + length($4) - 1 \"\\t\" $0 \n" + 
				"	} \n" + 
				"}' | cut -f 1-4,9-999 | sed 's/#id/rsId/' | bgzip > " + bedFile);

		// save in combined object
		if (options.getFirst() <= 9 && options.getLast() >= 9 ) {
			combined.addCmd05(formBedCmd);
		}
	}



	// tabix bed file for later use with gemini
	public void tabixBed () {


		// prepare command
		ArrayList<String> tabixCmd = new ArrayList<>();

		tabixCmd.add("tabix -p bed " + bedFile);

		// save in combined object
		if (options.getFirst() <= 9 && options.getLast() >= 9 ) {
			combined.addCmd05(tabixCmd);
		}
	}



	//// prepare columns, indeces, coltypes and mode

	public void prepareColumns() {

		ArrayList<String> prepColCmd = new ArrayList<>();

		prepColCmd.add("ALAMUT_IDXS=\"\"\n" + 
				"ALAMUT_COLTYPES=\"\"\n" + 
				"ALAMUT_MODE=\"\"\n" + 
				"\n" + 
				"\n" + 
				"start=4\n" + 
				"nAnno=$(zcat " + bedFile + " | head -n 1 | wc | awk '{ print $2 }')\n" + 
				"for (( i=$start; i<=$nAnno; i++ ))\n" + 
				"do \n" + 
				"	if [ $i -eq $start ]\n" + 
				"	then\n" + 
				"		ALAMUT_IDXS=$i\n" + 
				"		ALAMUT_COLTYPES=\"text\"\n" + 
				"		ALAMUT_MODE=\"list\"\n" + 
				"	else\n" + 
				"		ALAMUT_IDXS=\"$ALAMUT_IDXS,$i\"\n" + 
				"		ALAMUT_COLTYPES=\"$ALAMUT_COLTYPES,text\"\n" + 
				"		ALAMUT_MODE=\"$ALAMUT_MODE,list\"\n" + 
				"	fi\n" + 
				"done"
				+ "\n"
				+ "\n"
				+ "ALAMUT_COLS=$(zcat " + bedFile + " | head -n 1 | cut -f $start-999 | tr \"\\t\" \",\")"
				+ "");


		// save in combined object
		if (options.getFirst() <= 9 && options.getLast() >= 9 ) {
			combined.addCmd05(prepColCmd);
		}
	}



	//// update gemini with alamut

	public void updateGemini() {


		ArrayList<String> updateGeminiCmd = new ArrayList<>();

		updateGeminiCmd.add("gemini annotate " + combined.getDbName() + " -f " + bedFile + " -a extract -c $ALAMUT_COLS -e $ALAMUT_IDXS -t $ALAMUT_COLTYPES -o $ALAMUT_MODE");



		// save in combined object
		if (options.getFirst() <= 9 && options.getLast() >= 9 ) {
			combined.addCmd05(updateGeminiCmd);
		}



	}







	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
