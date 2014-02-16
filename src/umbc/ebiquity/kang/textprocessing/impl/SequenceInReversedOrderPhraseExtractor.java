package umbc.ebiquity.kang.textprocessing.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.LexicalFeature.Position;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;
import umbc.ebiquity.kang.textprocessing.interfaces.IPhraseExtractor;

public class SequenceInReversedOrderPhraseExtractor implements IPhraseExtractor {
	
	public static void main(String[] args) throws IOException {
		String[] words = { "word1", "word2", "word3", "word4", "word5" };
		SequenceInReversedOrderPhraseExtractor e = new SequenceInReversedOrderPhraseExtractor();
		printPhrase(e.extractPhraseStrings(words, 1));
		printPhrase(e.extractPhraseStrings(words, 2));
		printPhrase(e.extractPhraseStrings(words, 3));
		printPhrase(e.extractPhraseStrings(words, 4));
		printPhrase(e.extractPhraseStrings(words, 5));
		printPhrase(e.extractPhraseStrings(words, words.length));
	}

	private static void printPhrase(Collection<String> phrases) {
		System.out.println();
		for (String phrase : phrases) {
			System.out.println(phrase);
		}
	}

	@Override
	public Collection<String> extractPhraseStrings(String[] words, int phraseLength) {
		Collection<String> phraseCollection = new LinkedHashSet<String>();
		int length = phraseLength > words.length ? words.length : phraseLength;
		StringBuilder phraseBuilder = new StringBuilder();
		for (int i = length - 1; i >= 0; i--) {
			phraseBuilder.insert(0, " " + words[i]);
			phraseCollection.add(phraseBuilder.toString().trim());
		}
		return phraseCollection;
	}
	
	@Override
	public Collection<Phrase> extractPhrases(String[] words, int maxPhraseLength) {
		Collection<Phrase> phraseCollection = new LinkedHashSet<Phrase>();
		int length = maxPhraseLength > words.length ? words.length : maxPhraseLength;
		StringBuilder phraseBuilder = new StringBuilder();
		int phraseLength = 0;
		for (int i = length - 1; i >= 0; i--) {
			phraseLength++;
			phraseBuilder.insert(0, " " + words[i]);
			Phrase phrase = new Phrase(phraseBuilder.toString().trim(), Position.END);
			phrase.setSupport(1.0);
			phrase.setSignificant((double) phraseLength / (double) length);
			phraseCollection.add(phrase);
		}
		return phraseCollection;
	}
}
