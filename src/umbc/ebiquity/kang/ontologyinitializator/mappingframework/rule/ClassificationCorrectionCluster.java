package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.ClassificationCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IFeatureMatcher;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;
import umbc.ebiquity.kang.ontologyinitializator.utilities.Debugger;

public class ClassificationCorrectionCluster {
	
	private String _clusterCode;
	private Set<String> _correctionSourceClassSet;
	private String _correctionTargetClass;
	private double _instanceRateOfTargetClass;
	
	private List<String> _correctInstances;
	private Collection<IConcept2OntClassMapping> _allMappings;
	private Map<String, String> _hierarchyCloseness;
	
	private Collection<String> _negativeConcepts;
	private Collection<String> _positiveConcepts;
	private Collection<String> _otherConcept;
	private Collection<String> _unMappedConcepts;
	private Collection<String> _allConcepts;
	
	private Collection<IClassificationCorrection> _corrections;
	private Collection<LexicalFeature> _correctedInstanceLabelFeatures;
	private Map<LexicalFeature, Double> _lexicalFeaturesWithRepresentativenessOfTargetClass;
	private Map<LexicalFeature, Double> _lexicalFeaturesWithRepresentativenessOfSourceClass;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingsInThisCorrectionCluster;
	private Map<IInstanceMembershipInfereceFact, Double> _positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingSetsWithLocalRateToCorrectionSourceClass;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingSetsWithLocalRateToCorrectionTargetClass;
	
    private IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor;
	private IInstanceConcept2OntClassMappingFeatureExtractor _instanceConcept2OntClassMappingFeatureExtractor;

    public ClassificationCorrectionCluster(String clusterCode,
    									   CorrectionDirection correctionDirection, 
    									   IInstanceLexicalFeatureExtractor instanceLexicalFeatureExtractor,
    									   IInstanceConcept2OntClassMappingFeatureExtractor instanceConcept2OntClassMappingFeatureExtractor){
		this(clusterCode, 
			 correctionDirection.getSourceClassSet(), 
			 correctionDirection.getTargetClassName(), 
			 instanceLexicalFeatureExtractor, 
			 instanceConcept2OntClassMappingFeatureExtractor);
	}
    
	public ClassificationCorrectionCluster(String clusterCode,
			                               Set<String> correctionSourceClassSet,
			                               String correctionTargetClass,
			                               IInstanceLexicalFeatureExtractor instanceLexicalFeatureExtractor,
			                               IInstanceConcept2OntClassMappingFeatureExtractor instanceConcept2OntClassMappingFeatureExtractor) {
//		Debugger.print("Construct Cluster %s --> %s", correctionSourceClass, correctionTargetClass);
		_clusterCode = clusterCode;
		_correctionSourceClassSet = correctionSourceClassSet;
		_correctionTargetClass = correctionTargetClass;
		_instanceLexicalFeatureExtractor = instanceLexicalFeatureExtractor;
		_instanceConcept2OntClassMappingFeatureExtractor = instanceConcept2OntClassMappingFeatureExtractor;
		_correctInstances = new ArrayList<String>();
		_corrections = new HashSet<IClassificationCorrection>();
		_hierarchyCloseness = new HashMap<String, String>();

		_negativeMappingsInThisCorrectionCluster = new HashMap<IInstanceMembershipInfereceFact, Double>();
		_positiveMappingSetsWithLocalRateToCorrectionTargetClass = new HashMap<IInstanceMembershipInfereceFact, Double>();
		_negativeMappingSetsWithLocalRateToCorrectionSourceClass = new HashMap<IInstanceMembershipInfereceFact, Double>();
		_negativeMappingSetsWithLocalRateToCorrectionTargetClass = new HashMap<IInstanceMembershipInfereceFact, Double>();

		_negativeConcepts = new ArrayList<String>();
		_positiveConcepts = new ArrayList<String>();
		_otherConcept = new ArrayList<String>();
		
		_allMappings = new HashSet<IConcept2OntClassMapping>();
		_unMappedConcepts = new HashSet<String>();
		_allConcepts = new HashSet<String>();
	}

