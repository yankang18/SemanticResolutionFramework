package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingBasicInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingDetailInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMappingInfoRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IUpdatedInstanceRecord;

public class MappingInformationRepositoryTest {
	private static IMappingInfoRepository _mappingResult ;
	private static IManufacturingLexicalMappingRepository _MLReposiory;
	private static IOntologyRepository _ontologyRepository;
    private static IClassificationCorrectionRepository _classificationCorrectionRepository;

//	@BeforeClass
//	public static void LoadDataFromFile() throws IOException {
//		
//		// String homeURL = "http://www.accutrex.com/";
//		// String homeURL = "http://www.weaverandsons.com/";
//		// String homeURL = "http://www.bassettinc.com/";
//		String homeURL = "http://www.numericalconcepts.com/";
//		// String homeURL = "http://www.aerostarmfg.com/";
//		// String homeURL = "http://www.astromfg.com/";
//		// String homeURL = "http://www.navitekgroup.com/";
//		// String homeURL = "http://www.cmc-usa.com/";
//		
//		_MLReposiory = new ManufacturingLexicalMappingRepository();
//		_ontologyRepository = new DomainOntologyRepository();
//		_classificationCorrectionRepository = new ClassificationCorrectionRepository(OntologyRepositoryFactory.createOntologyRepository());
//		_mappingResult = new MappingInformationRepository(homeURL, MappingInfoRepositoryType.All, _ontologyRepository, _classificationCorrectionRepository, _MLReposiory);
//	}
	
	@Test
	public void updateMappingInfoRepositoryWithUpdatedInstance(){
		IUpdatedInstanceRecord instanceRecordSetter = _mappingResult.createInstanceClassificationRecord();
		
		String origInstanceName = "CNC Vertical & Horizontal Milling";
		String updatedInstanceLabel = "CNC Vertical and Horizontal Milling";
		String origOntoClassName = "Milling";
		String updatedOntoClassLabel = "Milling";
		
		instanceRecordSetter.setOriginalInstanceName(origInstanceName);
		instanceRecordSetter.setOriginalClassName(origOntoClassName);
		instanceRecordSetter.setUpdatedInstanceName(updatedInstanceLabel);
		instanceRecordSetter.setUpdatedClassName(updatedOntoClassLabel);
		instanceRecordSetter.isUpdatedInstance(true);
		
		Collection<IUpdatedInstanceRecord> instances = new ArrayList<IUpdatedInstanceRecord>();
		instances.add(instanceRecordSetter);
		
		_mappingResult.updateMappingInfo(instances);
		IClassifiedInstanceBasicRecord basicInfo1 = _mappingResult.getClassifiedInstanceBasicInfoByInstanceName(origInstanceName);
		assertNull(basicInfo1);
		
		IClassifiedInstanceBasicRecord basicInfo2 = _mappingResult.getClassifiedInstanceBasicInfoByInstanceName(updatedInstanceLabel);
		assertNotNull(basicInfo2);
		assertEquals("Milling", basicInfo2.getOntoClassName());
	}
	
