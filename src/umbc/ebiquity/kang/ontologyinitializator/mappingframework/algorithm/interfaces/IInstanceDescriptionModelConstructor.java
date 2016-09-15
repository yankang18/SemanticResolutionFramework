package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;

public interface IInstanceDescriptionModelConstructor {

	/**
	 * extract triple repository from a web site (e.g., based on the entity
	 * paths extracted from the web site and the entity graph constructed from
	 * the entity paths)
	 * 
	 * @return an instance of the TripleStore
	 */
	IInstanceDescriptionModel extractTripleRepository();

}