	public void addCorrection(IClassificationCorrection correction){
		this._corrections.add(correction);
	}
	
	public Collection<IClassificationCorrection> getCorrections(){
		return this._corrections;
	}

	public void extractFeatures() {
		this.extractMappingFeatures();
		this.extractCorrectedInstanceLabelFeatures();
		this.extractTargetClassInstanceLexicalFeatures();
		this.extractSourceClassInstanceLexicalFeatures(); 
	}
	
	private void extractMappingFeatures(){
		
		// get negative mapping in this correction cluster from Classification Correction Repository
		int allMappingCount = _instanceConcept2OntClassMappingFeatureExtractor.getAllConcept2OntClassMappingCount();
		
		_instanceRateOfTargetClass = _instanceLexicalFeatureExtractor.getInstanceRateOfOntClass(_correctionTargetClass);
		
		double defaultCount = (double) 1 / (double) allMappingCount;
		Map<IInstanceMembershipInfereceFact, Double> tempNegativeMappingsInThisCorrectionCluster = _instanceConcept2OntClassMappingFeatureExtractor.getNegativeConcept2OntClassMappingSetsWithRateOfCorrectionCluster(_clusterCode);
		if (tempNegativeMappingsInThisCorrectionCluster != null) {
			_negativeMappingsInThisCorrectionCluster.clear();
			_negativeMappingsInThisCorrectionCluster.putAll(tempNegativeMappingsInThisCorrectionCluster);
			_negativeMappingsInThisCorrectionCluster.put(null, defaultCount);
		}
		
		// get negative mapping from Classification Correction Repository
		for (String _correctionSourceClass : _correctionSourceClassSet) {
			Map<IInstanceMembershipInfereceFact, Double> tempNegativeMappings = _instanceConcept2OntClassMappingFeatureExtractor.getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(_correctionSourceClass);
			if (tempNegativeMappings != null) {
				_negativeMappingSetsWithLocalRateToCorrectionSourceClass.clear();
				_negativeMappingSetsWithLocalRateToCorrectionSourceClass.putAll(tempNegativeMappings);
				_negativeMappingSetsWithLocalRateToCorrectionSourceClass.put(null, defaultCount);
			}
		}
		
		Map<IInstanceMembershipInfereceFact, Double> tempNegativeMappings = _instanceConcept2OntClassMappingFeatureExtractor.getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(_correctionTargetClass);
		if (tempNegativeMappings != null) {
			_negativeMappingSetsWithLocalRateToCorrectionTargetClass.clear();
			_negativeMappingSetsWithLocalRateToCorrectionTargetClass.putAll(tempNegativeMappings);
			_negativeMappingSetsWithLocalRateToCorrectionTargetClass.put(null, defaultCount);
		}
		
		// get positive mapping from Classification Correction Repository
		Map<IInstanceMembershipInfereceFact, Double> tempPositiveMapping = _instanceConcept2OntClassMappingFeatureExtractor.getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(_correctionTargetClass);
		if (tempPositiveMapping != null) {
			_positiveMappingSetsWithLocalRateToCorrectionTargetClass.clear();
			_positiveMappingSetsWithLocalRateToCorrectionTargetClass.putAll(tempPositiveMapping);
			_positiveMappingSetsWithLocalRateToCorrectionTargetClass.put(null, defaultCount);
		}
		
		for (IClassificationCorrection correction : _corrections) {
			String instanceName = correction.getInstance();
			_correctInstances.add(instanceName);
			for (IConcept2OntClassMapping mapping : correction.getHittedMappings()) {
				_allMappings.add(mapping);
			}
			for (IConcept2OntClassMapping mapping : correction.getAmbiguousMappings()) {
				_allMappings.add(mapping);
			}
			for (String concept : correction.getUnMappedConcepts()) {
				_unMappedConcepts.add(concept);
			}
			for (String hierarchyNum : correction.getMappedClassHierarchies().keySet()) {
				_hierarchyCloseness.put(hierarchyNum, correction.getMappedClassHierarchies().get(hierarchyNum));
			}
		}
	}
	
