package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.List;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SubString;

public interface IInstanceLexicalFeatureExtractor {

	public Map<LexicalFeature, LexicalFeature> getInstanceLexicalFeaturesOfOntClass(String className);

	public Map<LexicalFeature, LexicalFeature> getLexicalFeaturesOfAllInstances();

	public Map<LexicalFeature, LexicalFeature> getInstancesLexicalFeatures(List<String> instances);

	public int getInstanceCount();

	Map<LexicalFeature, Double> getLexicalFeaturesWithRepresentativenessOfOntClass(String className);

	Map<LexicalFeature, Double> getLexicalFeatureWithNormalizedInverseOntClassFrequency();

	Map<LexicalFeature, Integer> getLexicalFeatureWithOntClassCount();

	double getInstanceRateOfOntClass(String className);

	int getNumberOfInstancesOfOntClass(String className);  

}
