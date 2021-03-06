package de.NextGP.initialize;

import java.io.IOException;
import java.util.Properties;

public class LoadConfig {

	//	initializing variables
	// programs
	private String bwa;
	private String samtools;
	private String picard;
	private String bedTools;
	private String vcfTools;
	private String vep;
	private String gemini;
	private String vtMaster;
	private String gatk;
	private String snpEff;
	private String tabix;
	private String bgzip;
	private String bcftools;
	private String platypus;
	private String freebayes;
	private String vcfFilter;
	private String consensus;
	private String fastQC;
	private String afterQC;
	private String fastp;
	private String alamut;
	private String addSetInfo;
	private String filterScript;
	private String interVarScript;
	private String omimScript;
	
	// references
	private String hg19Fasta;
	private String indelMills;
	private String indel1kgp;
	private String snps1kgp;
	private String dbsnp;
	private String hapmap;
	private String omni;
	private String vepCache;
	private String dbNSFP;
	private String vepCacheVersion;
	
	// misc
	private String java;
	private String tempDir;
	private String localTmp;

	// folder for output
	private String alignment;
	private String duplicates;
	private String realignment;
	private String baseReacalibration;
	private String metrices;
	private String variantCalling;
	private String annotation;
	private String database;
	private String filter;
	
	
	//constructor executing the loading of the config file
			public LoadConfig() throws IOException {
				loadConfig();
				
			}
			
			
			// read in config file and save used programs and resources 
			private void loadConfig() throws IOException {
				
				// Load resources from config file
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("NGP.config"));
				
				// read in different tools
				bwa = prop.getProperty("bwa");
				samtools = prop.getProperty("samtools");
				picard = prop.getProperty("picard");
				bedTools = prop.getProperty("bedTools");
				vcfTools = prop.getProperty("vcfTools");
				vep = prop.getProperty("VEP");
				snpEff = prop.getProperty("snpEff");
				gemini = prop.getProperty("gemini");
				vtMaster = prop.getProperty("vtMaster");
				gatk = prop.getProperty("gatk");
				tabix = prop.getProperty("tabix");
				bgzip = prop.getProperty("bgzip");
				bcftools = prop.getProperty("bcftools");
				platypus = prop.getProperty("platypus");
				freebayes = prop.getProperty("freebayes");
				vcfFilter = prop.getProperty("vcfFilter");
				consensus = prop.getProperty("consensus");
				fastQC = prop.getProperty("fastQC");
				afterQC	= prop.getProperty("afterQC");
				fastp = prop.getProperty("fastp");
				alamut = prop.getProperty("alamut");
				addSetInfo = prop.getProperty("addSetInfo");
				filterScript = prop.getProperty("filterScriptPath");
				interVarScript = prop.getProperty("interVarScriptPath");
				omimScript = prop.getProperty("omimAnnotationScriptPath");
				
				// read in reference directory names
				hg19Fasta = prop.getProperty("hg19Fasta");
				indelMills = prop.getProperty("indelMills");
				indel1kgp = prop.getProperty("indel1kgp");
				snps1kgp = prop.getProperty("snp1kgp");
				dbsnp = prop.getProperty("dbSNP");
				hapmap = prop.getProperty("hapmap");
				omni = prop.getProperty("omni");
				vepCache = prop.getProperty("vepCache");
				vepCacheVersion=prop.getProperty("vepCacheVersion");
				dbNSFP = prop.getProperty("dbNSFP");
				
				// misc
				java = prop.getProperty("java");
				tempDir = prop.getProperty("tempDir");
				localTmp = prop.getProperty("localTmp");
				
				// out folders
				alignment = prop.getProperty("alignment");
				duplicates = prop.getProperty("duplicates");
				realignment = prop.getProperty("realignment");
				baseReacalibration = prop.getProperty("baseReacalibration");
				metrices = prop.getProperty("matrices");
				variantCalling = prop.getProperty("variantCalling");
				annotation = prop.getProperty("annotation");
				database = prop.getProperty("database");
				filter = prop.getProperty("filter");
			}

			
		
			//////////////////////////////////
			////////  Getter / setter ////////
			//////////////////////////////////
			
			

			public String getBwa() {
				return bwa;
			}

			public String getSamtools() {
				return samtools;
			}

			public String getPicard() {
				return picard;
			}

			public String getBedTools() {
				return bedTools;
			}

			public String getVcfTools() {
				return vcfTools;
			}

			public String getVep() {
				return vep;
			}

			public String getGemini() {
				return gemini;
			}

			public String getVtMaster() {
				return vtMaster;
			}

			public String getHg19Fasta() {
				return hg19Fasta;
			}

			public String getIndelMills() {
				return indelMills;
			}

			public String getIndel1kgp() {
				return indel1kgp;
			}

			public String getSnps1kgp() {
				return snps1kgp;
			}

			public String getDbsnp() {
				return dbsnp;
			}

			public String getHapmap() {
				return hapmap;
			}

			public String getOmni() {
				return omni;
			}

			public String getJava() {
				return java;
			}

			public String getGatk() {
				return gatk;
			}

			public String getAlignment() {
				return alignment;
			}

			public String getDuplicates() {
				return duplicates;
			}

			public String getRealignment() {
				return realignment;
			}

			public String getBaseReacalibration() {
				return baseReacalibration;
			}

			public String getMetrices() {
				return metrices;
			}

			public String getVariantCalling() {
				return variantCalling;
			}

			public String getAnnotation() {
				return annotation;
			}

			public String getDatabase() {
				return database;
			}

			public String getSnpEff() {
				return snpEff;
			}

			public String getTabix() {
				return tabix;
			}

			public String getBgzip() {
				return bgzip;
			}

			public String getBcftools() {
				return bcftools;
			}

			public String getPlatypus() {
				return platypus;
			}

			public String getFreebayes() {
				return freebayes;
			}

			public String getVcfFilter() {
				return vcfFilter;
			}

			public String getConsensus() {
				return consensus;
			}

			public String getFilter() {
				return filter;
			}

			public String getFastQC() {
				return fastQC;
			}

			public String getVepCache() {
				return vepCache;
			}

			public String getTempDir() {
				return tempDir;
			}

			public String getAfterQC() {
				return afterQC;
			}

			public String getLocalTmp() {
				return localTmp;
			}

			public String getAlamut() {
				return alamut;
			}

			public String getAddSetInfo() {
				return addSetInfo;
			}

			public String getDbNSFP() {
				return dbNSFP;
			}

			public String getVepCacheVersion() {
				return vepCacheVersion;
			}

			public String getFastp() {
				return fastp;
			}


			public String getFilterScript() {
				return filterScript;
			}


			public String getInterVarScript() {
				return interVarScript;
			}


			public String getOmimScript() {
				return omimScript;
			}

			
			
			
			
}
