package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class ProprietoryClassifiedInstancesRepositoryTest {
	
	private static IManufacturingLexicalMappingRepository _MLReposiory;
	private static IManufacturingLexicalMappingRecordsReader _aggregratedManufacturingLexicalMappingRepository;
	private static IOntologyRepository _ontologyRepository;
    private static IClassifiedInstancesRepository _ProperietoryClassifiedInstancesRepository;
    
	@BeforeClass
	public static void LoadDataFromFile() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontology/MSDL-Fullv2.owl";
		
		// String homeURL = "http://www.accutrex.com/";
		// String homeURL = "http://www.weaverandsons.com/";
		// String homeURL = "http://www.bassettinc.com/";
		String homeURL = "http://www.numericalconcepts.com/";
		// String homeURL = "http://www.aerostarmfg.com/";
		// String homeURL = "http://www.astromfg.com/";
		// String homeURL = "http://www.navitekgroup.com/";
		// String homeURL = "http://www.cmc-usa.com/";
		URL webURL = new URL(homeURL);
		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_aggregratedManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory.createAggregratedManufacturingLexicalMappingRepository(_ontologyRepository);
		_MLReposiory= ManufacturingLexicalMappingRepositoryFactory.createProprietaryManufacturingLexiconRepository("numericalconcepts");

		_ProperietoryClassifiedInstancesRepository = ClassifiedInstancesRepositoryFactory.createProprietoryClassifiedInstancesRepository
		(
				webURL, 
				_ontologyRepository, 
				true,
				true,
				true
		);
		
		((ProprietoryClassifiedInstancesRepository)_ProperietoryClassifiedInstancesRepository).saveHumanReadableFile(RepositoryParameterConfiguration.getMappingHumanReadableDirectoryFullPath(), new HashSet<String>());
	}

	@Ignore
	@Test
	public void GetClassifiedInstanceBasicRecordByInstanceNameTest() {
		System.out.println("GetClassifiedInstanceBasicRecordByInstanceNameTest");
		for (String instance : _ProperietoryClassifiedInstancesRepository.getInstanceSet()) {
			IClassifiedInstanceBasicRecord record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceBasicRecordByInstanceName(instance);
			this.showClassifiedInstanceBasicRecord(record);
		}
	}
	
	@Ignore
	@Test
	public void GetClassifiedInstanceDetailRecordByInstanceNameTest() {
		for (String instance : _ProperietoryClassifiedInstancesRepository.getInstanceSet()) {
			IClassifiedInstanceDetailRecord record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instance);
			this.showClassifiedInstanceDetailRecord(record);
		}
	}
	