	@Test
	public void updateMappingInfoRepositoryWithUpdatedClass(){ 
		
		IUpdatedInstanceRecord instanceRecordSetter = _mappingResult.createInstanceClassificationRecord();
		
		String origInstanceName = "Milling";
		String updatedInstanceLabel = "Milling";
		String origOntoClassName = "Milling";
		String updatedOntoClassLabel = "Process";
		
		instanceRecordSetter.setOriginalInstanceName(origInstanceName);
		instanceRecordSetter.setOriginalClassName(origOntoClassName);
		instanceRecordSetter.setUpdatedInstanceName(updatedInstanceLabel);
		instanceRecordSetter.setUpdatedClassName(updatedOntoClassLabel);
		instanceRecordSetter.isUpdatedInstance(true);
		
		Collection<IUpdatedInstanceRecord> instances = new ArrayList<IUpdatedInstanceRecord>();
		instances.add(instanceRecordSetter);
		
		_mappingResult.updateMappingInfo(instances);
		IClassifiedInstanceBasicRecord basicInfo = _mappingResult.getClassifiedInstanceBasicInfoByInstanceName(origInstanceName);
		assertNotNull(basicInfo);
		assertEquals("Process", basicInfo.getOntoClassName());
		
	}
	
	
	@Test
	public void updateMappingInfoRepositoryWithUpdatedConcept2ClassMappingPenalizing() throws NoSuchEntryItemException{ 
		System.out.println("------------------------------------ ");
		
		IUpdatedInstanceRecord instanceRecordSetter = _mappingResult.createInstanceClassificationRecord();
		
		String origInstanceName = "Blanchard Grinder";
		String updatedInstanceLabel = "Blanchard Grinder";
		String origOntoClassName = "Part";
		String updatedOntoClassLabel = "Part";
		
		String conceptName = "equipment manufacture part";
		String oldClassName = "Part";
		String newClassName = "Product";
		double similarity = 0.9;
		MappingRelationType relation = MappingRelationType.relatedTo;
		instanceRecordSetter.setOriginalInstanceName(origInstanceName);
		instanceRecordSetter.setOriginalClassName(origOntoClassName);
		instanceRecordSetter.setUpdatedInstanceName(updatedInstanceLabel);
		instanceRecordSetter.setUpdatedClassName(updatedOntoClassLabel);
		instanceRecordSetter.isUpdatedInstance(true);

		OntoClassInfo newOntClass = _ontologyRepository.getLightWeightOntClassByName(newClassName);
		OntoClassInfo oldOntClass = _ontologyRepository.getLightWeightOntClassByName(oldClassName);
		Concept concept = new Concept(conceptName);
		instanceRecordSetter.addConcept2OntClassMappingPair(concept, relation, newOntClass, true, false, similarity);
		Collection<IUpdatedInstanceRecord> instances = new ArrayList<IUpdatedInstanceRecord>();
		instances.add(instanceRecordSetter);
		
		// get statistic information before updating the concept-to-class mapping
		IConcept2OntClassMappingStatistics statistics0 = _MLReposiory.getConcept2ClassMappingStatistics(concept, oldOntClass);
		int mt0 = statistics0.getUndeterminedCounts();
		int ft0 = statistics0.getFailedCounts();
		int st0 = statistics0.getSucceedCounts();
		double sim0 = statistics0.getSimilarity();
		System.out.println("MT0 " + mt0);
		System.out.println("FT0 " + ft0);
		System.out.println("ST0 " + st0);
		System.out.println("SIM0 " + sim0);
		
		_mappingResult.updateMappingInfo(instances);
		IClassifiedInstanceDetailRecord detailInfo = _mappingResult.getClassifiedInstanceDetailInfoByInstanceName(origInstanceName);
		assertNotNull(detailInfo);
		
		// To check if the new concept-to-class mapping updated
		Collection<IConcept2OntClassMapping> concept2OntClassMappingPairs = detailInfo.getConcept2OntClassMappingPairs();
		for (IConcept2OntClassMapping c : concept2OntClassMappingPairs) {
			if(c.getConceptName().endsWith(conceptName)){
				String mappedClassName = c.getMappedOntoClass().getOntClassName();
				assertEquals(newClassName, mappedClassName);
			}
		}
		
		// get the statistic information after penalizing the old concept-to-class mapping
		IConcept2OntClassMappingStatistics statistics1 = _MLReposiory.getConcept2ClassMappingStatistics(concept, oldOntClass);
		int mt1 = statistics1.getUndeterminedCounts();
		int ft1 = statistics1.getFailedCounts();
		int st1 = statistics1.getSucceedCounts();
		double sim1 = statistics1.getSimilarity();
		System.out.println("MT1 " + mt1);
		System.out.println("FT1 " + ft1);
		System.out.println("ST1 " + st1);
		System.out.println("SIM1 " + sim1);
		assertEquals(mt0 + 1, mt1);
		assertEquals(ft0 + 1, ft1);
		assertEquals(st0, st1);
		
		// get the statistic information after adding the new concept-to-class mapping
		IConcept2OntClassMappingStatistics statistics2 = _MLReposiory.getConcept2ClassMappingStatistics(concept, newOntClass);
		int mt = statistics2.getUndeterminedCounts();
		int ft = statistics2.getFailedCounts();
		int st = statistics2.getSucceedCounts();
		double sim = statistics2.getSimilarity();
		System.out.println("MT2 " + mt);
		System.out.println("FT2 " + ft);
		System.out.println("ST2 " + st);
		System.out.println("SIM2 " + sim);
	}
	
