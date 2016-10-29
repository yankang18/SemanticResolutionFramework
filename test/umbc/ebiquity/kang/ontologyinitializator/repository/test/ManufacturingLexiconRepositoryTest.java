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

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.AggregratedManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.UpdatedInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsUpdater.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IReadOnlyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;

public class ManufacturingLexiconRepositoryTest {

	private static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> _updatedRecord2OriginalRecord;
	private static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> _updatedRecord2OriginalRecord1;
	private static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> _updatedRecord2OriginalRecord2;
	private static FakeDataCreator fakeDataCreator;
	private static String repositoryOneFullName = "ManufacturingLexicalMappingRepositoryOneTest";
	private static String repositoryTwoFullName = "ManufacturingLexicalMappingRepositoryTwoTest";
	private static IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository1;
	private static IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository2;
	
	@BeforeClass
	public static void init() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		System.out.println("@@@1 " + FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH);
		fakeDataCreator = new FakeDataCreator();
		_updatedRecord2OriginalRecord = createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		_updatedRecord2OriginalRecord1 = createUpdatedInstanceRecordsAndClassifiedInstanceRecords1();
		_updatedRecord2OriginalRecord2 = createUpdatedInstanceRecordsAndClassifiedInstanceRecords2();
		
//		proprietaryManufacturingLexicalMappingRepository1 = ManufacturingLexicalMappingRepositoryFactory
//				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
//		
//		proprietaryManufacturingLexicalMappingRepository2 = ManufacturingLexicalMappingRepositoryFactory
//				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
	}
	

