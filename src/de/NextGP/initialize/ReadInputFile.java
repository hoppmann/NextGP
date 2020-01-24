package de.NextGP.initialize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import de.NextGP.general.Writer;
import de.NextGP.general.outfiles.Patients;

public class ReadInputFile {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private Map<String, Patients> patients = new HashMap<>();
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ReadInputFile() {

		System.out.println("Reading input file");
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
			pat.setReverse(backward);
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
			if ( splitLine.length < 2){
				System.out.println("Mal formed bam list!");
				System.exit(1);
			}
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

	
	
	
	
	
	////////////////////////////
	//////// read VCF list file
	
	public Map<String, Patients> readVcfList (String listName) {
		
		
		
		LinkedList<String> lines = new Writer().openFile(listName);
		ListIterator<String> listIterator = lines.listIterator();
		while (listIterator.hasNext()) {
			
			String curLine = listIterator.next();
			
			// check if line start with # or is empty
			if (curLine.isEmpty() || curLine.startsWith("#")) {
				continue;
			}
			
			// if is real line split and sort to patient
			String[] splitLine = curLine.split("\t");
			if (splitLine.length < 2) {
				System.out.println("Mal formed vcf list!");
				System.exit(1);
			}

			String patID = splitLine[0];
			String vcf = splitLine[1];
			
			
			// save to corresponding patient
			Patients pat = new Patients();
			pat.setLastOutFile(vcf);
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
