package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class ClassificationCorrectionRepositoryFactory {
	
	public static IClassificationCorrectionRepository createRepository() throws IOException { 
		String directory = RepositoryParameterConfiguration.getClassificationCorrectionDirectoryFullPath();
		String fileFullName = directory + RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_REPOSITORY_NAME;
		String negativeMappingFileFullName = directory + RepositoryParameterConfiguration.NEGATIVE_CONCEPT_CLASS_MAPPING;
		String positiveMappingFileFullName = directory + RepositoryParameterConfiguration.POSITIVE_CONCEPT_CLASS_MAPPING;
		String allMappingFileFullName = directory + RepositoryParameterConfiguration.All_CONCEPT_CLASS_MAPPING;

		if (FileUtility.exists(fileFullName) && FileUtility.exists(negativeMappingFileFullName) && FileUtility.exists(positiveMappingFileFullName) && FileUtility.exists(allMappingFileFullName) ) {
			IClassificationCorrectionRepository repository = new ClassificationCorrectionRepository(OntologyRepositoryFactory.createOntologyRepository());
			boolean succeed = repository.loadRepository();
			if (succeed) {
				return repository;
			} else {
				throw new IOException("Load Classification Correction Repository Failed");
			}
		} else {
			if (FileUtility.exists(directory)) {
				return createRepository(fileFullName, negativeMappingFileFullName, positiveMappingFileFullName, allMappingFileFullName);
			} else {

				boolean succeed = FileUtility.createDirectories(directory);
				if (succeed) {
					return createRepository(fileFullName, negativeMappingFileFullName, positiveMappingFileFullName, allMappingFileFullName);
				} else {
					throw new IOException("Create Directories for Classification Correction Repository Failed");
				}
			}
		}
	}

	private static IClassificationCorrectionRepository createRepository(String fileFullName1, String fileFullName2, String fileFullName3, String fileFullName4) throws IOException {
		boolean succeed1 = FileUtility.createFile(fileFullName1);
		boolean succeed2 = FileUtility.createFile(fileFullName2);
		boolean succeed3 = FileUtility.createFile(fileFullName3);
		boolean succeed4 = FileUtility.createFile(fileFullName4);
		if (succeed1 && succeed2 && succeed3 && succeed4) {
			return new ClassificationCorrectionRepository(OntologyRepositoryFactory.createOntologyRepository());
		} else {
			throw new IOException("Create Classification Correction Repository Failed");
		}
	}
}
