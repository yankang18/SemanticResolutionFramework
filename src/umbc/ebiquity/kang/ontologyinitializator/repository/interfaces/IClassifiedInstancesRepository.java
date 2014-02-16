package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.List;

public interface IClassifiedInstancesRepository extends IReadOnlyRepository {

	public List<String> getInstancesOfOntClass(String className);

	public List<String> getInstances(); 

}
