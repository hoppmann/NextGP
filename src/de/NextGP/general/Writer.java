package de.NextGP.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Writer {

	//////////////////
	//////// Variables
	private PrintWriter writer;
	private static Logger logger = LoggerFactory.getLogger(Writer.class);








	////////////////////
	//////// Constructor






	////////////////
	//////// Methods

	//////// open text file and read in
	public LinkedList<String> openFile(String filePath) {

		// prepare variables
		BufferedReader br = null;
		String line;
		LinkedList<String> lines = new LinkedList<>();
		try {
			// read in file
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();

		} catch (Exception e1) {
			System.out.println("Failed open file " + filePath);
			System.exit(1);
		}

		// return list of lines
		return lines;
	}

	
	
	
	
	
	
	//////// open new file
	public void openWriter(String file) {
		// open new printwriter
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			Log.error(logger, "Can not create file for writing");
			System.exit(1);
		}
	}

	
	
	
	
	/////// write in file
	public void writeLine (String line) {
		writer.println(line);
	}

	
	
	
	
	//////// write command
	public void writeCmd(ArrayList<String> cmd){

		writer.println("");
		
		
		int counter = 1;
		for (String line : cmd) {
			// only add \ if not last line
			if (counter++ == cmd.size()){
				writer.println(line);
			} else {
				writer.println(line + " \\");
			}
		}
		
		writer.println("\n");
		writer.println("echo \"\\n\\n\\n\"");
		writer.println("");
	}

	
	
	

	//////// close writer
	public void close() {
		writer.close();
	}




}








