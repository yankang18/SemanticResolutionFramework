package umbc.ebiquity.kang.ontologyinitializator.mappingframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.textprocessing.phraseextractor.IPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.phraseextractor.impl.CombinationInOrderPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class TermCollectionAnalyzer {

	/***
	 * 
	 */
	private Map<String, Double> termScorePairMap;

	/***
	 * 
	 */
	private Map<String, Double> representativeTermsScoreMap;

	/***
	 * 
	 */
	private Map<String, Double> inferredTermScorePairMap;
	
	/***
	 * 
	 */
	private Map<String, Set<String>> phraseProvenanceMap;
	
	private IPhraseExtractor phraseExtractor = new CombinationInOrderPhraseExtractor();
	
	private double maxScore = 0.0;
	private double average = 0.0;
	private double sum = 0.0;
	private double median = 0.0;
	
	public TermCollectionAnalyzer(Map<String, Double> termScoreMap) {
		this(termScoreMap, null);
	}

	public TermCollectionAnalyzer(Map<String, Double> termScoreMap, IPhraseExtractor phraseExtractor) {
		this.termScorePairMap = termScoreMap;
		this.inferredTermScorePairMap = new LinkedHashMap<String, Double>();
		this.phraseProvenanceMap = new LinkedHashMap<String, Set<String>>();
		this.representativeTermsScoreMap = new LinkedHashMap<String, Double>();
		if (phraseExtractor != null) {
			this.phraseExtractor = phraseExtractor;
		}
	}

	public void anaylzing(boolean inferring) {
		this.computeBasicStatistics();
		this.computeRepresentativeTerms();
		if (inferring) {
			this.computeInferredTerms();
		}
	}
	
	private void computeBasicStatistics() {
		
		List<Double> values = new ArrayList<Double>();
		int numOfTerm = termScorePairMap.size();
		for (String termLabel : termScorePairMap.keySet()) {
			double score = termScorePairMap.get(termLabel);
			values.add(score);
			this.sum += score;
			if (score > this.maxScore) {
				this.maxScore = score;
			}
		}
		this.average = this.sum / numOfTerm;
		this.median  = this.Median(values);
	}
	
	public double Median(List<Double> values) {
		Collections.sort(values);

		if (values.size() % 2 == 1)
			return values.get((values.size() + 1) / 2 - 1);
		else {
			double lower = values.get(values.size() / 2 - 1);
			double upper = values.get(values.size() / 2);

			return (lower + upper) / 2.0;
		}
	}

	private void computeRepresentativeTerms() {
		for (String termLabel : termScorePairMap.keySet()) {
			double score = termScorePairMap.get(termLabel);
			if (score >= this.median) {
				System.out.println("Here3 representative: " + termLabel);
				representativeTermsScoreMap.put(termLabel, score);
			}
		}
	}

	private void computeInferredTerms() {

		if (representativeTermsScoreMap.size() > 1) {
			Map<String, Double> oneWordsPhrasesMap = new LinkedHashMap<String, Double>();
			Map<String, Double> twoWordsPhrasesMap = new LinkedHashMap<String, Double>();
			Map<String, Double> threeWordsPhrasesMap = new LinkedHashMap<String, Double>();
			Map<String, Double> fourWordsPhrasesMap = new LinkedHashMap<String, Double>();
			int totalNumOfTokens = 0;
//			for (String termLabel : representativeTermsScoreMap.keySet()) {
//				double score = representativeTermsScoreMap.get(termLabel);
			for (String termLabel : termScorePairMap.keySet()) {
				double score = termScorePairMap.get(termLabel);
				String[] tokens = TextProcessingUtils.tokenizeLabel(TextProcessingUtils.removeStopwords(termLabel.toLowerCase()));
				totalNumOfTokens += tokens.length;
				
				for (String oneWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 1)) {
					// System.out.println("*** " + oneWordPhrase + " " + score);
					double tempScore = score;
					if (oneWordsPhrasesMap.containsKey(oneWordPhrase)) {
						tempScore = oneWordsPhrasesMap.get(oneWordPhrase) + score;
					}
					oneWordsPhrasesMap.put(oneWordPhrase, tempScore);

					Set<String> originalTermsSet = null;
					if (phraseProvenanceMap.containsKey(oneWordPhrase)) {
						originalTermsSet = phraseProvenanceMap.get(oneWordPhrase);
						originalTermsSet.add(termLabel);
					} else {
						originalTermsSet = new LinkedHashSet<String>();
						originalTermsSet.add(termLabel);
					}
					phraseProvenanceMap.put(oneWordPhrase, originalTermsSet);
				}

				for (String twoWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 2)) {
					// System.out.println("*** " + twoWordPhrase);
					double tempScore = score;
					if (twoWordsPhrasesMap.containsKey(twoWordPhrase)) {
						tempScore = twoWordsPhrasesMap.get(twoWordPhrase) + score;
					}
					twoWordsPhrasesMap.put(twoWordPhrase, tempScore);

					Set<String> originalTermsSet = null;
					if (phraseProvenanceMap.containsKey(twoWordPhrase)) {
						originalTermsSet = phraseProvenanceMap.get(twoWordPhrase);
						originalTermsSet.add(termLabel);
					} else {
						originalTermsSet = new LinkedHashSet<String>();
						originalTermsSet.add(termLabel);
					}
					phraseProvenanceMap.put(twoWordPhrase, originalTermsSet);
				}

				for (String threeWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 3)) {
					// System.out.println("*** " + threeWordPhrase);
					double tempScore = score;
					if (threeWordsPhrasesMap.containsKey(threeWordPhrase)) {
						tempScore = threeWordsPhrasesMap.get(threeWordPhrase) + score;
					}
					threeWordsPhrasesMap.put(threeWordPhrase, tempScore);

					Set<String> originalTermsSet = null;
					if (phraseProvenanceMap.containsKey(threeWordPhrase)) {
						originalTermsSet = phraseProvenanceMap.get(threeWordPhrase);
						originalTermsSet.add(termLabel);
					} else {
						originalTermsSet = new LinkedHashSet<String>();
						originalTermsSet.add(termLabel);
					}
					phraseProvenanceMap.put(threeWordPhrase, originalTermsSet);
				}

				for (String fourWordPhrase : phraseExtractor.extractPhraseStrings(tokens, 4)) {
					// System.out.println("*** " + fourWordPhrase);
					double tempScore = score;
					if (fourWordsPhrasesMap.containsKey(fourWordPhrase)) {
						tempScore = fourWordsPhrasesMap.get(fourWordPhrase) + score;
					}
					fourWordsPhrasesMap.put(fourWordPhrase, tempScore);

					Set<String> originalTermsSet = null;
					if (phraseProvenanceMap.containsKey(fourWordPhrase)) {
						originalTermsSet = phraseProvenanceMap.get(fourWordPhrase);
						originalTermsSet.add(termLabel);
					} else {
						originalTermsSet = new LinkedHashSet<String>();
						originalTermsSet.add(termLabel);
					}
					phraseProvenanceMap.put(fourWordPhrase, originalTermsSet);
				}
			}
