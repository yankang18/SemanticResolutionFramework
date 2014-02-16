package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.List;

import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.IWordListSimilarity;

public class EqualSemanticRootWordListSimilarity implements IWordListSimilarity {

	@Override
	public double computeSimilarity(List<String> wordList1, List<String> wordList2) {
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


}
