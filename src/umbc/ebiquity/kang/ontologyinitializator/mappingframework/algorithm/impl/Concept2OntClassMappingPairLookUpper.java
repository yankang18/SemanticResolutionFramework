package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm.SimilarityType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class Concept2OntClassMappingPairLookUpper implements IConcept2OntClassMappingPairLookUpper {

	private SimilarityAlgorithm labelSim = new SimilarityAlgorithm(); // refactor this??
	private double acceptedConceptMappingThreshold = 0.7;
	
	private IManufacturingLexicalMappingRepository MLRepository;
	private IOntologyRepository OntRepository;

	public Concept2OntClassMappingPairLookUpper(IManufacturingLexicalMappingRepository MLRepository, 
			 									IOntologyRepository OntRepository) {
		this.MLRepository = MLRepository;
		this.OntRepository = OntRepository;
	}
	
	public Concept2OntClassMappingPairLookUpper(double acceptedConceptMappingThreshold, 
			                                    IManufacturingLexicalMappingRepository MLRepository, 
			                                    IOntologyRepository OntRepository){
		this(MLRepository, OntRepository);
		this.acceptedConceptMappingThreshold = acceptedConceptMappingThreshold;
	}

	@Override
	public Collection<Concept2OntClassMapping> lookupConcept2OntClassMappingPairs(Concept concept) {
		Collection<Concept2OntClassMapping> concept2OntClassMappingPairs = new ArrayList<Concept2OntClassMapping>();
		for (String conceptInML_name : MLRepository.getAllConcepts()) {
			double conceptsSimilarity = labelSim.getSimilarity(SimilarityType.Semantics, concept.getConceptName(), conceptInML_name);
			if (conceptsSimilarity < acceptedConceptMappingThreshold) {
				continue;
			}

			Collection<OntoClassInfo> mappedOntClasses = MLRepository.getMappedOntClasses(conceptInML_name);
			if (mappedOntClasses.size() == 1) {
				/*
				 * if the concept has only mapped to one class, record this
				 * concept-to-class mapping
				 */
				for (OntoClassInfo ontClass : mappedOntClasses) {
					int hierarchyNumber = OntRepository.getOntClassHierarchyNumber(ontClass);
					String localPathCodeString = OntRepository.getLocalPathCode(ontClass);
					ontClass.setHierarchyNumber(hierarchyNumber);
					ontClass.setLocalPathCode(localPathCodeString);
					Concept conceptInML = new Concept(conceptInML_name);
					
					double score =  this.getConcept2ClassMappingScore(conceptInML, ontClass);
					score = (conceptsSimilarity + score) / 2;
					ontClass.setSimilarityToConcept(score);
					
					System.out.println("From MLR1: <" + concept.getConceptName() + "> --> <" + conceptInML_name + "> --> <" + ontClass.getOntClassName() + "> with " + score);
					Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, ontClass, score);
					concept2ClassMappingPair.setDirectMapping(false); // this mapping is indirect: concept -> conceptInML -> ontoClass
					concept2OntClassMappingPairs.add(concept2ClassMappingPair);
				}

			} else {
				/*
				 * if the concept has mapped to multiple classes, record all the
				 * concept-to-class mapping. However, every concept is only
				 * allowed to be mapped to one class from a class hierarchy.
				 */
				Map<String, OntoClassInfo> hierarchy2OntClassMap = new LinkedHashMap<String, OntoClassInfo>();
				for (OntoClassInfo ontClass : mappedOntClasses) {

//					System.out.println("Mapped Class: " + ontClass.getOntClassName());
					int hierarchyNumber = OntRepository.getOntClassHierarchyNumber(ontClass);
					String localPathCodeString = OntRepository.getLocalPathCode(ontClass);
					ontClass.setHierarchyNumber(hierarchyNumber);
					ontClass.setLocalPathCode(localPathCodeString);
					String hierarchyNumberStr = String.valueOf(hierarchyNumber);
					
//					System.out.println("Hierarchical Number: " + hierarchyNumberStr);
//					System.out.println("Local Path Code: " + localPathCodeString);
					OntoClassInfo hashedOntClass = hierarchy2OntClassMap.get(hierarchyNumberStr);
					if (hashedOntClass == null) {
						hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
					} else {
						/*
						 * if the concept has been mapped to multiple classes
						 * from the same hierarchy, choose one of these classes
						 * according the rules that: (1) if two classes in the
						 * same path, choose the more specific one. Otherwise
						 * (2) choose the class that has the highest score with
						 * the concept
						 */
						if (hashedOntClass.getLocalPathCode().contains(ontClass.getLocalPathCode())) {
							// Do nothing here
						} else if (ontClass.getLocalPathCode().contains(hashedOntClass.getLocalPathCode())) {
							/*
							 * record the most specific class, if the two
							 * classes are in the same path
							 */
							hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
						} else {

							/*
							 * if the two classes are in different path, record
							 * the class that has the highest score with the
							 * concept
							 */
							Concept conceptInML = new Concept(conceptInML_name);
							double score1 = this.getConcept2ClassMappingScore(conceptInML, hashedOntClass);
							double score2 = this.getConcept2ClassMappingScore(conceptInML, ontClass);
							if (score2 > score1){
								hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
							}
						}
					}
				}
				
				for (OntoClassInfo clazz : hierarchy2OntClassMap.values()) {
					Concept conceptInML = new Concept(conceptInML_name);
					double score = this.getConcept2ClassMappingScore(conceptInML, clazz);
					score = (conceptsSimilarity + score) / 2;
					clazz.setSimilarityToConcept(score);
					System.out.println("From MLR2: <" + concept.getConceptName() + "> --> <" + conceptInML_name + "> --> <" + clazz.getOntClassName() + "> with " + score);
					Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, clazz, score);
					concept2ClassMappingPair.setDirectMapping(false); // this mapping is indirect: concept -> conceptInML -> ontoClass
					concept2OntClassMappingPairs.add(concept2ClassMappingPair);
				}
			}
		}
		return concept2OntClassMappingPairs;
	}

	/**
	 * Compute the score of the concept-to-ontClass mapping. This score reflects
	 * the correctness of this mapping
	 * 
	 * @param concept
	 *            - an instance of the Concept class
	 * @param ontClass
	 *            - an instance of the OntClass class
	 * @return a score that reflects the correctness of the this
	 *         concept-to-ontClass mapping
	 */
	private double getConcept2ClassMappingScore(Concept concept, OntoClassInfo ontClass){
		
		double score = 0.0;
		try {
			IConcept2OntClassMappingStatistics mappingStatistics = MLRepository.getConcept2ClassMappingStatistics(concept, ontClass);
			int failedCounts = mappingStatistics.getFailedCounts();
			int succeedCounts = mappingStatistics.getSucceedCounts();
			int unSettledCounts = mappingStatistics.getUndeterminedCounts();
			double similarity = mappingStatistics.getSimilarity();

			double alpha = 0.3; // adjustment damping ratio
			int totalCounts = failedCounts + succeedCounts + unSettledCounts;
			if (totalCounts == 0) {
				/*
				 * if failedCounts, succeedCounts and unSetteledCounts are all
				 * Zero, then this mapping is brand-new. Therefore, just returning
				 * the similarity score;
				 */
				return similarity;
			}
			
			double succeedWeight = 1.0;
			double failedWeight = -1.0;
			double unSettltedWeight = 0.5;
			double weightedTotalCounts = failedWeight * failedCounts + succeedWeight * succeedCounts + unSettltedWeight * unSettledCounts;
			double adjt_strength = weightedTotalCounts / totalCounts; // adjustment strength

			double maximalAdjustmentQuantity = 0.0; // maximal adjustment quantity
			if (adjt_strength >= 0) {
				maximalAdjustmentQuantity = 1 - similarity;
			} else {
				maximalAdjustmentQuantity = similarity;
			}
			score = similarity + alpha * maximalAdjustmentQuantity * adjt_strength;
		} catch (NoSuchEntryItemException e) {
			return score;
		}
		return score;
	}
}