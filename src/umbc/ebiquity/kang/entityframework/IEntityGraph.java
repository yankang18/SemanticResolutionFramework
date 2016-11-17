package umbc.ebiquity.kang.entityframework;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.entityframework.object.Entity;
import umbc.ebiquity.kang.entityframework.object.EntityNode;
import umbc.ebiquity.kang.entityframework.object.EntityPath;
import umbc.ebiquity.kang.websiteparser.object.LeafNode;

public interface IEntityGraph {

	URL getWebSiteURL();

	Map<EntityNode, Set<Concept>> getInstance2ConceptSetMap();

	boolean isRelation(EntityNode node);

	boolean isRelation(LeafNode leafNode);

	boolean isRelation(Entity term);

	Collection<EntityPath> getEntityPaths();

	Collection<EntityNode> getDirectDescendants(EntityNode entityNode);

	Collection<EntityNode> getDirectAnscentors(EntityNode entityNode);

	Collection<EntityNode> getEntityNodes();

	EntityNode getGenericRelationNode(EntityNode specificRelationNode); 
	
	Collection<EntityNode> getCandidateInstances(); 
	
	/**
	 * Analyze the topological structure of the entity graph to identify
	 * relations and instances
	 */
    void labelEntityGraph();

	Set<EntityNode> getGenericRelationNodeSet();

}
