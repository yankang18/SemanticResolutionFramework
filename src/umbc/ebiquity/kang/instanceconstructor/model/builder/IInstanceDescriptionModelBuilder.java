package umbc.ebiquity.kang.instanceconstructor.model.builder;

import umbc.ebiquity.kang.instanceconstructor.entityframework.IReadOnlyEntityGraph;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;

public interface IInstanceDescriptionModelBuilder {

	/**
	 * extract triple repository from a web site (e.g., based on the entity
	 * paths extracted from the web site and the entity graph constructed from
	 * the entity paths)
	 * 
	 * @param entityGraph
	 * @return an instance of the TripleStore
	 */
	IInstanceDescriptionModel build(IReadOnlyEntityGraph entityGraph);

}
