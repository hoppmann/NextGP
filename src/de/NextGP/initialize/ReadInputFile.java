package de.NextGP.initialize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.general.Log;
import de.NextGP.general.Patients;
import de.NextGP.general.Writer;

public class ReadInputFile {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private Map<String, Patients> patients = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(ReadInputFile.class);
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ReadInputFile() {

		Log.logger(logger, "Reading input file");
	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	////////////////
	//////// read mate fastq files
	public Map<String, Patients> readMateFastqFile(String listName) {
		// read in fastq file list
		LinkedList<String> lines = new Writer().openFile(listName); 

		// sort reads to corresponding patients
		ListIterator<String> listIterator = lines.listIterator();
		while (listIterator.hasNext()) {

			String curLine = listIterator.next();

			// check if line starts with # or is empty
			if (curLine.isEmpty() || curLine.startsWith("#")) {
				continue;
			} 

			// if it is real line split and sort to patient
			String[] splitLine = curLine.split("\t");
			String patID = splitLine[0];
			String forward = splitLine[1];
			String backward = splitLine[2];

			Patients pat = new Patients();
			pat.setForward(forward);
			pat.setBackward(backward);
			patients.put(patID, pat);


		}

		// return Map
		return patients;
	}


	////////////////
	//////// read fastq files
	public Map<String, Patients> readSingleFastqFile(String listName) {
		// read in fastq file list
		LinkedList<String> lines = new Writer().openFile(listName); 

		// sort reads to corresponding patients
		ListIterator<String> listIterator = lines.listIterator();
		while (listIterator.hasNext()) {

			String curLine = listIterator.next();

			// check if line starts with # or is empty
			if (curLine.isEmpty() || curLine.startsWith("#")) {
				continue;
			} 

			// if it is real line split and sort to patient
			String[] splitLine = curLine.split("\t");
			String patID = splitLine[0];
			String forward = splitLine[1];

			// save to patient
			Patients pat = new Patients();
			pat.setForward(forward);
			patients.put(patID, pat);

		}

		// return Map
		return patients;
	}


	////////////////////
	//////// read bam list file
	public Map<String, Patients> readBamList(String listName) {
		// read in bam file list
		LinkedList<String> lines = new Writer().openFile(listName);

		// sort reads to corresponding patient
		ListIterator<String> listIterator = lines.listIterator();
		while (listIterator.hasNext()) {

			String curLine = listIterator.next();

			// check if line starts with # or is empty
			if (curLine.isEmpty() || curLine.startsWith("#")) {
				continue;
			}

			// if it is real line split and sort to patient
			String[] splitLine = curLine.split("\t");
			String patID = splitLine[0];
			String bam = splitLine[1];

			// save to corresponding patient
			Patients pat = new Patients();
			pat.setLastOutFile(bam);
			patients.put(patID, pat);

		}

		// return patient map
		return patients;

	}


	/////////////
	//////// File reader
	public LinkedList<String> openFile(String listName) {

		LinkedList<String> lines = new Writer().openFile(listName);

		return lines;
	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
