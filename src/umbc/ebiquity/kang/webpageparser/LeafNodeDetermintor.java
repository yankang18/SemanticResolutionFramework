package umbc.ebiquity.kang.webpageparser;

import org.jsoup.nodes.Element;

public interface LeafNodeDetermintor {

	boolean isLeafNode(Element element);

}
