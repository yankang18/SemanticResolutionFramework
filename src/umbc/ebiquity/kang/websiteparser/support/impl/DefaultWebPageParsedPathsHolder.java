package umbc.ebiquity.kang.websiteparser.support.impl;

import java.util.List;

import umbc.ebiquity.kang.websiteparser.IWebPagePath;
import umbc.ebiquity.kang.websiteparser.impl.WebPathPath;
import umbc.ebiquity.kang.websiteparser.object.CrawlerUrl;
import umbc.ebiquity.kang.websiteparser.support.IWebPageParsedPathsHolder;

public class DefaultWebPageParsedPathsHolder implements IWebPageParsedPathsHolder {
 
	private CrawlerUrl pageURL;
	private String baseURL;
	private List<IWebPagePath> webPagePathList;
	DefaultWebPageParsedPathsHolder(CrawlerUrl pageURL, String baseURL, List<IWebPagePath> webPagePathList) {
		this.pageURL = pageURL;
		this.baseURL = baseURL;
		this.webPagePathList = webPagePathList;
	}

	@Override
	public List<IWebPagePath> listWebTagPathsWithTextContent() {
		return webPagePathList;
	}

}
