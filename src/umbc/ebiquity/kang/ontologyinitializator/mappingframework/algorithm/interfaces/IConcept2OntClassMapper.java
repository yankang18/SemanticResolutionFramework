package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;

public interface IConcept2OntClassMapper {

	void setDomainSpecificConceptMap(Map<String, String> domainSpecificConceptMap);

	Collection<Concept2OntClassMapping> mapConcept2OntClass(Collection<Concept> conceptSet, Collection<OntoClassInfo> ontClasses); 
}
