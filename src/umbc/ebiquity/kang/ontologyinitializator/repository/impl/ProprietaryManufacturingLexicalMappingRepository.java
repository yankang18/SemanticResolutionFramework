package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.RuleEngine;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class ProprietaryManufacturingLexicalMappingRepository implements IManufacturingLexicalMappingRepository {

	private String _repositoryFullName;
	private IManufacturingLexicalMappingRecordsAccessor _manufacturingLexicalMappingRecordsAccessor;
//	private static final String MANUFACTURING_LEXICON_DIRECTROY_FULL_PATH = RepositoryParameterConfiguration.getManufacturingLexiconDirectoryFullPath();

	public ProprietaryManufacturingLexicalMappingRepository(String fileFullName, 
															   IOntologyRepository ontologyRepository,
															   IManufacturingLexicalMappingRecordsAccessor recordAggregator) {
		this._repositoryFullName = fileFullName;
		this._manufacturingLexicalMappingRecordsAccessor = recordAggregator;
	}

	/**
	 * Load all the data from the Manufacturing Lexicon Repository
	 */
	public boolean loadRepository() {
		System.out.println("Load Manufacturing Lexicon Repository");
		return loadRecords(_repositoryFullName);
	}

	private boolean loadRecords(String fileFullName) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadRecord(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	protected void loadRecord(String line) { 
		_manufacturingLexicalMappingRecordsAccessor.parseRecord(line);
	}
	
	@Override
	public boolean hasConcept(Concept concept){
		return _manufacturingLexicalMappingRecordsAccessor.hasConcept(concept);
	}

	@Override
	public boolean hasConcept2OntoClassMapping(Concept concept, OntoClassInfo ontoClass) {
		return _manufacturingLexicalMappingRecordsAccessor.hasConcept2OntoClassMapping(concept, ontoClass);
	}

	@Override
	public void addNewConcept2OntoClassMappings(Collection<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList) {
		_manufacturingLexicalMappingRecordsAccessor.addNewConcept2OntoClassMappings(classifiedInstanceDetailInfoList);
	}
	
	@Override
	public void addNewConcept2OntoClassMapping(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity) {
		_manufacturingLexicalMappingRecordsAccessor.addNewConcept2OntoClassMapping(concept, mappingRelationType, ontoClass, similarity);
	}
	
	@Override
	public void updateValidityOfConcept2OntClassMapping(IInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance) {
		_manufacturingLexicalMappingRecordsAccessor.updateValidityOfConcept2OntClassMapping(updatedInstance, originalInstance);
	}
	
	@Override
	public void updateConcept2OntoClassMappingRelation(Concept concept, OntoClassInfo ontoClass, MappingRelationType mappingRelationType) {
		_manufacturingLexicalMappingRecordsAccessor.updateConcept2OntoClassMappingRelation(concept, ontoClass, mappingRelationType);
	}
	
	@Override
	public void updateConcept2OntoClassMappingVerificationResult(
			                                                      Concept concept, 
			                                                      OntoClassInfo ontoClass,
			                                                      MappingVericationResult verificationResult
			                                                     ) {
		_manufacturingLexicalMappingRecordsAccessor.updateConcept2OntoClassMappingVerificationResult(concept, ontoClass, verificationResult);
	}
	
	@Override
	public Collection<String> getAllConcepts(){
		return _manufacturingLexicalMappingRecordsAccessor.getAllConcepts();
	}
	
	@Override
	public Collection<OntoClassInfo> getMappedOntClasses(String conceptName) {
		return _manufacturingLexicalMappingRecordsAccessor.getMappedOntClasses(conceptName);
	}

	@Override
	public Collection<OntoClassInfo> getMappedOntClasses(Concept concept) {
		return _manufacturingLexicalMappingRecordsAccessor.getMappedOntClasses(concept.getConceptName());
	}

	@Override
	public MappingRelationType getConcept2OntClassMappingRelation(Concept concept, OntoClassInfo ontClass){
		return _manufacturingLexicalMappingRecordsAccessor.getConcept2OntClassMappingRelation(concept, ontClass);
	}
	
	@Override
	public double getConcept2OntClassMappingSimilarity(Concept concept, OntoClassInfo ontClass){
		return _manufacturingLexicalMappingRecordsAccessor.getConcept2OntClassMappingSimilarity(concept, ontClass);
	}
	
	@Override
	public IConcept2OntClassMappingStatistics getConcept2ClassMappingStatistics(Concept concept, OntoClassInfo ontoClass) throws NoSuchEntryItemException{
		return _manufacturingLexicalMappingRecordsAccessor.getConcept2ClassMappingStatistics(concept, ontoClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean saveRepository() {
		
		StringBuilder entries = new StringBuilder();
		for (String conceptName : _manufacturingLexicalMappingRecordsAccessor.getAllConcepts()) {
			Map<String, Map<String, String>> mappedOntoClassDetails = _manufacturingLexicalMappingRecordsAccessor.getMappedOntClassDetailInformation(conceptName);
			JSONObject jsonDetailRecord = new JSONObject();
			jsonDetailRecord.put(MappingInfoSchemaParameter.CONCEPT_NAME, conceptName);
			jsonDetailRecord.put(MappingInfoSchemaParameter.MAPPED_ONTOCLASS_LIST, mappedOntoClassDetails);
			entries.append(jsonDetailRecord.toJSONString());
			entries.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		return FileAccessor.saveTripleString(this._repositoryFullName, entries.toString());
	}

	@Override
	public void showLexiconIndex() {
		_manufacturingLexicalMappingRecordsAccessor.showLexiconIndex();
	}

	@Override
	public void showRepositoryDetail() {
		_manufacturingLexicalMappingRecordsAccessor.showRepositoryDetail();	
	}

	@Override
	public Map<String, Map<String, String>> getMappedOntClassDetailInformation(String conceptName) {
		return _manufacturingLexicalMappingRecordsAccessor.getMappedOntClassDetailInformation(conceptName);
	}
}
