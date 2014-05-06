package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IClassificationCorrection {

	String getInstanceSource();
	
	String getInstance();
	
	OntoClassInfo getCorrectionSourceClass();

	OntoClassInfo getCorrectionTargetClass();

	Collection<String> getUnMappedConcepts();

	Map<String, String> getMappedClassHierarchies();

	Collection<IConcept2OntClassMapping> getHittedMappings();

	Collection<IConcept2OntClassMapping> getAmbiguousMappings();

	void setInstancePrevenance(String instanceSource);
	
	void setInstance(String instanceName);

	void addConcept2OntClassMapping(IConcept2OntClassMapping mapping);

	void addMappedClassHierarchy(String hierarchyNumber, String closenessScore);

	void setCorrectionSource(OntoClassInfo source);

	void setCorrectionTarget(OntoClassInfo target);

	String getCorrectionRepresentationCode(); 
	

}
