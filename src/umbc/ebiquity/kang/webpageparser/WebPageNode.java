package umbc.ebiquity.kang.webpageparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Entity;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class WebPageNode {
	
	public enum WebTagNodeType {TextNode, ElementNode}
//	private String tag;
	private WebPageNode parent;
	private WebPageNode child;
	private WebPathPath residePath;
	
	private WebTagNodeType nodeType;
	private String prefixPathID;
	
	private Element element;
	private int tagCount;
//	private TextNode textNode;
	private String textContent;
	private Collection<WebPageNode> children;
	private Collection<WebPageNode> sibling;
	private Map<String, String> attributes;
	private boolean isLeafNode = true;
//	private Collection<Topic> topics;
	
	public WebPageNode(Element node, int tagCount) {
		this.element = node;
		this.tagCount = tagCount;
		this.nodeType = WebTagNodeType.ElementNode;
		if (node.children().size() == 0) {
			this.isLeafNode = true;
		} else {
			this.isLeafNode = false;
		}
		this.textContent = null;
		this.init();
	}
	
	public WebPageNode(String textNodeContent) {
//		this.textNode = node;
		this.nodeType = WebTagNodeType.TextNode;
		this.isLeafNode = true;
		this.textContent = textNodeContent;
		this.init();
	}
	
	private void init(){
//		this.textContent = null;
		this.prefixPathID = null;
		this.parent =  null;
		this.child = null;
		this.residePath = null;
		this.children = null;
		this.sibling = null;
		this.attributes = null;
	}

	public String getTag() {
		if (nodeType == WebTagNodeType.ElementNode) {
//			return element.tagName().toLowerCase() + "(" + this.tagCount + ")";
			return element.tagName().toLowerCase();
		} else {
			return "text";
		}
	}
	
	public String getTagCount(){
		return  "(" + this.tagCount + ")";
	}
	
//	public String getTextContent() {
//		String textContent;
//		if (nodeType == WebPageNodeType.TextNode) {
//			textContent = this.textContent;
//		} else if (this.getTag().equals("img")) {
//			textContent = this.getHiddenText();
//		} else {
//			if (this.textContent == null) {
//				this.extractContent();
//			}
//			textContent = this.textContent;
//		}
//		return TextProcessingUtils.escapeSpecial(textContent);
//	}
	
	public String getOwnContent() {
		String textContent;
		if (nodeType == WebTagNodeType.TextNode) {
			textContent = this.textContent;
		} else if (this.getTag().equals("img")) {
			textContent = this.getHiddenText();
		} else {
			textContent = this.element.ownText().trim();
		}
		return TextProcessingUtils.escapeSpecial(textContent);
	}
	 
	public String getFullContent() {
		String textContent;
		if (nodeType == WebTagNodeType.TextNode) {
			textContent = this.textContent;
		} else if (this.getTag().equals("img")) {
			textContent = this.getHiddenText();
		} else {
			textContent = this.element.text().trim();
		}
		return TextProcessingUtils.escapeSpecial(textContent);
	}
	
	private String getHiddenText() {
		String elemAlt = element.attr("alt");
		String elemTitle = element.attr("title");
		if (elemAlt != null && !TextProcessingUtils.isStringEmpty(elemAlt)) {
			return elemAlt;
		} else if (elemTitle != null && !TextProcessingUtils.isStringEmpty(elemTitle)) {
			return elemTitle;
		}
		return "";
	}

	public Collection<WebPageNode> listChildren() {
		this.populateChildrenCollection();
		return this.children;
	}

	public Collection<WebPageNode> listSiblings() {
		this.populateSiblingCollection();
		return this.sibling;
	}

	public Map<String, String> listAttributes() {
		this.populateAttributeMap();
		return this.attributes;
	}

	public Set<String> attributeKeySet() {
		this.populateAttributeMap();
		return this.attributes.keySet();
	}

	public String attributeValue(String key) {
		this.populateAttributeMap();
		return this.attributes.get(key);
	}

	/***
	 * 
	 */
	private void extractContent() {
		if (nodeType == WebTagNodeType.TextNode) {
//			textContent = textNode.text();
		} else {
			textContent = element.text();
			for (Element child : element.children()) {
				if (!HTMLTags.getIgnoredTags().contains(child.tagName())) {
					textContent = textContent.replace(child.text(), "");
				}
				// if (!HTMLTags.getIgnoredTags().contains(child.tagName())
				// && !HTMLTags.getTopicTags().contains(child.tagName())) {
				// textContent = textContent.replace(child.text(), "");
				// }
			}
			textContent = textContent.trim();
		}
	}
	
	private void populateAttributeMap(){
		
		if (this.attributes == null) {
			this.attributes = new LinkedHashMap<String, String>();
			if(nodeType == WebTagNodeType.TextNode){
				return;
			}
			for (Attribute attribute : this.element.attributes()) {
				String key = attribute.getKey();
				if (key.equalsIgnoreCase("class") || key.equalsIgnoreCase("id") || key.equalsIgnoreCase("style")) {
					attributes.put(attribute.getKey(), attribute.getValue());
				}
			}
		}
	}
	
	private void populateChildrenCollection(){
		if (this.children == null) {
			this.children = new ArrayList<WebPageNode>();
			if(nodeType == WebTagNodeType.TextNode){
				return;
			}
			for (Element child : this.element.children()) {
				WebPageNode nodeWrapper = new WebPageNode(child,1);
				this.children.add(nodeWrapper);
			}
		}
	}
	
	private void populateSiblingCollection(){
		if (this.sibling == null) {
			this.sibling = new ArrayList<WebPageNode>();
			if(nodeType == WebTagNodeType.TextNode){
				return;
			}
			for (Element child : this.element.siblingElements()) {
				WebPageNode nodeWrapper = new WebPageNode(child,1);
				this.sibling.add(nodeWrapper);
			}
		}
	}

//	public void setNodeType(NodeType nodetype) {
//		this.nodetype = nodetype;
//	}

//	public NodeType getNodeType() {
//		return nodetype;
//	}

	public boolean isLeafNode(){
		return this.isLeafNode;
	}
	
	public void setParent(WebPageNode parent) {
		this.parent = parent;
	}

	public WebPageNode getParent() {
		return parent;
	}

	public void setResidePath(WebPathPath residePath) {
		this.residePath = residePath;
	}

	public String getResidePathID() {
		return residePath.getPathID();
	}
	
	public WebPathPath getResidePath() {
		return residePath;
	}

	public void setPrefixPathID(String prefixPath) {
		this.prefixPathID = prefixPath;
	}

	public String getPrefixPathID() {
		return prefixPathID;
	}

	public void setChild(WebPageNode child) {
		this.child = child;
	}

	public WebPageNode getChild() {
		return child;
	}
	
	public Element getWrappedElement(){
		return this.element;
	}
	
	public WebPageNode fomer(int i) {
		WebPageNode node = this;
		for (int index = 0; index < i; index++) {
			node = node.getParent();
			if (node == null)
				break;
		}
		return node;
	}
	
	public String getNodePattern(){

		Map<String, String> attributes = this.listAttributes();
		String attributePattern = this.getAttributePattern(attributes.get("class"), attributes.get("id"), attributes.get("style"));
		StringBuilder patternBuilder = new StringBuilder(this.getTag() + attributePattern);
		
		WebPageNode parentNode = this.getParent();
		while (parentNode != null) {
			Map<String, String> parentAttributes = parentNode.listAttributes();
			String parentAttributePattern = this.getAttributePattern(parentAttributes.get("class"), parentAttributes.get("id"), parentAttributes.get("style"));
			patternBuilder.append("/" + parentNode.getTag() + parentAttributePattern);
			parentNode = parentNode.getParent();
		}

//		StringBuilder patternBuilder = new StringBuilder(this.getTag() + this.getTagCount());
//		
//		WebTagNode parentNode = this.getParent();
//		while (parentNode != null) {
//			patternBuilder.append("/" + parentNode.getTag() + parentNode.getTagCount());
//			parentNode = parentNode.getParent();
//		}
		
		return patternBuilder.toString();
	}
	
	private String getAttributePattern(String classAttri, String idAttri, String styleAttri) {
		
		String attributePattern = null;
		if (classAttri != null && idAttri == null && styleAttri == null) {
			attributePattern = "class:\"" + classAttri + "\"";
		} else if (classAttri == null && idAttri != null && styleAttri == null) {
			attributePattern = "id:\"" + idAttri + "\"";
		} else if (classAttri == null && idAttri == null && styleAttri != null) {
			attributePattern = "style:\"" + styleAttri + "\"";
		} else if (classAttri != null && idAttri != null && styleAttri == null) {
			attributePattern = "class:\"" + classAttri + "\", id:\"" + idAttri + "\"";
		} else if (classAttri != null && idAttri == null && styleAttri != null) {
			attributePattern = "class:\"" + classAttri + "\", style:\"" + styleAttri + "\"";
		} else if (classAttri == null && idAttri != null && styleAttri != null) {
			attributePattern = "id:\"" + idAttri + "\", style:\"" + styleAttri + "\"";
		} else if (classAttri != null && idAttri != null && styleAttri != null) {
			attributePattern = "class:\"" + classAttri + "\", id:\"" + idAttri + "\", style:\"" + styleAttri + "\"";
		}

		attributePattern = attributePattern == null ? "" : "[" + attributePattern + "]";
		return attributePattern;
	}
	
	public void setTagNodeType(WebTagNodeType tagNodeType){
		this.nodeType = tagNodeType;
	}

	@Override
	public WebPageNode clone() {
		WebPageNode node = null;
		if (nodeType == WebTagNodeType.TextNode) {
			node = new WebPageNode(this.textContent);
		} else {
			node = new WebPageNode(this.element, this.tagCount);
			node.setTagNodeType(this.nodeType);
		}
		
		node.setLeafNode(this.isLeafNode);
		node.setPrefixPathID(this.getPrefixPathID());
		return node;
	}

	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}
}
