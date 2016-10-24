package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import umbc.ebiquity.kang.instanceconstructor.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityPathExtractor;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.EntityPathExtractorImpl;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.webpageparser.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.webpageparser.WebPagePathsImpl;
import umbc.ebiquity.kang.webpageparser.WebSiteCrawler;
import umbc.ebiquity.kang.webpageparser.interfaces.WebPage;

public class EntityGraphLabelingTest {
	
	private EntityGraph entityGraph;
	private WebSiteCrawler crawler ;
	
	public static void main(String[] arg) throws Exception{ 
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		
//		String webSiteURLString = "http://www.astromfg.com";
//		String webSiteURLString = "http://www.accutrex.com";
//		String webSiteURLString = "http://www.lincolnparkboring.com";
		String webSiteURLString = "http://www.princetonind.com";
		URL webSiteURL = new URL(webSiteURLString);
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL); 
		crawler.crawl();
		EntityPathExtractor extractor = new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter());
		EntityGraph entityGraph = new EntityGraph(extractor.extractor(), webSiteURL);
//		entityGraph.printForwardTermGraphNodesAfterAnalyzing();
//		entityGraph.analyzeEntityGraph();
//		entityGraph.showInstanceConceptSet();
		
	}
	
//	@Before
//	public void Init() throws IOException {
//		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
//		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
//		
////		String webSiteURLString = "http://www.astromfg.com";
//		String webSiteURLString = "http://www.accutrex.com";
//		URL webSiteURL = new URL(webSiteURLString);
//		crawler = new WebSiteCrawler(webSiteURL);
//		crawler.crawl();
//	}

//	@Test
//	public void setUp(){
//		InitializeEntityGraphTest();
//		RelationExtractionAlgorithmTest();
////		InstanceConceptSetExtractionAlgorithmTest();
//	}
	
	@Test
	public void InitializeEntityGraphTest() throws Exception {  
		String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL, 1);
		List<WebPage> webPages = crawler.crawl();
		
		WebPagePathsImpl webPagePath = new WebPagePathsImpl(webPages.get(0));  
		webPagePath.construct();
		EntityPathExtractor extractor = new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter());
//		EntityPathExtractorImpl extractor = new EntityPathExtractorImpl(crawler, new SimplePageTemplatesSplitter());
		entityGraph = new EntityGraph(extractor.extractor(), webSiteURL);
		entityGraph.printForwardTermGraphNodesAfterAnalyzing();
		
		RelationExtractionAlgorithmTest();
		InstanceConceptSetExtractionAlgorithmTest();
	}
	
	public void RelationExtractionAlgorithmTest(){
		IRelationExtractionAlgorithm relationExtractionAlgorithm = new RelationExtractionAlgorithm();
		relationExtractionAlgorithm.extractRelation(entityGraph);
		entityGraph.showRelations();
	}
	
	public void InstanceConceptSetExtractionAlgorithmTest(){
		InstanceConceptSetExtractionAlgorithm InstanceConceptSetExtractionAlgorithm = new InstanceConceptSetExtractionAlgorithm();
		InstanceConceptSetExtractionAlgorithm.extractInstanceConceptSet(entityGraph);
		entityGraph.showInstanceConceptSet();
	}
}
