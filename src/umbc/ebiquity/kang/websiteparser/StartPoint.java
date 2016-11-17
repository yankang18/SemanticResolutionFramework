package umbc.ebiquity.kang.websiteparser;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.websiteparser.impl.WebSiteCrawlerFactory;
import umbc.ebiquity.kang.websiteparser.support.IPathFocusedWebSiteParser;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.impl.PathFocusedWebSiteParserFactory;

public class StartPoint {
	public static void main(String[] args) throws IOException {
		String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		ICrawledWebSite website = WebSiteCrawlerFactory.createCrawler(webSiteURL).crawl();
		IPathFocusedWebSiteParser parser = PathFocusedWebSiteParserFactory.createParser(website);
		IWebSiteParsedPathsHolder webSitePathHolder = parser.parse();
	}
}
