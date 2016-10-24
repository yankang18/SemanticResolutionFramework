package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IModelSemanticResolver;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionCluster;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionRuleGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InterpretationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.testdata.FakeDataCreator;
import umbc.ebiquity.kang.textprocessing.impl.SequenceInReversedOrderPhraseExtractor;

public class ClassificationCorrectionRuleGeneratorTest {
	
private static IClassificationCorrectionRepository _correctionRepository;
private static IOntologyRepository _ontologyRepository;
private static ClassificationCorrectionRuleGenerator _ruleGenerator;
private static IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor;
private static IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingFeatureExtractor;
private static ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator;
private static IModelSemanticResolver _alg;
	
	@BeforeClass
	public static void init() throws IOException{ 
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		FileRepositoryParameterConfiguration.CLASSIFIED_INSTANCE_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule_NoNaive";
		FileRepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule_NoNaive";
		FileRepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule_NoNaive";
		
		
		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_correctionClusterCodeGenerator = new CorrectionClusterCodeGenerator();
		_correctionRepository = InterpretationCorrectionRepositoryFactory.createAggregratedClassificationCorrectionRepository();
		
		FakeDataCreator fakeDataCreator = new FakeDataCreator();
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = fakeDataCreator.createUpdatedInstanceRecordsAndClassifiedInstanceRecords();
		for (IInstanceRecord updatedInstanceRecord : XXX.keySet()) {
			IClassifiedInstanceDetailRecord originalClassifiedInstance = XXX.get(updatedInstanceRecord);
			_correctionRepository.extractCorrection(updatedInstanceRecord, originalClassifiedInstance);
		}
		
		_instanceC2CMappingFeatureExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(_correctionClusterCodeGenerator, _correctionRepository, _ontologyRepository);
		IClassifiedInstancesAccessor classifiedInstances = ClassifiedInstancesRepositoryFactory.createAggregatedClassifiedInstancesRepository(_ontologyRepository);
//		classifiedInstances.showRepositoryDetail();
		_instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(classifiedInstances, new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor()));
		
		_ruleGenerator = new ClassificationCorrectionRuleGenerator
		(
				_correctionRepository,
				_ontologyRepository,
				_correctionClusterCodeGenerator,
				_instanceC2CMappingFeatureExtractor, 
				_instanceLexicalFeatureExtractor
		);
		
		System.out.println("------------------------ Show Classification Correction Rules ------------------------");
		_ruleGenerator.showClassificationCorrectionRules();
		
	}
	
	@Ignore
	@Test
	public void CorrectionClusterCodeGeneratorTest(){
		String sourceClass = "Machining";
		String targetClass = "WaterJetCutting";
		String code1 = _correctionClusterCodeGenerator.generateCorrectionClusterCode(new CorrectionDirection(sourceClass,targetClass));
		System.out.println(sourceClass + ", " + targetClass + ": " + code1);
	}
	
