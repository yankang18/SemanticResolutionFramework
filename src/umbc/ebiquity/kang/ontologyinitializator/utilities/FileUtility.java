package umbc.ebiquity.kang.ontologyinitializator.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileUtility {
	
	public static String convertURL2FileName(URL webSiteURL) {
		String webSiteURLString = webSiteURL.toString();
		int indexOfFirstPeriod = webSiteURLString.indexOf(".");
		String temp = webSiteURLString.substring(indexOfFirstPeriod + 1);
		indexOfFirstPeriod = temp.indexOf(".");
		temp = temp.substring(0, indexOfFirstPeriod);
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