//	@Test
//	public void GetMappingBasicInfoTest(){
//		System.out.println("======================== Basic Instance Classification Info ========================");
//		System.out.println();
//		System.out.println();
//		MappingBasicInfo mappingBasicInfo = _ProperietoryClassifiedInstancesRepository.getMappingBasicInfo();
//		for (IClassifiedInstanceBasicRecord basicRecord : mappingBasicInfo.getClassifiedInstanceBasicInfoCollection()) {
//			System.out.println("--------------------------------------------------");
//			System.out.println("Instance Name  : " + basicRecord.getInstanceLabel());
//			System.out.println("Class Name     : " + basicRecord.getOntoClassName());
//			System.out.println("Class NameSpace: " + basicRecord.getOntoClassNameSpace());
//			System.out.println("Class URI      : " + basicRecord.getOntoClassURI());
//			System.out.println("Similarity     : " + basicRecord.getSimilarity());
//		}
//	}
//	
//	@Test
//	public void GetMappingDetailInfoTest(){
//		System.out.println("======================== Detail Instance Classification Info ========================");
//		System.out.println();
//		System.out.println();
//		MappingDetailInfo mappingDetailInfo = _ProperietoryClassifiedInstancesRepository.getMappingDetailInfo();
//		for (IClassifiedInstanceDetailRecord detailedRecord : mappingDetailInfo.getClassifiedInstanceDetailRecords()) {
//			System.out.println("--------------------------------------------------");
//			System.out.println("Instance Name  : " + detailedRecord.getInstanceLabel());
//			System.out.println("Class Name     : " + detailedRecord.getOntoClassName());
//			System.out.println("Class NameSpace: " + detailedRecord.getOntoClassNameSpace());
//			System.out.println("Class URI      : " + detailedRecord.getOntoClassURI());
//			System.out.println("Similarity     : " + detailedRecord.getSimilarity());
//
//			for (OntoClassInfo rcl1 : detailedRecord.getSuperOntoClassesOfMatchedOntoClass()) {
//				System.out.println("rcl1-1 :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
//			}
//
//			for (OntoClassInfo rcl1 : detailedRecord.getFirstLevelRecommendedOntoClasses()) {
//				System.out.println("rcl1   :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
//			}
//
//			for (OntoClassInfo rcl2 : detailedRecord.getSecondLevelRecommendedOntoClasses()) {
//				System.out.println("rcl2   :  " + rcl2.getURI());
//			}
//
//			for (IConcept2OntClassMapping mappingPair : detailedRecord.getConcept2OntClassMappingPairs()) {
//				System.out.println("concept : " + mappingPair.getConceptName());
//				if (mappingPair.isMappedConcept()) {
////					String ontoClassLabel = concept.getMappedOntoClass().getLabel();
//					String ontoClassURI = mappingPair.getMappedOntoClass().getURI();
//					double mappingSimilarity = mappingPair.getMappingScore();
//					System.out.println("       mapped to: " + ontoClassURI + " with similairty: " + mappingSimilarity);
//				} else {
//
//				}
//			}
//		}
//	}
//	
//	@Test
//	public void UpdateInstances() throws IOException { 
//		FakeDataCreator fakeDataCreator = new FakeDataCreator();
//		// update instance
//		String provenanceInstance = "Manufactured Parts - All";
//		String originalInstance = "Manufactured Parts - All";
//		String updatedInstanceName = "Manufacture Parts";
//		String originalClassName = "SheetMetalService";
//		String updatedClassName = "SheetMetalService";
//		Map<String, String> c2cMapping = new HashMap<String, String>();
//		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
//		c2cMapping.put("products", "Product");
//		c2cMapping.put("ManufacturingService", "Service");
//		
//		IClassifiedInstanceDetailRecord record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalInstance);
//		Assert.assertEquals("Manufactured Parts - All", record.getInstanceLabel());
//		record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(updatedInstanceName);
//		Assert.assertEquals(null, record);
//		IUpdatedInstanceRecord updatedRecord1 = fakeDataCreator.createUpdatedInstanceRecord(provenanceInstance, originalInstance, updatedInstanceName, originalClassName, updatedClassName, c2cMapping);
//		_ProperietoryClassifiedInstancesRepository.updateInstance(updatedRecord1);
//		record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalClassName);
//		Assert.assertEquals(null, record);
//		record = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(updatedInstanceName);
//		Assert.assertEquals("Manufacture Parts", record.getInstanceLabel());
//		
//		// update class
//		provenanceInstance = "Knurling";
//		originalInstance = "Knurling";
//		updatedInstanceName = "Knurling";
//		originalClassName = "Process";
//		updatedClassName = "Machining";
//		c2cMapping = new HashMap<String, String>();
//		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
//		c2cMapping.put("capabilities", "Process");
//		c2cMapping.put("carbon steel shims", "CarbonSteel");
//		
//		IClassifiedInstanceDetailRecord record2 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalInstance);
//		Assert.assertEquals("Process", record2.getOntoClassName());
//		IUpdatedInstanceRecord updatedRecord2 = fakeDataCreator.createUpdatedInstanceRecord(provenanceInstance, originalInstance, updatedInstanceName, originalClassName, updatedClassName, c2cMapping);
//		_ProperietoryClassifiedInstancesRepository.updateInstance(updatedRecord2);
//		record2 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalInstance);
//		Assert.assertEquals("Machining", record2.getOntoClassName());
//		
//		
//		// update both instance and class
//		
//		
//		provenanceInstance = "Machine Shop Capabilities";
//		originalInstance = "Machine Shop Capabilities";
//		updatedInstanceName = "Machine Shop Capability";
//		originalClassName = "ManufacturingService";
//		updatedClassName = "Machining";
//		c2cMapping = new HashMap<String, String>();
//		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
//		c2cMapping.put("shims", "NULL");
//		c2cMapping.put("shim types", "NULL");
//		
//		IClassifiedInstanceDetailRecord record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalInstance);
//		Assert.assertEquals("Machine Shop Capabilities", record3.getInstanceLabel());
//		record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(updatedInstanceName);
//		Assert.assertEquals(null, record3);
//		
//		record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalInstance);
//		Assert.assertEquals("ManufacturingService", record3.getOntoClassName());
//		
//		IUpdatedInstanceRecord updatedRecord3 = fakeDataCreator.createUpdatedInstanceRecord(provenanceInstance, originalInstance, updatedInstanceName, originalClassName, updatedClassName, c2cMapping);
//		_ProperietoryClassifiedInstancesRepository.updateInstance(updatedRecord3);
//		
//		record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(originalClassName);
//		Assert.assertEquals(null, record3);
//		record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(updatedInstanceName);
//		Assert.assertEquals("Machine Shop Capability", record3.getInstanceLabel());
//		
//		record3 = _ProperietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(updatedInstanceName);
//		Assert.assertEquals("Machining", record3.getOntoClassName());
//		
//		
//	}

	private void showClassifiedInstanceBasicRecord(IClassifiedInstanceBasicRecord record) {
		System.out.println("--------------------------------------------------");
		System.out.println("Instance Name  : " + record.getInstanceLabel());
		System.out.println("Class Name     : " + record.getOntoClassName());
		System.out.println("Class NameSpace: " + record.getOntoClassNameSpace());
		System.out.println("Class URI      : " + record.getOntoClassURI());
		System.out.println("Similarity     : " + record.getSimilarity());
	}

	private void showClassifiedInstanceDetailRecord(IClassifiedInstanceDetailRecord record) {
		System.out.println("--------------------------------------------------");
		System.out.println("Instance Name  : " + record.getInstanceLabel());
		System.out.println("Class Name     : " + record.getOntoClassName());
		System.out.println("Class NameSpace: " + record.getOntoClassNameSpace());
		System.out.println("Class URI      : " + record.getOntoClassURI());
		System.out.println("Similarity     : " + record.getSimilarity());

		for (OntoClassInfo rcl1 : record.getSuperOntoClassesOfMatchedOntoClass()) {
			System.out.println("rcl1-1 :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
		}

		for (OntoClassInfo rcl1 : record.getFirstLevelRecommendedOntoClasses()) {
			System.out.println("rcl1   :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
		}

		for (OntoClassInfo rcl2 : record.getSecondLevelRecommendedOntoClasses()) {
			System.out.println("rcl2   :  " + rcl2.getURI());
		}

		for (IConcept2OntClassMapping mappingPair : record.getConcept2OntClassMappingPairs()) {
			System.out.println("concept : " + mappingPair.getConceptName());
			if (mappingPair.isMappedConcept()) {
				String ontoClassURI = mappingPair.getMappedOntoClass().getURI();
				double mappingSimilarity = mappingPair.getMappingScore();
				System.out.println("       mapped to: " + ontoClassURI + " with similairty: " + mappingSimilarity);
			} else {

			}
		}
	}
}
