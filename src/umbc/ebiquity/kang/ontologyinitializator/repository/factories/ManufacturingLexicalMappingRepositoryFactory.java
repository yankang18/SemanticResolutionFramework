package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.RuleEngine;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.AggregratedManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietaryManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IReadOnlyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class ManufacturingLexicalMappingRepositoryFactory { 
	
	public static IManufacturingLexicalMappingRecordsReader createAggregratedManufacturingLexicalMappingRepository(IOntologyRepository ontologyRepository) throws IOException {
		IManufacturingLexicalMappingRecordsReader reader = new AggregratedManufacturingLexicalMappingRepository(new ManufacturingLexicalMappingRecordsAccessor(new RuleEngine(ontologyRepository)));
		((IReadOnlyRepository) reader).loadRepository();
		return reader;
	}
	
	public static IManufacturingLexicalMappingRepository createProprietaryManufacturingLexiconRepository(String repositoryName) throws IOException { 
		String directory = FileRepositoryParameterConfiguration.getManufacturingLexiconDirectoryFullPath();
		String fileFullName = directory + repositoryName;

		if (FileUtility.exists(fileFullName)) {
			System.out.println("load + " + fileFullName);
			IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
			IManufacturingLexicalMappingRepository repository = new ProprietaryManufacturingLexicalMappingRepository(fileFullName, 
																													 ontologyRepository,
																													 new ManufacturingLexicalMappingRecordsAccessor(new RuleEngine(ontologyRepository)));
			boolean succeed = repository.loadRepository();
			if (succeed) {
				return repository;
			} else {
				throw new IOException("Load Manufacturing Lexicon Repository Failed");
			}
		} else {
			if (FileUtility.exists(directory)) {
				return createRepository(fileFullName);
			} else {

				boolean succeed = FileUtility.createDirectories(directory);
				if (succeed) {
					return createRepository(fileFullName);
				} else {
					throw new IOException("Create Directories for Manufacturing Lexicon Repository Failed");
				}
			}
		}
	}

	private static IManufacturingLexicalMappingRepository createRepository(String fileFullName) throws IOException {
		boolean succeed = FileUtility.createFile(fileFullName);
		if (succeed) {
			IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
			IManufacturingLexicalMappingRepository repository = new ProprietaryManufacturingLexicalMappingRepository(
					 fileFullName, 
					 ontologyRepository,
					 new ManufacturingLexicalMappingRecordsAccessor(new RuleEngine(ontologyRepository)));
			return repository;
		} else {
			throw new IOException("Create Manufacturing Lexicon Repository Failed");
		}
	}

}
