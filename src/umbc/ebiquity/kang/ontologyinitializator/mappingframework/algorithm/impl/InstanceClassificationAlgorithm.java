package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IBestMatchedOntClassFinder;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmComponent;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmVisitor;
import umbc.ebiquity.kang.ontologyinitializator.ontology.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;

public class InstanceClassificationAlgorithm implements IInstanceClassificationAlgorithm,  IMappingAlgorithmComponent {
	
	/**
	 * Java representation of a triple store extracted from a web site
	 */
	private ITripleRepository tripleStore;
	
	/**
	 * Java representation of the specific domain ontology
	 */
	private IOntologyRepository ontologyRepository;
	private IConcept2OntClassMapper concept2OntClassMapper;
	private IBestMatchedOntClassFinder BestMatchedOntClassFinder;
	/**
	 * 
	 */
	private Collection<ClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList;
	
	private Map<String, MatchedOntProperty> relation2PropertyMap;

	public InstanceClassificationAlgorithm(ITripleRepository tripleStore, IOntologyRepository ontologyRepository, IConcept2OntClassMapper concept2OntClassMapper){
		this.tripleStore = tripleStore;
		this.ontologyRepository = ontologyRepository;
		this.concept2OntClassMapper = concept2OntClassMapper;
		this.BestMatchedOntClassFinder = new BestMatchedOntClassFinder(ontologyRepository);
		
		this.classifiedInstanceDetailInfoList = new ArrayList<ClassifiedInstanceDetailRecord>();
		this.relation2PropertyMap = new LinkedHashMap<String, MatchedOntProperty>();
	}
	
	@Override
	public void setRelation2PropertyMap(Map<String, MatchedOntProperty> map){
		this.relation2PropertyMap.putAll(map);
	}
	
	
	@Override
	public void classifyInstances() {
		this.classifyInstances(tripleStore.getInstanceTripleSets(), ontologyRepository.getAllOntClasses());
	}

	/*
	 * the "public" modifier only for test
	 */
	private void classifyInstances(Collection<InstanceTripleSet> instanceTripleSets, Collection<OntoClassInfo> ontClasses) {
		List<ClassifiedInstanceDetailRecord> classifiedInstanceInfoes = new ArrayList<ClassifiedInstanceDetailRecord>();
		for (InstanceTripleSet instanceTripleSet : instanceTripleSets) {
			System.out.println();
			System.out.println("Identifying class for: <" + instanceTripleSet.getSubjectLabel() + ">");
			
			Collection<MatchedOntProperty> matchedOntProperties =this.collectMatchedOntProperties(instanceTripleSet);
			Set<Concept> fullConceptSet = this.collectConcepts(instanceTripleSet);

			
			Collection<Concept2OntClassMapping> concept2OntClassMappingPairs = concept2OntClassMapper.mapConcept2OntClass(fullConceptSet, ontClasses);
			MatchedOntoClassInfo matchedOntClassResult = BestMatchedOntClassFinder.findBestMatchedOntoClass(instanceTripleSet.getSubjectLabel(), concept2OntClassMappingPairs);
			
			if (matchedOntClassResult != null) {

				IConcept2OntClassMappingPairPruner concept2OntClassMappingPairPruner = new Concept2OntClassMappingPairPruner();
				Collection<IConcept2OntClassMapping> prunedConcept2OntClassMappingPairs = concept2OntClassMappingPairPruner.getPrunedConcept2OntoClassMappingPairs(
								matchedOntClassResult.getClassHierarchyNumber(),
								instanceTripleSet.getSubjectLabel(), 
								fullConceptSet, 
								concept2OntClassMappingPairs);

				/*
				 * if such best matched ontology class exists, record the
				 * instance and this class as matched pair
				 */
				OntoClassInfo bestMatchedOntClassInfo = matchedOntClassResult.getMatchedOntoClassInfo();
				Collection<OntoClassInfo> ontClassSet = ontologyRepository.getUpwardCotopy(bestMatchedOntClassInfo);
				bestMatchedOntClassInfo.addSuperOntClassesInHierarchy(ontClassSet);
				ClassifiedInstanceDetailRecord classifiedInstanceInfo = new ClassifiedInstanceDetailRecord(instanceTripleSet, matchedOntClassResult);
				classifiedInstanceInfo.setConcept2OntClassMappingPairs(prunedConcept2OntClassMappingPairs);
				classifiedInstanceInfo.addMatchedOntoPropertyCollection(matchedOntProperties);
				classifiedInstanceInfoes.add(classifiedInstanceInfo);
			}
		}
		Collections.sort(classifiedInstanceInfoes);
		this.classifiedInstanceDetailInfoList.addAll(classifiedInstanceInfoes);
	}
	
	private Collection<MatchedOntProperty> collectMatchedOntProperties(InstanceTripleSet instanceTripleSet){
		Collection<MatchedOntProperty> relation2PropertyMappingPairs = new ArrayList<MatchedOntProperty>();
		for (String relation : instanceTripleSet.getCustomRelation()) {
			MatchedOntProperty pair = relation2PropertyMap.get(relation);
			if (pair != null) {
				relation2PropertyMappingPairs.add(pair);
			}
		}
		return relation2PropertyMappingPairs;
	}
	
	private Set<Concept> collectConcepts(InstanceTripleSet instanceTripleSet){
		Set<Concept> fullConceptSet = new LinkedHashSet<Concept>();
		
		/*
		 * 
		 */
		Collection<Concept> conceptSet = instanceTripleSet.getConceptSet();
		if (conceptSet != null) {
			for (Concept concept : conceptSet) {
				/*
				 *  record the concepts learned from the Entity Graph
				 */
				String value = concept.getConceptName().trim();
				fullConceptSet.add(concept);
				double score = Math.log(concept.getScore()) > 1 ? Math.log(concept.getScore()) : 1;
				System.out.println("   - concept: " + value + ", " + score);
			}
		}
		
		/*
		 *  get concepts from matched properties 
		 */
		Collection<MatchedOntProperty> relation2PropertyMappingPairs = new ArrayList<MatchedOntProperty>();
		for (String relation : instanceTripleSet.getCustomRelation()) {
			MatchedOntProperty pair = relation2PropertyMap.get(relation);
			if (pair != null) {
				relation2PropertyMappingPairs.add(pair);
				String targetPropertyURI = pair.getOntPropertyURI();
				OntPropertyInfo ontPropertyInfo = ontologyRepository.getOntPropertyByURI(targetPropertyURI);
				for(String domainStr : ontPropertyInfo.getAllDomains()){
					System.out.println("   - concept: " + domainStr + " from property " + ontPropertyInfo.getLocalName() + ", " + 1.0);
					Concept concept = new Concept(domainStr);
					concept.setScore(1.0);
					fullConceptSet.add(concept);
				}
			}
		}
		return fullConceptSet;
	}
    
	@Override
    public IConcept2OntClassMapper getConcept2OntClassMapper(){
    	return this.concept2OntClassMapper;
    }

    @Override
    public Collection<ClassifiedInstanceDetailRecord> getClassifiedInstances(){
    	return this.classifiedInstanceDetailInfoList;
    }
    
	@Override
	public void accept(IMappingAlgorithmVisitor visitor) {
		visitor.visit(this);
	}
    
	
}
