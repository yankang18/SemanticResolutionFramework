package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public interface IClassificationCorrectionRuleSet {

	public Collection<IClassificationCorrectionRule> getRules();

	public IClassificationCorrectionRuleSelectionResult selectBestRule(IClassifiedInstanceDetailRecord classifiedInstance);
}
