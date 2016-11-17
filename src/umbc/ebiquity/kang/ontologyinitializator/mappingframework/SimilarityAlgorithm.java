package umbc.ebiquity.kang.ontologyinitializator.mappingframework;

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

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2Extended;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.entityframework.object.EntityNode;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class SimilarityAlgorithm {
	
	private MSMLabelSimilarity labelSimilarity ;
	private QGramsDistance ngramSimilarity ;
	
	private double similarityBoostingFactor = 0.5;
	private double similarityPenalityFactor = 0.7;
	private double equalityThreshold = 0.9;
	private double differenceThreshold = 0.4;
	
	public enum SimilarityType{
		Kim, Ngram, Kim_Ngram, Semantics
	}

	public SimilarityAlgorithm() {
		labelSimilarity = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
		ngramSimilarity = new QGramsDistance(new TokeniserQGram2Extended());
	}

	/**
	 * get similarity between two strings by using ngram algorithm
	 * @param label1
	 * @param label2
	 * @return
	 */
	private double getNgramSimilarity(String label1, String label2) {
		label1 = TextProcessingUtils.getProcessedLabelWithStemming(label1, "");
		label2 = TextProcessingUtils.getProcessedLabelWithStemming(label2, "");
		double ngram_sim = 0.0;
		if (!label1.trim().equals("") && !label2.trim().equals("")) {
			// System.out.println("4 compare " + label1 + " with " + label2);
			ngram_sim = ngramSimilarity.getSimilarity(label1, label2);
			ngram_sim = (1 - (1 - ngram_sim) * 0.5) * ngram_sim;
		}
		return ngram_sim;
	}
		
	/**
	 * get similarity between two strings by using Kim's label matching algorithm
	 * @param label1
	 * @param label2
	 * @return
	 */
	private double getKimSimilarity(String label1, String label2) {
		label1 = TextProcessingUtils.getProcessedLabelWithStemming(label1, " ");
		label2 = TextProcessingUtils.getProcessedLabelWithStemming(label2, " ");
		double msm_sim = 0.0;
		if (!label1.trim().equals("") && !label2.trim().equals("")) {
//			System.out.println(label1 + " -- " + label2);
			msm_sim = labelSimilarity.getSimilarity(label1, label2);
		}
		return msm_sim;
	}
	
	/**
	 * 
	 * @param label1
	 * @param label2
	 * @return
	 */
	private double getCombinedSimilarity(String label1, String label2) {
		/*
		 * get similarity between two strings by using Kim's label matching
		 * algorithm
		 */
		double msm_sim = this.getKimSimilarity(label1, label2);

		/*
		 * get similarity between two strings by using ngram algorithm
		 */
		double ngram_sim = this.getNgramSimilarity(label1, label2);

		// System.out.println(msm_sim + " " + ngram_sim);
		return msm_sim > ngram_sim ? msm_sim : ngram_sim;
	}

	public double getSimilarity(SimilarityType type, String label1, String label2) {
		double sim = 0.0;
		switch (type) {
		case Ngram:
			sim = this.getNgramSimilarity(label1, label2);
			break;
		case Kim:
			sim = this.getKimSimilarity(label1, label2);
			break;
		case Kim_Ngram:
			sim = this.getCombinedSimilarity(label1, label2);
			break;
		case Semantics:
			sim = this.getSemanticSimilarity(label1, label2);
		}
		return sim;
	}
	
	private double getSemanticSimilarity(String label1, String label2) {

		int wordCountInConcept = this.getWordCount(label1);
		int wordCountInOntoClass = this.getWordCount(label2);
		int wordCountDiff = wordCountInConcept - wordCountInOntoClass;

		
//		System.out.println("label1: " + label1 + ", " + wordCountInConcept);
//		System.out.println("label2: " + label2 + ", " + wordCountInOntoClass);
//		System.out.println("wordCountDiff: " + wordCountDiff);
		
		double kim_sim = this.getSimilarity(SimilarityType.Kim, label1, label2);
		double ngram_sim = this.getSimilarity(SimilarityType.Ngram, label1, label2);
		double similarity = 0.0;
		if (kim_sim >= ngram_sim) {
			similarity = kim_sim;
		} else {
			similarity = ngram_sim;
		}

		if (similarity >= this.equalityThreshold || similarity < this.differenceThreshold) {
			return similarity;
		}

		if (wordCountDiff > 0) {
			/*
			 * boosting the similarity between the concept and the onto-class
			 * when the concept is highly likely the subclass of the onto-class
			 * (e.g., AB:B, ABC:BC, ABC:C, ABCD:ABC, ABCD:CD, etc.)
			 */
			StringBuilder subConceptLabelBuilder1 = new StringBuilder();
			String[] conceptTokens = TextProcessingUtils.getTokensWithStemming(label1);
			
//			System.out.println("Concept List: " + Arrays.asList(conceptTokens));
			
			for (int i = wordCountDiff; i < wordCountInConcept; i++) {
				subConceptLabelBuilder1.append(conceptTokens[i] + " ");
			}

			// System.out.println("1 Compute Similarity of " +
			// subConceptLabelBuilder1.toString() +" and " + ontoClassLabel);
			double sim1 = this.getSimilarity(SimilarityType.Kim_Ngram, subConceptLabelBuilder1.toString().trim(), label2);

			if (sim1 >= this.equalityThreshold) {
				similarity = similarity + (1 - similarity) * this.similarityBoostingFactor;
			}

			/*
			 * penalizing the similarity between the concept and the onto-class
			 * when the concept is highly likely not referring to the same stuff
			 * as the onto-class is. They may have high similarity simply
			 * because they own some same words (e.g., AB:A, ABC:AB, ...)
			 */
			StringBuilder subConceptLabelBuilder2 = new StringBuilder();
			for (int i = 0; i < wordCountInOntoClass; i++) {
				subConceptLabelBuilder2.append(conceptTokens[i] + " ");
			}

			// System.out.println("2 Compute Similarity of " +
			// subConceptLabelBuilder2.toString() +" and " + ontoClassLabel);
			double sim2 = this.getSimilarity(SimilarityType.Kim_Ngram, subConceptLabelBuilder2.toString().trim(), label2);

			if (sim2 >= this.equalityThreshold) {
				similarity = similarity - (1 - similarity) * this.similarityPenalityFactor;
			}
		}
		return similarity;
	}

//	/**
//     * compute the difference between the amount of words in label1 and label2:
//     *     if the amount of words in label1 is bigger than the amount of words in label2, return 1; 
//     *     if the amount of words in label1 is smaller than the amount of words in label2, return -1; 
//     *     otherwise return 0
//     * 
//     * @param label1
//     * @param label2
//     * @return 
//     */
//	public int getWordCountDifference(String label1, String label2) {
//		int num1 = TextProcessingUtils.getTokensWithStemming(label1).length;
//		int num2 = TextProcessingUtils.getTokensWithStemming(label2).length;
//		if (num1 > num2) {
//			return 1;
//		} else if (num1 < num2) {
//			return -1;
//		}
//		return 0;
//	}
	
	/**
	 * compute the number of words contained in the inputed label
	 * 
	 * @param label - the inputed label that is type of String
	 * @return the number of words contained in the inputed label
	 */
	public int getWordCount(String label){
		return TextProcessingUtils.getTokensWithStemming(label).length;
	}
	
	public double computeSetSimilarityWithEqualSemanticRootBoosting(Set<String> set1, Set<String> set2, boolean penalize){
		if(set1.size() == 0 || set2.size() == 0){
			return 0.0;
		} 
		Map<String, Double> valueMap = new HashMap<String, Double>();
		
		double matchedSize1 = 0.0;
		double totalScore1 = 0.0;
		for (String range1 : set1) {
			double maxScore = 0.0;
			for (String range2 : set2) {
				double score = this.computeLabelSimilarityWithEqualSemainticRootBoosting(range1, range2, penalize);
				
				String key = range1 + "@" + range2;
				valueMap.put(key, score);
				if (score > 0.0) {
					System.out.println("1 <" + range1 + "  ?  " + range2 + ">  with score " + score);
				}
				if(score == 1.0){
					maxScore = score;
					break;
				} else if (score > maxScore) {
					maxScore = score;
				}
			}
			if (maxScore > 0.0) {
				matchedSize1++;
				totalScore1 += maxScore;
			}
//			totalScore1 += maxScore;
		}

		double matchedSize2 = 0.0;
		double totalScore2 = 0.0;
		for (String range2 : set2) {
			double maxScore = 0.0;
			for (String range1 : set1) {
				double score = 0.0;
				String key = range1 + "@" + range2;
				if (valueMap.containsKey(key)) {
					score = valueMap.get(key);
//					System.out.println("from cache <" + range1 + "  ?  " + range2 + ">  with score " + score);
				} else {
//					score = this.computeSimilarity(range1, range2);
					score = this.computeLabelSimilarityWithEqualSemainticRootBoosting(range1, range2, true);
//					System.out.println("new computed <" + range1 + "  ?  " + range2 + ">  with score " + score);
				}
				if (score > 0.0) {
					System.out.println("2 <" + range2 + "  ?  " + range1 + ">  with score " + score);
				}
				if (score == 1.0) {
					maxScore = score;
					break;
				} else if (score > maxScore) {
					maxScore = score;
				}
			}
			
			if (maxScore > 0.0) {
				matchedSize2++;
				totalScore2 += maxScore;
			}
//			totalScore2 += maxScore;
		}

		if(matchedSize1 == 0 || matchedSize2 == 0){
			return 0.0;
		}
		
		double largerSize = matchedSize1 > matchedSize2 ? matchedSize1 : matchedSize2;
		double smallerSize = matchedSize1 < matchedSize2 ? matchedSize1 : matchedSize2;
		double sizeRatio = largerSize / smallerSize;
		double sizeRatio1 = (double) set1.size() / matchedSize1;
		double sizeRatio2 = (double) set2.size() / matchedSize2;
		System.out.println("Size Ratios: #"+ sizeRatio + "; " + sizeRatio1 + "; " + sizeRatio2);
		double avePenaltyFactor = this.computePenaltyFactor(sizeRatio,sizeRatio1,sizeRatio2);
		double aveScore = ((double) totalScore1 + (double) totalScore2) / ((double) matchedSize1 + (double) matchedSize2);
		System.out.println("Ave Penalty: " + avePenaltyFactor);
		System.out.println("Ave Score: " + aveScore);
		return aveScore * avePenaltyFactor;
	}
	
	private double computePenaltyFactor(double multipleDiffOfTwoSide, double multipleDiffOfOneSide1, double multipleDiffOfOneSide2){
		
		double penaltyFactor1 = 1 / (1 + Math.log(multipleDiffOfTwoSide));
		double penaltyFactor2 = 1 / (1 + Math.log(multipleDiffOfOneSide1));
		double penaltyFactor3 = 1 / (1 + Math.log(multipleDiffOfOneSide2));
		
		double avePenaltyFactor = (penaltyFactor1 + this.Fmeasure(penaltyFactor2, penaltyFactor3, 1)) / 2;
		System.out.println("Penalty: #" + penaltyFactor1 + "; " + penaltyFactor2 + "; " + penaltyFactor3);
		return avePenaltyFactor;
	}

	private double Fmeasure(double value1, double value2, int beta) {
		return (1 + beta * beta) * value1 * value2 / ((beta * beta * value1) + value2);
	}

	public double computeSetsSimilarityByEqualitySemanticRoot(Set<String> set1, Set<String> set2){
		if(set1.size() == 0 || set2.size() == 0){
			return 0.0;
		} 
		
		if(set1.size() < set2.size()){
			Set<String> tempSet = set1;
			set1 = set2;
			set2 = tempSet;
		}
		
		double totalScore = 0.0;
		for (String range1 : set1) {
			double maxScore = 0.0;
			for (String range2 : set2) {
				double score = this.computeLabelSimilarityByEqualitySemanticRoot(range1.toLowerCase(), range2.toLowerCase());
				if(score == 1.0){
					maxScore = score;
					break;
				} else if (score > maxScore) {
					maxScore = score;
				}
			}
			totalScore += maxScore;
		}
		double returnedScore = (double) totalScore / (double) set1.size() ;
		
		return returnedScore;
	}
	
	public double computeLabelSimilarityWithSubSumptionRelationshipBoosting(String label, String referenceLabel, boolean penalize){
		if (label.equals(referenceLabel)) return 1.0;
		List<String> wordList1 = TextProcessingUtils.tokenizeLabel2List(label, true, true, 0);
		List<String> wordList2 = TextProcessingUtils.tokenizeLabel2List(referenceLabel, true, true, 0);
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		
		double wordLevel_sim = this.computeWordListSimilarityByOrderedWordPattern(wordList1, wordList2);
		double charLevel_sim = this.getSimilarity(SimilarityType.Ngram, label, referenceLabel);
		double similarity = 0.0;
		if (wordLevel_sim >= charLevel_sim) {
			similarity = wordLevel_sim;
		} else {
			similarity = charLevel_sim;
		}
		if (similarity >= this.equalityThreshold || similarity < 0.2) {
			return similarity;
		}
		
		double boostingFactor = this.computeWordListSimilarityByEqualitySemanticRoot(wordList1, wordList2);
		int wordCountDiff = wordList1.size() - wordList2.size();
		if (boostingFactor == 0) {
			
			if (penalize) {
				boostingFactor = -0.15;
				similarity = similarity + similarity * boostingFactor;
			}
			
		} else {
			
			if (wordCountDiff > 0) {
				/*
				 * when the concept has more words than the onto-class
				 */
				/*
				 * boosting the similarity between the concept and the
				 * onto-class when the concept is highly likely the subclass of
				 * the onto-class (e.g., AB:B, ABC:BC, ABC:C, ABCD:BCD, ABCD:CD,
				 * etc.)
				 */

				similarity = similarity + (1 - similarity) * boostingFactor;
			} else {
				if (penalize) {
					boostingFactor = -0.15;
					similarity = similarity + similarity * boostingFactor;
				}
			}

		}
		return similarity;
		
	}
	
	public double computeLabelSimilarityWithEqualSemainticRootBoosting(String label1, String label2, boolean penalize){
		if (label1.toLowerCase().equals(label2.toLowerCase())) return 1.0;
		List<String> wordList1 = TextProcessingUtils.tokenizeLabel2List(label1, true, true, 1);
		List<String> wordList2 = TextProcessingUtils.tokenizeLabel2List(label2, true, true, 1);
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		
		double wordLevel_sim = this.computeWordListSimilarityByOrderedWordPattern(wordList1, wordList2);
//		double charLevel_sim = this.getSimilarity(SimilarityType.Ngram, label1, label2);
		double similarity = wordLevel_sim;
		if (similarity >= this.equalityThreshold || similarity < 0.2) {
			return similarity;
		}
		
		double boostingFactor = this.boosting(this.computeWordListSimilarityByEqualitySemanticRoot(wordList1, wordList2));
		
		if (boostingFactor == 0 && penalize) {
			boostingFactor = -0.15;
			return similarity + similarity * boostingFactor;
		}

		similarity = similarity + (1 - similarity) * boostingFactor;
		return similarity;
	}
	
	private double boosting(double value) {
		return Math.sqrt(1 - (value - 1) * (value - 1));
	}
	
	public double computeLabelSimilarityByEqualitySemanticRoot(String label1, String label2) { 
		if (label1.equals(label2)) return 1.0;
		List<String> wordList1 = TextProcessingUtils.tokenizeLabel2List(label1, true, true, 1);;
		List<String> wordList2 = TextProcessingUtils.tokenizeLabel2List(label2, true, true, 1);;
		return this.computeWordListSimilarityByEqualitySemanticRoot(wordList1, wordList2);
	}
	
	public double computeLabelSimilarityByOrderedWordPattern(String label1, String label2){
		List<String> list1 = Arrays.asList(TextProcessingUtils.tokenizeLabel2Array(label1, true, true, 1));
		List<String> list2 = Arrays.asList(TextProcessingUtils.tokenizeLabel2Array(label2, true, true, 1));
		return this.computeWordListSimilarityByOrderedWordPattern(list1, list2);
	}
	
	
	
	
	
	
	
	
	/**
	 * Compute similarity of two word lists based on Same Semantic Root (SSR)
	 * @param wordList1
	 * @param wordList2
	 * @return
	 */
	public double computeWordListSimilarityByEqualitySemanticRoot(List<String> wordList1, List<String> wordList2){
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		
		int wordListSize1 = wordList1.size();
		int wordListSize2 = wordList2.size();
		int minWordListSize = wordListSize1 < wordListSize2 ? wordListSize1 : wordListSize2;
		int count = 0;
		for (int z = 0, i = wordListSize1 - 1, j = wordListSize2 - 1; z < minWordListSize; z++, i--, j--) {
			if (wordList1.get(i).equals(wordList2.get(j))) {
				count++;
			} else {
				break;
			}
		}
		double aveWordListSize = 0.5 * ((double) wordListSize1 + (double) wordListSize2);
		return (double) count / aveWordListSize;
	}
	
	/**
	 * Compute similarity of two word lists based on Ordered Words Pattern (OWP)
	 * @param wordList1
	 * @param wordList2
	 * @return
	 */
	public double computeWordListSimilarityByOrderedWordPattern(List<String> wordList1, List<String> wordList2){
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		int wordListSize1 = wordList1.size();
		int wordListSize2 = wordList2.size();
		int previousWordIndex = -1;
		int matchedWordCount = 0;
		for (int i = 0; i < wordListSize2; i++) {
			if (wordList1.contains(wordList2.get(i))
					&& wordList1.indexOf(wordList2.get(i)) > previousWordIndex) {
				previousWordIndex = wordList1.indexOf(wordList2.get(i));
				matchedWordCount++;
			}
		}
		double relationMatchScore = (double) matchedWordCount / (((double) wordListSize1 + (double) wordListSize2) * 0.5);
		return relationMatchScore;
	}
	
	public double computeWordListSimilarityByUnorderedWordPattern(List<String> wordList1, List<String> wordList2){
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		int wordListSize1 = wordList1.size();
		int wordListSize2 = wordList2.size();
		int matchedWordCount = 0;
		for (int i = 0; i < wordListSize2; i++) {
			if (wordList1.contains(wordList2.get(i))) {
				matchedWordCount++;
			}
		}
		double relationMatchScore = (double) matchedWordCount / ((double) wordListSize1 + (double) wordListSize2 - (double) matchedWordCount);
		return relationMatchScore;
	}
	
}
