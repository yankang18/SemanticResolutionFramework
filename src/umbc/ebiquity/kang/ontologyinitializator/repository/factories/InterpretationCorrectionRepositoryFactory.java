package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;

import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.AggregratedClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRecordParser;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordParser;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class InterpretationCorrectionRepositoryFactory {
	public static IClassificationCorrectionRepository createAggregratedClassificationCorrectionRepository() throws IOException {
		IClassificationCorrectionRecordParser classificationCorrectionRecordParser = new ClassificationCorrectionRecordParser();
		IClassificationCorrectionRepository repo = new AggregratedClassificationCorrectionRepository(classificationCorrectionRecordParser);
		repo.loadRepository();
		return repo;
	}

	public static IClassificationCorrectionRepository createProprietaryClassificationCorrectionRepository(String repositoryName) throws IOException { 
		String directory = RepositoryParameterConfiguration.getInterpretationCorrectionDirectoryFullPath();
		String fileFullName = directory + repositoryName;

		if (FileUtility.exists(fileFullName)) {
			IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
			IClassificationCorrectionRecordParser classificationCorrectionRecordParser = new ClassificationCorrectionRecordParser();
			IClassificationCorrectionRepository repository = new ProprietoryClassificationCorrectionRepository(fileFullName, ontologyRepository, classificationCorrectionRecordParser);
			boolean succeed = repository.loadRepository();
			if (succeed) {
				return repository;
			} else {
				throw new IOException("Load Classification Correction Repository Failed");
			}
		} else {
			if (FileUtility.exists(directory)) {
				return createRepository(fileFullName);
			} else {

				boolean succeed = FileUtility.createDirectories(directory);
				if (succeed) {
					return createRepository(fileFullName);
				} else {
					throw new IOException("Create Directories for Classification Correction Repository Failed");
				}
			}
		}
	}

	private static IClassificationCorrectionRepository createRepository(String fileFullName) throws IOException {
		boolean succeed = FileUtility.createFile(fileFullName);
		if (succeed) {
			IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
			IClassificationCorrectionRecordParser classificationCorrectionRecordParser = new ClassificationCorrectionRecordParser();
			return new ProprietoryClassificationCorrectionRepository(fileFullName, ontologyRepository, classificationCorrectionRecordParser);
		} else {
			throw new IOException("Create Classification Correction Repository Failed");
		}
	}
}
