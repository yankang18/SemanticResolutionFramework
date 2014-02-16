package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;

public interface IConcept2OntClassMappingPairLookUpper {

	/**
	 * look up all concept-to-class mapping pairs that existed in the
	 * Manufacturing Lexicon Repository. <br>
	 * Although multiple concept-to-class mapping pairs can be returned, each
	 * concept can only be mapped to one class in a class hierarchy. If one
	 * concept has been mapped to multiple classes from the same class
	 * hierarchy, the concept-to-class mapping pairs with the highest score will
	 * be returned.
	 * 
	 * @param concept - an instance of Concept
	 * @return a collection of concept-to-class mapping pairs
	 */
	Collection<Concept2OntClassMapping> lookupConcept2OntClassMappingPairs(Concept concept);

}
