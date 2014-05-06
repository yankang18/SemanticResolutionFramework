package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public interface ICorrectionRule {

//	String getTargetClass(IClassifiedInstanceDetailRecord instance);

	String obtainCorrectedClassLabel(IClassifiedInstanceDetailRecord instance, String originalClass);

	void showDetail();  

}
