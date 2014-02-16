package umbc.ebiquity.kang.ontologyinitializator.similarity.test;

import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.OrderedWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SubSumptionRelationshipBoostingLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;

public class SimilarityAlgorithmTest {
	
	public void EqualSemanticRootWordListSimilarityTest() {
	}

	public void OrderedWordPatternWordListSimilarityTest() {
	}

	public void UnorderedWordPatternWordListSimilarityTest() {
	}

	public void EqualSemanticRootBoostingLabelSimilarityTest() {
	}

	public void SubSumptionRelationshipBoostingLabelSimilarityTest() {
	}

	public void SizeSensitiveSetSimilarityTest() {
	}

	
	
	@Test
	public void SubSumptionRelationshipBoostingLabelSimilarity(){
		ILabelSimilarity similarity = new SubSumptionRelationshipBoostingLabelSimilarity(new OrderedWordListSimilarity());
//		double sim1 = similarity.computeSimilarity("Stone Waterjet Cutting", "Water Jet Cutting");
//		double sim2 = similarity.computeSimilarity("Stone Waterjet Cutting", "WaterJetCutting");
//		double sim3 = similarity.computeSimilarity("Machining Service", "Machining Service");
		double sim4 = similarity.computeLabelSimilarity("shim materials", "Material");
//		System.out.println(sim1);
//		System.out.println(sim2);
//		System.out.println(sim3);
		System.out.println(sim4);
	}
	
	public void test(){
//		SimilarityAlgorithm alg = new SimilarityAlgorithm();
		
//		String label1 = "Materials Used";
//		String label2 = "materialUsed";
//		double sim1 = alg.getSimilarity(label1, label2);
//		System.out.println("compare " + label1 + " with " + label2);
//		System.out.println("sim1 " + sim1);
		System.out.println();
//		String label1 = "Aluminum Shims";
//		String label2 = "Aluminum";
//		System.out.println("3 compare " + label1 + " with " + label2);
//		double sim1 = alg.getSimilarity(SimilarityType.Kim_Ngram, label1, label2);
//		System.out.println("sim1 " + sim1);
//		
//		double sim2 = alg.getKimSimilarity(label1, label2);
//		System.out.println("sim2 " + sim2);
//		
//		double sim3 = alg.getNgramSimilarity(label1, label2);
//		System.out.println("sim3 " + sim3);
		
//		String instance = "Cable Assemblies & Wire Harnesses";
//		String[] tokens = instance.replaceAll("#", "").replaceAll("\\*", "").split(" and | or |&| & / |/");
//		Collection<EntityNode> instanceNodes = new HashSet<EntityNode>();
//		for(String token : tokens){
//			System.out.println(token.trim());
//			instanceNodes.add(new EntityNode(token.trim()));
//		}
//
//		CommonPhraseAnalyzer analyzer = new CommonPhraseAnalyzer();
//		for (String commonPhrase : analyzer.computeCommonPhrases(instanceNodes)) {
//			System.out.println("Com-Phrase: " + commonPhrase);
//		}
		
//		String tripleStoreURI = "www.bassettomc.com";
//		
//		int indexOfFirstPeriod = tripleStoreURI.indexOf(".");
//		String temp = tripleStoreURI.substring(indexOfFirstPeriod + 1);
//
//		System.out.println("#1 " + temp);
//		indexOfFirstPeriod = temp.indexOf(".");
//		temp = temp.substring(0, indexOfFirstPeriod);
//		
//		System.out.println("#2 " + temp);
		
//		Set<String> set2 = new LinkedHashSet<String>();
//		set2.add("Copper".toLowerCase());
//		set2.add("Brass".toLowerCase());
//		set2.add("Rubber".toLowerCase());
//		set2.add("Phenolics".toLowerCase());
//		set2.add("Nylon".toLowerCase());
//		set2.add("Plastics".toLowerCase());
//		set2.add("Carbon steel".toLowerCase());
//		set2.add("Stainless steel".toLowerCase());
//		set2.add("Spring steel".toLowerCase());
//		set2.add("Titanium".toLowerCase());
//		Set<String> set3 = new LinkedHashSet<String>();
//		set3.add("Aluminum".toLowerCase());
//		set3.add("Brass".toLowerCase());
//		set3.add("Carbon steel".toLowerCase());
//		set3.add("Nickel".toLowerCase());
//		set3.add("Inconel".toLowerCase());
//		set3.add("Hastelloy".toLowerCase());
//		set3.add("Monel".toLowerCase());
//		set3.add("Spring steel".toLowerCase());
//		System.out.println("set score: " + alg.computeSetsSimilarityByUWP(set2, set3));
		
		
//		double sim1 = alg.computeSimilarityWithEqualSemainticRootBoosting("water jet cutting", "water jet cutting service", false);
//		double sim2 = alg.computeSimilarityWithEqualSemainticRootBoosting("aerojet water jet cutting", "abstrive water jet cutting", false);
//		System.out.println(sim1);
//		System.out.println(sim2);
		
//		double sim1 = alg.computeLabelSimilarityBySubSumptionRelationshipBoosting("water jet cutting", "cutting", true);
//		double sim2 = alg.computeLabelSimilarityBySubSumptionRelationshipBoosting("wateronly waters jet cutting", "cutting", true);
//		double sim3 = alg.computeLabelSimilarityBySubSumptionRelationshipBoosting("wateronly waters jet cutting", "arep water jets cutting", true);
//		double sim4 = alg.computeLabelSimilarityBySubSumptionRelationshipBoosting("waters jet cutting", "arep water jets cutting", true);
//		double sim5 = alg.computeLabelSimilarityBySubSumptionRelationshipBoosting("waters jet cutting material", "waters jet cutting equipment", true);
		
//		double sim6 = alg.computeLabelSimilarityWithEqualSemainticRootBoosting("automated powder coating", "PowderCoating", true);
//		double sim7 = alg.computeLabelSimilarityWithSubSumptionRelationshipBoosting("Shim Stock", "StockType", true);
		
//		double sim8 = alg.computeLabelSimilarityByOrderedWordPattern("capability", "hasProcess");
//		System.out.println(sim1);
//		System.out.println(sim2);
//		System.out.println(sim3);
//		System.out.println(sim4);
//		System.out.println(sim5);
//		System.out.println(sim6);
//		System.out.println(sim7);
//		System.out.println(sim8);
		

	}
}
