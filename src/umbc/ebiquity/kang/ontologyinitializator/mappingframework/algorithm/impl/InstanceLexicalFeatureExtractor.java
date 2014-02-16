package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature.Position;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SubString;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ILexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.utilities.Debugger;

public class InstanceLexicalFeatureExtractor implements IInstanceLexicalFeatureExtractor {

	private IClassifiedInstancesAccessor _classifiedInstanceAccessor;
	private ILexicalFeatureExtractor _lexicalFeatureExtractor;
	private Map<String, Map<LexicalFeature, LexicalFeature>> _class2LexicalFeaturesCache;
	private Map<LexicalFeature, LexicalFeature> _allInstanceFeaturesCache;
	private Map<LexicalFeature, Double> _featureNICF_Cache;
	private Map<LexicalFeature, Integer> _feature2ClassCount_Cache;
	
	
	public InstanceLexicalFeatureExtractor(
			                               IClassifiedInstancesAccessor classifiedInstanceAccessor,
			                               ILexicalFeatureExtractor commonPhraseExtractor
               							  ){
		_classifiedInstanceAccessor = classifiedInstanceAccessor;
		_lexicalFeatureExtractor = commonPhraseExtractor;
		_class2LexicalFeaturesCache = new HashMap<String, Map<LexicalFeature, LexicalFeature>>();
		_allInstanceFeaturesCache = new HashMap<LexicalFeature, LexicalFeature>();
	}
	
	@Override
	public int getInstanceCount(){
		return _classifiedInstanceAccessor.getInstances().size();
	}
	
	@Override
	public Map<LexicalFeature, Double> getLexicalFeaturesWithRepresentativenessOfOntClass(String className){
		Map<LexicalFeature, Double> feature2Representativeness = new HashMap<LexicalFeature, Double>();
//		Map<LexicalFeature, Double> feature2NICF = this.getNormalizedInverseOntClassFrequency();
		Map<LexicalFeature, LexicalFeature> lexicalFeaturesOfInstancesOfTargetClass = this.getInstanceLexicalFeaturesOfOntClass(className);
		
		double totalStrength = 0.0;
		for (LexicalFeature featureOfTargetClass : lexicalFeaturesOfInstancesOfTargetClass.keySet()) {
//			double NICF = feature2NICF.get(featureOfTargetClass);
			double NICF = 1.0;
			double strength = this.computeFeatureStrength(featureOfTargetClass, NICF, true);
			totalStrength += strength;
			feature2Representativeness.put(featureOfTargetClass, strength); 
		}

		for (LexicalFeature featureOfTargetClass : feature2Representativeness.keySet()) {
			feature2Representativeness.put(featureOfTargetClass, feature2Representativeness.get(featureOfTargetClass) / totalStrength);
		}
		
		return feature2Representativeness;
	}
	
	private double computeFeatureStrength(LexicalFeature feature, double NICF, boolean withReduceFactor) {
		double w1;
		double w2;
		if (feature instanceof Phrase) {
			w1 = 0.3;
			w2 = 0.7;
		} else {
			w1 = 0.7;
			w2 = 0.3;
		}
		
		double reduceStrengthFactor = 1.0;
		if(withReduceFactor && feature.getFeaturePosition() == Position.BEGIN){
			reduceStrengthFactor = 0.4;
		}
		return reduceStrengthFactor * (w2 * feature.getSignificant() + w1 * (double) feature.getSupport() * NICF);
	}

	@Override
	public Map<LexicalFeature, Integer> getLexicalFeatureWithOntClassCount(){
		if(_feature2ClassCount_Cache == null){
		   this.getNormalizedInverseOntClassFrequency(); 
		}
		return this._feature2ClassCount_Cache;
	}
	
	@Override
	public Map<LexicalFeature, Double> getLexicalFeatureWithNormalizedInverseOntClassFrequency(){
		if(_featureNICF_Cache == null){
		   this.getNormalizedInverseOntClassFrequency(); 
		}
		return this._featureNICF_Cache;
	}
	
