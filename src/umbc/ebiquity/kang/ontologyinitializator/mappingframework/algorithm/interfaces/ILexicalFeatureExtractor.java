package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SubString;

public interface ILexicalFeatureExtractor {

	Collection<SubString> extractCommonSubStrings(Collection<String> LabelCollections);

	Collection<String> computeCommonPhrasesInString(Collection<String> wordSets);

	Collection<Phrase> extractCommonPhrases(Collection<String> wordSets);

	String[] normalizeLabelToArray(String label);

	String normalizeLabelToString(String label); 
 
}
