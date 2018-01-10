package de.NextGP.general.outfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Patients {

	///////////////////////////
	//////// Variables ////////
	///////////////////////////
	
	/////////////////////////
	//////// Prior combined gatk calling
	private String forward;
	private String reverse;
	private String lastOutFile;
	private String bam;
	private Map<String, String> vcfFiles = new HashMap<>();
	private List<ArrayList<String>> cmds02 = new LinkedList<>();
	private List<ArrayList<String>> cmds04 = new LinkedList<>();
	
	
	
	/////////////////////////////
	//////// Constructor ////////
	/////////////////////////////
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////
	
	
	// save methods for commands run in file 02
	
	public void addCmd02 (ArrayList<String> cmd) {
		cmds02.add(cmd);
	}
	
	// save methods for commands run in file 04
	public void addCmd04 (ArrayList<String> cmd) {
		cmds04.add(cmd);
	}

	// make entry in vcf hash connecting platform and vcf file
	public void setVcfFiles(String caller, String file) {
		
		vcfFiles.put(caller, file);
		
	}

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
	
	
	
	/////////////
	//////// general stuff
	
	public String getForward() {
		return forward;
	}
	public void setForward(String forward) {
		this.forward = forward;
	}
	
	
	public String getReverse() {
		return reverse;
	}
	public void setReverse(String reverse) {
		this.reverse = reverse;
	}
	
	public String getLastOutFile() {
		return lastOutFile;
	}
	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}
	
	public String getBam() {
		return bam;
	}
	public void setBam(String bam) {
		this.bam = bam;
	}
	
	public Map<String, String> getVcfFiles() {
		return vcfFiles;
	}

	public void setVcfFiles(Map<String, String> vcfFiles) {
		this.vcfFiles = vcfFiles;
	}

	public List<ArrayList<String>> getCmds02() {
		return cmds02;
	}

	public void setCmds02(List<ArrayList<String>> cmds02) {
		this.cmds02 = cmds02;
	}

	public List<ArrayList<String>> getCmds04() {
		return cmds04;
	}

	public void setCmds04(List<ArrayList<String>> cmds04) {
		this.cmds04 = cmds04;
	}

	
	
	
	
	
	
}
