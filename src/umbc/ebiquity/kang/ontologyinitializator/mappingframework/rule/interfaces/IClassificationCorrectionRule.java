package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public interface IClassificationCorrectionRule {

	double getApplicability(IClassifiedInstanceDetailRecord instance);

	boolean apply(IClassifiedInstanceDetailRecord instance);

	String getTargetOntClassName();

}
