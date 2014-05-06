package umbc.ebiquity.kang.webpageparser;

import java.util.Collection;
import java.util.LinkedList;

import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.webpageparser.WebTagNode.WebTagNodeType;

public class WebTagPath {
	
	private String pathID;
	private LinkedList<WebTagNode> nodeList;
	private String host;
	private boolean isPathPatternCreated = false;
	private String pathPattern;
	
	public WebTagPath(){
		pathID = null;
		nodeList = new LinkedList<WebTagNode>();
	}
	
	public void addNode(WebTagNode node) {
		WebTagNode last = null;
		if (nodeList.size() != 0) {
			last = nodeList.getLast();
			last.setChild(node);
		}
		node.setParent(last);
		node.setPrefixPathID(this.getPathID());
		node.setResidePath(this);
		nodeList.add(node);
	}
	
	public String getPathID() {
		return this.computePathID();
	}
	
	public String getPathPattern() {
		if (this.isPathPatternCreated) {
			return pathPattern;
		}
		return this.computePathPattern();
	}

	public WebTagNode getNode(String prefixPathID){
		for(WebTagNode node : nodeList){
			if(node.getPrefixPathID().equals(prefixPathID)){
				return node;
			}
		}
		return null;
	}
	
	@Override
	public WebTagPath clone(){
		WebTagPath newPath = new WebTagPath();
		newPath.setPathID(this.getPathID());
		newPath.setClonedNodes(this.getClonedNodes());
		return newPath;
	}
	
	private void setPathID(String pathID) {
		this.pathID = pathID;
	}
	
	private void setClonedNodes(Collection<WebTagNode> nodes) {
		for (WebTagNode node : nodes) {
			node.setResidePath(this);
			this.nodeList.add(node);
		}
	}
	
	private Collection<WebTagNode> getClonedNodes() {
		LinkedList<WebTagNode> newNodes = new LinkedList<WebTagNode>();

		WebTagNode parent = null;
		for (WebTagNode node : this.nodeList) {
			WebTagNode newNode = node.clone();
			if (parent != null) {
				parent.setChild(newNode);
			}
			newNode.setParent(parent);
			newNodes.add(newNode);
			parent = newNode;
		}
		return newNodes;
	}
	
	private String computePathID(){
		StringBuilder builder = new StringBuilder();
		for (WebTagNode node : nodeList) {
			
//			StringBuilder attributeBuilder = new StringBuilder("[");
//			for (String key : node.attributeKeySet()) {
//				attributeBuilder.append(key + ":" + node.attributeValue(key) + ",");
//			}
//			String attributes = node.listAttributes().size() == 0 ? "" : attributeBuilder.substring(0, attributeBuilder.length() - 1) + "]";
//			String content  = "";
//			if (node.isLeafNode()) {
//				builder.append(node.getTag() + attributes + "[" + node.getFullContent() + "]");
//			} else {
//				builder.append(node.getTag() + attributes + content + "/");
//			}
			
			
////			StringBuilder attributeBuilder = new StringBuilder("[");
////			for (String key : node.attributeKeySet()) {
////				attributeBuilder.append(key + ":" + node.attributeValue(key) + ",");
////			}
////			String attributes = node.listAttributes().size() == 0 ? "" : attributeBuilder.substring(0, attributeBuilder.length() - 1) + "]";

			String content  = "";
			if (node.isLeafNode()) {
				builder.append(node.getTag() + node.getTagCount() + "[" + node.getFullContent() + "]");
			} else {
				builder.append(node.getTag() + node.getTagCount() + content + "/");
			}
		}
		return builder.toString();
	}
	
	private String computePathPattern() {
		StringBuilder builder = new StringBuilder();
		for (WebTagNode node : nodeList) {

			StringBuilder attributeBuilder = new StringBuilder("[");
			for (String key : node.attributeKeySet()) {
				attributeBuilder.append(key + ":" + node.attributeValue(key) + ",");
			}
			String attributes = node.listAttributes().size() == 0 ? "" : attributeBuilder.substring(0, attributeBuilder.length() - 1) + "]";
			String content = "";
			if (node.isLeafNode()) {
				builder.append(node.getTag() + attributes + "[" + node.getFullContent() + "]");
			} else {
				builder.append(node.getTag() + attributes + content + "/");
			}

		}
		this.isPathPatternCreated = true;
		this.pathPattern =builder.toString();
		return pathPattern;
	}

	public boolean containsTextContent() {
		boolean containsTextContent = false;
		for (WebTagNode node : nodeList) {
			
			if(!TextProcessingUtils.isStringEmpty(node.getFullContent())){
				containsTextContent = true;
			}
			if (containsTextContent) {
				return containsTextContent;
			}
		}
		return containsTextContent;
	}
	
	public WebTagNode getLastNode() {
		if (nodeList.size() == 0) {
			return null;
		}
		
		return nodeList.getLast();
	}
	
	public WebTagNode getSecondToLastNode(){
		
		if (nodeList.size() == 1) {
			return null;
		}
		
		return nodeList.get(nodeList.size() - 2);
	}
	
	@Override
	public int hashCode(){
		return this.getPathPattern().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		WebTagPath webPagePath = (WebTagPath) obj;
		return this.getPathPattern().equals(webPagePath.getPathPattern());
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
