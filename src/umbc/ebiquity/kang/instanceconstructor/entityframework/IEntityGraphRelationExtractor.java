package umbc.ebiquity.kang.instanceconstructor.entityframework;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityNode;

public interface IEntityGraphRelationExtractor extends IReadOnlyEntityGraph {
	
	public void addRelationInferredFromM2NStructure(EntityNode relationNode);

	public void addSpecificRelation2GeneralRelationMap(EntityNode specificRelationNode, EntityNode generalRelationNode);
}
