package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;

public interface IModelSemanticResolver {
	
	public void resolve();

	public IClassifiedInstancesRepository getProprietoryClassifiedInstancesRepository();

	Collection<ClassifiedInstanceDetailRecord> getClassifiedInstances();

	Map<String, MatchedOntProperty> getRelation2PropertyMap();
}
