package umbc.ebiquity.kang.instanceconstructor.builder;

import umbc.ebiquity.kang.entityframework.IEntityGraph;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;

public interface IInstanceDescriptionModelBuilder {

	/**
	 * extract triple repository from a web site (e.g., based on the entity
	 * paths extracted from the web site and the entity graph constructed from
	 * the entity paths)
	 * 
	 * @param entityGraph
	 * @return an instance of the Instance Description Model
	 */
	IInstanceDescriptionModel build(IEntityGraph entityGraph);

}
