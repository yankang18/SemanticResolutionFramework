package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.EqualSemanticRootBoostingLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.OrderedWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SimpleLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SizeSensitiveSetSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ISetSimilarity;

public class Relation2PropertyMapper implements IRelation2PropertyMapper{

	/**
	 * algorithm computes the similarity between two labels. This algorithm
	 * combines several (current two) similarity computing method
	 */
//	private SimilarityAlgorithm similarityAlg = new SimilarityAlgorithm();
	private ILabelSimilarity labelSimilarity;
	private ISetSimilarity setSimilarity;
	
	public Relation2PropertyMapper(){
		labelSimilarity = new SimpleLabelSimilarity(new OrderedWordListSimilarity());
		setSimilarity = new SizeSensitiveSetSimilarity(new EqualSemanticRootBoostingLabelSimilarity(new OrderedWordListSimilarity(), true));
	}
	
	@Override
	public MSMResult matchRelations2OntProperties(Collection<OntPropertyInfo> sPropertyCollection, Collection<OntPropertyInfo> tPropertyCollection) {
		return this.matchRelations2OntProperties(sPropertyCollection.toArray(new OntPropertyInfo[0]), tPropertyCollection.toArray(new OntPropertyInfo[0]));
	}
	
	private MSMResult matchRelations2OntProperties(OntPropertyInfo[] sPropertyNodeSet, OntPropertyInfo[] tPropertyNodeSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sPropertyNodeSet.length];
		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sPropertyNodeSet[i].getLocalName());
			slabelSet[i].setRevisedName(sPropertyNodeSet[i].getRevisedLocalName());
			slabelSet[i].setURI(sPropertyNodeSet[i].getURI());
			slabelSet[i].setNameSpace(sPropertyNodeSet[i].getNamespace());
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tPropertyNodeSet.length];
		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tPropertyNodeSet[j].getLocalName());
			tlabelSet[j].setRevisedName(tPropertyNodeSet[j].getRevisedLocalName());
			tlabelSet[j].setURI(tPropertyNodeSet[j].getURI());
			tlabelSet[j].setNameSpace(tPropertyNodeSet[j].getNamespace());
		}
		return matchRelations2OntProperties(sPropertyNodeSet, slabelSet, tPropertyNodeSet, tlabelSet);
	}

	/***
	 * This is the core algorithm to compute the similarity between the
	 * properties two classes.
	 * 
	 * @param sPropertyNodeSet
	 * @param sPropertyLabelSet
	 * @param tPropertyNodeSet
	 * @param tPropetyLabelSet
	 * @return
	 */
	private MSMResult matchRelations2OntProperties(OntPropertyInfo[] sPropertyNodeSet, SimilarityMatrixLabel[] sPropertyLabelSet, OntPropertyInfo[] tPropertyNodeSet,
			SimilarityMatrixLabel[] tPropetyLabelSet) {
		
		SimilarityMatrix sm = new SimilarityMatrix(sPropertyNodeSet.length, tPropertyNodeSet.length);
		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			sm.setCol(j, tPropetyLabelSet[j]);
		}
		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			sm.setRow(i, sPropertyLabelSet[i]);

			for (int j = 0; j < tPropertyNodeSet.length; j++) {

				boolean isDatatypeProperty = false;
				if (tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty) {
					isDatatypeProperty = true;
				}
				
				System.out.println();
				System.out.println("Comparing " + sPropertyNodeSet[i].getLocalName() + " with " + tPropertyNodeSet[j].getLocalName());
//				double relationLabelSimilarity = similarityAlg.computeLabelSimilarityByOrderedWordPattern(sPropertyNodeSet[i].getLocalName(), tPropertyNodeSet[j].getLocalName());
				double relationLabelSimilarity = labelSimilarity.computeLabelSimilarity(sPropertyNodeSet[i].getLocalName(), tPropertyNodeSet[j].getLocalName());
				System.out.println("- Label Sim: " + relationLabelSimilarity);
				
				double overallSimilarity = relationLabelSimilarity;
//				if (relationLabelSimilarity >=  this.differenceThreshold) {
				
					OntResourceInfo[] sSubjectCandidates = sPropertyNodeSet[i].getSubjectCandidatesAsArray();
					OntResourceInfo[] sObjectCandidates = sPropertyNodeSet[i].getObjectCandidatesAsArray();
					OntResourceInfo[] tSubjectCandidates = tPropertyNodeSet[j].getSubjectCandidatesAsArray();
					OntResourceInfo[] tObjectCandidates = tPropertyNodeSet[j].getObjectCandidatesAsArray();

					boolean domainSetsCompared = false;
					double simOfDomainSets = 0.0;
					if (sSubjectCandidates.length != 0 && tSubjectCandidates.length != 0) {
						domainSetsCompared = true;
						simOfDomainSets = this.computeSetsSimilarity(sSubjectCandidates, tSubjectCandidates);
						System.out.println("- Subject Sim: " + simOfDomainSets);
					}

					boolean rangeSetsCompared;
					double simOfRangeSets = 0.0;
					if (isDatatypeProperty) {
						rangeSetsCompared = false;
					} else {
						rangeSetsCompared = false;
						if (sObjectCandidates.length != 0 && tObjectCandidates.length != 0) {
							rangeSetsCompared = true;
							simOfRangeSets = this.computeSetsSimilarity(sObjectCandidates, tObjectCandidates);
							System.out.println("- Object Sim: " + simOfRangeSets);
						}
					}

					// combination of the three similarities
					overallSimilarity = this.combineRelationSimilarities(relationLabelSimilarity, simOfRangeSets, simOfDomainSets, rangeSetsCompared, domainSetsCompared);
//				}
				System.out.println("- Overall Sim: " + overallSimilarity);
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), overallSimilarity);
				cell.setSourcePropertyType(sPropertyNodeSet[i].getPropertyType());
				cell.setTargetPropertyType(tPropertyNodeSet[j].getPropertyType());
				cell.setSourceRangeSize(sPropertyNodeSet[i].getAllRangeClasses().size());
				cell.setTargetRangeSize(tPropertyNodeSet[j].getAllRangeClasses().size());
				sm.setCellAt(i, j, cell);
			}
		}

