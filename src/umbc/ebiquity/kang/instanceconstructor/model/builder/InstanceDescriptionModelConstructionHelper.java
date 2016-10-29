package umbc.ebiquity.kang.instanceconstructor.model.builder;

import java.io.IOException;
import java.net.URL;

import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class InstanceDescriptionModelConstructionHelper {

	/**
	 * Checks whether the Instance Description Model for the given URL has been constructed.
	 * @param webSiteURL
	 * @return true if the Instance Description Model has been constructed. false otherwise. 
	 */
	public static boolean isConstructed(URL webSiteURL) {
		String repositoryFullName = getRepositoryFullName(webSiteURL);
		return FileUtility.exists(repositoryFullName);
	}

	/**
	 * 
	 * @param webSiteURL
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public static IInstanceDescriptionModel createModel(URL webSiteURL, IInstanceDescriptionModelRepository repo) throws IOException {

		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String fileFullName = getRepositoryFullName(webSiteURL);
		if (FileUtility.exists(fileFullName)) {
			return repo.load(tripleRepositoryName);
		} else {
			boolean succeed = FileUtility.createDirectories(directory);
			if (succeed) {
				return InstanceDescriptionModelFactory.construct(webSiteURL, tripleRepositoryName);
			} else {
				throw new IOException("Create Directories for Triple Repository Failed");
			}
		}
	}
	
	/**
	 * 
	 * @param webSiteURL
	 * @return
	 */
	private static String getRepositoryFullName(URL webSiteURL) {
		String tripleRepositoryName = FileUtility.convertURL2FileName(webSiteURL);
		String directory = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String repositoryFullName = directory + tripleRepositoryName;
		return repositoryFullName;
	}
}
