package umbc.ebiquity.kang.instanceconstructor.model.builder;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.entityframework.IEntityGraph;
import umbc.ebiquity.kang.entityframework.IEntityPathExtractor;
import umbc.ebiquity.kang.entityframework.impl.EntityGraph;
import umbc.ebiquity.kang.entityframework.impl.EntityPathExtractor;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.impl.FileModelRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;
import umbc.ebiquity.kang.webpageparser.Crawler;
import umbc.ebiquity.kang.webpageparser.impl.WebSiteCrawler;

public class InstanceDescriptionModelFactory {

	public static IInstanceDescriptionModel construct(URL webSiteURL) throws IOException {
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		return construct(webSiteURL, tripleRepositoryName);
	}

	public static IInstanceDescriptionModel construct(URL webSiteURL, String modelName) throws IOException {

		Crawler crawler = WebSiteCrawler.createCrawler(webSiteURL);
		crawler.crawl();

		// extract Entity Paths and create Entity Graph
		IEntityPathExtractor extractor = EntityPathExtractor.create(crawler);
		IEntityGraph entityGraph = EntityGraph.create(webSiteURL, extractor.extractor());
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
		FileModelRepository repo = new FileModelRepository();
		repo.save(extractedTripleStore, "testRepo");
	}
}
