package umbc.ebiquity.kang.webpageparser;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public interface Crawler {
 
	public Collection<WebPage> crawl() throws IOException; 

	public Collection<WebPage> getCrawledWebPages() throws IOException; 

	public URL getWebSiteURL();

}
