package umbc.ebiquity.kang.ontologyinitializator.entityframework;

import java.util.Collection;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityNode;

public interface IEntityGraphInstanceConceptsExtractor extends IReadOnlyEntityGraph {
	
	public void separateEntityNodes(Collection<EntityNode> relationNodes,
            Collection<EntityNode> instanceNodes,
            Collection<EntityNode> entityNodes) ;
	
	public void addInstance2ConceptSetMap(EntityNode instance, Set<Concept> conceptSet);

}
