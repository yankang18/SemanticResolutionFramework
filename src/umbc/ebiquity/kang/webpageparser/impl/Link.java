package umbc.ebiquity.kang.webpageparser.impl;

public class Link {
	
	public enum LinkType {EXTERNAL, INTERNAL};
	private String text;
	private String href;
	private LinkType type;
	
	public Link(String href){
		this.href = href;
		this.analyzeLinkType();
	}
	
	private void analyzeLinkType() {
		//TODO: get the link type based on the href
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
//	public void setUrl(String url) {
//		this.url = url;
//	}
	
	public String getHyperReference() {
		return href;
	}

	public LinkType getLinkType() {
		return type;
	}

}
