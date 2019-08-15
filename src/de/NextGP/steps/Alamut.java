package de.NextGP.steps;

import java.io.File;
import java.util.ArrayList;

import de.NextGP.general.outfiles.Combined;
import de.NextGP.initialize.LoadConfig;
import de.NextGP.initialize.options.GetOptions;

public class Alamut {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
//	private static  Logger logger = LoggerFactory.getLogger(Alamut.class);

	GetOptions options; 
	LoadConfig config;
	Combined combined;
	String alamutOut;
	String bedFile;
	String bedFileZip;
	

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Alamut(GetOptions options, LoadConfig config, Combined combined) {

		this.options = options;
		this.config = config;
		this.combined = combined;


		// make log entry
		System.out.println("Preparing annotation with alamut");


		//// execute all parts needed to annotate with alamut
		runAlamut();
		prepareBedFile();
		tabixBed();
		prepareColumns();
		annotateGeminiWithAlamut();
		annotateInAlamutBool();


	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	//////// annotate the unannotated  variants with Alamut and HGMD
	public void runAlamut() {


		//// prepare out file name
		String sep = File.separator;
		String outDir =  options.getOutDir() + sep + config.getAnnotation() + sep + "Alamut";
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
		alamutCmd.add("--dogenesplicer");
		alamutCmd.add("--donnsplice");
		alamutCmd.add("--outputannonly");
		alamutCmd.add("--ann " + alamutOut);
		alamutCmd.add("--unann "  + alamutLog);

		// save in combined object
		Integer step = options.getSteps().get("alamut");

		if ( options.getFirst() <= step && options.getLast() >= step ) {
			if (! options.isSkiptAlamutBatch()) {
				combined.addAlamutCmd(alamutCmd);
			}
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
				"}' | cut -f 1-3,9-999 | bgzip > " + bedFile);

		// save in combined object
		Integer step = options.getSteps().get("alamut");

		if (options.getFirst() <= step && options.getLast() >= step ) {
			combined.addUpdateGeminiCmd(formBedCmd);
		}
	}



	// tabix bed file for later use with gemini
	public void tabixBed () {


		// prepare command
		ArrayList<String> tabixCmd = new ArrayList<>();

		tabixCmd.add("tabix -p bed " + bedFile);

		// save in combined object
		Integer step = options.getSteps().get("alamut");

		if (options.getFirst() <= step && options.getLast() >= step ) {
			combined.addUpdateGeminiCmd(tabixCmd);
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
				"\n" + 
				"function chooseType() {\n" + 
				"\n" + 
				"	if [[ $1 == *gnomad* ]]; then\n" + 
				"		colMode=\"max\"\n" + 
				"		colType=\"float\"\n" + 
				"	elif [[ $1 == *1000g* ]]; then\n" + 
				"		colMode=\"max\"\n" + 
				"		colType=\"float\"\n" + 
				"\n" + 
				"	elif [[ $1 ==  *esp* ]]; then\n" + 
				"		colMode=\"max\"\n" + 
				"		colType=\"float\"\n" + 
				"\n" + 
				"	elif [[ $1 == *rsId* ]]; then\n" + 
				"		colMode=\"first\"\n" + 
				"		colType=text\n" + 
				"	else\n" + 
				"		colMode=\"list\"\n" + 
				"		colType=\"text\"\n" + 
				"	fi\n" + 
				"\n" + 
				"}\n" + 
				"\n" + 
				"\n" + 
				"start=3\n" + 
				"\n" + 
				"\n" + 
				"#### get all header besides chrom start stop\n" + 
				"ALAMUT_COLS=$(zcat " + bedFile + " | head -n 1 | cut -f $start-999 | tr \"\\t\" \",\")\n" + 
				"IFS=',' read -r -a ALAMUT_ARRAY <<< \"$ALAMUT_COLS\"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"nAnno=$(zcat " + bedFile + " | head -n 1 | wc | awk '{ print $2 }')\n" + 
				"\n" + 
				"for (( i=$start; i<=$nAnno; i++ ))\n" + 
				"do\n" + 
				"\n" + 
				"	chooseType \"${ALAMUT_ARRAY[$i - 4]}\"\n" + 
				"\n" + 
				"	if [ $i -eq $start ]\n" + 
				"	then\n" + 
				"		ALAMUT_IDXS=$i\n" + 
				"		ALAMUT_COLTYPES=$colType\n" + 
				"		ALAMUT_MODE=$colMode\n" + 
				"	else\n" + 
				"		ALAMUT_IDXS=\"$ALAMUT_IDXS,$i\"\n" + 
				"		ALAMUT_COLTYPES=\"$ALAMUT_COLTYPES,$colType\"\n" + 
				"		ALAMUT_MODE=\"$ALAMUT_MODE,$colMode\"\n" + 
				"	fi\n" + 
				"done");


		// save in combined object
		Integer step = options.getSteps().get("alamut");

		if (options.getFirst() <= step && options.getLast() >= step ) {
			combined.addUpdateGeminiCmd(prepColCmd);
		}
	}



	
	
	
	
	//////////////////////////////
	//// update gemini with alamut

	public void annotateGeminiWithAlamut() {


		ArrayList<String> updateGeminiCmd = new ArrayList<>();

		updateGeminiCmd.add("gemini annotate ");
		updateGeminiCmd.add("-f " + bedFile);
		updateGeminiCmd.add("-a extract");
		updateGeminiCmd.add("-c $ALAMUT_COLS");
		updateGeminiCmd.add("-e $ALAMUT_IDXS");
		updateGeminiCmd.add("-t $ALAMUT_COLTYPES");
		updateGeminiCmd.add("-o $ALAMUT_MODE");


		
		
		// save in combined object
		Integer step = options.getSteps().get("alamut");

		if (options.getFirst() <= step && options.getLast() >= step ) {
			combined.addUpdateGeminiCmd(updateGeminiCmd);
		}



	}

	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	//// add boolean to database containing if an Alamut annotation is available or not
	
	public void annotateInAlamutBool () {
		
		ArrayList<String> addOverlapCmd = new ArrayList<>();
		
		addOverlapCmd.add("gemini annotate");
		addOverlapCmd.add("-f " + bedFile);
		addOverlapCmd.add("-c inAlamut");
		addOverlapCmd.add("-a boolean");
		addOverlapCmd.add(combined.getDbName());
		
		
		Integer step = options.getSteps().get("alamut");
		
		if (options.getFirst() <= step && options.getLast() >= step ) {
			combined.addUpdateGeminiCmd(addOverlapCmd);
		}
		
		
		
	}
	
	
	






	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
