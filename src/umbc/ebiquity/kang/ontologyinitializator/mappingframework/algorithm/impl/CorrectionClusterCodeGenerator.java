package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;

public class CorrectionClusterCodeGenerator implements ICorrectionClusterCodeGenerator {
	
	@Override
	public String generateCorrectionClusterCode(CorrectionDirection correctionDirection) {
		List<String> sourceClassList = new ArrayList<String>(correctionDirection.getSourceClassSet());
		Collections.sort(sourceClassList);
		StringBuilder sb = new StringBuilder();
		for (String sourceClass : sourceClassList) {
			sb.append(this.wrapClassName(sourceClass));
		}
		String targetClass = this.wrapClassName(correctionDirection.getTargetClassName());
		return sb.toString() + "@" + targetClass;
	}
	
	private String wrapClassName(String className) {
		return "<" + className + ">";
	}

}