	private Map<LexicalFeature, Double> getNormalizedInverseOntClassFrequency(){
		if (_featureNICF_Cache == null) {
			_featureNICF_Cache = new LinkedHashMap<LexicalFeature, Double>();
			_feature2ClassCount_Cache = new LinkedHashMap<LexicalFeature, Integer>(); 
		} else {
			return _featureNICF_Cache;
		}
		Map<LexicalFeature, Integer> feature2ClassCount = new HashMap<LexicalFeature, Integer>();
		Map<LexicalFeature, Double> feature2NICF = new HashMap<LexicalFeature, Double>();
		
		Collection<OntoClassInfo> allClasses = _classifiedInstanceAccessor.getAllClasses();
		int totalNumberOfClasses = allClasses.size();
		double dominator = 0.0;
		for (LexicalFeature feature : this.getLexicalFeaturesOfAllInstances().values()) {
//			System.out.println("Current Feature: " + feature.toString());
//			if("ing@[SUBSTRING-END]".equals(feature.toString()))
//			{
//				System.out.println(" find class of: " + feature.toString());
//			}
			for (OntoClassInfo c : allClasses) {
				for (String instance : _classifiedInstanceAccessor.getInstancesOfOntClass(c)) {
					String lowerCaseInstanceLabel = _lexicalFeatureExtractor.normalizeLabelToString(instance).toLowerCase();
					String lowerCaseSubStringLabel = feature.getLabel().toLowerCase();
					if (LexicalFeature.Position.BEGIN == feature.getFeaturePosition()) {
						if (lowerCaseInstanceLabel.startsWith(lowerCaseSubStringLabel)) {
//							System.out.println("   " + instance);
							int classCount = 1;
							if (feature2ClassCount.containsKey(feature)) {
								classCount = feature2ClassCount.get(feature);
								classCount++;
							}
							feature2ClassCount.put(feature, classCount);
							break;
						}
					} else if (LexicalFeature.Position.END == feature.getFeaturePosition()) {
//						System.out.println("   " + instance);
						if (lowerCaseInstanceLabel.endsWith(lowerCaseSubStringLabel)) {
							int classCount = 1;
							if (feature2ClassCount.containsKey(feature)) {
								classCount = feature2ClassCount.get(feature);
								classCount++;
							}
//							if("ing@[SUBSTRING-END]".equals(feature.toString()))
//							{
//								
//							System.out.println(" class: " + c.getOntClassName());
//							System.out.println("      instnace: " + instance);
//							}
							feature2ClassCount.put(feature, classCount);
							break;
						}
					} else {
						if (lowerCaseInstanceLabel.contains(lowerCaseSubStringLabel)) {
//							System.out.println("   " + instance);
							int classCount = 1;
							if (feature2ClassCount.containsKey(feature)) {
								classCount = feature2ClassCount.get(feature);
								classCount++;
							}
							feature2ClassCount.put(feature, classCount);
							break;
						}
					}
					
//					if (instance.toLowerCase().contains(feature.getLabel())) {
//						System.out.println("   " + instance);
//						int classCount = 1;
//						if (feature2ClassCount.containsKey(feature)) {
//							classCount = feature2ClassCount.get(feature);
//							classCount++;
//						}
//						feature2ClassCount.put(feature, classCount);
//						break;
//					}
				}
			}
			
			int classCount = feature2ClassCount.get(feature);
			dominator += (double) totalNumberOfClasses / (double) classCount;
		}
		
//		System.out.println("@@@ dominator: " + dominator);
		for(LexicalFeature feature: feature2ClassCount.keySet()){
			int classCount = feature2ClassCount.get(feature);
//			System.out.println(feature.toString() + " " + classCount + " " + totalNumberOfClasses);
			double numerator = (double) totalNumberOfClasses / (double) classCount;
//			System.out.println("    " + numerator + "/" + dominator + "=" + numerator/dominator);
			feature2NICF.put(feature, numerator/dominator);
		}
		_featureNICF_Cache.putAll(feature2NICF);
		_feature2ClassCount_Cache.putAll(feature2ClassCount);
		return feature2NICF;
	}
	
