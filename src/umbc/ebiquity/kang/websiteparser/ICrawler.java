package umbc.ebiquity.kang.websiteparser;

import java.io.IOException;

public interface ICrawler {

	ICrawledWebSite crawl() throws IOException;

	ICrawledWebSite getCrawledWebSite() throws IOException; 

}
