package umbc.ebiquity.kang.webpageparser.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.webpageparser.WebPage;

public class SimplePageTemplatesSplitter{
	
	public Collection<WebPathPath> splitPageTemplates(Collection<WebPage> originalWebPages) {
		Map<String, WebPathPath> webSitePaths = new LinkedHashMap<String, WebPathPath>();
		Map<String, Integer> webPagePathCounter = new LinkedHashMap<String, Integer>();
		Map<String, List<String>> webPagePathProvenances = new LinkedHashMap<String, List<String>>();
		Set<String> baseUrls = new HashSet<String>();
		int numOfPages = originalWebPages.size();
		for (WebPage webPage : originalWebPages) {
			Map<String, WebPathPath> webPagePaths = new LinkedHashMap<String, WebPathPath>();
			//count # of base urls
			String baseUrl = webPage.getBaseURL();
			if(!baseUrls.contains(baseUrl)){
				baseUrls.add(baseUrl);
			}
			webPage.analyzeWebPage();
			
			// record paths (eliminate duplicated paths) of each web.
			for (WebPathPath path : webPage.listWebTagPathsWithTextContent()) {
				if (!webPagePaths.containsKey(path.getPathPattern())) {
					webPagePaths.put(path.getPathPattern(), path);
				}
			}
			
			// record the occurrence of paths across web pages and the
			// provenance of each path
			for (WebPathPath path : webPagePaths.values()) {
				
				if (webSitePaths.containsKey(path.getPathPattern())) {
					int pathOccurence = webPagePathCounter.get(path.getPathPattern());
					List<String> provenances = webPagePathProvenances.get(path.getPathPattern());
					provenances.add(path.getHost());
					webPagePathCounter.put(path.getPathPattern(), ++pathOccurence);
				} else {
					webSitePaths.put(path.getPathPattern(), path);
					webPagePathCounter.put(path.getPathPattern(), 1);
					List<String> provenances = new ArrayList<String>();
					provenances.add(path.getHost());
					webPagePathProvenances.put(path.getPathPattern(), provenances);
				}
			}
		}
		
		int numOfBaseUrls = baseUrls.size();
//		System.out.println("### number of base urls: " + numOfBaseUrls);
//		System.out.println("### number of pages: " + numOfPages);
		Collection<WebPathPath> templateWebPagePaths = new ArrayList<WebPathPath>();
		for (String key : webSitePaths.keySet()) {
			int counter = webPagePathCounter.get(key);
			WebPathPath path = webSitePaths.get(key);
//			System.out.println("** " + counter + " " + key);
//			System.out.println("** Path ID " + path.getPathID());
			
			if (counter > numOfBaseUrls) {
				templateWebPagePaths.add(webSitePaths.get(key));
				for(String proveance : webPagePathProvenances.get(key)){
//					System.out.println("      -> from page: " + proveance);
				}
			}
		}
		return templateWebPagePaths;
	}
	
//	public Collection<WebPathPath> splitPageTemplates(Collection<WebPagePathsImpl> originalWebPages) {
//		Map<String, WebPathPath> webSitePaths = new LinkedHashMap<String, WebPathPath>();
//		Map<String, Integer> webPagePathCounter = new LinkedHashMap<String, Integer>();
//		Map<String, List<String>> webPagePathProvenances = new LinkedHashMap<String, List<String>>();
//		Set<String> baseUrls = new HashSet<String>();
//		int numOfPages = originalWebPages.size();
//		for (WebPagePathsImpl webPage : originalWebPages) {
//			Map<String, WebPathPath> webPagePaths = new LinkedHashMap<String, WebPathPath>();
//			//count # of base urls
//			String baseUrl = webPage.getBaseURL();
//			if(!baseUrls.contains(baseUrl)){
//				baseUrls.add(baseUrl);
//			}
////			webPage.analyzeWebPage();
//			
//			// record paths (eliminate duplicated paths) of each web.
//			for (WebPathPath path : webPage.listWebTagPathsWithTextContent()) {
//				if (!webPagePaths.containsKey(path.getPathPattern())) {
//					webPagePaths.put(path.getPathPattern(), path);
//				}
//			}
//			
//			// record the occurrence of paths across web pages and the
//			// provenance of each path
//			for (WebPathPath path : webPagePaths.values()) {
//				
//				if (webSitePaths.containsKey(path.getPathPattern())) {
//					int pathOccurence = webPagePathCounter.get(path.getPathPattern());
//					List<String> provenances = webPagePathProvenances.get(path.getPathPattern());
//					provenances.add(path.getHost());
//					webPagePathCounter.put(path.getPathPattern(), ++pathOccurence);
//				} else {
//					webSitePaths.put(path.getPathPattern(), path);
//					webPagePathCounter.put(path.getPathPattern(), 1);
//					List<String> provenances = new ArrayList<String>();
//					provenances.add(path.getHost());
//					webPagePathProvenances.put(path.getPathPattern(), provenances);
//				}
//			}
//		}
//		
//		int numOfBaseUrls = baseUrls.size();
////		System.out.println("### number of base urls: " + numOfBaseUrls);
////		System.out.println("### number of pages: " + numOfPages);
//		Collection<WebPathPath> templateWebPagePaths = new ArrayList<WebPathPath>();
//		for (String key : webSitePaths.keySet()) {
//			int counter = webPagePathCounter.get(key);
//			WebPathPath path = webSitePaths.get(key);
////			System.out.println("** " + counter + " " + key);
////			System.out.println("** Path ID " + path.getPathID());
//			
//			if (counter > numOfBaseUrls) {
//				templateWebPagePaths.add(webSitePaths.get(key));
//				for(String proveance : webPagePathProvenances.get(key)){
////					System.out.println("      -> from page: " + proveance);
//				}
//			}
//		}
//		return templateWebPagePaths;
//	}
}
