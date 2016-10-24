package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ibm.icu.impl.Assert;

import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassificationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InterpretationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;

public class ClassificationCorrectionRepositoryText {

	private static IClassificationCorrectionRepository _proprietaryIntergretationCorrectionRepository1;
	private static IClassificationCorrectionRepository _proprietaryInterpretationCorrectionRepository2;
	private static IClassificationCorrectionRepository _aggregatedInterpretationCorrectionRepository;
	private static IOntologyRepository _ontologyRepository;

	
	@BeforeClass
	public static void init() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/Test";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		
//		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
//		_correctionRepository = new ClassificationCorrectionRepository(_ontologyRepository);
	}
	
	@Test
	public void getAggregatedClassificationCorrectoinRepository() throws IOException{
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		FileRepositoryParameterConfiguration.CLASSIFIED_INSTANCE_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_Rule_Naive";
		FileRepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_Rule_Naive";
		FileRepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_Rule_Naive";
		IClassificationCorrectionRepository _aggregatedInterpretationCorrectionRepository = InterpretationCorrectionRepositoryFactory.createAggregratedClassificationCorrectionRepository();
//		_aggregatedInterpretationCorrectionRepository.showRepositoryDetail();
		_aggregatedInterpretationCorrectionRepository.showMappingInfo();
	}
	
	@Ignore
	@Test
	public void createClassificationCorrectionRepositoryTest() throws IOException{
		// TODO: should first delete all the related correction repositories
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		String repositoryName1 = "repositoryOne";
		_proprietaryIntergretationCorrectionRepository1 = InterpretationCorrectionRepositoryFactory.createProprietaryClassificationCorrectionRepository(repositoryName1);
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX1 = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords1();
		for (IInstanceRecord updatedInstanceRecord : XXX1.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX1.get(updatedInstanceRecord);
			_proprietaryIntergretationCorrectionRepository1.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		
		String repositoryName2 = "repositoryTwo";
		_proprietaryInterpretationCorrectionRepository2 = InterpretationCorrectionRepositoryFactory.createProprietaryClassificationCorrectionRepository(repositoryName2);
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX2 = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords2();
		for (IInstanceRecord updatedInstanceRecord : XXX2.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX2.get(updatedInstanceRecord);
			_proprietaryInterpretationCorrectionRepository2.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		
		Set<IInstanceClassificationEvidence> evidences = new HashSet<IInstanceClassificationEvidence>();
		for (IInstanceClassificationEvidence evidence : _proprietaryIntergretationCorrectionRepository1
				.getAllInstanceMembershipInferenceFacts()) {
			evidences.add(evidence);
		}

		for (IInstanceClassificationEvidence evidence : _proprietaryInterpretationCorrectionRepository2
				.getAllInstanceMembershipInferenceFacts()) {
			evidences.add(evidence);
		}
		

		_proprietaryIntergretationCorrectionRepository1.saveRepository();
		_proprietaryInterpretationCorrectionRepository2.saveRepository();
		
		_aggregatedInterpretationCorrectionRepository = InterpretationCorrectionRepositoryFactory.createAggregratedClassificationCorrectionRepository();
//		_aggregatedInterpretationCorrectionRepository.loadRepository();
		
		Set<IInstanceClassificationEvidence> evidences2 = new HashSet<IInstanceClassificationEvidence>();
		for (IInstanceClassificationEvidence evidence : _aggregatedInterpretationCorrectionRepository
				.getAllInstanceMembershipInferenceFacts()) {
			evidences2.add(evidence);
		}
		
		boolean equal = true;
		System.out.println("--------------------------------------------------");
		for(IInstanceClassificationEvidence evidence: evidences){
			System.out.println(evidence.getEvidenceCode());
			if(!evidences2.contains(evidence)){
				equal = false;
			}
		}
		
		System.out.println("--------------------------------------------------");
		for (IInstanceClassificationEvidence evidence : evidences2) {
			System.out.println(evidence.getEvidenceCode());
			if (!evidences.contains(evidence)) {
				equal = false;
			}
		}
		assertEquals(true, equal);
	}

	@Ignore
	@Test
	public void ExtractCorrectionTest() throws IOException { 
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Collection<IClassificationCorrection> corrections = new ArrayList<IClassificationCorrection>();
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			corrections.addAll(_proprietaryIntergretationCorrectionRepository1.extractCorrection(updatedInstanceRecord, originalClassifiedInstance));
		}
	}

//	@Ignore
//	@Test
	public void GetAllConcept2OntClassMappings() {
		Collection<IInstanceClassificationEvidence> allc2cMappingSets = _proprietaryIntergretationCorrectionRepository1.getAllInstanceMembershipInferenceFacts();
		print(allc2cMappingSets, "Mapping: ");
		System.out.println();
	}

//	@Ignore
//	@Test
	public void GetNegativeConcept2OntClassMappings() {
		Collection<IInstanceClassificationEvidence> negativeC2CMappingSets = _proprietaryIntergretationCorrectionRepository1.getHiddenInstanceMembershipInferenceFacts();
		print(negativeC2CMappingSets, "Negative Mapping: ");
		System.out.println();
	}

//	@Ignore
//	@Test
	public void GetPositiveConcept2OntClassMappings() {
		Collection<IInstanceClassificationEvidence> positiveC2CMappingSets = _proprietaryIntergretationCorrectionRepository1.getExplicitInstanceMembershipInferenceFacts();
		print(positiveC2CMappingSets, "Positive Mapping: ");
		System.out.println();
	}
	
	private void print(Collection<IInstanceClassificationEvidence> mappingSets, String string) {
		for (IInstanceClassificationEvidence mappingSet : mappingSets) {
			System.out.println(string + mappingSet.getEvidenceCode() + " " + mappingSet.getCorrectionTargetClass().getOntClassName());
		}
	}
	
}
