package umbc.ebiquity.kang.webpageparser;



public class LeafNode {
	
	public enum LeafType {
		Description, Term
	}

	private LeafType leafType;
	private String href;
	private String content;
	
	public LeafNode(LeafType leafType, String nodeContent){
		this.leafType = leafType;
		this.content = nodeContent;
	}
	
	public LeafNode(LeafType leafType, String nodeContent, String href){
		this(leafType, nodeContent);
		this.href = href;
	}
	
	public void setHyperRef(String href){
		this.href = href;
	}

	public LeafType getLeafType() {
		return leafType;
	}

	public String getHyperRef() {
		return href;
	}

	public String getNodeContent() {
		return content;
	}
	
	

}
