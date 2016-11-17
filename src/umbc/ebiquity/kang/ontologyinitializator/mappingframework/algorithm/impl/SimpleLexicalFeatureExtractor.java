package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature.Position;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SubString;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ILexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.repository.test.POSTest;
import umbc.ebiquity.kang.textprocessing.phraseextractor.IPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.phraseextractor.impl.SequenceInReversedOrderPhraseExtractor;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class SimpleLexicalFeatureExtractor implements ILexicalFeatureExtractor {

//	public static void main(String[] args) throws IOException {
//
//		Collection<String> wordSets = new ArrayList<String>();
//		wordSets.add("laser cutting");
//		wordSets.add("laser cutting");
//		wordSets.add("waterject laser cutting");
//		SimpleLexicalFeatureExtractor phraseAnalyzer = new SimpleLexicalFeatureExtractor(new SequenceInReversedOrderPhraseExtractor());
//		for (Phrase phrase : phraseAnalyzer.extractCommonPhrases(wordSets)) {
//			System.out.println(phrase.getLabel() + ", " + phrase.getSignificant() + ", " + phrase.getSupport());
//		}
//		System.out.println();
//		Map<String, String> subStrings = POSTest.findCommonSubStrings(new ArrayList<String>(wordSets));
//		for (String subString : subStrings.keySet()) {
//			System.out.println(subString + ", " + subStrings.get(subString));
//		}
//	}

	private IPhraseExtractor phraseExtractor;
	private boolean _removeStopwords; 

	public SimpleLexicalFeatureExtractor(IPhraseExtractor phraseExtractor) {
		this.phraseExtractor = phraseExtractor;
		this._removeStopwords = false;
	}
	
	@Override
	public String[] normalizeLabelToArray(String label){
		String processedLabel = label.trim();
		if (_removeStopwords) {
			processedLabel = TextProcessingUtils.removeStopwords2(label);
		}
		return TextProcessingUtils.tokenizeLabel(processedLabel);
	}

	// TODO: should move this method to other class for applying Single
	// Responsibility Principle
	@Override
	public String normalizeLabelToString(String label){
		StringBuilder sb = new StringBuilder();
		for(String token : this.normalizeLabelToArray(label)){
			sb.append(token + " ");
		}
		return sb.toString().trim().toLowerCase();
	}

	@Override
	public Collection<Phrase> extractCommonPhrases(Collection<String> wordSets) {
		Map<String, Phrase> phraseCollection = new HashMap<String, Phrase>();
		Map<String, Integer> phraseCounter = new HashMap<String, Integer>();

		int setNumber = wordSets.size();
		for (String wordSet : wordSets) {
			String[] tokens = normalizeLabelToArray(wordSet);
			Collection<Phrase> phrases = phraseExtractor.extractPhrases(tokens, tokens.length);
			for (Phrase p : phrases) {
				if(p.getLabel().toCharArray().length < 3){
					continue;
				}
				// System.out.println("@ " + p.getLabel() + ", " +
				// p.getSignificant() + ", " + p.getSupport());
				if (phraseCollection.containsKey(p.getLabel())) {
					Phrase phrase = phraseCollection.get(p.getLabel());
					int count = phraseCounter.get(p.getLabel());
					count++;
					phraseCounter.put(p.getLabel(), count);
					phrase.setCount(count);
					phrase.setSupport((double) count / (double) setNumber);
					phrase.setSignificant((phrase.getSignificant() + p.getSignificant()) / 2);
				} else {
					p.setCount(1);
					p.setSupport(1.0 / (double) setNumber);
					phraseCollection.put(p.getLabel(), p);
					phraseCounter.put(p.getLabel(), 1);
				}
			}
		}
		return phraseCollection.values();
	}

	@Override
	public Collection<String> computeCommonPhrasesInString(Collection<String> wordSets) {
		Collection<String> phraseCollection = new HashSet<String>();

		for (String wordSet : wordSets) {
			String processedLabel = TextProcessingUtils.removeStopwords2(wordSet);
			if (processedLabel.equals(""))
				continue;
			String[] tokens = TextProcessingUtils.tokenizeLabel(processedLabel.trim());
			Collection<Phrase> phrases = phraseExtractor.extractPhrases(tokens, tokens.length);
			for (Phrase p : phrases) {
				if (!phraseCollection.contains(p.getLabel())) {
					phraseCollection.add(p.getLabel());
				}

			}
		}
		return phraseCollection;
	}

	@Override
	public Collection<SubString> extractCommonSubStrings(Collection<String> LabelCollections) {
		List<String> LabelList = new ArrayList<String>(LabelCollections);

		int charThreshold = 4;
		int subStringFrequency = 3;
		
		Map<String, Integer> substring2CountMap = new HashMap<String, Integer>();
		Map<String, Double> subString2ConfidenceMap = new HashMap<String, Double>();
		Map<String, Set<String>> subString2SourcesMap = new HashMap<String, Set<String>>();

		int entityStringListSize = LabelList.size();
		for (int i = 0; i < entityStringListSize; i++) {
			char[] entityStringCharArray1 = this.normalizeLabelToString(LabelList.get(i)).toCharArray();

			for (int j = i + 1; j < entityStringListSize; j++) {
				// System.out.println(entityStringList.get(i)+ " <-> " +
				// entityStringList.get(j));
				char[] entityStringCharArray2 = LabelList.get(j).toCharArray();
				int lengthOfArray1 = entityStringCharArray1.length;
				int lengthOfArray2 = entityStringCharArray2.length;
				int length = lengthOfArray1 < lengthOfArray2 ? lengthOfArray1 : lengthOfArray2;

				int charCount = 0;
				for (int z = 0; z < length; z++) {
					if (entityStringCharArray1[z] == entityStringCharArray2[z] && entityStringCharArray1[z] != ' ') {
						charCount++;
					} else {
						break;
					}
				}

				// TODO: refactor this !!!
				if (charCount >= charThreshold) {
					String subStringB = String.valueOf(entityStringCharArray1, 0, charCount) + "@";
					// System.out.println("-subStringB:" + subStringB + " " +
					// entityStringList.get(i) + ", " +
					// entityStringList.get(j));
					if (!substring2CountMap.containsKey(subStringB)) {
						/*
						 * 
						 */
						double confidence1 = (double) charCount / (double) lengthOfArray1;
						double confidence2 = (double) charCount / (double) lengthOfArray2;
						double avg = (confidence1 + confidence2) / 2;
						substring2CountMap.put(subStringB, 2);
						subString2ConfidenceMap.put(subStringB, avg);

						Set<String> sources = new HashSet<String>();
						sources.add(LabelList.get(i));
						sources.add(LabelList.get(j));
						subString2SourcesMap.put(subStringB, sources);
					} else {
						Set<String> sources = subString2SourcesMap.get(subStringB);
						int repetition = substring2CountMap.get(subStringB);
						double confidence = subString2ConfidenceMap.get(subStringB);
						if (!sources.contains(LabelList.get(i)) && !sources.contains(LabelList.get(j))) {
							int updatedRepetition = repetition + 2;
							double confidence1 = (double) charCount / (double) lengthOfArray1;
							double confidence2 = (double) charCount / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence1 + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(LabelList.get(i));
							sources.add(LabelList.get(j));
						} else if (!sources.contains(LabelList.get(i))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) charCount / (double) lengthOfArray1;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(LabelList.get(i));
						} else if (!sources.contains(LabelList.get(j))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) charCount / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(LabelList.get(j));
						}
					}
				}

				charCount = 0;
				for (int x = lengthOfArray1 - 1, y = lengthOfArray2 - 1; x >= 0 && y >= 0; x--, y--) {
					if (entityStringCharArray1[x] == entityStringCharArray2[y] && entityStringCharArray1[x] != ' ') {
						// System.out.println("## " +
						// entityStringCharArray1[x]);
						charCount++;
					} else {
						break;
					}
				}

				if (charCount >= charThreshold) {
					String subStringE = "@" + String.valueOf(entityStringCharArray1, entityStringCharArray1.length - charCount, charCount);
					// System.out.println("subStringE:" + subStringE + ", " +
					// count);
					if (substring2CountMap.get(subStringE) == null) {
						double confidence1 = (double) charCount / (double) lengthOfArray1;
						double confidence2 = (double) charCount / (double) lengthOfArray2;
						double avg = (confidence1 + confidence2) / 2;
						substring2CountMap.put(subStringE, 2);
						subString2ConfidenceMap.put(subStringE, avg);

						Set<String> sources = new HashSet<String>();
						sources.add(LabelList.get(i));
						sources.add(LabelList.get(j));
						subString2SourcesMap.put(subStringE, sources);
					} else {

						Set<String> sources = subString2SourcesMap.get(subStringE);
						int repetition = substring2CountMap.get(subStringE);
						double confidence = subString2ConfidenceMap.get(subStringE);
						if (!sources.contains(LabelList.get(i)) && !sources.contains(LabelList.get(j))) {
							int updatedRepetition = repetition + 2;
							double confidence1 = (double) charCount / (double) lengthOfArray1;
							double confidence2 = (double) charCount / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence1 + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(LabelList.get(i));
							sources.add(LabelList.get(j));
						} else if (!sources.contains(LabelList.get(i))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) charCount / (double) lengthOfArray1;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(LabelList.get(i));
						} else if (!sources.contains(LabelList.get(j))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) charCount / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(LabelList.get(j));
						}
					}
				}
			}
		}

		Collection<SubString> subStrings = new ArrayList<SubString>();
		Map<String, String> subString2StatisticsMap = new HashMap<String, String>();
		for (String subString : substring2CountMap.keySet()) {
			int count = substring2CountMap.get(subString);
			if (count < subStringFrequency)
				continue;
			double confidence = subString2ConfidenceMap.get(subString);
			double support = (double) count / (double) entityStringListSize;
			String statistics = count + "/" + confidence + "/" + support;

			SubString sub;
			if (subString.startsWith("@")) {
				sub = new SubString(subString.substring(1), Position.END);
			} else if (subString.endsWith("@")) {
				sub = new SubString(subString.substring(0, subString.length() - 1), Position.BEGIN);
			} else {
				sub = new SubString(subString, Position.ANY);
			}

			sub.setSignificant(confidence);
			sub.setSupport(support);
			sub.setCount(count);
			subStrings.add(sub);
			subString2StatisticsMap.put(subString, statistics);
			// System.out.println(subString + " ,count:" + count +
			// " ,confidence:" + confidence + " ,support:" + support);

		}

		return subStrings;
	}
}
