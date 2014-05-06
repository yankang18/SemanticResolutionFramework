package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;

public class Concept2OntClassMappingPairPruner implements IConcept2OntClassMappingPairPruner {

	@Override
	public Collection<IConcept2OntClassMapping>  getPrunedConcept2OntoClassMappingPairs(int classHierarchyNumber, String instanceLabel, Collection<Concept> conceptCollection, Collection<Concept2OntClassMapping> concept2OntClassMappingPairCollection){
		Collection<IConcept2OntClassMapping> resolvedConcept2OntClassMappingPairs = this.resolveOne2OneOntoClassMappingPairs(classHierarchyNumber, concept2OntClassMappingPairCollection);
		Collection<IConcept2OntClassMapping> allConcept2OntClassMappingPairs = this.recordConcept2OntoClassMappings(resolvedConcept2OntClassMappingPairs, instanceLabel, conceptCollection);
		return allConcept2OntClassMappingPairs;
	}
	/**
	 * 
	 * @param bestHierarchyNumber
	 * @param allConcept2OntClassMappingPairs
	 * @return
	 */
	@Override
	public Collection<IConcept2OntClassMapping> resolveOne2OneOntoClassMappingPairs(int bestHierarchyNumber, Collection<Concept2OntClassMapping> allConcept2OntClassMappingPairs) {
		
		Collection<IConcept2OntClassMapping> one2OneOntClassMappingPair = new HashSet<IConcept2OntClassMapping>();
		
		Map<String, Collection<Concept2OntClassMapping>> conceptName2IndirectMappingPairsMap = new LinkedHashMap<String, Collection<Concept2OntClassMapping>>();
		for(Concept2OntClassMapping concept2OntClassMappingPair : allConcept2OntClassMappingPairs){
			int hierarchrNumber = concept2OntClassMappingPair.getMappedOntoClass().getHierarchyNumber();
			if (hierarchrNumber == bestHierarchyNumber) {
				/*
				 * 
				 */
				concept2OntClassMappingPair.setHittedMapping(true);
			}
			
			if (concept2OntClassMappingPair.isDirectMapping()) {
				/*
				 * if a concept-to-class mapping is direct mapping, this mapping
				 * is already an one-to-one mapping.
				 */
				one2OneOntClassMappingPair.add(concept2OntClassMappingPair);
				continue;
			}

			/*
			 * if a concept-to-class mapping is indirect mapping, we group them
			 * by their concept names. This is because a concept may be
			 * indirectly mapped to multiple classes and we will pick one
			 * concept-to-class mapping from each group.
			 */
			String conceptName = concept2OntClassMappingPair.getConceptName();
			Collection<Concept2OntClassMapping> mappingPair = conceptName2IndirectMappingPairsMap.get(conceptName);
			if (mappingPair == null) {
				mappingPair = new HashSet<Concept2OntClassMapping>();
				conceptName2IndirectMappingPairsMap.put(conceptName, mappingPair);
			}
			mappingPair.add(concept2OntClassMappingPair);
		}

		/*
		 * The following code basically picks one concept-to-class mapping from
		 * each group by following rules: (1) if the mapped class of a
		 * concept-to-class mapping in a group is from the "best" hierarchy,
		 * then this mapping is picked out. (2) otherwise, pick the mapping with
		 * the highest score from each group.
		 */
		for(Collection<Concept2OntClassMapping> concept2OntClassIndirectMappingPairs : conceptName2IndirectMappingPairsMap.values()){
			
			List<Concept2OntClassMapping> concept2OntClassIndirectMappingPairList = new ArrayList<Concept2OntClassMapping>(concept2OntClassIndirectMappingPairs);
			int numOfIndirectMappingWithSameConcept = concept2OntClassIndirectMappingPairList.size();
			if (numOfIndirectMappingWithSameConcept == 1) {
				one2OneOntClassMappingPair.add(concept2OntClassIndirectMappingPairList.get(0));
				continue;
			} else {
				Collections.sort(concept2OntClassIndirectMappingPairList);
			}
			
			Concept2OntClassMapping concept2OntClassMappingPairWithMaxScore = null;
			double maxScore = 0.0;
			boolean hasHittedMappingPair = false;
			for (Concept2OntClassMapping concept2OntClassMappingPair : concept2OntClassIndirectMappingPairList) {
				int hierarchrNumber = concept2OntClassMappingPair.getMappedOntoClass().getHierarchyNumber();
				if (hierarchrNumber == bestHierarchyNumber) {
					one2OneOntClassMappingPair.add(concept2OntClassMappingPair);
					hasHittedMappingPair = true;
					break;
				}
				
				if(concept2OntClassMappingPair.getMappingScore() > maxScore){
					maxScore = concept2OntClassMappingPair.getMappingScore() ;
					concept2OntClassMappingPairWithMaxScore = concept2OntClassMappingPair;
				}
				
			}
			if (!hasHittedMappingPair) {
				one2OneOntClassMappingPair.add(concept2OntClassMappingPairWithMaxScore);
			}
		}
		return one2OneOntClassMappingPair;
	}
	
	@Override
	public Collection<IConcept2OntClassMapping> recordConcept2OntoClassMappings(Collection<IConcept2OntClassMapping> matchedConcept2OntClassMappingPairs, 
			                                                                       String instanceLabel,
			                                                                       Collection<Concept> fullConceptSet) {
		
		Set<String> mappedConceptlist = new HashSet<String>();
		Collection<IConcept2OntClassMapping> mappingPairs = new LinkedHashSet<IConcept2OntClassMapping>();
		mappedConceptlist.add(instanceLabel.trim());
		for(IConcept2OntClassMapping mappingPair: matchedConcept2OntClassMappingPairs){
			mappedConceptlist.add(mappingPair.getConceptName());
			Concept mappedConcept = mappingPair.getConcept();
			String ontoClassLabel = mappingPair.getMappedOntoClassName();
			double score = mappingPair.getMappingScore();
//			if((mappingPair.isDirectMapping() && score <= this.samenessThreshold && !mappedConcept.isFromInstance()) 
//					||!mappingPair.isDirectMapping() && !mappedConcept.isFromInstance()){
//			if((mappingPair.isDirectMapping() && !mappedConcept.isFromInstance()) 
//					||!mappingPair.isDirectMapping() && !mappedConcept.isFromInstance()){
			if (mappingPair.isDirectMapping() || !mappingPair.isDirectMapping()) {
				Concept concept = new Concept(mappedConcept.getConceptName());
				Concept2OntClassMapping pair = new Concept2OntClassMapping(concept, mappingPair.getMappedOntoClass(), score);
				pair.setHittedMapping(mappingPair.isHittedMapping());
				mappingPairs.add(pair);
				System.out.println("> Mapped Concept: " + concept.getConceptName() + " : "  + ontoClassLabel + " : " + score);
			} 
		}
		
		for (Concept concept : fullConceptSet) { 
			if (!mappedConceptlist.contains(concept.getConceptName()) && !concept.isFromInstance()) {
				/*
				 * record the non-mapped concepts
				 */
				Concept2OntClassMapping pair = new Concept2OntClassMapping(concept, null, 0.0);
				mappingPairs.add(pair);
				System.out.println("> Non-mapped Concept: " + concept.getConceptName());
			}
		}
		return mappingPairs;
	}

}