	@Test
//	@Ignore
	public void updateMappingInfoRepositoryWithUpdatedConcept2ClassMappingBoosting() throws NoSuchEntryItemException {
		System.out.println("------------------------------------ ");
		
		IUpdatedInstanceRecord instanceRecordSetter = _mappingResult.createInstanceClassificationRecord();

		String origInstanceName = "Finish Grinding";
		String updatedInstanceLabel = "Finish Grinding";
		String origOntoClassName = "Grinding";
		String updatedOntoClassLabel = "Grinding";

		String conceptName = "machining processes";
		String oldClassName = "Process";
		String newClassName = "Machining";
		double similarity = 0.9;
		MappingRelationType relation = MappingRelationType.relatedTo;
		instanceRecordSetter.setOriginalInstanceName(origInstanceName);
		instanceRecordSetter.setOriginalClassName(origOntoClassName);
		instanceRecordSetter.setUpdatedInstanceName(updatedInstanceLabel);
		instanceRecordSetter.setUpdatedClassName(updatedOntoClassLabel);
		instanceRecordSetter.isUpdatedInstance(true);
//		instanceRecordSetter.addConcept2ClassMapping(conceptName, relation, newClassName, similarity, isMappedConcept);

		OntoClassInfo newOntClass = _ontologyRepository.getLightWeightOntClassByName(newClassName);
		OntoClassInfo oldOntClass = _ontologyRepository.getLightWeightOntClassByName(oldClassName);
		Concept concept = new Concept(conceptName);
		
		instanceRecordSetter.addConcept2OntClassMappingPair(concept, relation, newOntClass, true, false, similarity);

		Collection<IUpdatedInstanceRecord> instances = new ArrayList<IUpdatedInstanceRecord>();
		instances.add(instanceRecordSetter);

		IConcept2OntClassMappingStatistics statistics0 = _MLReposiory.getConcept2ClassMappingStatistics(concept, oldOntClass);
		int mt0 = statistics0.getUndeterminedCounts();
		int ft0 = statistics0.getFailedCounts();
		int st0 = statistics0.getSucceedCounts();
		double sim0 = statistics0.getSimilarity();
		System.out.println("MT0 " + mt0);
		System.out.println("FT0 " + ft0);
		System.out.println("ST0 " + st0);
		System.out.println("SIM0 " + sim0);
		
		_mappingResult.updateMappingInfo(instances);
		IClassifiedInstanceDetailRecord detailInfo = _mappingResult.getClassifiedInstanceDetailInfoByInstanceName(origInstanceName);
		assertNotNull(detailInfo);
		
	    Collection<IConcept2OntClassMapping> concept2OntClassMappingPairs = detailInfo.getConcept2OntClassMappingPairs();
		
		for (IConcept2OntClassMapping c : concept2OntClassMappingPairs) {
			if (c.getConceptName().endsWith(conceptName)) {
				String mappedClassName = c.getMappedOntoClass().getOntClassName();
				assertEquals(newClassName, mappedClassName);
			}
		}

		IConcept2OntClassMappingStatistics statistics1 = _MLReposiory.getConcept2ClassMappingStatistics(concept, oldOntClass);
		int mt1 = statistics1.getUndeterminedCounts();
		int ft1 = statistics1.getFailedCounts();
		int st1 = statistics1.getSucceedCounts();
		double sim1 = statistics1.getSimilarity();
		System.out.println("MT1 " + mt1);
		System.out.println("FT1 " + ft1);
		System.out.println("ST1 " + st1);
		System.out.println("SIM1 " + sim1);

		assertEquals(mt0, mt1);
		assertEquals(ft0, ft1);
		assertEquals(st0, st1);
		assertEquals(sim0, sim1, 1.0);
		
		IConcept2OntClassMappingStatistics statistics2 = _MLReposiory.getConcept2ClassMappingStatistics(concept, newOntClass);
		int mt2 = statistics2.getUndeterminedCounts();
		int ft2 = statistics2.getFailedCounts();
		int st2 = statistics2.getSucceedCounts();
		double sim2 = statistics2.getSimilarity();
		System.out.println("MT2 " + mt2);
		System.out.println("FT2 " + ft2);
		System.out.println("ST2 " + st2);
		System.out.println("SIM2 " + sim2);

	}
	
