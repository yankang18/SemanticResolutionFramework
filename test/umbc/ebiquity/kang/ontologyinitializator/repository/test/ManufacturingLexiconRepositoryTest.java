package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.UpdatedInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IUpdatedInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;

public class ManufacturingLexiconRepositoryTest {
	
	private static IManufacturingLexicalMappingRepository _repo;
	private static IOntologyRepository _ontologyRepository;
	private static Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> _updatedRecord2OriginalRecord; 
	private static FakeDataCreator fakeDataCreator;
	
	@BeforeClass
	public static void init() throws IOException{ 
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		
//		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_repo = ManufacturingLexicalMappingRepositoryFactory.createManufacturingLexiconRepository();
		fakeDataCreator = new FakeDataCreator();
		_updatedRecord2OriginalRecord = createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
	}
	
	@Ignore
	@Test
	public void AddNewConcept2OntoClassMappingTest() {
		System.out.println("addNewConcept2OntoClassMappingTest");
		System.out.println("");
		
		Concept concept1 = new Concept("Test1");
		OntoClassInfo ontoClass1 = new OntoClassInfo("test_URI1", "test_NameSpace1", "test_className1");
		
		Concept concept2= new Concept("Test2");
		OntoClassInfo ontoClass2 = new OntoClassInfo("test_URI2", "test_NameSpace2", "test_className2");
		
		_repo.addNewConcept2OntoClassMapping(concept1, MappingRelationType.relatedTo, ontoClass1, 0.9);
		_repo.addNewConcept2OntoClassMapping(concept2, MappingRelationType.relatedTo, ontoClass2, 0.9);
		
		assertEquals(true, _repo.hasConcept(concept1));
		assertEquals(true, _repo.hasConcept2OntoClassMapping(concept1, ontoClass1)); 
		
		assertEquals(true, _repo.hasConcept(concept2));
		assertEquals(true, _repo.hasConcept2OntoClassMapping(concept2, ontoClass2)); 
		
		System.out.println("--------------------------");
		_repo.showRepositoryDetail();
	}
	
//	@Ignore
	@Test
	public void AddNewConcept2OntClassMappingsFromClassifiedInstanceRecordsTest() throws IOException{ 
		System.out.println("addNewConcept2OntClassMappingsFromClassifiedInstanceRecordsTest");
		System.out.println("");
		
		_repo.addNewConcept2OntoClassMappings(_updatedRecord2OriginalRecord.values());
		System.out.println("--------------------------");
		_repo.showRepositoryDetail();
	}
	
