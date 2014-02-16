package umbc.ebiquity.kang.ontologyinitializator.entityframework.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.Entity;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.EntityNode;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.EntityPath;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.EntityValidator;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IEntityGraphInstanceConceptsExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IEntityGraphRelationExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IEntityPathExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.RelationCluster;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.webpageparser.LeafNode;

public class EntityGraph implements IEntityGraphRelationExtractor, IEntityGraphInstanceConceptsExtractor {

	/**
	 * 
	 */
	private IEntityPathExtractor entityPathConstructor;
	/**
	 * 
	 */
	private Set<EntityPath> entityPathSet;
	/**
	 * 
	 */
	private Map<EntityNode, Map<EntityNode, EntityNode>> entityDescendantsMap;
	private Map<EntityNode, Map<EntityNode, EntityNode>> entityAncestorsMap;
	private Map<EntityNode, Set<Concept>> instanceConceptMap;
	private Set<EntityNode> qualifiedInstanceCandidateSet;

	/**
	 * 
	 */
	private Set<EntityNode> relationsFromTables;
	private Set<EntityNode> relationsFromM2NStructure;
//	private Set<EntityNode> relationsFromM2OneStructure;
	private Map<EntityNode, EntityNode> relationRefTable;

	/**
	 * 
	 */
	private EntityValidator commonValidator; 

	/**
	 * 
	 */
	private Map<EntityNode, Set<EntityNode>> analyzedTermPrecedentsMap;

	/**
	 * 
	 */
	private Map<EntityNode, Map<EntityNode, Set<EntityNode>>> analyzedForwardTermDescendantsMap;

	public EntityGraph(IEntityPathExtractor entityPathConstructor) {
		this.entityPathConstructor = entityPathConstructor;
		this.commonValidator = new EntityValidator();
		this.init(); 
		this.initializeEntityGraph();
	}
	
	private void init(){
		this.entityPathSet = new LinkedHashSet<EntityPath>();
		this.entityDescendantsMap = new LinkedHashMap<EntityNode, Map<EntityNode, EntityNode>>();
		this.entityAncestorsMap = new LinkedHashMap<EntityNode, Map<EntityNode, EntityNode>>();
		this.instanceConceptMap = new HashMap<EntityNode, Set<Concept>>();
		this.analyzedTermPrecedentsMap = new LinkedHashMap<EntityNode, Set<EntityNode>>();
		this.analyzedForwardTermDescendantsMap = new LinkedHashMap<EntityNode, Map<EntityNode, Set<EntityNode>>>();
		this.qualifiedInstanceCandidateSet = new HashSet<EntityNode>();
		this.relationsFromTables = new HashSet<EntityNode>();
		this.relationsFromM2NStructure = new HashSet<EntityNode>();
		this.relationRefTable = new LinkedHashMap<EntityNode, EntityNode>();
	}
	
	/**
	 * construct (initialize) the entity graph from the a set of entity paths
	 * 
	 * @param entityPathSet a set of entity paths
	 */
	private void initializeEntityGraph() {
		System.out.println("[INITIALIZING ENTITY GRAPH ...]");
		
		for (EntityPath entityPath : entityPathConstructor.constructEntityPaths()) {
//			System.out.println("Path:  " + entityPath.printPathBottomUp());
			if (entityPathSet.contains(entityPath)) {
				continue;
			} else {
				entityPathSet.add(entityPath);
			}
		}

		for (EntityPath entityPath : entityPathSet) {
			List<Entity> entityList = new ArrayList<Entity>(entityPath.getEntities());
			int size = entityList.size();
			if (size > 0) {
				LeafNode leafNode = entityPath.getLeafNode();
				EntityNode leafEntityNode = new EntityNode(leafNode);
				EntityNode descendantEntityNode = new EntityNode(entityList.get(0));
				if (this.isValidatedEntityNodePair(leafEntityNode, descendantEntityNode)) {
					this.addAdjacentEntityNodes(leafEntityNode, descendantEntityNode);
				}

			}

			if (size > 1) {
				for (int i = 1; i < size; i++) {
					Entity entity = entityList.get(i - 1);
					Entity descendant = entityList.get(i);
					if (entity.getLevel() != descendant.getLevel() || entity.getScore() != descendant.getScore()) {
						EntityNode entityNode = new EntityNode(entity);
						EntityNode descendantNode = new EntityNode(descendant);
						if (this.isValidatedEntityNodePair(entityNode, descendantNode)) {
							this.addAdjacentEntityNodes(entityNode, descendantNode);
						}
					}
				}
			}
		}
	}
	
	private boolean isValidatedEntityNodePair(EntityNode node1, EntityNode node2){
		if(!node1.getLabel().equals(node2.getLabel()) && commonValidator.isValidEntityNode(node1) && commonValidator.isValidEntityNode(node2)){
			return true;
		}
		return false;
	}

