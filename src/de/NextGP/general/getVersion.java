package de.NextGP.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.NextGP.initialize.LoadConfig;

public class getVersion {
	
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	Logger logger = LoggerFactory.getLogger(getVersion.class);
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	//// Picard version
	public String getPicardVersion(LoadConfig config){
		

		// get program info
		String picard = config.getPicard();
		String java = config.getJava();

		// prepare command
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(java);
		cmd.add("-jar");
		cmd.add(picard);
		cmd.add("SortSam");
		cmd.add("--version");
		
		// execute command and get version number
		String version = null;
		try {
			String line;
			ProcessBuilder builder = new ProcessBuilder(cmd);
			Process proc = builder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			while ( ( line = br.readLine()) != null) {
				version= line.split("\\(")[0];
			}
			
		} catch (IOException e) {
			Log.error(logger, "Can't get Picard version!");
			System.exit(1);
		}
		
		
		return version;
		
		
	}
	
	
	//// BWA version
	public String getBwaVersion(LoadConfig config){
		
		// get program path
				String bwa = config.getBwa();
				
				// prepare command
				ArrayList<String> cmd = new ArrayList<>();
				cmd.add(bwa);
				
				Log.cmd(cmd);
				// execute command and extract version number
				String version = null;
				
				try {
					String line;
					ProcessBuilder builder = new ProcessBuilder(cmd);
					Process proc = builder.start();
					
					// read in prog output
					BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
					while ((line = br.readLine()) != null) {
						
						// extract line with version number
						if (line.startsWith("Version")){
							version = line.split(" ")[1];
						}
					}
					
				} catch (IOException e) {
					Log.error(logger, "Can't get BWA version");
					System.exit(2);
				}
				

				// return version number
				
				return version;
		
	}

	
	//// GATK version
	public String getGatkVersion (LoadConfig config) {
		
		String version = null;
		return version;
	}
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
