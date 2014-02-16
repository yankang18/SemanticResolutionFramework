package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;


public interface IMatchedOntProperty extends Comparable<IMatchedOntProperty> {

	public String getRelationName();

	public String getOntPropertyName();

	public String getOntPropertyNameSpace();

	public String getOntPropertyURI();

	public double getSimilarity();

}
