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
import java.util.StringTokenizer;

import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InstanceDescriptionModelFactory;

public abstract class AbstractWebUrlLoader {
	
	public enum PopulationType {
		ONLY_CRAWL_FAILED, CRAWL_INDICATED, CRAWL_ALL
	}
	
	protected Map<String, Boolean> webSiteRecrawlFailed;
	protected Map<String, String> webSiteRecrawlFailedMsg;
	protected Map<String, Boolean> webSiteRecrawlIndicated;
	protected Map<String, Boolean> webSiteRecrawlAll; 
	
	protected AbstractWebUrlLoader(){
		webSiteRecrawlFailed = new LinkedHashMap<String, Boolean>();
		webSiteRecrawlFailedMsg = new LinkedHashMap<String, String>();
		webSiteRecrawlIndicated = new LinkedHashMap<String, Boolean>();
		webSiteRecrawlAll = new LinkedHashMap<String, Boolean>();
	}

	protected boolean loadRecords(String fileFullName) {

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
//		System.out.println("@ " + line);
		// populate the four Hash Map
		if (line.trim().startsWith("#") || this.isBlank(line)) {
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
		String msg = tokens[3].trim();
		webSiteRecrawlFailedMsg.put(tokens[0], msg.substring(1, msg.length() - 1));
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

	public void showWebUrls() {
		for (String webSiteURLStr : webSiteRecrawlAll.keySet()) {
			boolean indicated = webSiteRecrawlIndicated.get(webSiteURLStr);
			boolean failed = webSiteRecrawlFailed.get(webSiteURLStr);
			String msg = webSiteRecrawlFailedMsg.get(webSiteURLStr);
			System.out.println(webSiteURLStr + ", " + indicated + ", " + failed + ", " + msg);
		}
	}
}
