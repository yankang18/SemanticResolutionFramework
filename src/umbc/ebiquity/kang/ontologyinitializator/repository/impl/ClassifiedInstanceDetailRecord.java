package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.ebiquity.kang.instanceconstructor.impl.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingPairSet;

public class ClassifiedInstanceDetailRecord extends ClassifiedInstanceBasicRecord implements IClassifiedInstanceDetailRecord, IConcept2OntClassMappingPairSet {

	private InstanceTripleSet tripleSet;
	private List<MatchedOntProperty> matchedOntPropertyPair;
	protected Map<OntPropertyInfo, Collection<String>> property2ValuesMap;
	protected MatchedOntoClassInfo matchedOntClassResult;
	
	private List<IConcept2OntClassMapping> _concept2OntClassMappingPairs;
	private Map<String, IConcept2OntClassMapping> _conceptName2MappingPairMap;
	
	public ClassifiedInstanceDetailRecord(InstanceTripleSet tripleGroup, MatchedOntoClassInfo matchedOntClassResult) {
		super(tripleGroup.getSubjectLabel(), matchedOntClassResult.getMatchedOntoClassInfo(), matchedOntClassResult.getSimilarity());
		this.tripleSet = tripleGroup;
		this.matchedOntPropertyPair = new ArrayList<MatchedOntProperty>();
		this.property2ValuesMap = new LinkedHashMap<OntPropertyInfo, Collection<String>>();
		this.matchedOntClassResult = matchedOntClassResult;
		this._concept2OntClassMappingPairs = new ArrayList<IConcept2OntClassMapping>();
		this._conceptName2MappingPairMap = new HashMap<String, IConcept2OntClassMapping>();
	}

	@Override
	public InstanceTripleSet getTripleSet() {
		return tripleSet;
	}

	public void addMatchedOntoPropertyCollection(Collection<MatchedOntProperty> pairs) {
		this.matchedOntPropertyPair.addAll(pairs);
	}
	
	@Override
	public void addProperty2Values(OntPropertyInfo property, Collection<String> values){
		property2ValuesMap.put(property, values);
	}
	
	@Override
	public void clearProperty2Values(){
		property2ValuesMap.clear();
	}

	@Override
	public Collection<MatchedOntProperty> getRelation2PropertyMappingPairs() {
		return this.matchedOntPropertyPair;
	}
	
	@Override
	public Collection<String> getSuperOntClassInHierarchy() {
		Collection<String> ontClasses = new ArrayList<String>();
		for (OntoClassInfo ontClassInfo : this._ontClass.getSuperOntClassInHierarchy()) {
			ontClasses.add(ontClassInfo.getOntClassName());
		}
		return ontClasses;
	}
	
	@Override
	public Collection<OntoClassInfo> getSuperOntoClassesOfMatchedOntoClass() {
		return this._ontClass.getSuperOntClassInHierarchy();
	}
	
	@Override
	public Map<OntPropertyInfo, Collection<String>> getProperty2ValuesMap(){
		return this.property2ValuesMap;
	}
	
	@Override
	public Collection<OntoClassInfo> getFirstLevelRecommendedOntoClasses(){
		return this.matchedOntClassResult.getFirstLevelRecommendedOntoClasses();
	}
	
	@Override
	public Collection<OntoClassInfo> getSecondLevelRecommendedOntoClasses(){
		return this.matchedOntClassResult.getSecondLevelRecommendedOntoClasses();
	} 
	
	@Override
	public Map<String, Double> getClassHierarchyNumber2ClosenessScoreMap(){
		return this.matchedOntClassResult.getClassHierarchyNumber2ClosenessScoreMap();
	}
	
	@Override
	public MatchedOntoClassInfo getMatchedOntoClassInfo(){
		return this.matchedOntClassResult;
	}
	
	@Override
	public void setConcept2OntClassMappingPairs(Collection<IConcept2OntClassMapping> concept2OntClassMappings){
		_concept2OntClassMappingPairs.clear();
		_conceptName2MappingPairMap.clear();
		for(IConcept2OntClassMapping pair : concept2OntClassMappings){
			String mappedOntoClassName = "NULL";
			if(pair.getMappedOntoClass() != null){
				mappedOntoClassName = pair.getMappedOntoClassName();
			}
			System.out.println("HERE: " + pair.getConceptName() + " " + mappedOntoClassName + " " + pair.isMappedConcept());
			this._concept2OntClassMappingPairs.add(pair);
			this._conceptName2MappingPairMap.put(pair.getConceptName(), pair);
		}
	}

	@Override
	public List<IConcept2OntClassMapping> getConcept2OntClassMappingPairs() {
		return _concept2OntClassMappingPairs;
	}

	@Override
	public IConcept2OntClassMapping getConcept2OntClassMappingPairByConceptName(String conceptName) {
		return _conceptName2MappingPairMap.get(conceptName);
	}

}
