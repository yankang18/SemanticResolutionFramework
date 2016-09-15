package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.entityframework.IReadOnlyEntityGraph;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Entity;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityNode;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityPath;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityPathSorterByLeafNodeText;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityValidator;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.InstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.Triple;
import umbc.ebiquity.kang.instanceconstructor.model.Triple.BuiltinType;
import umbc.ebiquity.kang.instanceconstructor.model.Triple.PredicateType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceDescriptionModelConstructor;
import umbc.ebiquity.kang.webpageparser.LeafNode;

public class InstanceDescriptionModelConstructorImpl implements IInstanceDescriptionModelConstructor {
	
	private IReadOnlyEntityGraph entityGraph;
	private EntityValidator entityValidator;
	
	public InstanceDescriptionModelConstructorImpl(IReadOnlyEntityGraph entityGraph) {
		this.entityGraph = entityGraph;
		this.entityValidator = new EntityValidator();
	}
	
	@Override
	public IInstanceDescriptionModel extractTripleRepository() {
		System.out.println("EXTRACTING TRIPLE STORE ...");
		Collection<Triple> tripleSet = new ArrayList<Triple>();
		List<EntityPath> entityPathList = new ArrayList<EntityPath>(this.entityGraph.getEntityPaths());
		Collections.sort(entityPathList, new EntityPathSorterByLeafNodeText());
		
		/*
		 * 
		 */
		boolean lookingForRelation = false;
		boolean lookingForInstance = false;
		EntityNode relationNode = null;
		Set<EntityNode> objectNodes = new HashSet<EntityNode>();
		for (EntityPath entityPath : entityPathList) {
			LeafNode leafNode = entityPath.getLeafNode();
			EntityNode firstEntityNode = new EntityNode(leafNode);
			/*
			 * skip the situation when the leaf entity node is relation
			 */
			if(!entityValidator.isValidEntityNode(firstEntityNode) || this.entityGraph.isRelation(leafNode)){
				continue;
			}
			List<Entity> entityList = new ArrayList<Entity>(entityPath.getEntities());
			int size = entityList.size();
			objectNodes.clear();
			objectNodes.add(firstEntityNode);
			lookingForRelation = true;
			lookingForInstance = false;
			relationNode = null;
			for (int index = 0; index < size; index++) {
				Entity entity = entityList.get(index);
				if(!entityValidator.isValideEntity(entity)){
					continue;
				}
				if (lookingForRelation) {
					/*
					 * 
					 */
					if (this.entityGraph.isRelation(entity)) {
						relationNode = new EntityNode(entity);
						lookingForRelation = false;
						lookingForInstance = true;
					} else {
						objectNodes.add(new EntityNode(entity));
					}
				}
				if (lookingForInstance) {
					if (!this.entityGraph.isRelation(entity)) {
						/*
						 * 
						 */
						EntityNode subjectNode = new EntityNode(entity);
						for (EntityNode objectNode : objectNodes) {
							
							EntityNode generalRelationNode = entityGraph.getGenericRelationNode(relationNode);
							Triple triple = new Triple(subjectNode.getLabel(), subjectNode.getProcessedTermLabel(),
									generalRelationNode.getLabel(), objectNode.getLabel(), objectNode.getProcessedTermLabel());
							triple.setPredicateType(PredicateType.Custom);
							tripleSet.add(triple);
							
//							System.out.println(relationNode.getLabel() + " --> " + generalRelationNode.getLabel() + ":  " +  objectNode.getLabel());
							
						}
						objectNodes.clear();
						objectNodes.add(subjectNode);
						lookingForRelation = true;
						lookingForInstance = false;
						relationNode = null;
					}
				}
			}
		}

		Map<EntityNode, Set<Concept>> instance2ConceptSetMap = entityGraph.getInstance2ConceptSetMap();
		for (EntityNode instanceNode : instance2ConceptSetMap.keySet()) {
			Set<Concept> conceptSet = instance2ConceptSetMap.get(instanceNode);
			for (Concept concept : conceptSet) {
				Triple triple = new Triple(instanceNode, concept);
				triple.setPredicateType(PredicateType.Builtin);
				tripleSet.add(triple);
			}
		}
		
		
		Set<EntityNode> genericRelationNodes = entityGraph.getGenericRelationNodeSet();
		for (EntityNode genericRelationNode : genericRelationNodes) {
			Triple triple = new Triple(genericRelationNode, BuiltinType.Property);
			triple.setPredicateType(PredicateType.Builtin);
			tripleSet.add(triple);
		}
		return new InstanceDescriptionModel(tripleSet, this.entityGraph.getWebSiteURL());
	}
}
