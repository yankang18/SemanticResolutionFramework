package umbc.ebiquity.kang.ontologyinitializator.evaluation.test;

import java.io.IOException;

import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.AbstractWebUrlLoader;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class WebUrlLoader extends AbstractWebUrlLoader {
	
	@Test
	public void evaluate() throws IOException {
		String fileFullPath = "/Users/yankang/Desktop/Test/WebSiteURLs.txt";

		boolean dirExists = FileUtility.exists(fileFullPath);
		if (dirExists) {
			WebUrlLoader webUrlLoader = new WebUrlLoader();
			webUrlLoader.loadRecords(fileFullPath);
			webUrlLoader.showWebUrls();
		} else {
			System.out.println("No " + fileFullPath);
		}
	}

}
