package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IClassifiedInstanceBasicRecord extends Comparable<IClassifiedInstanceBasicRecord> {
	
	public String getInstanceLabel();
	
	public String getOntoClassName() ;
	
	public String getOntoClassNameSpace();

	public String getOntoClassURI();
	
	public double getSimilarity();

	public OntoClassInfo getMatchedOntoClass();

	public void setInstanceName(String newLabel);

	void setMatchedOntoClass(OntoClassInfo ontoClass);
 
	void setSimilarity(double similarity);  

}
