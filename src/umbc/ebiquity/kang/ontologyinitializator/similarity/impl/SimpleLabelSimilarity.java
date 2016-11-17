package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.List;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class SimpleLabelSimilarity implements ILabelSimilarity {
	
	private IWordListSimilarity _wordListSimilarity;

	public SimpleLabelSimilarity(IWordListSimilarity wordListSimilarity) {
		this._wordListSimilarity = wordListSimilarity;
	}

	@Override
	public double computeLabelSimilarity(String label1, String label2) {
		List<String> list1 = TextProcessingUtils.tokenizeLabel2List(label1, true, true, 1);
		List<String> list2 = TextProcessingUtils.tokenizeLabel2List(label2, true, true, 1);
		return this._wordListSimilarity.computeSimilarity(list1, list2);
	}

}
