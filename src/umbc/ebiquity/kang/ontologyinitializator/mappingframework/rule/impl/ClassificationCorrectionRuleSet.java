package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRuleSelectionResult;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IClassificationCorrectionRuleSet;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public class ClassificationCorrectionRuleSet implements IClassificationCorrectionRuleSet {

	private Collection<IClassificationCorrectionRule> _correctionRules;

	public static IClassificationCorrectionRuleSet createInstance(Collection<IClassificationCorrectionRule> correctionRules){
		return new ClassificationCorrectionRuleSet(correctionRules);
	}
	
	private ClassificationCorrectionRuleSet(Collection<IClassificationCorrectionRule> correctionRules) {
		this._correctionRules = correctionRules;
	}
	
	@Override
	public Collection<IClassificationCorrectionRule> getRules() {
		return _correctionRules;
	}

	@Override
	public IClassificationCorrectionRuleSelectionResult selectBestRule(IClassifiedInstanceDetailRecord classifiedInstance) {
		if(_correctionRules.size() == 0) return new BestClassificationCorrectionRule(null, 0.0);;
		
		double denominator = 0.0;
		double bestApplicability = 0.0;
		IClassificationCorrectionRule bestRule = null;
		for (IClassificationCorrectionRule rule : _correctionRules) {
			double applicability = rule.getApplicability(classifiedInstance);
			denominator += applicability;
			System.out.println(rule + " with applicability <" + applicability + ">");

			if (applicability > bestApplicability) {
				bestApplicability = applicability;
				bestRule = rule;
			}
		}

		double applicability = bestApplicability / denominator;
		IClassificationCorrectionRuleSelectionResult bestCorrectionRule = new BestClassificationCorrectionRule(bestRule, applicability);
		return bestCorrectionRule;
	}

}
