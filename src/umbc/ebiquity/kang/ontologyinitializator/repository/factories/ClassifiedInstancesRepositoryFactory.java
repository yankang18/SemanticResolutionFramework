package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.CorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.TS2OntoMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.ClassificationCorrectionRuleGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository.ClassifiedInstancesRepositoryType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.textprocessing.impl.SequenceInReversedOrderPhraseExtractor;

public class ClassifiedInstancesRepositoryFactory {
	
	public static IClassifiedInstancesAccessor createClassifiedInstancesRepository(IOntologyRepository ontologyRepository) {
		return new ClassifiedInstancesAccessor(ontologyRepository);
	}

	public static IProprietoryClassifiedInstancesRepository createProprietoryClassifiedInstancesRepository(URL webSiteURL, 
			                                                         IOntologyRepository ontologyRepository, 
			                                                         IClassificationCorrectionRepository classificationCorrectionRepository, 
			                                                         IManufacturingLexicalMappingRepository manufacturingLexicalMappingRepository, 
			                                                         boolean loadLocal) throws IOException{
		
		String mappingInfoRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String basicInfoDirectory = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String detailInfoDirectory = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		String basicInfoFileFullName = basicInfoDirectory + mappingInfoRepositoryName;
		String detailInfoFileFullName = detailInfoDirectory + mappingInfoRepositoryName;
		boolean basicInfoFileExists = FileUtility.exists(basicInfoFileFullName);
		boolean detailInfoFileExists = FileUtility.exists(detailInfoFileFullName);
		
		if (basicInfoFileExists && detailInfoFileExists && loadLocal) {
			return new umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository
			       (
			        mappingInfoRepositoryName, 
					ClassifiedInstancesRepositoryType.All, 
                    ontologyRepository, 
                    manufacturingLexicalMappingRepository
                    );
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
				ITripleRepository tripleStore = TripleRepositoryFactory.createTripleRepository(webSiteURL, loadLocal);
				IMappingAlgorithm alg = new TS2OntoMappingAlgorithm(tripleStore, ontologyRepository, manufacturingLexicalMappingRepository);
				alg.mapping();
				IProprietoryClassifiedInstancesRepository proprietoryClassifiedInstancesRepository = alg.getProprietoryClassifiedInstancesRepository();
				
				////// TODO: do we need to apply the classification correction algorithm here!!!
				ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator = new CorrectionClusterCodeGenerator();
				IInstanceConcept2OntClassMappingFeatureExtractor _instanceC2CMappingFeatureExtractor = new InstanceConcept2OntClassMappingFeatureExtractor(_correctionClusterCodeGenerator, classificationCorrectionRepository);
				IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor = new InstanceLexicalFeatureExtractor(
						ClassifiedInstancesRepositoryFactory.createClassifiedInstancesRepository(ontologyRepository), new SimpleLexicalFeatureExtractor(
								new SequenceInReversedOrderPhraseExtractor()));
				ClassificationCorrectionRuleGenerator _ruleGenerator = new ClassificationCorrectionRuleGenerator
				(
						classificationCorrectionRepository,
						ontologyRepository,
						_correctionClusterCodeGenerator,
						_instanceC2CMappingFeatureExtractor, 
						_instanceLexicalFeatureExtractor
				);

				for (String instanceName : proprietoryClassifiedInstancesRepository.getInstanceSet()) {
					IClassifiedInstanceDetailRecord detailInstance = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName);
					ICorrectionRule rule = _ruleGenerator.getClassificationCorrectionRule(detailInstance);
					String originalClass = detailInstance.getOntoClassName();
					String targetClass = rule.getTargetClass(detailInstance, originalClass);
					if (!targetClass.equals(originalClass)) {
//						detailInstance.getMatchedOntoClass().setLabel(targetClass);
						proprietoryClassifiedInstancesRepository.updateInstanceClass(instanceName, targetClass);
					} else {
						System.out.println("NO CLASS CHANGE");
					}

					String className1 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getOntoClassName();
					String className2 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
					String className3 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceBasicRecordByInstanceName(instanceName).getMatchedOntoClass().getOntClassName();
					System.out.println("###1 " + instanceName);
					System.out.println("###2 " + className1 + "  " + className2 + " " + className3);
				}
				//////
//				proprietoryClassifiedInstancesRepository.showRepositoryDetail();
//				String className1 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName("CNC Machining").getOntoClassName();
//				String className2 = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName("CNC Machining").getMatchedOntoClass().getOntClassName();
//				System.out.println("######1 " + "CNC Machining");
//				System.out.println("######2 " + className1 + "  " + className2);
				
				manufacturingLexicalMappingRepository.addNewConcept2OntoClassMappings(proprietoryClassifiedInstancesRepository.getAllClassifiedInstanceDetailRecords());
				boolean succeed11 = manufacturingLexicalMappingRepository.saveRepository();
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
