package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;

public interface IManufacturingLexicalMappingRecordsReader {

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

	public MappingRelationType getConcept2OntClassMappingRelation(Concept concept, OntoClassInfo ontClass);

	public void showRepositoryDetail();

	public void showLexiconIndex();

	/**
	 * 
	 * @param concept
	 * @param ontoClass
	 * @return
	 * @throws NoSuchEntryItemException
	 */
	public IConcept2OntClassMappingStatistics getConcept2ClassMappingStatistics(Concept concept, OntoClassInfo ontoClass) throws NoSuchEntryItemException;
	
	public double getConcept2OntClassMappingSimilarity(Concept concept, OntoClassInfo ontClass);

	Map<String, Map<String, String>> getMappedOntClassDetailInformation(String conceptName);
	
}
