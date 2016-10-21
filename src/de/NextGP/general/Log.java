package de.NextGP.general;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.slf4j.Logger;

public final class Log {

	//////////////////////
	//////// set variables
	private static PrintWriter writer;


	///////////////////
	//////// Construcor

	static {
	}


	////////////////
	//////// Methods


	//writes handed over String to file
	public static void file(String line) {
		writer.println(line);
	}

	//writes line in file and on screen
	public static void logger(Logger logger, String line){
		System.out.println(line);
		logger.info(line);
	}

	//write ERROR in file and screen and close file
	public static void error(Logger logger, String line) {
		System.out.println("\n######## ERROR: " + line);
		logger.error(line);
		
	}

	// print out on screen only
	public static void screen(String line){
		System.out.println(line);
	}

	// close file handle
	public static void closeFile() {
		writer.close();
	}


	// write command in bash file
	public static void cmd(ArrayList<String> cmd){
		int counter = 1;
		for (String line : cmd) {
			// only ad \ if not last line
			if (counter++ == cmd.size()){
				writer.println(line);
			} else {
				writer.println(line + " \\");
			}
		}
		
		// add empty line
		writer.println();

	}



}
