package de.NextGP.general;

import java.util.ArrayList;

public class Combined {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	
	String rawVCF;
	String lastOutFile;
	
	
	// commands
	private ArrayList<String> genotypeGVCF;
	private ArrayList<String> variantRecalibration;
	private ArrayList<String> applyRecalibration;
	private ArrayList<String> extractSnpSet;
	private ArrayList<String> hardFiterSnpSet;
	private ArrayList<String> extractIndelSet;
	private ArrayList<String> hardFilterIndelSet;
	private ArrayList<String> combineVariants;
	private ArrayList<String> removeFilteredReads;
	private ArrayList<String> extractInd;
	private ArrayList<String> vepAnnotation;
	private ArrayList<String> vtMaster;
	private ArrayList<String> varEval;
	private ArrayList<String> geminiLoad;
	
	private ArrayList<String> mkDirs = new ArrayList<>();
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void mkdir (String mkdirCmd) {
		mkDirs.add(mkdirCmd);
	}
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////

	public ArrayList<String> getGenotypeGVCF() {
		return genotypeGVCF;
	}
	
	public void setGenotypeGVCF(ArrayList<String> genotypeGVCF) {
		this.genotypeGVCF = genotypeGVCF;
	}
	
	public ArrayList<String> getVariantRecalibration() {
		return variantRecalibration;
	}
	
	public void setVariantRecalibration(ArrayList<String> variantRecalibration) {
		this.variantRecalibration = variantRecalibration;
	}
	
	public ArrayList<String> getApplyRecalibration() {
		return applyRecalibration;
	}
	
	public void setApplyRecalibration(ArrayList<String> applyRecalibration) {
		this.applyRecalibration = applyRecalibration;
	}
	
	public String getRawVCF() {
		return rawVCF;
	}
	
	public void setRawVCF(String rawVCF) {
		this.rawVCF = rawVCF;
	}
	
	public ArrayList<String> getExtractSnpSet() {
		return extractSnpSet;
	}
	
	public void setExtractSnpSet(ArrayList<String> extractSnpSet) {
		this.extractSnpSet = extractSnpSet;
	}
	
	public ArrayList<String> getHardFiterSnpSet() {
		return hardFiterSnpSet;
	}
	
	public void setHardFiterSnpSet(ArrayList<String> hardFiterSnpSet) {
		this.hardFiterSnpSet = hardFiterSnpSet;
	}

	public ArrayList<String> getExtractIndelSet() {
		return extractIndelSet;
	}
	
	public void setExtractIndelSet(ArrayList<String> extractIndelSet) {
		this.extractIndelSet = extractIndelSet;
	}
	
	public ArrayList<String> getHardFilterIndelSet() {
		return hardFilterIndelSet;
	}
	
	public void setHardFilterIndelSet(ArrayList<String> hardFilterIndelSet) {
		this.hardFilterIndelSet = hardFilterIndelSet;
	}
	
	public ArrayList<String> getCombineVariants() {
		return combineVariants;
	}

	public void setCombineVariants(ArrayList<String> combineVariants) {
		this.combineVariants = combineVariants;
	}

	public ArrayList<String> getRemoveFilteredReads() {
		return removeFilteredReads;
	}
	
	public void setRemoveFilteredReads(ArrayList<String> removeFilteredReads) {
		this.removeFilteredReads = removeFilteredReads;
	}
	
	public ArrayList<String> getExtractInd() {
		return extractInd;
	}
	
	public void setExtractInd(ArrayList<String> extractInd) {
		this.extractInd = extractInd;
	}
	
	public String getLastOutFile() {
		return lastOutFile;
	}

	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}

	public ArrayList<String> getVepAnnotation() {
		return vepAnnotation;
	}

	public void setVepAnnotation(ArrayList<String> vepAnnotation) {
		this.vepAnnotation = vepAnnotation;
	}

	public ArrayList<String> getVtMaster() {
		return vtMaster;
	}

	public void setVtMaster(ArrayList<String> prepareVEP) {
		this.vtMaster = prepareVEP;
	}

	public ArrayList<String> getVarEval() {
		return varEval;
	}

	public void setVarEval(ArrayList<String> varEval) {
		this.varEval = varEval;
	}

	public ArrayList<String> getGeminiLoad() {
		return geminiLoad;
	}

	public void setGeminiLoad(ArrayList<String> geminiLoad) {
		this.geminiLoad = geminiLoad;
	}

	public ArrayList<String> getMkDirs() {
		return mkDirs;
	}

	public void setMkDirs(ArrayList<String> mkDirs) {
		this.mkDirs = mkDirs;
	}

	
	

}