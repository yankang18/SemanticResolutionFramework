package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;

public interface IConcept2OntClassMapper {

	public Collection<Concept2OntClassMapping> mapConcept2OntClass(Collection<Concept> conceptSet, Collection<OntoClassInfo> ontClasses);

	void setDomainSpecificConceptMap(Map<String, String> domainSpecificConceptMap); 

}
