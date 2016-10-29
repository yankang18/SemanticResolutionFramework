package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;

public class TripleRepositoriesConstructor {

	private Map<URL, String> _failedWebSiteMap2Cause;
	
	public TripleRepositoriesConstructor(){
		_failedWebSiteMap2Cause = new HashMap<URL, String>();
	}
	
	public boolean constructTripleRepositories(Set<URL> webSiteURLs, boolean replace) {

		boolean succeed = true;
		for (URL url : webSiteURLs) {
			System.out.println();
			System.out.println("Construct Triple Repository for <" + url.toExternalForm() + ">");
			try {
				FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
				InstanceDescriptionModelFactory.construct(url);
			} catch (IOException e) {
				_failedWebSiteMap2Cause.put(url, e.getMessage());
				succeed = false;
			}
		}
		return succeed;
	}
	
	public Collection<URL> getURLsOfFailedWebSites(){
		return _failedWebSiteMap2Cause.keySet();
	}
	
	public String getCauseOfCrawlingFailure(URL webSiteURL){
		return _failedWebSiteMap2Cause.get(webSiteURL);
	}
	
	public static void main(String[] arg) { 
		
		TripleRepositoriesConstructor TRC = new TripleRepositoriesConstructor();
		String webPageUrl1 = "http://www.numericalconcepts.com";
		String webPageUrl2 = "http://www.bassettinc.com";
		String webPageUrl3 = "http://www.wisconsinmetalparts.com";
		String webPageUrl4 = "http://www.aerostarmfg.com";
		String webPageUrl5 = "http://www.accutrex.com";
		String webPageUrl6 = "http://www.weaverandsons.com";
		String webPageUrl8 = "http://www.astromfg.com";
		String webPageUrl9 = "http://www.navitekgroup.com";
		String webPageUrl10 = "http://www.cmc-usa.com";
		Set<URL> webURLs = new HashSet<URL>();
		
		try { 
			
			webURLs.add(new URL(webPageUrl1));
			webURLs.add(new URL(webPageUrl2));
			webURLs.add(new URL(webPageUrl3));
			webURLs.add(new URL(webPageUrl4));
			webURLs.add(new URL(webPageUrl5));
			webURLs.add(new URL(webPageUrl6));
//			webURLs.add(new URL(webPageUrl7));
			webURLs.add(new URL(webPageUrl8));
			webURLs.add(new URL(webPageUrl9));
			webURLs.add(new URL(webPageUrl10));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		
		
		TRC.constructTripleRepositories(webURLs, false);
	}
	
	
	

}
