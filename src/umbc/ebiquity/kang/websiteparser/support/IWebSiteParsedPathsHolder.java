package umbc.ebiquity.kang.websiteparser.support;

import java.net.URL;
import java.util.List;

public interface IWebSiteParsedPathsHolder {

	List<IWebPageParsedPathsHolder> getWebPageParsedPathHolders();

	URL getWebSiteURL();
}
