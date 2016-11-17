package umbc.ebiquity.kang.websiteparser;

import org.jsoup.nodes.Element;

public interface IWebPageNode {

	String getTag();

	String getFullContent();

	String getNodePattern();

	IWebPageNode getParent();

	String getPrefixPathID();

	Element getWrappedElement();  
 
}
