package umbc.ebiquity.kang.webpageparser;

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

import umbc.ebiquity.kang.webpageparser.interfaces.ICrawler;


/***
 * 
 * @author kangyan2003
 */
public class WebSiteCrawler implements ICrawler {


	private Queue<WebPage> webPageQueue;
	private HashMap<String, WebPage> visitedPageDir;
	private HashSet<String> visitedPage;
	
	private String homePageURLString;
	private URL webSiteURL;
	private WebPage homePageNode;
	private int maxNumberPagesToVisit;
	private boolean isCrawled;

	public WebSiteCrawler(URL homePageUrl, int maxNumberPagesToVisit) throws IOException {
		this.webSiteURL = homePageUrl;
		this.homePageURLString = homePageUrl.toString().trim();
		this.homePageNode = new WebPage(new CrawlerUrl(homePageURLString, 0));
		this.webPageQueue = new LinkedList<WebPage>();
		this.visitedPageDir = new HashMap<String, WebPage>();
		this.visitedPage = new HashSet<String>();
		this.isCrawled = false;
		this.maxNumberPagesToVisit = maxNumberPagesToVisit;
	}
	
	public WebSiteCrawler(URL homePageUrl) throws IOException {
		this(homePageUrl, 1000);
	}

	public void crawl() {
		System.out.println("Crawling Web Site ...");
		this.webPageQueue.clear();
		this.homePageNode.extractLinks(visitedPage);
		this.webPageQueue.add(homePageNode);
		
		String hostName = homePageNode.getHostName();

		// BFS crawling
		while (this.continueCrawling()) {
			WebPage top = webPageQueue.remove();		
			Map<String, String> links = top.getExternalLinks();
			for(String webPageUrl : links.keySet()){
				if (this.isValidated(webPageUrl)) {
					WebPage webPage;
					String text = links.get(webPageUrl);
					try {
						webPage = new WebPage(new CrawlerUrl(webPageUrl, 0));
						webPage.setHostName(hostName);
						webPage.addPredecessor(top);
						webPage.setWebPageMainTopic(text);
						top.addDecendant(webPage);
						System.out.println("@ Now crawling <" + webPage.getPageURLString() + "> with main topic <"
								+ webPage.getWebPageMainTopic() + ">");
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
	}

	private void extractLinks(WebPage webPage) {
		visitedPageDir.put(webPage.getPageURL().getUrlString(), webPage);
		visitedPage.add(webPage.getPageURL().getUrlString());
		webPage.extractLinks(visitedPage);
	}

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
	
	public Collection<WebPage> getCrawledWebPages(){
		if(!this.isCrawled){
			crawl();
		}
		return this.visitedPageDir.values();
	}
	
	public URL getWebSiteURL(){
		return this.webSiteURL;
	}

}
