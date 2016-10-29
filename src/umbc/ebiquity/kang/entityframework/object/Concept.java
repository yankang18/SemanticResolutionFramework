package umbc.ebiquity.kang.entityframework.object;

public class Concept {

	private String label;
	private String computingLabel;
	private double score = 1.0;
	private boolean isFromInstance = false;

	public Concept(String label) {
		this(label, false);
	}

	public Concept(String label, boolean isFromInstance) {
		this.label = label.trim();
		this.computingLabel = this.label;
		this.isFromInstance = isFromInstance;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void multipleScore(double score) {
		this.score = this.score * score;
	}

	public void addScore(double score) {
		this.score = this.score + score;
	}

	public double getScore() {
		return this.score;
	}

	public void updateLabel(String label) {
		this.label = label;
		this.computingLabel = label;
	}

	public void updateComputingLabel(String computeLabel) {
		this.computingLabel = computeLabel;
	}

	public String getConceptName() {
		return this.label.toLowerCase();
	}

	public String getComputingLabel() {
		return this.computingLabel;
	}

	public boolean isFromInstance() {
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
