package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;

public interface ICorrectionClusterCodeGenerator {
	
	public String generateCorrectionClusterCode(CorrectionDirection correctionDirection);

}
