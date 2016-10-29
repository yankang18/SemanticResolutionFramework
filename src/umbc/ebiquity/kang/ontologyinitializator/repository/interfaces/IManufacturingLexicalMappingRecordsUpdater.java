package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;

public interface IManufacturingLexicalMappingRecordsUpdater {
	
	public enum MappingVericationResult {Succeed, Failed, Unknow}
	
	public void updateConcept2OntoClassMappingRelation(
			Concept concept, 
			OntoClassInfo ontoClass, 
			MappingRelationType mappingRelationType
	);

	public void updateConcept2OntoClassMappingVerificationResult(
			Concept concept, 
			OntoClassInfo ontoClass,
			MappingVericationResult 
			verificationResult
	);

	void updateValidityOfConcept2OntClassMapping(IInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance);

	void addNewConcept2OntoClassMappings(Collection<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList);

	void addNewConcept2OntoClassMapping(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity);
}