	@Override
	public double getInstanceRateOfOntClass(String className) {
		int numOfInstanceOfCurrentClass = _classifiedInstanceAccessor.getInstancesOfOntClass(className).size();
		int numOfAllInstance = this.getInstanceCount();
		return (double) numOfInstanceOfCurrentClass / (double) numOfAllInstance;
	}
	
	@Override
	public Map<LexicalFeature, LexicalFeature> getLexicalFeaturesOfAllInstances() {
		if (_allInstanceFeaturesCache.isEmpty()) {
			List<String> classifiedInstances = _classifiedInstanceAccessor.getInstances();
			
//			for(String instance : classifiedInstances){
//				Debugger.print("  Instance1: %s", instance);
//			}
			
			Map<LexicalFeature, LexicalFeature> instanceLabelFeatures = this.getInstancesLexicalFeatures(classifiedInstances);
			_allInstanceFeaturesCache.putAll(instanceLabelFeatures);
		}
		return _allInstanceFeaturesCache;
	}
	
	@Override
	public Map<LexicalFeature, LexicalFeature> getInstanceLexicalFeaturesOfOntClass(String className) {
//		Debugger.print("", "");
//		Debugger.print("Get Instance of: %s", className);
		if(_class2LexicalFeaturesCache.containsKey(className)){
//			Debugger.print("  cache hitted");
			return _class2LexicalFeaturesCache.get(className);
		}
		List<String> classifiedInstances = new ArrayList<String>();
		classifiedInstances.addAll(_classifiedInstanceAccessor.getInstancesOfOntClass(className));
		
//		for(String instance : classifiedInstances){
//			Debugger.print("  Instance2: %s", instance);
//		}
		
		Map<LexicalFeature, LexicalFeature> instanceLabelFeatures = this.getInstancesLexicalFeatures(classifiedInstances);
		_class2LexicalFeaturesCache.put(className, instanceLabelFeatures);
		return instanceLabelFeatures;
	}

	@Override
	public Map<LexicalFeature, LexicalFeature> getInstancesLexicalFeatures(List<String> instances) {

		Map<LexicalFeature, LexicalFeature> instanceLabelFeatures = new LinkedHashMap<LexicalFeature, LexicalFeature>();
		Collection<SubString> subStrings = _lexicalFeatureExtractor.extractCommonSubStrings(new ArrayList<String>(instances));
		Map<String, Double> duplicateFeatureFilter = new HashMap<String, Double>();
		for (SubString subString : subStrings) {
			// System.out.println("*** Sub-String: " + subString.getLabel() +
			// ", " + subString.getCount() + ", " + subString.getSignificant() +
			// ", " + subString.getSupport());
			instanceLabelFeatures.put(subString, subString);
			String code = subString.getLabel().toLowerCase();
			duplicateFeatureFilter.put(code, subString.getSupport());
		}
		for (Phrase phrase : _lexicalFeatureExtractor.extractCommonPhrases(instances)) {
			// System.out.println("Phrase: " + phrase.getLabel() + ", " +
			// phrase.getCount() + ", " + phrase.getSignificant() + ", " +
			// phrase.getSupport());
			String code = phrase.getLabel();
			if (duplicateFeatureFilter.containsKey(code) && duplicateFeatureFilter.get(code) < phrase.getSupport()) {
				instanceLabelFeatures.remove(new SubString(phrase.getLabel(), phrase.getFeaturePosition()));
				instanceLabelFeatures.put(phrase, phrase);
			}
		}
		return instanceLabelFeatures;
	}
}
