package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

public interface IManufacturingLexicalMappingRepository extends IManufacturingLexicalMappingRecordsReader,
		IManufacturingLexicalMappingRecordsUpdater {
	
	public boolean saveRepository();

	public boolean loadRepository();
}
