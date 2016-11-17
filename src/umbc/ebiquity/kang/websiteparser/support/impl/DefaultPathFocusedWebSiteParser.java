package umbc.ebiquity.kang.websiteparser.support.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import umbc.ebiquity.kang.websiteparser.ICrawledWebSite;
import umbc.ebiquity.kang.websiteparser.IWebPage;
import umbc.ebiquity.kang.websiteparser.support.IPathFocusedWebSiteParser;
import umbc.ebiquity.kang.websiteparser.support.IWebPageParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;

public class DefaultPathFocusedWebSiteParser implements IPathFocusedWebSiteParser {

	private ICrawledWebSite website;

	DefaultPathFocusedWebSiteParser(ICrawledWebSite website) {
		this.website = website;
	}

	public IWebSiteParsedPathsHolder parse() {
		List<IWebPageParsedPathsHolder> webPagePaths = new ArrayList<IWebPageParsedPathsHolder>();
		for (IWebPage webPage : website.getWebPages()) {
			// TODO: may user static method to parse web page for saving memory
			DefaultWebPagePathsParser webPagePathsImpl = new DefaultWebPagePathsParser(webPage);
			IWebPageParsedPathsHolder webPagePathHolder = webPagePathsImpl.parse();
			webPagePaths.add(webPagePathHolder);
		}
		return new DefaultWebSiteParsedPathsHolder(website.getWebSiteURL(), webPagePaths);
	}

}
