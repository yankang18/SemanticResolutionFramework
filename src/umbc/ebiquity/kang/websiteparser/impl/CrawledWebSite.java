package umbc.ebiquity.kang.websiteparser.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import umbc.ebiquity.kang.websiteparser.ICrawledWebSite;
import umbc.ebiquity.kang.websiteparser.IWebPage;

public class CrawledWebSite implements ICrawledWebSite {

	private List<IWebPage> webPages;
	private URL webSiteURL;

	CrawledWebSite(List<IWebPage> webPages, URL webSiteURL) {
		this.webPages = webPages;
		this.webSiteURL = webSiteURL;
	}

	@Override
	public List<IWebPage> getWebPages() {
		return webPages;
	}

	@Override
	public URL getWebSiteURL() {
		return webSiteURL;
	}

}
