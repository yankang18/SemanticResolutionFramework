package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.OrderedWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SubSumptionRelationshipBoostingLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;

public class Concept2OntClassMapper implements IConcept2OntClassMapper {
	
	private double thresholdOfLegitimateCandidateClass = 0.50;
	private double equalityThreshold = 0.9;
	private Map<String, String> domainSpecificTermMap;
	private IConcept2OntClassMappingPairLookUpper concept2OntClassMappingPairLookUpper;
	private ILabelSimilarity labelSimilarity;
	private boolean applyMappingRule = true;
	
	public Concept2OntClassMapper(IConcept2OntClassMappingPairLookUpper concept2OntClassMappingPairLookUpper, boolean applyMappingRule){
		this.domainSpecificTermMap = new HashMap<String, String>();
		this.concept2OntClassMappingPairLookUpper = concept2OntClassMappingPairLookUpper;
		this.labelSimilarity = new SubSumptionRelationshipBoostingLabelSimilarity(new OrderedWordListSimilarity(), true);
		this.applyMappingRule = applyMappingRule;
	}
	
	public Concept2OntClassMapper(){
		this.domainSpecificTermMap = new HashMap<String, String>();
		this.labelSimilarity = new SubSumptionRelationshipBoostingLabelSimilarity(new OrderedWordListSimilarity(), true);
		this.applyMappingRule = false;
	}
	
	@Override
	public void setDomainSpecificConceptMap(Map<String, String> domainSpecificConceptMap){
		this.domainSpecificTermMap.putAll(domainSpecificConceptMap);
	}

	@Override
	public Collection<Concept2OntClassMapping> mapConcept2OntClass(Collection<Concept> conceptSet, Collection<OntoClassInfo> ontClasses) {
		
		List<Concept2OntClassMapping> concept2OntClassMappingPairs = new ArrayList<Concept2OntClassMapping>(); 
		for (Concept concept : conceptSet) {

			/*
			 * 
			 */
			if (applyMappingRule) {
				List<Concept2OntClassMapping> concept2OntClassMappingPairsFromMLP = concept2OntClassMappingPairLookUpper
						.lookupConcept2OntClassMappingPairs(concept);
				if (concept2OntClassMappingPairsFromMLP.size() != 0) {
					concept2OntClassMappingPairs.addAll(concept2OntClassMappingPairsFromMLP);
				}
				
				if (concept2OntClassMappingPairsFromMLP.size() > 0) {
					continue;
				}
			}
			
//			for (String relation : domainSpecificTermMap.keySet()) {
//				double sim = labelSimilarity.computeLabelSimilarity(concept.getConceptName(), relation);
//				if (sim >= this.equalityThreshold) {
//					concept.updateComputingLabel(domainSpecificTermMap.get(relation)); 
//				}
//			}
			
			/*
			 * initialize the maxSimiliarty to the threshold of legitimate
			 * candidate onto-class such that all the candidate onto-classes
			 * would have above-threshold similarity to certain concept
			 */
			double maxSimilarity = this.thresholdOfLegitimateCandidateClass;
			boolean isDirectMapping = true;
			OntoClassInfo similarOntClass = null; 
			for (OntoClassInfo ontClassInfo : ontClasses) {
				
				double similarity = this.computeLabelSimilarity(concept, ontClassInfo);
//				if (similarity > 0.7) {
//					System.out.println(concept.getConceptName() + ": " + ontClassInfo.getOntClassName() + " - " + similarity);
//				}
				/*
				 * record the onto-class with the highest similarity to current
				 * comparing concept
				 */
				if (similarity >= maxSimilarity) {
					maxSimilarity = similarity;
					isDirectMapping = true;
					similarOntClass = ontClassInfo;
				}
			}
			
			if (similarOntClass != null) {
				
				// TODO: try cloning the OntoClass
				System.out.println("Mapped Concept: " + concept.getConceptName() + " : " + similarOntClass.getOntClassName() + " : " + similarOntClass.getSimilarityToConcept() + " : " + maxSimilarity);
				Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, similarOntClass, maxSimilarity);
				concept2ClassMappingPair.setDirectMapping(isDirectMapping);
				concept2OntClassMappingPairs.add(concept2ClassMappingPair);
			}
		}
		
//		for(Concept2OntClassMappingPair pair : matchedConcept2OntClassMappingPairs){
//			System.out.println("HERE0^^ " + pair.getMatchedConceptLabel() + " : " + pair.getMatchedOntClass().getLabel() + " : " + pair.getMatchedOntClass().getSimilarityToConcept() + ":" + pair.getSimilarity());
//		}
		
		return concept2OntClassMappingPairs;
	}
	
	/**
	 * Compute the similarity between a <strong>concept</strong> and a <strong>onto-class</strong>
	 * 
	 * @param label1
	 * @param label2
	 * @return the similarity
	 */
	private double computeLabelSimilarity(Concept concept, OntoClassInfo ontoClassInfo){
		double similarity = labelSimilarity.computeLabelSimilarity(concept.getComputingLabel(), ontoClassInfo.getOntClassName());
		ontoClassInfo.setSimilarityToConcept(similarity);
		return similarity;
	}

}
