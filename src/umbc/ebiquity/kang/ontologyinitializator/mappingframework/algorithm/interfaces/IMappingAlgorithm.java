package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IProprietoryClassifiedInstancesRepository;

public interface IMappingAlgorithm {
	
	public void mapping();

	public IProprietoryClassifiedInstancesRepository getProprietoryClassifiedInstancesRepository();
}
