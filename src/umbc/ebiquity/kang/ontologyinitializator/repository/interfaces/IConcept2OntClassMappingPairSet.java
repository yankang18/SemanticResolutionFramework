package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;

public interface IConcept2OntClassMappingPairSet {

	public Collection<IConcept2OntClassMapping> getConcept2OntClassMappingPairs();

	public IConcept2OntClassMapping getConcept2OntClassMappingPairByConceptName(String conceptName);

}
