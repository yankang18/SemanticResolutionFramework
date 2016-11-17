package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.test;

import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionCluster;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionClusterFeatureWrapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;
import umbc.ebiquity.kang.textprocessing.phraseextractor.impl.SequenceInReversedOrderPhraseExtractor;


public class ClassificationCorrectionClusterTest {
	
	private static IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor;
	private static IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingExtractor;
	private static IClassificationCorrectionRepository _correctionRepository;
	private static IOntologyRepository _ontologyRepository;
	
	@BeforeClass
	public static void init() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";

		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_correctionRepository = new ClassificationCorrectionRepository(_ontologyRepository);
		
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			_correctionRepository.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		
		_instanceC2CMappingExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(new CorrectionClusterCodeGenerator(), _correctionRepository, _ontologyRepository);

		_instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(
				ClassifiedInstancesRepositoryFactory.createAggregatedClassifiedInstancesRepository(_ontologyRepository), new SimpleLexicalFeatureExtractor(
						new SequenceInReversedOrderPhraseExtractor()));

	}

	@Test
	public void ClassificationCorrectionClusterFeatureWrapperTest() { 
		this.createCorrectionCluster("Machining", "WaterJetCutting");
		this.createCorrectionCluster("Service", "Process");
	}
	
	private void createCorrectionCluster(String targetClass, String sourceClass) {
		System.out.println();
		System.out.println("Correction: " + targetClass + " -->" + sourceClass);

		CorrectionDirection correctionDirection = new CorrectionDirection(targetClass, sourceClass);
		ClassificationCorrectionCluster cluster = new ClassificationCorrectionCluster("", correctionDirection,
				_instanceLexicalFeatureExtractor, _instanceC2CMappingExtractor);
		cluster.extractFeatures();

		CorrectionClusterFeatureWrapper wrapper = cluster.getCorrectionClusterFeature();
		Map<LexicalFeature, Double> lexicalFeatures = wrapper.getTargetClassInstanceLexicalFeatures();
		Map<IInstanceClassificationEvidence, Double> negativeFeatures = wrapper.getNegativeMappingSetsWithLocalRateToCorrectionTargetClass();
		Map<IInstanceClassificationEvidence, Double> positiveFeatures = wrapper.getPositiveMappingSetsWithLocalRateToCorrectionTargetClass();
		
		printLexicalFeatures(lexicalFeatures, "Lexical Features");
		printMappingFeatures(positiveFeatures, "Positive Features");
		printMappingFeatures(negativeFeatures, "Negative Features");
	}

	private void printLexicalFeatures(Map<LexicalFeature, Double> lexicalFeatures, String label) {
		
		double total = 0.0;
		for (LexicalFeature f : lexicalFeatures.keySet()) {
			double representativeness = lexicalFeatures.get(f);
			total += representativeness;
			
			if (f == null) {
				System.out.println(label + ": default1 " + representativeness);
			} else {
				System.out.println(label + ": " + f.getLabel() + ",  " + f.getSupport() + ",  " + representativeness);
			}
		}
		
		System.out.println("total: " + total);
	}

	private void printMappingFeatures(Map<IInstanceClassificationEvidence, Double> mappingFeatures, String label) {
		for (IInstanceClassificationEvidence f : mappingFeatures.keySet()) {
			double representativeness = mappingFeatures.get(f);
			if (f == null) {
				System.out.println(label + ": default2 " + representativeness);
			} else {
				System.out.println(label + ": " + f.getEvidenceCode() + ",  " + representativeness);
			}
		}
	}
}