//	@Ignore
	@Test
	public void xxxx() throws IOException{ 
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		FileRepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		IManufacturingLexicalMappingRecordsReader aggregratedManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createAggregratedManufacturingLexicalMappingRepository(OntologyRepositoryFactory.createOntologyRepository());
		System.out.println("--------------------------");
		aggregratedManufacturingLexicalMappingRepository.showRepositoryDetail();
	}
	

	@Ignore
	@Test
	public void AddNewConcept2OntoClassMappingTest() throws IOException { 
		System.out.println("addNewConcept2OntoClassMappingTest");
		System.out.println("");
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
		
		Concept concept1 = new Concept("Test1");
		OntoClassInfo ontoClass1 = new OntoClassInfo("test_URI1", "test_NameSpace1", "test_className1");

		Concept concept2 = new Concept("Test2");
		OntoClassInfo ontoClass2 = new OntoClassInfo("test_URI2", "test_NameSpace2", "test_className2");

		proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept1, MappingRelationType.relatedTo,
				ontoClass1, 0.9);
		proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept2, MappingRelationType.relatedTo,
				ontoClass2, 0.9);

		assertEquals(true, proprietaryManufacturingLexicalMappingRepository.hasConcept(concept1));
		assertEquals(true, proprietaryManufacturingLexicalMappingRepository.hasConcept2OntoClassMapping(concept1, ontoClass1));

		assertEquals(true, proprietaryManufacturingLexicalMappingRepository.hasConcept(concept2));
		assertEquals(true, proprietaryManufacturingLexicalMappingRepository.hasConcept2OntoClassMapping(concept2, ontoClass2));

		System.out.println("--------------------------");
		proprietaryManufacturingLexicalMappingRepository.showRepositoryDetail();
	}

	@Ignore
	@Test
	public void AddNewConcept2OntClassMappingsFromClassifiedInstanceRecordsTest() throws IOException {
		System.out.println("addNewConcept2OntClassMappingsFromClassifiedInstanceRecordsTest");
		System.out.println("");
		System.out.println("@@@2 " + FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH);
		proprietaryManufacturingLexicalMappingRepository1.addNewConcept2OntoClassMappings(_updatedRecord2OriginalRecord.values());
		System.out.println("--------------------------");
		proprietaryManufacturingLexicalMappingRepository1.showRepositoryDetail();
	}

	@Ignore
	@Test
	public void UpdateConcept2OntoClassMappingVerificationResultTest() throws IOException {
		System.out.println("updateConcept2OntoClassMappingVerificationResultTest");
		System.out.println("");

		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
		
		Concept concept1 = new Concept("Test1");
		OntoClassInfo ontoClass1 = new OntoClassInfo("test_URI1", "test_NameSpace1", "test_className1");
		proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept1, MappingRelationType.narrower,
				ontoClass1, 0.8);
		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept1, ontoClass1,
				MappingVericationResult.Succeed);

		Concept concept2 = new Concept("Test2");
		OntoClassInfo ontoClass2 = new OntoClassInfo("test_URI2", "test_NameSpace2", "test_className2");
		proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept2, MappingRelationType.broader, ontoClass2,
				0.8);
		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept2, ontoClass2,
				MappingVericationResult.Failed);

		Concept concept3 = new Concept("Test3");
		OntoClassInfo ontoClass3 = new OntoClassInfo("test_URI3", "test_NameSpace3", "test_className3");
		proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept3, MappingRelationType.broader, ontoClass3,
				0.8);
		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept3, ontoClass3,
				MappingVericationResult.Unknow);

		try {
			IConcept2OntClassMappingStatistics statistics1 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept1, ontoClass1);
			IConcept2OntClassMappingStatistics statistics2 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept2, ontoClass2);
			IConcept2OntClassMappingStatistics statistics3 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept3, ontoClass3);
			assertEquals(0, statistics1.getUndeterminedCounts());
			assertEquals(0, statistics1.getFailedCounts());
			assertEquals(1, statistics1.getSucceedCounts());
			assertEquals(1, statistics1.getVerificationAttempts());

			assertEquals(0, statistics2.getUndeterminedCounts());
			assertEquals(1, statistics2.getFailedCounts());
			assertEquals(0, statistics2.getSucceedCounts());
			assertEquals(1, statistics2.getVerificationAttempts());

			assertEquals(1, statistics3.getUndeterminedCounts());
			assertEquals(0, statistics3.getFailedCounts());
			assertEquals(0, statistics3.getSucceedCounts());
			assertEquals(1, statistics3.getVerificationAttempts());

		} catch (NoSuchEntryItemException e) {
			e.printStackTrace();
		}

		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept1, ontoClass1,
				MappingVericationResult.Failed);
		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept2, ontoClass2,
				MappingVericationResult.Unknow);
		proprietaryManufacturingLexicalMappingRepository.updateConcept2OntoClassMappingVerificationResult(concept3, ontoClass3,
				MappingVericationResult.Succeed);

		try {
			IConcept2OntClassMappingStatistics statistics1 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept1, ontoClass1);
			IConcept2OntClassMappingStatistics statistics2 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept2, ontoClass2);
			IConcept2OntClassMappingStatistics statistics3 = proprietaryManufacturingLexicalMappingRepository
					.getConcept2ClassMappingStatistics(concept3, ontoClass3);
			assertEquals(0, statistics1.getUndeterminedCounts());
			assertEquals(1, statistics1.getFailedCounts());
			assertEquals(1, statistics1.getSucceedCounts());
			assertEquals(2, statistics1.getVerificationAttempts());

			assertEquals(1, statistics2.getUndeterminedCounts());
			assertEquals(1, statistics2.getFailedCounts());
			assertEquals(0, statistics2.getSucceedCounts());
			assertEquals(2, statistics2.getVerificationAttempts());

			assertEquals(1, statistics3.getUndeterminedCounts());
			assertEquals(0, statistics3.getFailedCounts());
			assertEquals(1, statistics3.getSucceedCounts());
			assertEquals(2, statistics3.getVerificationAttempts());

		} catch (NoSuchEntryItemException e) {
			e.printStackTrace();
		}

		System.out.println("--------------------------");
		proprietaryManufacturingLexicalMappingRepository.showRepositoryDetail();
	}

	@Ignore
	@Test
	public void UpdateValidityOfConcept2OntClassMappingTest() throws NoSuchEntryItemException, IOException {
		System.out.println("UpdateValidityOfConcept2OntClassMappingTest");
		System.out.println("");
		for (IInstanceRecord updatedRecord : _updatedRecord2OriginalRecord.keySet()) {
			proprietaryManufacturingLexicalMappingRepository1.updateValidityOfConcept2OntClassMapping(updatedRecord,
					_updatedRecord2OriginalRecord.get(updatedRecord));
		}

		this.checkRepository(proprietaryManufacturingLexicalMappingRepository1);
		proprietaryManufacturingLexicalMappingRepository1.showRepositoryDetail();
	}

	private void checkRepository(IManufacturingLexicalMappingRecordsReader proprietaryManufacturingLexicalMappingRecordsRepo) throws NoSuchEntryItemException {
		Concept concept = new Concept("sheet metal fabrication");
		OntoClassInfo ontoClass = fakeDataCreator.createOntClass("SheetMetalService");
		IConcept2OntClassMappingStatistics statistics1 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		// assertEquals(0,statistics1.getUndeterminedCounts());
		assertEquals(3, statistics1.getUndeterminedCounts());
		assertEquals(1, statistics1.getFailedCounts());
		assertEquals(0, statistics1.getSucceedCounts());
		// assertEquals(1,statistics1.getVerificationAttempts());
		assertEquals(4, statistics1.getVerificationAttempts());

		concept = new Concept("products");
		ontoClass = fakeDataCreator.createOntClass("Product");
		IConcept2OntClassMappingStatistics statistics2 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(1, statistics2.getUndeterminedCounts());
		assertEquals(0, statistics2.getFailedCounts());
		assertEquals(0, statistics2.getSucceedCounts());
		assertEquals(1, statistics2.getVerificationAttempts());

		concept = new Concept("ManufacturingService");
		ontoClass = fakeDataCreator.createOntClass("Service");
		IConcept2OntClassMappingStatistics statistics3 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(2, statistics3.getUndeterminedCounts());
		assertEquals(0, statistics3.getFailedCounts());
		assertEquals(0, statistics3.getSucceedCounts());
		assertEquals(2, statistics3.getVerificationAttempts());

		concept = new Concept("capabilities");
		ontoClass = fakeDataCreator.createOntClass("Process");
		IConcept2OntClassMappingStatistics statistics4 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(0, statistics4.getUndeterminedCounts());
		assertEquals(0, statistics4.getFailedCounts());
		assertEquals(1, statistics4.getSucceedCounts());
		assertEquals(1, statistics4.getVerificationAttempts());

		concept = new Concept("carbon steel shims");
		ontoClass = fakeDataCreator.createOntClass("CarbonSteel");
		IConcept2OntClassMappingStatistics statistics5 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(1, statistics5.getUndeterminedCounts());
		assertEquals(0, statistics5.getFailedCounts());
		assertEquals(0, statistics5.getSucceedCounts());
		assertEquals(1, statistics5.getVerificationAttempts());

		concept = new Concept("military defense");
		ontoClass = fakeDataCreator.createOntClass("DefenseAndMilitary");
		IConcept2OntClassMappingStatistics statistics6 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);

		// System.out.println(statistics6.getUndeterminedCounts());
		// System.out.println(statistics6.getFailedCounts());
		// System.out.println(statistics6.getSucceedCounts());
		// System.out.println(statistics6.getVerificationAttempts());

		assertEquals(0, statistics6.getUndeterminedCounts());
		assertEquals(1, statistics6.getFailedCounts());
		assertEquals(0, statistics6.getSucceedCounts());
		assertEquals(1, statistics6.getVerificationAttempts());

		concept = new Concept("gaskets");
		ontoClass = fakeDataCreator.createOntClass("Product");
		IConcept2OntClassMappingStatistics statistics7 = proprietaryManufacturingLexicalMappingRecordsRepo
				.getConcept2ClassMappingStatistics(concept, ontoClass);
		assertEquals(0, statistics7.getUndeterminedCounts());
		assertEquals(0, statistics7.getFailedCounts());
		assertEquals(1, statistics7.getSucceedCounts());
		assertEquals(1, statistics7.getVerificationAttempts());

		System.out.println("--------------------------");
