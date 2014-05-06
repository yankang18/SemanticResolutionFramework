package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.List;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IClassifiedInstancesAccessor {

	public List<String> getInstancesOfOntClass(String className);

	public List<String> getInstances();

	Collection<OntoClassInfo> getAllClasses();

	List<String> getInstancesOfOntClass(OntoClassInfo ontClass);  
	
	void showRepositoryDetail();

}