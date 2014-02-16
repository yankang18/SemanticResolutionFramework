package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2Extended;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class SubSumptionRelationshipBoostingLabelSimilarity implements ILabelSimilarity {
	
	private QGramsDistance ngramSimilarity = new QGramsDistance(new TokeniserQGram2Extended()); 
	private IWordListSimilarity _wordListSimilarity;
	private IWordListSimilarity _equalSemanticRootWordListSimilarity = new EqualSemanticRootWordListSimilarity();
	private boolean _penalize = true;

	private double differenceThreshold = 0.2;
	private double equalityThreshold = 0.9;
	private double penalityRate = -0.4;
	private double boostingRate = 0.8;
	
	public SubSumptionRelationshipBoostingLabelSimilarity(IWordListSimilarity wordListSimilarity) {
		this._wordListSimilarity = wordListSimilarity;
	}
	
	public SubSumptionRelationshipBoostingLabelSimilarity(IWordListSimilarity wordListSimilarity, boolean penalize) {
		this._wordListSimilarity = wordListSimilarity;
		this._penalize = penalize;
	}
	
	@Override
	public double computeLabelSimilarity(String label, String referedLabel) {
		label = label.trim();
		referedLabel = referedLabel.trim();
		if (label.equals(referedLabel)) return 1.0;
		
		List<String> wordList1 = TextProcessingUtils.tokenizeLabel2List(label, true, true, 0);
		List<String> wordList2 = TextProcessingUtils.tokenizeLabel2List(referedLabel, true, true, 0);
		if (wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		
//		System.out.println(" " + wordList1);
//		System.out.println(" " + wordList2);
		
		double wordLevel_sim = this._wordListSimilarity.computeSimilarity(wordList1, wordList2);
//		System.out.println("Word Level: " + wordLevel_sim);
		label = this.concatenateListAsString(wordList1);
		referedLabel = this.concatenateListAsString(wordList2);
		double charLevel_sim = this.ngramSimilarity.getSimilarity(label, referedLabel);
//		System.out.println("Char Level: " + charLevel_sim);
		double similarity = 0.0;
		if (wordLevel_sim >= charLevel_sim) {
			similarity = wordLevel_sim;
		} else {
			similarity = charLevel_sim;
		}
		if (similarity >= this.equalityThreshold || similarity < differenceThreshold) {
			return similarity;
		}
		
		double rootSimilarityFactor = this._equalSemanticRootWordListSimilarity.computeSimilarity(wordList1, wordList2);
//		System.out.println("RootSimilarityFactor: " + rootSimilarityFactor);
		if (rootSimilarityFactor > 0) {
			int labelLengthDiff = label.length() - referedLabel.length();
//			System.out.println("labelLengthDiff: " + labelLengthDiff);
//			System.out.println(label + " - " + referedLabel);
//			System.out.println("end with : " + label.endsWith(referedLabel));
			if (labelLengthDiff > 0 && label.endsWith(referedLabel)) {
				/*
				 * when the concept has more words than the onto-class boosting
				 * the similarity between the concept and the onto-class when
				 * the concept is highly likely the subclass of the onto-class
				 * (e.g., AB:B, ABC:BC, ABC:C, ABCD:BCD, ABCD:CD, etc.)
				 */
				similarity = similarity + this.boostingRate * (1 - similarity) * rootSimilarityFactor;
			} else if (_penalize) {
				double penalityFactor = this.penalityRate * similarity;
				similarity = similarity + similarity * penalityFactor;
			}
		} else {
			if (_penalize) {
				double penalityFactor = this.penalityRate;
				similarity = similarity + similarity * penalityFactor;
			}
		}
		return similarity;
	}
	
	private String concatenateListAsString(List<String> list){
		StringBuilder sb = new StringBuilder();
		for(String token : list){
			sb.append(token);
		}
		return sb.toString().toLowerCase();
	}
}
