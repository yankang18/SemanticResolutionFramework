package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;

public class AggregratedManufacturingLexicalMappingRepository extends AbstractRepositoryBatchLoader implements
		IManufacturingLexicalMappingRecordsReader {

	IManufacturingLexicalMappingRecordsAccessor _recordsAccessor;
	public AggregratedManufacturingLexicalMappingRepository(IManufacturingLexicalMappingRecordsAccessor recordsAccessor) {
		super(FileRepositoryParameterConfiguration.getManufacturingLexiconDirectoryFullPath());
		_recordsAccessor = recordsAccessor;
	}

	@Override
	public boolean hasConcept(Concept concept) {
		return _recordsAccessor.hasConcept(concept);
	}

	@Override
	public boolean hasConcept2OntoClassMapping(Concept concept, OntoClassInfo ontoClass) {
		return _recordsAccessor.hasConcept2OntoClassMapping(concept, ontoClass);
	}

	@Override
	public Collection<OntoClassInfo> getMappedOntClasses(Concept concept) {
		return _recordsAccessor.getMappedOntClasses(concept);
	}

	@Override 
	public Collection<OntoClassInfo> getMappedOntClasses(String conceptName) {
		return _recordsAccessor.getMappedOntClasses(conceptName);
	}

	@Override
	public Collection<String> getAllConcepts() {
		return _recordsAccessor.getAllConcepts();
	}

	@Override
	public MappingRelationType getConcept2OntClassMappingRelation(Concept concept, OntoClassInfo ontClass) {
		return _recordsAccessor.getConcept2OntClassMappingRelation(concept, ontClass);
	}

	@Override
	public void showRepositoryDetail() {
		_recordsAccessor.showRepositoryDetail();
	}

	@Override
	public void showLexiconIndex() {
		_recordsAccessor.showLexiconIndex();
	}

	@Override
	public double getConcept2OntClassMappingSimilarity(Concept concept, OntoClassInfo ontClass) {
		return _recordsAccessor.getConcept2OntClassMappingSimilarity(concept, ontClass);
	}

	@Override
	public Map<String, Map<String, String>> getMappedOntClassDetailInformation(String conceptName) {
		return _recordsAccessor.getMappedOntClassDetailInformation(conceptName);
	}

	@Override
	protected void loadRecord(String line) {
		_recordsAccessor.parseRecord(line);
	}

	@Override
	public IConcept2OntClassMappingStatistics getConcept2ClassMappingStatistics(Concept concept, OntoClassInfo ontoClass)
			throws NoSuchEntryItemException {
		return _recordsAccessor.getConcept2ClassMappingStatistics(concept, ontoClass);
	}

}
