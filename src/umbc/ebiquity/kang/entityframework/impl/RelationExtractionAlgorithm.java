package umbc.ebiquity.kang.entityframework.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.IEntityGraphRelationExtractor;
import umbc.ebiquity.kang.entityframework.IRelationExtractionAlgorithm;
import umbc.ebiquity.kang.entityframework.object.EntityNode;
import umbc.ebiquity.kang.entityframework.object.EntityValidator;

public class RelationExtractionAlgorithm implements IRelationExtractionAlgorithm {

	private EntityValidator _commonValidator = new EntityValidator();
	private double clusterMergingThreshold = 0.5;
	private double relationClusteringThreshold = 0.4;

	public RelationExtractionAlgorithm() {
	}

	@Override
	public void extractRelation(IEntityGraphRelationExtractor entityGraph) {
		Collection<RelationCluster> initialRelationClusters = this.initializeRelationClusters(entityGraph);
		Collection<RelationCluster> mergedRelationClusters = this.mergeRelationClusters(initialRelationClusters);
		this.recordRelations(mergedRelationClusters, entityGraph);
	}

	private Collection<RelationCluster> initializeRelationClusters(IEntityGraphRelationExtractor entityGraph) {
		Collection<EntityNode> possibleRelations = new HashSet<EntityNode>();
		Collection<RelationCluster> relationClusters = new ArrayList<RelationCluster>();
		for (EntityNode node : entityGraph.getEntityNodes()) {

			// if this instance node is intermediate node (not leaf node)
			if (node.isIntermediateNode()) {

				Collection<EntityNode> domainTerms = entityGraph.getDirectDescendants(node);
				Collection<EntityNode> rangeTerms = entityGraph.getDirectAnscentors(node);

				int sizeOfDescendants = domainTerms.size();
				Collection<EntityNode> validateRangeTerms = _commonValidator.validateRangeTerms(rangeTerms);
				int sizeOfAncestors = validateRangeTerms.size();
				if (sizeOfDescendants > 1 && sizeOfAncestors > 1) {
					/*
					 * Create initial relation clusters with one relation. We
					 * make the assumption that if a entity has multiple
					 * descendants and ancestors at the same time, it is a
					 * relation.
					 */
					System.out.println("@INITIAL RELATION: " + node.getLabel());
					if (_commonValidator.isValidRelation(node, 7)) {
						RelationCluster relationCluster = new RelationCluster(validateRangeTerms, node.getLabel(), domainTerms);
						relationClusters.add(relationCluster);
						this.showDomainsAndRanges(domainTerms, validateRangeTerms);
					}
				} else if (sizeOfDescendants > 0 && sizeOfAncestors > 1) {
					/*
					 * Collect possible relations. We make the assumption that
					 * if a entity has multiple ancestors and more than one
					 * descendant it is possible that it is a relation.
					 */
					System.out.println("@POSSIBLE RELATION: " + node.getLabel());
					possibleRelations.add(node);
					this.showDomainsAndRanges(domainTerms, validateRangeTerms);
				}
			}
		}

		System.out.println();
		System.out.println("[START CLUSTRING RELATION ...]");
		for (EntityNode relation : possibleRelations) {
			System.out.println("---------------------------");
			System.out.println("CLUSTERING RELATION: " + relation.getLabel());

			Collection<EntityNode> domainTerms = entityGraph.getDirectDescendants(relation);
			Collection<EntityNode> rangeTerms = entityGraph.getDirectAnscentors(relation);
			Collection<EntityNode> validateRangeTerms = _commonValidator.validateRangeTerms(rangeTerms);
			RelationCluster bestCluster = null;
			double bestClusterScore = 0.0;
			for (RelationCluster relationCluster : relationClusters) {

				double similarity = relationCluster.computeBelongingness(validateRangeTerms, relation.getLabel(), domainTerms);
				System.out.println(relationCluster.getCenterRelationWordList() + " " + similarity);
				if (similarity > bestClusterScore) {
					bestCluster = relationCluster;
					bestClusterScore = similarity;
				}
			}
			
//			if (bestCluster == null) {
//				System.out.println("CREATE NEW CLUSTER: " + relation.getLabel());
//				RelationCluster relationCluster = new RelationCluster(validateRangeTerms, relation.getLabel(), domainTerms);
//				relationClusters.add(relationCluster);
//
//			} else {

				if (bestClusterScore > this.relationClusteringThreshold) {
					System.out.println("ADD TO CLUSTER: " + bestCluster.getCenterRelationWordList());
					bestCluster.addMember(validateRangeTerms, relation.getLabel(), domainTerms);
				} else {
					System.out.println("CREATE NEW CLUSTER: " + relation.getLabel());
					RelationCluster relationCluster = new RelationCluster(validateRangeTerms, relation.getLabel(), domainTerms);
					relationClusters.add(relationCluster);
				}
//			}
		}

		///////////////////
		System.out.println();
		System.out.println("CLUSTERS DETAIL");
		for (RelationCluster cluster : relationClusters) {
			cluster.showDetail();
		}
		return relationClusters;
		///////////////////
	}

