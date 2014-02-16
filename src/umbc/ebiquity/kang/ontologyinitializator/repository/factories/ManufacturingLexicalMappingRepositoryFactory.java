package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class ManufacturingLexicalMappingRepositoryFactory {
	
	public static IManufacturingLexicalMappingRepository createManufacturingLexiconRepository() throws IOException { 
		String directory = RepositoryParameterConfiguration.getManufacturingLexiconDirectoryFullPath();
		String fileFullName = directory + RepositoryParameterConfiguration.MANUFACTURING_LEXICON_NAME;

		if (FileUtility.exists(fileFullName)) {
			IManufacturingLexicalMappingRepository repository = new ManufacturingLexicalMappingRepository(OntologyRepositoryFactory.createOntologyRepository());
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
			return new ManufacturingLexicalMappingRepository(OntologyRepositoryFactory.createOntologyRepository());
		} else {
			throw new IOException("Create Manufacturing Lexicon Repository Failed");
		}
	}

}