	/**
	 * Add two entity nodes to the entity graph. The second entity node is the
	 * descendant of the first entity node
	 * 
	 * @param entityNode the first entity node
	 * @param descendantNode an entity node that is the descendant of the first entity node
	 */
	private void addAdjacentEntityNodes(EntityNode entityNode, EntityNode descendantNode) {

		if (commonValidator.isValidInstance(entityNode, 7)) {
			qualifiedInstanceCandidateSet.add(entityNode);
		}

		if (commonValidator.isValidInstance(descendantNode, 7)) {
			qualifiedInstanceCandidateSet.add(descendantNode);
		}
		
		this.inferRelationFromTable(entityNode);
		this.inferRelationFromTable(descendantNode);

		this.addEdgeOfGraph(entityNode, descendantNode, entityDescendantsMap);
		this.addEdgeOfGraph(descendantNode, entityNode, entityAncestorsMap);
	}
	
	/**
	 * Analyze the topological structure of the entity graph to identify
	 * relations and instances
	 */
	@Override
	public void analyzeEntityGraph(IRelationExtractionAlgorithm relationExtractorAlgorithm,
								   InstanceConceptSetExtractionAlgorithm instanceConceptSetExtractionAlgorithm) {
		relationExtractorAlgorithm.extractRelation(this);
		instanceConceptSetExtractionAlgorithm.extractInstanceConceptSet(this);
	}
	
	@Override
	public Collection<EntityNode> getCandidateInstances() {
		Set<EntityNode> qualifiedInstance = new HashSet<EntityNode>();
		for (EntityNode instance : qualifiedInstanceCandidateSet) {
			if (!relationsFromTables.contains(instance) && !relationsFromM2NStructure.contains(instance)) {
				qualifiedInstance.add(instance);
			}
		}
		return qualifiedInstance;
	}

	@Override
	public void separateEntityNodes(Collection<EntityNode> relationNodes,
			                         Collection<EntityNode> instanceNodes,
			                         Collection<EntityNode> entityNodes) {
		for (EntityNode entityNode : entityNodes) {
			if (this.isRelation(entityNode)) {
				System.out.println("## REL #: " + entityNode.getLabel());
				/*
				 * 
				 */
				String label = entityNode.getLabel();
				String[] phrases = TextProcessingUtils.tokenizeLabel2PhrasesWithParallelledSemantic(label);
				for (String phrase : phrases) {
					if (!phrase.equals("")) {
						// System.out.println("     Rel:" + token);
						relationNodes.add(new EntityNode(phrase));
					}
				}
			} else {
				System.out.println("## INS #: " + entityNode.getLabel());
				/*
				 * 
				 */
				if(!commonValidator.isValidInstance(entityNode, 8)){
					continue;
				}
				
				String label = entityNode.getLabel();
				String[] phrases = TextProcessingUtils.tokenizeLabel2PhrasesWithParallelledSemantic(label);
				for (String phrase : phrases) {
					if (!phrase.equals("")) {
						// System.out.println("     Rel:" + token);
						instanceNodes.add(new EntityNode(phrase));
					}
				}
			}
		}
	}
	
	private void inferRelationFromTable(EntityNode entityNode){
		Entity entity = entityNode.getTerm();
		if (entity != null && entity.getScore() == 7.0) {
			if (commonValidator.isValidRelation(entityNode, 7)) {
				this.addRelationInferredFromTable(entityNode);
				this.addSpecificRelation2GeneralRelationMap(entityNode, entityNode);
			}
		}
	}
	
	private void addRelationInferredFromTable(EntityNode relationNode){
		relationsFromTables.add(relationNode);
	}
	
	@Override
	public void addRelationInferredFromM2NStructure(EntityNode relationNode){ 
		relationsFromM2NStructure.add(relationNode);
	}
	
	@Override
	public void addSpecificRelation2GeneralRelationMap(EntityNode specificRelationNode, EntityNode generalRelationNode){
		relationRefTable.put(specificRelationNode, generalRelationNode);
	}
	
	@Override
	public void addInstance2ConceptSetMap(EntityNode instance, Set<Concept> conceptSet){
		this.instanceConceptMap.put(instance, conceptSet);
	}
	
	private void addEdgeOfGraph(EntityNode fromNode, EntityNode toNode, Map<EntityNode, Map<EntityNode, EntityNode>> edgeDirectionMap){
		Map<EntityNode, EntityNode> toNodes;
		if (edgeDirectionMap.containsKey(fromNode)) {
			toNodes = edgeDirectionMap.get(fromNode);
			if (toNodes.containsKey(toNode)) {
				EntityNode existedToNode = toNodes.get(toNode);
				existedToNode.addScore(toNode.getScore());
			} else {
				toNodes.put(toNode, toNode);
			}
		} else {
			toNodes = new LinkedHashMap<EntityNode, EntityNode>();
			toNodes.put(toNode, toNode);
			edgeDirectionMap.put(fromNode, toNodes);
		}
	}

	@Override
	public EntityNode getGeneralizedRelationNode(EntityNode specificRelationNode) {
		return this.relationRefTable.get(specificRelationNode);
	}

	@Override
	public Collection<EntityNode> getEntityNodes() {
		return entityDescendantsMap.keySet();
	}
	