//	@Ignore
	@Test
	public void GenerateClassificationCorrectionRules() throws IOException {
		
		System.out.println("------------------------ GenerateClassificationCorrectionRules------------------------");
		for (IClassifiedInstanceDetailRecord classifiedInstance : this.createClassifiedInstanceDetailInfo()) {
			System.out.println();
			ICorrectionRule rule = _ruleGenerator.getClassificationCorrectionRule(classifiedInstance);
//			rule.showDetail();
			String targetClass = rule.obtainCorrectedClassLabel(classifiedInstance, classifiedInstance.getOntoClassName());
			System.out.println("The Correct Class: " + targetClass);
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}
	
//	@Ignore
//	@Test
//	public void ApplyClassificationCorrectionRules() throws IOException {
//		
////		URL webSiteURL = new URL("http://www.astromfg.com");
////		URL webSiteURL = new URL("http://www.numericalconcepts.com");
////		URL webSiteURL = new URL("http://www.weaverandsons.com");
////		URL webSiteURL = new URL("http://www.aerostarmfg.com");
////		URL webSiteURL = new URL("http://www.plastechonline.com/index.html");
//		URL webSiteURL = new URL("http://www.michiganmechanical.com/index.html");
//		ITripleRepository tripleStore = TripleRepositoryFactory.createTripleRepository(webSiteURL, true);
////		IClassificationCorrectionRepository classificationCorrectionRepository = ClassificationCorrectionRepositoryFactory.createRepository();
////		classificationCorrectionRepository.loadRepository();
////		classificationCorrectionRepository.computeMappingStatistics();
////		classificationCorrectionRepository.showRepositoryDetail();
//		_alg = new TS2OntoMappingAlgorithm(tripleStore, _ontologyRepository, _correctionRepository,
//				ManufacturingLexicalMappingRepositoryFactory.createManufacturingLexiconRepository());
//		
//		_alg.mapping();
//		IMappingInfoRepository mappingRepository = _alg.getMappingInfoRepository();
//		MappingDetailInfo mappingDetailInfo = mappingRepository.getMappingDetailInfo();
//		Collection<IClassifiedInstanceDetailRecord> instanceDetailInfoCollection = mappingDetailInfo.getClassifiedInstanceDetailInfoCollection();
//		
//		System.out.println("==================================================================");
//		System.out.println("Automatically Correction based-on Correction Rules");
//		System.out.println();
//		int threshold_50 = 0;
//		int threshold_60 = 0;
//		for (IClassifiedInstanceDetailRecord instanceDetailInfo : instanceDetailInfoCollection) {
//			System.out.println();
//			String instanceName = instanceDetailInfo.getInstanceLabel();
//			String className = instanceDetailInfo.getOntoClassName();
//			System.out.println("--- <" + instanceName + "> of type <" + className + ">");
//			ICorrectionRule rule = _ruleGenerator.getClassificationCorrectionRule(instanceDetailInfo);
//			String targetClassName = rule.getTargetClass(instanceDetailInfo);
//		}
//	}

	private List<IClassifiedInstanceDetailRecord> createClassifiedInstanceDetailInfo() throws IOException {
		List<IClassifiedInstanceDetailRecord> classifiedInstanceRecordList = new ArrayList<IClassifiedInstanceDetailRecord>();
		FakeDataCreator fakeDataCreator = new FakeDataCreator(); 
		String instanceName = "Abrasive Waterjet Cutting";
		String className = "ManufacturingService";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("process", "Process");
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("carbon steel shims", "CarbonSteel");
		classifiedInstanceRecordList.add(fakeDataCreator.createClassifiedInstanceRecord(instanceName, className, c2cMapping));
		
		instanceName = "Abrasive Waterjet Cutting";
		className = "SheetMetalService";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("capabilities", "Process");
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("sheet metal fabrications", "SheetMetalService");
		classifiedInstanceRecordList.add(fakeDataCreator.createClassifiedInstanceRecord(instanceName, className, c2cMapping));
		
		instanceName = "Abrasive Waterjet Cutting";
		className = "Machining";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("process", "Process");
		c2cMapping.put("mechanicalmachining", "MechanicalMachining");
		c2cMapping.put("waterjetcutting", "WaterJetCutting");
		classifiedInstanceRecordList.add(fakeDataCreator.createClassifiedInstanceRecord(instanceName, className, c2cMapping));
		return classifiedInstanceRecordList;
	}
	
	@Ignore
	@Test
	public void ClassificationCorrectionClusterFeatureTest(){
		String clusterCode = "Service@23";
		String sourceClass = "Service";
		String targetClass ="Process";
		
		CorrectionDirection correctionDirection = new CorrectionDirection(sourceClass, targetClass);
		ClassificationCorrectionCluster cluster = new ClassificationCorrectionCluster(clusterCode, 
																					  correctionDirection, 
				                                                                      _instanceLexicalFeatureExtractor,
				                                                                      _instanceC2CMappingFeatureExtractor);
		cluster.extractFeatures();
		cluster.showFeatures();
	}
}
