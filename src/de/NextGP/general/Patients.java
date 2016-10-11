package de.NextGP.general;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Patients {

	///////////////////////////
	//////// Variables ////////
	///////////////////////////
	
	private String forward;
	private String backward;
	private String lastOutFile;
	private List<Method> commands = new LinkedList<>();
	
	
	
	//////// commands
	private ArrayList<String> bwa;
	private ArrayList<String> samToBam;
	private ArrayList<String> realignmentTargetCreator;
	private ArrayList<String> indelRealigner;
	private ArrayList<String> addOrReplaceReadgroups;
	private ArrayList<String> baseRecalibration_pre;
	private ArrayList<String> baseRecalibration_post;
	private ArrayList<String> analyzeCovariates;
	private ArrayList<String> printReads;
	private ArrayList<String> statsMeanDepth;
	private ArrayList<String> statsAsMetric;
	private ArrayList<String> statsGCBMetric;
	private ArrayList<String> statsISMetric;
	private ArrayList<String> statsCoverage;
	private ArrayList<String> haplotypeCaller;
	
	
	/////////////////////////////
	//////// Constructor ////////
	/////////////////////////////
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////
	
	
	
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
	
	
	public String getBackward() {
		return backward;
	}
	public void setBackward(String backward) {
		this.backward = backward;
	}
	
	public List<Method> getCommands() {
		return commands;
	}
	public void setCommands(List<Method> commands) {
		this.commands = commands;
	}
	
	public String getLastOutFile() {
		return lastOutFile;
	}
	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}
	
	
	
	///////////
	//////// Commands
	
	
	public ArrayList<String> getBwa() {
		return bwa;
	}
	public void setBwa(ArrayList<String> bwa) {
		this.bwa = bwa;
		try {
			commands.add(Patients.class.getMethod("getBwa"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getSamToBam() {
		return samToBam;
	}
	public void setSamToBam(ArrayList<String> samToBam) {
		this.samToBam = samToBam;
		try {
			commands.add(Patients.class.getMethod("getSamToBam"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getRealignmentTargetCreator() {
		return realignmentTargetCreator;
	}
	public void setRealignmentTargetCreator(
			ArrayList<String> realignmentTargetCreator) {
		this.realignmentTargetCreator = realignmentTargetCreator;
		try {
			commands.add(Patients.class.getMethod("getRealignmentTargetCreator"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getIndelRealigner() {
		return indelRealigner;
	}
	public void setIndelRealigner(ArrayList<String> indelRealigner) {
		this.indelRealigner = indelRealigner;
		try {
			commands.add(Patients.class.getMethod("getIndelRealigner"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getAddOrReplaceReadgroups() {
		return addOrReplaceReadgroups;
	}
	public void setAddOrReplaceReadgroups(ArrayList<String> addOrReplaceReadgroups) {
		this.addOrReplaceReadgroups = addOrReplaceReadgroups;
		try {
			commands.add(Patients.class.getMethod("getAddOrReplaceReadgroups"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getBaseRecalibration_pre() {
		return baseRecalibration_pre;
	}
	public void setBaseRecalibration_pre(ArrayList<String> baseRecalibration_pre) {
		this.baseRecalibration_pre = baseRecalibration_pre;
		try {
			commands.add(Patients.class.getMethod("getBaseRecalibration_pre"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getBaseRecalibration_post() {
		return baseRecalibration_post;
	}
	public void setBaseRecalibration_post(ArrayList<String> baseRecalibration_post) {
		this.baseRecalibration_post = baseRecalibration_post;
		try {
			commands.add(Patients.class.getMethod("getBaseRecalibration_post"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getAnalyzeCovariates() {
		return analyzeCovariates;
	}
	public void setAnalyzeCovariates(ArrayList<String> analyzeCovariates) {
		this.analyzeCovariates = analyzeCovariates;
		try {
			commands.add(Patients.class.getMethod("getAnalyzeCovariates"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getPrintReads() {
		return printReads;
	}
	public void setPrintReads(ArrayList<String> printReads) {
		this.printReads = printReads;
		try {
			commands.add(Patients.class.getMethod("getPrintReads"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getStatsMeanDepth() {
		return statsMeanDepth;
	}
	public void setStatsMeanDepth(ArrayList<String> statsMeanDepth)  {
		this.statsMeanDepth = statsMeanDepth;
		try {
			commands.add(Patients.class.getMethod("getStatsMeanDepth"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getStatsAsMetric() {
		return statsAsMetric;
	}
	public void setStatsAsMetric(ArrayList<String> statsAsMetric) {
		this.statsAsMetric = statsAsMetric;
		try {
			commands.add(Patients.class.getMethod("getStatsAsMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getStatsGCBMetric() {
		return statsGCBMetric;
	}
	public void setStatsGCBMetric(ArrayList<String> statsBCBMetric) {
		this.statsGCBMetric = statsBCBMetric;
		try {
			commands.add(Patients.class.getMethod("getStatsGCBMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getStatsISMetric() {
		return statsISMetric;
	}
	public void setStatsISMetric(ArrayList<String> statsISMetric) {
		this.statsISMetric = statsISMetric;
		try {
			commands.add(Patients.class.getMethod("getStatsISMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getStatsCoverage() {
		return statsCoverage;
	}
	public void setStatsCoverage(ArrayList<String> statscoverage) {
		this.statsCoverage = statscoverage;
		try {
			commands.add(Patients.class.getMethod("getStatsCoverage"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getHaplotypeCaller() {
		return haplotypeCaller;
	}
	public void setHaplotypeCaller(ArrayList<String> haplotypeCaller) {
		this.haplotypeCaller = haplotypeCaller;
		try {
			commands.add(Patients.class.getMethod("getHaplotypeCaller"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
}
