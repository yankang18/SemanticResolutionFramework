package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;

public class ClassifiedInstanceBasicRecord implements IClassifiedInstanceBasicRecord {
	
	protected String _instanceLabel;
	protected OntoClassInfo _ontClass;
	protected double _similarity;
	
	public ClassifiedInstanceBasicRecord(String instanceLabel, OntoClassInfo ontClass, double similarity){
		this._instanceLabel = instanceLabel;
		this._ontClass = ontClass;
		this._similarity = similarity;
	}
	
	@Override
	public String getInstanceLabel() {
		return this._instanceLabel;
	} 
	
	@Override
	public String getOntoClassName() {
		return this._ontClass.getOntClassName();
	}
	
	@Override
	public String getOntoClassNameSpace() {
		return this._ontClass.getNameSpace();
	}

	@Override
	public String getOntoClassURI() {
		return this._ontClass.getURI();
	}
	
	@Override
	public double getSimilarity() {
		return _similarity;
	}
	
	@Override
	public OntoClassInfo getMatchedOntoClass() {
		return _ontClass;
	}

	@Override
	public void setInstanceName(String instanceLabel){
		this._instanceLabel = instanceLabel;
	}
	
	@Override
	public void setMatchedOntoClass(OntoClassInfo ontoClass){
		this._ontClass = ontoClass;
	}
	
	@Override
	public void setSimilarity(double similarity){
		this._similarity = similarity;
	}
	
	@Override
	public int compareTo(IClassifiedInstanceBasicRecord classifiedInstanceBasicInfo) {
		if (this.getSimilarity() > classifiedInstanceBasicInfo.getSimilarity()) {
			return -1;
		} else if (this.getSimilarity() == classifiedInstanceBasicInfo.getSimilarity()) {
			return 0;
		} else {
			return 1;
		}
	}
}