//		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getLabelMapCardinality());
		MSMUnordered msm = new MSMUnordered();
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}

	/***
	 * match two set of terms (i.e., domain terms or range terms)
	 * 
	 * @param sTerms - a set of terms from the source
	 * @param tTerms - a set of terms from the target
	 * @return similarity between the two sets of terms
	 */
	private double computeSetsSimilarity(OntResourceInfo[] sTerms, OntResourceInfo[] tTerms) {

		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();

		for (OntResourceInfo r : sTerms) {
			set1.add(r.getLocalName().toLowerCase());
		}
		for(OntResourceInfo r : tTerms){
			set2.add(r.getLocalName());
		}
		return setSimilarity.computeSimilarity(set1, set2);
//		return similarityAlg.computeSetSimilarityWithEqualSemanticRootBoosting(set1, set2, true);
	}
	
	private double combineRelationSimilarities(double relationLabelSimilarity, double rangeSetSimilarity, double domainSetSimilarity, boolean isRangeSetCompared, boolean isDomainSetCompared){
		
		System.out.println("* Before A: l" + relationLabelSimilarity + ", r" + rangeSetSimilarity + ", d" + domainSetSimilarity);
		relationLabelSimilarity = this.sigmoidAdjustment(relationLabelSimilarity, 8, 0.6);
		rangeSetSimilarity = this.sigmoidAdjustment(rangeSetSimilarity, 10 , 0.2);
		domainSetSimilarity = this.sigmoidAdjustment(domainSetSimilarity, 4, 0.3);
		System.out.println("* After A: l" + relationLabelSimilarity + ", r" + rangeSetSimilarity + ", d" + domainSetSimilarity);
		double overallSimilarity = relationLabelSimilarity;
		// combination of the three similarities
		if (isRangeSetCompared && isDomainSetCompared) {
			if (rangeSetSimilarity >= relationLabelSimilarity) {
				overallSimilarity = 0.3 * relationLabelSimilarity + 0.7 * rangeSetSimilarity ;
//				+ 0.1 * domainSetSimilarity;
			} else {
				overallSimilarity = 0.7 * relationLabelSimilarity + 0.3 * rangeSetSimilarity  ;
//				+ 0.1 * domainSetSimilarity;
			}

		} else if (isDomainSetCompared) {
			if (relationLabelSimilarity > domainSetSimilarity) {
				overallSimilarity = 0.8 * relationLabelSimilarity + 0.2 * domainSetSimilarity;
			} else {
				overallSimilarity = 0.6 * relationLabelSimilarity + 0.4 * domainSetSimilarity;
			}
		} else if (isRangeSetCompared) {
			if (relationLabelSimilarity > rangeSetSimilarity) {
				overallSimilarity = 0.7 * relationLabelSimilarity + 0.3 * rangeSetSimilarity;
			} else {
				overallSimilarity = 0.3 * relationLabelSimilarity + 0.7 * rangeSetSimilarity;
			}
		} 
		return overallSimilarity;
	}
	
	private double sigmoidAdjustment(double value, double alpha, double threshold) {
		if(value == 0.0) return 0.0;
		return 1 / (1 + Math.pow(Math.E, - alpha * (value - threshold)));
	}
}
