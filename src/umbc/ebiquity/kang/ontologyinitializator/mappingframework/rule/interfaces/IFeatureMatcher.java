package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;

public interface IFeatureMatcher {

	public double match(IClassifiedInstanceDetailRecord instance);
}