	/**
	 * Get direct descendants (in the entity graph) of the inputed entity node
	 * 
	 * @param entityNode
	 * @return a collection of entity nodes that are descendants of the inputed entity node
	 */
	@Override
	public Collection<EntityNode> getDirectDescendants(EntityNode entityNode) {
		Collection<EntityNode> descendants = new HashSet<EntityNode>();
		if (entityDescendantsMap.get(entityNode) != null) {
			for (EntityNode descendant : entityDescendantsMap.get(entityNode).keySet()) {
					descendants.add(descendant);
			}
		}
		return descendants;
	}
	
	@Override
	public Collection<EntityNode> getDirectAnscentors(EntityNode entityNode) {
		
		Collection<EntityNode> anscentors = new HashSet<EntityNode>();
		if (entityAncestorsMap.get(entityNode) != null) {
			for (EntityNode anscentor : entityAncestorsMap.get(entityNode).keySet()) {
					anscentors.add(anscentor);
			}
		}
		return anscentors;
	}
	
	@Override
	public Collection<EntityPath> getEntityPaths(){
		return this.entityPathSet;
	}
	
	@Override
	public boolean isRelation(Entity term){
		return this.isRelation(new EntityNode(term));
	}

	@Override
	public boolean isRelation(LeafNode leafNode) { 
		return this.isRelation(new EntityNode(leafNode));
	}
	
	
	@Override
	public boolean isRelation(EntityNode node){
		boolean isRelation = false;
		if (this.relationsFromTables.contains(node) || this.relationsFromM2NStructure.contains(node)) {
			isRelation = true;
		}
		return isRelation;
	}
	
	@Override
	public Map<EntityNode, Set<Concept>> getInstance2ConceptSetMap() {
		return instanceConceptMap;
	}
	
	@Override
	public URL getWebSiteURL(){
		return this.entityPathConstructor.getWebSiteURL();
	}

	public void printForwardTermGraphNodesAfterAnalyzing() {
		System.out.println();
		System.out.println("-------- Start Printing Forward TermGraphNodes ---------");

		StringBuilder sb = new StringBuilder();
		for (EntityNode termNode : analyzedForwardTermDescendantsMap.keySet()) {
			sb.append("\n Term Node: " + termNode.getProcessedTermLabel());
			Map<EntityNode, Set<EntityNode>> descendantProvenanceMap = analyzedForwardTermDescendantsMap.get(termNode);
			for (EntityNode descendantNode : descendantProvenanceMap.keySet()) {
				sb.append("\n       Descendant- " + descendantNode.getProcessedTermLabel() + "[" + descendantNode.getScore() + "]");
				Set<EntityNode> provenanceNodes = descendantProvenanceMap.get(descendantNode);
				for (EntityNode provenanceNode : provenanceNodes) {
					sb.append("\n            Provenance- " + provenanceNode.getProcessedTermLabel());
					if (analyzedForwardTermDescendantsMap.get(provenanceNode) != null) {
						for (EntityNode descendantNode1 : analyzedForwardTermDescendantsMap.get(provenanceNode).keySet()) {
							sb.append("\n                   Provenance-Descendant- " + descendantNode1.getProcessedTermLabel());
						}
					}
				}
			}
		}
		System.out.println(sb.toString());

		System.out.println("-------- End of Printing Forward TermGraphNodes ---------");
		System.out.println();
	}
	
	public void printBackwardTermGraphNodesAfterAnalyzing() {
		System.out.println();
		System.out.println("-------- Start Printing Backward TermGraphNodes ---------");

		StringBuilder sb = new StringBuilder();
		for (EntityNode termNode : analyzedTermPrecedentsMap.keySet()) {
			sb.append("\n Term Node: " + termNode.getProcessedTermLabel());
			Set<EntityNode> descendantSet = analyzedTermPrecedentsMap.get(termNode);
			for (EntityNode descendantNode : descendantSet) {
				sb.append("\n            Descendant- " + descendantNode.getProcessedTermLabel());
			}
		}
		System.out.println(sb.toString());

		System.out.println("-------- End of Printing Backward TermGraphNodes ---------");
		System.out.println();
	}

	
	public void showRelations() {
		System.out.println("### Relations identified from table ###");
		for (EntityNode relation : relationsFromTables) {
			System.out.println(relation.getLabel());
		}
		
		System.out.println();
		System.out.println("### Relations identified from M-to-N structure ###");
		int count = 0;
		for (EntityNode relation : this.relationRefTable.keySet()) {
			System.out.println("[" + relation + "] -> [" + relationRefTable.get(relation).getLabel()+ "]");
			count++;
		}
		System.out.println("number of relations from M2N " + count);
		
	}
	
	public void showInstanceConceptSet() {
		for (EntityNode instanceNode : instanceConceptMap.keySet()) {
			System.out.println(instanceNode.getLabel());
			for (Concept c : instanceConceptMap.get(instanceNode)) {
				System.out.println("    " + c.getConceptName() + ", " + c.getScore());
			}

		}
	}
}
