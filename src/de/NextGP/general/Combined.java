package de.NextGP.general;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

public class Combined {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	
	private String lastOutFile;
	private boolean matePair;
	private LinkedList<Method> commands = new LinkedList<>();
	
	
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
	private ArrayList<String> snpEffAnnotation;
	private ArrayList<String> varEval;
	private ArrayList<String> geminiLoad;
	private ArrayList<String> mkDirs = new ArrayList<>();
	private ArrayList<String> tabix;
	private ArrayList<String> bgzip;
	
	
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

	
	
	/////////////
	//////// general stuff
	
	public String getLastOutFile() {
		return lastOutFile;
	}

	public void setLastOutFile(String lastOutFile) {
		this.lastOutFile = lastOutFile;
	}

	public boolean isMatePair() {
		return matePair;
	}

	public void setMatePair(boolean matePair) {
		this.matePair = matePair;
	}

	public LinkedList<Method> getCommands() {
		return commands;
	}

	public void setCommands(LinkedList<Method> commands) {
		this.commands = commands;
	}

	
	///////////////
	//////// method getter
	
	
	public ArrayList<String> getGenotypeGVCF() {
		return genotypeGVCF;
	}
	
	public void setGenotypeGVCF(ArrayList<String> genotypeGVCF) {
		this.genotypeGVCF = genotypeGVCF;
		try {
			commands.add(Combined.class.getMethod("getGenotypeGVCF"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			
			System.out.println("lbadsf");
			e.printStackTrace();
		}
	}
	

	
	public ArrayList<String> getVariantRecalibration() {
		return variantRecalibration;
	}
	
	public void setVariantRecalibration(ArrayList<String> variantRecalibration) {
		this.variantRecalibration = variantRecalibration;
		try {
			commands.add(Combined.class.getMethod("getVariantRecalibration"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getApplyRecalibration() {
		return applyRecalibration;
	}
	
	public void setApplyRecalibration(ArrayList<String> applyRecalibration) {
		this.applyRecalibration = applyRecalibration;
		try {
			commands.add(Combined.class.getMethod("getApplyRecalibration"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getExtractSnpSet() {
		return extractSnpSet;
	}
	
	public void setExtractSnpSet(ArrayList<String> extractSnpSet) {
		this.extractSnpSet = extractSnpSet;
		try {
			commands.add(Combined.class.getMethod("getExtractSnpSet"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getHardFiterSnpSet() {
		return hardFiterSnpSet;
	}
	
	public void setHardFiterSnpSet(ArrayList<String> hardFiterSnpSet) {
		this.hardFiterSnpSet = hardFiterSnpSet;
		try {
			commands.add(Combined.class.getMethod("getHardFiterSnpSet"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getExtractIndelSet() {
		return extractIndelSet;
	}
	
	public void setExtractIndelSet(ArrayList<String> extractIndelSet) {
		this.extractIndelSet = extractIndelSet;
		try {
			commands.add(Combined.class.getMethod("getExtractIndelSet"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getHardFilterIndelSet() {
		return hardFilterIndelSet;
	}
	
	public void setHardFilterIndelSet(ArrayList<String> hardFilterIndelSet) {
		this.hardFilterIndelSet = hardFilterIndelSet;
		try {
			commands.add(Combined.class.getMethod("getHardFilterIndelSet"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getCombineVariants() {
		return combineVariants;
	}

	public void setCombineVariants(ArrayList<String> combineVariants) {
		this.combineVariants = combineVariants;
		try {
			commands.add(Combined.class.getMethod("getCombineVariants"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getRemoveFilteredReads() {
		return removeFilteredReads;
	}
	
	public void setRemoveFilteredReads(ArrayList<String> removeFilteredReads) {
		this.removeFilteredReads = removeFilteredReads;
		try {
			commands.add(Combined.class.getMethod("getRemoveFilteredReads"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getExtractInd() {
		return extractInd;
	}
	
	public void setExtractInd(ArrayList<String> extractInd) {
		this.extractInd = extractInd;
		try {
			commands.add(Combined.class.getMethod("getExtractInd"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<String> getVepAnnotation() {
		return vepAnnotation;
	}

	public void setVepAnnotation(ArrayList<String> vepAnnotation) {
		this.vepAnnotation = vepAnnotation;
		try {
			commands.add(Combined.class.getMethod("getVepAnnotation"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getVtMaster() {
		return vtMaster;
	}

	public void setVtMaster(ArrayList<String> prepareVEP) {
		this.vtMaster = prepareVEP;
		try {
			commands.add(Combined.class.getMethod("getVtMaster"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getVarEval() {
		return varEval;
	}

	public void setVarEval(ArrayList<String> varEval) {
		this.varEval = varEval;
		try {
			commands.add(Combined.class.getMethod("getVarEval"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getGeminiLoad() {
		return geminiLoad;
	}

	public void setGeminiLoad(ArrayList<String> geminiLoad) {
		this.geminiLoad = geminiLoad;
		try {
			commands.add(Combined.class.getMethod("getGeminiLoad"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getMkDirs() {
		return mkDirs;
	}

	public void setMkDirs(ArrayList<String> mkDirs) {
		this.mkDirs = mkDirs;
		try {
			commands.add(Combined.class.getMethod("getMkDirs"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getSnpEffAnnotation() {
		return snpEffAnnotation;
	}

	public void setSnpEffAnnotation(ArrayList<String> snpEffAnnotation) {
		this.snpEffAnnotation = snpEffAnnotation;
		
		try {
			commands.add(Combined.class.getMethod("getSnpEffAnnotation"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	public ArrayList<String> getTabix() {
		return tabix;
	}

	public void setTabix(ArrayList<String> tabix) {
		this.tabix = tabix;
		try {
			commands.add(Combined.class.getMethod("getTabix"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<String> getBgzip() {
		return bgzip;
	}

	public void setBgzip(ArrayList<String> bgzip) {
		this.bgzip = bgzip;
		try {
			commands.add(Combined.class.getMethod("getBgzip"));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	
	

}