	@Test
	public void showMappingRepositoryResult(){

		MappingBasicInfo mappingBasicInfo = _mappingResult.getMappingBasicInfo();
		MappingDetailInfo mappingDetailInfo = _mappingResult.getMappingDetailInfo();

		System.out.println("======================== Basic Instance Classification Info ========================");
		System.out.println();
		System.out.println();
		for (IClassifiedInstanceBasicRecord basicInfo : mappingBasicInfo.getClassifiedInstanceBasicInfoCollection()) {
			System.out.println("--------------------------------------------------");
			System.out.println("Instance Name  : " + basicInfo.getInstanceLabel());
			System.out.println("Class Name     : " + basicInfo.getOntoClassName());
			System.out.println("Class NameSpace: " + basicInfo.getOntoClassNameSpace());
			System.out.println("Class URI      : " + basicInfo.getOntoClassURI());
			System.out.println("Similarity     : " + basicInfo.getSimilarity());
		}

		System.out.println();
		System.out.println();
		System.out.println("======================== Detail Instance Classification Info ========================");
		System.out.println();
		System.out.println();
		for (IClassifiedInstanceDetailRecord detailedInfo : mappingDetailInfo.getClassifiedInstanceDetailRecords()) {
			System.out.println("--------------------------------------------------");
			System.out.println("Instance Name  : " + detailedInfo.getInstanceLabel());
			System.out.println("Class Name     : " + detailedInfo.getOntoClassName());
			System.out.println("Class NameSpace: " + detailedInfo.getOntoClassNameSpace());
			System.out.println("Class URI      : " + detailedInfo.getOntoClassURI());
			System.out.println("Similarity     : " + detailedInfo.getSimilarity());

			for (OntoClassInfo rcl1 : detailedInfo.getSuperOntoClassesOfMatchedOntoClass()) {
				System.out.println("rcl1-1 :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
			}

			for (OntoClassInfo rcl1 : detailedInfo.getFirstLevelRecommendedOntoClasses()) {
				System.out.println("rcl1   :  " + rcl1.getURI() + " " + rcl1.getOntClassName());
			}

			for (OntoClassInfo rcl2 : detailedInfo.getSecondLevelRecommendedOntoClasses()) {
				System.out.println("rcl2   :  " + rcl2.getURI());
			}

			for (IConcept2OntClassMapping mappingPair : detailedInfo.getConcept2OntClassMappingPairs()) {
				System.out.println("concept : " + mappingPair.getConceptName());
				if (mappingPair.isMappedConcept()) {
//					String ontoClassLabel = concept.getMappedOntoClass().getLabel();
					String ontoClassURI = mappingPair.getMappedOntoClass().getURI();
					double mappingSimilarity = mappingPair.getMappingScore();
					System.out.println("       mapped to: " + ontoClassURI + " with similairty: " + mappingSimilarity);
				} else {

				}
			}
		}
	}
	
	
	@Test
	public void showManufacturingLexiconRepository(){
		_MLReposiory.showRepositoryDetail();
	}
}