	private Collection<RelationCluster> mergeRelationClusters(Collection<RelationCluster> relationClusters) {

		System.out.println();
		System.out.println("[START MERGING CLUSTRS ...]");
		Set<RelationCluster> newRelationClustetList = new HashSet<RelationCluster>();

		// trace relation clusters that have already been merged
		Set<RelationCluster> mergedRelationCluster = new HashSet<RelationCluster>();
		for (RelationCluster cluster1 : relationClusters) {
			if (mergedRelationCluster.contains(cluster1))
				continue;
			System.out.println();
			System.out.println("MERGING CLUSTER: " + cluster1.getCenterRelationWordList());
			RelationCluster cluster2 = null;
			double maxScore = 0.0;
			for (RelationCluster c : relationClusters) {
				if (cluster1.equals(c))
					continue;
				System.out.println("-----------------------");
				double score = cluster1.computeCloseness(c);
				if (score > maxScore) {
					maxScore = score;
					cluster2 = c;
				}
			}

			if (cluster2 != null) {
				if (maxScore >= this.clusterMergingThreshold) {
					System.out.println("MERGE WITH CLUSTER:    " + cluster2.getCenterRelationWordList() + " with score: " + maxScore);
					RelationCluster mergedCluster = cluster2.merge(cluster1);
					System.out.println("MERGED NEW CLUSTER:        " + mergedCluster.getCenterRelationWordList());
					System.out.println("MERGED NEW CLUSTER CENTER: " + mergedCluster.toString());
					newRelationClustetList.add(mergedCluster);
					System.out.println("REMOVE CLUSTER:        " + cluster2.toString());
					System.out.println("REMOVE CLUSTER:        " + cluster1.toString());
					mergedRelationCluster.add(cluster2.copy());
					mergedRelationCluster.add(cluster1.copy());

				} else {
					System.out.println("MERGE WITH NO CLUSTER: " + maxScore);
					newRelationClustetList.add(cluster1.copy());
				}

			} else {
				System.out.println("MERGE WITH NO CLUSTER: " + maxScore);
				newRelationClustetList.add(cluster1.copy());
			}
		}
		return newRelationClustetList;
	}

	private void recordRelations(Collection<RelationCluster> mergedRelationClusters, IEntityGraphRelationExtractor entityGraph) {
		System.out.println();
		System.out.println("CLUSTERS DETAIL AFTER MERGING");
		for (RelationCluster cluster : mergedRelationClusters) {
			if (cluster.getRangeSet().size() > 1 && cluster.getDomainSet().size() > 1) {
				cluster.showDetail();
				EntityNode reptaRelnEntityNode = new EntityNode(cluster.computeRepresentativeRelationLabel());
				for (String relationLabel : cluster.getRelationMembers()) {
					EntityNode relationEntityNode = new EntityNode(relationLabel);
					if (_commonValidator.isValidRelation(relationEntityNode, 20)) {
						entityGraph.addRelationInferredFromM2NStructure(relationEntityNode);
						entityGraph.addSpecificRelation2GeneralRelationMap(relationEntityNode, reptaRelnEntityNode);
					}
				}
				System.out.println("### Cluster: " + reptaRelnEntityNode.getLabel());
				System.out.println("Ranges: " + cluster.getRangeSet());
				System.out.println("Domains: " + cluster.getDomainSet());
				System.out.println("Center Word List: " + cluster.getCenterRelationWordList());
			}
		}
	}
	
	private void showDomainsAndRanges(Collection<EntityNode> domains, Collection<EntityNode> ranges){
		for (EntityNode domain : domains) {
			System.out.println("  d: " + domain.getLabel());
		}
		for (EntityNode range : ranges) {
			System.out.println("  r: " + range.getLabel());
		}
	}
}
