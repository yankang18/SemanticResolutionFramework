package umbc.ebiquity.kang.instanceconstructor.entityframework;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.entityframework.impl.InstanceConceptSetExtractionAlgorithm;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Entity;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityNode;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityPath;
import umbc.ebiquity.kang.webpageparser.LeafNode;

public interface IReadOnlyEntityGraph {

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
	
    void labelEntityGraph(IRelationExtractionAlgorithm relationExtractorAlgorithm, InstanceConceptSetExtractionAlgorithm instanceConceptSetExtractionAlgorithm);

	Set<EntityNode> getGenericRelationNodeSet();

}
