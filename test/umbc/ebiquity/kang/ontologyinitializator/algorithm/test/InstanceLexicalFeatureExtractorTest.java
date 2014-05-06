package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.textprocessing.impl.SequenceInReversedOrderPhraseExtractor;

public class InstanceLexicalFeatureExtractorTest {

	// private static IClassifiedInstancesAccessor _repo;
	private static IOntologyRepository _ontoRepo;
	private static IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor;

	@BeforeClass
	public static void init() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		RepositoryParameterConfiguration.CLASSIFIED_INSTANCE_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule";
		RepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule";
		RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_HOST_DIRECTORY = "/Users/yankang/Desktop/Test_NoRule";
		
		_ontoRepo = OntologyRepositoryFactory.createOntologyRepository();
		// _repo =
		// ClassifiedInstancesRepositoryFactory.createClassifiedInstancesRepository(OntologyRepositoryFactory
		// .createOntologyRepository());

		_instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(
				ClassifiedInstancesRepositoryFactory.createAggregatedClassifiedInstancesRepository(_ontoRepo), new SimpleLexicalFeatureExtractor(
						new SequenceInReversedOrderPhraseExtractor()));

	}

	@Test
	public void GetInstanceLexicalFeatureTest() {
		IClassifiedInstancesAccessor classifiedInstanceAccessor = ClassifiedInstancesRepositoryFactory
				.createAggregatedClassifiedInstancesRepository(_ontoRepo);
		classifiedInstanceAccessor.showRepositoryDetail();
		
		IInstanceLexicalFeatureExtractor instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(classifiedInstanceAccessor,
				new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor()));

		String className1 = "Material";
		String className2 = "Process";
		String className3 = "SubtractionProcess";
		String className4 = "EngineeringService";
		String className5 = "StainlessSteel";

		outputFeatures(className1, instanceLexicalFeatureExtractor.getInstanceLexicalFeaturesOfOntClass(className1));
		outputFeatures(className2, instanceLexicalFeatureExtractor.getInstanceLexicalFeaturesOfOntClass(className2));
		outputFeatures(className3, instanceLexicalFeatureExtractor.getInstanceLexicalFeaturesOfOntClass(className3));
		outputFeatures(className4, instanceLexicalFeatureExtractor.getInstanceLexicalFeaturesOfOntClass(className4));
		outputFeatures(className5, instanceLexicalFeatureExtractor.getInstanceLexicalFeaturesOfOntClass(className5));

	}

	@Ignore
	@Test
	public void GetLexicalFeatureWithGlobalStatistics() {

		String className1 = "Material";
		String className2 = "Process";
		String className3 = "SubtractionProcess";
		String className4 = "EngineeringService";
		String className5 = "ManufacturingService";

		// Map<LexicalFeature, Integer> feature2ClassCount =
		// _instanceLexicalFeatureExtractor.getLexicalFeatureWithOntClassCount();

		Map<LexicalFeature, Double> lexicalFeatures = _instanceLexicalFeatureExtractor
				.getLexicalFeaturesWithRepresentativenessOfOntClass(className2);
		print(lexicalFeatures, "Representativeness");

		System.out.println();
		Map<LexicalFeature, Double> lexicalFeatures2 = _instanceLexicalFeatureExtractor
				.getLexicalFeaturesWithRepresentativenessOfOntClass(className3);
		print(lexicalFeatures2, "Representativeness2");

		// for(LexicalFeature f : feature2ClassCount.keySet()){
		// System.out.println(f.toString() + "   Class Count: " +
		// feature2ClassCount.get(f));
		// }
		// Map<LexicalFeature, Double> feature2NICF =
		// _instanceLexicalFeatureExtractor.getLexicalFeatureWithNormalizedInverseOntClassFrequency();
		// for(LexicalFeature f : feature2NICF.keySet()){
		// System.out.println(f.toString() + "   NICF Count: " +
		// feature2NICF.get(f));
		// }
	}

	private void print(Map<LexicalFeature, Double> lexicalFeatures, String label) {
		for (LexicalFeature f : lexicalFeatures.keySet()) {
			double representativeness = lexicalFeatures.get(f);
			System.out.println(f.getLabel() + ",   " + label + ": " + representativeness);
		}
	}

	private void outputFeatures(String className, Map<LexicalFeature, LexicalFeature> features) {
		System.out.println("Features of " + className);
		for (LexicalFeature feature : features.keySet()) {
			System.out.println("  -- Feature: " + feature.toString() + " <Significant:" + feature.getSignificant() + ">  <Support:"
					+ feature.getSupport() + ">  <Count: " + feature.getCount() + ">");
		}
	}
}
