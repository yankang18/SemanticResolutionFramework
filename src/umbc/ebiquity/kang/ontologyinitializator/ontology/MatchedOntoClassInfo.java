package umbc.ebiquity.kang.ontologyinitializator.ontology;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MatchedOntoClassInfo {
	
	private OntoClassInfo matchedOntoClassInfo;
	private double similarity;
	private int hierarchyNumber;
	private Collection<OntoClassInfo> firstLevelRecommendedOntoClasses;
	private Collection<OntoClassInfo> secondLevelRecommendedOntoClasses;
	private Map<String, Double> hierarchyNumber2ClosenessScoreMap;
	
	public MatchedOntoClassInfo(){
		firstLevelRecommendedOntoClasses = new HashSet<OntoClassInfo>();
		secondLevelRecommendedOntoClasses = new HashSet<OntoClassInfo>();
		hierarchyNumber2ClosenessScoreMap = new HashMap<String, Double>();
	}
	public void setMatchedOntoClassInfo(OntoClassInfo matchedOntoClassInfo) {
		this.matchedOntoClassInfo = matchedOntoClassInfo;
	}
	public OntoClassInfo getMatchedOntoClassInfo() {
		return matchedOntoClassInfo;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setFirstLevelRecommendedOntoClasses(Collection<OntoClassInfo> recommendedOntoClasses) {
		this.firstLevelRecommendedOntoClasses = recommendedOntoClasses;
	}
	public Collection<OntoClassInfo> getFirstLevelRecommendedOntoClasses() {
		return firstLevelRecommendedOntoClasses;
	}
	public void setSecondLevelRecommendedOntoClasses(Collection<OntoClassInfo> secondLevelRecommendedOntoClasses) {
		this.secondLevelRecommendedOntoClasses = secondLevelRecommendedOntoClasses;
	}
	public Collection<OntoClassInfo> getSecondLevelRecommendedOntoClasses(){
		return this.secondLevelRecommendedOntoClasses;
	}
	public void setClassHierarchyNumber(int classHierarchyNumber) {
		this.hierarchyNumber = classHierarchyNumber;
	}

	public int getClassHierarchyNumber() {
		return hierarchyNumber;
	}
	
	public void recordClosenessScoresForClassHierarchy(Map<OntoClassHierarchy, Double> classHierarchyClosenessMap) {
		this.hierarchyNumber2ClosenessScoreMap = new HashMap<String, Double>();
		for(OntoClassHierarchy hierarchy : classHierarchyClosenessMap.keySet()){
			String hierarchyNumber = String.valueOf(hierarchy.getClassHierarchyNumber());
			double closenessScore = classHierarchyClosenessMap.get(hierarchy);
			this.hierarchyNumber2ClosenessScoreMap.put(hierarchyNumber, closenessScore);
		}
	}
	
	public void recordClosenessScoresForClassHierarchy2(Map<String, Double> classHierarchyClosenessMap) {
		this.hierarchyNumber2ClosenessScoreMap = new HashMap<String, Double>();
		for(String hierarchyNumber : classHierarchyClosenessMap.keySet()){
			double closenessScore = classHierarchyClosenessMap.get(hierarchyNumber);
			this.hierarchyNumber2ClosenessScoreMap.put(hierarchyNumber, closenessScore);
		}
	}
	
	public Map<String, Double> getClassHierarchyNumber2ClosenessScoreMap(){
		return this.hierarchyNumber2ClosenessScoreMap;
	}

}
