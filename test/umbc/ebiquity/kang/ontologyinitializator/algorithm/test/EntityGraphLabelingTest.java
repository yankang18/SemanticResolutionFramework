package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.net.URL;

import org.junit.Test;

import umbc.ebiquity.kang.entityframework.IEntityGraph;
import umbc.ebiquity.kang.entityframework.IEntityPathExtractor;
import umbc.ebiquity.kang.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.entityframework.impl.EntityPathExtractor;
import umbc.ebiquity.kang.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.websiteparser.ICrawledWebSite;
import umbc.ebiquity.kang.websiteparser.ICrawler;
import umbc.ebiquity.kang.websiteparser.impl.WebSiteCrawler;
import umbc.ebiquity.kang.websiteparser.impl.WebSiteCrawlerFactory;
import umbc.ebiquity.kang.websiteparser.support.IPathFocusedWebSiteParser;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.impl.PathFocusedWebSiteParserFactory;

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
		ICrawler crawler = WebSiteCrawlerFactory.createCrawler(webSiteURL); 
		ICrawledWebSite website = crawler.crawl();
		IPathFocusedWebSiteParser parser = PathFocusedWebSiteParserFactory.createParser(website);
		IWebSiteParsedPathsHolder webSitePathHolder = parser.parse();
		IEntityPathExtractor extractor = EntityPathExtractor.create(webSitePathHolder);
		IEntityGraph entityGraph = EntityGraph.create(webSiteURL, extractor.extract());
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
		ICrawler crawler = WebSiteCrawlerFactory.createCrawler(webSiteURL, 1);
		
 		ICrawledWebSite website = crawler.crawl();
		IPathFocusedWebSiteParser parser = PathFocusedWebSiteParserFactory.createParser(website);
		IWebSiteParsedPathsHolder webSitePathHolder = parser.parse();
		
//		WebPagePathsImpl webPagePath = new WebPagePathsImpl(webPages.get(0));  
//		webPagePath.construct();
		IEntityPathExtractor extractor = EntityPathExtractor.create(webSitePathHolder);
//		EntityPathExtractorImpl extractor = new EntityPathExtractorImpl(crawler, new SimplePageTemplatesSplitter());
		entityGraph = (EntityGraph) EntityGraph.create(webSiteURL, extractor.extract());
//		entityGraph.printForwardTermGraphNodesAfterAnalyzing();
		
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
