package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

public interface IReadWriteRepository extends IReadOnlyRepository {

	public boolean saveRepository();
}
