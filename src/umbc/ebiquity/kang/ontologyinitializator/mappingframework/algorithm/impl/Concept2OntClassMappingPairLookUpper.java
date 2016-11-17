package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class Concept2OntClassMappingPairLookUpper implements IConcept2OntClassMappingPairLookUpper {

	private SimilarityAlgorithm labelSim = new SimilarityAlgorithm(); // refactor this??
	private double acceptedConceptMappingThreshold = 0.7;
	
	private IManufacturingLexicalMappingRecordsReader MLRepository;
	private IOntologyRepository OntRepository;

	public Concept2OntClassMappingPairLookUpper(IManufacturingLexicalMappingRecordsReader MLRepository, 
			 									IOntologyRepository OntRepository) {
		this.MLRepository = MLRepository;
		this.OntRepository = OntRepository;
	}
	
	public Concept2OntClassMappingPairLookUpper(double acceptedConceptMappingThreshold, 
												IManufacturingLexicalMappingRecordsReader MLRepository, 
			                                    IOntologyRepository OntRepository){
		this(MLRepository, OntRepository);
		this.acceptedConceptMappingThreshold = acceptedConceptMappingThreshold;
	}
	
	@Override
	public boolean hasMappedOntClass(Concept concept) {
		int size = MLRepository.getMappedOntClasses(concept.getConceptName()).size();
		if (size == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<Concept2OntClassMapping> lookupConcept2OntClassMappingPairs(Concept concept) {
		List<Concept2OntClassMapping> concept2OntClassMappingPairs = new ArrayList<Concept2OntClassMapping>();
		
		Set<String> hierarchies = new HashSet<String>();
		Map<String, OntoClassInfo> hierarchy2MappedOntClass = new HashMap<String, OntoClassInfo>();
//		OntoClassInfo mappedOntClass1 = null;
		for (OntoClassInfo ontClass : MLRepository.getMappedOntClasses(concept.getConceptName().toLowerCase())){
			double newScore = this.getConcept2ClassMappingScore(concept, ontClass);
//			if (applyScore >= 0.4) {
				String hierarchyStr = String.valueOf(OntRepository.getOntClassHierarchyNumber(ontClass));
				if (hierarchy2MappedOntClass.containsKey(hierarchyStr)) {
					
					double oldScore = hierarchy2MappedOntClass.get(hierarchyStr).getSimilarityToConcept();
					if(newScore > oldScore){
						ontClass.setSimilarityToConcept(newScore);
						hierarchy2MappedOntClass.put(hierarchyStr, ontClass);
					}
//					
//					String newClassName = ontClass.getOntClassName();
//					String oldClassName = hierarchy2MappedOntClass.get(hierarchyStr).getOntClassName();
//					if(OntRepository.isSuperClassOf(newClassName, oldClassName, false)){
//						ontClass.setSimilarityToConcept(newScore);
//						hierarchy2MappedOntClass.put(hierarchyStr, ontClass);
//					}
					
				} else {
					ontClass.setSimilarityToConcept(newScore);
					hierarchy2MappedOntClass.put(hierarchyStr, ontClass);
				}
//			}
		}
		
		
		for(String hierarchyStr : hierarchy2MappedOntClass.keySet()){
			OntoClassInfo ontoClassInfo = hierarchy2MappedOntClass.get(hierarchyStr);
			Concept2OntClassMapping concept2ClassMapping = new Concept2OntClassMapping(concept, ontoClassInfo, ontoClassInfo.getSimilarityToConcept());
			concept2ClassMapping.setDirectMapping(false); 
			concept2OntClassMappingPairs.add(concept2ClassMapping);
		}
		
//		if (concept2OntClassMappingPairs.size() > 0) {
//			return concept2OntClassMappingPairs;
//		}
//		
//		String conceptName = concept.getConceptName();
//		List<String> superConcepts = this.getSuperConcept(conceptName);
//		for(String superConcept : superConcepts){
//			Concept newConcept = new Concept(superConcept, concept.isFromInstance());
//			newConcept.setScore(concept.getScore());
//			newConcept.updateComputingLabel(concept.getComputingLabel()); 
//			
//			Set<String> hierarchies2 = new HashSet<String>();
//			
//			
//			OntoClassInfo mappedOntClass = null;
//			for (OntoClassInfo ontClass : MLRepository.getMappedOntClasses(superConcept)){
//				double maxApplyScore = 0.0;
//				double applyScore = this.getConcept2ClassMappingApplyScore(newConcept, ontClass);
//				if (applyScore > 0.6 && applyScore > maxApplyScore) {
//					String hierarchyStr = String.valueOf(OntRepository.getOntClassHierarchyNumber(ontClass));
//					if (!hierarchies2.contains(hierarchyStr)) {
//						mappedOntClass = ontClass;
//						hierarchies2.add(hierarchyStr);
//						double similarity = this.getSimilarity(newConcept, mappedOntClass);
//						mappedOntClass.setSimilarityToConcept(similarity);
//						System.out.println("From MLR2: <" + newConcept.getConceptName() + "> --> <" + mappedOntClass.getOntClassName() + "> with " + similarity);
//						Concept2OntClassMapping concept2ClassMapping = new Concept2OntClassMapping(newConcept, mappedOntClass, similarity);
//						concept2ClassMapping.setDirectMapping(false); 
//						concept2OntClassMappingPairs.add(concept2ClassMapping);
//					}
//				}
//			}
//			if (concept2OntClassMappingPairs.size() > 0) {
//				break;
//			}
//
//		}
		
		return concept2OntClassMappingPairs;
	}
	
	private List<String> getSuperConcept(String conceptName){
		String[] token = conceptName.split(" ");
		List<String> superConcepts = new ArrayList<String>();
		int length = token.length;
		for (int i = 0; i < length - 1; i++) {
			StringBuilder previousTokens = new StringBuilder();
			for (int j = i + 1; j < length; j++) {
				previousTokens.append(" " + TextProcessingUtils.pluralStem(token[j]));
			}
			superConcepts.add(previousTokens.toString().trim());
		}
		return superConcepts;
	}
	
	/*
	 * For testing only
	 */
	public Concept2OntClassMappingPairLookUpper(){}
	public static void main(String[] args) {
		Concept2OntClassMappingPairLookUpper xx = new Concept2OntClassMappingPairLookUpper();
		List<String> concepts = xx.getSuperConcept("sheet metal fabrication");
		for(String concept : concepts){
			System.out.println(concept);;
		}
	}
	
	
//	@Override
//	public Collection<Concept2OntClassMapping> lookupConcept2OntClassMappingPairs(Concept concept) {
//		Collection<Concept2OntClassMapping> concept2OntClassMappingPairs = new ArrayList<Concept2OntClassMapping>();
//		
//		String conceptName = concept.getConceptName();
//		
//		Collection<OntoClassInfo> mappedOntClasses = MLRepository.getMappedOntClasses(concept.getConceptName());
//
//		if (mappedOntClasses != null && mappedOntClasses.size() > 0) {
//
//			for (OntoClassInfo ontClass : mappedOntClasses) {
//				double applyScore = this.getConcept2ClassMappingApplyScore(concept, ontClass);
//				if (applyScore > 0.6) {
//					ontClass.setSimilarityToConcept(applyScore);
//					double similarity = this.getSimilarity(concept, ontClass);
//					System.out.println("From MLR: <" + concept.getConceptName() + "> --> <" + ontClass.getOntClassName() + "> with " + similarity);
//					Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, ontClass, similarity);
//					concept2ClassMappingPair.setDirectMapping(false); 
//					concept2OntClassMappingPairs.add(concept2ClassMappingPair);
//				}
//			}
//		}
//		return concept2OntClassMappingPairs;
//	}
	

//	@Override
//	public Collection<Concept2OntClassMapping> lookupConcept2OntClassMappingPairs(Concept concept) {
//		Collection<Concept2OntClassMapping> concept2OntClassMappingPairs = new ArrayList<Concept2OntClassMapping>();
//		for (String conceptInML_name : MLRepository.getAllConcepts()) {
//			double conceptsSimilarity = labelSim.getSimilarity(SimilarityType.Semantics, concept.getConceptName(), conceptInML_name);
//			if (conceptsSimilarity < acceptedConceptMappingThreshold) {
//				continue;
//			}
//
//			Collection<OntoClassInfo> mappedOntClasses = MLRepository.getMappedOntClasses(conceptInML_name);
//			if (mappedOntClasses.size() == 1) {
//				/*
//				 * if the concept has only mapped to one class, record this
//				 * concept-to-class mapping
//				 */
//				for (OntoClassInfo ontClass : mappedOntClasses) {
//					int hierarchyNumber = OntRepository.getOntClassHierarchyNumber(ontClass);
//					String localPathCodeString = OntRepository.getLocalPathCode(ontClass);
//					ontClass.setHierarchyNumber(hierarchyNumber);
//					ontClass.setLocalPathCode(localPathCodeString);
//					Concept conceptInML = new Concept(conceptInML_name);
//					
//					double score =  this.getConcept2ClassMappingScore(conceptInML, ontClass);
//					score = (conceptsSimilarity + score) / 2;
//					ontClass.setSimilarityToConcept(score);
//					
//					System.out.println("From MLR1: <" + concept.getConceptName() + "> --> <" + conceptInML_name + "> --> <" + ontClass.getOntClassName() + "> with " + score);
//					Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, ontClass, score);
//					concept2ClassMappingPair.setDirectMapping(false); // this mapping is indirect: concept -> conceptInML -> ontoClass
//					concept2OntClassMappingPairs.add(concept2ClassMappingPair);
//				}
//
//			} else {
//				/*
//				 * if the concept has mapped to multiple classes, record all the
//				 * concept-to-class mapping. However, every concept is only
//				 * allowed to be mapped to one class from a class hierarchy.
//				 */
//				Map<String, OntoClassInfo> hierarchy2OntClassMap = new LinkedHashMap<String, OntoClassInfo>();
//				for (OntoClassInfo ontClass : mappedOntClasses) {
//
////					System.out.println("Mapped Class: " + ontClass.getOntClassName());
//					int hierarchyNumber = OntRepository.getOntClassHierarchyNumber(ontClass);
//					String localPathCodeString = OntRepository.getLocalPathCode(ontClass);
//					ontClass.setHierarchyNumber(hierarchyNumber);
//					ontClass.setLocalPathCode(localPathCodeString);
//					String hierarchyNumberStr = String.valueOf(hierarchyNumber);
//					
////					System.out.println("Hierarchical Number: " + hierarchyNumberStr);
////					System.out.println("Local Path Code: " + localPathCodeString);
//					OntoClassInfo hashedOntClass = hierarchy2OntClassMap.get(hierarchyNumberStr);
//					if (hashedOntClass == null) {
//						hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
//					} else {
//						/*
//						 * if the concept has been mapped to multiple classes
//						 * from the same hierarchy, choose one of these classes
//						 * according the rules that: (1) if two classes in the
//						 * same path, choose the more specific one. Otherwise
//						 * (2) choose the class that has the highest score with
//						 * the concept
//						 */
//						if (hashedOntClass.getLocalPathCode().contains(ontClass.getLocalPathCode())) {
//							// Do nothing here
//						} else if (ontClass.getLocalPathCode().contains(hashedOntClass.getLocalPathCode())) {
//							/*
//							 * record the most specific class, if the two
//							 * classes are in the same path
//							 */
//							hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
//						} else {
//
//							/*
//							 * if the two classes are in different path, record
//							 * the class that has the highest score with the
//							 * concept
//							 */
//							Concept conceptInML = new Concept(conceptInML_name);
//							double score1 = this.getConcept2ClassMappingScore(conceptInML, hashedOntClass);
//							double score2 = this.getConcept2ClassMappingScore(conceptInML, ontClass);
//							if (score2 > score1){
//								hierarchy2OntClassMap.put(hierarchyNumberStr, ontClass);
//							}
//						}
//					}
//				}
//				
//				for (OntoClassInfo clazz : hierarchy2OntClassMap.values()) {
//					Concept conceptInML = new Concept(conceptInML_name);
//					double score = this.getConcept2ClassMappingScore(conceptInML, clazz);
//					score = (conceptsSimilarity + score) / 2;
//					clazz.setSimilarityToConcept(score);
//					System.out.println("From MLR2: <" + concept.getConceptName() + "> --> <" + conceptInML_name + "> --> <" + clazz.getOntClassName() + "> with " + score);
//					Concept2OntClassMapping concept2ClassMappingPair = new Concept2OntClassMapping(concept, clazz, score);
//					concept2ClassMappingPair.setDirectMapping(false); // this mapping is indirect: concept -> conceptInML -> ontoClass
//					concept2OntClassMappingPairs.add(concept2ClassMappingPair);
//				}
//			}
//		}
//		return concept2OntClassMappingPairs;
//	}

	private double getConcept2ClassMappingApplyScore(Concept concept, OntoClassInfo ontClass){
		double score = 0.0;
		try {
			IConcept2OntClassMappingStatistics mappingStatistics = MLRepository.getConcept2ClassMappingStatistics(concept, ontClass);
			int failedCounts = mappingStatistics.getFailedCounts();
			int succeedCounts = mappingStatistics.getSucceedCounts();
			int unSettledCounts = mappingStatistics.getUndeterminedCounts();
//			double similarity = mappingStatistics.getSimilarity();

			int totalCounts = failedCounts + succeedCounts + unSettledCounts;
			if (totalCounts == 0) {
				/*
				 * if failedCounts, succeedCounts and unSetteledCounts are all
				 * Zero, then this mapping is brand-new. Therefore, just returning
				 * the similarity score;
				 */
				return score;
			}
			
			double succeedWeight = 1.0;
			double unSettltedWeight = 0.5;
			double weightedTotalCounts = succeedWeight * succeedCounts + unSettltedWeight * unSettledCounts;
			score = weightedTotalCounts / totalCounts; 

		} catch (NoSuchEntryItemException e) {
			return score;
		}
		return score;
	}
	
	private double getSimilarity(Concept concept, OntoClassInfo ontClass) {
		IConcept2OntClassMappingStatistics mappingStatistics;
		double similarity = 0.0;
		try {
			mappingStatistics = MLRepository.getConcept2ClassMappingStatistics(concept, ontClass);
			similarity = mappingStatistics.getSimilarity();
		} catch (NoSuchEntryItemException e) {
			similarity = 0.0;
		}
		return similarity;
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
			double unSettltedWeight = 0.0;
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
			System.out.println("here: " + score);
			return score;
		}
		return score;
	}
}