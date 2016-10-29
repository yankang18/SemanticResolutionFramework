package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository;
import umbc.ebiquity.kang.instanceconstructor.builder.InstanceDescriptionModelConstructionHelper;
import umbc.ebiquity.kang.instanceconstructor.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.instanceconstructor.impl.FileModelRepository;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.TS2OntoMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceDescriptionModelSemanticResolver;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IModelSemanticResolver;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionRuleGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.AggregratedClassifiedInstanceRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository.ClassifiedInstancesRepositoryType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.textprocessing.impl.SequenceInReversedOrderPhraseExtractor;

public class ClassifiedInstancesRepositoryFactory {

	public static IClassifiedInstancesAccessor createAggregatedClassifiedInstancesRepository(IOntologyRepository ontologyRepository) {
		return new AggregratedClassifiedInstanceRepository(ontologyRepository);
	}

	public static IClassifiedInstancesRepository createProprietoryClassifiedInstancesRepository(URL webSiteURL,
																								IOntologyRepository ontologyRepository, 
																								boolean loadLocal,
																								boolean applyMappingRule,
																								boolean applyCorrection) throws IOException {

		String repositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String basicInfoDirectory = FileRepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String detailInfoDirectory = FileRepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		String basicInfoFileFullName = basicInfoDirectory + repositoryName;
		String detailInfoFileFullName = detailInfoDirectory + repositoryName;
		boolean basicInfoFileExists = FileUtility.exists(basicInfoFileFullName);
		boolean detailInfoFileExists = FileUtility.exists(detailInfoFileFullName);

        IManufacturingLexicalMappingRepository proprietaryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory
                .createProprietaryManufacturingLexiconRepository(repositoryName);
		if (basicInfoFileExists && detailInfoFileExists && loadLocal) {
			return new ProprietoryClassifiedInstancesRepository(repositoryName, ClassifiedInstancesRepositoryType.All,
					ontologyRepository, proprietaryManufacturingLexicalMappingRepository);
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
		                .createAggregratedManufacturingLexicalMappingRepository(ontologyRepository);
				
				// create Instance Description Model of a web site
				// move this out of this class
				IInstanceDescriptionModelRepository repo = new FileModelRepository();
				IInstanceDescriptionModel IDM = InstanceDescriptionModelConstructionHelper.createModel(webSiteURL, repo);
				
				// Create Relation-Property Mapping Algorithm Object
				IRelation2PropertyMappingAlgorithm relation2PropertymMappingAlgorithm = new Relation2PropertyMappingAlgorithm(
						IDM, ontologyRepository, new Relation2PropertyMapper());
				
				// Create Instance Classification Algorithm Object
				IInstanceClassificationAlgorithm instanceClassificationAlgorithm = new InstanceClassificationAlgorithm(IDM,
						ontologyRepository, 
						new Concept2OntClassMapper(new Concept2OntClassMappingPairLookUpper(aggregratedManufacturingLexicalMappingRepository, 
																							ontologyRepository), 
																							applyMappingRule), aggregatedClassificationCorrectionRepository);
				// Create the Annotation (Mapping) Algorithm Object
				IModelSemanticResolver mappingAlgorithm = new InstanceDescriptionModelSemanticResolver(relation2PropertymMappingAlgorithm, instanceClassificationAlgorithm);
				mappingAlgorithm.resolve();
				
				IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository = new ProprietoryClassifiedInstancesRepository(IDM.getRepositoryName(), 
						  ontologyRepository, 
						  proprietaryManufacturingLexicalMappingRepository, 
						  mappingAlgorithm.getRelation2PropertyMap(), 
						  mappingAlgorithm.getClassifiedInstances());
				
				// apply the classification correction algorithm here!!!
				if (applyCorrection) {
					
			        IClassifiedInstancesAccessor aggregratedClassifiedInstanceRepository = ClassifiedInstancesRepositoryFactory.createAggregatedClassifiedInstancesRepository(ontologyRepository);
					
					
					System.out.println("### " + "Correction Applied ...");
					ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator = new CorrectionClusterCodeGenerator();
					
					// Create Annotation (Interpretation, Classification) evidences Extractor
					IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingFeatureExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(
																																	_correctionClusterCodeGenerator, 
																																	aggregatedClassificationCorrectionRepository,
																																	ontologyRepository);
					
					// Create lexical Features Extractor
					IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(
																				aggregratedClassifiedInstanceRepository,
																				new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor())
																				);
					
					// Create Rule Generator
					ClassificationCorrectionRuleGenerator _ruleGenerator = new ClassificationCorrectionRuleGenerator(aggregatedClassificationCorrectionRepository, 
																													 ontologyRepository, 
																													 _correctionClusterCodeGenerator,
																													 _instanceC2CMappingFeatureExtractor, 
																													 _instanceLexicalFeatureExtractor
																													 );

					/*
					 * Iterate all classified instances and check it
					 * correctness. Using naive bayes algorithm to correction
					 * possibly misclassified instances
					 */
					for (String instanceName : proprietoryClassifiedInstancesRepository.getInstanceSet()) {
						IClassifiedInstanceDetailRecord detailInstance = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName);
						ICorrectionRule rule = _ruleGenerator.getClassificationCorrectionRule(detailInstance);
						String originalClass = detailInstance.getOntoClassName();
						String targetClass = rule.obtainCorrectedClassLabel(detailInstance, originalClass);
						if (!targetClass.equals(originalClass)) {
							// detailInstance.getMatchedOntoClass().setLabel(targetClass);
							proprietoryClassifiedInstancesRepository.updateInstanceClass(instanceName, targetClass);
						} else {
							System.out.println("### NO CLASS CHANGEs");
						}

						String className1 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getOntoClassName();
						String className2 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
						String className3 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceBasicRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
						System.out.println("###1 " + instanceName);
						System.out.println("###2 " + className1 + "  " + className2 + " " + className3);
					}
					// ////
					// proprietoryClassifiedInstancesRepository.showRepositoryDetail();
					// String className1 =
					// proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName("CNC Machining").getOntoClassName();
					// String className2 =
					// proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName("CNC Machining").getMatchedOntoClass().getOntClassName();
					// System.out.println("######1 " + "CNC Machining");
					// System.out.println("######2 " + className1 + "  " +
					// className2);
				}

				proprietaryManufacturingLexicalMappingRepository.addNewConcept2OntoClassMappings(proprietoryClassifiedInstancesRepository.getAllClassifiedInstanceDetailRecords());
				boolean succeed11 = proprietaryManufacturingLexicalMappingRepository.saveRepository();
				boolean succeed22 = proprietoryClassifiedInstancesRepository.saveRepository();
				if (succeed11 && succeed22) {
					return proprietoryClassifiedInstancesRepository;

				} else {
					throw new IOException("Create Proprietory Classified Instances Repository Failed");
				}
			} else {
				throw new IOException("Create Proprietory Classified Instances Repository Failed");
			}
		}
	}
}
