package umbc.ebiquity.kang.instanceconstructor.model;


public interface IInstanceDescriptionModelRepository {

	public boolean save(IInstanceDescriptionModel model, String repositoryFullName);

	public IInstanceDescriptionModel load(String repositoryFullName);
}
