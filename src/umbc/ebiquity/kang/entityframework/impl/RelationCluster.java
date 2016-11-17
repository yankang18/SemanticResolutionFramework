package umbc.ebiquity.kang.entityframework.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.EntityNode;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.SimpleLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ILexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.EqualStemWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.OrderedWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SimpleLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.CardinalitySensitiveSetSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ISetSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;
import umbc.ebiquity.kang.textprocessing.phraseextractor.impl.SequenceInReversedOrderPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class RelationCluster {
	 
	private ILexicalFeatureExtractor _phraseExtractor;
	private Set<String> _relationMemeber; 
	private List<List<String>> _relationWordLists;
	private Map<String, Double> _relationWordList2Frequency;
	private Map<String, Double> _relationWordList2Significant;
	private Set<String> _rangeSet;
	private Set<String> _domainSet;
	private List<String> _centerRelationWordList;
//	private SimilarityAlgorithm similarityAlg = new SimilarityAlgorithm();
	private IWordListSimilarity wordListSimilarity = new OrderedWordListSimilarity();
	private ISetSimilarity setSimilarity = new CardinalitySensitiveSetSimilarity(new SimpleLabelSimilarity(new EqualStemWordListSimilarity()));
	
	private RelationCluster(){
		this.init();
	}
	public RelationCluster(Collection<EntityNode> rangeEntityNodeSet, 
			               String relationLabel,  
						   Collection<EntityNode> domainEntityNodeSet){
		this.init();
		this.addMember(rangeEntityNodeSet, relationLabel, domainEntityNodeSet);
	}
	
	private void init(){
		_relationMemeber = new LinkedHashSet<String>();
		_relationWordLists = new ArrayList<List<String>>();
		_centerRelationWordList = new ArrayList<String>();
		_rangeSet = new LinkedHashSet<String>();
		_domainSet = new LinkedHashSet<String>();
		_relationWordList2Frequency = new HashMap<String, Double>();
		_relationWordList2Significant = new HashMap<String, Double>();
		_phraseExtractor = new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor());
	}
	
	public void addMember(Collection<EntityNode> rangeEntityNodeSet, 
			              String relationLabel, 
			              Collection<EntityNode> domainEntityNodeSet){
		Set<String> fromRangeSet = new HashSet<String>();
		for(EntityNode entityNode : rangeEntityNodeSet){
			fromRangeSet.add(entityNode.getLabel());
		}
		Set<String> fromDomainSet = new HashSet<String>();
		for(EntityNode entityNode : domainEntityNodeSet){
			fromDomainSet.add(entityNode.getLabel());
		}
		
		this.addSetMember(_rangeSet, fromRangeSet);
		this.addSetMember(_domainSet, fromDomainSet);
		this.addRelationMember(relationLabel);
		this.computeClusterCenterWhenAddedNewMember();
	}
	
	public RelationCluster merge(RelationCluster cluster){
		RelationCluster mergedCluster = this.copy();
		mergedCluster._relationMemeber.addAll(cluster.getRelationMembers());
		mergedCluster._relationWordLists.addAll(cluster.getRelationWordLists());
		mergedCluster.addSetMember(mergedCluster._rangeSet, cluster.getRangeSet());
		mergedCluster.addSetMember(mergedCluster._domainSet, cluster.getDomainSet());
//		mergedCluster.ComputeNewSetWords(mergedCluster._rangeSet);
//		mergedCluster.ComputeNewSetWords(mergedCluster._domainSet);
		
		_relationWordList2Frequency.clear();
		_relationWordList2Significant.clear();
		mergedCluster.identifyCandidateRelationPatterns(_relationWordLists, _relationWordList2Frequency, _relationWordList2Significant);
		mergedCluster.IdentifyBestRelationPattern(_relationWordList2Frequency, _relationWordList2Significant, _centerRelationWordList);
		return mergedCluster;
	}
	
	public String computeRepresentativeRelationLabel(){
		System.out.println("--- Compute Representative Relation Label");
		List<String> generatedCenterRelationWordList = new ArrayList<String>();
		List<List<String>> relationWordLists = new ArrayList<List<String>>();
		Map<String, Double> relationWordList2Frequency = new HashMap<String, Double>();
		Map<String, Double> relationWordList2Significant = new HashMap<String, Double>();
		for(String relationMember : _relationMemeber){
			List<String> relationWordList = new ArrayList<String>();
			for(String token : TextProcessingUtils.tokenizeLabel2Array(relationMember, true, true, 0)){
				relationWordList.add(token);
			}
			relationWordLists.add(relationWordList);
		}
		this.identifyCandidateRelationPatterns(relationWordLists, relationWordList2Frequency, relationWordList2Significant);
		this.IdentifyBestRelationPattern(relationWordList2Frequency, relationWordList2Significant, generatedCenterRelationWordList);
		return this.convertPatternList2PatternString(generatedCenterRelationWordList);
	}
	
	private void addRelationMember(String relationLabel){
		_relationMemeber.add(relationLabel);
		_relationWordLists.add(TextProcessingUtils.processRelationLabelWithStemming(relationLabel));
	}
	
	private void addSetMember(Collection<String> toSet, Collection<String> fromSet) {
		for (String s : fromSet) {
			toSet.add(s.toLowerCase());
		}
	}
	
	private void computeClusterCenterWhenAddedNewMember(){
//		this.ComputeNewSetWords(_rangeSet);
//		this.ComputeNewSetWords(_domainSet);
		this.identifyCandidateRelationPatternsAfterAddedNewMember(_relationWordLists, _relationWordList2Frequency, _relationWordList2Significant);
		this.IdentifyBestRelationPattern(_relationWordList2Frequency, _relationWordList2Significant, _centerRelationWordList);
	}
	
	private void identifyCandidateRelationPatterns(List<List<String>> relationWordLists,
												   Map<String, Double> relationWordList2Frequency,
												   Map<String, Double> relationWordList2Significant) {
		int size = relationWordLists.size(); 
		if(size == 1){
			String patternString = this.convertPatternList2PatternString(relationWordLists.get(0));
			relationWordList2Frequency.put(patternString, 1.0);
			relationWordList2Significant.put(patternString, 1.0);
		}
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				this.computeRelationPatternStatistics(size * (size - 1) * 0.5, relationWordLists.get(i), relationWordLists.get(j),
						relationWordList2Frequency, relationWordList2Significant);
			}
		}
	}
	
	private void identifyCandidateRelationPatternsAfterAddedNewMember(List<List<String>> relationWordLists,
			 														  Map<String, Double> relationWordList2Frequency,
			  														  Map<String, Double> relationWordList2Significant) {
		int size = relationWordLists.size();
		if (size == 1) {
			String wordList = relationWordLists.get(0).toString();
//			System.out.println("Add first relation memeber: " + wordList);
			relationWordList2Frequency.put(wordList, 1.0);
			relationWordList2Significant.put(wordList, 1.0);
		} else if (size > 1) {

			List<String> lastRelationWordList = relationWordLists.get(size - 1);
//			System.out.println("Add New relation memeber: " + lastRelationWordList);
			for (int i = 0; i < size - 1; i++) {
				this.computeRelationPatternStatistics(size, relationWordLists.get(i), lastRelationWordList, relationWordList2Frequency, relationWordList2Significant);
			}
		}
		System.out.println();
	}

	private void computeRelationPatternStatistics(double totalNumberComparison,
			                                      List<String> relationWordList1, 
												  List<String> relationWordList2,
												  Map<String, Double> relationWordList2Frequency,
												  Map<String, Double> relationWordList2Significant) {
		
		System.out.println("-- Computer Relation Pattern : " + relationWordList1.toString() + " <-> " +  relationWordList2.toString());

		List<String> pattern = this.identifyPattern(relationWordList1, relationWordList2);
		String patternString = pattern.toString();
		int sizeOfPattern = pattern.size();
		int sizeOfList1 = relationWordList1.size();
		int sizeOfList2 = relationWordList2.size();
		double new_significant = ((double) sizeOfPattern / (double) sizeOfList1 + (double) sizeOfPattern / (double) sizeOfList2) * 0.5;

		System.out.println("Pattern: " + patternString);
		
		if (relationWordList2Frequency.containsKey(patternString)) {
			double frequency = relationWordList2Frequency.get(patternString) * totalNumberComparison + 1;
			System.out.println("Pattern Frequency: " + frequency / totalNumberComparison);
			relationWordList2Frequency.put(patternString, frequency / totalNumberComparison);
		} else {
			System.out.println("Pattern Frequency: " + 1.0);
			relationWordList2Frequency.put(patternString, 1.0 / totalNumberComparison);
		}

		if (relationWordList2Significant.containsKey(patternString)) {
			double old_significant = relationWordList2Significant.get(patternString);
//			System.out.println("Pattern Significant: " + (old_significant + new_significant) * 0.5);
			relationWordList2Significant.put(patternString, (old_significant + new_significant) * 0.5);
		} else {
//			System.out.println("Pattern Significant: " + new_significant);
			relationWordList2Significant.put(patternString, new_significant);
		}
	}
	
	private List<String> identifyPattern(List<String> list1, List<String> list2) {
//		System.out.println("Compute Pattern between: " + list1 + " <-> " + list2);
		int start_pointer = 0;
		List<String> newOrderedRelationWordList = new ArrayList<String>();
		for (String w : list1) {
			for (int i = start_pointer; i < list2.size(); i++) {
				if (w.equals(list2.get(i))) {
					newOrderedRelationWordList.add(w);
					start_pointer = i + 1;
					break;
				}
			}
		}
		return newOrderedRelationWordList;
	}
	
	public List<String> IdentifyBestRelationPattern(Map<String, Double> relationWordList2Frequency,
													Map<String, Double> relationWordList2Significant, 
													List<String> generatedCenterRelationWordList){
		double maxScore = 0.0;
		String bestPatternString = "";
		for(String patternString : relationWordList2Frequency.keySet()){
			double frequency = relationWordList2Frequency.get(patternString);
			double significant = relationWordList2Significant.get(patternString);
			double score = (5 * frequency * significant) / (4 * frequency + significant);
			if (score > maxScore) {
				maxScore = score;
				bestPatternString = patternString;
			}
			System.out.println("Pattern String: " + patternString + ", " + frequency + ", " + significant);
		}
		generatedCenterRelationWordList.clear();
		generatedCenterRelationWordList.addAll(convertPatternString2PatternList(bestPatternString));
		return generatedCenterRelationWordList;
	}
	
	private List<String> convertPatternString2PatternList(String bestPatternString) {
		String[] words = bestPatternString.replace("[", "").replace("]", "").split(", ");
		return Arrays.asList(words); 
	}

	private String convertPatternList2PatternString(List<String> patternList) {
		StringBuilder patternString = new StringBuilder();
		for (String word : patternList) {
			patternString.append(word + " ");
		}
		return patternString.toString().trim();
	}

	public void ComputeNewSetWords(Collection<String> set){
		set.addAll(_phraseExtractor.computeCommonPhrasesInString(set));
	}
	
	public double computeCloseness(RelationCluster cluster){
		return computeBelongingness(cluster.getRangeSet(), cluster.getCenterRelationWordList(), cluster.getDomainSet());
	}
	
	public double computeBelongingness(Collection<EntityNode> rangeEntityNodeSet,
			                           String relationLabel,
			                           Collection<EntityNode> domainEntityNodeSet){
		List<String> relationWordList = TextProcessingUtils.processRelationLabelWithStemming(relationLabel);
		Set<String> rangeSet = new HashSet<String>();
		for (EntityNode entityNode : rangeEntityNodeSet) {
			rangeSet.add(entityNode.getLabel());
		}
		
		Set<String> domainSet = new HashSet<String>();
		for (EntityNode entityNode : domainEntityNodeSet) {
			domainSet.add(entityNode.getLabel());
		}
		
		return computeBelongingness(rangeSet, relationWordList, domainSet);
	}
	
	private double computeBelongingness(Set<String> rangeEntityNodeSet,
									    List<String> relationWordList,
									    Set<String> domainEntityNodeSet) {
		
		System.out.println("Compare Relation : " + relationWordList);
		System.out.println("                   " + _centerRelationWordList);
		
		double relationMatchScore = wordListSimilarity.computeSimilarity(relationWordList, _centerRelationWordList);
//		double relationMatchScore = similarityAlg.computeWordListSimilarityByOrderedWordPattern(relationWordList, _centerRelationWordList);
		if (relationMatchScore < 0.3) {
			return relationMatchScore;
		}
		
		System.out.println("Compare Range : " + rangeEntityNodeSet);
		System.out.println("                " + _rangeSet);
		System.out.println("Compare Domain: " + domainEntityNodeSet);
		System.out.println("                " + _domainSet);
		double rangeSetsSimilarity = setSimilarity.computeSimilarity(rangeEntityNodeSet, _rangeSet);
		double domainSetsSimilarity = setSimilarity.computeSimilarity(domainEntityNodeSet, _domainSet);
//		double rangeSetsSimilarity = similarityAlg.computeSetsSimilarityByEqualitySemanticRoot(rangeEntityNodeSet, _rangeSet);
//		double domainSetsSimilarity = similarityAlg.computeSetsSimilarityByEqualitySemanticRoot(domainEntityNodeSet, _domainSet); 
		                                   
		double combinedScore = 0.5 * relationMatchScore +  0.4 * rangeSetsSimilarity +  0.1 * domainSetsSimilarity;
		System.out.println("Center Relation Pattern: " + _centerRelationWordList + ":" + combinedScore + ", " + relationMatchScore + ", " + rangeSetsSimilarity + ", " + domainSetsSimilarity);
		return combinedScore;
	}
	
	public Collection<String> getRelationMembers(){
		return this._relationMemeber;
	}
	
	private List<List<String>> getRelationWordLists(){
		return this._relationWordLists;
	}
	
	public String getCenterRelationLabel(){
		return this.convertPatternList2PatternString(this._centerRelationWordList);
	}
	
	public List<String> getCenterRelationWordList(){
		return this._centerRelationWordList;
	}
	
	public Set<String> getRangeSet(){
		return this._rangeSet;
	}
	
	public Set<String> getDomainSet(){
		return this._domainSet;
	}
	
	private Map<String, Double> getRelationWordList2Frequency(){
		return this._relationWordList2Frequency;
	}
	
	private Map<String, Double> getRelationWordList2Significant(){
		return this._relationWordList2Significant;
	}
	
	public RelationCluster copy(){
		RelationCluster cluster = new RelationCluster();
		cluster._relationMemeber.addAll(this.getRelationMembers());
		cluster._centerRelationWordList.addAll(this.getCenterRelationWordList());
		cluster._rangeSet.addAll(this.getRangeSet());
		cluster._domainSet.addAll(this.getDomainSet());
		cluster._relationWordList2Frequency.putAll(this.getRelationWordList2Frequency());
		cluster._relationWordList2Significant.putAll(this.getRelationWordList2Significant());
		for(List<String> wordList : this.getRelationWordLists()){
			List<String> newWordList = new ArrayList<String>();
			newWordList.addAll(wordList);
			cluster._relationWordLists.add(newWordList);
		}
		return cluster;
	}
	
	@Override
	public String toString() {
		return this._centerRelationWordList  + "@" + this._rangeSet + "@" + this._domainSet + "@" + this._relationMemeber;
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof RelationCluster))
			return false;

		RelationCluster cluster = (RelationCluster) o;
		return this.toString().equals(cluster.toString());
	}

	public void showDetail(){
		System.out.println();
		for(String patternString : _relationWordList2Frequency.keySet()){
			double frequency = _relationWordList2Frequency.get(patternString);
			double significant = _relationWordList2Significant.get(patternString);
			System.out.println(patternString + ", " + frequency + ", " + significant);
		}
		
		for(String relationStr : _relationMemeber){
			System.out.println("Member: " + relationStr);
		}
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException {
		Set<EntityNode> set1 = new HashSet<EntityNode>();
		RelationCluster cluster = new RelationCluster(set1, "We provide a full range of materials to meet your application requirements, including", set1);
		
		String relation1 = "AccuTrex offers a full range of materials for the gaskets we manufacture. The materials available include";
		String relation2 = "Custom shim materials include";
		String relation3 = "Our adjustable shims materials include:";
		String relation4 = "include shims materials:";
//		cluster.computeNewOrderedRelationWordList("AccuTrex offers a full range of materials for the gaskets we manufacture. The materials available include");
//		cluster.computeNewOrderedRelationWordList("Custom shim materials include");
//		cluster.computeNewOrderedRelationWordList("Our adjustable shims materials include:");
		
		cluster.addMember(set1, relation1, set1);
		cluster.addMember(set1, relation2, set1);
		cluster.addMember(set1, relation3, set1);
		
		
		cluster.showDetail();
		System.out.println("best pattern: " + cluster.computeRepresentativeRelationLabel());
		
		System.out.println("score1: " + cluster.computeBelongingness(new HashSet<EntityNode>(), relation1, new HashSet<EntityNode>()));
		System.out.println("score2: " + cluster.computeBelongingness(new HashSet<EntityNode>(), relation2, new HashSet<EntityNode>()));
		System.out.println("score3: " + cluster.computeBelongingness(new HashSet<EntityNode>(), relation4, new HashSet<EntityNode>()));
		
		Set<String> set2 = new HashSet<String>();
		set2.add("Copper".toLowerCase());
		set2.add("Brass".toLowerCase());
		set2.add("Rubber".toLowerCase());
		set2.add("Phenolics".toLowerCase());
		set2.add("Nylon".toLowerCase());
		set2.add("Plastics".toLowerCase());
		set2.add("Carbon steel".toLowerCase());
		set2.add("Stainless steel".toLowerCase());
		set2.add("Spring steel".toLowerCase());
		set2.add("Titanium".toLowerCase());
		Set<String> set3 = new HashSet<String>();
		set3.add("Aluminum".toLowerCase());
		set3.add("Brass".toLowerCase());
		set3.add("Carbon steel".toLowerCase());
		set3.add("Nickel".toLowerCase());
		set3.add("Inconel".toLowerCase());
		set3.add("Hastelloy".toLowerCase());
		set3.add("Monel".toLowerCase());
		set3.add("Spring steel".toLowerCase());
//		System.out.println("set score: " + cluster.computeWordListSimilarityBySSR(set2, set3));
//		cluster.ComputeNewSetWords(set2);
	}
}
