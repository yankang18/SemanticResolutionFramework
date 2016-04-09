package umbc.ebiquity.kang.ontologyinitializator.repository.factories;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityGraph;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityPathExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.TripleRepositoryExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ITripleRepositoryExtractor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.TripleRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.webpageparser.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.webpageparser.WebSiteCrawler;

public class TripleRepositoryFactory {
	
	public static ITripleRepository createTripleRepository(URL webSiteURL) throws IOException {
		return createTripleRepository(webSiteURL, true);
	}
	
	public static boolean existTripleRepository(URL webSiteURL){
	  String repositoryFullName = getRepositoryFullName(webSiteURL);
		return FileUtility.exists(repositoryFullName);
	}
	
	public static ITripleRepository createTripleRepository(URL webSiteURL, boolean localLoad) throws IOException {
		
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String fileFullName = getRepositoryFullName(webSiteURL);
		if (localLoad && FileUtility.exists(fileFullName)) {
			ITripleRepository tripleStore = new TripleRepository();
			boolean succeed = tripleStore.loadRepository(tripleRepositoryName);
			if (succeed) {
				return tripleStore;
			} else {
				throw new IOException("Load Triple Repository Failed");
			}
		} else {

			boolean succeed = FileUtility.createDirectories(directory);
			if (succeed) {
				return createRepository(webSiteURL, tripleRepositoryName);
			} else {
				throw new IOException("Create Directories for Triple Repository Failed");
			}
		}
	}

	private static ITripleRepository createRepository(URL webSiteURL, String tripleRepositoryName) throws IOException{
		
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL);
		crawler.crawl();

		/*
		 * extract Entity Paths and create Entity Graph
		 */
		EntityPathExtractor extractor = new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter());
		EntityGraph entityGraph = new EntityGraph(extractor.extractor());
		entityGraph.labelEntityGraph(new RelationExtractionAlgorithm(), new InstanceConceptSetExtractionAlgorithm());
		/*
		 * extract triples from the Entity Graph
		 */
		ITripleRepositoryExtractor ontologyInstantiator = new TripleRepositoryExtractor(entityGraph);
		ITripleRepository tripleStore = ontologyInstantiator.extractTripleRepository();
		boolean succeed = tripleStore.saveRepository(tripleRepositoryName);
		if (succeed) {
			return tripleStore;
		} else {
			throw new IOException("Create Triple Repository Failed");
		}
	}
	
	private static String getRepositoryFullName(URL webSiteURL){
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String repositoryFullName = directory + tripleRepositoryName;
		return repositoryFullName;
	}

}
