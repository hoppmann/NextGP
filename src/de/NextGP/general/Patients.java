package de.NextGP.general;

import java.util.ArrayList;


public class Patients {

	///////////////////////////
	//////// Variables ////////
	///////////////////////////
	
	private String forward;
	private String backward;
	private String lastOutFile;
	
	
	
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
//	private ArrayList<String>
	
	
	/////////////////////////////
	//////// Constructor ////////
	/////////////////////////////
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
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
	public ArrayList<String> getBwa() {
		return bwa;
	}
	
	public void setBwa(ArrayList<String> bwa) {
		this.bwa = bwa;
	}
	public ArrayList<String> getSamToBam() {
		return samToBam;
	}
	public void setSamToBam(ArrayList<String> samToBam) {
		this.samToBam = samToBam;
	}
	public ArrayList<String> getRealignmentTargetCreator() {
		return realignmentTargetCreator;
	}
	public void setRealignmentTargetCreator(
			ArrayList<String> realignmentTargetCreator) {
		this.realignmentTargetCreator = realignmentTargetCreator;
	}
	public ArrayList<String> getIndelRealigner() {
		return indelRealigner;
	}
	public void setIndelRealigner(ArrayList<String> indelRealigner) {
		this.indelRealigner = indelRealigner;
	}
	public ArrayList<String> getAddOrReplaceReadgroups() {
		return addOrReplaceReadgroups;
	}
	public void setAddOrReplaceReadgroups(ArrayList<String> addOrReplaceReadgroups) {
		this.addOrReplaceReadgroups = addOrReplaceReadgroups;
	}
	public String getLastOutFile() {
		return lastOutFile;
	}
	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}
	public ArrayList<String> getBaseRecalibration_pre() {
		return baseRecalibration_pre;
	}
	public void setBaseRecalibration_pre(ArrayList<String> baseRecalibration_pre) {
		this.baseRecalibration_pre = baseRecalibration_pre;
	}
	public ArrayList<String> getBaseRecalibration_post() {
		return baseRecalibration_post;
	}
	public void setBaseRecalibration_post(ArrayList<String> baseRecalibration_post) {
		this.baseRecalibration_post = baseRecalibration_post;
	}
	public ArrayList<String> getAnalyzeCovariates() {
		return analyzeCovariates;
	}
	public void setAnalyzeCovariates(ArrayList<String> analyzeCovariates) {
		this.analyzeCovariates = analyzeCovariates;
	}
	public ArrayList<String> getPrintReads() {
		return printReads;
	}
	public void setPrintReads(ArrayList<String> printReads) {
		this.printReads = printReads;
	}
	public ArrayList<String> getStatsMeanDepth() {
		return statsMeanDepth;
	}
	public void setStatsMeanDepth(ArrayList<String> statsMeanDepth) {
		this.statsMeanDepth = statsMeanDepth;
	}
	public ArrayList<String> getStatsAsMetric() {
		return statsAsMetric;
	}
	public void setStatsAsMetric(ArrayList<String> statsAsMetric) {
		this.statsAsMetric = statsAsMetric;
	}
	public ArrayList<String> getStatsGCBMetric() {
		return statsGCBMetric;
	}
	public void setStatsGCBMetric(ArrayList<String> statsBCBMetric) {
		this.statsGCBMetric = statsBCBMetric;
	}
	public ArrayList<String> getStatsISMetric() {
		return statsISMetric;
	}
	public void setStatsISMetric(ArrayList<String> statsISMetric) {
		this.statsISMetric = statsISMetric;
	}
	public ArrayList<String> getStatsCoverage() {
		return statsCoverage;
	}
	public void setStatsCoverage(ArrayList<String> statscoverage) {
		this.statsCoverage = statscoverage;
	}
	public ArrayList<String> getHaplotypeCaller() {
		return haplotypeCaller;
	}
	public void setHaplotypeCaller(ArrayList<String> haplotypeCaller) {
		this.haplotypeCaller = haplotypeCaller;
	}
	
	
	
	
	
}
