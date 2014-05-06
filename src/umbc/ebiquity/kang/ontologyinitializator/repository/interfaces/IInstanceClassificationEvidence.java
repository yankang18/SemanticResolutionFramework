package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IInstanceClassificationEvidence { 

	public OntoClassInfo getCorrectionTargetClass();

	OntoClassInfo getCorrectionSourceClass();

	String getEvidenceCode();

//	CorrectionDirection getCorrectionDirection();

	Set<IConcept2OntClassMapping> getConcept2OntClassMappingMembers();

}
