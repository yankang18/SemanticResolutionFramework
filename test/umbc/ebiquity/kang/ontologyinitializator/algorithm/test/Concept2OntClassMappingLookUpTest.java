package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;
import java.io.IOException;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMappingPairPruner;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class Concept2OntClassMappingLookUpTest {

	private static Concept2OntClassMappingPairLookUpper lookUpper;

	@BeforeClass
	public static void LoadDataFromFile() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		
		FileRepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/MLM";
		IManufacturingLexicalMappingRepository _MLReposiory = ManufacturingLexicalMappingRepositoryFactory.createProprietaryManufacturingLexiconRepository("_MLM");
		_MLReposiory.showRepositoryDetail();
		IOntologyRepository _ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		lookUpper = new Concept2OntClassMappingPairLookUpper(0.75, _MLReposiory, _ontologyRepository);
		
	}
	
	@Test
	public void lookUpConceptWithOneMappedClassTest() throws IOException { 
		System.out.println();
		Concept concept = new Concept("fabrication");
		Collection<Concept2OntClassMapping> mappingPairs = lookUpper.lookupConcept2OntClassMappingPairs(concept);
		
		for(Concept2OntClassMapping mappingPair:mappingPairs){
			System.out.println("<" + mappingPair.getConceptName() + "  " + mappingPair.getMappedOntoClassName() + "  " + mappingPair.getMappingScore() + ">");
		}
		
		Concept concept2 = new Concept("shearing");
		Collection<Concept2OntClassMapping> mappingPairs2 = lookUpper.lookupConcept2OntClassMappingPairs(concept2);
		
		for(Concept2OntClassMapping mappingPair:mappingPairs2){
			System.out.println("<" + mappingPair.getConceptName() + "  " + mappingPair.getMappedOntoClassName() + "  " + mappingPair.getMappingScore() + ">");
		}
	}

	@Ignore
	@Test
	public void lookUpConceptWithMultipleMappedClassesInSameHierarchyPathTest() {
		System.out.println();
		Concept concept = new Concept("Precise Grinding");
		Collection<Concept2OntClassMapping> mappingPairs = lookUpper.lookupConcept2OntClassMappingPairs(concept);
		
		
		for(Concept2OntClassMapping mappingPair:mappingPairs){
			System.out.println("<" + mappingPair.getConceptName() + "  " + mappingPair.getRelation().toString() + "  " + mappingPair.getMappedOntoClassName() + ">");
		}
	}
	
	@Ignore
	@Test
	public void lookUpConceptWithMultipleMappedClassesInSameHierarchyButInDifferentPathTest() {
		System.out.println();
		Concept concept = new Concept("Precise StraightTurning");
		Collection<Concept2OntClassMapping> mappingPairs = lookUpper.lookupConcept2OntClassMappingPairs(concept);
		
		for(Concept2OntClassMapping mappingPair:mappingPairs){
			System.out.println("<" + mappingPair.getConceptName() + "  " + mappingPair.getRelation().toString() + "  " + mappingPair.getMappedOntoClassName() + ">");
		}
	}
	
	@Ignore
	@Test
	public void lookUpConceptWithMultipleMappedClassesInDifferentHierarchyTest() {
		System.out.println();
		Concept concept = new Concept("capability");
		Collection<Concept2OntClassMapping> mappingPairs = lookUpper.lookupConcept2OntClassMappingPairs(concept);

		for (Concept2OntClassMapping mappingPair : mappingPairs) {
			System.out.println("<" + mappingPair.getConceptName() + "  " + mappingPair.getRelation().toString() + "  "
					+ mappingPair.getMappedOntoClassName() + ">");
		}

		IConcept2OntClassMappingPairPruner concept2OntClassMappingPairPruner = new Concept2OntClassMappingPairPruner();
		
		Collection<IConcept2OntClassMapping> determinedMappingPairs = concept2OntClassMappingPairPruner.resolveOne2OneOntoClassMappingPairs(23, mappingPairs);
		for (IConcept2OntClassMapping mappingPair : determinedMappingPairs) {
			System.out.println("D1: <" + mappingPair.getConceptName() + "  " + mappingPair.getRelation().toString() + "  " + mappingPair.getMappedOntoClassName() + ">");
		}
		
		Collection<IConcept2OntClassMapping> determinedMappingPairs2 = concept2OntClassMappingPairPruner.resolveOne2OneOntoClassMappingPairs(3, mappingPairs);
		for (IConcept2OntClassMapping mappingPair : determinedMappingPairs2) {
			System.out.println("D2: <" + mappingPair.getConceptName() + "  " + mappingPair.getRelation().toString() + "  " + mappingPair.getMappedOntoClassName() + ">");
		}
	}

	@Test
	@Ignore
	public void lookUpConceptWithMultipleMappedClassesInDifferentHierarchyTest2() {
		System.out.println();
	}
}