//			double aveLenOfRepLbl = (double) totalNumOfTokens / (double) representativeTermsScoreMap.size();
			double aveLenOfRepLbl = (double) totalNumOfTokens / (double) termScorePairMap.size();
			for (String fourWordPhrase : fourWordsPhrasesMap.keySet()) {
				double score = fourWordsPhrasesMap.get(fourWordPhrase);
				if (score > this.maxScore) {
					inferredTermScorePairMap.put(fourWordPhrase, score * 4 / aveLenOfRepLbl);
				}
			}

			for (String threeWordPhrase : threeWordsPhrasesMap.keySet()) {
				double score = threeWordsPhrasesMap.get(threeWordPhrase);
				if (score > this.maxScore) {
					inferredTermScorePairMap.put(threeWordPhrase, score * 3 / aveLenOfRepLbl);
				}
			}

			for (String twoWordPhrase : twoWordsPhrasesMap.keySet()) {
				double score = twoWordsPhrasesMap.get(twoWordPhrase);
				if (score > this.maxScore) {
					inferredTermScorePairMap.put(twoWordPhrase, score * 2 / aveLenOfRepLbl);
				}
			}

			for (String oneWordPhrase : oneWordsPhrasesMap.keySet()) {
				double score = oneWordsPhrasesMap.get(oneWordPhrase);
				if (score > this.maxScore) {
					inferredTermScorePairMap.put(oneWordPhrase, score * 1 / aveLenOfRepLbl);
				}
			}
		}
	}
	
	public Map<String, Double> getRepresentativeTerms(){
		return this.representativeTermsScoreMap;
	}
	
	public Map<String, Double> getInferredTerms(){
		return this.inferredTermScorePairMap;
	}
	
	public Map<String, Set<String>> getPhraseProvenanceMap(){
		return this.phraseProvenanceMap;
	}

}
