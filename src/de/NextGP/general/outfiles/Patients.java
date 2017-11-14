package de.NextGP.general.outfiles;

import java.lang.reflect.Method;
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
	private List<Method> primaryCommands = new LinkedList<>();
	private String bam;
	
	
	//////// commands
	private ArrayList<String> afterQC;
	private ArrayList<String> bwa;
	private ArrayList<String> samToBam;
	private ArrayList<String> realignmentTargetCreator;
	private ArrayList<String> indelRealigner;
	private ArrayList<String> addOrReplaceReadgroups;
	private ArrayList<String> removeDuplicates;
	private ArrayList<String> baseRecalibration_pre;
	private ArrayList<String> baseRecalibration_post;
	private ArrayList<String> analyzeCovariates;
	private ArrayList<String> printReads;
	private ArrayList<String> fastQC;
	private ArrayList<String> statsMeanDepth;
	private ArrayList<String> statsAsMetric;
	private ArrayList<String> statsGCBMetric;
	private ArrayList<String> statsISMetric;
	private ArrayList<String> statsCoverage;
	private ArrayList<String> haplotypeCaller;
	private ArrayList<String> extractInd;
	private ArrayList<String> sortHaploCaller;
	private ArrayList<String> mpileup;
	private ArrayList<String> sortMpileup;
	private ArrayList<String> bgzipSamtools;
	private ArrayList<String> platypus;
	private ArrayList<String> sortPlatypus;
	private ArrayList<String> filterPlatypus;
	private ArrayList<String> freebayes;
	private ArrayList<String> sortFreebayes;
	private ArrayList<String> variantEval;
	
	
	////////////////////////
	//////// post combined gatk calling
	
	private List<Method> secondaryCommands = new LinkedList<>();
	private Map<String, String> vcfFiles = new HashMap<>();
	

	
	//////// commands
	private ArrayList<String> sampleNameModSamtools;
	private ArrayList<String> sampleNameModPlatypus;
	private ArrayList<String> sampleNameModGATK;
	private ArrayList<String> sampleNameModFreebayes;
	private ArrayList<String> combineVariants;
	private ArrayList<String> consensus;
	
	
	
	/////////////////////////////
	//////// Constructor ////////
	/////////////////////////////
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

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
	
	public List<Method> getPrimaryCommands() {
		return primaryCommands;
	}
	public void setPrimaryCommands(List<Method> primaryCommands) {
		this.primaryCommands = primaryCommands;
	}
	
	
	public List<Method> getSecondaryCommands() {
		return secondaryCommands;
	}

	public void setSecondaryCommands(List<Method> secondaryCommands) {
		this.secondaryCommands = secondaryCommands;
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

	
	///////////
	//////// Commands



	public ArrayList<String> getAfterQC() {
		return afterQC;
	}

	public void setAfterQC(ArrayList<String> afterQC) {
		this.afterQC = afterQC;
		try {
			primaryCommands.add(Patients.class.getMethod("getAfterQC"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	public ArrayList<String> getBwa() {
		return bwa;
	}
	public void setBwa(ArrayList<String> bwa) {
		this.bwa = bwa;
		try {
			primaryCommands.add(Patients.class.getMethod("getBwa"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getSamToBam() {
		return samToBam;
	}
	public void setSamToBam(ArrayList<String> samToBam) {
		this.samToBam = samToBam;
		try {
			primaryCommands.add(Patients.class.getMethod("getSamToBam"));
		} catch (NoSuchMethodException | SecurityException e) {
			
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
			primaryCommands.add(Patients.class.getMethod("getRealignmentTargetCreator"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getIndelRealigner() {
		return indelRealigner;
	}
	public void setIndelRealigner(ArrayList<String> indelRealigner) {
		this.indelRealigner = indelRealigner;
		try {
			primaryCommands.add(Patients.class.getMethod("getIndelRealigner"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getRemoveDuplicates () {
		return removeDuplicates;
	}

	public void setRemoveDuplicates(ArrayList<String> removeDuplicates) {
		this.removeDuplicates = removeDuplicates;
		try {
			primaryCommands.add(Patients.class.getMethod("getRemoveDuplicates"));
		} catch (NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		
		}
	}

	
	
	
	public ArrayList<String> getAddOrReplaceReadgroups() {
		return addOrReplaceReadgroups;
	}
	public void setAddOrReplaceReadgroups(ArrayList<String> addOrReplaceReadgroups) {
		this.addOrReplaceReadgroups = addOrReplaceReadgroups;
		try {
			primaryCommands.add(Patients.class.getMethod("getAddOrReplaceReadgroups"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getBaseRecalibration_pre() {
		return baseRecalibration_pre;
	}
	public void setBaseRecalibration_pre(ArrayList<String> baseRecalibration_pre) {
		this.baseRecalibration_pre = baseRecalibration_pre;
		try {
			primaryCommands.add(Patients.class.getMethod("getBaseRecalibration_pre"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getBaseRecalibration_post() {
		return baseRecalibration_post;
	}
	public void setBaseRecalibration_post(ArrayList<String> baseRecalibration_post) {
		this.baseRecalibration_post = baseRecalibration_post;
		try {
			primaryCommands.add(Patients.class.getMethod("getBaseRecalibration_post"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getAnalyzeCovariates() {
		return analyzeCovariates;
	}
	public void setAnalyzeCovariates(ArrayList<String> analyzeCovariates) {
		this.analyzeCovariates = analyzeCovariates;
		try {
			primaryCommands.add(Patients.class.getMethod("getAnalyzeCovariates"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getPrintReads() {
		return printReads;
	}
	public void setPrintReads(ArrayList<String> printReads) {
		this.printReads = printReads;
		try {
			primaryCommands.add(Patients.class.getMethod("getPrintReads"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getFastQC() {
		return fastQC;
	}

	public void setFastQC(ArrayList<String> fastQC) {
		this.fastQC = fastQC;
		
		try {
			primaryCommands.add(Patients.class.getMethod("getFastQC"));
		} catch (NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		}
	}

	
	
	
	public ArrayList<String> getStatsMeanDepth() {
		return statsMeanDepth;
	}
	public void setStatsMeanDepth(ArrayList<String> statsMeanDepth)  {
		this.statsMeanDepth = statsMeanDepth;
		try {
			primaryCommands.add(Patients.class.getMethod("getStatsMeanDepth"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getStatsAsMetric() {
		return statsAsMetric;
	}
	public void setStatsAsMetric(ArrayList<String> statsAsMetric) {
		this.statsAsMetric = statsAsMetric;
		try {
			primaryCommands.add(Patients.class.getMethod("getStatsAsMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getStatsGCBMetric() {
		return statsGCBMetric;
	}
	public void setStatsGCBMetric(ArrayList<String> statsBCBMetric) {
		this.statsGCBMetric = statsBCBMetric;
		try {
			primaryCommands.add(Patients.class.getMethod("getStatsGCBMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getStatsISMetric() {
		return statsISMetric;
	}
	public void setStatsISMetric(ArrayList<String> statsISMetric) {
		this.statsISMetric = statsISMetric;
		try {
			primaryCommands.add(Patients.class.getMethod("getStatsISMetric"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getStatsCoverage() {
		return statsCoverage;
	}
	public void setStatsCoverage(ArrayList<String> statscoverage) {
		this.statsCoverage = statscoverage;
		try {
			primaryCommands.add(Patients.class.getMethod("getStatsCoverage"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getHaplotypeCaller() {
		return haplotypeCaller;
	}
	public void setHaplotypeCaller(ArrayList<String> haplotypeCaller) {
		this.haplotypeCaller = haplotypeCaller;
		try {
			primaryCommands.add(Patients.class.getMethod("getHaplotypeCaller"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getMpileup() {
		return mpileup;
	}
	public void setMpileup(ArrayList<String> mpileup) {
		this.mpileup = mpileup;
		try {
			primaryCommands.add(Patients.class.getMethod("getMpileup"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}		
	}
	
	
	
	public ArrayList<String> getBgzipSamtools() {
		return bgzipSamtools;
	}
	public void setBgzipSamtools(ArrayList<String> bgzipSamtools) {
		this.bgzipSamtools = bgzipSamtools;
		try {
			primaryCommands.add(Patients.class.getMethod("getBgzipSamtools"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getPlatypus() {
		return platypus;
	}
	public void setPlatypus(ArrayList<String> platypus) {
		this.platypus = platypus;
		try {
			primaryCommands.add(Patients.class.getMethod("getPlatypus"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getFreebayes() {
		return freebayes;
	}
	public void setFreebayes(ArrayList<String> freebayes) {
		this.freebayes = freebayes;
		try {
			primaryCommands.add(Patients.class.getMethod("getFreebayes"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getFilterPlatypus() {
		return filterPlatypus;
	}

	public void setFilterPlatypus(ArrayList<String> filterPlatypus) {
		this.filterPlatypus = filterPlatypus;
		try {
			primaryCommands.add(Patients.class.getMethod("getFilterPlatypus"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSortMpileup() {
		return sortMpileup;
	}

	public void setSortMpileup(ArrayList<String> sortMpileup) {
		this.sortMpileup = sortMpileup;
		try {
			primaryCommands.add(Patients.class.getMethod("getSortMpileup"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSortPlatypus() {
		return sortPlatypus;
	}

	public void setSortPlatypus(ArrayList<String> sortPlatypus) {
		this.sortPlatypus = sortPlatypus;
		try {
			primaryCommands.add(Patients.class.getMethod("getSortPlatypus"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSortFreebayes() {
		return sortFreebayes;
	}

	public void setSortFreebayes(ArrayList<String> sortFreebayes) {
		this.sortFreebayes = sortFreebayes;
		try {
			primaryCommands.add(Patients.class.getMethod("getSortFreebayes"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
	
	
	
	
	
	//////// post combined steps ////////
	
	
	public ArrayList<String> getExtractInd() {
		return extractInd;
	}
	public void setExtractInd(ArrayList<String> extractInd) {
		this.extractInd = extractInd;
		try {
			secondaryCommands.add(Patients.class.getMethod("getExtractInd"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getSortHaploCaller() {
		return sortHaploCaller;
	}

	public void setSortHaploCaller(ArrayList<String> sortHaploCaller) {
		this.sortHaploCaller = sortHaploCaller;
		try {
			secondaryCommands.add(Patients.class.getMethod("getSortHaploCaller"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSampleNameModSamtools() {
		return sampleNameModSamtools;
	}
	public void setSampleNameModSamtools(ArrayList<String> sampleNameModSamtools) {
		this.sampleNameModSamtools = sampleNameModSamtools;
		try {
			secondaryCommands.add(Patients.class.getMethod("getSampleNameModSamtools"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSampleNameModPlatypus() {
		return sampleNameModPlatypus;
	}
	public void setSampleNameModPlatypus(ArrayList<String> sampleNameModPlatypus) {
		this.sampleNameModPlatypus = sampleNameModPlatypus;
		try {
			secondaryCommands.add(Patients.class.getMethod("getSampleNameModPlatypus"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSampleNameModGATK() {
		return sampleNameModGATK;
	}
	public void setSampleNameModGATK(ArrayList<String> sampleNameModGATK) {
		this.sampleNameModGATK = sampleNameModGATK;
		try {
			secondaryCommands.add(Patients.class.getMethod("getSampleNameModGATK"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}

	}
	
	

	public ArrayList<String> getSampleNameModFreebayes() {
		return sampleNameModFreebayes;
	}	public void setSampleNameModFreebayes(ArrayList<String> sampleNameModFreebayes) {
		this.sampleNameModFreebayes = sampleNameModFreebayes;
		try {
			secondaryCommands.add(Patients.class.getMethod("getSampleNameModFreebayes"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}

	}

	
	
	public ArrayList<String> getCombineVariants() {
		return combineVariants;
	}
	public void setCombineVariants(ArrayList<String> combineVariants) {
		this.combineVariants = combineVariants;
		try {
			secondaryCommands.add(Patients.class.getMethod("getCombineVariants"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getConsensus() {
		return consensus;
	}
	public void setConsensus(ArrayList<String> consensus) {
		this.consensus = consensus;
		try {
			secondaryCommands.add(Patients.class.getMethod("getConsensus"));
		} catch (NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getVariantEval() {
		return variantEval;
	}

	public void setVariantEval(ArrayList<String> variantEval) {
		this.variantEval = variantEval;
		
		try {
			secondaryCommands.add(Patients.class.getMethod("getVariantEval"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
	}

	
	
	
	
	
	
}
