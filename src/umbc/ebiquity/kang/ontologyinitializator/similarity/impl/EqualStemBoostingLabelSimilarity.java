package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.List;

import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class EqualStemBoostingLabelSimilarity implements ILabelSimilarity {

	private IWordListSimilarity _wordListSimilarity;
	private IWordListSimilarity _equalSemanticRootWordListSimilarity = new EqualStemWordListSimilarity();
	private boolean _penalize = true;

	private double differenceThreshold = 0.2;
	private double equalityThreshold = 0.9;
	private double penalityFactor = -0.15;
	
	public EqualStemBoostingLabelSimilarity(IWordListSimilarity wordListSimilarity) {
		this._wordListSimilarity = wordListSimilarity;
	}
	
	public EqualStemBoostingLabelSimilarity(IWordListSimilarity wordListSimilarity, boolean penalize) {
		this._wordListSimilarity = wordListSimilarity;
		this._penalize = penalize;
	}
	
	@Override
	public double computeLabelSimilarity(String label1, String label2) {
		if (label1.toLowerCase().equals(label2.toLowerCase())) return 1.0;
		List<String> wordList1 = TextProcessingUtils.tokenizeLabel2List(label1, true, true, 1);
		List<String> wordList2 = TextProcessingUtils.tokenizeLabel2List(label2, true, true, 1);
		if(wordList1.size() == 0 || wordList2.size() == 0){
			return 0.0;
		} 
		
		double wordLevel_sim = this._wordListSimilarity.computeSimilarity(wordList1, wordList2);
		double similarity = wordLevel_sim;
		if (similarity >= this.equalityThreshold || similarity < differenceThreshold) {
			return similarity;
		}
		
		double boostingFactor = this.boosting(this._equalSemanticRootWordListSimilarity.computeSimilarity(wordList1, wordList2));
		
		if (boostingFactor == 0 && _penalize) {
			boostingFactor = penalityFactor;
			return similarity + similarity * boostingFactor;
		}

		similarity = similarity + (1 - similarity) * boostingFactor;
		return similarity;
	}
	
	private double boosting(double value) {
		return Math.sqrt(1 - (value - 1) * (value - 1));
	}

}
