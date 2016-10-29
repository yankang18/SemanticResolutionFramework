package umbc.ebiquity.kang.ontologyinitializator.mappingframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.EntityNode;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.textprocessing.impl.CombinationInOrderPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.interfaces.IPhraseExtractor;

public class LongestCommonPhraseAnalyzer {

	private Map<String, Set<String>> phraseProvenanceMap;
	private IPhraseExtractor phraseExtractor = new CombinationInOrderPhraseExtractor();

	public LongestCommonPhraseAnalyzer() {
		this.phraseProvenanceMap = new HashMap<String, Set<String>>();
	}

	public Map<String, Double> computeCommonPhrases(Collection<EntityNode> entitySet) {

		Set<String> allPhrases = new HashSet<String>();
		Map<String, Double> commonePhraseMap = new HashMap<String, Double>();
		Map<String, Double> oneWordsPhrasesMap = new LinkedHashMap<String, Double>();
		Map<String, Double> twoWordsPhrasesMap = new LinkedHashMap<String, Double>();
		Map<String, Double> threeWordsPhrasesMap = new LinkedHashMap<String, Double>();
		Map<String, Double> fourWordsPhrasesMap = new LinkedHashMap<String, Double>();
		Map<String, Double> FiveWordsPhrasesMap = new LinkedHashMap<String, Double>();
		int numOfEntities = entitySet.size();
		double threshold = ((double) numOfEntities * 0.5) < 1 ? 1 : (double) numOfEntities * 0.5;
		double maxScore = 0.0;
		for (EntityNode entity : entitySet) {
			String EntityLabel = entity.getLabel();
//			allPhrases.add(EntityLabel);
			String processedLabel = TextProcessingUtils.removeStopwords2(entity.getLabel());
			if (processedLabel.equals(""))
				continue;
			String[] tokens = TextProcessingUtils.tokenizeLabel(processedLabel.trim());
			
			double score = 1.0;
			for (String oneWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 1)) {
//				 System.out.println("one word phrase: " + oneWordPhrase);
				double tempScore = score;
				if (oneWordsPhrasesMap.containsKey(oneWordPhrase)) {
					tempScore = oneWordsPhrasesMap.get(oneWordPhrase) + score;
				}
				oneWordsPhrasesMap.put(oneWordPhrase, tempScore);
				if(tempScore > maxScore){
					maxScore = tempScore;
				}

				Set<String> originalTermsSet = null;
				if (phraseProvenanceMap.containsKey(oneWordPhrase)) {
					originalTermsSet = phraseProvenanceMap.get(oneWordPhrase);
					originalTermsSet.add(entity.getLabel());
				} else {
					originalTermsSet = new LinkedHashSet<String>();
					originalTermsSet.add(entity.getLabel());
				}
				phraseProvenanceMap.put(oneWordPhrase, originalTermsSet);
			}

			for (String twoWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 2)) {
//				 System.out.println("two word phrase: " + twoWordPhrase);
				double tempScore = score;
				if (twoWordsPhrasesMap.containsKey(twoWordPhrase)) {
					tempScore = twoWordsPhrasesMap.get(twoWordPhrase) + score;
				}
				twoWordsPhrasesMap.put(twoWordPhrase, tempScore);
				if(tempScore > maxScore){
					maxScore = tempScore;
				}

				Set<String> originalTermsSet = null;
				if (phraseProvenanceMap.containsKey(twoWordPhrase)) {
					originalTermsSet = phraseProvenanceMap.get(twoWordPhrase);
					originalTermsSet.add(entity.getLabel());
				} else {
					originalTermsSet = new LinkedHashSet<String>();
					originalTermsSet.add(entity.getLabel());
				}
				phraseProvenanceMap.put(twoWordPhrase, originalTermsSet);
			}

