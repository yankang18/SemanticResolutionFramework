package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.instanceconstructor.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityPathExtractor;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.InstanceDescriptionModel;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.InstanceDescriptionModelConstructorImpl;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceDescriptionModelConstructor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.webpageparser.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.webpageparser.WebSiteCrawler;

public class InstanceDescriptionModelFactory {
	
	public static IInstanceDescriptionModel createTripleRepository(URL webSiteURL) throws IOException {
		return createModel(webSiteURL, true);
	}
	
	public static boolean instanceDescriptionModelConstructed(URL webSiteURL) {
		String repositoryFullName = getRepositoryFullName(webSiteURL);
		return FileUtility.exists(repositoryFullName);
	}

	/**
	 * 
	 * @param webSiteURL
	 * @param localLoad
	 * @return
	 * @throws IOException
	 */
	public static IInstanceDescriptionModel createModel(URL webSiteURL, boolean localLoad) throws IOException {
		
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String fileFullName = getRepositoryFullName(webSiteURL);
		if (localLoad && FileUtility.exists(fileFullName)) {
			IInstanceDescriptionModel tripleStore = new InstanceDescriptionModel();
			boolean succeed = tripleStore.loadRepository(tripleRepositoryName);
			if (succeed) {
				return tripleStore;
			} else {
				throw new IOException("Load Triple Repository Failed");
			}
		} else {

			boolean succeed = FileUtility.createDirectories(directory);
			if (succeed) {
				return construct(webSiteURL, tripleRepositoryName);
			} else {
				throw new IOException("Create Directories for Triple Repository Failed");
			}
		}
	}

	private static IInstanceDescriptionModel construct(URL webSiteURL, String modelName) throws IOException{
		
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL);
		crawler.crawl();

		// extract Entity Paths and create Entity Graph
		EntityPathExtractor extractor = new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter());
		EntityGraph entityGraph = new EntityGraph(extractor.extractor());
		entityGraph.labelEntityGraph(new RelationExtractionAlgorithm(), new InstanceConceptSetExtractionAlgorithm());

		// extract triples from the Entity Graph
		IInstanceDescriptionModelConstructor ontologyInstantiator = new InstanceDescriptionModelConstructorImpl(entityGraph);
		IInstanceDescriptionModel IDM = ontologyInstantiator.extractTripleRepository();
		boolean succeed = IDM.save(modelName);
		if (succeed) {
			return IDM;
		} else {
			throw new IOException("Instance Description Model Save Failed");
		}
	}
	
	private static String getRepositoryFullName(URL webSiteURL){
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String repositoryFullName = directory + tripleRepositoryName;
		return repositoryFullName;
	}
}
