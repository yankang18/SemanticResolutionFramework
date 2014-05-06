package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;

public class CorrectionClusterFeatureWrapper {
	
	private Set<String> _correctionSourceClassSet;
	private String _correctionTargetClass;
//	private Collection<LexicalFeature> _correctedInstanceLexicalFeatures;
//	private Map<LexicalFeature, Double> _sourceClassInstanceLexicalFeatures;
//	private Map<IInstanceClassificationEvidence, Double> _negativeMappingInCorrectionCluster;
//	private Map<IInstanceClassificationEvidence, Double> _negativeMappingSetsWithLocalRateToCorrectionSourceClass;
	
	// Currently, only use the following four features
	private double _instanceRateOfTargetClass;
	private int _instanceNumberOfTargetClass;
	private int _positiveEvidenceNumberOfTargetClass;
	private int _negativeEvidenceNumberOfTargetClass;
	private int _numberOfC2CMappingsOfTargetClass; 
	private Map<LexicalFeature, Double> _targetClassInstanceLexicalFeatures;
	private Map<IInstanceClassificationEvidence, Double> _negativeMappingSetsWithLocalRateToCorrectionTargetClass;
	private Map<IInstanceClassificationEvidence, Double> _positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	private Map<IConcept2OntClassMapping, Double> _c2cMappingWithLocalRateToCorrectionTargetClass;
	
	public CorrectionClusterFeatureWrapper
	    (
	    	Set<String> correctionSourceClassSet,
	    	String correctionTargetClass,
	    	double instanceRateOfTargetClass,
	    	int instanceNumberOfTargetClass,
//			Collection<LexicalFeature> correctedInstanceLexicalFeatures,
//			Map<LexicalFeature, Double> sourceClassInstanceLexicalFeatures, 
			Map<LexicalFeature, Double> targetClassInstanceLexicalFeatures,
//			Map<IInstanceClassificationEvidence, Double> negativeMappingInCorrectionCluster,
//			Map<IInstanceClassificationEvidence, Double> negativeMappingSetsWithLocalRateToCorrectionSourceClass,
			Map<IInstanceClassificationEvidence, Double> negativeMappingSetsWithLocalRateToCorrectionTargetClass,
			Map<IInstanceClassificationEvidence, Double> positiveMappingSetsWithLocalRateToCorrectionTargetClass,
			Map<IConcept2OntClassMapping, Double> c2cMappingWithLocalRateToCorrectionTargetClass, 
			int positiveEvidenceNumberOfTargetClass,
			int negativeEvidenceNumberOfTargetClass, 
			int numberOfC2CMappingsOfTargetClass 
		) 
	{
		this._correctionSourceClassSet = correctionSourceClassSet;
		this._correctionTargetClass = correctionTargetClass;
		this._instanceRateOfTargetClass = instanceRateOfTargetClass;
		this._instanceNumberOfTargetClass = instanceNumberOfTargetClass;
//		this._correctedInstanceLexicalFeatures = correctedInstanceLexicalFeatures;
//		this._sourceClassInstanceLexicalFeatures = sourceClassInstanceLexicalFeatures;
		this._targetClassInstanceLexicalFeatures = targetClassInstanceLexicalFeatures;
//		this._negativeMappingInCorrectionCluster = negativeMappingInCorrectionCluster;
//		this._negativeMappingSetsWithLocalRateToCorrectionSourceClass = negativeMappingSetsWithLocalRateToCorrectionSourceClass;
		this._negativeMappingSetsWithLocalRateToCorrectionTargetClass = negativeMappingSetsWithLocalRateToCorrectionTargetClass;
		this._positiveMappingSetsWithLocalRateToCorrectionTargetClass = positiveMappingSetsWithLocalRateToCorrectionTargetClass;
		this._c2cMappingWithLocalRateToCorrectionTargetClass = c2cMappingWithLocalRateToCorrectionTargetClass;
		this._positiveEvidenceNumberOfTargetClass = positiveEvidenceNumberOfTargetClass;
		this._negativeEvidenceNumberOfTargetClass = negativeEvidenceNumberOfTargetClass;
		this._numberOfC2CMappingsOfTargetClass = numberOfC2CMappingsOfTargetClass;
	}

	public Map<IInstanceClassificationEvidence, Double> getPositiveMappingSetsWithLocalRateToCorrectionTargetClass() {
		return _positiveMappingSetsWithLocalRateToCorrectionTargetClass;
	}

	public Map<IInstanceClassificationEvidence, Double> getNegativeMappingSetsWithLocalRateToCorrectionTargetClass() {
		return _negativeMappingSetsWithLocalRateToCorrectionTargetClass;
	}
	
	public Map<IConcept2OntClassMapping, Double> getC2CMappingWithLocalRateToCorrectionTargetClass() {
		return _c2cMappingWithLocalRateToCorrectionTargetClass;
	}
	
	public int getNumberOfC2CMappingOfTargetClass(){
		return _numberOfC2CMappingsOfTargetClass;
	}

//	public Map<LexicalFeature, Double> getSourceClassInstanceLexicalFeatures() {
//		return _sourceClassInstanceLexicalFeatures;
//	}

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

	public int getInstanceNumberOfTargetClass(){
		return _instanceNumberOfTargetClass;
	}
	
	public int getNumberOfPositiveMappingOfTargetClass(){
		return this._positiveEvidenceNumberOfTargetClass;
	}
	
	public int getNumberOfNegativeMappingOfTargetClass(){
		return this._negativeEvidenceNumberOfTargetClass;
	}
	
	public void showCorrectionDetails() {
		System.out.println("Target Class: " + _correctionTargetClass);

		for (LexicalFeature lexicalFeature : _targetClassInstanceLexicalFeatures.keySet()) {
			double rate = _targetClassInstanceLexicalFeatures.get(lexicalFeature);
			if (lexicalFeature == null) {
				System.out.println("L  default " + rate);
			} else {
				System.out.println("L  " + lexicalFeature.getLabel() + " " + rate);
			}
		}
		for (IInstanceClassificationEvidence evidence : _positiveMappingSetsWithLocalRateToCorrectionTargetClass.keySet()) {
			double rate = _positiveMappingSetsWithLocalRateToCorrectionTargetClass.get(evidence);
			if (evidence == null) {
				System.out.println("P  default " + rate);
			} else {
				System.out.println("P  " + evidence.getEvidenceCode() + " " + rate);
			}
		}
		
		for(IInstanceClassificationEvidence evidence : _negativeMappingSetsWithLocalRateToCorrectionTargetClass.keySet()){
			double rate = _negativeMappingSetsWithLocalRateToCorrectionTargetClass.get(evidence);
			if (evidence == null) {
				System.out.println("N  default " + rate);
			} else {
				System.out.println("N  " + evidence.getEvidenceCode() + " " + rate);
			}
		}
		
	}
}
