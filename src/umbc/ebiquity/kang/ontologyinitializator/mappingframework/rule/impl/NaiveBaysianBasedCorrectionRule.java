package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionClusterFeatureWrapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.InstanceMembershipInferenceFact;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;

public class NaiveBaysianBasedCorrectionRule implements ICorrectionRule {
	
	private Collection<CorrectionClusterFeatureWrapper> _correctionFeatureWrappers;
	
	// Factory Method
	public static ICorrectionRule createInstance(Collection<CorrectionClusterFeatureWrapper> correctionRules){
		return new NaiveBaysianBasedCorrectionRule(correctionRules);
	}
	
	private NaiveBaysianBasedCorrectionRule(Collection<CorrectionClusterFeatureWrapper> correctionFeatureWrappers) {
		this._correctionFeatureWrappers = correctionFeatureWrappers;
	}
	
	@Override
	public String getTargetClass(IClassifiedInstanceDetailRecord instance, String originalClass) {
		if(_correctionFeatureWrappers.size() == 0) return instance.getOntoClassName();
		
		String instanceName = instance.getInstanceLabel();
		Set<LexicalFeature> unionOfAllLexicalFeature = new HashSet<LexicalFeature>();
		Set<IInstanceMembershipInfereceFact> unionOfAllPositiveMappingFeature = new HashSet<IInstanceMembershipInfereceFact>();
		Set<IInstanceMembershipInfereceFact> unionOfAllNegativeMappingFeature = new HashSet<IInstanceMembershipInfereceFact>();
		Map<String, Double> ontClass2Support = new HashMap<String, Double>();
		Map<String, Integer> ontClass2MatchedLexicalFeatureCount = new HashMap<String, Integer>();
		Map<String, Integer> ontClass2MatchedPositiveMappingFeatureCount = new HashMap<String, Integer>();
		Map<String, Integer> ontClass2MatchedNegativeMappingFeatureCount = new HashMap<String, Integer>();
		
		Set<String> computedTargetClass = new HashSet<String>();
		for (CorrectionClusterFeatureWrapper featureWrapper : _correctionFeatureWrappers) {
			String targetClassName = featureWrapper.getCorrectionTargetClass();
			if (computedTargetClass.contains(targetClassName)) continue;

			computedTargetClass.add(targetClassName);
			
			// works as the prior probability
			double instanceRatioOfTargetClass = featureWrapper.getInstanceRatioOfTargetClass();
			
			Map<LexicalFeature, Double> lexicalFeature = featureWrapper.getTargetClassInstanceLexicalFeatures();
			Map<IInstanceMembershipInfereceFact, Double> positiveMappingFeature = featureWrapper.getPositiveMappingSetsWithLocalRateToCorrectionTargetClass();
			Map<IInstanceMembershipInfereceFact, Double> negativeMappingFeature = featureWrapper.getNegativeMappingSetsWithLocalRateToCorrectionTargetClass();
			
			
			/////// JUST FOR TESTING
			System.out.println("COMPUTE PROBABILITY FOR CLASS: <" + targetClassName + ">");
			System.out.println("Ratio of Instance: " + instanceRatioOfTargetClass);
			printLexicalFeatures(lexicalFeature, "Lexical Features of " + targetClassName);
//			printMappingFeatures(positiveMappingFeature, "Positive Features of " + targetClassName);
//			printMappingFeatures(negativeMappingFeature, "Negative Features of " + targetClassName);
			///////
			
			
			System.out.println("--- Check Matched Lexical Features");
			double support = instanceRatioOfTargetClass;
			support = this.computeLexicalFeaturesScore(instanceName, lexicalFeature, targetClassName, ontClass2MatchedLexicalFeatureCount);
			System.out.println("--- Check Matched Positive Mapping Features");
			support *= this.computeMappingFeaturesScore(instance, positiveMappingFeature, targetClassName, ontClass2MatchedPositiveMappingFeatureCount);
			System.out.println("--- Check Matched Negative Mapping Features");
			support *= this.computeMappingFeaturesScore(instance, negativeMappingFeature, targetClassName, ontClass2MatchedNegativeMappingFeatureCount);
			
			unionOfAllLexicalFeature.addAll(lexicalFeature.keySet());
			unionOfAllPositiveMappingFeature.addAll(positiveMappingFeature.keySet());
			unionOfAllNegativeMappingFeature.addAll(negativeMappingFeature.keySet());
			ontClass2Support.put(targetClassName, support);
		}
		
		int numOfLexicalFeature = unionOfAllLexicalFeature.size();
		int numOfPositiveMappingFeature = unionOfAllPositiveMappingFeature.size();
		int numOfNegativeMappingFeature = unionOfAllNegativeMappingFeature.size();
		
		
		////////// JUST FOR TESTING
		printLexicalFeatures(unionOfAllLexicalFeature, "Lexical Features of All");
		System.out.println("Num of All LF: " + numOfLexicalFeature);
		printAllMappingFeatures(unionOfAllPositiveMappingFeature, "Positive Features of All");
		System.out.println("Num of All PMF: " + numOfPositiveMappingFeature);
		printAllMappingFeatures(unionOfAllNegativeMappingFeature, "Negative Features of All");
		System.out.println("Num of All NMF: " + numOfNegativeMappingFeature);
		//////////
		
		
		for(String className : ontClass2Support.keySet()){
			double support = ontClass2Support.get(className);
			int matchedLexicalFeatureCount = ontClass2MatchedLexicalFeatureCount.get(className);
			int matchedPositiveMappingFeatureCount = ontClass2MatchedPositiveMappingFeatureCount.get(className);
			int matchedNegativeMappingFeatureCount = ontClass2MatchedNegativeMappingFeatureCount.get(className);
			int difference1 = numOfLexicalFeature - matchedLexicalFeatureCount;
			int difference2 = numOfPositiveMappingFeature - matchedPositiveMappingFeatureCount;
			int difference3 = numOfNegativeMappingFeature - matchedNegativeMappingFeatureCount;
			System.out.println("Class: <" + className + ">");
			System.out.println("-- L:" + matchedLexicalFeatureCount +",  P:" + matchedPositiveMappingFeatureCount + ",  N:" + matchedNegativeMappingFeatureCount);
			System.out.println("-- DL:" + difference1 +",  DP:" + difference2 + ",  DN:" + difference3);
			support = Math.log(support * Math.pow(1 / (double) numOfLexicalFeature, difference1)
					* Math.pow(1 / (double) numOfPositiveMappingFeature, difference2)
					* Math.pow(1 / (double) numOfNegativeMappingFeature, difference3));
			
			
			ontClass2Support.put(className, support);
			
			System.out.println("-- Support:" + support);
		}
		return getClassWithHighestScore(ontClass2Support, originalClass);
	}
	
	
	
	
	
