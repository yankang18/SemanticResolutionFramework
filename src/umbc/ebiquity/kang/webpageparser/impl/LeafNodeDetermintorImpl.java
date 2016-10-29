package umbc.ebiquity.kang.webpageparser.impl;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.webpageparser.LeafNodeDetermintor;

public class LeafNodeDetermintorImpl implements LeafNodeDetermintor {

	@Override
	public boolean isLeafNode(Element element) {
		int numberOfTextNode = 0;
		for (Node child : element.childNodes()) {
			if (child instanceof TextNode) {
				if (!TextProcessingUtils.isStringEmpty(((TextNode) child).text())) {
					numberOfTextNode++;
				}
			} else if (child instanceof Element) {
				if (!"br".equals(((Element) child).tagName().toLowerCase())) {
					return false;
				}
			}
		}
		if (numberOfTextNode > 1) {
			return false;
		}
		return true;
	}

}
