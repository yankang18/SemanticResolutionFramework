package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import java.io.InputStream;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.OntologyModel;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class OntologyRepositoryFactory {
	
	public static IOntologyRepository createOntologyRepository() throws IOException {
		
		String directory = FileRepositoryParameterConfiguration.getOntologyIndexFilesDirectoryFullPath();
		String classRecordsFileFullName = directory + FileRepositoryParameterConfiguration.ONTOLOGY_CLASS_RECORDS_FILENAME;
		String propertyRecoredFileFullName = directory + FileRepositoryParameterConfiguration.ONTOLOGY_PROPERTY_RECORDS_FILENAME;
		String indexFileFullName = directory + FileRepositoryParameterConfiguration.ONTOLOGY_CODED_CLASS_RECORDS_FILENAME;
		
		boolean exists1 = FileUtility.exists(classRecordsFileFullName);
		boolean exists2 = FileUtility.exists(propertyRecoredFileFullName);
		boolean exists3 = FileUtility.exists(indexFileFullName);

		if (exists1 && exists2 && exists3) {

			IOntologyRepository ontologyRepository = new OntologyModel();
			boolean succeed = ontologyRepository.loadRepository();
			if (succeed) {
				return ontologyRepository;
			} else {
				throw new IOException("Load Ontology Repository Failed");
			}
		} else {

			if (FileUtility.exists(directory)) {
				return createRepostory(directory);

			} else {
				System.out.println("DIRECTORY: " + directory);
				boolean succeed = FileUtility.createDirectories(directory);
				if (succeed) {
					return createRepostory(directory);
				} else {
					throw new IOException("Create Directories for Ontology Repository Failed");
				}
			}
		}
	}
	
	private static IOntologyRepository createRepostory(String ontologyRepositoryDirectoryFullName) throws IOException {
		
		String classRecordsFileFullName = ontologyRepositoryDirectoryFullName + FileRepositoryParameterConfiguration.ONTOLOGY_CLASS_RECORDS_FILENAME;
		String propertyRecoredFileFullName = ontologyRepositoryDirectoryFullName + FileRepositoryParameterConfiguration.ONTOLOGY_PROPERTY_RECORDS_FILENAME;
		String indexFileFullName = ontologyRepositoryDirectoryFullName + FileRepositoryParameterConfiguration.ONTOLOGY_CODED_CLASS_RECORDS_FILENAME;
		
		boolean exists1 = FileUtility.exists(classRecordsFileFullName);
		boolean exists2 = FileUtility.exists(propertyRecoredFileFullName);
		boolean exists3 = FileUtility.exists(indexFileFullName);
		
		
		if(!exists1){
			boolean succeed = FileUtility.createFile(classRecordsFileFullName);
			if(!succeed){
				throw new IOException("Create Ontology Repository Failed");
			}
		}
		
		if(!exists2){
			boolean succeed = FileUtility.createFile(propertyRecoredFileFullName);
			if(!succeed){
				throw new IOException("Create Ontology Repository Failed");
			}
		}
		
		if(!exists3){
			boolean succeed = FileUtility.createFile(indexFileFullName);
			if (!succeed) {
				throw new IOException("Create Ontology Repository Failed");
			}
		}

		InputStream instream = FileManager.get().open(FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH);
		Model model = ModelFactory.createDefaultModel();
		model.read(instream, "");
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
		IOntologyRepository ontologyRepository = new OntologyModel(ontModel);
		boolean succeed = ontologyRepository.saveRepository();
		if (succeed) {
			return ontologyRepository;
		} else {
			throw new IOException("Create Ontology Repository Failed");
		}
	}
}
