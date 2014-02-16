package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.IOException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.TripleRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;


public class TripleRepositoryExtractionTest {
	
	@Test
	public void ExtractTriplesFromWebSite() throws IOException{ 
		this.createTripleRepository(false).showTriples();
	}
	
	@Test
	@Ignore
	public void LoadTriplesFromLocalStorage() throws IOException{ 
		this.createTripleRepository(true).showTriples();
	}
	
	private ITripleRepository createTripleRepository(boolean local) throws IOException{
		
//		String webSiteURLString = "http://www.astromfg.com";
		String webSiteURLString = "http://www.accutrex.com";
		URL webSiteURL = new URL(webSiteURLString);
		
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		String tripleRepository = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		System.out.println("Triple Repo: " + tripleRepository);
		ITripleRepository extractedTripleStore = TripleRepositoryFactory.createTripleRepository(webSiteURL, local);
		return extractedTripleStore;
	}
}
