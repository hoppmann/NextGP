package de.NextGP.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class Writer {

	//////////////////
	//////// Variables
	private PrintWriter writer;








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
			Log.error( "Can not create file for writing");
			System.exit(1);
		}
	}

	
	
	
	
	/////// write in file
	public void writeLine (String line) {
		writer.println(line);
	}

	
	// write slurm option
	public void writeOption (String line) {
		writer.println("#SBATCH " + line);
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
		writer.println("echo -e \"\\n\\n\\n\\n\\n\"");
		writer.println("");
	}

	
	
	

	//////// close writer
	public void close() {
		writer.close();
	}




}








