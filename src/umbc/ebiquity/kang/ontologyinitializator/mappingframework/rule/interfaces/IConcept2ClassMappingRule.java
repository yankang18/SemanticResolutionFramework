package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;

public interface IConcept2ClassMappingRule extends IRule {
	public void prepare(String instanceClassName, IConcept2OntClassMapping newMap, IConcept2OntClassMapping oldMap);
}
