package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;

public interface IConcept2OntClassMapping {
	
	public OntoClassInfo getMappedOntoClass();

	public String getMappedOntoClassName();

	public double getMappingScore();

	public String getConceptName();
	
	public Concept getConcept();
	
	public String getProvenantHostInstance();

	public boolean isMappedConcept();

	public MappingRelationType getRelation();
	
	public void setMappedOntoClass(OntoClassInfo mappedOntoClass, MappingRelationType relation);
	
	public void setMappedOntoClass(OntoClassInfo mappedOntoClass, MappingRelationType relation, double mappingScore);
	
	public void setProvenantHostInstance(String provenance);

	boolean isHittedMapping();

	boolean isDirectMapping();

	boolean isManualMapping();

	String getHostInstance();

	void setHostInstance(String instance);

	String getMappingCode(); 
	
}
