package umbc.ebiquity.kang.textprocessing.stemmer.impl;

import umbc.ebiquity.kang.textprocessing.stemmer.IStemmer;

/**
 * This stemmer is only for plural. But, it is a useful, but not particularly
 * efficient plural stripper.
 * 
 * @author kangyan2003
 */
public class PluralStemmer implements IStemmer {

	private final static int STRIP_PLURAL_MIN_WORD_SIZE = 3;

	/**
	 * Stem a word provided as a String. Returns the result as a String.
	 */
	public String stem(String word) {
		// too small?
		if (word.length() < STRIP_PLURAL_MIN_WORD_SIZE) {
			return word;
		}
		// special cases
		if (word.equals("has") || word.equals("was") || word.equals("does") || 
		    word.equals("goes") || word.equals("dies") || word.equals("yes") || 
		    word.equals("gets") || // means too much in java/JSP
			word.equals("its")) {
			return word;
		}
		String newWord = word;
		if (word.endsWith("sses") || word.endsWith("xes") || word.endsWith("hes")) {
			// remove 'es'
			newWord = word.substring(0, word.length() - 2);
		} else if (word.endsWith("ies")) {
			// remove 'ies', replace with 'y'
			newWord = word.substring(0, word.length() - 3) + 'y';
		} else if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("is") && 
				  !word.endsWith("us") && !word.endsWith("pos") && !word.endsWith("ses")) {
			// remove 's'
			newWord = word.substring(0, word.length() - 1);
		}
		return newWord;
	}
}
