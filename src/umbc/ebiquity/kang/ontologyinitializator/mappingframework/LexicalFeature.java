package umbc.ebiquity.kang.ontologyinitializator.mappingframework;

public abstract class LexicalFeature {
	public enum Position {
		BEGIN, END, ANY;

		public static Position getPosition(String positionString) {
			positionString = positionString.toUpperCase();
			if (positionString.equals(Position.ANY)) {
				return Position.ANY;
			} else if (positionString.equals(Position.END)) {
				return Position.END;
			} else {
				return Position.BEGIN;
			}
		}
	}
	
	public enum Type {
		PHRASE, SUBSTRING
	}

	protected String label;
	protected Position subStringPosition;
	protected double support;
	protected double significant;
	private int count;
	private Type type;

	protected LexicalFeature(String label, Position subStringPosition, Type type) {
		this.label = label.toLowerCase();
		this.subStringPosition = subStringPosition;
		this.type = type;
	}

	public Position getFeaturePosition() {
		return this.subStringPosition;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	public double getSupport() {
		return support;
	}

	public void setSignificant(double significant) {
		this.significant = significant;
	}

	public double getSignificant() {
		return significant;
	}

	public String getLabel() {
		return label;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return this.label + "@[" + this.type + "-" + this.subStringPosition + "]";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		LexicalFeature lexicalFeature = (LexicalFeature) obj;
		return this.toString().equals(lexicalFeature.toString());
	}
}
