package umbc.ebiquity.kang.entityframework.object;

import umbc.ebiquity.kang.websiteparser.object.Link;

public class Entity {

	public enum TermType {
		Concept, Role, Description
	}
	
	private String termLabel;
	private String presentingLabel;
	private String description;
	private Link link;
	private boolean hasLink = false;
	private String wrappingTag;
	private double score;
	private int level;
	private int webPageIndex;
	private TermType termType;

	public Entity(String termLabel) {
//		this.termLabel = this.removeBracket(termLabel);
		this.termLabel = getTrimedLabel(termLabel);
		this.presentingLabel = termLabel;
		this.termType = TermType.Concept;
	}

	public void setLink(Link link) {
		this.link = link;
		this.hasLink = true;
	}

	public Link getLink() {
		return this.link;
	}

	public String getEntityLabel() {
		return this.termLabel;
	}
	
	public String getPresentingTermLabel() {
		return this.presentingLabel;
	}

	public boolean hasLink() {
		return hasLink;
	}

	public void setWrappingTag(String wrappingTag) {
		this.wrappingTag = wrappingTag;
	}

	public String getWrappingTag() {
		return wrappingTag;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
	
	public void setWebPageIndex(int webPageIndex){
		this.webPageIndex = webPageIndex;
	}
	
	public int getWebPageIndex(){
		return this.webPageIndex;
	}

	public String getCombinedScoreString() {
		return this.getWebPageIndex() + ";" + this.getLevel() + ";" + this.getScore();
	}

	@Override
	public int hashCode() {
		return this.termLabel.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Entity topic = (Entity) obj;
		return this.getEntityLabel().equals(topic.getEntityLabel());
	}

	public void setTermType(TermType termType) {
		this.termType = termType;
	}

	public TermType getTermType() {
		return termType;
	}
	
	protected String getTrimedLabel(String label) {

		int preIndex = label.indexOf('(');
		int afterIndex = label.indexOf(')');
		while (preIndex >= 0 && preIndex < afterIndex) {
			String before = label.substring(0, preIndex);
			String after = label.substring(afterIndex + 1);
			label = before + after;
			preIndex = label.indexOf('(');
			afterIndex = label.indexOf(')');
		}

		label = label.replaceAll(" - ", "-");
		int hythenIndex = label.indexOf('-');
		if (hythenIndex != -1) {
			label = label.substring(0, label.indexOf('-')).trim();
		}
		return label;
	}
}
