package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRuleSelectionResult;

public class BestClassificationCorrectionRule implements IClassificationCorrectionRuleSelectionResult {

	private IClassificationCorrectionRule _rule;
	private double _applicability;
	public BestClassificationCorrectionRule(IClassificationCorrectionRule rule, double applicability){
		_rule = rule;
		_applicability = applicability;
	}
	
	@Override
	public boolean hasRule(){
		if(_rule == null) return false;
		return true;
	}
	
	@Override
	public IClassificationCorrectionRule getRule() {
		return _rule;
	}

	@Override
	public double getApplicability() {
		return _applicability;
	}

}
