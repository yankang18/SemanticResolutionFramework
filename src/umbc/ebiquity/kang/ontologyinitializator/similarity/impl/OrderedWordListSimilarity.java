package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.List;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;

public class OrderedWordListSimilarity implements IWordListSimilarity {

	@Override
	public double computeSimilarity(List<String> wordList1, List<String> wordList2) {
		if (wordList1.size() == 0 || wordList2.size() == 0) {
			return 0.0;
		}
		int wordListSize1 = wordList1.size();
		int wordListSize2 = wordList2.size();
		int previousWordIndex = -1;
		int matchedWordCount = 0;
		for (int i = 0; i < wordListSize2; i++) {
			if (wordList1.contains(wordList2.get(i)) && wordList1.indexOf(wordList2.get(i)) > previousWordIndex) {
				previousWordIndex = wordList1.indexOf(wordList2.get(i));
				matchedWordCount++;
			}
		}
		double relationMatchScore = (double) matchedWordCount / (((double) wordListSize1 + (double) wordListSize2) * 0.5);
		return relationMatchScore;
	}

}
