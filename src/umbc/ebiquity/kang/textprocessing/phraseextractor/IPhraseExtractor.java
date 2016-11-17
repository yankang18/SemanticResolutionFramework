package umbc.ebiquity.kang.textprocessing.phraseextractor;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;

public interface IPhraseExtractor {
	
	public Collection<String> extractPhraseStrings(String[] words, int phraseLength);

	public Collection<Phrase> extractPhrases(String[] words, int phraseLength);

}
