package umbc.ebiquity.kang.websiteparser.impl;

import java.util.Collection;
import java.util.LinkedList;

import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;
import umbc.ebiquity.kang.websiteparser.IWebPagePath;

public class WebPagePath implements IWebPagePath {

	private String pathId;
	private LinkedList<WebPageNode> nodeList;
	private String host;
	private boolean isPathPatternCreated = false;
	private boolean isPathIdCreated = false;
	private String pathPattern;
	
	public WebPagePath(){
		pathId = null;
		nodeList = new LinkedList<WebPageNode>();
	}
	
	public void addNode(WebPageNode node) {
		WebPageNode last = null;
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
		if (isPathIdCreated) {
			return pathId;
		}
		return computePathId();
	}

	public String getPathPattern() {
		if (isPathPatternCreated) {
			return pathPattern;
		}
		return computePathPattern();
	}

	public WebPageNode getNode(String prefixPathID){
		for(WebPageNode node : nodeList){
			if(node.getPrefixPathID().equals(prefixPathID)){
				return node;
			}
		}
		return null;
	}
	
	@Override
	public WebPagePath clone(){
		WebPagePath newPath = new WebPagePath();
		newPath.setPathID(this.getPathID());
		newPath.setClonedNodes(this.getClonedNodes());
		return newPath;
	}
	
	private void setPathID(String pathId) {
		this.pathId = pathId;
	}
	
	private void setClonedNodes(Collection<WebPageNode> nodes) {
		for (WebPageNode node : nodes) {
			node.setResidePath(this);
			this.nodeList.add(node);
		}
	}
	
	private Collection<WebPageNode> getClonedNodes() {
		LinkedList<WebPageNode> newNodes = new LinkedList<WebPageNode>();
		WebPageNode parent = null;
		for (WebPageNode node : this.nodeList) {
			WebPageNode newNode = node.clone();
			if (parent != null) {
				parent.setChild(newNode);
			}
			newNode.setParent(parent);
			newNodes.add(newNode);
			parent = newNode;
		}
		return newNodes;
	}
	
	private String computePathId() {
		StringBuilder builder = new StringBuilder();
		for (WebPageNode node : nodeList) {
			String content = "";
			if (node.isLeafNode()) {
				builder.append(node.getTag() + node.getTagCount() + "[" + node.getFullContent() + "]");
			} else {
				builder.append(node.getTag() + node.getTagCount() + content + "/");
			}
		}
		isPathIdCreated = true;
		pathId = builder.toString();
		return pathId;
	}
	
	private String computePathPattern() {
		StringBuilder builder = new StringBuilder();
		for (WebPageNode node : nodeList) {

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
		this.pathPattern = builder.toString();
		return pathPattern;
	}

	public boolean containsTextContent() {
		boolean containsTextContent = false;
		for (WebPageNode node : nodeList) {
			
			if(!TextProcessingUtils.isStringEmpty(node.getFullContent())){
				containsTextContent = true;
			}
			if (containsTextContent) {
				return containsTextContent;
			}
		}
		return containsTextContent;
	}
	
	public WebPageNode getLastNode() {
		if (nodeList.size() == 0) {
			return null;
		}
		
		return nodeList.getLast();
	}
	
	public WebPageNode getSecondToLastNode(){
		
		if (nodeList.size() == 1) {
			return null;
		}
		
		return nodeList.get(nodeList.size() - 2);
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}
	
	@Override
	public int hashCode(){
		return this.getPathPattern().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		WebPagePath webPagePath = (WebPagePath) obj;
		return this.getPathPattern().equals(webPagePath.getPathPattern());
	}
}
