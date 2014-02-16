package umbc.ebiquity.kang.webpageparser.interfaces;

import java.net.URL;
import java.util.Collection;
import umbc.ebiquity.kang.webpageparser.WebPage;

public interface ICrawler {
	
	public void crawl();
	
	public Collection<WebPage> getCrawledWebPages();
	
	public URL getWebSiteURL();

}