	@Ignore
	@Test
	public void UpdateConcept2OntoClassMappingVerificationResultTest(){
		System.out.println("updateConcept2OntoClassMappingVerificationResultTest");
		System.out.println("");
		
		Concept concept1 = new Concept("Test1");
		OntoClassInfo ontoClass1 = new OntoClassInfo("test_URI1", "test_NameSpace1", "test_className1");
		_repo.addNewConcept2OntoClassMapping(concept1, MappingRelationType.narrower, ontoClass1, 0.8);
		_repo.updateConcept2OntoClassMappingVerificationResult(concept1, ontoClass1, MappingVericationResult.Succeed);
		
		Concept concept2= new Concept("Test2");
		OntoClassInfo ontoClass2 = new OntoClassInfo("test_URI2", "test_NameSpace2", "test_className2");
		_repo.addNewConcept2OntoClassMapping(concept2, MappingRelationType.broader, ontoClass2, 0.8);
		_repo.updateConcept2OntoClassMappingVerificationResult(concept2, ontoClass2, MappingVericationResult.Failed);
		
		Concept concept3= new Concept("Test3");
		OntoClassInfo ontoClass3 = new OntoClassInfo("test_URI3", "test_NameSpace3", "test_className3");
		_repo.addNewConcept2OntoClassMapping(concept3, MappingRelationType.broader, ontoClass3, 0.8);
		_repo.updateConcept2OntoClassMappingVerificationResult(concept3, ontoClass3, MappingVericationResult.Unknow);
		
		try {
			IConcept2OntClassMappingStatistics statistics1 = _repo.getConcept2ClassMappingStatistics(concept1, ontoClass1);
			IConcept2OntClassMappingStatistics statistics2 = _repo.getConcept2ClassMappingStatistics(concept2, ontoClass2);
			IConcept2OntClassMappingStatistics statistics3 = _repo.getConcept2ClassMappingStatistics(concept3, ontoClass3);
			assertEquals(0,statistics1.getUndeterminedCounts());
			assertEquals(0,statistics1.getFailedCounts());
			assertEquals(1,statistics1.getSucceedCounts());
			assertEquals(1,statistics1.getVerificationAttempts());
			
			assertEquals(0,statistics2.getUndeterminedCounts());
			assertEquals(1,statistics2.getFailedCounts());
			assertEquals(0,statistics2.getSucceedCounts());
			assertEquals(1,statistics2.getVerificationAttempts());
			
			assertEquals(1,statistics3.getUndeterminedCounts());
			assertEquals(0,statistics3.getFailedCounts());
			assertEquals(0,statistics3.getSucceedCounts());
			assertEquals(1,statistics3.getVerificationAttempts());
			
		} catch (NoSuchEntryItemException e) { 
			e.printStackTrace();
		}
		
		_repo.updateConcept2OntoClassMappingVerificationResult(concept1, ontoClass1, MappingVericationResult.Failed);
		_repo.updateConcept2OntoClassMappingVerificationResult(concept2, ontoClass2, MappingVericationResult.Unknow);
		_repo.updateConcept2OntoClassMappingVerificationResult(concept3, ontoClass3, MappingVericationResult.Succeed);
		
		try {
			IConcept2OntClassMappingStatistics statistics1 = _repo.getConcept2ClassMappingStatistics(concept1, ontoClass1);
			IConcept2OntClassMappingStatistics statistics2 = _repo.getConcept2ClassMappingStatistics(concept2, ontoClass2);
			IConcept2OntClassMappingStatistics statistics3 = _repo.getConcept2ClassMappingStatistics(concept3, ontoClass3);
			assertEquals(0,statistics1.getUndeterminedCounts());
			assertEquals(1,statistics1.getFailedCounts());
			assertEquals(1,statistics1.getSucceedCounts());
			assertEquals(2,statistics1.getVerificationAttempts());
			
			assertEquals(1,statistics2.getUndeterminedCounts());
			assertEquals(1,statistics2.getFailedCounts());
			assertEquals(0,statistics2.getSucceedCounts());
			assertEquals(2,statistics2.getVerificationAttempts());
			
			assertEquals(1,statistics3.getUndeterminedCounts());
			assertEquals(0,statistics3.getFailedCounts());
			assertEquals(1,statistics3.getSucceedCounts());
			assertEquals(2,statistics3.getVerificationAttempts());
			
		} catch (NoSuchEntryItemException e) { 
			e.printStackTrace();
		}
		
		System.out.println("--------------------------");
		_repo.showRepositoryDetail();
	}
	
	@Test
	public void UpdateValidityOfConcept2OntClassMappingTest() throws NoSuchEntryItemException {
		System.out.println("UpdateValidityOfConcept2OntClassMappingTest");
		System.out.println("");
		
		for (IUpdatedInstanceRecord updatedRecord : _updatedRecord2OriginalRecord.keySet()) {
			_repo.updateValidityOfConcept2OntClassMapping(updatedRecord, _updatedRecord2OriginalRecord.get(updatedRecord));
		}
		
		this.checkRepository();
		_repo.showRepositoryDetail();
	}
	
