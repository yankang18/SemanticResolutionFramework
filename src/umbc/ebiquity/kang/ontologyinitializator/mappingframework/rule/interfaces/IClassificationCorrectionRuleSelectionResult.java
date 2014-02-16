package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

public interface IClassificationCorrectionRuleSelectionResult {
	
	IClassificationCorrectionRule getRule();
	double getApplicability();
	boolean hasRule();

}
