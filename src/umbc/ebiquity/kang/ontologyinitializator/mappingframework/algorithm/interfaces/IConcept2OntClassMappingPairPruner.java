package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;

public interface IConcept2OntClassMappingPairPruner {


	Collection<IConcept2OntClassMapping> getPrunedConcept2OntoClassMappingPairs(int classHierarchyNumber, String instanceLabel,
			Collection<Concept> conceptCollection, Collection<Concept2OntClassMapping> concept2OntClassMappingPairCollection);

	Collection<IConcept2OntClassMapping> recordConcept2OntoClassMappings(
			Collection<IConcept2OntClassMapping> matchedConcept2OntClassMappingPairs, String instanceLabel, 
			Collection<Concept> fullConceptSet);

	Collection<IConcept2OntClassMapping> resolveOne2OneOntoClassMappingPairs(int bestHierarchyNumber,
			Collection<Concept2OntClassMapping> allConcept2OntClassMappingPairs); 

}
