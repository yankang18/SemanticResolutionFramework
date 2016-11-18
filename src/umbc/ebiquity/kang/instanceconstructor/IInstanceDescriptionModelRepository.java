package umbc.ebiquity.kang.instanceconstructor;


public interface IInstanceDescriptionModelRepository {

	/**
	 * 
	 * @param model
	 * @param repositoryFullName
	 * @return
	 */
	public boolean save(IInstanceDescriptionModel model, String repositoryFullName);

	/**
	 * 
	 * @param repositoryFullName
	 * @return
	 */
	public IInstanceDescriptionModel load(String repositoryFullName);
}
