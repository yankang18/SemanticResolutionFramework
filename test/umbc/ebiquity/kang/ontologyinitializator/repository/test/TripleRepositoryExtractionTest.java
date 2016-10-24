package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.IOException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceRepository;
import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceFileRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;


public class TripleRepositoryExtractionTest {
	
	@Test
	public void ExtractTriplesFromWebSite() throws IOException{ 
		this.createTripleRepository(true).showTriples();
	}
	
	@Test
	@Ignore
	public void LoadTriplesFromLocalStorage() throws IOException {
		this.createTripleRepository(true).showTriples();
	}

	private IInstanceDescriptionModel createTripleRepository(boolean local) throws IOException {

		String webSiteURLString = "http://www.aerostarmfg.com";
		// String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		String tripleRepository = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		System.out.println("Triple Repo: " + tripleRepository);
		IInstanceDescriptionModel extractedTripleStore;
		if (local) {
			IInstanceRepository repo = new InstanceFileRepository();
			extractedTripleStore = InstanceDescriptionModelFactory.createModel(webSiteURL, repo);
		} else {
			extractedTripleStore = InstanceDescriptionModelFactory.createModel(webSiteURL);
		}
		return extractedTripleStore;
	}
}
