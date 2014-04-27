package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityGraph;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityPathExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.impl.RelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.webpageparser.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.webpageparser.WebSiteCrawler;

public class EntityGraphLabelingTest {
	
	private EntityGraph entityGraph;
	private WebSiteCrawler crawler ;
	
	public static void main(String[] arg) throws IOException{
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		
//		String webSiteURLString = "http://www.astromfg.com";
//		String webSiteURLString = "http://www.accutrex.com";
//		String webSiteURLString = "http://www.lincolnparkboring.com";
		String webSiteURLString = "http://www.princetonind.com";
		URL webSiteURL = new URL(webSiteURLString);
		WebSiteCrawler crawler = new WebSiteCrawler(webSiteURL); 
		crawler.crawl();
		
		EntityGraph entityGraph = new EntityGraph(new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter()));
//		entityGraph.printForwardTermGraphNodesAfterAnalyzing();
//		entityGraph.analyzeEntityGraph();
//		entityGraph.showInstanceConceptSet();
		
	}
	
	@Before
	public void Init() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		
//		String webSiteURLString = "http://www.astromfg.com";
		String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		crawler = new WebSiteCrawler(webSiteURL);
		crawler.crawl();
	}

	@Test
	public void setUp(){
		InitializeEntityGraphTest();
		RelationExtractionAlgorithmTest();
//		InstanceConceptSetExtractionAlgorithmTest();
	}
	
	public void InitializeEntityGraphTest(){
		entityGraph = new EntityGraph(new EntityPathExtractor(crawler, new SimplePageTemplatesSplitter()));
		entityGraph.printForwardTermGraphNodesAfterAnalyzing();
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