	private void printAllMappingFeatures(Set<IInstanceMembershipInfereceFact> unionOfAllPositiveMappingFeature, String string) {
		
	}

	private void printLexicalFeatures(Set<LexicalFeature> unionOfAllLexicalFeature, String label) {
		
	}

	//////////
	private void printLexicalFeatures(Map<LexicalFeature, Double> lexicalFeatures, String label) {

		double total = 0.0;
		for (LexicalFeature f : lexicalFeatures.keySet()) {
			double representativeness = lexicalFeatures.get(f);
			total += representativeness;

			if (f == null) {
				System.out.println(label + ": default1 " + representativeness);
			} else {
				System.out.println(label + ": " + f.getLabel() + ",  " + f.getSupport() + ",  " + representativeness);
			}
		}

		System.out.println("total: " + total);
	}

	private void printMappingFeatures(Map<IInstanceMembershipInfereceFact, Double> mappingFeatures, String label) {
		for (IInstanceMembershipInfereceFact f : mappingFeatures.keySet()) {
			double representativeness = mappingFeatures.get(f);
			if (f == null) {
				System.out.println(label + ": default2 " + representativeness);
			} else {
				System.out.println(label + ": " + f.getMembershipInferenceFactCode() + ",  " + representativeness);
			}
		}
	}
	//////////
	
	
	
