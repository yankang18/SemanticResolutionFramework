package umbc.ebiquity.kang.entityframework;

import java.util.Collection;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.entityframework.object.EntityNode;

public interface IEntityGraphInstanceConceptsExtractor extends IEntityGraph {
	
	public void separateEntityNodes(Collection<EntityNode> relationNodes,
            Collection<EntityNode> instanceNodes,
            Collection<EntityNode> entityNodes) ;
	
	public void addInstance2ConceptSetMap(EntityNode instance, Set<Concept> conceptSet);

}