			for (String threeWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 3)) {
//				 System.out.println("three word phrase: " + threeWordPhrase);
				double tempScore = score;
				if (threeWordsPhrasesMap.containsKey(threeWordPhrase)) {
					tempScore = threeWordsPhrasesMap.get(threeWordPhrase) + score;
				}
				threeWordsPhrasesMap.put(threeWordPhrase, tempScore);
				if(tempScore > maxScore){
					maxScore = tempScore;
				}

				Set<String> originalTermsSet = null;
				if (phraseProvenanceMap.containsKey(threeWordPhrase)) {
					originalTermsSet = phraseProvenanceMap.get(threeWordPhrase);
					originalTermsSet.add(entity.getLabel());
				} else {
					originalTermsSet = new LinkedHashSet<String>();
					originalTermsSet.add(entity.getLabel());
				}
				phraseProvenanceMap.put(threeWordPhrase, originalTermsSet);
			}

			for (String fourWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 4)) {
//				 System.out.println("four word phrase: " + fourWordPhrase);
				double tempScore = score;
				if (fourWordsPhrasesMap.containsKey(fourWordPhrase)) {
					tempScore = fourWordsPhrasesMap.get(fourWordPhrase) + score;
				}
				fourWordsPhrasesMap.put(fourWordPhrase, tempScore);
				if(tempScore > maxScore){
					maxScore = tempScore;
				}

				Set<String> originalTermsSet = null;
				if (phraseProvenanceMap.containsKey(fourWordPhrase)) {
					originalTermsSet = phraseProvenanceMap.get(fourWordPhrase);
					originalTermsSet.add(entity.getLabel());
				} else {
					originalTermsSet = new LinkedHashSet<String>();
					originalTermsSet.add(entity.getLabel());
				}
				phraseProvenanceMap.put(fourWordPhrase, originalTermsSet);
			}
			
			for (String fiveWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 5)) {
//				 System.out.println("five word phrase: " + fiveWordPhrase);
				double tempScore = score;
				if (FiveWordsPhrasesMap.containsKey(fiveWordPhrase)) {
					tempScore = FiveWordsPhrasesMap.get(fiveWordPhrase) + score;
				}
				FiveWordsPhrasesMap.put(fiveWordPhrase, tempScore);
				if(tempScore > maxScore){
					maxScore = tempScore;
				}

				Set<String> originalTermsSet = null;
				if (phraseProvenanceMap.containsKey(fiveWordPhrase)) {
					originalTermsSet = phraseProvenanceMap.get(fiveWordPhrase);
					originalTermsSet.add(entity.getLabel());
				} else {
					originalTermsSet = new LinkedHashSet<String>();
					originalTermsSet.add(entity.getLabel());
				}
				phraseProvenanceMap.put(fiveWordPhrase, originalTermsSet);
			}
		}
		
		/*
		 * 
		 */
		
		boolean hasNoPhrases = true;
		for (String fiveWordPhrase : FiveWordsPhrasesMap.keySet()) {
			double count = FiveWordsPhrasesMap.get(fiveWordPhrase);
			if (count == maxScore) {
				commonePhraseMap.put(fiveWordPhrase, count);
				hasNoPhrases = false;
			}
		}

		if (hasNoPhrases) {
			for (String fourWordPhrase : fourWordsPhrasesMap.keySet()) {
				double count = fourWordsPhrasesMap.get(fourWordPhrase);
				if (count == maxScore) {
					commonePhraseMap.put(fourWordPhrase, count);
					hasNoPhrases = false;
				}
			}
		}

		if (hasNoPhrases) {
			for (String threeWordPhrase : threeWordsPhrasesMap.keySet()) {
				double count = threeWordsPhrasesMap.get(threeWordPhrase);
				if (count == maxScore) {
					commonePhraseMap.put(threeWordPhrase, count);
					hasNoPhrases = false;
				}
			}
		}

		if (hasNoPhrases) {
			for (String twoWordPhrase : twoWordsPhrasesMap.keySet()) {
				double count = twoWordsPhrasesMap.get(twoWordPhrase);
				if (count == maxScore) {
					commonePhraseMap.put(twoWordPhrase, count);
					hasNoPhrases = false;
				}
			}
		}

		if (hasNoPhrases) {
			for (String oneWordPhrase : oneWordsPhrasesMap.keySet()) {
				double count = oneWordsPhrasesMap.get(oneWordPhrase);
				if (count == maxScore) {
					commonePhraseMap.put(oneWordPhrase, count);
					hasNoPhrases = false;
				}
			}
		}

//		allPhrases.addAll(commonePhraseSet);
//		if (commonePhraseSet.size() == 0) {
//			for (EntityNode entity : entitySet) {
//
//				String[] tokens = entity.getLabel().split("/");
//				for (String token : tokens) {
//					String processedLabel = TextProcessingUtils.removeStopwords2(token.toLowerCase());
//					if(processedLabel.equals("")) continue;
//					commonePhraseSet.add(processedLabel);
//				}
//			}
//		}
		return commonePhraseMap;
	}
	

	public static void main(String[] args) throws IOException {

		LongestCommonPhraseAnalyzer pa = new LongestCommonPhraseAnalyzer();
		EntityNode entityNode1 = new EntityNode("tile stone waterjet cutting");
		EntityNode entityNode2 = new EntityNode("abrasive waterjet cutting application");
		EntityNode entityNode3 = new EntityNode("stone waterjet cutting");
		EntityNode entityNode4 = new EntityNode("architectural waterjet cutting");
		EntityNode entityNode5 = new EntityNode("waterjet cutting benefits");
		List<EntityNode> entityNodes = new ArrayList<EntityNode>();
		entityNodes.add(entityNode1);
		entityNodes.add(entityNode2);
		entityNodes.add(entityNode3);
		entityNodes.add(entityNode4);
		entityNodes.add(entityNode5);
		Map<String, Double> phraseMap = pa.computeCommonPhrases(entityNodes);
		for(String phrase : phraseMap.keySet()){
			System.out.println(phrase + ", " + phraseMap.get(phrase));
		}

	}

}
