package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Map;
import java.util.Set;



public interface IEvaluationCorpusRecordsAccessor extends IEvaluationCorpusRecordsReader {

//	void parseRecord(String record);
	
	public void addClassifiedInstance(String instanceLabel, String classLabel);
	
	public void addRelation2PropertyMapping(String relationLabel, String propertyLabel);
	
	public void addConcept2ClassMappingForInstance(String instanceLabel, String conceptLabel, String classLabel);

	void addConcept2ClassMap(String conceptLabel, String classLabel); 


}
