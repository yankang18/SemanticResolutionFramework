package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceLexicalFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.NaiveBaysianBasedCorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.ICorrectionRule;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.Debugger;

public class ClassificationCorrectionRuleGenerator {
	
	private IOntologyRepository _ontologyRepository;
	private IClassificationCorrectionRepository _classificationCorrectionRepository;
	private ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator;
	private IInstanceConcept2OntClassMappingFeatureExtractor _instanceConcept2OntClassMappingFeatureExtractor;
	private IInstanceLexicalFeatureExtractor _instanceLexicalFeatureExtractor;
	private Map<String, ClassificationCorrectionCluster> _clusters;
	private Map<String, Collection<ClassificationCorrectionCluster>> _sourceClass2CorrectionCluster;
	
	public ClassificationCorrectionRuleGenerator(IClassificationCorrectionRepository classificationCorrectionRepository,
			                                     IOntologyRepository ontologyRepository,
			                                     ICorrectionClusterCodeGenerator correctionClusterCodeGenerator,
			                                     IInstanceConcept2OntClassMappingFeatureExtractor instanceConcept2OntClassMappingFeatureExtractor,
			                                     IInstanceLexicalFeatureExtractor instanceLexicalFeatureExtractor){
		_classificationCorrectionRepository = classificationCorrectionRepository;
		_ontologyRepository = ontologyRepository;
		_correctionClusterCodeGenerator = correctionClusterCodeGenerator;
		_instanceConcept2OntClassMappingFeatureExtractor = instanceConcept2OntClassMappingFeatureExtractor;
		_instanceLexicalFeatureExtractor = instanceLexicalFeatureExtractor;
		_clusters = new HashMap<String, ClassificationCorrectionCluster>();
		_sourceClass2CorrectionCluster = new HashMap<String, Collection<ClassificationCorrectionCluster>>();
		this.formCorrectionClusters();
	}

//	public void constructClassificationCorrectionRules() {
//		this.formCorrectionClusters();
//		this.extractClusterFeatures();
//	}
	
	/**
	 * 
	 * @param instanceLabel
	 * @param ontClassLabel
	 * @return
	 */
	public ICorrectionRule getClassificationCorrectionRule(IClassifiedInstanceDetailRecord instance) {
		System.out.println("------------------");
		System.out.println("CREATE CORRECTION RULE FOR INSTANCE: <" + instance.getInstanceLabel() + "> of type <" + instance.getOntoClassName() +">");
		String sourceClassName = instance.getOntoClassName();
//		Collection<IClassificationCorrectionRule> correctionRules = new ArrayList<IClassificationCorrectionRule>();
		Collection<CorrectionClusterFeatureWrapper> correctionClusterFeatureWrappers = new ArrayList<CorrectionClusterFeatureWrapper>();
		
//		Collection<ClassificationCorrectionCluster> clusters = _sourceClass2CorrectionCluster.get(sourceClassName);
		Collection<ClassificationCorrectionCluster> correctionClusters = new HashSet<ClassificationCorrectionCluster>();
		for (String superClassName : _ontologyRepository.getUpwardCotopy(sourceClassName)) {
			Collection<ClassificationCorrectionCluster> clusters = _sourceClass2CorrectionCluster.get(superClassName);
			if (clusters != null) {
				correctionClusters.addAll(clusters);
			}
		}

		if (correctionClusters.size() == 0) return NaiveBaysianBasedCorrectionRule.createInstance(correctionClusterFeatureWrappers);
		
//		// FOR TEST ONLY
//		for (ClassificationCorrectionCluster cluster : clusters) {
//			System.out.println("--- Cluster: " + cluster.getDirectionSourceClassSet() + "->" + cluster.getDirectionTargetClass());
//			for (IClassificationCorrection correction : cluster.getCorrections()) {
//				System.out.println("  --- Correction: " + correction.getCorrectionRepresentationCode());
//			}
//		}
		
		Set<String> targetClassSet = new HashSet<String>();
//		for (ClassificationCorrectionCluster correctionCluster : clusters) {
		for (ClassificationCorrectionCluster correctionCluster : correctionClusters) {
			correctionCluster.extractFeatures();
			targetClassSet.add(correctionCluster.getDirectionTargetClass());
//			correctionRules.add(correctionCluster.toCorrectionRule());
			correctionClusterFeatureWrappers.add(correctionCluster.getCorrectionClusterFeature());
		}
		
		correctionClusterFeatureWrappers.add(this.createReservedCorrectionCluster(targetClassSet, sourceClassName));
		return NaiveBaysianBasedCorrectionRule.createInstance(correctionClusterFeatureWrappers); 
	}
	
