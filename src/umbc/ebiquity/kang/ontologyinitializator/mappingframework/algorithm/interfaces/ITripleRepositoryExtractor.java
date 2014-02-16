package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;

public interface ITripleRepositoryExtractor {

	/**
	 * extract triple repository from a web site (e.g., based on the entity
	 * paths extracted from the web site and the entity graph constructed from
	 * the entity paths)
	 * 
	 * @return an instance of the TripleStore
	 */
	ITripleRepository extractTripleRepository();

}
