package umbc.ebiquity.kang.websiteparser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import umbc.ebiquity.kang.entityframework.object.EntityPath;
import umbc.ebiquity.kang.websiteparser.impl.WebPagePath;
import umbc.ebiquity.kang.websiteparser.object.CrawlerUrl;

public interface IWebPage {

	String getHostName();

	void setHostName(String hostName);

	void extractLinks(Set<String> visitedPage);

	Map<String, String> getExternalLinks();

	void addDecendant(IWebPage webPage);

	void addPredecessor(IWebPage top);

	CrawlerUrl getPageURL();

	void load() throws IOException;

	Document getWebPageDocument();

	List<WebPagePath> listWebTagPathsWithTextContent();

//	void addEntityPath(EntityPath termPath);
//
//	Collection<EntityPath> getEntityPaths();

	String getPageURLAsString();

	String getBaseURL();

	void analyzeWebPage();

	// String getPageURLString();

}
