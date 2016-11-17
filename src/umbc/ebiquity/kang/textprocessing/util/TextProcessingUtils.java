package umbc.ebiquity.kang.textprocessing.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2Extended;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;
import umbc.ebiquity.kang.textprocessing.stemmer.IStemmer;
import umbc.ebiquity.kang.textprocessing.stemmer.impl.PluralStemmer;
import umbc.ebiquity.kang.textprocessing.stemmer.impl.PorterStemmer;

public class TextProcessingUtils {
	static boolean mode_removeEngStopWords = true;
	private static String projectDir = System.getProperty("user.dir");
	private static String web_sw = projectDir + "/Stopwords/WEB.TXT";
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(TextProcessingUtils.class.getName());

	private static HashMap<String, String[]> cachedTextProcessing = new HashMap<String, String[]>();
	private static IStemmer porterStemmer = new PorterStemmer();
	private static IStemmer pluralStemmer = new PluralStemmer();
	private static Stopwords stopwords = new Stopwords(true);
	private static Stopwords defaultStopwords = new Stopwords(true);
	private static Stopwords webpageStopwords = new Stopwords(false);
//	private static 
	static {
		try {
			stopwords.read();
			webpageStopwords.read(web_sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean containsOnlyDefaultStopwords(String t) {
		t = t.replaceAll("[();:\"'.,]", "").toLowerCase().trim();
		String[] tokens = t.trim().split(" ");
		boolean containsOnlyDefaultStopwords = true;
		for(String token: tokens){
			if(!defaultStopwords.getStopwords().contains(token)){
				containsOnlyDefaultStopwords = false;
			}
		}
		return containsOnlyDefaultStopwords;
	}
	
	public static boolean containsWebPageStopwords(String t) {
		t = t.replaceAll("[();:\"'.,]", "").toLowerCase().trim();
		
		for(String stopword : webpageStopwords.getStopwords()){
			if(stopword.contains(t) || t.contains(stopword)) return true;
		}
		
		return false;
	}
	
	public static String getProcessedLabel(String label, String spliter) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(TextProcessingUtils.removeStopwords(label.toLowerCase()));
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			processedTermLabelSB.append(token + spliter);
		}
		return processedTermLabelSB.toString().trim();
	}

	public static String getProcessedLabel2(String label, String spliter) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			processedTermLabelSB.append(token + spliter);
		}
		return processedTermLabelSB.toString().trim();
	}
	
	public static String getProcessedLabelWithStemming(String label, String delimer) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			token = token.toLowerCase();
			if (!stopwords.is(token)) {
				processedTermLabelSB.append(pluralStemmer.stem(token) + delimer);
			}
		}
		return processedTermLabelSB.toString().trim();
	}
	
	public static List<String> processRelationLabelWithStemming(String label) {
		label = label.replaceAll("[();:\"'.,]", "");
		defaultStopwords.remove("and");
		List<String> wordList = new ArrayList<String>();
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		for (String token : tokens) {
			token = token.toLowerCase();
			if (!defaultStopwords.is(token)) {
				wordList.add(porterStemmer.stem(token));
			}
		}
		defaultStopwords.add("and");
		return wordList;
	}
	
	public static String getProcessedLabel2WithStemming(String label, String delimer) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			processedTermLabelSB.append(pluralStemmer.stem(token.toLowerCase()) + delimer); 
		}
		return processedTermLabelSB.toString().trim();
	}
	
	public static String[] getTokensWithStemming(String label) {
		return tokenizeLabel(getProcessedLabelWithStemming(label, " "));
	}
	
	public static String tokenizeLabel2String(String label, boolean stemming, boolean removeStopWords, int flag){
		StringBuilder sb = new StringBuilder(); 
		for(String token : tokenizeLabel2List(label, stemming, removeStopWords, flag).toArray(new String[0])){
			sb.append(token + " ");
		}
		return sb.toString().trim();
	}
	
	
	public static String[] tokenizeLabel2Array(String label, boolean stemming, boolean removeStopWords, int flag){
		return tokenizeLabel2List(label, stemming, removeStopWords, flag).toArray(new String[0]);
	}
	
	public static List<String> tokenizeLabel2List(String label, boolean stemming, boolean removeStopWords, int flag){
		Stopwords stopWords;
		if(flag == 0){ 
			stopWords = defaultStopwords;
		}
		else {
			stopWords = stopwords;
		}
		
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		ArrayList<String> newTokens = new ArrayList<String>();
		for (String token : tokens) {
			token = token.toLowerCase();
			if (!removeStopWords || (removeStopWords && !stopWords.is(token))) {
				if (stemming) {
					newTokens.add(pluralStemmer.stem(token));
				} else {
					newTokens.add(token);
				}
			}
		}
		return newTokens;
	}

	
	public static String pluralStem(String s){
		return pluralStemmer.stem(s);
	}
	
	public static String[] tokenizeLabel2PhrasesWithParallelledSemantic(String label) {
		Collection<String> phrases = new HashSet<String>();
		int preIndex = label.indexOf('(');
		int afterIndex = label.indexOf(')');
		while (preIndex >= 0 && preIndex < afterIndex) {
			String before = label.substring(0, preIndex);
			String after = label.substring(afterIndex + 1);
			String between = label.substring(preIndex + 1, afterIndex);
			phrases.add(pluralStemmer.stem(between));
			label = before + after;
			preIndex = label.indexOf('(');
			afterIndex = label.indexOf(')');
		}
		String[] tokens = label.replaceAll("#", "").replaceAll("\\*", "").split(" and | or |&| / |/");
		for (String token : tokens) {
			String value = token.trim();
			if (!value.equals("")) {
				phrases.add(pluralStemmer.stem(value));
			}
		}
		return phrases.toArray(new String[0]);
	}
	
	/**
	 * count the number of words in the inputed string
	 * @param label the inputed string
	 * @return number of words (stopwords removed) in the inputed string
	 */
	public static int getWordCount(String label) {

		int count = 0;
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		for (String token : tokens) {
			token = token.toLowerCase();
			if (!stopwords.is(token)) {
				count++;
			}
		}
		return count;
	}

	public static String getProcessedLabelWithoutStemming(String label, String delimer) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(label);
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			token = token.toLowerCase();
			if (!stopwords.is(token)) {
				processedTermLabelSB.append(token + delimer);
			}
		}
		return processedTermLabelSB.toString().trim();
	}
	
	public static String[] tokenizeLabel(String label) {
		try {
			logger.entering("TextProcessingUtils", "StringTokenizer", label);
			/**
			 * Tokenizer cannot be cached because it is non-recurring variable.
			 */
			if (cachedTextProcessing.containsKey(label)) {
				logger.finest("Cache Hit!");
				String[] textTokenizer = cachedTextProcessing.get(label);
				logger.finer("# of tokens = " + textTokenizer.length);
				return textTokenizer;
			}
			String resultStr = extractWordString(label);
			logger.finer("extracted word string = " + resultStr);
			StringTokenizer textTokenizer = new StringTokenizer(resultStr);
			logger.finer("# of tokens = " + textTokenizer.countTokens());

			String[] tokens = new String[textTokenizer.countTokens()];
			for (int i = 0; textTokenizer.hasMoreTokens(); i++) {
				String token = textTokenizer.nextToken();
				tokens[i] = token;
			}

			cachedTextProcessing.put(label, tokens);
			return tokens;
		} finally {
			logger.exiting("TextProcessingUtils", "StringTokenizer");
		}
	}

	public static String extractWordString(String label) {
		StringBuffer result = new StringBuffer();
		int lastSpace = 0;

		Character c2 = null;
		for (int i = 0; i < label.length(); i++) {
			Character c = label.charAt(i);

			/**
			 * If the previous inserted character is whitespace, now consider
			 * the current character as a first letter of word if it's letter.
			 * For example, Vehicle ID... Vehicle _ID, Vehicle-ID
			 */
			if (Character.isLetter(c) || Character.isDigit(c) || c.equals('/')) {
				if (c2 == null || Character.isWhitespace(c2)) {
					c2 = Character.toLowerCase(c);
					result.append(c2);
					continue;
				}
				try {
					/**
					 * if the current charter is a Upper Case, it might be the
					 * first letter of new word.
					 */
					if (Character.isUpperCase(c)) {
						/**
						 * if either before or next letter is a lower case such
						 * as Vehicle(D)ata, Vehicle(I)D, or UBL(D)ata, it might
						 * be the first letter of new word.
						 */
						if ((Character.isLowerCase(label.charAt(i - 1)) && Character
								.isLowerCase(label.charAt(i + 1)))) {
							/**
							 * For example, SchemaXPath, we consider 'X' is just
							 * prefix but not a word.
							 */
							if (i - lastSpace > 1) {
								/**
								 * Now put a whitespace before copying the
								 * current letter.
								 */
								result.append(' ');
								lastSpace = i;
							}
						} else if ((Character.isLowerCase(label.charAt(i - 1)) && Character
								.isUpperCase(label.charAt(i + 1)))) {
							// SchemaXPath
							if ((i + 2) < label.length()) {
								if (Character.isLowerCase(label.charAt(i + 2))) {
									// For SchemaXPath, append the current 'X'
									// and move to 'P' which will be copied at
									// finally code then go to 'a'
									result.append(' ');
									lastSpace = i;
									c2 = Character.toLowerCase(c);
									result.append(c2);
									c = label.charAt(++i);
								} else {
									result.append(' ');
									lastSpace = i;
								}
							} else {
								result.append(' ');
								lastSpace = i;
							}

						} else if ((Character.isUpperCase(label.charAt(i - 1)) && Character
								.isLowerCase(label.charAt(i + 1)))) {
							// e.g., POBill
							if (i - lastSpace > 1) {
								/**
								 * Now put a whitespace before copying the
								 * current letter.
								 */
								result.append(' ');
								lastSpace = i;
							}
						}
					}
				} catch (Exception e) {
				} finally {
					// In any case, the letter should be added.
					c2 = Character.toLowerCase(c);
					result.append(c2);
				}
			} else {
				/**
				 * if cur char is not a starting char and non-letter (e.g., -_,"
				 * so on) but non-whitespace, consider as whitespace. Meaning
				 * that any characters other than letter, number, or whitespace
				 * will be ignored.
				 */
				if (c2 != null && Character.isWhitespace(c2) == false) {
					if (i - lastSpace > 1) {
						c2 = ' ';
						result.append(c2);
						lastSpace = i;
					}
				}
			}

		}
		String resultStr = result.toString().trim();
		return resultStr;
	}

	
	public static String removeStopwords(String t) {

//		t = t.replaceAll("[();:\"'.,]", "");

		// extracted from http://www.ranks.nl/tools/stopwords.html
		if (mode_removeEngStopWords) {
			t = t.replaceAll(" a ", " ").replaceAll(" the ", " ").replaceAll(
					" latest ", " ").replaceAll(" or ", " ").replaceAll(" in ",
					" ").replaceAll(" of ", " ").replaceAll(" to ", " ")
					.replaceAll(" that ", " ").replaceAll(" it ", " ")
					.replaceAll(" i ", " ").replaceAll(" you ", " ")
					.replaceAll(" about ", " ").replaceAll(" an ", " ")
					.replaceAll(" are ", " ").replaceAll(" as ", " ")
					.replaceAll(" at ", " ").replaceAll(" be ", " ")
					.replaceAll(" by ", " ").replaceAll(" for ", " ")
					.replaceAll(" from ", " ").replaceAll(" how ", " ")
					.replaceAll(" is ", " ").replaceAll(" on ", " ")
					.replaceAll(" this ", " ").replaceAll(" was ", " ")
					.replaceAll(" what ", " ").replaceAll(" when ", " ")
					.replaceAll(" where ", " ").replaceAll(" who ", " ")
					.replaceAll(" will ", " ").replaceAll(" into ", " ")
					.replaceAll(" which ", " ").replaceAll(" with ", " ")
					.replaceAll(" some ", " ").replaceAll(" our ", " ").replaceAll("our ", "")
					.replaceAll(" those ", " ").replaceAll(" these ", " ").replaceAll("these ", "")
					.replaceAll(" this ", " ").replaceAll(" that ", " ")
					.replaceAll(" we ", " ").replaceAll(" some ", " ").replaceAll(" and ", " ");   
			
		}
		return t;
	}
	
	public static String removePunctations(String s){
		return s.replaceAll("[();:\"'.,]", "");
	}
	
	public static String removeStopwords2(String t) {
		t = t.replaceAll("[();:\"'.,]", "");

		String[] tokens = t.split(" ");

		StringBuilder returnStringBuilder = new StringBuilder("");
		for (String token : tokens) {
			if (!stopwords.is(token.toLowerCase())) {
				returnStringBuilder.append(token + " ");
			}
		}
		return returnStringBuilder.toString().trim();

	}
	
	public static boolean isStringEmpty(String original){
		if(original.replaceAll("\u00a0"," ").replaceAll("&nbsp;", " ").trim().equals("")){
			return true;
		}else {
			return false;
		}
	}
	
	public static String escapeSpecial(String original) {
		return original.replaceAll("\u00a0"," ").replaceAll("&nbsp;", " ").trim();
	}

	/***
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isDescription(String text) {

		int numOfWords = TextProcessingUtils.tokenizeLabel(text.trim()).length;
		if (numOfWords > 7) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		
//		String value = "name1 or name2 and name3 for anda & kang";
//		String[] tokens = value.split("&| and | or ");
//		
//		for(String token : tokens){
//			System.out.println("--"+token.trim());
//		}
		
//		String kk = "cnn machining (machining1) / mahcining2/machining3";
//		for(String phrase : TextProcessingUtils.tokenizeLabel2PhrasesWithParallelledSemantic(kk)){
//			System.out.println(phrase);
//		}

		String[] tokens = TextProcessingUtils.tokenizeLabel("WaterjetCuttings");
		for(String token : tokens){
		System.out.println("--" + token);
		}
//		String t= "* * #; ) fd";
		
		
//		double[] scoreRecorder = new double[3];
//		scoreRecorder[0] = 3.0;
//		scoreRecorder[1] = 1.0;
//		scoreRecorder[2] = 2.0;
//		Arrays.sort(scoreRecorder);
//		
//		for(double v : scoreRecorder){
//			System.out.println("=> " + v);
//		}
//		
//		int numOfSourceTerms = 4;
//		int numOfTermFocused = (int) Math.ceil((double) numOfSourceTerms * 0.6);
//		System.out.println("=> " + numOfTermFocused);
		
//		String value1 = "Capabilities include";
//		String value2 = "Our capabilitites include";
//		String k1 = TextProcessingUtils.getProcessedLabel(value1, "");
//		String k2 = TextProcessingUtils.getProcessedLabel(value2, "");
//		System.out.println(k1);
//		System.out.println(k2);
//		
//		QGramsDistance ngramSimilarity = new QGramsDistance(new TokeniserQGram2Extended());
//		double sim = ngramSimilarity.getSimilarity(k1, k2);
//		System.out.println(sim);

	}
}