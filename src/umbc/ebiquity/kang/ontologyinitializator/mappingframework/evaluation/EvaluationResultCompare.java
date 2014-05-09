package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.TripleRepositoriesAutomaticConstructor.PopulationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class EvaluationResultCompare extends AbstractWebUrlLoader {
	
	public static void main(String[] args) throws IOException {
		
		String fileFullPath = "/home/yankang/Desktop/WebSiteURLs.txt";
		EvaluationResultCompare constructor = new EvaluationResultCompare();
		constructor.loadResultFiles(fileFullPath);
		constructor.compareResult(PopulationType.CRAWL_INDICATED, "/home/yankang/Desktop/");
	}
	
	Map<String, String> statisticItems = new HashMap<String, String>();
	Map<String, Double> statisticMap1; 
	Map<String, Double> statisticMap2; 
	Map<String, Double> statisticMap3;
	Map<String, Double> statisticMap4;
	
	public EvaluationResultCompare(){
		
		statisticItems.put("Number of All Instances".toLowerCase(), "Number of All Instances");
		statisticItems.put("Number of Classified instances".toLowerCase(), "Number of Classified instances");
		statisticItems.put("Overall Score".toLowerCase(), "Overall Score");
		statisticItems.put("Overall Of Error".toLowerCase(), "Overall Of Error");
		statisticItems.put("Recall".toLowerCase(), "Recall");
		statisticItems.put("Precision".toLowerCase(), "Precision");
		statisticItems.put("Fmeasure".toLowerCase(), "Fmeasure");
		statisticItems.put("Number Of Correction".toLowerCase(), "Number Of Correction");
		statisticItems.put("Correction Rate".toLowerCase(), "Correction Rate");
		
		
	}
	
	public void compareResult(PopulationType populationType, String resultDir) throws IOException {

		Map<String, Boolean> crawlIndicators;
		switch (populationType) {
		case ONLY_CRAWL_FAILED:
			crawlIndicators = webSiteRecrawlFailed;
			break;
		case CRAWL_INDICATED:
			crawlIndicators = webSiteRecrawlIndicated;
			break;
		case CRAWL_ALL:
			crawlIndicators = webSiteRecrawlAll;
			break;
		default:
			crawlIndicators = webSiteRecrawlFailed;
		}

		String textNone = resultDir + "Test_None/EvaluationResult";
		String textRule =  resultDir + "Test_Rule/EvaluationResult";
		String textNaive = resultDir + "Test_Naive/EvaluationResult";
		String textRuleNaivePath = resultDir + "Test_Rule_Naive/EvaluationResult";
		String result = resultDir + "result";
		StringBuilder stringBuilder = new StringBuilder();
		for (String webSiteURLStr : crawlIndicators.keySet()) {
//			System.out.println(webSiteURLStr);
			stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			stringBuilder.append(webSiteURLStr);
			stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			boolean recrawl = crawlIndicators.get(webSiteURLStr);
			if (recrawl) {
				try {
					URL webSiteURL = new URL(webSiteURLStr);
					String repositoryName = FileUtility.convertURL2FileName(webSiteURL);
					statisticMap1 = new LinkedHashMap<String, Double>();
					statisticMap2 = new LinkedHashMap<String, Double>();
					statisticMap3 = new LinkedHashMap<String, Double>();
					statisticMap4 = new LinkedHashMap<String, Double>();
					System.out.println(textRuleNaivePath+"/"+repositoryName);
					this.loadResultRecords(textNone+"/"+repositoryName, statisticMap1);
					this.loadResultRecords(textRule+"/"+repositoryName, statisticMap2);
					this.loadResultRecords(textNaive+"/"+repositoryName, statisticMap3);
					this.loadResultRecords(textRuleNaivePath+"/"+repositoryName, statisticMap4);
			

					for (String s : statisticMap4.keySet()) {
						double n1 = statisticMap1.get(s);
						double n2 = statisticMap2.get(s);
						double n3 = statisticMap3.get(s);
						double n4 = statisticMap4.get(s);
//						stringBuilder.append(s + ":   "  + n4);
//						stringBuilder.append(s + ":   " + n1 + "/" + n2 + "/" + n4);
						stringBuilder.append(s + ":   " + n1 + "/" + n2 + "/" + n3 + "/" + n4);
						stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
					}
					
				} catch (MalformedURLException e) {
				}
			}
			
			stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		FileAccessor.saveTripleString(result, stringBuilder.toString());
	}
	
	public boolean loadResultFiles(String fileFullName){
		return this.loadRecords(fileFullName);
	}
	
	private boolean loadResultRecords(String fileFullName, Map<String, Double> statisticMap) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadRecord(line, statisticMap);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private void loadRecord(String line, Map<String, Double> statisticMap) {
		if (this.isBlank(line))
			return;
		String[] tokens = line.split(":");
		if (tokens.length != 2)
			return;
		
		if (statisticItems.containsKey(tokens[0].trim().toLowerCase())) {
			statisticMap.put(tokens[0].trim().toLowerCase(), Double.valueOf(tokens[1].trim()));
		}

	}
	
	private boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

}
