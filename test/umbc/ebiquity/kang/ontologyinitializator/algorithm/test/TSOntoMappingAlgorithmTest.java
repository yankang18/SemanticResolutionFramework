package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;

import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class TSOntoMappingAlgorithmTest {
	
	private IInstanceDescriptionModel extractedTripleStore;
	private IOntologyRepository ontologyRepository;
	private IManufacturingLexicalMappingRecordsReader MLRepository;
	
	@Before
	public void Init() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		MLRepository = ManufacturingLexicalMappingRepositoryFactory.createAggregratedManufacturingLexicalMappingRepository(ontologyRepository);

//		String webSiteURLString = "http://www.accutrex.com";
		String webSiteURLString = "http://www.princetonind.com";
		URL webSiteURL = new URL(webSiteURLString);
		extractedTripleStore = InstanceDescriptionModelFactory.construct(webSiteURL);
		extractedTripleStore.showTriples();
	}

	public static void main(String[] args) throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
//		IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
//		IManufacturingLexicalMappingRepository MLRepository = ManufacturingLexicalMappingRepositoryFactory.createManufacturingLexiconRepository();

		String webSiteURLString = "http://www.accutrex.com";
//		String webSiteURLString = "http://cncmachining.boyermachine.com";
//		String webSiteURLString = "http://www.npcnc.com";
		URL webSiteURL = new URL(webSiteURLString);
		IInstanceDescriptionModel extractedTripleStore = InstanceDescriptionModelFactory.construct(webSiteURL);
		extractedTripleStore.showTriples();
	}
	
//	@Test
//	public void GetMappingInfo() throws IOException {
//		IMappingAlgorithm mappingAlgorithm = new TS2OntoMappingAlgorithm2(extractedTripleStore, ontologyRepository, null, MLRepository);
//		mappingAlgorithm.mapping();
//		IMappingInfoRepository mappingResult = mappingAlgorithm.getMappingInfoRepository();
//		mappingResult.showRepositoryDetail();
//	}

}
