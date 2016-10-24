package umbc.ebiquity.kang.instanceconstructor.model.builder;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.instanceconstructor.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityPathExtractor;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceRepository;
import umbc.ebiquity.kang.instanceconstructor.model.InstanceDescriptionModel;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.webpageparser.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.webpageparser.WebSiteCrawler;

public class InstanceDescriptionModelFactory {
	
//	public static IInstanceDescriptionModel createTripleRepository(URL webSiteURL) throws IOException {
//		return createModel(webSiteURL, true);
//	}
	
	public static boolean instanceDescriptionModelConstructed(URL webSiteURL) {
		String repositoryFullName = getRepositoryFullName(webSiteURL);
		return FileUtility.exists(repositoryFullName);
	}
	
	public static IInstanceDescriptionModel createModel(URL webSiteURL, IInstanceRepository repo) throws IOException {

		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String fileFullName = getRepositoryFullName(webSiteURL);
		if (FileUtility.exists(fileFullName)) {
			return repo.load(tripleRepositoryName);
		} else {
			boolean succeed = FileUtility.createDirectories(directory);
			if (succeed) {
				return construct(webSiteURL, tripleRepositoryName);
			} else {
				throw new IOException("Create Directories for Triple Repository Failed");
			}
		}
	}

//	public static IInstanceDescriptionModel createModel(URL webSiteURL, boolean localLoad) throws IOException {
//		
//		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
//		String directory = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
//		String fileFullName = getRepositoryFullName(webSiteURL);
//		if (localLoad && FileUtility.exists(fileFullName)) {
//			
//			IInstanceDescriptionModel tripleStore = new InstanceDescriptionModel();
//			boolean succeed = tripleStore.load(tripleRepositoryName);
//			if (succeed) {
//				return tripleStore;
//			} else {
//				throw new IOException("Load Triple Repository Failed");
//			}
//		} else {
//
//			boolean succeed = FileUtility.createDirectories(directory);
//			if (succeed) {
//				return construct(webSiteURL, tripleRepositoryName);
//			} else {
//				throw new IOException("Create Directories for Triple Repository Failed");
//			}
//		}
//	}
	
	public static IInstanceDescriptionModel createModel(URL webSiteURL) throws IOException {
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		return construct(webSiteURL, tripleRepositoryName);
	}

	private static IInstanceDescriptionModel construct(URL webSiteURL, String modelName) throws IOException{
		
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL);
		crawler.crawl();

		// extract Entity Paths and create Entity Graph
		EntityPathExtractor extractor = new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter());
		EntityGraph entityGraph = new EntityGraph(extractor.extractor(), webSiteURL);
		entityGraph.labelEntityGraph(new RelationExtractionAlgorithm(), new InstanceConceptSetExtractionAlgorithm());

		// extract triples from the Entity Graph
		IInstanceDescriptionModelBuilder ontologyInstantiator = new InstanceDescriptionModelBuilderImpl();
		IInstanceDescriptionModel IDM = ontologyInstantiator.build(entityGraph);
		return IDM;
	}
	
	private static String getRepositoryFullName(URL webSiteURL){
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String repositoryFullName = directory + tripleRepositoryName;
		return repositoryFullName;
	}
}