//		proprietaryManufacturingLexicalMappingRecordsRepo.showRepositoryDetail();
	}

	public static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Precision Machining";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("products", "Product"); // neural
		c2cMapping.put("ManufacturingService", "Service"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Abrasive Waterjet Cutting";
		originalClassName = "ManufacturingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("capabilities", "Process"); // boosting
		c2cMapping.put("carbon steel shims", "CarbonSteel"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Spiral Wound Gaskets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("types gaskets", "NULL"); // no such mapping
		c2cMapping.put("gaskets", "NULL"); // no such mapping
		//
		Map<String, String> c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping2.put("types gaskets", "NULL"); // no such mapping
		c2cMapping2.put("gaskets", "Product"); // boosting
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping2), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shims";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // penalized
		c2cMapping.put("ManufacturingService", "Service"); // neural
		c2cMapping.put("military defense", "DefenseAndMilitary"); // penalized

		c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "Process"); // neural
		c2cMapping2.put("ManufacturingService", "Service"); // neural
		c2cMapping2.put("military defense", "NULL"); // no such mapping
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping2), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		return XXX;
	}
	
	public static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords1() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Precision Machining";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("products", "Product"); // neural
		c2cMapping.put("ManufacturingService", "Service"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));


		instanceName = "Spiral Wound Gaskets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("types gaskets", "NULL"); // no such mapping
		c2cMapping.put("gaskets", "NULL"); // no such mapping
		//
		Map<String, String> c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping2.put("types gaskets", "NULL"); // no such mapping
		c2cMapping2.put("gaskets", "Product"); // boosting
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping2), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		return XXX;
	}
	
	public static Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords2() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Abrasive Waterjet Cutting";
		String originalClassName = "ManufacturingService";
		String updatedClassName = "WaterJetCutting";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // neural
		c2cMapping.put("capabilities", "Process"); // boosting
		c2cMapping.put("carbon steel shims", "CarbonSteel"); // neural
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		
		instanceName = "Shims";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService"); // penalized
		c2cMapping.put("ManufacturingService", "Service"); // neural
		c2cMapping.put("military defense", "DefenseAndMilitary"); // penalized

		Map<String, String> c2cMapping2 = new HashMap<String, String>();
		c2cMapping2.put("sheet metal fabrication", "Process"); // neural
		c2cMapping2.put("ManufacturingService", "Service"); // neural
		c2cMapping2.put("military defense", "NULL"); // no such mapping
		XXX.put(fakeDataCreator.createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName,
				c2cMapping2), fakeDataCreator.createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		return XXX;
	}
	
	@Ignore
	@Test
	public void xxx() throws IOException, NoSuchEntryItemException { 
		System.out.println("Aggregated Manufacturing Lexical Mapping Repository");
		System.out.println("");
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository1 = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
		
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository2 = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryTwoFullName);
		
		proprietaryManufacturingLexicalMappingRepository1.addNewConcept2OntoClassMappings(_updatedRecord2OriginalRecord1.values());
		proprietaryManufacturingLexicalMappingRepository2.addNewConcept2OntoClassMappings(_updatedRecord2OriginalRecord2.values());
		
		for (IInstanceRecord updatedRecord : _updatedRecord2OriginalRecord1.keySet()) {
			proprietaryManufacturingLexicalMappingRepository1.updateValidityOfConcept2OntClassMapping(updatedRecord,
					_updatedRecord2OriginalRecord1.get(updatedRecord));
		}
		
		for (IInstanceRecord updatedRecord : _updatedRecord2OriginalRecord2.keySet()) {
			proprietaryManufacturingLexicalMappingRepository2.updateValidityOfConcept2OntClassMapping(updatedRecord,
					_updatedRecord2OriginalRecord2.get(updatedRecord));
		}
		
		System.out.println("--------- Proprietary MLM one -----------------");
		proprietaryManufacturingLexicalMappingRepository1.showRepositoryDetail();
		
		System.out.println("---------- Proprietary MLM two----------------");
		proprietaryManufacturingLexicalMappingRepository2.showRepositoryDetail();
		
		proprietaryManufacturingLexicalMappingRepository1.saveRepository();
		proprietaryManufacturingLexicalMappingRepository2.saveRepository();
		
		IManufacturingLexicalMappingRecordsReader aggregratedManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createAggregratedManufacturingLexicalMappingRepository(OntologyRepositoryFactory.createOntologyRepository());
		System.out.println("---------- Aggregated MLM ----------------");
		aggregratedManufacturingLexicalMappingRepository.showRepositoryDetail();
		checkRepository(aggregratedManufacturingLexicalMappingRepository);
	}

	@Ignore
	@Test 
	public void SaveRepositoryTest() throws IOException {
		
		System.out.println("SaveRepositoryTest");
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
		proprietaryManufacturingLexicalMappingRepository.saveRepository();
	}

	@Ignore
	@Test
	public void LoadRepositoryTest() throws NoSuchEntryItemException, IOException { 
		System.out.println("LoadRepositoryTest");
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
				.createProprietaryManufacturingLexiconRepository(repositoryOneFullName);
		proprietaryManufacturingLexicalMappingRepository.loadRepository();
		this.checkRepository(proprietaryManufacturingLexicalMappingRepository);
	}

}
