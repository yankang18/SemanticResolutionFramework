package umbc.ebiquity.kang.ontologyinitializator.entityframework.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityNode;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces.IEntityGraphInstanceConceptsExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LongestCommonPhraseAnalyzer;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.textprocessing.stemmer.PluralStemmer;

public class InstanceConceptSetExtractionAlgorithm {
	
	private LongestCommonPhraseAnalyzer commonPhraseAnalyzer;
	private double _distanceDecayFactor = 0.9;
	
	public InstanceConceptSetExtractionAlgorithm(){
		commonPhraseAnalyzer = new LongestCommonPhraseAnalyzer();
	}
	
	public void extractInstanceConceptSet(IEntityGraphInstanceConceptsExtractor entityGraph) {
		for (EntityNode instance : entityGraph.getCandidateInstances()) {

			System.out.println(instance.getLabel());
			Map<Concept, Concept> conceptSet = new HashMap<Concept, Concept>();

			/*
			 * get the concepts by analyzing the instance itself
			 */
			this.getConceptByAnalyzingInstance(entityGraph, instance, conceptSet);
			/*
			 * get the concepts of the inputed instance by traversing the Entity
			 * Graph
			 */
			this.getConceptsByTraversingEntityGraph(entityGraph, instance, conceptSet, 1, 2);

			// TEST
			System.out.println();
			for (Concept concept : conceptSet.keySet()) {
				System.out.println("[TEST]->>> " + concept.getConceptName() + " (" + concept.getScore() + ")");
			}

			entityGraph.addInstance2ConceptSetMap(instance, conceptSet.keySet());
			System.out.println("---------------------------");
		}
	}
	
	/**
	 * get the concepts by analyzing the inputed instance
	 * 
	 * @param instance
	 * @param conceptSet
	 */
	private void getConceptByAnalyzingInstance(IEntityGraphInstanceConceptsExtractor entityGraph, EntityNode instance, Map<Concept, Concept> conceptSet) {

		/*
		 * Currently, this method is kind of ad hoc. It first replaces some
		 * special characters in the instance; then it splits the instance based
		 * on coordinating conjunctions or characters
		 */
		String[] phrases = TextProcessingUtils.tokenizeLabel2PhrasesWithParallelledSemantic(instance.getLabel());
		for (String conceptLabel : phrases) {
			Concept concept = new Concept(conceptLabel, true);
			conceptSet.put(concept, concept);
			System.out.println("###1: " + conceptLabel);
		}
	}

	/**
	 * Get the concepts of the inputed instance by traversing the Entity Graph
	 * 
	 * @param instance
	 * @param conceptSet
	 * @param currentDegree
	 * @param maxDegree
	 */
	private void getConceptsByTraversingEntityGraph(IEntityGraphInstanceConceptsExtractor entityGraph, EntityNode instance, Map<Concept, Concept> conceptSet, int currentDegree, int maxDegree) {
		
		if(currentDegree > maxDegree) return;
		System.out.println("### TRAVERSING DEGREE " + currentDegree + " ...");
		System.out.println("###     Previous: " + instance.getLabel());
		Collection<EntityNode> d1EntityNodes = entityGraph.getDirectDescendants(instance);
//		for (EntityNode entityNode : d1EntityNodes) {
//			System.out.println("      Desc:" + entityNode.getLabel());
//		}
		
		Collection<EntityNode> relationNodes = new ArrayList<EntityNode>();
		Collection<EntityNode> instanceNodes = new ArrayList<EntityNode>();

		/*
		 * separate the descendants (entity nodes) into two group: relations and
		 * instances
		 */
		entityGraph.separateEntityNodes(relationNodes, instanceNodes, d1EntityNodes);
		
		/*
		 * find common phrases from all the relation nodes
		 */
		this.extractConcepts(conceptSet, relationNodes, currentDegree - 1);

		System.out.println();
		/*
		 * find common phrases from all the instance nodes
		 */
		this.extractConcepts(conceptSet, instanceNodes, currentDegree - 1);

		for (EntityNode instanceNode : instanceNodes) {
			this.getConceptsByTraversingEntityGraph(entityGraph, instanceNode, conceptSet, currentDegree + 1, maxDegree);
		}	
	}
	
	
	private void extractConcepts(Map<Concept, Concept> conceptSet, Collection<EntityNode> instanceNodes, double distanceDegree){
		Map<String, Double> commonInstancePhraseMap = commonPhraseAnalyzer.computeCommonPhrases(instanceNodes);
		for (String commonPhrase : commonInstancePhraseMap.keySet()) {
			Concept concept = new Concept(TextProcessingUtils.pluralStem(commonPhrase));
			double phraseCount = commonInstancePhraseMap.get(commonPhrase);
			double distanceDecayFactor = Math.pow(this._distanceDecayFactor, distanceDegree);
			double totalScore = concept.getScore() * phraseCount * distanceDecayFactor;
			if(conceptSet.containsKey(concept)){
				conceptSet.get(concept).addScore(totalScore);
			} else {
				concept.setScore(totalScore);
				conceptSet.put(concept, concept);
			}
		}
	}

}
