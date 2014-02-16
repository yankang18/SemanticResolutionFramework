package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

public interface INamedRepository {
	
	public boolean saveRepository(String repositoryFullName);

	public boolean loadRepository(String repositoryFullName);
	
	public void showRepositoryDetail();
	
	public String getRepositoryName();

}
