package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;
import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.RuleEngine;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository.MappingVericationResult;

public interface IManufacturingLexicalMappingRepository {
	
	/**
	 * To check if a particular concept has already existed in the Manufacturing Lexicon
	 * 
	 * @param concept - the concept object
	 * @return true if this concept exists in the ML. Otherwise return false.
	 */
	public boolean hasConcept(Concept concept);

	/**
	 * To check if a particular concept-to-class mapping has already existed in
	 * the Manufacturing Lexicon
	 * 
	 * @param concept - the concept object
	 * @param ontoClass - the ontology class object
	 * @return true if this particular concept-to-class mapping has already
	 *         existed in the Lexicon. Otherwise return false
	 */
	public boolean hasConcept2OntoClassMapping(Concept concept, OntoClassInfo ontoClass);

	/**
	 * 
	 * @param concept
	 * @return
	 */
	public Collection<OntoClassInfo> getMappedOntClasses(Concept concept);
	public Collection<OntoClassInfo> getMappedOntClasses(String conceptName); 
	public Collection<String> getAllConcepts();
	/**
	 * 
	 * @param concept
	 * @param ontoClass
	 * @return
	 * @throws NoSuchEntryItemException
	 */
	public IConcept2OntClassMappingStatistics getConcept2ClassMappingStatistics(Concept concept, OntoClassInfo ontoClass) throws NoSuchEntryItemException;
	
	public double getConcept2OntClassMappingSimilarity(Concept concept, OntoClassInfo ontClass);
	
//	public double getConcept2ClassMappingScore(Concept concept, OntoClassInfo ontoClass);

	public MappingRelationType getConcept2OntClassMappingRelation(Concept concept, OntoClassInfo ontClass);

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

	void updateValidityOfConcept2OntClassMapping(IUpdatedInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance);

	void addNewConcept2OntoClassMappings(Collection<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList);

	void addNewConcept2OntoClassMapping(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity);

	public boolean saveRepository();

	public boolean loadRepository();
	
	public void showRepositoryDetail();
	
	public void showLexiconIndex();
}