	private String getClassWithHighestScore(Map<String, Double> map, String originalClass) {

		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(map.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		int size = list.size();
		
		// If the list of classes have the same score, return the original class.
		if (list.get(0).getValue().compareTo(list.get(size - 1).getValue()) == 0) {
			return originalClass;
		}
		return list.get(0).getKey();
	}

	private double computeLexicalFeaturesScore(String instanceLabel, Map<LexicalFeature, Double> lexicalFeatures, String targetClassName, Map<String, Integer> correction2LexicalFeatureCount){

		int matchedFeatureCount = 0;
		double totalSupport = 1.0;
		String lowerCaseInstanceLabel = instanceLabel.toLowerCase();
		for (LexicalFeature feature : lexicalFeatures.keySet()) {
			if (feature == null)
				continue;
			String lowerCaseSubStringLabel = feature.getLabel().toLowerCase();
			if (LexicalFeature.Position.BEGIN == feature.getFeaturePosition()) {
				if (lowerCaseInstanceLabel.startsWith(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED BEGIN: <" + feature.getLabel() + ">, " + feature.getCount() + ", " + feature.getSignificant()+", " + feature.getSupport());
					totalSupport*= lexicalFeatures.get(feature);
					matchedFeatureCount++;
				}
			} else if (LexicalFeature.Position.END == feature.getFeaturePosition()) {
				if (lowerCaseInstanceLabel.endsWith(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED END: <" + feature.getLabel() + ">, "+ feature.getCount() + ", " + feature.getSignificant()+", " + feature.getSupport());
					totalSupport*= lexicalFeatures.get(feature);
					matchedFeatureCount++;
				}
			} else if (LexicalFeature.Position.ANY == feature.getFeaturePosition()){
				if (lowerCaseInstanceLabel.contains(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED ANY: <" + feature.getLabel() + ">, "+ feature.getCount() + ", " + feature.getSignificant()+", " + feature.getSupport());
					totalSupport*= lexicalFeatures.get(feature);
					matchedFeatureCount++;
				}
			} else {
				
			}
		}

		correction2LexicalFeatureCount.put(targetClassName, matchedFeatureCount);
		return totalSupport;
	}

	private double computeMappingFeaturesScore(
			IClassifiedInstanceDetailRecord instance,
			Map<IInstanceMembershipInfereceFact, Double> mappingSets, String targetClassName,
			Map<String, Integer> ontClass2MatchedMappingFeatureCount
	){

		ontClass2MatchedMappingFeatureCount.put(targetClassName, 0);
		double totalScore = 1.0;
		List<IConcept2OntClassMapping> mappingList = instance.getConcept2OntClassMappingPairs();
		
//		for (IConcept2OntClassMapping mapping : mappingList) {
//			System.out.println("*** " + mapping.getMappingCode());
//		}
		
		int sizeOfList = mappingList.size();
		for (int i = 0; i < sizeOfList; i++) {
			IConcept2OntClassMapping mapping1 = mappingList.get(i);
			if (!mapping1.isMappedConcept()) continue;
			
			Set<IConcept2OntClassMapping> mappingSet1 = new HashSet<IConcept2OntClassMapping>();
			mappingSet1.add(mapping1);
//			System.out.println("1** " + mappingSet1);
			totalScore *= getMappingSetStrength(mappingSet1, mappingSets, targetClassName, ontClass2MatchedMappingFeatureCount);

			for (int j = i+1; j < sizeOfList; j++) {
				IConcept2OntClassMapping mapping2 = mappingList.get(j);
				if (!mapping2.isMappedConcept() || mapping2.equals(mapping1)) continue;
				
				Set<IConcept2OntClassMapping> mappingSet2 = new HashSet<IConcept2OntClassMapping>();
				mappingSet2.add(mapping1);
				mappingSet2.add(mapping2);
//				System.out.println("2** " + mappingSet2);
				totalScore *= getMappingSetStrength(mappingSet2, mappingSets, targetClassName, ontClass2MatchedMappingFeatureCount);

			}

		}
		return totalScore;
	}

	private double getMappingSetStrength(
			Set<IConcept2OntClassMapping> mappingSet,
			Map<IInstanceMembershipInfereceFact, Double> mappingSet2Strength, 
			String targetClassName,
			Map<String, Integer> ontClass2MatchedMappingFeatureCount
	) {

		double score = 1.0;
		IInstanceMembershipInfereceFact ms = InstanceMembershipInferenceFact.createInstance(mappingSet, targetClassName);
		if (mappingSet2Strength.containsKey(ms)) {
			score = mappingSet2Strength.get(ms);
			int count = ontClass2MatchedMappingFeatureCount.get(targetClassName);
			count++;
			ontClass2MatchedMappingFeatureCount.put(targetClassName, count);
			System.out.println("  MATCHED MAPPING FEATURE: <" + ms.getMembershipInferenceFactCode() + "> with Score: " + score);
		}
		return score;
	}
}
