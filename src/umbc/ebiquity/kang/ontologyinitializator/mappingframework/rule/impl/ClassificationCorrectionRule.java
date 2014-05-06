package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IFeatureMatcher;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public class ClassificationCorrectionRule implements IClassificationCorrectionRule {
	
	private Set<String> _sourceClassSet;
	private String _targetClassName;
	private IFeatureMatcher _featureWrapper;
	public ClassificationCorrectionRule(Set<String> sourceClassSet,  
			                            String targetClassName,
			                            IFeatureMatcher featureWrapper) {
		this._sourceClassSet = sourceClassSet;
		this._targetClassName = targetClassName;
		this._featureWrapper = featureWrapper;
	}
	
	@Override
	public double getApplicability(IClassifiedInstanceDetailRecord instance){
		return _featureWrapper.match(instance);
	}
	
	@Override
	public boolean apply(IClassifiedInstanceDetailRecord instance) {
		return false;
	}
	
	@Override
	public String toString(){
		return "CORRECTION RULE: " + this.getRuleString(); 
	}
	
	private String getRuleString(){
		return this._sourceClassSet + " -> " + this._targetClassName; 
	}
	
	@Override
    public String getTargetOntClassName(){
    	return _targetClassName;
    }
}
