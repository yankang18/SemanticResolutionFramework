package umbc.ebiquity.kang.websiteparser.support.impl;

import umbc.ebiquity.kang.websiteparser.ICrawledWebSite;
import umbc.ebiquity.kang.websiteparser.support.IPathFocusedWebSiteParser;

public class PathFocusedWebSiteParserFactory {

	public static IPathFocusedWebSiteParser createParser(ICrawledWebSite website) {
		return new DefaultPathFocusedWebSiteParser(website);
	}

}
