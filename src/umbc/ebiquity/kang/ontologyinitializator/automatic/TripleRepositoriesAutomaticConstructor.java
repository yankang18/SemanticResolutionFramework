package umbc.ebiquity.kang.ontologyinitializator.automatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.TripleRepositoryFactory;

public class TripleRepositoriesAutomaticConstructor {

	public static void main(String[] args) throws IOException {

		String fileFullName = "/Users/kangyan2003/Desktop/Test/WebSiteURLInfo.txt";
		String repositoryDirFullPath = "/Users/kangyan2003/Desktop/Test/";
		TripleRepositoriesAutomaticConstructor constructor = new TripleRepositoriesAutomaticConstructor(fileFullName,"repositoryDirFullPath");
		constructor.populateTripleRepository(PopulationType.CRAWL_ALL);

	}

	public enum PopulationType {
		ONLY_CRAWL_FAILED, CRAWL_INDICATED, CRAWL_ALL
	}

	private Map<String, Boolean> webSiteRecrawlFailed;
	private Map<String, String> webSiteRecrawlFailedMsg;
	private Map<String, Boolean> webSiteRecrawlIndicated;
	private Map<String, Boolean> webSiteRecrawlAll;
	
	public TripleRepositoriesAutomaticConstructor(String webSitesInfoFileFullName, String repositoryDirectoryFullPath){
		webSiteRecrawlFailed = new LinkedHashMap<String, Boolean>();
		webSiteRecrawlFailedMsg = new LinkedHashMap<String, String>();
		webSiteRecrawlIndicated = new LinkedHashMap<String, Boolean>();
		webSiteRecrawlAll = new LinkedHashMap<String, Boolean>();
		this.loadRecords(webSitesInfoFileFullName);
	}
	
	private boolean loadRecords(String fileFullName) {
		
		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
				String line;
				reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					loadRecord(line);
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

	private void loadRecord(String line) {

		//populate the four Hash Map
		if(line.trim().startsWith("#") || this.isBlank(line)){ 
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		int i = 0;
		String[] tokens = new String[4];
		while (tokenizer.hasMoreTokens()) {
			tokens[i] = tokenizer.nextToken().trim();
			i++;
		}
		webSiteRecrawlIndicated.put(tokens[0], Boolean.valueOf(tokens[1]));
		webSiteRecrawlFailed.put(tokens[0], Boolean.valueOf(tokens[2]));
		webSiteRecrawlFailedMsg.put(tokens[0], tokens[3]);
		webSiteRecrawlAll.put(tokens[0], true);
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
						TripleRepositoryFactory.createTripleRepository(webSiteURL, false);
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
		webSiteRecrawlFailedMsg.put(webSiteURLString, message);
	}
	
	public void saveUpdatedInfo(String fileFathFullName){
		// save updated info
		StringBuilder records = new StringBuilder();
		for (String key : webSiteRecrawlIndicated.keySet()) {
			String indicated = String.valueOf(webSiteRecrawlIndicated.get(key));
			String failed = String.valueOf(webSiteRecrawlFailed.get(key));
			String failedMsg = webSiteRecrawlFailedMsg.get(key);
			String record = key + "," + indicated + "," + failed + "," + "\"" + failedMsg + "\"";
			records.append(record);
			records.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		FileAccessor.saveTripleString(fileFathFullName, records.toString());
	}
	
}
