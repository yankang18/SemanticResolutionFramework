package umbc.ebiquity.kang.ontologyinitializator.entityframework;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityNode;

public interface IEntityGraphRelationExtractor extends IReadOnlyEntityGraph {
	
	public void addRelationInferredFromM2NStructure(EntityNode relationNode);

	public void addSpecificRelation2GeneralRelationMap(EntityNode specificRelationNode, EntityNode generalRelationNode);
}
