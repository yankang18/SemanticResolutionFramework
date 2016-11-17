package umbc.ebiquity.kang.textprocessing.phraseextractor.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.Phrase;
import umbc.ebiquity.kang.textprocessing.phraseextractor.IPhraseExtractor;

/***
 * 
 * @author kangyan2003
 *
 */
public class CombinationInOrderPhraseExtractor implements IPhraseExtractor {
	
	public static void main(String[] args) throws IOException {

		String[] words = { "word1", "word2", "word3", "word4", "word5" };
		CombinationInOrderPhraseExtractor e = new CombinationInOrderPhraseExtractor();
		printPhrase(e.extractPhraseStrings(words, 1));
		printPhrase(e.extractPhraseStrings(words, 2));
		printPhrase(e.extractPhraseStrings(words, 3));
		printPhrase(e.extractPhraseStrings(words, 4));
		printPhrase(e.extractPhraseStrings(words, 5));
		
	}

	private static void printPhrase(Collection<String> phrases) {
		System.out.println();
		for(String phrase : phrases){
			System.out.println(phrase);
		}
	}

	/***
	 * 
	 * @param words
	 * @param phraseLength
	 * @return
	 */
	public Collection<String> extractPhraseStrings(String[] words, int phraseLength) {
		Collection<String> phraseCollection = new LinkedHashSet<String>();
		if (words.length >= phraseLength && phraseLength > 0) {
			int numOfTokens = words.length;
			if (phraseLength == 2) {
				for (int i = 0; i < numOfTokens - 1; i++) {

					for (int j = i + 1; j < numOfTokens; j++) {
						phraseCollection.add(words[i] + " " + words[j]);
					}

				}

			} else if (phraseLength == 3) {

				for (int i = 0; i < numOfTokens - 2; i++) {

					for (int j = i + 1; j < numOfTokens - 1; j++) {

						for (int z = j + 1; z < numOfTokens; z++) {
							phraseCollection.add(words[i] + " " + words[j] + " " + words[z]);
						}
					}
				}

			} else {

				for (int i = phraseLength - 1; i < numOfTokens; i++) {

					StringBuilder phraseBuilder = new StringBuilder();
					int nextPhraseStartPosition = i - phraseLength + 1;
					for (int j = nextPhraseStartPosition; j <= nextPhraseStartPosition + phraseLength - 1; j++) {
						phraseBuilder.append(words[j] + " ");
					}
					phraseCollection.add(phraseBuilder.toString().trim());
				}
			}
		}

		return phraseCollection;
	}

	@Override
	public Collection<Phrase> extractPhrases(String[] words, int phraseLength) {
		// TODO Auto-generated method stub
		return null;
	}

}
