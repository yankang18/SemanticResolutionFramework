package umbc.ebiquity.kang.instanceconstructor.builder;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.entityframework.IEntityGraph;
import umbc.ebiquity.kang.entityframework.IEntityPathExtractor;
import umbc.ebiquity.kang.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.entityframework.impl.EntityPathExtractorImpl;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.impl.FileModelRepository;
import umbc.ebiquity.kang.instanceconstructor.impl.FileSystemRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.websiteparser.ICrawledWebSite;
import umbc.ebiquity.kang.websiteparser.impl.WebSiteCrawlerFactory;
import umbc.ebiquity.kang.websiteparser.support.IPathFocusedWebSiteParser;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.impl.PathFocusedWebSiteParserFactory;

public class InstanceDescriptionModelFactory {

	public static IInstanceDescriptionModel construct(URL webSiteURL) throws IOException {
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		return construct(webSiteURL, tripleRepositoryName);
	}

	public static IInstanceDescriptionModel construct(URL webSiteURL, String modelName) throws IOException {

		ICrawledWebSite website = WebSiteCrawlerFactory.createCrawler(webSiteURL).crawl();
		IPathFocusedWebSiteParser parser = PathFocusedWebSiteParserFactory.createParser(website);
		IWebSiteParsedPathsHolder webSitePathHolder = parser.parse();
		
		// extract Entity Paths and create Entity Graph
		IEntityPathExtractor extractor = EntityPathExtractorImpl.create(webSitePathHolder);
		IEntityGraph entityGraph = EntityGraph.create(webSiteURL, extractor.extract());
		entityGraph.labelEntityGraph();

		// extract triples from the Entity Graph
		IInstanceDescriptionModelBuilder modelBuilder = new InstanceDescriptionModelBuilderImpl();
		IInstanceDescriptionModel IDM = modelBuilder.build(entityGraph);
		return IDM;
	}

	public static void main(String[] args) throws IOException {
		String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		IInstanceDescriptionModel extractedTripleStore = InstanceDescriptionModelFactory.construct(webSiteURL);
//		FileModelRepository repo = new FileModelRepository();
		FileSystemRepository repo = new FileSystemRepository();
		repo.save(extractedTripleStore, "testRepo10");
	}
}
