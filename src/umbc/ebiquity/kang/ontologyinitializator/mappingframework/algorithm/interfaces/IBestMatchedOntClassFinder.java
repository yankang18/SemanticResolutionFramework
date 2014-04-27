package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;

public interface IBestMatchedOntClassFinder {
	/**
	 * Identify the best matched class from a set of candidate classes. Two
	 * steps are involved: <br/>
	 * (1) identify the closest class hierarchy <br/>
	 * (2) identify the best class from this identified class hierarchy <br/>
	 * 
	 * @param concept2OntClassMappingPairs
	 *            - a collection of candidate classes
	 * @return an instance of MatchedOntClass - the best matched class including
	 *         similarity score
	 */
	public MatchedOntoClassInfo findBestMatchedOntoClass(String instanceLable, 
			 											 Collection<Concept2OntClassMapping> concept2OntClassMappingPairs,
			 											 IClassificationCorrectionRepository aggregratedClassificationCorrectionRepository);
		
}