	private CorrectionClusterFeatureWrapper createReservedCorrectionCluster(Set<String> sourceClassSet, String targetClass){
		
		CorrectionDirection correctionDirection = new CorrectionDirection(sourceClassSet, targetClass);
		ClassificationCorrectionCluster reservedcluster = new ClassificationCorrectionCluster("", 
				  																			  correctionDirection, 
				  																			  _instanceLexicalFeatureExtractor,
				  																			  _instanceConcept2OntClassMappingFeatureExtractor
				  																			  );
		reservedcluster.extractFeatures();
		return reservedcluster.getCorrectionClusterFeature();
//		return reservedcluster.toCorrectionRule();
		
	}
	
	private void formCorrectionClusters() {
		for (IClassificationCorrection correction : _classificationCorrectionRepository.getClassificationCorrections()) {
			OntoClassInfo sourceClass = correction.getCorrectionSourceClass();
			OntoClassInfo targetClass = correction.getCorrectionTargetClass();
			
			System.out.println(correction.getCorrectionRepresentationCode());
			
			// create the correction-cluster code which uniquely identify each correction cluster
			String correctionClusterCode = _correctionClusterCodeGenerator.generateCorrectionClusterCode(new CorrectionDirection(sourceClass, targetClass));
			if (_clusters.containsKey(correctionClusterCode)) {
				_clusters.get(correctionClusterCode).addCorrection(correction);
			} else {
				
				CorrectionDirection correctionDirection = new CorrectionDirection(sourceClass, targetClass);
				ClassificationCorrectionCluster cluster = new ClassificationCorrectionCluster(correctionClusterCode, 
																							  correctionDirection, 
						                                                                      _instanceLexicalFeatureExtractor,
						                                                                      _instanceConcept2OntClassMappingFeatureExtractor
						                                                                      );
				cluster.addCorrection(correction);
				_clusters.put(correctionClusterCode, cluster);
				
				Collection<ClassificationCorrectionCluster> correctionClusterCollection = _sourceClass2CorrectionCluster.get(sourceClass.getOntClassName());
				if(correctionClusterCollection == null){
					correctionClusterCollection = new ArrayList<ClassificationCorrectionCluster>();
				}
				correctionClusterCollection.add(cluster);

				// group correction-clusters that have the same source class.
				// Corrections in the same group (with the same source class
				// label) was made by domain expert to correct instances that
				// had been mis-classified into the same class.
				_sourceClass2CorrectionCluster.put(sourceClass.getOntClassName(), correctionClusterCollection);
			}
		}
	}
	
//	private String getSouceClassClusterCode(String sourceClassName){
//		
//		return "";
//	}
	
	public void extractClusterFeatures() {
		for(String clusterCode : _clusters.keySet()){
			_clusters.get(clusterCode).extractFeatures();
			_clusters.get(clusterCode).showFeatures();
		}
		System.out.println();
		System.out.println("Number of Clusters: " + _clusters.size());
	}
	
	public void showClassificationCorrectionRules() {
		Debugger.print("", "");
		Debugger.print("### CORRECTION CLUSTER", "");
		for (String className : _sourceClass2CorrectionCluster.keySet()) {
			System.out.println("Class: " + className);
			for (ClassificationCorrectionCluster cluster : _sourceClass2CorrectionCluster.get(className)) {
				System.out.println("--- Cluster: " + cluster.getDirectionSourceClassSet() + "->" + cluster.getDirectionTargetClass());
				for(IClassificationCorrection correction : cluster.getCorrections()){
					System.out.println("  --- Correction: " + correction.getCorrectionRepresentationCode());
				}
			}
		}
	}
	
}
