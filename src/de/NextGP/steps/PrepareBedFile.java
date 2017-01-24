package de.NextGP.steps;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.Writer;
import de.NextGP.initialize.options.GetOptions;

public class PrepareBedFile {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetOptions options;
	private static Logger logger = LoggerFactory.getLogger(PrepareBedFile.class);

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public PrepareBedFile(GetOptions options) {
		this.options = options;
		// make log entry for starting IonProton pipeline
		Log.logger(logger, "Preparing .bed file.");
		
	}
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	public void createBed() {
		
		String sep = File.separator;

		
		// prepare bed file as needed for Platypus
		String bedFileIn = options.getBedFile();
		
		String bedName = new File(bedFileIn).getName();
		String bedFileOut = options.getSlurmDir() + sep + "00-" + bedName;
		
		
		Writer bedOut = new Writer();
		bedOut.openWriter(bedFileOut);

		LinkedList<String> lines = new Writer().openFile(bedFileIn);
		ListIterator<String> listIterator = lines.listIterator();

		// iterate over all lines
		while (listIterator.hasNext()){

			// get next line
			String curLine = listIterator.next();


			String[] splitLine = curLine.split("\\t");



			// check number of splits (header not allways defined by symbol but has only few entries.
			if (splitLine.length <= 1){
				bedOut.writeLine(curLine);
			} else {
				bedOut.writeLine(splitLine[0] + "\t" + splitLine[1] + "\t" + splitLine[2]);
			}
		}
		
		// close writer
		bedOut.close();
		
		// change saved bed file name to new bed file name
		options.setBedFile(bedFileOut);
		

	}
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