	private void checkRepository() throws NoSuchEntryItemException {
		Concept concept = new Concept("sheet metal fabrication");
		OntoClassInfo ontoClass = fakeDataCreator.createOntClass("SheetMetalService");
		IConcept2OntClassMappingStatistics statistics1 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
//		assertEquals(0,statistics1.getUndeterminedCounts());
		assertEquals(3,statistics1.getUndeterminedCounts());
		assertEquals(1,statistics1.getFailedCounts());
		assertEquals(0,statistics1.getSucceedCounts());
//		assertEquals(1,statistics1.getVerificationAttempts());
		assertEquals(4,statistics1.getVerificationAttempts());

		concept = new Concept("products");
		ontoClass = fakeDataCreator.createOntClass("Product");
		IConcept2OntClassMappingStatistics statistics2 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(1,statistics2.getUndeterminedCounts());
		assertEquals(0,statistics2.getFailedCounts());
		assertEquals(0,statistics2.getSucceedCounts());
		assertEquals(1,statistics2.getVerificationAttempts());
		
		concept = new Concept("ManufacturingService");
		ontoClass = fakeDataCreator.createOntClass("Service");
		IConcept2OntClassMappingStatistics statistics3 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(2,statistics3.getUndeterminedCounts());
		assertEquals(0,statistics3.getFailedCounts());
		assertEquals(0,statistics3.getSucceedCounts());
		assertEquals(2,statistics3.getVerificationAttempts());
		
		concept = new Concept("capabilities");
		ontoClass = fakeDataCreator.createOntClass("Process");
		IConcept2OntClassMappingStatistics statistics4 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(0,statistics4.getUndeterminedCounts());
		assertEquals(0,statistics4.getFailedCounts());
		assertEquals(1,statistics4.getSucceedCounts());
		assertEquals(1,statistics4.getVerificationAttempts());
		
		concept = new Concept("carbon steel shims");
		ontoClass = fakeDataCreator.createOntClass("CarbonSteel");
		IConcept2OntClassMappingStatistics statistics5 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(1,statistics5.getUndeterminedCounts());
		assertEquals(0,statistics5.getFailedCounts());
		assertEquals(0,statistics5.getSucceedCounts());
		assertEquals(1,statistics5.getVerificationAttempts());
		
		concept = new Concept("military defense");
		ontoClass = fakeDataCreator.createOntClass("DefenseAndMilitary");
		IConcept2OntClassMappingStatistics statistics6 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		
//		System.out.println(statistics6.getUndeterminedCounts());
//		System.out.println(statistics6.getFailedCounts());
//		System.out.println(statistics6.getSucceedCounts());
//		System.out.println(statistics6.getVerificationAttempts());
		
		assertEquals(0,statistics6.getUndeterminedCounts());
		assertEquals(1,statistics6.getFailedCounts());
		assertEquals(0,statistics6.getSucceedCounts());
		assertEquals(1,statistics6.getVerificationAttempts());

		concept = new Concept("gaskets");
		ontoClass = fakeDataCreator.createOntClass("Product");
		IConcept2OntClassMappingStatistics statistics7 = _repo.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(0,statistics7.getUndeterminedCounts());
		assertEquals(0,statistics7.getFailedCounts());
		assertEquals(1,statistics7.getSucceedCounts());
		assertEquals(1,statistics7.getVerificationAttempts());
		
		System.out.println("--------------------------");
		_repo.showRepositoryDetail();
	}
	
	public static Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords() {
		Map<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IUpdatedInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Precision Machining";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("products", "Product"); //neural
		c2cMapping.put("ManufacturingService", "Service"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Abrasive Waterjet Cutting";
		originalClassName = "ManufacturingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("capabilities", "Process"); // boosting
		c2cMapping.put("carbon steel shims", "CarbonSteel"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Spiral Wound Gaskets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); //neural
		c2cMapping.put("types gaskets", "NULL"); // no such mapping
		c2cMapping.put("gaskets", "NULL"); // no such mapping
//		
		Map<String, String> c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping2.put("types gaskets", "NULL"); // no such mapping
		c2cMapping2.put("gaskets", "Product"); // boosting
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping2),
				fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shims";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // penalized
		c2cMapping.put("ManufacturingService", "Service"); //neural
		c2cMapping.put("military defense", "DefenseAndMilitary"); // penalized
		
		c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "Process"); // neural
		c2cMapping2.put("ManufacturingService", "Service"); // neural
		c2cMapping2.put("military defense", "NULL"); // no such mapping
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping2),
				fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		return XXX;
	}
	
	@Test
	public void SaveRepositoryTest(){
		System.out.println("SaveRepositoryTest");
		_repo.saveRepository();
	}
	
	@Test
	public void LoadRepositoryTest() throws NoSuchEntryItemException {
		System.out.println("LoadRepositoryTest");
		_repo.loadRepository();
		this.checkRepository();
	}

}
