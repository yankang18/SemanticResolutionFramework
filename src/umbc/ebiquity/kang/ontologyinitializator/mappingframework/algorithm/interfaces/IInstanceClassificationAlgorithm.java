package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;

public interface IInstanceClassificationAlgorithm {
	
	/**
	 * This method is the starting point of classifying instances into the right
	 * onto-classes Identify best matched classes
	 * 
	 * @return a collection of matched subject-class
	 */
	public void classifyInstances();

	public Collection<ClassifiedInstanceDetailRecord> getClassifiedInstances();
	
	public void setRelation2PropertyMap(Map<String, MatchedOntProperty> map);

	IConcept2OntClassMapper getConcept2OntClassMapper();
	
}
