package umbc.ebiquity.kang.textprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Synonyms {

	private static final Map<String, Set<Set<String>>> WORD_TO_SYNOYMYM_SETS = new HashMap<String, Set<Set<String>>>(15000);
	private static final Set<Set<String>> EMPTY_SET = Collections.unmodifiableSet(new HashSet<Set<String>>());

	static {
		synchronized (WORD_TO_SYNOYMYM_SETS) {

			String projectDir = System.getProperty("user.dir");
			// load(projectDir + "/../data/WordNet-3.0/dict/data.adj");
			// load(projectDir + "/../data/WordNet-3.0/dict/data.adv");
			// load(projectDir + "/../data/WordNet-3.0/dict/data.noun");
			// load(projectDir + "/../data/WordNet-3.0/dict/data.verb");

			load(projectDir + "/data/WordNet-3.0/dict/data.adj");
			load(projectDir + "/data/WordNet-3.0/dict/data.adv");
			load(projectDir + "/data/WordNet-3.0/dict/data.noun");
			load(projectDir + "/data/WordNet-3.0/dict/data.verb");
		}
	}

	public static void load(String[] paths) {
		for (String path : paths) {
			load(path);

		}
	}

	private static void load(String path) {

		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = new FileInputStream(path);
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = null;
			while ((line = reader.readLine()) != null) {
				processLine(line);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	private static void processLine(String line) {

		/**
		 * every line is a synonym set, so the next we should do is to extract
		 * words from each line.
		 */
		if ((line.length() > 17) && (line.charAt(0) == '0')) {

			/**
			 * the data we want starts at the 17th character; and then we
			 * extract words from this line.
			 */
			line = line.substring(17);

			Set<String> synonymSet = new HashSet<String>();
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreElements()) {
				String token = st.nextToken();

				/**
				 * When "00" appears, the left portion of the line can not
				 * contain any synonyms
				 */
				if (token.startsWith("00")) {
					break;
				}

				/**
				 * if the length of the token > 2 and the first char of the
				 * token is letter, the token is a word
				 */
				if (token.length() > 2 && Character.isLetter(token.charAt(0))) {
					// System.out.println("word: " + token);
					synonymSet.add(token);
				}
			}

			if (synonymSet.size() > 1) {
				synonymSet = Collections.unmodifiableSet(synonymSet);

				/**
				 * The synonymSet is (one of) the synonym set for every word in
				 * synonymSet
				 */
				for (String word : synonymSet) {
					Set<Set<String>> synonymSetsForThisWord = WORD_TO_SYNOYMYM_SETS.get(word);
					if (synonymSetsForThisWord == null) {
						synonymSetsForThisWord = new HashSet<Set<String>>();
						WORD_TO_SYNOYMYM_SETS.put(word, synonymSetsForThisWord);
					}
					synonymSetsForThisWord.add(synonymSet);
				}
			}
		}
	}

	public static Set<Set<String>> getSynonymSets(String word) {
		// why here using the synchronized
		synchronized (WORD_TO_SYNOYMYM_SETS) {
			Set<Set<String>> synonymSets = WORD_TO_SYNOYMYM_SETS.get(word);
			return ((synonymSets != null) ? Collections.unmodifiableSet(synonymSets) : EMPTY_SET);
		}
	}

}
