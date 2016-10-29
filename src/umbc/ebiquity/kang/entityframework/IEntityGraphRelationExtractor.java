package umbc.ebiquity.kang.entityframework;

import umbc.ebiquity.kang.entityframework.object.EntityNode;

public interface IEntityGraphRelationExtractor extends IEntityGraph {
	
	public void addRelationInferredFromM2NStructure(EntityNode relationNode);

	public void addSpecificRelation2GeneralRelationMap(EntityNode specificRelationNode, EntityNode generalRelationNode);
}
