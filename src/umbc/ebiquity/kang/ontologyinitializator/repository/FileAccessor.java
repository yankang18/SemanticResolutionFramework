package umbc.ebiquity.kang.ontologyinitializator.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileAccessor {

	/**
	 * 
	 * @param fileFullName
	 * @param tripleString
	 * @return
	 */
	public static boolean saveTripleString(String fileFullName, String tripleString) {
		File file = new File(fileFullName);
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(tripleString);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
