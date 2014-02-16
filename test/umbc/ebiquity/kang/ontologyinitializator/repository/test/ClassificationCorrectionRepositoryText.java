package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassificationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IUpdatedInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;

public class ClassificationCorrectionRepositoryText {

	private static IClassificationCorrectionRepository _correctionRepository;
	private static IOntologyRepository _ontologyRepository;

	@BeforeClass
	public static void init() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		
//		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
//		_correctionRepository = new ClassificationCorrectionRepository(_ontologyRepository);
	}
	
	@Test
	public void createClassificationCorrectionRepositoryTest() throws IOException{
		// TODO: should first delete all the related correction repositories
		_correctionRepository = ClassificationCorrectionRepositoryFactory.createRepository();
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IUpdatedInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			_correctionRepository.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		_correctionRepository.saveRepository();
	}

	@Ignore
	@Test
	public void ExtractCorrectionTest() throws IOException { 
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Collection<IClassificationCorrection> corrections = new ArrayList<IClassificationCorrection>();
		Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IUpdatedInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			corrections.addAll(_correctionRepository.extractCorrection(updatedInstanceRecord, originalClassifiedInstance));
		}
	}

//	@Ignore
	@Test
	public void GetAllConcept2OntClassMappings() {
		Collection<IInstanceMembershipInfereceFact> allc2cMappingSets = _correctionRepository.getAllInstanceMembershipInferenceFacts();
		print(allc2cMappingSets, "Mapping: ");
		System.out.println();
	}

//	@Ignore
	@Test
	public void GetNegativeConcept2OntClassMappings() {
		Collection<IInstanceMembershipInfereceFact> negativeC2CMappingSets = _correctionRepository.getHiddenInstanceMembershipInferenceFacts();
		print(negativeC2CMappingSets, "Negative Mapping: ");
		System.out.println();
	}

//	@Ignore
	@Test
	public void GetPositiveConcept2OntClassMappings() {
		Collection<IInstanceMembershipInfereceFact> positiveC2CMappingSets = _correctionRepository.getExplicitInstanceMembershipInferenceFacts();
		print(positiveC2CMappingSets, "Positive Mapping: ");
		System.out.println();
	}
	
	private void print(Collection<IInstanceMembershipInfereceFact> mappingSets, String string) {
		for (IInstanceMembershipInfereceFact mappingSet : mappingSets) {
			System.out.println(string + mappingSet.getMembershipInferenceFactCode() + " " + mappingSet.getCorrectionTargetClass().getOntClassName());
		}
	}
	
}
