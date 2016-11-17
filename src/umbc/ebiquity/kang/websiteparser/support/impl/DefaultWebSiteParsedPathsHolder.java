package umbc.ebiquity.kang.websiteparser.support.impl;

import java.net.URL;
import java.util.List;

import umbc.ebiquity.kang.websiteparser.support.IWebPageParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;

public class DefaultWebSiteParsedPathsHolder implements IWebSiteParsedPathsHolder {

	private URL webSiteURL;
	private List<IWebPageParsedPathsHolder> webPagePathHolders;

	DefaultWebSiteParsedPathsHolder(URL webSiteURL, List<IWebPageParsedPathsHolder> webPagePathHolders) {
		this.webSiteURL = webSiteURL;
		this.webPagePathHolders = webPagePathHolders;
	}

    /*
     * (non-Javadoc)
     * @see umbc.ebiquity.kang.entityframework.support.IWebSitePathHolder#getWebPagePathHolders()
     */
	public List<IWebPageParsedPathsHolder> getWebPageParsedPathHolders() {
		return webPagePathHolders;
	}

	/*
	 * (non-Javadoc)
	 * @see umbc.ebiquity.kang.entityframework.support.IWebSitePathHolder#getWebSiteURL()
	 */
	public URL getWebSiteURL() {
		return webSiteURL;
	}
}
