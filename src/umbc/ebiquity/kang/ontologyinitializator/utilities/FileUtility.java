package umbc.ebiquity.kang.ontologyinitializator.utilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtility {
	
	public static void main(String[] args)  throws MalformedURLException{
		String w1 = "http://www.example1.com";
		String w2 = "http://www.example1.com/kkk";
		String w3 = "http://www.example1.com/kkk.php";
		String w4 = "http://example1.com/kkk.php";
		
		URL ww1 = new URL(w1);
		URL ww2 = new URL(w2);
		URL ww3 = new URL(w3);
		URL ww4 = new URL(w4);
		
		System.out.println("1 " + convertURL2FileName(ww1));
		System.out.println("2 " + convertURL2FileName(ww2));
		System.out.println("3 " + convertURL2FileName(ww3));
		System.out.println("4 " + convertURL2FileName(ww4));
		
	}

	public static String convertURL2FileName(URL webSiteURL) {
		
		String webSiteURLString = webSiteURL.toString();
		webSiteURLString = webSiteURLString.replace("http://", "");

		int indexOfFirstSlash = webSiteURLString.indexOf("/");
		
		if(indexOfFirstSlash != -1){
			webSiteURLString = webSiteURLString.substring(0, indexOfFirstSlash);
		}
//		System.out.println("@ " + webSiteURLString);
		String[] tokens = webSiteURLString.split("\\.");
//		for(String token : tokens)
//		System.out.println("@ " + token);
		String temp;
		if (tokens.length == 3) {
			
//			int indexOfFirstPeriod = webSiteURLString.indexOf(".");
//			temp = webSiteURLString.substring(indexOfFirstPeriod + 1);
//			indexOfFirstPeriod = temp.indexOf(".");
//			temp = temp.substring(0, indexOfFirstPeriod);
			
			temp = tokens[1];
		} else if (tokens.length == 2) {
			temp = tokens[0];
		} else {
			temp = tokens[0];
		}
		return temp;
	}
	
	public static boolean exists(String fileFullName){
		File file = new File(fileFullName);
		return file.exists();
	}
	
	public static boolean createDirectories(String directoryString){
		File dir = new File(directoryString);
		if(dir.exists()){
			return true;
		}
		return dir.mkdirs();
	}
	
	public static boolean createFile(String fileFullName){
		File file = new File(fileFullName);
		if(file.exists()){
			return true;
		}
		try {
			return file.createNewFile();
		} catch (IOException e) {
			return false;
		}
	}

}