	private void extractCorrectedInstanceLabelFeatures(){
//		Debugger.print("Extract Corrected Instance Label Features", "");
		
		_correctedInstanceLabelFeatures = new ArrayList<LexicalFeature>(); 
		_correctedInstanceLabelFeatures.addAll(_instanceLexicalFeatureExtractor.getInstancesLexicalFeatures(_correctInstances).values());
	}
	
	private void extractSourceClassInstanceLexicalFeatures() {
//		Debugger.print("Extract Source Class Features", "");
		
		_lexicalFeaturesWithRepresentativenessOfSourceClass = new HashMap<LexicalFeature, Double>();
		for (String _correctionSourceClass : _correctionSourceClassSet) {
			_lexicalFeaturesWithRepresentativenessOfSourceClass.putAll(_instanceLexicalFeatureExtractor.getLexicalFeaturesWithRepresentativenessOfOntClass(_correctionSourceClass));
		}

		_lexicalFeaturesWithRepresentativenessOfSourceClass.put(null, (double) 1 / (double) _instanceLexicalFeatureExtractor.getInstanceCount());
	}
	
	private void extractTargetClassInstanceLexicalFeatures() {
//		Debugger.print("Extract Target Class Features", "");
		
		_lexicalFeaturesWithRepresentativenessOfTargetClass = new HashMap<LexicalFeature, Double>();
		_lexicalFeaturesWithRepresentativenessOfTargetClass.putAll(_instanceLexicalFeatureExtractor.getLexicalFeaturesWithRepresentativenessOfOntClass(_correctionTargetClass));
		_lexicalFeaturesWithRepresentativenessOfTargetClass.put(null, (double) 1 / (double) _instanceLexicalFeatureExtractor.getInstanceCount());
	}
	
	public void showFeatures() {
		
		System.out.println("---------------------------------------");
		System.out.println("Cluster: " + _clusterCode);
		System.out.println("instance:");
		for (String instance : _correctInstances) {
			System.out.println("  " + instance);
		}
	
		System.out.println();
		System.out.println();
//		this.createCorrectionMappingFeatures();
		System.out.println();
		System.out.println();
		System.out.println("### Negative Concepts:");
		for (String concept : _negativeConcepts) {
			System.out.println("  " + concept);
		}
		System.out.println("### Positive Concepts:");
		for (String concept : _positiveConcepts) {
			System.out.println("  " + concept);
		}
		System.out.println("### Other Concepts:");
		for (String concept : _otherConcept) {
			System.out.println("  " + concept);
		}
		System.out.println("### UnMapped Concepts:");
		for (String concept : _unMappedConcepts) {
			System.out.println("  " + concept);
		}
		for(String hierarchyNumber : _hierarchyCloseness.keySet()){
			System.out.println("HN: " + hierarchyNumber + " : " + _hierarchyCloseness.get(hierarchyNumber));
		}
		
		System.out.println();
		System.out.println("### Corrected Instance Label Features:");
		showLexicalFeatures(_correctedInstanceLabelFeatures);
		System.out.println();
		System.out.println("### Target Class Features:");
		showLexicalFeatures(_lexicalFeaturesWithRepresentativenessOfTargetClass);
		System.out.println();
//		System.out.println("### Target Class Not in Source Class Features:");
//		showLexicalFeatures(_featuresOfTargetClassButNotSourceClass);
//		System.out.println();
		System.out.println("### SOURCE CLASS FEATURES:");
		showLexicalFeatures(_lexicalFeaturesWithRepresentativenessOfSourceClass);
		System.out.println();
		System.out.println("### POSITIVE MAPPING");
//		for(String mappingCode : _positiveMappingSetsToCorrectionTargetClass.keySet()){
//			if(mappingCode == null) continue;
//			double rate = _positiveMappingSetsToCorrectionTargetClass.get(mappingCode);
//			System.out.println(mappingCode + " : " + rate);
//		}
//		System.out.println();
//		System.out.println("### NEGATIVE MAPPING");
//		for(String mappingCode : _negativeMappingSetsToCorrectionSourceClass.keySet()){
//			if(mappingCode == null) continue;
//			double rate = _negativeMappingSetsToCorrectionSourceClass.get(mappingCode);
//			System.out.println(mappingCode + " : " + rate);
//		}
//		System.out.println();
//		System.out.println("### NEGATIVE MAPPING in CORRECTION CLUSTER");
//		for(String mappingCode : _negativeMappingsInThisCorrectionCluster.keySet()){
//			if(mappingCode == null) continue;
//			double rate = _negativeMappingsInThisCorrectionCluster.get(mappingCode);
//			System.out.println(mappingCode + " : " + rate);
//		}
//		System.out.println("---------------------------------------");
	}
	
