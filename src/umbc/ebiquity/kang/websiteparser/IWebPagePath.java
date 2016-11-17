package umbc.ebiquity.kang.websiteparser;

public interface IWebPagePath {

	String getPathID();

	IWebPageNode getLastNode();

	String getHost();

	IWebPageNode getNode(String prefixPathID);

	boolean containsTextContent();     

}
