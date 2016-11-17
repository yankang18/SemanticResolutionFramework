package umbc.ebiquity.kang.websiteparser;

import java.net.URL;
import java.util.List;

public interface ICrawledWebSite {
	
	public List<IWebPage> getWebPages();

	public URL getWebSiteURL();
}
