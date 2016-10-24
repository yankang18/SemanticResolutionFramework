package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.BestMatchedOntClassFinder;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IBestMatchedOntClassFinder;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InterpretationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;


public class InstanceClassificationAlgorithmTest {
	
	private IOntologyRepository ontologyRepository;
	private IManufacturingLexicalMappingRecordsReader MLRepository;
	
	@Before
	public void Init() throws IOException{ 
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		
		FileRepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/MLM";
		
		ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		MLRepository = ManufacturingLexicalMappingRepositoryFactory.createAggregratedManufacturingLexicalMappingRepository(ontologyRepository);
	}
	
	@Test
	public void BundleTest() throws IOException{ 
		Concept2OntClassMapperTest();
//		BestMatchedOntClassFinderTest();
//		PruneConcept2OntClassMappingPairTest();
	}

	private Collection<Concept2OntClassMapping> mappingPairs;
	private Collection<Concept> concepts;
	public void Concept2OntClassMapperTest(){
		IConcept2OntClassMappingPairLookUpper concept2OntClassMappingPairLookUpper = new Concept2OntClassMappingPairLookUpper(MLRepository, ontologyRepository);
		IConcept2OntClassMapper concept2OntClassMapper = new Concept2OntClassMapper(concept2OntClassMappingPairLookUpper, true);
//		Map<String, String> domainSpecificConceptMap = this.createDomainSpecificConceptMap();
//		concept2OntClassMapper.setDomainSpecificConceptMap(domainSpecificConceptMap);
		
		mappingPairs = concept2OntClassMapper.mapConcept2OntClass(this.createConceptCollection(), ontologyRepository.getAllOntClasses());
		for(Concept2OntClassMapping mappingPair : mappingPairs){
			System.out.println("1: " + mappingPair.getConceptName() + " --> " + mappingPair.getMappedOntoClassName() + "  " +  mappingPair.getMappingScore());
		}
	}
	
	private Map<String, String> createDomainSpecificConceptMap(){
		Map<String, String> domainSpecificConceptMap = new HashMap<String, String>();
//		domainSpecificConceptMap.put("capability", "process");
		domainSpecificConceptMap.put("capability", "service");
		return domainSpecificConceptMap;
	}
	
	private Collection<Concept> createConceptCollection(){
		concepts = new LinkedHashSet<Concept>();
		Concept concept1 = new Concept("Capability");
		Concept concept2 = new Concept("Machining Service");
		Concept concept3 = new Concept("Assembly Services");
		concept1.setScore(1.0);
		concept2.setScore(1.0);
		concept3.setScore(1.0);
		concepts.add(concept1);
		concepts.add(concept2);
		concepts.add(concept3);
		return concepts;
	}
	
	private MatchedOntoClassInfo matchedOntClassResult;
	private String instancelabel = "cnc turning";
	public void BestMatchedOntClassFinderTest() throws IOException{
		IBestMatchedOntClassFinder bestMatchedOntClassFinder = new BestMatchedOntClassFinder(ontologyRepository);
		IClassificationCorrectionRepository aggregatedClassificationCorrectionRepository = null;
		mappingPairs = new ArrayList<Concept2OntClassMapping>();
		matchedOntClassResult = bestMatchedOntClassFinder.findBestMatchedOntoClass(instancelabel, mappingPairs, aggregatedClassificationCorrectionRepository);
		System.out.println("class: " + matchedOntClassResult.getMatchedOntoClassInfo().getOntClassName());
	}
	
	public void PruneConcept2OntClassMappingPairTest(){
		IConcept2OntClassMappingPairPruner concept2OntClassMappingPairPruner = new Concept2OntClassMappingPairPruner();
		Collection<IConcept2OntClassMapping> prunedConcept2OntClassMappingPairs = concept2OntClassMappingPairPruner.getPrunedConcept2OntoClassMappingPairs(matchedOntClassResult.getClassHierarchyNumber(), instancelabel, concepts, mappingPairs);
		for (IConcept2OntClassMapping pair : prunedConcept2OntClassMappingPairs) {
			if (pair.isMappedConcept()) {
				System.out.println("Mapped Concept: " + pair.getConceptName() + " <-> " + pair.getMappedOntoClass().getOntClassName());
			} else {
				System.out.println("unMapped Concept:" + pair.getConceptName());
			}
		}
	}
}
