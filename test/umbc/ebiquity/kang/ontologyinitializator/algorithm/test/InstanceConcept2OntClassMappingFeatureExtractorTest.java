package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
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


public class InstanceConcept2OntClassMappingFeatureExtractorTest {

	private static IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingExtractor;
	private static IClassificationCorrectionRepository _correctionRepository;
	private static IOntologyRepository _ontologyRepository;
	
	@BeforeClass
	public static void init() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";

		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_correctionRepository = new ClassificationCorrectionRepository(_ontologyRepository);
		
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IUpdatedInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			_correctionRepository.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		
		_instanceC2CMappingExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(new CorrectionClusterCodeGenerator(), _correctionRepository);
	}
	
	@Test
	public void test(){
		
		String className1 = "Product";
		String className2 = "Process";
		String className3 = "WaterJetCutting";
		String className4 = "Product";
		String className5 = "ManufacturingService";
		
		Collection<IInstanceMembershipInfereceFact> negativeMappingSets =  _instanceC2CMappingExtractor.getNegativeConcept2OntClassMappingSetsOfOntClass(className1);
		Map<IInstanceMembershipInfereceFact, Double> negativeMappingFeatures = _instanceC2CMappingExtractor.getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(className1);
		print(negativeMappingSets, "Negative Mapping");
		print(negativeMappingFeatures, "Negative");
		
		Collection<IInstanceMembershipInfereceFact> positiveMappingSets = _instanceC2CMappingExtractor.getPositiveConcept2OntClassMappingSetsOfOntClass(className3);
		Map<IInstanceMembershipInfereceFact, Double> positiveMappingFeatures = _instanceC2CMappingExtractor.getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(className3);
		print(positiveMappingSets, "Positive Mapping");
		print(positiveMappingFeatures, "Positive");
	}
	
	private void print(Collection<IInstanceMembershipInfereceFact> mappingSets, String label){
		for(IInstanceMembershipInfereceFact f : mappingSets){
			System.out.println(label  + ": "+ f.getMembershipInferenceFactCode());
		}
	}
	
	private void print(Map<IInstanceMembershipInfereceFact, Double> mappingFeatures, String label){
		for(IInstanceMembershipInfereceFact f : mappingFeatures.keySet()){
			double representativeness = mappingFeatures.get(f);
			System.out.println(f.getMembershipInferenceFactCode() + ",   " + label  + ": "+ representativeness);
		}
	}

}
