package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTest {

	public static void main(String[] args) throws IOException {

//		// Initialize the tagger
//		MaxentTagger tagger = new MaxentTagger(System.getProperty("user.dir") + "/taggers/english-left3words-distsim.tagger");
//
//		// The sample string
//		String sample1 = "abrasive water jet cutting";
//		String sample2 = "Wire EDM machining";
//		String sample3 = "Nibbling";
//		String sample4 = "Circle shearing";
//		
//		String sample5 = "Customer";
//		String sample6 = "Counter Bore Tool";
//		Sentence s;
//		// The tagged string
//		String tagged1 = tagger.tagString(sample1);
//		String tagged2 = tagger.tagString(sample2);
//		String tagged3 = tagger.tagString(sample3);
//		String tagged4 = tagger.tagString(sample4);
//		String tagged5 = tagger.tagString(sample5);
//		String tagged6 = tagger.tagString(sample6);
//		// Output the result
//		System.out.println(tagged1);
//		System.out.println(tagged2);
//		System.out.println(tagged3);
//		System.out.println(tagged4);
//		System.out.println(tagged5);
//		System.out.println(tagged6);
		
		
		
		
//		List<String> tagSeq1 = new ArrayList<String>();
//		tagSeq1.add("VBG");
//		tagSeq1.add("NN");
//		tagSeq1.add("NN");
//		tagSeq1.add("JJ");
//		List<String> tagSeq2 = new ArrayList<String>();
//		tagSeq2.add("NN");
//		tagSeq2.add("NN");
//		tagSeq2.add("NN");
//		List<String> tagSeq3 = new ArrayList<String>();
//		tagSeq3.add("VBG");
//		tagSeq3.add("NN");
//		List<String> tagSeq4 = new ArrayList<String>();
//		tagSeq4.add("VBG");
//		
//		List<List<String>> tagSeqs = new ArrayList<List<String>>();
//		tagSeqs.add(tagSeq1);
//		tagSeqs.add(tagSeq2);
//		tagSeqs.add(tagSeq3);
//		tagSeqs.add(tagSeq4);
//		
//		for(String tag : findPOSPattern(tagSeqs)){
//			System.out.print(tag + " ");
//		}
//		
//		System.out.println();
//		
//		String entityString1 = "WireEDMmachining";
//		String entityString2 = "abrasivewaterjetcutting";
//		String entityString3 = "Circleshearing";
//		
//		String entityString4 = "Thermoplastic";
//		String entityString5 = "Thermoset";
//		
//		List<String> entityStringList = new ArrayList<String>();
//		entityStringList.add(entityString1);
//		entityStringList.add(entityString2);
//		entityStringList.add(entityString3);
////		entityStringList.add(entityString4);
////		entityStringList.add(entityString5);
//		for(String subStrings : findCommonSubStrings(entityStringList)){
//			System.out.print(subStrings + ", ");
//		}
//		
//		List<String> wordSeq1 = new ArrayList<String>();
//		wordSeq1.add("machining");
//		wordSeq1.add("EDM");
//		wordSeq1.add("Wire");
//		List<String> wordSeq2 = new ArrayList<String>();
//		wordSeq2.add("cutting");
//		wordSeq2.add("waterjet");
//		wordSeq2.add("abrasive");
//		List<String> wordSeq3 = new ArrayList<String>();
//		wordSeq3.add("cutting");
//		wordSeq3.add("beam");
//		wordSeq3.add("laser");
//		List<String> wordSeq4 = new ArrayList<String>();
//		wordSeq4.add("cutting");
//		wordSeq4.add("plasma");
//		List<String> wordSeq5 = new ArrayList<String>();
//		wordSeq5.add("Bonding");
//		wordSeq5.add("adhesive");
//		wordSeq5.add("flexible");
//		List<String> wordSeq6 = new ArrayList<String>();
//		wordSeq6.add("Bonding");
//		wordSeq6.add("adhesive");
//		wordSeq6.add("rigid");
//		List<String> wordSeq7 = new ArrayList<String>();
//		wordSeq5.add("prototyping");
//		wordSeq5.add("based");
//		wordSeq5.add("deposition");
//		List<String> wordSeq8 = new ArrayList<String>();
//		wordSeq6.add("prototyping");
//		wordSeq6.add("based");
//		wordSeq6.add("laser");
//		
//		List<List<String>> wordSeqs = new ArrayList<List<String>>();
//		wordSeqs.add(wordSeq1);
//		wordSeqs.add(wordSeq2);
//		wordSeqs.add(wordSeq3);
//		wordSeqs.add(wordSeq4);
//		wordSeqs.add(wordSeq5);
//		wordSeqs.add(wordSeq6);
//		wordSeqs.add(wordSeq7);
//		wordSeqs.add(wordSeq8);
//		
//		System.out.println();
//		
//		constructWordTree(wordSeqs);
//		for(String phaseOneKey : phaseOneMap.keySet()){
//			System.out.println(phaseOneKey);
//			Set<String> words = phaseOneMap.get(phaseOneKey);
//			if(words != null){
//				for(String w1: words){
//					System.out.println("    " + w1);
//					Set<String> words2 = phaseTwoMap.get(w1);
//					for(String w2: words2){
//						System.out.println("        " + w2);
//					}
//				}
//			}
//		}
		
		
//		test();
		testDeriveConcepts();
	}

	public static void testDeriveConcepts() {
		Collection<String> originalConcepts1 = new ArrayList<String>();
		originalConcepts1.add("specialties shims");
		originalConcepts1.add("shim materials");
		originalConcepts1.add("gearbox shims");
		originalConcepts1.add("materials");
		originalConcepts1.add("aluminum");
		originalConcepts1.add("laser cutting");
		originalConcepts1.add("steals shim");
		originalConcepts1.add("adjustable shims");
		originalConcepts1.add("cuc laser cutting");
		
		Collection<String> originalConcepts2 = new ArrayList<String>();
		originalConcepts2.add("nickel shims");
		originalConcepts2.add("laser cutting");
		originalConcepts2.add("capabilities");
		originalConcepts2.add("sheet metal fabrication");

		System.out.println();
		Set<String> set1 = deriveConcepts(originalConcepts1);
		for (String concept : set1) {
			System.out.println(concept);
		}
		
		System.out.println();
		Set<String> set2 = deriveConcepts(originalConcepts2);
		for (String concept : set2) {
			System.out.println(concept);
		}
		
		System.out.println();
		ArrayList<Set<String>> sets = new ArrayList<Set<String>>();
		sets.add(set1);
		sets.add(set2);
		for(String concept : calculateConceptsFrequencies(sets).keySet()){
			System.out.println(concept + "  " + calculateConceptsFrequencies(sets).get(concept));
		}
	}

	public static void test() throws IOException{ 
		
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/kangyan2003/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/kangyan2003/Desktop/Ontology/MSDL-Fullv2.owl";
		IOntologyRepository ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		List<List<String>> wordSequences = new ArrayList<List<String>>();
		List<List<String>> tagSequences = new ArrayList<List<String>>();
		List<String> entityStringList = new ArrayList<String>();
		OntoClassInfo ontClassInfo = new OntoClassInfo("http://www.umbc.edu/ebiquity/msdl.owl#Process", "http://www.umbc.edu/ebiquity/msdl.owl", "Process");
		for (OntoClassInfo c : ontologyRepository.getOntClassesInClassHierarchy(ontClassInfo)) {
			List<String> wordSeq = new ArrayList<String>();
			String className = c.getOntClassName();
			String[] tokens = TextProcessingUtils.getTokensWithStemming(className);
			for (int i = tokens.length - 1; i >= 0; i--) {
				wordSeq.add(tokens[i]);
			}
			wordSequences.add(wordSeq);
			String classLabel = TextProcessingUtils.getProcessedLabelWithStemming(className," ");
			entityStringList.add(classLabel);
		}
		
		Map<String ,String> subStrings = findCommonSubStrings(entityStringList);
		for(String subString : subStrings.keySet()){
			System.out.println(subString + ", " + subStrings.get(subString));
		}
		
		MaxentTagger tagger = new MaxentTagger(System.getProperty("user.dir") + "/taggers/english-left3words-distsim.tagger");
		for(List<String> wordSeq : wordSequences){
			List<TaggedWord> tSentence = tagger.tagSentence(Sentence.toUntaggedList(wordSeq));
			List<String> tagSeq = new ArrayList<String>();
			for(TaggedWord taggedWord: tSentence){
				tagSeq.add(taggedWord.tag());
			}
//			System.out.println(Sentence.listToString(tSentence)); 
			tagSequences.add(tagSeq);
		}
		
		System.out.println();
		for (String tag : findPOSPattern(tagSequences)) {
			System.out.print(tag + " ");
		}
		
		System.out.println();
		constructWordTree(wordSequences);
		for(String phaseOneKey : phaseOneMap.keySet()){
			System.out.println(phaseOneKey);
			Set<String> words = phaseOneMap.get(phaseOneKey);
			if(words != null){
				for(String w1: words){
					System.out.println("    " + w1);
					Set<String> words2 = phaseTwoMap.get(w1);
					for(String w2: words2){
						System.out.println("        " + w2);
					}
				}
			}
		}
	}
	
	
	public static List<String> findPOSPattern(List<List<String>> tagSequences) {
		List<String> tagPattern = new ArrayList<String>();
		Map<Integer, Map<String, Integer>> tagSeqMap = new LinkedHashMap<Integer, Map<String, Integer>>();
		int seqCount = 0;
		for (List<String> tagSeq : tagSequences) {
			int i = 1;
			
			if(tagSeq.size() > seqCount){
				seqCount = tagSeq.size();
			}
			
			for (String tag : tagSeq) {
				if (tag.startsWith("JJ")) {
					tag = "JJ";
				} else if (tag.startsWith("NN")) {
					tag = "NN";
				} else if (tag.startsWith("PRP")) {
					tag = "PRP";
				} else if (tag.startsWith("RB")) {
					tag = "RB";
				} else if (tag.startsWith("WP")) {
					tag = "WP";
				}
				
				Map<String, Integer> tagCountMap = tagSeqMap.get(i);
				if (tagCountMap == null) {
					tagCountMap = new HashMap<String, Integer>();
				}
				tagSeqMap.put(i, tagCountMap);

				if (tagCountMap.get(tag) == null) {
					tagCountMap.put(tag, 1);
				} else {
					int tagCount = tagCountMap.get(tag);
					tagCountMap.put(tag, tagCount + 1);
				}
				i++;
			}
		}	
		
		for (Map<String, Integer> tagCountMap : tagSeqMap.values()) {
			int maxCount = 0;
			String mostFreqTag = "";
			for (String tag : tagCountMap.keySet()) {
				int count = tagCountMap.get(tag);
				System.out.print(tag + " " + (double) count / (double) tagSequences.size() + "   ");
				if(count > maxCount){
					maxCount = count;
					mostFreqTag = tag;
				}
			}
			
			System.out.println();
			double support = (double) maxCount / (double) seqCount;
			System.out.println(mostFreqTag + " " + (double) maxCount / (double) tagSequences.size());
			if (support >= 0.5) {
				tagPattern.add(mostFreqTag);
			}
		}
		return tagPattern;
	}

	public static Map<String, String> findCommonSubStrings(List<String> entityStringList) {
		Map<String, Integer> substring2CountMap = new HashMap<String, Integer>();
		Map<String, Double> subString2ConfidenceMap = new HashMap<String, Double>();
		Map<String, Set<String>> subString2SourcesMap = new HashMap<String, Set<String>>();

		int entityStringListSize = entityStringList.size();
		for (int i = 0; i < entityStringListSize; i++) {
			char[] entityStringCharArray1 = entityStringList.get(i).toCharArray();
			for (int j = i + 1; j < entityStringListSize; j++) {
//				System.out.println(entityStringList.get(i)+ " <-> " + entityStringList.get(j));
				char[] entityStringCharArray2 = entityStringList.get(j).toCharArray();
				int lengthOfArray1 = entityStringCharArray1.length;
				int lengthOfArray2 = entityStringCharArray2.length;
				int length = lengthOfArray1 < lengthOfArray2 ? lengthOfArray1 : lengthOfArray2;

				int count = 0;
				for (int z = 0; z < length; z++) {
					if (entityStringCharArray1[z] == entityStringCharArray2[z] && entityStringCharArray1[z] != ' ') {
						count++;
					} else {
						break;
					}
				}
				
				// TODO: refactor this !!!
				if (count > 1) {
					String subStringB = String.valueOf(entityStringCharArray1, 0, count) + "@";
//					System.out.println("subStringB:" + subStringB);
					if (substring2CountMap.get(subStringB) == null) {
						/*
						 * 
						 */
						double confidence1 = (double) count / (double) lengthOfArray1;
						double confidence2 = (double) count / (double) lengthOfArray2;
						double avg = (confidence1 + confidence2) / 2;
						substring2CountMap.put(subStringB, 2);
						subString2ConfidenceMap.put(subStringB, avg);
						
						Set<String> sources = new HashSet<String>();
						sources.add(entityStringList.get(i));
						sources.add(entityStringList.get(j));
						subString2SourcesMap.put(subStringB, sources);
					} else {

						Set<String> sources = subString2SourcesMap.get(subStringB);
						int repetition = substring2CountMap.get(subStringB);
						double confidence = subString2ConfidenceMap.get(subStringB);
						if (!sources.contains(entityStringList.get(i)) && !sources.contains(entityStringList.get(j))) {
							int updatedRepetition = repetition + 2;
							double confidence1 = (double) count / (double) lengthOfArray1;
							double confidence2 = (double) count / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence1 + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(entityStringList.get(i));
							sources.add(entityStringList.get(j));
						} else if (!sources.contains(entityStringList.get(i))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) count / (double) lengthOfArray1;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(entityStringList.get(i));
						} else if (!sources.contains(entityStringList.get(j))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) count / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringB, updatedRepetition);
							subString2ConfidenceMap.put(subStringB, avg);
							sources.add(entityStringList.get(j));
						}

					}
				}

				count = 0;
				for (int x = lengthOfArray1 - 1, y = lengthOfArray2 - 1; x >= 0 && y >= 0; x--, y--) {
					if (entityStringCharArray1[x] == entityStringCharArray2[y] && entityStringCharArray1[x] != ' ') {
//						System.out.println("## " + entityStringCharArray1[x]);
						count++;
					} else {
						break;
					}
				}

				if (count > 1) {
					String subStringE = "@" + String.valueOf(entityStringCharArray1, entityStringCharArray1.length - count, count);
//					System.out.println("subStringE:" + subStringE + ", " + count);
					if (substring2CountMap.get(subStringE) == null) {
						double confidence1 = (double) count / (double) lengthOfArray1;
						double confidence2 = (double) count / (double) lengthOfArray2;
						double avg = (confidence1 + confidence2) / 2;
						substring2CountMap.put(subStringE, 2);
						subString2ConfidenceMap.put(subStringE, avg);
						
						Set<String> sources = new HashSet<String>();
						sources.add(entityStringList.get(i));
						sources.add(entityStringList.get(j));
						subString2SourcesMap.put(subStringE, sources);
					} else {
						
						Set<String> sources = subString2SourcesMap.get(subStringE);
						int repetition = substring2CountMap.get(subStringE);
						double confidence = subString2ConfidenceMap.get(subStringE);
						if (!sources.contains(entityStringList.get(i)) && !sources.contains(entityStringList.get(j))) {
							int updatedRepetition = repetition + 2;
							double confidence1 = (double) count / (double) lengthOfArray1;
							double confidence2 = (double) count / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence1 + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(entityStringList.get(i));
							sources.add(entityStringList.get(j));
						} else if (!sources.contains(entityStringList.get(i))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) count / (double) lengthOfArray1;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(entityStringList.get(i));
						} else if (!sources.contains(entityStringList.get(j))) {
							int updatedRepetition = repetition + 1;
							double confidence2 = (double) count / (double) lengthOfArray2;
							double avg = (confidence * repetition + confidence2) / updatedRepetition;
							substring2CountMap.put(subStringE, updatedRepetition);
							subString2ConfidenceMap.put(subStringE, avg);
							sources.add(entityStringList.get(j));
						}
						
					}
				}

			}
		}
		
		Map<String, String> subString2StatisticsMap = new HashMap<String, String>();
		for(String subString : substring2CountMap.keySet()){
			int count = substring2CountMap.get(subString);
			double confidence = subString2ConfidenceMap.get(subString);
			double support = (double) count / (double) entityStringListSize;
			String statistics = count +"/"+confidence+"/"+support;
			subString2StatisticsMap.put(subString, statistics);
//			System.out.println(subString + " ,count:" + count + " ,confidence:" + confidence + " ,support:" + support);
			
		}

		return subString2StatisticsMap;
	}
	
	static Map<String, Set<String>> phaseOneMap = new HashMap<String, Set<String>>();
	static Map<String, Set<String>> phaseTwoMap = new HashMap<String, Set<String>>();
	public static void constructWordTree(List<List<String>> wordSequences) {

		for (List<String> wordSequence : wordSequences) {
			for (int i = 0; i < wordSequence.size(); i++) {

				if (i == 0) {
					constructFollowingWordSet(phaseOneMap, wordSequence, i);
				} else {
					constructFollowingWordSet(phaseTwoMap, wordSequence, i);
				}
			}
		}
	}

	public static void constructFollowingWordSet(Map<String, Set<String>> followingWordMap, List<String> wordSequence, int index) {
		Set<String> wordSet = followingWordMap.get(wordSequence.get(index));
		if (wordSet == null) {
			wordSet = new HashSet<String>();
			followingWordMap.put(wordSequence.get(index), wordSet);
		}

		if (index + 1 < wordSequence.size()) {
			wordSet.add(wordSequence.get(index + 1));
		}
	}

	public static Set<String> deriveConcepts(Collection<String> originalConcepts) {
		Map<String, Integer> superConceptSet = new HashMap<String, Integer>();
		Set<String> allConceptSet = new LinkedHashSet<String>();
		for (String concept : originalConcepts) {
			String[] tokens = TextProcessingUtils.tokenizeLabel2Array(concept, true, false, 0);
			StringBuilder superConceptBuilder = new StringBuilder("");
			for (int i = tokens.length - 1; i >= 0; i--) {
				superConceptBuilder.insert(0, " " + tokens[i]);
				String superConcept = superConceptBuilder.toString().trim();
				if (superConceptSet.get(superConcept) == null) {
					superConceptSet.put(superConceptBuilder.toString().trim(), 1);
				} else {
					int count = superConceptSet.get(superConcept);
					superConceptSet.put(superConcept, ++count);
				}
			}
			allConceptSet.add(superConceptBuilder.toString().trim());
		}
		for (String superConcept : superConceptSet.keySet()) {
			int count = superConceptSet.get(superConcept);
			if (count > 1) {
				allConceptSet.add(superConcept);
			}
		}
		return allConceptSet;
	}
	
	public static Map<String, String> calculateConceptsFrequencies(Collection<Set<String>> conceptSets) {
		Map<String, String> concept2FreqMap = new HashMap<String, String>();
		int totalConceptCount = 0;
		for (Set<String> conceptSet : conceptSets) {
			for (String concept : conceptSet) {
				totalConceptCount++;
				if (concept2FreqMap.containsKey(concept)) {
					int freq = Integer.valueOf(concept2FreqMap.get(concept));
					concept2FreqMap.put(concept, String.valueOf(++freq));
				} else {
					concept2FreqMap.put(concept, String.valueOf(1));
				}
			}
		}

		for (String concept : concept2FreqMap.keySet()) {
			concept2FreqMap.put(concept, concept2FreqMap.get(concept) + "|" + totalConceptCount);
		}
		return concept2FreqMap;

	}

}
