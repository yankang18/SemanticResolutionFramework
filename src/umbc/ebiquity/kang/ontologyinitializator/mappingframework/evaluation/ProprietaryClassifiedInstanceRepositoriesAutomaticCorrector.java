package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.TS2OntoMappingAlgorithm2;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.AbstractWebUrlLoader.PopulationType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionRuleGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InterpretationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.TripleRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.EvaluationCorpus;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.EvaluationCorpusRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MappingDataGateway;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository.ClassifiedInstancesRepositoryType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.textprocessing.impl.SequenceInReversedOrderPhraseExtractor;

public class ProprietaryClassifiedInstanceRepositoriesAutomaticCorrector extends AbstractWebUrlLoader {

    private enum TestType {
    	None, Rule_Bayes, Rule, Bayes
    }
    
	private String _hostDirectory;
	private String _evaluationResultDirectory;
	private String _goldenStandardRepositoriesDir;

	private IOntologyRepository _ontologyRepository;
	private InterpretationEvaluator _evaluator;
	public static void main(String[] args) throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		String fileFullPath = "/Users/yankang/Desktop/WebSiteURLs.txt";
		
		String dir = "Test_None";
//		String dir = "Test_Rule_Naive";
//		String dir = "Test_Rule";
//		String dir = "Test_Naive";
		
//		TestType testType;
//		testType = TestType.None;
//		testType = TestType.Bayes;
//		testType = TestType.Rule;
//		testType = TestType.Rule_Bayes;

		
		ProprietaryClassifiedInstanceRepositoriesAutomaticCorrector PCIRAC = new ProprietaryClassifiedInstanceRepositoriesAutomaticCorrector(
				"/Users/yankang/Desktop/" + dir, "/Users/yankang/Desktop/standards/", "/Users/yankang/Desktop/" + dir + "/EvaluationResult/");
		PCIRAC.loadRecords(fileFullPath);
		PCIRAC.createProprietaryClassifiedInstanceRepositories(PopulationType.CRAWL_INDICATED);
	}

	public ProprietaryClassifiedInstanceRepositoriesAutomaticCorrector(String hostDirectory, 
			String goldenStandardRepositoriesDir, 
			String evaluationResultDirectory)
			throws IOException {
		_hostDirectory = hostDirectory;
		_goldenStandardRepositoriesDir = goldenStandardRepositoriesDir;
		_evaluationResultDirectory = evaluationResultDirectory;
		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_evaluator = new InterpretationEvaluator(_ontologyRepository);
	}

	public void createProprietaryClassifiedInstanceRepositories(PopulationType populationType) throws IOException {

		Map<String, Boolean> crawlIndicators;
		switch (populationType) {
		case ONLY_CRAWL_FAILED:
			crawlIndicators = webSiteRecrawlFailed;
			break;
		case CRAWL_INDICATED:
			crawlIndicators = webSiteRecrawlIndicated;
			break;
		case CRAWL_ALL:
			crawlIndicators = webSiteRecrawlAll;
			break;
		default:
			crawlIndicators = webSiteRecrawlFailed;
		}
		
		RepositoryParameterConfiguration.CLASSIFIED_INSTANCE_HOST_DIRECTORY = _hostDirectory;
		RepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = _hostDirectory;
		RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_HOST_DIRECTORY = _hostDirectory;
		
		boolean applyMappingRule = false;
		boolean applyCorrections = false;
		
		for (String webSiteURLStr : crawlIndicators.keySet()) {
			boolean recrawl = crawlIndicators.get(webSiteURLStr);
			if (recrawl) {
				try {
					URL webSiteURL = new URL(webSiteURLStr);
					String repositoryName = FileUtility.convertURL2FileName(webSiteURL);

					// String evaluationCorpusFileFullName =
					// RepositoryParameterConfiguration.getMappingHumanReadableDirectoryFullPath()
					// + repositoryName;
					String evaluationCorpusFileFullName = _goldenStandardRepositoriesDir + repositoryName;
					String basicInfoFileFullName = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath() + repositoryName;
					String detailInfoFileFullName = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath()
							+ repositoryName;

					System.out.println("1" + evaluationCorpusFileFullName);
					System.out.println("2" + basicInfoFileFullName);
					System.out.println("3" + detailInfoFileFullName);

					boolean goldenStandardFileExists = FileUtility.exists(evaluationCorpusFileFullName);

//					if (!basicInfoFileExists || !detailInfoFileExists || !goldenStandardFileExists) {
//						continue;
//					}
					
					if (!goldenStandardFileExists) {
						continue;
					}

					System.out.println("Annotate Repository: " + repositoryName);

					/*
					 * 
					 */
					IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository = this.createProprietaryClassifiedInstanceRepository(webSiteURLStr, applyMappingRule);

					if(proprietoryClassifiedInstancesRepository == null){
						continue;
					}
					/*
					 * Correct possibly mis-classified instances
					 */
					if (applyCorrections) {
						this.ApplyCorrectionsClassLabels(proprietoryClassifiedInstancesRepository);
					}
					/*
					 * 
					 */
					this.compareWithGoldenStandard(proprietoryClassifiedInstancesRepository, repositoryName, evaluationCorpusFileFullName);

				} catch (MalformedURLException e) {
				}
			}
		}
	}

	private IClassifiedInstancesRepository createProprietaryClassifiedInstanceRepository(String webSiteURLStr, boolean applyMappingRule) throws IOException {
		URL webSiteURL = new URL(webSiteURLStr);
		String repositoryName = FileUtility.convertURL2FileName(webSiteURL);
//		IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository;
//		proprietoryClassifiedInstancesRepository = ClassifiedInstancesRepositoryFactory.createProprietoryClassifiedInstancesRepository(
//				webSiteURL, _ontologyRepository, true, applyMappingRule, false);
		
		String basicInfoDirectory = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String detailInfoDirectory = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		String basicInfoFileFullName = basicInfoDirectory + repositoryName;
		String detailInfoFileFullName = detailInfoDirectory + repositoryName;
		boolean basicInfoFileExists = FileUtility.exists(basicInfoFileFullName);
		boolean detailInfoFileExists = FileUtility.exists(detailInfoFileFullName);

        IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
                .createProprietaryManufacturingLexiconRepository(repositoryName);
		if (basicInfoFileExists && detailInfoFileExists && true) {
			return new ProprietoryClassifiedInstancesRepository(repositoryName, ClassifiedInstancesRepositoryType.All,
					_ontologyRepository, proprietaryManufacturingLexicalMappingRepository);
		} else {

			boolean succeed1 = false;
			if (!basicInfoFileExists) {
				succeed1 = FileUtility.createDirectories(basicInfoDirectory);
			}

			boolean succeed2 = false;
			if (!detailInfoFileExists) {
				succeed2 = FileUtility.createDirectories(detailInfoDirectory);
			}

			if (succeed1 && succeed2) {
				
				IClassificationCorrectionRepository aggregatedClassificationCorrectionRepository = InterpretationCorrectionRepositoryFactory
						.createAggregratedClassificationCorrectionRepository();

				IManufacturingLexicalMappingRecordsReader aggregratedManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
		                .createAggregratedManufacturingLexicalMappingRepository(_ontologyRepository);
				
				ITripleRepository tripleStore = TripleRepositoryFactory.createTripleRepository(webSiteURL, true);
				
				// Create Relation-Property Mapping Algorithm Object
				IRelation2PropertyMappingAlgorithm relation2PropertymMappingAlgorithm = new Relation2PropertyMappingAlgorithm(
						tripleStore, _ontologyRepository, new Relation2PropertyMapper());
				
				// Create Instance Classification Algorithm Object
				IInstanceClassificationAlgorithm instanceClassificationAlgorithm = new InstanceClassificationAlgorithm(tripleStore,
						_ontologyRepository, 
						new Concept2OntClassMapper(new Concept2OntClassMappingPairLookUpper(aggregratedManufacturingLexicalMappingRepository, 
								_ontologyRepository), 
																							applyMappingRule), aggregatedClassificationCorrectionRepository);
				// Create the Annotation (Mapping) Algorithm Object
				IMappingAlgorithm mappingAlgorithm = new TS2OntoMappingAlgorithm2(relation2PropertymMappingAlgorithm, instanceClassificationAlgorithm);
				mappingAlgorithm.mapping();
				
				IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository = new ProprietoryClassifiedInstancesRepository(tripleStore.getRepositoryName(), 
						_ontologyRepository, 
						  proprietaryManufacturingLexicalMappingRepository, 
						  mappingAlgorithm.getRelation2PropertyMap(), 
						  mappingAlgorithm.getClassifiedInstances());
				
				// apply the classification correction algorithm here!!!

				proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMappings(proprietoryClassifiedInstancesRepository.getAllClassifiedInstanceDetailRecords());
				
//				boolean succeed11 = proprietaryManufacturingLexicalMappingRepository.saveRepository();
//				boolean succeed22 = proprietoryClassifiedInstancesRepository.saveRepository();
//				if (succeed11 && succeed22) {
//					return proprietoryClassifiedInstancesRepository;
//
//				} else {
//					throw new IOException("Create Proprietory Classified Instances Repository Failed");
//				}
		
				return proprietoryClassifiedInstancesRepository;

			} else {
				return null;
			}
		}
	}

	/**
	 * Apply corrections on class labels of instances. Only change the class label of an instance if necessary.
	 * @param proprietoryClassifiedInstancesRepository
	 * @throws IOException
	 */
	private void ApplyCorrectionsClassLabels(IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository) throws IOException {

		// 
		IClassificationCorrectionRepository aggregatedClassificationCorrectionRepository = InterpretationCorrectionRepositoryFactory
				.createAggregratedClassificationCorrectionRepository();

		//
		IClassifiedInstancesAccessor aggregratedClassifiedInstanceRepository = ClassifiedInstancesRepositoryFactory
				.createAggregatedClassifiedInstancesRepository(_ontologyRepository);

		//
		ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator = new CorrectionClusterCodeGenerator();

		// Create annotation (Interpretation, Classification) evidences Extractor
		IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingFeatureExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(
				_correctionClusterCodeGenerator, aggregatedClassificationCorrectionRepository, _ontologyRepository);

		// Create lexical Features Extractor
		IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(
				aggregratedClassifiedInstanceRepository, new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor()));

		// Create Rule Generator
		ClassificationCorrectionRuleGenerator _ruleGenerator = new ClassificationCorrectionRuleGenerator(
				aggregatedClassificationCorrectionRepository, _ontologyRepository, _correctionClusterCodeGenerator,
				_instanceC2CMappingFeatureExtractor, _instanceLexicalFeatureExtractor);

		/*
		 * Iterate all classified instances and check it correctness. Using
		 * naive bayes algorithm to correction possibly misclassified instances
		 */
		for (String instanceName : proprietoryClassifiedInstancesRepository.getInstanceSet()) {
			IClassifiedInstanceDetailRecord detailInstance = proprietoryClassifiedInstancesRepository
					.getClassifiedInstanceDetailRecordByInstanceName(instanceName);
			ICorrectionRule rule = _ruleGenerator.getClassificationCorrectionRule(detailInstance);
			String originalClass = detailInstance.getOntoClassName();
			String targetClass = rule.obtainCorrectedClassLabel(detailInstance, originalClass);
			if (!targetClass.equals(originalClass)) {
				// detailInstance.getMatchedOntoClass().setLabel(targetClass);
				System.out.println("### CLASS CHANGEs from " + originalClass + " to " + targetClass);
				proprietoryClassifiedInstancesRepository.updateInstanceClass(instanceName, targetClass);
			} else {
				System.out.println("### NO CLASS CHANGEs");
			}

			// String className1 =
			// proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getOntoClassName();
			// String className2 =
			// proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
			// String className3 =
			// proprietoryClassifiedInstancesRepository.getClassifiedInstanceBasicRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
			// System.out.println("###1 " + instanceName);
			// System.out.println("###2 " + className1 + "  " + className2 + " "
			// + className3);
		}
	}

	private void compareWithGoldenStandard(IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository, String repositoryName,
			String evaluationCorpusFullName) throws IOException {

		//
		IEvaluationCorpusRecordsAccessor _evaluationCorpusRecordsAccessor = new EvaluationCorpusRecordsAccessor();
		IEvaluationCorpusRecordsReader evaluationCorpusRecordsReader = new EvaluationCorpus(evaluationCorpusFullName, _ontologyRepository,
				_evaluationCorpusRecordsAccessor);
		((EvaluationCorpus) evaluationCorpusRecordsReader).loadRepository();
		
		//
		EvaluationResult evaluationResult = _evaluator.evaluate(evaluationCorpusRecordsReader, proprietoryClassifiedInstancesRepository);
		evaluationResult.outputEvaluationResult(this._evaluationResultDirectory + repositoryName);
		
		
		
		
		//
		
		
		
		
		
		
		
		
		//
		IClassificationCorrectionRepository classificationCorrectionRepository = InterpretationCorrectionRepositoryFactory
				.createProprietaryClassificationCorrectionRepository(repositoryName);
		IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ((ProprietoryClassifiedInstancesRepository) proprietoryClassifiedInstancesRepository)
				.getManufacturingLexicalMappingRepository();
		MappingDataGateway gateWay = new MappingDataGateway(proprietoryClassifiedInstancesRepository, classificationCorrectionRepository,
				proprietaryManufacturingLexicalMappingRepository);

		int numberOfClassifiedInstance = 0;
		int numberOfUpdatedInstance = 0;
		Collection<IInstanceRecord> updatedInstanceRecords = new ArrayList<IInstanceRecord>();
		for (IClassifiedInstanceDetailRecord record : proprietoryClassifiedInstancesRepository.getAllClassifiedInstanceDetailRecords()) {
			numberOfClassifiedInstance++;
			IInstanceRecord instanceRecord = gateWay.createInstanceClassificationRecord();
			updatedInstanceRecords.add(instanceRecord);
			String origInstanceName = record.getInstanceLabel().trim();
			String origOntoClassName = record.getOntoClassName().trim();

			instanceRecord.setPrevenanceOfInstance(repositoryName);
			instanceRecord.setOriginalInstanceName(origInstanceName);
			instanceRecord.setUpdatedInstanceName(origInstanceName);
			instanceRecord.setOriginalClassName(origOntoClassName);
			String stardandClassLabel = evaluationCorpusRecordsReader.getClassLabelforInstance(origInstanceName);

			if (stardandClassLabel != null && !origOntoClassName.equals(stardandClassLabel.trim())) {
				numberOfUpdatedInstance++;
				instanceRecord.setUpdatedClassName(stardandClassLabel);
				instanceRecord.isUpdatedInstance(true);
			} else {
				instanceRecord.setUpdatedClassName(origOntoClassName);
				instanceRecord.isUpdatedInstance(false);
			}

			for (IConcept2OntClassMapping c2cMapping : record.getConcept2OntClassMappingPairs()) {
				Concept concept = c2cMapping.getConcept();
				String conceptLabel = concept.getConceptName();
				String standardMappedOntClassLabel = evaluationCorpusRecordsReader.getOntClassForConcept(conceptLabel);
				List<String> standardMappedOntClassSet = evaluationCorpusRecordsReader.getClassSet(origInstanceName, conceptLabel);
				
				if (c2cMapping.isMappedConcept()) {
					String mappedOntClassLabel = c2cMapping.getMappedOntoClassName().trim();
					if (standardMappedOntClassSet.size() > 0 && !mappedOntClassLabel.equals(standardMappedOntClassSet.get(0).trim())) {
//					if (standardMappedOntClassLabel != null && !mappedOntClassLabel.equals(standardMappedOntClassLabel.trim())) {
						standardMappedOntClassLabel = standardMappedOntClassSet.get(0).trim();
						System.out.println("### Change Concept_Class Mapping: " + conceptLabel + "  --> " + standardMappedOntClassLabel);
						OntoClassInfo updatedMappedOntClass = _ontologyRepository.getLightWeightOntClassByName(standardMappedOntClassLabel);
						double sim = 0.75;
						instanceRecord.addConcept2OntClassMappingPair(concept, null, updatedMappedOntClass, true, true, sim);
					} else {
						instanceRecord.addConcept2OntClassMappingPair(concept, c2cMapping.getRelation(), c2cMapping.getMappedOntoClass(), c2cMapping.isDirectMapping(), c2cMapping.isManualMapping(), c2cMapping.getMappingScore());
					}

				} else {
					
					if (standardMappedOntClassSet.size() > 0 ) {
//					if (standardMappedOntClassLabel != null) {
						standardMappedOntClassLabel = standardMappedOntClassSet.get(0).trim();
						System.out.println("### Add New Concept_Class Mapping: " + conceptLabel + "  --> " + standardMappedOntClassLabel);
						OntoClassInfo updatedMappedOntClass = _ontologyRepository.getLightWeightOntClassByName(standardMappedOntClassLabel);
						double sim = 0.75;
						instanceRecord.addConcept2OntClassMappingPair(concept, MappingRelationType.relatedTo, updatedMappedOntClass, true, true, sim);
						proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMapping(concept, MappingRelationType.relatedTo, updatedMappedOntClass, sim); 
					} else {
						instanceRecord.addConcept2OntClassMappingPair(concept, null, null, false, false, 0.0);
					}
				}
			}
		}

		System.out.println("### number of classified instances: " + numberOfClassifiedInstance);
		System.out.println("### number of updated instances: " + numberOfUpdatedInstance);
		// ((ProprietoryClassifiedInstancesRepository)
		// proprietoryClassifiedInstancesRepository).saveHumanReadableFile("");
		gateWay.updateMappingInfo(updatedInstanceRecords);
		gateWay.saveRepository();
		// boolean succeed22 =
		// proprietoryClassifiedInstancesRepository.saveRepository();
	}

}
