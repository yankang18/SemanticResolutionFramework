package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;

public class TripleRepositoriesAutomaticConstructor extends AbstractWebUrlLoader {

	public static void main(String[] args) throws IOException {
		String fileFullName = "/Users/kangyan2003/Desktop/Test/WebSiteURLInfo.txt";
		String repositoryDirFullPath = "/Users/kangyan2003/Desktop/Test/";
		TripleRepositoriesAutomaticConstructor constructor = new TripleRepositoriesAutomaticConstructor();
		constructor.loadRecords(fileFullName);
		constructor.populateTripleRepository(PopulationType.CRAWL_ALL);
	}

	public enum PopulationType {
		ONLY_CRAWL_FAILED, CRAWL_INDICATED, CRAWL_ALL
	}

//	private Map<String, Boolean> webSiteRecrawlFailed;
//	private Map<String, String> webSiteRecrawlFailedMsg;
//	private Map<String, Boolean> webSiteRecrawlIndicated;
//	private Map<String, Boolean> webSiteRecrawlAll;
	
//	public TripleRepositoriesAutomaticConstructor(String webSitesInfoFileFullName){
//		webSiteRecrawlFailed = new LinkedHashMap<String, Boolean>();
//		webSiteRecrawlFailedMsg = new LinkedHashMap<String, String>();
//		webSiteRecrawlIndicated = new LinkedHashMap<String, Boolean>();
//		webSiteRecrawlAll = new LinkedHashMap<String, Boolean>();
//		this.loadRecords(webSitesInfoFileFullName);
//	}
	
	public void populateTripleRepository(PopulationType populationType) {
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

		for (String webSiteURLStr : crawlIndicators.keySet()) {
			boolean recrawl = crawlIndicators.get(webSiteURLStr);
			if (recrawl) {
				try {
					URL webSiteURL = new URL(webSiteURLStr);
					try {
						InstanceDescriptionModelFactory.construct(webSiteURL);
						this.recordSuccess(webSiteURL.toString());
					} catch (MalformedURLException e) {
						this.recordErrorMsg(webSiteURL.toString(), e.getMessage());
					} catch (IOException e) {
						this.recordErrorMsg(webSiteURL.toString(), e.getMessage());
					}
				} catch (MalformedURLException e) {
					this.recordErrorMsg(webSiteURLStr, e.getMessage());
				}
			}
		}
	}

	private void recordSuccess(String webSiteURLString) {
		webSiteRecrawlFailed.put(webSiteURLString, false);
		webSiteRecrawlFailedMsg.put(webSiteURLString, "");
	}

	private void recordErrorMsg(String webSiteURLString ,String message) {
		webSiteRecrawlFailed.put(webSiteURLString, true);
		webSiteRecrawlFailedMsg.put(webSiteURLString, message);
	}
	
	public void saveUpdatedInfo(String fileFathFullName){
		// save updated info
		StringBuilder records = new StringBuilder();
		for (String key : webSiteRecrawlAll.keySet()) {
			String indicated = String.valueOf(webSiteRecrawlIndicated.get(key));
			String failed = String.valueOf(webSiteRecrawlFailed.get(key));
			String failedMsg = webSiteRecrawlFailedMsg.get(key);
			String record = key + "," + indicated + "," + failed + "," + "\"" + failedMsg + "\"";
			records.append(record);
			records.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		FileAccessor.saveTripleString(fileFathFullName, records.toString());
	}
	
}
