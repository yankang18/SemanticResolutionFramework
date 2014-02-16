package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;

public class CorrectionClusterFeatureWrapper {
	
	private Set<String> _correctionSourceClassSet;
	private String _correctionTargetClass;
	private Collection<LexicalFeature> _correctedInstanceLexicalFeatures;
	private Map<LexicalFeature, Double> _sourceClassInstanceLexicalFeatures;
	private Map<LexicalFeature, Double> _targetClassInstanceLexicalFeatures;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingInCorrectionCluster;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingSetsWithLocalRateToCorrectionSourceClass;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeMappingSetsWithLocalRateToCorrectionTargetClass;
	private Map<IInstanceMembershipInfereceFact, Double> _positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	private double _instanceRateOfTargetClass; 
	
	public CorrectionClusterFeatureWrapper
	    (
	    	Set<String> correctionSourceClassSet,
	    	String correctionTargetClass,
	    	double instanceRateOfTargetClass,
			Collection<LexicalFeature> correctedInstanceLexicalFeatures,
			Map<LexicalFeature, Double> sourceClassInstanceLexicalFeatures, 
			Map<LexicalFeature, Double> targetClassInstanceLexicalFeatures,
			Map<IInstanceMembershipInfereceFact, Double> negativeMappingInCorrectionCluster,
			Map<IInstanceMembershipInfereceFact, Double> negativeMappingSetsWithLocalRateToCorrectionSourceClass,
			Map<IInstanceMembershipInfereceFact, Double> negativeMappingSetsWithLocalRateToCorrectionTargetClass,
			Map<IInstanceMembershipInfereceFact, Double> positiveMappingSetsWithLocalRateToCorrectionTargetClass
		) 
	{
		this._correctionSourceClassSet = correctionSourceClassSet;
		this._correctionTargetClass = correctionTargetClass;
		this._instanceRateOfTargetClass = instanceRateOfTargetClass;
		this._correctedInstanceLexicalFeatures = correctedInstanceLexicalFeatures;
		this._sourceClassInstanceLexicalFeatures = sourceClassInstanceLexicalFeatures;
		this._targetClassInstanceLexicalFeatures = targetClassInstanceLexicalFeatures;
		this._negativeMappingInCorrectionCluster = negativeMappingInCorrectionCluster;
		this._negativeMappingSetsWithLocalRateToCorrectionSourceClass = negativeMappingSetsWithLocalRateToCorrectionSourceClass;
		this._negativeMappingSetsWithLocalRateToCorrectionTargetClass = negativeMappingSetsWithLocalRateToCorrectionTargetClass;
		this._positiveMappingSetsWithLocalRateToCorrectionTargetClass = positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	}

	public Map<IInstanceMembershipInfereceFact, Double> getPositiveMappingSetsWithLocalRateToCorrectionTargetClass() {
		return _positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	}

	public Map<IInstanceMembershipInfereceFact, Double> getNegativeMappingSetsWithLocalRateToCorrectionTargetClass() {
		return _negativeMappingSetsWithLocalRateToCorrectionTargetClass;
	}

	public Map<LexicalFeature, Double> getSourceClassInstanceLexicalFeatures() {
		return _sourceClassInstanceLexicalFeatures;
	}

	public Map<LexicalFeature, Double> getTargetClassInstanceLexicalFeatures() {
		return _targetClassInstanceLexicalFeatures;
	}

	public Set<String> getCorrectionSourceClassSet() {
		return _correctionSourceClassSet;
	}

	public String getCorrectionTargetClass() {
		return _correctionTargetClass;
	}
	
	public double getInstanceRatioOfTargetClass(){
		return _instanceRateOfTargetClass;
	}

}
