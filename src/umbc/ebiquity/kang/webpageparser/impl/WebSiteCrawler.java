package umbc.ebiquity.kang.webpageparser.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import umbc.ebiquity.kang.webpageparser.Crawler;
import umbc.ebiquity.kang.webpageparser.WebPage;

public class WebSiteCrawler implements Crawler {

	private final static int MAX_VISIT_PAGE = 1000;
	private Queue<WebPage> webPageQueue;
	private HashMap<String, WebPage> visitedPageDir;
	private HashSet<String> visitedPage;
	
	private String homePageURLString;
	private URL webSiteURL;
	private WebPage homePage;
	private int maxNumberPagesToVisit;
	private boolean isCrawled;
	
	public static Crawler createCrawler(URL siteURL, int maxNumberPagesToVisit) throws IOException {
		return new WebSiteCrawler(siteURL, maxNumberPagesToVisit);
	}

	public static Crawler createCrawler(URL siteURL) throws IOException { 
		return new WebSiteCrawler(siteURL);
	}
	
	WebSiteCrawler(URL siteURL, int maxNumberPagesToVisit) throws IOException {
		this.webSiteURL = siteURL;
		this.homePageURLString = siteURL.toString().trim();
		this.homePage = new WebPageImpl(new CrawlerUrl(homePageURLString, 0));
		this.webPageQueue = new LinkedList<WebPage>();
		this.visitedPageDir = new HashMap<String, WebPage>();
		this.visitedPage = new HashSet<String>();
		this.isCrawled = false;
		this.maxNumberPagesToVisit = maxNumberPagesToVisit;
	}
	
	WebSiteCrawler(URL start) throws IOException {
		this(start, MAX_VISIT_PAGE);
	}

	@Override
	public List<WebPage> crawl() throws IOException { 
		System.out.println("Crawling Web Site ...");
		
		if (isCrawled)
			return new ArrayList<WebPage>(this.visitedPageDir.values());
		
		this.webPageQueue.clear();
		homePage.load();
		this.homePage.extractLinks(visitedPage);
		this.webPageQueue.add(homePage);
		
		String hostName = homePage.getHostName();

		// BFS crawling
		while (this.continueCrawling()) {
			WebPage top = webPageQueue.remove();		
			Map<String, String> links = top.getExternalLinks();
			for(String webPageUrl : links.keySet()){
				if (this.isValidated(webPageUrl)) {
					WebPage webPage;
					String text = links.get(webPageUrl);
					try {
						webPage = new WebPageImpl(new CrawlerUrl(webPageUrl, 0));
						webPage.load();
						webPage.setHostName(hostName);
						webPage.addPredecessor(top);
//						webPage.setWebPageMainTopic(text);
						top.addDecendant(webPage);
						System.out.println("@ Now crawling <" + webPage.getPageURL().getUrlString() + "> with main topic <"
								+ text + ">");
						this.extractLinks(webPage);
						this.webPageQueue.add(webPage);
					} catch (IOException e) {
						continue;
					}
				}
			}
		}
		isCrawled = true;
		System.out.println(visitedPageDir.size() + " have been crawled");
		return new ArrayList<WebPage>(this.visitedPageDir.values()); 
	}

	private void extractLinks(WebPage webPage) {
		visitedPageDir.put(webPage.getPageURL().getUrlString(), webPage);
		visitedPage.add(webPage.getPageURL().getUrlString());
		webPage.extractLinks(visitedPage);
	}

	/**
	 * if no web page exists or the max number of web pages to be visited was
	 * reached, return false indicating that web crawling should be continued.
	 * Otherwise, return true.
	 * 
	 * @return true if continue crawling the web site. Otherwise, return false
	 */
	private boolean continueCrawling() {
		return (!webPageQueue.isEmpty() && getNumberOfPagesVisited() < this.maxNumberPagesToVisit);
	}
	
	private boolean isValidated(String webPageUrl) {
		if (!visitedPage.contains(webPageUrl)) {
			return true;
		} else {
			return false;
		}
	}

	private int getNumberOfPagesVisited() {
		return this.visitedPageDir.size();
	}
	
	@Override
	public Collection<WebPage> getCrawledWebPages() throws IOException { 
		if(!this.isCrawled){
			crawl();
		}
		return this.visitedPageDir.values();
	}
	
	@Override
	public URL getWebSiteURL(){
		return this.webSiteURL;
	}
}
