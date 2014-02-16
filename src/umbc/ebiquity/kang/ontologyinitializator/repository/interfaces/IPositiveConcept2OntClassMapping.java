package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IPositiveConcept2OntClassMapping extends IConcept2OntClassMapping {
	public OntoClassInfo getTargetClass();
	public int getTargetClassHierarchyNumber();
	public void setTargetClass(OntoClassInfo targetClass);
	public void setTargetClassHierarchyNumber(int classHierarchyNumber);

}
