package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.ebiquity.kang.instanceconstructor.model.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;

public interface IClassifiedInstanceDetailRecord extends IClassifiedInstanceBasicRecord, IConcept2OntClassMappingPairSet {
	
	public Collection<MatchedOntProperty> getRelation2PropertyMappingPairs();

	public Collection<String> getSuperOntClassInHierarchy();
	
	public Collection<OntoClassInfo> getSuperOntoClassesOfMatchedOntoClass();
	
	public Map<OntPropertyInfo, Collection<String>> getProperty2ValuesMap();
	
	public Collection<OntoClassInfo> getFirstLevelRecommendedOntoClasses();
	
	public Collection<OntoClassInfo> getSecondLevelRecommendedOntoClasses(); 
	
	public List<IConcept2OntClassMapping> getConcept2OntClassMappingPairs();

	Map<String, Double> getClassHierarchyNumber2ClosenessScoreMap();

	MatchedOntoClassInfo getMatchedOntoClassInfo();

	void addProperty2Values(OntPropertyInfo property, Collection<String> values);

	void clearProperty2Values(); 

	InstanceTripleSet getTripleSet();

	void setConcept2OntClassMappingPairs(Collection<IConcept2OntClassMapping> concept2OntClassMappings); 

}
