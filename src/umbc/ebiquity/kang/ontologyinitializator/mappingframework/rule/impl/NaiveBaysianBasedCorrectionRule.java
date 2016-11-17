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
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.InstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class NaiveBaysianBasedCorrectionRule implements ICorrectionRule {
	
	private Collection<CorrectionClusterFeatureWrapper> _correctionFeatureWrappers;
	
	// Factory Method
	public static ICorrectionRule createInstance(Collection<CorrectionClusterFeatureWrapper> correctionRules){
		return new NaiveBaysianBasedCorrectionRule(correctionRules);
	}
	
	private NaiveBaysianBasedCorrectionRule(Collection<CorrectionClusterFeatureWrapper> correctionFeatureWrappers) {
		this._correctionFeatureWrappers = correctionFeatureWrappers;
	}
	
	public void showDetail(){
		System.out.println();
		for(CorrectionClusterFeatureWrapper wrapper : _correctionFeatureWrappers){
			wrapper.showCorrectionDetails();
			System.out.println();
		}
	}
	
	@Override
	public String obtainCorrectedClassLabel(IClassifiedInstanceDetailRecord instance, String originalClass) {
		String sourceOntClass = instance.getOntoClassName().trim();
		if(_correctionFeatureWrappers.size() == 0) return sourceOntClass;
		System.out.println("");;
		System.out.println("### Identify Class Label for " + instance.getInstanceLabel());
		String instanceName = instance.getInstanceLabel();
		Set<LexicalFeature> unionOfAllLexicalFeature = new HashSet<LexicalFeature>();
//		Set<IInstanceClassificationEvidence> unionOfAllPositiveMappingFeature = new HashSet<IInstanceClassificationEvidence>();
//		Set<IInstanceClassificationEvidence> unionOfAllNegativeMappingFeature = new HashSet<IInstanceClassificationEvidence>();
		Map<String, Double> ontClass2Support = new HashMap<String, Double>();
		Map<String, Double> ontClass2InstanceRate = new HashMap<String, Double>();
		Map<String, Integer> ontClass2MatchedLexicalFeatureCount = new HashMap<String, Integer>();
		Map<String, Integer> ontClass2MatchedPositiveMappingFeatureCount = new HashMap<String, Integer>();
		Map<String, Integer> ontClass2MatchedNegativeMappingFeatureCount = new HashMap<String, Integer>();
		Map<String, Integer> ontClass2MatchedC2CMappingFeatureCount = new HashMap<String, Integer>();
		
		int unionOfAllPositiveMappingFeature = 0;
		int unionOfAllNegativeMappingFeature = 0;
		int unionOfAllC2CMappingFeature = 0;
		int allLexicalFeature = 0;
		Set<String> computedTargetClass = new HashSet<String>();
		int totalInstanceNumber = 0;
		for (CorrectionClusterFeatureWrapper featureWrapper : _correctionFeatureWrappers) {
			String targetClassName = featureWrapper.getCorrectionTargetClass();
			if (computedTargetClass.contains(targetClassName)) continue;
			computedTargetClass.add(targetClassName);
			
			boolean sourceClassProbabilityComputing = false;
			if(targetClassName.equals(sourceOntClass)){
				sourceClassProbabilityComputing = true;
			}
			
			// works as the prior probability
			double instanceRatioOfTargetClass = featureWrapper.getInstanceRatioOfTargetClass();
			int numberOfInstanceOfTargetClass =  featureWrapper.getInstanceNumberOfTargetClass();
			totalInstanceNumber += numberOfInstanceOfTargetClass;
			
			Map<LexicalFeature, Double> lexicalFeature = featureWrapper.getTargetClassInstanceLexicalFeatures();
			Map<IInstanceClassificationEvidence, Double> positiveMappingFeature = featureWrapper.getPositiveMappingSetsWithLocalRateToCorrectionTargetClass();
			Map<IInstanceClassificationEvidence, Double> negativeMappingFeature = featureWrapper.getNegativeMappingSetsWithLocalRateToCorrectionTargetClass();
			Map<IConcept2OntClassMapping, Double> c2cMappingFeature = featureWrapper.getC2CMappingWithLocalRateToCorrectionTargetClass();
			
			int numberOfPositiveMappingOfTargetClass = featureWrapper.getNumberOfPositiveMappingOfTargetClass();
			int numberOfNegativeMappingOfTargetClass = featureWrapper.getNumberOfNegativeMappingOfTargetClass();
			int numberOfC2CMappingOfTargetClass = featureWrapper.getNumberOfC2CMappingOfTargetClass();
			
			/////// JUST FOR TESTING
			System.out.println();
			System.out.println("COMPUTE PROBABILITY FOR CLASS: <" + targetClassName + ">");
			System.out.println("Probability of Class - " + targetClassName +": "+ instanceRatioOfTargetClass);
			System.out.println("Number of instance of Class - " + targetClassName +": "+ numberOfInstanceOfTargetClass);
//			printLexicalFeatures(lexicalFeature, "Lexical Features of " + targetClassName);
			// printMappingFeatures(positiveMappingFeature,
			// "Positive Features of " + targetClassName);
			// printMappingFeatures(negativeMappingFeature,
			// "Negative Features of " + targetClassName);
			// /////
			
			System.out.println("--- Check Matched Lexical Features");
			double support = 0.0;
			support += this.computeLexicalFeaturesScore(instanceName, lexicalFeature, targetClassName, ontClass2MatchedLexicalFeatureCount);
			
//			System.out.println("--- Check Matched Positive Mapping Features");
//			support *= this.computeMappingFeaturesScore(instance, 
//														positiveMappingFeature, targetClassName, 
//														ontClass2MatchedPositiveMappingFeatureCount, sourceClassProbabilityComputing,
//														numberOfPositiveMappingOfTargetClass);
			System.out.println("@@1  " + support);
//			System.out.println("--- Check Matched Negative Mapping Features");
//			support *= this.computeMappingFeaturesScore(instance, 
//														negativeMappingFeature, targetClassName, 
//														ontClass2MatchedNegativeMappingFeatureCount, sourceClassProbabilityComputing,
//														numberOfNegativeMappingOfTargetClass);
			System.out.println("--- Check Matched Mapping Features");
			
			support += this.computeMappingFeaturesScore(instance, targetClassName,
					 									positiveMappingFeature, negativeMappingFeature,
					 									ontClass2MatchedPositiveMappingFeatureCount,
					 									ontClass2MatchedNegativeMappingFeatureCount,
					 									sourceClassProbabilityComputing,
					 									positiveMappingFeature.keySet().size(),
					 									negativeMappingFeature.keySet().size()); 
			
//			support += this.computeMappingFeaturesScore2(instance, targetClassName,
//														 c2cMappingFeature,
//														 ontClass2MatchedC2CMappingFeatureCount,
//														 sourceClassProbabilityComputing,
//														 numberOfC2CMappingOfTargetClass); 
			
			System.out.println("@@2 " + support);
			unionOfAllLexicalFeature.addAll(lexicalFeature.keySet());
//			unionOfAllPositiveMappingFeature.addAll(positiveMappingFeature.keySet());
//			unionOfAllNegativeMappingFeature.addAll(negativeMappingFeature.keySet());
			allLexicalFeature += numberOfInstanceOfTargetClass;
			unionOfAllPositiveMappingFeature += numberOfPositiveMappingOfTargetClass;
			unionOfAllNegativeMappingFeature += numberOfNegativeMappingOfTargetClass;
//			unionOfAllC2CMappingFeature += numberOfC2CMappingOfTargetClass;
			ontClass2Support.put(targetClassName, support);
			ontClass2InstanceRate.put(targetClassName, (double) instanceRatioOfTargetClass);
			
			System.out.println("unique lexicalFeature size: " + lexicalFeature.keySet().size());
			System.out.println("instance rate: " + 1 / ((double) numberOfInstanceOfTargetClass));
			System.out.println("unique positive mapping size: " + positiveMappingFeature.keySet().size());
			System.out.println("unique negative mapping size: " + negativeMappingFeature.keySet().size());
		}
		
		int numOfLexicalFeature = allLexicalFeature;
		int numOfUniqueLexicalFeature = unionOfAllLexicalFeature.size();
//		int numOfPositiveMappingFeature = unionOfAllPositiveMappingFeature.size();
//		int numOfNegativeMappingFeature = unionOfAllNegativeMappingFeature.size();
		int numOfPositiveMappingFeature = unionOfAllPositiveMappingFeature;
		int numOfNegativeMappingFeature = unionOfAllNegativeMappingFeature;
		int numOfC2CMappingFeature = unionOfAllC2CMappingFeature;
		double defaultInstanceRate = 1 / (double) (totalInstanceNumber + 1);
		
		////////// JUST FOR TESTING
		System.out.println("Num of LF: " + numOfLexicalFeature);
		System.out.println("Num of All LF: " + numOfUniqueLexicalFeature);
		System.out.println("Num of All PMF: " + numOfPositiveMappingFeature);
		System.out.println("Num of All NMF: " + numOfNegativeMappingFeature);
		System.out.println("Num of All MF: " + numOfC2CMappingFeature);
		//////////
		
		
		for(String className : ontClass2Support.keySet()){
			double support = ontClass2Support.get(className);
			int matchedLexicalFeatureCount = ontClass2MatchedLexicalFeatureCount.get(className);
			int matchedPositiveMappingFeatureCount = ontClass2MatchedPositiveMappingFeatureCount.get(className);
			int matchedNegativeMappingFeatureCount = ontClass2MatchedNegativeMappingFeatureCount.get(className);
//			int matchedC2CMappingFeatureCount = ontClass2MatchedC2CMappingFeatureCount.get(className);
			int difference1 = numOfUniqueLexicalFeature - matchedLexicalFeatureCount;
			int difference2 = numOfPositiveMappingFeature - matchedPositiveMappingFeatureCount;
			int difference3 = numOfNegativeMappingFeature - matchedNegativeMappingFeatureCount;
//			int difference4 = numOfC2CMappingFeature - matchedC2CMappingFeatureCount;
			
			System.out.println("Class: <" + className + ">");
			System.out.println("-- L:" + matchedLexicalFeatureCount +",  P:" + matchedPositiveMappingFeatureCount + ",  N:" + matchedNegativeMappingFeatureCount);
			System.out.println("-- DL:" + difference1 +",  DP:" + difference2 + ",  DN:" + difference3);
			
//			System.out.println("-- L:" + matchedLexicalFeatureCount + "  ,M:" + matchedC2CMappingFeatureCount);
//			System.out.println("-- DL:" + difference1 + ",  DM:" + difference4);
			
			
			
			double instanceRate = ontClass2InstanceRate.get(className);
			if(instanceRate == 0.0){
				instanceRate = defaultInstanceRate;
			}
			System.out.println("Support: <" + support + ">");
			System.out.println("Instance: <" + instanceRate + ">");
			System.out.println("lexical feature: <" + Math.log(1 / ((double) numOfLexicalFeature + 1)) * difference1 + ">");
			System.out.println("positive mapping: <" + Math.log(1 / ((double) numOfPositiveMappingFeature + 1)) * difference2 + ">");
			System.out.println("negative mapping: <" + Math.log(1 / ((double) numOfNegativeMappingFeature + 1)) * difference3 + ">");
//			System.out.println("C2C mapping: <" + Math.log(1 / ((double) numOfC2CMappingFeature + 1)) * difference4 + ">");
			System.out.println("instance rate: <" + Math.log10(instanceRate) + ">");
			
			System.out.println("lexical feature score2: " + Math.log(1 / ((double) numOfLexicalFeature + 1)));
			
			support = support + Math.log(1 / ((double) numOfLexicalFeature + 1)) * difference1 + 
					Math.log(1 / ((double) numOfPositiveMappingFeature + 1)) * difference2
					+ Math.log(1 / ((double) numOfNegativeMappingFeature + 1)) * difference3
					+ Math.log(instanceRate);
			
//			support = support + Math.log(1 / ((double) numOfLexicalFeature + 1)) * difference1 + 
//					+ Math.log(1 / ((double) numOfC2CMappingFeature + 1)) * difference4
//					+ Math.log10(instanceRate);
			
			
			ontClass2Support.put(className, support);
			
			System.out.println("-- Support:" + support);
		}
		return getClassWithHighestScore(ontClass2Support, originalClass);
	}
	
	
	/*
	 * original code backup
	 */
	
//	@Override
//	public String obtainCorrectedClassLabel(IClassifiedInstanceDetailRecord instance, String originalClass) {
//		if(_correctionFeatureWrappers.size() == 0) return instance.getOntoClassName();
//		System.out.println("");;
//		System.out.println("### Identify Class Label for " + instance.getInstanceLabel());
//		String instanceName = instance.getInstanceLabel();
//		Set<LexicalFeature> unionOfAllLexicalFeature = new HashSet<LexicalFeature>();
//		Set<IInstanceClassificationEvidence> unionOfAllPositiveMappingFeature = new HashSet<IInstanceClassificationEvidence>();
//		Set<IInstanceClassificationEvidence> unionOfAllNegativeMappingFeature = new HashSet<IInstanceClassificationEvidence>();
//		Map<String, Double> ontClass2Support = new HashMap<String, Double>();
//		Map<String, Integer> ontClass2MatchedLexicalFeatureCount = new HashMap<String, Integer>();
//		Map<String, Integer> ontClass2MatchedPositiveMappingFeatureCount = new HashMap<String, Integer>();
//		Map<String, Integer> ontClass2MatchedNegativeMappingFeatureCount = new HashMap<String, Integer>();
//		
//		Set<String> computedTargetClass = new HashSet<String>();
//		for (CorrectionClusterFeatureWrapper featureWrapper : _correctionFeatureWrappers) {
//			String targetClassName = featureWrapper.getCorrectionTargetClass();
//			if (computedTargetClass.contains(targetClassName)) continue;
//
//			computedTargetClass.add(targetClassName);
//			
//			// works as the prior probability
//			double instanceRatioOfTargetClass = featureWrapper.getInstanceRatioOfTargetClass();
//			
//			Map<LexicalFeature, Double> lexicalFeature = featureWrapper.getTargetClassInstanceLexicalFeatures();
//			Map<IInstanceClassificationEvidence, Double> positiveMappingFeature = featureWrapper.getPositiveMappingSetsWithLocalRateToCorrectionTargetClass();
//			Map<IInstanceClassificationEvidence, Double> negativeMappingFeature = featureWrapper.getNegativeMappingSetsWithLocalRateToCorrectionTargetClass();
//			
//			
//			/////// JUST FOR TESTING
//			System.out.println("COMPUTE PROBABILITY FOR CLASS: <" + targetClassName + ">");
//			System.out.println("Probability of Class - " + targetClassName +": "+ instanceRatioOfTargetClass);
//			printLexicalFeatures(lexicalFeature, "Lexical Features of " + targetClassName);
////			printMappingFeatures(positiveMappingFeature, "Positive Features of " + targetClassName);
////			printMappingFeatures(negativeMappingFeature, "Negative Features of " + targetClassName);
//			///////
//			
//			
//			System.out.println("--- Check Matched Lexical Features");
//			double support = instanceRatioOfTargetClass;
//			support = this.computeLexicalFeaturesScore(instanceName, lexicalFeature, targetClassName, ontClass2MatchedLexicalFeatureCount);
//			System.out.println("--- Check Matched Positive Mapping Features");
//			support *= this.computeMappingFeaturesScore(instance, positiveMappingFeature, targetClassName, ontClass2MatchedPositiveMappingFeatureCount);
//			System.out.println("--- Check Matched Negative Mapping Features");
//			support *= this.computeMappingFeaturesScore(instance, negativeMappingFeature, targetClassName, ontClass2MatchedNegativeMappingFeatureCount);
//			
//			unionOfAllLexicalFeature.addAll(lexicalFeature.keySet());
//			unionOfAllPositiveMappingFeature.addAll(positiveMappingFeature.keySet());
//			unionOfAllNegativeMappingFeature.addAll(negativeMappingFeature.keySet());
//			ontClass2Support.put(targetClassName, support);
//		}
//		
//		int numOfLexicalFeature = unionOfAllLexicalFeature.size();
//		int numOfPositiveMappingFeature = unionOfAllPositiveMappingFeature.size();
//		int numOfNegativeMappingFeature = unionOfAllNegativeMappingFeature.size();
//		
//		
//		////////// JUST FOR TESTING
//		printLexicalFeatures(unionOfAllLexicalFeature, "Lexical Features of All");
//		System.out.println("Num of All LF: " + numOfLexicalFeature);
//		printAllMappingFeatures(unionOfAllPositiveMappingFeature, "Positive Features of All");
//		System.out.println("Num of All PMF: " + numOfPositiveMappingFeature);
//		printAllMappingFeatures(unionOfAllNegativeMappingFeature, "Negative Features of All");
//		System.out.println("Num of All NMF: " + numOfNegativeMappingFeature);
//		//////////
//		
//		
//		for(String className : ontClass2Support.keySet()){
//			double support = ontClass2Support.get(className);
//			int matchedLexicalFeatureCount = ontClass2MatchedLexicalFeatureCount.get(className);
//			int matchedPositiveMappingFeatureCount = ontClass2MatchedPositiveMappingFeatureCount.get(className);
//			int matchedNegativeMappingFeatureCount = ontClass2MatchedNegativeMappingFeatureCount.get(className);
//			int difference1 = numOfLexicalFeature - matchedLexicalFeatureCount;
//			int difference2 = numOfPositiveMappingFeature - matchedPositiveMappingFeatureCount;
//			int difference3 = numOfNegativeMappingFeature - matchedNegativeMappingFeatureCount;
//			System.out.println("Class: <" + className + ">");
//			System.out.println("-- L:" + matchedLexicalFeatureCount +",  P:" + matchedPositiveMappingFeatureCount + ",  N:" + matchedNegativeMappingFeatureCount);
//			System.out.println("-- DL:" + difference1 +",  DP:" + difference2 + ",  DN:" + difference3);
//			support = Math.log(support * Math.pow(1 / (double) numOfLexicalFeature, difference1)
//					* Math.pow(1 / (double) numOfPositiveMappingFeature, difference2)
//					* Math.pow(1 / (double) numOfNegativeMappingFeature, difference3));
//			
//			
//			ontClass2Support.put(className, support);
//			
//			System.out.println("-- Support:" + support);
//		}
//		return getClassWithHighestScore(ontClass2Support, originalClass);
//	}	
	

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

	private void printMappingFeatures(Map<IInstanceClassificationEvidence, Double> mappingFeatures, String label) {
		for (IInstanceClassificationEvidence f : mappingFeatures.keySet()) {
			double representativeness = mappingFeatures.get(f);
			if (f == null) {
				System.out.println(label + ": default2 " + representativeness);
			} else {
				System.out.println(label + ": " + f.getEvidenceCode() + ",  " + representativeness);
			}
		}
	}
	
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
//		double totalSupport = 1.0;
		double totalSupport = 0.0;
		String lowerCaseInstanceLabel = TextProcessingUtils.getProcessedLabel2WithStemming(instanceLabel, " ");
		for (LexicalFeature feature : lexicalFeatures.keySet()) {
			if (feature == null)
				continue;
			String lowerCaseSubStringLabel = feature.getLabel();
			if (LexicalFeature.Position.BEGIN == feature.getFeaturePosition()) {
				if (lowerCaseInstanceLabel.startsWith(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED BEGIN: <" + feature.getLabel() + ">, count: " + feature.getCount() + ", strength: " + feature.getSignificant()+", " + feature.getSupport());
//					totalSupport*= lexicalFeatures.get(feature);
					System.out.println("feature score: " + feature);
					System.out.println("feature score2: " + lexicalFeatures.get(feature));
					totalSupport+= Math.log(lexicalFeatures.get(feature));
					matchedFeatureCount++;
				}
			} else if (LexicalFeature.Position.END == feature.getFeaturePosition()) {
				if (lowerCaseInstanceLabel.endsWith(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED END: <" + feature.getLabel() + ">, count: "+ feature.getCount() + ", strength: " + feature.getSignificant()+", " + feature.getSupport());
//					totalSupport*= lexicalFeatures.get(feature);
					System.out.println("feature score: " + feature);
					System.out.println("feature score2: " + lexicalFeatures.get(feature));
					totalSupport+= Math.log(lexicalFeatures.get(feature));
					matchedFeatureCount++;
				}
			} else if (LexicalFeature.Position.ANY == feature.getFeaturePosition()){
				if (lowerCaseInstanceLabel.contains(lowerCaseSubStringLabel)) {
					System.out.println("  MATCHED ANY: <" + feature.getLabel() + ">, count: "+ feature.getCount() + ", strength: " + feature.getSignificant()+", " + feature.getSupport());
//					totalSupport*= lexicalFeatures.get(feature);
					System.out.println("feature score: " + feature);
					System.out.println("feature score2: " + lexicalFeatures.get(feature));
					totalSupport+= Math.log(lexicalFeatures.get(feature));
					matchedFeatureCount++;
				}
			} else {
				
			}
		}

		correction2LexicalFeatureCount.put(targetClassName, matchedFeatureCount);
		return totalSupport;
	}
	
	private double computeMappingFeaturesScore2(IClassifiedInstanceDetailRecord instance, String targetClassName,
			Map<IConcept2OntClassMapping, Double> evidences, Map<String, Integer> ontClass2MatchedC2CMappingFeatureCount,
			boolean sourceClassProbabilityComputing, int numberOfC2CMappingOfTargetClass) {

		ontClass2MatchedC2CMappingFeatureCount.put(targetClassName, 0);
		double score = 0.0;
		List<IConcept2OntClassMapping> mappingList = instance.getConcept2OntClassMappingPairs();

		int sizeOfList = mappingList.size();
		for (int i = 0; i < sizeOfList; i++) {
			IConcept2OntClassMapping mapping = mappingList.get(i);
			if (!mapping.isMappedConcept())
				continue;

			if (!evidences.containsKey(mapping)) {

				if (sourceClassProbabilityComputing) {
					// score *= 1 / ((double)
					// numberOfPositiveMappingOfTargetClass + 1);
					// score += Math.log(1 / Math.log(((double)
					// numberOfPositiveMappingOfTargetClass + 1) + 1));
					score += Math.log(1 / ((double) numberOfC2CMappingOfTargetClass + 1));
					int count = ontClass2MatchedC2CMappingFeatureCount.get(targetClassName);
					count++;
					ontClass2MatchedC2CMappingFeatureCount.put(targetClassName, count);
					System.out.println("  MATCHED MAPPING FEATURE 1: " + mapping.getMappingCode() + " --> " + targetClassName + " with Score: " + score);
				}
			} else {

				// score *= positiveEvidences.get(ms);
				score += Math.log(evidences.get(mapping));
				int count = ontClass2MatchedC2CMappingFeatureCount.get(targetClassName);
				count++;
				ontClass2MatchedC2CMappingFeatureCount.put(targetClassName, count);
				System.out.println("  MATCHED MAPPING FEATURE 2: <" + mapping.getMappingCode() + " --> " + targetClassName + " with Score: " + score);

			}
		}
		return score;
	}

	private double computeMappingFeaturesScore(IClassifiedInstanceDetailRecord instance,String targetClassName,
			Map<IInstanceClassificationEvidence, Double> positiveEvidences,  
			Map<IInstanceClassificationEvidence, Double> negativeEvidences,
			Map<String, Integer> ontClass2MatchedPositiveMappingFeatureCount,
			Map<String, Integer> ontClass2MatchedNegativeMappingFeatureCount,
			boolean sourceClassProbabilityComputing,
			int numberOfPositiveMappingOfTargetClass,
			int numberOfNegativeMappingOfTargetClass) {

		ontClass2MatchedPositiveMappingFeatureCount.put(targetClassName, 0);
		ontClass2MatchedNegativeMappingFeatureCount.put(targetClassName, 0);
//		double score = 1.0;
		double score = 0.0;
		List<IConcept2OntClassMapping> mappingList = instance.getConcept2OntClassMappingPairs();

		int sizeOfList = mappingList.size();
		for (int i = 0; i < sizeOfList; i++) {
			IConcept2OntClassMapping mapping = mappingList.get(i);
			if (!mapping.isMappedConcept())
				continue;

			Set<IConcept2OntClassMapping> mappingSet = new HashSet<IConcept2OntClassMapping>();
			mappingSet.add(mapping);
			
			IInstanceClassificationEvidence ms = InstanceClassificationEvidence.createInstance(mappingSet, targetClassName);
			if (!positiveEvidences.containsKey(ms) && !negativeEvidences.containsKey(ms) ) {
				
				if (sourceClassProbabilityComputing){
//					score *= 1 / ((double) numberOfPositiveMappingOfTargetClass + 1);
//					score += Math.log(1 / Math.log(((double) numberOfPositiveMappingOfTargetClass + 1) + 1));
					score += Math.log(1 / ((double) numberOfPositiveMappingOfTargetClass + 1));
					int count = ontClass2MatchedPositiveMappingFeatureCount.get(targetClassName);
					count++;
					ontClass2MatchedPositiveMappingFeatureCount.put(targetClassName, count);
					System.out.println("  MATCHED MAPPING FEATURE 1: <" + ms.getEvidenceCode() + "> with Score: " + score);
				}
			} else {
				
				if (positiveEvidences.containsKey(ms)){
//					score *= positiveEvidences.get(ms);
					score += Math.log(positiveEvidences.get(ms));
					int count = ontClass2MatchedPositiveMappingFeatureCount.get(targetClassName);
					count++;
					ontClass2MatchedPositiveMappingFeatureCount.put(targetClassName, count);
					System.out.println("  MATCHED MAPPING FEATURE 2: <" + ms.getEvidenceCode() + "> with Score: " + score);
				}
				
				if(negativeEvidences.containsKey(ms)) {
//					score *= negativeEvidences.get(ms);
					score += Math.log(negativeEvidences.get(ms));
					int count = ontClass2MatchedNegativeMappingFeatureCount.get(targetClassName);
					count++;
					ontClass2MatchedNegativeMappingFeatureCount.put(targetClassName, count);
					System.out.println("  MATCHED MAPPING FEATURE 3: <" + ms.getEvidenceCode() + "> with Score: " + score);
				}
			}
		}
		return score;
	}


//	private double computeMappingFeaturesScore(
//												IClassifiedInstanceDetailRecord instance,
//												Map<IInstanceClassificationEvidence, Double> mappingSets, String targetClassName,
//												Map<String, Integer> ontClass2MatchedMappingFeatureCount, 
//												boolean sourceClassProbabilityComputing,
//												int numberOfMappingOfTargetClass
//											  ){
//
//		ontClass2MatchedMappingFeatureCount.put(targetClassName, 0);
//		double totalScore = 1.0;
//		List<IConcept2OntClassMapping> mappingList = instance.getConcept2OntClassMappingPairs();
//		
////		for (IConcept2OntClassMapping mapping : mappingList) {
////			System.out.println("*** " + mapping.getMappingCode());
////		}
//		
//		int sizeOfList = mappingList.size();
//		for (int i = 0; i < sizeOfList; i++) {
//			IConcept2OntClassMapping mapping1 = mappingList.get(i);
//			if (!mapping1.isMappedConcept()) continue;
//			
//			Set<IConcept2OntClassMapping> mappingSet1 = new HashSet<IConcept2OntClassMapping>();
//			mappingSet1.add(mapping1);
////			System.out.println("1** " + mappingSet1);
//			totalScore *= getMappingSetStrength(mappingSet1, mappingSets, targetClassName, 
//												ontClass2MatchedMappingFeatureCount, sourceClassProbabilityComputing,
//												numberOfMappingOfTargetClass);
//
////			for (int j = i+1; j < sizeOfList; j++) {
////				IConcept2OntClassMapping mapping2 = mappingList.get(j);
////				if (!mapping2.isMappedConcept() || mapping2.equals(mapping1)) continue;
////				
////				Set<IConcept2OntClassMapping> mappingSet2 = new HashSet<IConcept2OntClassMapping>();
////				mappingSet2.add(mapping1);
////				mappingSet2.add(mapping2);
//////				System.out.println("2** " + mappingSet2);
////				totalScore *= getMappingSetStrength(mappingSet2, mappingSets, targetClassName, 
////													ontClass2MatchedMappingFeatureCount, sourceClassProbabilityComputing,
////													numberOfMappingOfTargetClass);
////
////			}
//		}
//		return totalScore;
//	}
//
//	private double getMappingSetStrength(
//										  Set<IConcept2OntClassMapping> mappingSet,
//										  Map<IInstanceClassificationEvidence, Double> mappingSet2Strength, 
//										  String targetClassName,
//										  Map<String, Integer> ontClass2MatchedMappingFeatureCount,
//										  boolean sourceClassProbabilityComputing,
//										  int numberOfMappingOfTargetClass
//										) {
//
//		double score = 1.0;
//		IInstanceClassificationEvidence ms = InstanceClassificationEvidence.createInstance(mappingSet, targetClassName);
//		if (mappingSet2Strength.containsKey(ms)) {
//			score = mappingSet2Strength.get(ms);
//			int count = ontClass2MatchedMappingFeatureCount.get(targetClassName);
//			count++;
//			ontClass2MatchedMappingFeatureCount.put(targetClassName, count);
//			System.out.println("  MATCHED MAPPING FEATURE 1: <" + ms.getEvidenceCode() + "> with Score: " + score);
//		} else if (sourceClassProbabilityComputing){
//			score = 1 / ((double) numberOfMappingOfTargetClass + 1);
//			int count = ontClass2MatchedMappingFeatureCount.get(targetClassName);
//			count++;
//			ontClass2MatchedMappingFeatureCount.put(targetClassName, count);
//			System.out.println("  MATCHED MAPPING FEATURE 2: <" + ms.getEvidenceCode() + "> with Score: " + score);
//		}
//		return score;
//	}
}
