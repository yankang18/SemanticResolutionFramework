package umbc.ebiquity.kang.ontologyinitializator.entityframework.component;

import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.webpageparser.LeafNode;

public class EntityNode {
	
	private String label;
	private String processedLabel;
	private double score;
	private Entity entity;
	
	private boolean isLeafNode;
	private boolean isIntermediateNode;
	
	public EntityNode(Entity entity) {
		this(entity.getEntityLabel(), entity.getScore(), false);
		this.entity = entity;
		this.isIntermediateNode = true;
	}

	public EntityNode(String termLabel) {
		this(termLabel, 0.0, false);
	}

	public EntityNode(LeafNode leafNode) {
		this(leafNode.getNodeContent().trim(), 0.0, false);
		this.isLeafNode = true;
	}
	
	private EntityNode(String termLabel, double score, boolean inferred){
		this.label = termLabel.replaceAll("-", "").trim();
		this.score = score;
		this.processedLabel = TextProcessingUtils.getProcessedLabel(label, " ");
	}
	
	public void addScore(double score) {
		this.score = this.score + score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}
	
	public Entity getTerm(){
		return this.entity;
	}

	public String getLabel() {
		return label.trim();
	}
	
	public String getProcessedTermLabel(){
		return this.processedLabel;
	}
	
	public boolean isLeafNode(){
		return this.isLeafNode;
	}
	
	public boolean isIntermediateNode(){
		return this.isIntermediateNode;
	}
	
	@Override
	public String toString(){
		return this.label.trim();
	}
	
	@Override
	public int hashCode() {
//		return this.label.hashCode();
		return this.processedLabel.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		EntityNode termNode = (EntityNode) obj;
//		return this.label.equalsIgnoreCase(termNode.label);
		return this.processedLabel.equals(termNode.processedLabel);
	}

}
