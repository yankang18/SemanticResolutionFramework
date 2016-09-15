package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMappingPairLookUpper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceDescriptionModelSemanticResolver;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IModelSemanticResolver;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class ProprietaryClassifiedInstanceRepositoriesAutomaticConstructor extends AbstractWebUrlLoader {

	public ProprietaryClassifiedInstanceRepositoriesAutomaticConstructor() {

	}
	
	public static void main(String[] args) throws IOException {
		
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		RepositoryParameterConfiguration.CLASSIFIED_INSTANCE_HOST_DIRECTORY = "/Users/yankang/Desktop/Test";
		RepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/Users/yankang/Desktop/Test";
		RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_HOST_DIRECTORY = "/Users/yankang/Desktop/Test";
		String fileFullPath = "/Users/yankang/Desktop/WebSiteURLs.txt";
		ProprietaryClassifiedInstanceRepositoriesAutomaticConstructor PCIRAC = new ProprietaryClassifiedInstanceRepositoriesAutomaticConstructor();
		PCIRAC.loadRecords(fileFullPath);
		PCIRAC.createProprietaryClassifiedInstanceRepositories(PopulationType.CRAWL_INDICATED);
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

		String basicInfoDirectory = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String detailInfoDirectory = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		boolean basicInfoFileExists = FileUtility.exists(basicInfoDirectory);
		boolean detailInfoFileExists = FileUtility.exists(basicInfoDirectory);

		if (!basicInfoFileExists) {
			FileUtility.createDirectories(basicInfoDirectory);
		}

		if (!detailInfoFileExists) {
			FileUtility.createDirectories(detailInfoDirectory);
		}

		for (String webSiteURLStr : crawlIndicators.keySet()) {
			boolean recrawl = crawlIndicators.get(webSiteURLStr);
			if (recrawl) {
				try {
					URL webSiteURL = new URL(webSiteURLStr);
					String repositoryName = FileUtility.convertURL2FileName(webSiteURL);
					System.out.println("Annotate Repository: " + repositoryName);
					boolean existTripleRepository = InstanceDescriptionModelFactory.instanceDescriptionModelConstructed(webSiteURL);
					
					if(!existTripleRepository){
						continue;
					}
					
					System.out.println("Annotating Repository: " + repositoryName);
					
					IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
					ClassifiedInstancesRepositoryFactory.createProprietoryClassifiedInstancesRepository(webSiteURL, ontologyRepository, false, false, false);
					
				} catch (MalformedURLException e) {
				}
			}
		}
	}

}
