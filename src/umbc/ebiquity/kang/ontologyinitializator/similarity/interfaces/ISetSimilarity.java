package umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces;

import java.util.Set;

public interface ISetSimilarity {

	double computeSimilarity(Set<String> set1, Set<String> set2);

	void setSignificantMathcThreshold(double threshold); 
}