	private void showLexicalFeatures(Map<LexicalFeature, Double> lexicalFeatures){
		for(LexicalFeature feature : lexicalFeatures.keySet()){
			if (feature == null) continue;
			double strength = lexicalFeatures.get(feature);
			Debugger.print("  %s, %f", feature.getLabel(), strength);
		}
		
		Debugger.print("  Count %d",  lexicalFeatures.keySet().size());
	}
	
	private void showLexicalFeatures(Collection<LexicalFeature> lexicalFeatures){
		for(LexicalFeature feature : lexicalFeatures){
			Debugger.print("  %s, %f", feature.getLabel(), feature.getSupport());
		}
		Debugger.print("  Count %d",  lexicalFeatures.size());
	}
	
	
	public Set<String> getDirectionSourceClassSet(){
		return this._correctionSourceClassSet;
	}
	
	public String getDirectionTargetClass(){
		return this._correctionTargetClass;
	}

	public CorrectionClusterFeatureWrapper getCorrectionClusterFeature(){
		return new CorrectionClusterFeatureWrapper(
				   _correctionSourceClassSet,
				   _correctionTargetClass,
				   _instanceRateOfTargetClass,
				   _correctedInstanceLabelFeatures,
				   _lexicalFeaturesWithRepresentativenessOfSourceClass,
				   _lexicalFeaturesWithRepresentativenessOfTargetClass,
				   _negativeMappingsInThisCorrectionCluster,
				   _negativeMappingSetsWithLocalRateToCorrectionSourceClass,
				   _negativeMappingSetsWithLocalRateToCorrectionTargetClass,
				   _positiveMappingSetsWithLocalRateToCorrectionTargetClass
	    );
	}
	
//	public IClassificationCorrectionRule toCorrectionRule() {
//		IFeatureMatcher featureMatcher = new FeatureBasedFeatureMatcher(
//				   _correctedInstanceLabelFeatures,
//				   _lexicalFeaturesWithRepresentativenessOfSourceClass,
//				   _lexicalFeaturesWithRepresentativenessOfTargetClass,
//				   _negativeMappingsInThisCorrectionCluster,
//				   _negativeMappingSetsWithLocalRateToCorrectionSourceClass,
//				   _negativeMappingSetsWithLocalRateToCorrectionTargetClass,
//				   _positiveMappingSetsWithLocalRateToCorrectionTargetClass
//				   );
//		
//		ClassificationCorrectionRule correctionRule = new ClassificationCorrectionRule(
//																					   _correctionSourceClassSet,
//																					   _correctionTargetClass,
//																					   featureMatcher
//																					   );
//		return correctionRule;
//	}
	
	public String getClusterCode(){
		return this._clusterCode;
	}
	
	@Override
	public int hashCode(){
		return this._clusterCode.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ClassificationCorrectionCluster cluster = (ClassificationCorrectionCluster) obj;
		
		if (this.getClusterCode().equals(cluster.getClusterCode())) {
			return true;
		} else {
			return false;
		}
	}
}
