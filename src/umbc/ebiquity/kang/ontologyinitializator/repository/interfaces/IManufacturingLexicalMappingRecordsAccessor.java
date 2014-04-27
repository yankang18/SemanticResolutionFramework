package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;


public interface IManufacturingLexicalMappingRecordsAccessor extends IManufacturingLexicalMappingRecordsReader, IManufacturingLexicalMappingRecordsUpdater {
	
	public void parseRecord(String record);

}
