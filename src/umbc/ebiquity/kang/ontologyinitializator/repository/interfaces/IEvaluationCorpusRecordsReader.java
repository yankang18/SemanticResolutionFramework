package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.List;
import java.util.Set;


public interface IEvaluationCorpusRecordsReader {
	
	String getClassLabelforInstance(String instanceLabel);

	String getPropertyLabelforRelation(String relationLabel);

	void showRecords();

	String getOntClassForConcept(String conceptLabel);

//	Map<String, Set<String>> getConcept2ClassMap(String instanceLabel);

	List<String> getClassSet(String instanceLabel, String concept);

	boolean containsInstance(String instanceLabel);

	Set<String> getInstanceSet();  

}
