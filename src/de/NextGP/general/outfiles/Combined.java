package de.NextGP.general.outfiles;

import java.util.ArrayList;
import java.util.LinkedList;

public class Combined {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private String combinedVcfFile;
	private String lastOutFile;
	private String vcfForAnnotation;
	private boolean matePair;
	private String dbName;


	// command collections
	private ArrayList<String> mkDirs = new ArrayList<>();
	private LinkedList<ArrayList<String>> cmds03 = new LinkedList<>(); 
	private LinkedList<ArrayList<String>> cmds05 = new LinkedList<>();




	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////




	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void mkdir (String mkdirCmd) {
		mkDirs.add(mkdirCmd);
	}

	
	public void addCmd05 (ArrayList<String> cmd) {
		
		cmds05.add(cmd);
		
	}
	
	public void addCmd03 (ArrayList<String> cmd) {
		
		cmds03.add(cmd);
		
	}
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	public ArrayList<String> getMkDirs() {
		return mkDirs;
	}
	
	public LinkedList<ArrayList<String>> getCmds03() {
		return cmds03;
	}


	public LinkedList<ArrayList<String>> getCmds05() {
		return cmds05;
	}
	
	
	
	
	
	
	
	
	

	/////////////
	//////// general stuff

	public String getLastOutFile() {
		return lastOutFile;
	}

	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}

	public String getVcfForAnnotation() {
		return vcfForAnnotation;
	}

	public void setVcfForAnnotation(String vcfForAnnotation) {
		this.vcfForAnnotation = vcfForAnnotation;
	}
	
	
	public boolean isMatePair() {
		return matePair;
	}

	public void setMatePair(boolean matePair) {
		this.matePair = matePair;
	}



	public String getCombinedVcfFile() {
		return combinedVcfFile;
	}

	public void setCombinedVcfFile(String combinedVcfFile) {
		this.combinedVcfFile = combinedVcfFile;
	}


	public String getDbName() {
		return dbName;
	}


	public void setDbName(String dbName) {
		this.dbName = dbName;
	}


	







	
	



	
}