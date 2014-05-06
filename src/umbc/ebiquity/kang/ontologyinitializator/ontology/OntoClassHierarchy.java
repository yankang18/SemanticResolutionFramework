package umbc.ebiquity.kang.ontologyinitializator.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;

/**
 * Object of this Java class contains all ontology classes from the same
 * ontology class hierarchy (i.e., these classes can be the ones that have been
 * matched to some concepts from the concept set of an instance of an RDF triple
 * store).
 * 
 * This object contains the hierarchy number, a collection of class members, a
 * collection of similarities each of which is the similarity between a class
 * and a concept, similarity between the class hierarchy represented by this
 * object and a concept.
 * 
 * @author kangyan2003
 * 
 */
public class OntoClassHierarchy implements Comparable<OntoClassHierarchy> {

	private int classHierarchyNumber;
	private double similarity;
	private List<OntoClassInfo> classMembers;
	private List<Double> classMemberScores;
	private List<Double> classMemberWeights;
	private double weightSum;
	private Collection<Concept2OntClassMapping> MatchedConcept2OntoClassPairs;

	public OntoClassHierarchy(int classHierarchyNumber) {
		this.classHierarchyNumber = classHierarchyNumber;
		this.classMembers = new ArrayList<OntoClassInfo>();
		this.classMemberScores = new ArrayList<Double>();
		this.classMemberWeights = new ArrayList<Double>();
		this.MatchedConcept2OntoClassPairs = new ArrayList<Concept2OntClassMapping>();
	}

	public void addMatchedOntoClass2ConceptPair(Concept2OntClassMapping matchedConcept2OntClassPair, OntoClassInfo targetClass, double annotationScore) {
		MatchedConcept2OntoClassPairs.add(matchedConcept2OntClassPair);
		this.classMembers.add(targetClass);
		this.classMemberScores.add(matchedConcept2OntClassPair.getMappingScore() * annotationScore);
		
		double score = Math.log(matchedConcept2OntClassPair.getConcept().getScore());
		score = score > 1 ? score : 1;
		this.classMemberWeights.add(score);
		weightSum += score;
	}
	
	public void addMatchedOntoClass2ConceptPair(Concept2OntClassMapping matchedConcept2OntClassPair) {
		MatchedConcept2OntoClassPairs.add(matchedConcept2OntClassPair);
		this.classMembers.add(matchedConcept2OntClassPair.getMappedOntoClass());
		this.classMemberScores.add(matchedConcept2OntClassPair.getMappingScore());
		
		double score = Math.log(matchedConcept2OntClassPair.getConcept().getScore());
		score = score > 1 ? score : 1;
		this.classMemberWeights.add(score);
		weightSum += score;
	}
	
	public Collection<Concept2OntClassMapping> getMatchedConcept2OntoClassPairs(){
		return this.MatchedConcept2OntoClassPairs;
	}
	
	public List<OntoClassInfo> getMemebers(){
		return this.classMembers;
	}
	
	public List<Double> getMemeberSimilarities(){
		return this.classMemberScores;
	}
	
	public List<Double> getMemberWeights(){
		return this.classMemberWeights;
	}
	
	public double getWeightsSum(){
		return this.weightSum;
	}
	
	public int getNumberOfMembers(){
		return this.classMembers.size();
	}

	public int getClassHierarchyNumber() {
		return classHierarchyNumber;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public double getSimilarity() {
		return similarity;
	}

	@Override
	public int compareTo(OntoClassHierarchy h2) {
		int h1N = this.getClassHierarchyNumber();
		int h2N = h2.getClassHierarchyNumber();
		if (h1N > h2N) {
			return 1;
		} else if (h1N < h2N) {
			return -1;
		} else {
			return 0;
		}
	}

}
