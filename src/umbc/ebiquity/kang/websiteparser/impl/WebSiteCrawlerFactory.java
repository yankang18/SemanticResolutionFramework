package umbc.ebiquity.kang.websiteparser.impl;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.websiteparser.ICrawler;

public class WebSiteCrawlerFactory {
	public static ICrawler createCrawler(URL siteURL, int maxNumberPagesToVisit) throws IOException {
		return new WebSiteCrawler(siteURL, maxNumberPagesToVisit);
	}

	public static ICrawler createCrawler(URL siteURL) throws IOException {
		return new WebSiteCrawler(siteURL);
	}
}
