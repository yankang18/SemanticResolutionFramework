package umbc.ebiquity.kang.ontologyinitializator.entityframework;


/***
 * 
 * 
 * @author kangyan2003
 */
public class Concept {
	
	private String label;
	private double score = 1.0;
	private boolean isFromInstance = false;
	
	public Concept(String label){
		this(label, false);
	}
	
	public Concept(String label, boolean isFromInstance){
//		this.label = this.getTrimedLabel(label.trim());
		this.label = label.trim();
		this.isFromInstance = isFromInstance;
	}
	
	public void setScore(double score){
		this.score = score;
	}
	
	public void multipleScore(double score){
		this.score = this.score * score;
	}
	
	public void addScore(double score){
		this.score = this.score + score;
	}
	
	public double getScore(){
		return this.score;
	}
	
	public void updateConceptName(String conceptName){
		this.label = conceptName;
	}
	public String getConceptName(){
		return this.label;
	}
	
	public boolean isFromInstance(){
		return this.isFromInstance;
	}

	@Override
	public String toString() {
		return this.label;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Concept concept = (Concept) obj;
		return this.getConceptName().equals(concept.getConceptName());
	}
}
