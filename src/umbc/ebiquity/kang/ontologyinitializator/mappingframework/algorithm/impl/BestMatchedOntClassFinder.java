package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.icu.util.StringTokenizer;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm.SimilarityType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IBestMatchedOntClassFinder;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassHierarchy;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class BestMatchedOntClassFinder implements IBestMatchedOntClassFinder {
	
	private IOntologyRepository OntologyRepository;
	private SimilarityAlgorithm similarityAlg = new SimilarityAlgorithm();
	/**
	 * threshold for legitimate matched onto-classes from identified class
	 * hierarchy in domain ontology
	 */
	private double thresholdOfSecondLevelRecommendedClass = 0.65;
	
	public BestMatchedOntClassFinder(IOntologyRepository OntRepository){
		this.OntologyRepository = OntRepository;
	}

	public MatchedOntoClassInfo findBestMatchedOntoClass(String instanceLable, 
														 Collection<Concept2OntClassMapping> concept2OntClassMappingPairs,
														 IClassificationCorrectionRepository aggregratedClassificationCorrectionRepository) {
		
		/*
		 * TODO
		 */
		Collection<OntoClassHierarchy> identifiedOntoClassHierarchies = this.groupOntoClassHierarchiesForMappedOntoClasses(concept2OntClassMappingPairs);
		Map<OntoClassHierarchy, Double> classHierarchyClosenessMap = this.calculateClosenessToOntoClassHierarchies(identifiedOntoClassHierarchies);
		List<OntoClassHierarchy> candidateBestMatchedClassHierarchies = this.identifyBestOntoClassHierarchies(classHierarchyClosenessMap);
		
		/*
		 * identify candidates of "best" onto-class hierarchies based on matched onto-classes identified previously
		 */
//		List<OntoClassHierarchy> candidateOntoClassHierarchies = this.identifyCandidatesOfOntoClassHierarchies(identifiedOntoClassHierarchies);
		
		/*
		 * identify the "best" onto-class hierarchy from the candidate hierarchies
		 */
		int bestOntoClassHierarchyIndex = this.singleOutBestOntoClassHierarchy(instanceLable, candidateBestMatchedClassHierarchies);
		
		MatchedOntoClassInfo matchedOntClassInfo = null;
		if (bestOntoClassHierarchyIndex != -1) {
			OntoClassHierarchy bestMatchedClassHierarchy = candidateBestMatchedClassHierarchies.get(bestOntoClassHierarchyIndex);
			System.out.println("Onto-classes from the identified hierarchy:" + bestMatchedClassHierarchy.getClassHierarchyNumber());
			Collection<String> pathCodes = new ArrayList<String>();
			for (OntoClassInfo member : bestMatchedClassHierarchy.getMemebers()) {
		        System.out.println("    has member: " + member.getOntClassName());
				String pathCodeStr = OntologyRepository.getLocalPathCode(member);
				pathCodes.add(pathCodeStr);
			}

			OntoClassInfo bestMatchedOntoClassInfo = null;
			if (pathCodes.size() != 0) {
				
				bestMatchedOntoClassInfo = this.identifyBestMatchedOntoClass(pathCodes, bestMatchedClassHierarchy.getClassHierarchyNumber());
				/*
				 * 
				 */
				Collection<OntoClassInfo> firstLevelRecommendedOntoClasses = this.getFirstLevelRecommendedOntoClasses(candidateBestMatchedClassHierarchies, bestMatchedOntoClassInfo);
				/*
				 * 
				 */
				Collection<OntoClassInfo> secondLevelRecommendedOntoClasses = this.getSecondLevelRecommendedOntoClasses(identifiedOntoClassHierarchies, candidateBestMatchedClassHierarchies);
				
				/*
				 *  identify the "best" onto-class from the identified hierarchies
				 */
				matchedOntClassInfo = new MatchedOntoClassInfo();
				matchedOntClassInfo.setMatchedOntoClassInfo(bestMatchedOntoClassInfo);
				matchedOntClassInfo.setSimilarity(bestMatchedClassHierarchy.getSimilarity());
				matchedOntClassInfo.setClassHierarchyNumber(bestMatchedClassHierarchy.getClassHierarchyNumber());
				matchedOntClassInfo.setFirstLevelRecommendedOntoClasses(firstLevelRecommendedOntoClasses);
				matchedOntClassInfo.setSecondLevelRecommendedOntoClasses(secondLevelRecommendedOntoClasses);
				matchedOntClassInfo.recordClosenessScoresForClassHierarchy(classHierarchyClosenessMap);
//				matchedOntClassInfo.setRelatedConcepts(relatedConcepts);
				System.out.println("The best Onto-class is: [" + bestMatchedOntoClassInfo.getOntClassName() + "]");
			}
			
		} else {
			
			matchedOntClassInfo = new MatchedOntoClassInfo();
			OntoClassInfo ontoClassInfo = OntologyRepository.getLightWeightOntClassByName("Any");
			matchedOntClassInfo.setMatchedOntoClassInfo(ontoClassInfo);
			matchedOntClassInfo.setSimilarity(1.0);
			matchedOntClassInfo.setClassHierarchyNumber(OntologyRepository.getOntClassHierarchyNumber(ontoClassInfo));
			
			System.out.println("No onto-class hierarchy identified, Therefore give <Any>");
		}
		
		System.out.println("------------------------------------------");
		return matchedOntClassInfo;
	}
	
//	/**
//	 * get class hierarchies of classes in the concept-class mappings and group
//	 * these class hierarchies based on hierarchical number
//	 * 
//	 * @param concept2OntClassMappingPairs
//	 * @param aggregratedClassificationCorrectionRepository 
//	 * @return a collection of class hierarchies
//	 */
//	private Collection<OntoClassHierarchy> groupOntoClassHierarchiesForMappedOntoClasses(Collection<Concept2OntClassMapping> concept2OntClassMappingPairs,
//			       																		 IClassificationCorrectionRepository aggregratedClassificationCorrectionRepository) {
//		
//		/*
//		 * record onto-class hierarchies for candidate onto-classes
//		 */
//		Map<String, OntoClassHierarchy> hierarchyNumber2OntoClassHierarchyMap = new HashMap<String, OntoClassHierarchy>();
//		for (Concept2OntClassMapping concept2OntClassMappingPair : concept2OntClassMappingPairs) {
//				OntoClassInfo ontoClassInfo = concept2OntClassMappingPair.getMappedOntoClass();
//				
//			Collection<String> ontClassList = aggregratedClassificationCorrectionRepository.getTargetClasses(concept2OntClassMappingPair);
//			if (!ontClassList.contains(ontoClassInfo.getOntClassName())) {
//				
//				double rate = aggregratedClassificationCorrectionRepository.getC2CMappingRateInOntClass(concept2OntClassMappingPair, ontoClassInfo.getOntClassName());
//				ontoClassInfo.setSimilarityToConcept(concept2OntClassMappingPair.getMappingScore());
//				int hierarchyNumber = OntologyRepository.getOntClassHierarchyNumber(ontoClassInfo);
//				ontoClassInfo.setHierarchyNumber(hierarchyNumber);
//				String hierarchyNumberStr = String.valueOf(hierarchyNumber);
//				System.out.println("Onto-Class:" + ontoClassInfo.getOntClassName() + ";  Class Hierarchy Num:" + hierarchyNumber + ";  Similarity: " + String.valueOf(ontoClassInfo.getSimilarityToConcept()));
//				if (hierarchyNumber2OntoClassHierarchyMap.containsKey(hierarchyNumberStr)) {
//					OntoClassHierarchy hierarchy = hierarchyNumber2OntoClassHierarchyMap.get(hierarchyNumberStr);
//					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair, ontoClassInfo, rate);
//				} else {
//					OntoClassHierarchy hierarchy = new OntoClassHierarchy(hierarchyNumber);
//					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair, ontoClassInfo, rate);
//					hierarchyNumber2OntoClassHierarchyMap.put(hierarchyNumberStr, hierarchy);
//				}
//			}
//
//			for (String ontClassName : ontClassList) {
//				double rate = aggregratedClassificationCorrectionRepository.getC2CMappingRateInOntClass(concept2OntClassMappingPair, ontClassName);
//				OntoClassInfo ontClass = OntologyRepository.getLightWeightOntClassByName(ontClassName);
//				ontClass.setSimilarityToConcept(concept2OntClassMappingPair.getMappingScore());
//				int hierarchyNumber = OntologyRepository.getOntClassHierarchyNumber(ontClass);
//				ontClass.setHierarchyNumber(hierarchyNumber);
//				String hierarchyNumberStr = String.valueOf(hierarchyNumber);
//				System.out.println("Onto-Class:" + ontClass.getOntClassName() + ";  Class Hierarchy Num:" + hierarchyNumber + ";  Similarity: " + String.valueOf(ontoClassInfo.getSimilarityToConcept()));
//				if (hierarchyNumber2OntoClassHierarchyMap.containsKey(hierarchyNumberStr)) {
//					OntoClassHierarchy hierarchy = hierarchyNumber2OntoClassHierarchyMap.get(hierarchyNumberStr);
//					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair, ontClass, rate);
//				} else {
//					OntoClassHierarchy hierarchy = new OntoClassHierarchy(hierarchyNumber);
//					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair, ontClass, rate);
//					hierarchyNumber2OntoClassHierarchyMap.put(hierarchyNumberStr, hierarchy);
//				}
//			}
//		}
//		return hierarchyNumber2OntoClassHierarchyMap.values();
//	}
	
	/**
	 * get class hierarchies of classes in the concept-class mappings and group
	 * these class hierarchies based on hierarchical number
	 * 
	 * @param concept2OntClassMappingPairs
	 * @return a collection of class hierarchies
	 */
	private Collection<OntoClassHierarchy> groupOntoClassHierarchiesForMappedOntoClasses(Collection<Concept2OntClassMapping> concept2OntClassMappingPairs) {
		
		/*
		 * record onto-class hierarchies for candidate onto-classes
		 */
		Map<String, OntoClassHierarchy> hierarchyNumber2OntoClassHierarchyMap = new HashMap<String, OntoClassHierarchy>();
		for (Concept2OntClassMapping concept2OntClassMappingPair : concept2OntClassMappingPairs) {
				OntoClassInfo ontoClassInfo = concept2OntClassMappingPair.getMappedOntoClass();
				
				/*
				 * TODO: 
				 */
				ontoClassInfo.setSimilarityToConcept(concept2OntClassMappingPair.getMappingScore());
				
				int hierarchyNumber = OntologyRepository.getOntClassHierarchyNumber(ontoClassInfo);
				ontoClassInfo.setHierarchyNumber(hierarchyNumber);
				String hierarchyNumberStr = String.valueOf(hierarchyNumber);
				System.out.println("Onto-Class:" + ontoClassInfo.getOntClassName() + ";  Class Hierarchy Num:" + hierarchyNumber + ";  Similarity: " + String.valueOf(ontoClassInfo.getSimilarityToConcept()));
				if (hierarchyNumber2OntoClassHierarchyMap.containsKey(hierarchyNumberStr)) {
					OntoClassHierarchy hierarchy = hierarchyNumber2OntoClassHierarchyMap.get(hierarchyNumberStr);
					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair);
				} else {
					OntoClassHierarchy hierarchy = new OntoClassHierarchy(hierarchyNumber);
					hierarchy.addMatchedOntoClass2ConceptPair(concept2OntClassMappingPair);
					hierarchyNumber2OntoClassHierarchyMap.put(hierarchyNumberStr, hierarchy);
				}
		}
		return hierarchyNumber2OntoClassHierarchyMap.values();
	}

	/***
	 * Identify the best matched class hierarchy of the instance (subject of a
	 * triple) that is currently comparing.
	 * 
	 * @param ontoClassHierarchies
	 * @return
	 */
	private Map<OntoClassHierarchy, Double> calculateClosenessToOntoClassHierarchies(Collection<OntoClassHierarchy> ontoClassHierarchies) {
		Map<OntoClassHierarchy, Double> classHierarchyClosenessMap = new HashMap<OntoClassHierarchy, Double>();
		double totalWeights = 0.0;
		for (OntoClassHierarchy hierarchy : ontoClassHierarchies) {
			totalWeights += hierarchy.getWeightsSum();
		}

		for (OntoClassHierarchy hierarchy : ontoClassHierarchies) {

			System.out.println("Hierarchy Number: " + hierarchy.getClassHierarchyNumber());
			//
			double numberOfMembers = hierarchy.getNumberOfMembers();
			System.out.println("      - Number of Member: " + numberOfMembers);
			List<Double> similarities = hierarchy.getMemeberSimilarities();
			List<Double> weights = hierarchy.getMemberWeights();
			int size = similarities.size();
			double totalScore = 0.0;
			for(int index = 0; index<size;index++){
				totalScore += similarities.get(index) * weights.get(index) / totalWeights;
			}
			System.out.println("      - Overall Similarity: " + totalScore);
			classHierarchyClosenessMap.put(hierarchy, totalScore);
		}
		return classHierarchyClosenessMap;
	}
	
	private List<OntoClassHierarchy> identifyBestOntoClassHierarchies(Map<OntoClassHierarchy, Double> classHierarchyClosenessMap) {
		
		Map<Double, List<OntoClassHierarchy>> identifiedHierarchies = new HashMap<Double, List<OntoClassHierarchy>>();
		double maxHierarchySimilarity = 0.0;
		for(OntoClassHierarchy hierarchy : classHierarchyClosenessMap.keySet()){
			double overallSim = classHierarchyClosenessMap.get(hierarchy);
			/*
			 * 
			 */
			List<OntoClassHierarchy> hierarchies = null;
			if (identifiedHierarchies.containsKey(overallSim)) {
				hierarchies = identifiedHierarchies.get(overallSim);
			} else {
				hierarchies = new ArrayList<OntoClassHierarchy>();
				identifiedHierarchies.put(overallSim, hierarchies);
			}
			hierarchy.setSimilarity(this.roundSimilarity(overallSim));
			hierarchies.add(hierarchy);
			
			/*
			 * 
			 */
			if (overallSim > maxHierarchySimilarity) {
				maxHierarchySimilarity = overallSim;
			}
		}
		List<OntoClassHierarchy> hierarchies =  identifiedHierarchies.get(maxHierarchySimilarity);
		if (hierarchies == null) {
			return null;
		} else {
			Collections.sort(hierarchies);
			return hierarchies;
		}
	}
	
	/**
	 * 
	 * @param instanceLabel
	 * @param bestMatchedHierarchies
	 * @return
	 */
	private int singleOutBestOntoClassHierarchy(String instanceLabel, List<OntoClassHierarchy> bestMatchedHierarchies) {

		if (bestMatchedHierarchies == null || bestMatchedHierarchies.size() == 0) {
			return -1;
		}

		if (bestMatchedHierarchies.size() == 1) {
			return 0;
		}
		
		Collections.sort(bestMatchedHierarchies); 
		int bestHierarchyIndex = 0;
		double maxSim = 0.0;
		for (int index = 0; index < bestMatchedHierarchies.size(); index++) {
			double sim1 = 0.0;
			double sim2 = 0.0;
			int count1 = 0;
			int count2 = 0;
			for(IConcept2OntClassMapping map : bestMatchedHierarchies.get(index).getMatchedConcept2OntoClassPairs()){
				String conceptName = map.getConceptName();
				String mappedOntClassName = map.getMappedOntoClassName();
				sim1 += similarityAlg.getSimilarity(SimilarityType.Kim_Ngram, conceptName, mappedOntClassName);
				count1 ++;
			}
			sim1 = sim1 / count1;
			for (OntoClassInfo ontoClassInfo : bestMatchedHierarchies.get(index).getMemebers()) {
				sim2 += similarityAlg.getSimilarity(SimilarityType.Kim_Ngram, ontoClassInfo.getOntClassName(), instanceLabel);
				count2 ++;
			}
			sim2 = sim2 / count2;
			double sim = sim1 + sim2;
			if (sim >= maxSim) {
				maxSim = sim;
				bestHierarchyIndex = index;
			}

		}
		return bestHierarchyIndex;
	}
	

	/**
	 * Compute the first level recommended onto-classes. These onto-classes are
	 * from the best matched onto-class hierarchies. 
	 * 
	 * @param identifiedHierarchies
	 * @param bestOntoClass
	 */
	private Collection<OntoClassInfo> getFirstLevelRecommendedOntoClasses(List<OntoClassHierarchy> candidateOntoClassHierarchies,
			                                                              OntoClassInfo bestOntoClass) {

		Collection<OntoClassInfo> recommendedOntoClasses = new HashSet<OntoClassInfo>();
		for (int index = 0; index < candidateOntoClassHierarchies.size(); index++) {
			for (OntoClassInfo ontoClassInfo : candidateOntoClassHierarchies.get(index).getMemebers()) {
				
				/*
				 * 
				 */
				if (bestOntoClass != null && !ontoClassInfo.getOntClassName().equals(bestOntoClass.getOntClassName())) {
					recommendedOntoClasses.add(ontoClassInfo);
					System.out.println("> First Level Recommendation: " + ontoClassInfo.getOntClassName() + " : " + ontoClassInfo.getSimilarityToConcept());
				}
			}
		}
		return recommendedOntoClasses;
	}

	/**
	 * compute the second level recommended onto-classes. These onto-classes are
	 * from the identified onto-class hierarchies but not from the best matched
	 * onto-class hierarchies.
	 */
	private Collection<OntoClassInfo> getSecondLevelRecommendedOntoClasses(Collection<OntoClassHierarchy> identifiedOntoClassHierarchies,
			                                                               List<OntoClassHierarchy> candidateBestOntoClassHierarchies) {
		
		Collection<OntoClassInfo> recommendedOntoClasses = new HashSet<OntoClassInfo>();
		Set<OntoClassInfo> blacklist = new HashSet<OntoClassInfo>();
		for(OntoClassHierarchy hierarchy: candidateBestOntoClassHierarchies){
			for(OntoClassInfo ontoClassInfo: hierarchy.getMemebers()){
				blacklist.add(ontoClassInfo);
			}
		}
		
		for(OntoClassHierarchy hierarchy: identifiedOntoClassHierarchies){
			for(OntoClassInfo ontoClassInfo: hierarchy.getMemebers()){
				if(!blacklist.contains(ontoClassInfo) && ontoClassInfo.getSimilarityToConcept() >= this.thresholdOfSecondLevelRecommendedClass){
					recommendedOntoClasses.add(ontoClassInfo);
					System.out.println("> Second Recommendation: " + ontoClassInfo.getOntClassName() + " : " + ontoClassInfo.getSimilarityToConcept() );
				}
			}
		}
		return recommendedOntoClasses;
	}

	/**
	 * Identify the best matched class (the most specific reconciling class)
	 * from a set of classes in the same hierarchy. Here each class is
	 * represented by its path code.
	 * 
	 * @param pathCodes
	 *            - a collection of path codes representing classes in the same hierarchy
	 * @param classHierarchyNumber
	 *            - hierarchy number of the hierarchy
	 * @return an instance of OntClassInfo - the best matched class
	 */
	private OntoClassInfo identifyBestMatchedOntoClass(Collection<String> pathCodes, int classHierarchyNumber) {

		/*
		 * if only has one matched ontology class
		 */
		int numberOfPathCodes = pathCodes.size();
		if (numberOfPathCodes == 1) {}
		
		/*
		 * if has more than one matched ontology class
		 */
		int maxPathCodeLength = 0;
		List<String[]> pathCodeArrayList = new ArrayList<String[]>();
		for (String pathCode : pathCodes) {
			System.out.println();
			System.out.println("Path Code: " + pathCode);
			String[] pathCodeArray = this.parsePathCodeString(pathCode);
			
			for(String code : pathCodeArray){
				System.out.print(code + " ");
			}
			pathCodeArrayList.add(pathCodeArray);
			if(pathCodeArray.length > maxPathCodeLength){
				maxPathCodeLength = pathCodeArray.length;
			}
		}
		
		String identifiedOntClassPathCode = "";
		for (int hierarchyLevelpointer = 0; hierarchyLevelpointer < maxPathCodeLength; hierarchyLevelpointer++) {
			
			String firstCode = "";
			int currentPathCode = 0;
			for (; currentPathCode < numberOfPathCodes; currentPathCode++) {
				String[] pathCodeArray = pathCodeArrayList.get(currentPathCode);
				if (pathCodeArray.length > hierarchyLevelpointer) {
					firstCode = pathCodeArray[hierarchyLevelpointer];
					break;
				}
			}
			
			boolean pathForkHited = false;
			for (; currentPathCode < numberOfPathCodes; currentPathCode++) {
				String[] pathCodeArray = pathCodeArrayList.get(currentPathCode);
				if (pathCodeArray.length > hierarchyLevelpointer) {
					String code = pathCodeArray[hierarchyLevelpointer];
					if(!code.equals(firstCode)){
						pathForkHited = true;
						break;
					}
				}
			}
			
			if(pathForkHited){
				break;
			} else {
				identifiedOntClassPathCode += "-" + firstCode;
			}
		}

		identifiedOntClassPathCode = classHierarchyNumber + "-" + identifiedOntClassPathCode.substring(1);
		System.out.println();
		System.out.println(identifiedOntClassPathCode);
		OntoClassInfo ontClassInfo = OntologyRepository.getOntClassByGlobalCode(identifiedOntClassPathCode);
		System.out.println(ontClassInfo.getURI());
		return ontClassInfo;
	}

	private String[] parsePathCodeString(String pathCodeString) {
		StringTokenizer textTokenizer = new StringTokenizer(pathCodeString, "-");
		String[] tokens = new String[textTokenizer.countTokens()];
		for (int i = 0; textTokenizer.hasMoreTokens(); i++) {
			String token = textTokenizer.nextToken();
			tokens[i] = token;
		}
		return tokens;
	}

	private double roundSimilarity(double similarity) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.format(similarity);
		return Double.valueOf(df.format(similarity));
	}

}
