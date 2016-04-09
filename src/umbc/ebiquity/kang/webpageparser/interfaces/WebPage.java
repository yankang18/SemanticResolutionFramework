package umbc.ebiquity.kang.webpageparser.interfaces;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import umbc.ebiquity.kang.webpageparser.CrawlerUrl;

public interface WebPage {

	String getHostName();

	void setHostName(String hostName);

	void extractLinks(Set<String> visitedPage);

	Map<String, String> getExternalLinks();

	void addDecendant(WebPage webPage);

	void addPredecessor(WebPage top);

	CrawlerUrl getPageURL();

	void load() throws IOException; 

	Document getWebPageDocument(); 

}
