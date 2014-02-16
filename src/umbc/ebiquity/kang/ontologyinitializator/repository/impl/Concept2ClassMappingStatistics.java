package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;

public class Concept2ClassMappingStatistics implements IConcept2OntClassMappingStatistics {

	private int _verificationAttempts;
	private int _undeterminedCounts;
	private int _succeedAttempts;
	private int _failedAttempts;
	private double _similarity;
	
	@Override
	public int getVerificationAttempts(){
		return _verificationAttempts;
	}
	
	@Override
	public int getUndeterminedCounts() {
		return _undeterminedCounts;
	}

	@Override
	public int getSucceedCounts() {
		return _succeedAttempts;
	}

	@Override
	public int getFailedCounts() {
		return _failedAttempts;
	}

	@Override
	public double getSimilarity() {
		return _similarity;
	}
	
	
//	public void setMappingAttempts(int attempts) {
//		this._mappingAttempts = attempts;
//	}

	public void setSucceedCounts(int attempts) {
		this._succeedAttempts = attempts;
	}

	public void setFailedCounts(int attempts) {
		this._failedAttempts = attempts;
	}

	public void setSimilarity(double similarity) {
		this._similarity = similarity;
	} 

	public void setUndeterminedCounts(int undeterminedCounts) {
		_undeterminedCounts = undeterminedCounts;
	}

	public void setVerificationAttempts(int verificationAttempts) {
		_verificationAttempts = verificationAttempts;
	}

}
