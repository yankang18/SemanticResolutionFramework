package umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces;

import java.util.List;

public interface IWordListSimilarity {
	
	double computeSimilarity(List<String> wordList1, List<String> wordList2);

}
