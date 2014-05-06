package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMatchedOntProperty;

public class MatchedOntProperty implements IMatchedOntProperty{

	private String _ontPropertyURI;
	private String _ontPropertyNS;
	private String _relationName;
	private String _ontPropertyName;
	private double _similarity;
	
	public MatchedOntProperty(String relationName, String ontPropertyURI, String ontPropertyNameSpace, 
			                               String ontPropertyName, double similarity) {
		this._relationName = relationName;
		this._ontPropertyName = ontPropertyName;
		this._ontPropertyURI = ontPropertyURI;
		this._ontPropertyNS = ontPropertyNameSpace;
		this._similarity = similarity;
	}
	
	public String getOntPropertyURI(){
		return this._ontPropertyURI;
	}
	
	public String getOntPropertyNameSpace(){
		return this._ontPropertyNS;
	}
	
	public String getOntPropertyName(){
		return this._ontPropertyName;
	}
	
	public String getRelationName(){
		return this._relationName;
	}
	
	public double getSimilarity(){
		return this._similarity;
	}

	@Override
	public int compareTo(IMatchedOntProperty mappedRelationInfo) {
		if (this.getSimilarity() > mappedRelationInfo.getSimilarity()) {
			return -1;
		} else if (this.getSimilarity() == mappedRelationInfo.getSimilarity()) {
			return 0;
		} else {
			return 1;
		}
	}
}
