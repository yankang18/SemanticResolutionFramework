package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class InstanceConcept2OntClassMappingFeatureExtractor implements IInstanceConcept2OntClassMappingFeatureExtractor {
	
	private IOntologyRepository _ontologyRepository;
	private Map<IInstanceClassificationEvidence, Integer> _allC2CMapping2Frequency;
	private Map<IInstanceClassificationEvidence, Integer> _positiveC2CMapping2Frequency;
	private Map<IInstanceClassificationEvidence, Integer> _negativeC2CMapping2Frequency;
	private Map<IInstanceClassificationEvidence, Double> _positiveC2CMapping2Accuracy;
	private Map<IInstanceClassificationEvidence, Double> _negativeC2CMapping2Accuracy;
	private Map<String, List<IInstanceClassificationEvidence>> _correctionCluster2NegativeC2CMappingMembers;
	private Map<String, Map<IInstanceClassificationEvidence, Integer>> _correctionCluster2NegativeC2CMappingMembersWithFrequency;
	private Map<String, Map<IInstanceClassificationEvidence, Double>> _correctionCluster2NegativeC2CMappingMembersWithRate;
	private Map<String, Map<IInstanceClassificationEvidence, Double>> _ontClass2PositiveC2CMappingsWithAccuracy;
	private Map<String, Map<IInstanceClassificationEvidence, Double>> _ontClass2NegativeC2CMappingsWithAccuracy; 
	private Map<String, Map<IInstanceClassificationEvidence, Double>> _ontClass2ExplicitEvidencesWithLocalRate;
	private Map<String, Map<IInstanceClassificationEvidence, Double>> _ontClass2HiddenEvidencesWithLocalRate;
	private IClassificationCorrectionRepository _classificationCorrectionRepository;
	private ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator;
	
	private Map<String, List<IInstanceClassificationEvidence>> _ontClass2PositiveC2CMappingMembers;
	private Map<String, List<IInstanceClassificationEvidence>> _ontClass2NegativeC2CMappingMembers;
	
	public InstanceConcept2OntClassMappingFeatureExtractor(
			ICorrectionClusterCodeGenerator correctionClusterCodeGenerator,
			IClassificationCorrectionRepository classificationCorrectionRepository,
			IOntologyRepository ontologyRepository
	){
		_correctionClusterCodeGenerator = correctionClusterCodeGenerator;
		_classificationCorrectionRepository = classificationCorrectionRepository;
		_ontologyRepository = ontologyRepository;
		this.init();
		this.computeMappingStatistics();
	}

	private void init() {
		_allC2CMapping2Frequency = new HashMap<IInstanceClassificationEvidence, Integer>();
		_positiveC2CMapping2Frequency = new HashMap<IInstanceClassificationEvidence, Integer>();
		_negativeC2CMapping2Frequency = new HashMap<IInstanceClassificationEvidence, Integer>();
		_positiveC2CMapping2Accuracy = new HashMap<IInstanceClassificationEvidence, Double>();
		_negativeC2CMapping2Accuracy = new HashMap<IInstanceClassificationEvidence, Double>();
		
		_ontClass2PositiveC2CMappingMembers = new HashMap<String, List<IInstanceClassificationEvidence>>();
		_ontClass2NegativeC2CMappingMembers = new HashMap<String, List<IInstanceClassificationEvidence>>();
		_ontClass2PositiveC2CMappingsWithAccuracy = new HashMap<String, Map<IInstanceClassificationEvidence, Double>>();
		_ontClass2NegativeC2CMappingsWithAccuracy = new HashMap<String, Map<IInstanceClassificationEvidence, Double>>();
		_ontClass2ExplicitEvidencesWithLocalRate = new HashMap<String, Map<IInstanceClassificationEvidence, Double>>();
		_ontClass2HiddenEvidencesWithLocalRate = new HashMap<String, Map<IInstanceClassificationEvidence, Double>>();
		
		_correctionCluster2NegativeC2CMappingMembersWithFrequency = new HashMap<String, Map<IInstanceClassificationEvidence, Integer>>();
		_correctionCluster2NegativeC2CMappingMembersWithRate = new HashMap<String, Map<IInstanceClassificationEvidence, Double>>();
		_correctionCluster2NegativeC2CMappingMembers = new HashMap<String, List<IInstanceClassificationEvidence>>();
	}
	
	@Override
	public void computeMappingStatistics(){
		
		// NOTE: the ORDER to compute the frequency matters
		for (IInstanceClassificationEvidence mappingSet : _classificationCorrectionRepository.getAllInstanceMembershipInferenceFacts()) {
			if (_allC2CMapping2Frequency.containsKey(mappingSet)) {
				int count = _allC2CMapping2Frequency.get(mappingSet);
				count++;
				_allC2CMapping2Frequency.put(mappingSet, count);
			} else {
				_allC2CMapping2Frequency.put(mappingSet, 1);
			}
		}
		
		for (IInstanceClassificationEvidence mappingSet : _classificationCorrectionRepository.getExplicitInstanceMembershipInferenceFacts()) {
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			if (_ontClass2PositiveC2CMappingMembers.containsKey(correctionTargetClass)) {
				_ontClass2PositiveC2CMappingMembers.get(correctionTargetClass).add(mappingSet);
			} else {
				List<IInstanceClassificationEvidence> mappingSets = new ArrayList<IInstanceClassificationEvidence>();
				mappingSets.add(mappingSet);
				_ontClass2PositiveC2CMappingMembers.put(correctionTargetClass, mappingSets);
			}	
			
			int count = 0;
			if(_positiveC2CMapping2Frequency.containsKey(mappingSet)){
				count = _positiveC2CMapping2Frequency.get(mappingSet);
				count++;
				_positiveC2CMapping2Frequency.put(mappingSet, count);
			} else {
				_positiveC2CMapping2Frequency.put(mappingSet, 1);
				count = 1;
			}
			
			int allCount = _allC2CMapping2Frequency.get(mappingSet);
			double rate = (double) count / (double) allCount;
			_positiveC2CMapping2Accuracy.put(mappingSet, rate);
		}
		
		///
		for (String ontClass : _ontClass2PositiveC2CMappingMembers.keySet()) {
			Collection<IInstanceClassificationEvidence> mappingSets = _ontClass2PositiveC2CMappingMembers.get(ontClass);
			_ontClass2ExplicitEvidencesWithLocalRate.put(ontClass, this.groupMappingMembersWithRate(mappingSets));
		}
		
		
		
		for(IInstanceClassificationEvidence mappingSet: _positiveC2CMapping2Accuracy.keySet()){
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			
			double rate = _positiveC2CMapping2Accuracy.get(mappingSet);
			if(_ontClass2PositiveC2CMappingsWithAccuracy.containsKey(correctionTargetClass)){
				_ontClass2PositiveC2CMappingsWithAccuracy.get(correctionTargetClass).put(mappingSet, rate);
			} else {
				Map<IInstanceClassificationEvidence, Double> mappingRate = new HashMap<IInstanceClassificationEvidence, Double>();
				mappingRate.put(mappingSet, rate);
				_ontClass2PositiveC2CMappingsWithAccuracy.put(correctionTargetClass, mappingRate);
			}
		}
		
		for (IInstanceClassificationEvidence mappingSet : _classificationCorrectionRepository.getHiddenInstanceMembershipInferenceFacts()) {
			String correctionClusterCode = this.computeCorrectionClusterCode(mappingSet); 
			
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			if (_ontClass2NegativeC2CMappingMembers.containsKey(correctionTargetClass)) {
				_ontClass2NegativeC2CMappingMembers.get(correctionTargetClass).add(mappingSet);
			} else {
				List<IInstanceClassificationEvidence> mappingSets = new ArrayList<IInstanceClassificationEvidence>();
				mappingSets.add(mappingSet);
				_ontClass2NegativeC2CMappingMembers.put(correctionTargetClass, mappingSets);
			}	
			
			if (_correctionCluster2NegativeC2CMappingMembers.containsKey(correctionClusterCode)) {
				_correctionCluster2NegativeC2CMappingMembers.get(correctionClusterCode).add(mappingSet);
			} else {
				List<IInstanceClassificationEvidence> mappingSets = new ArrayList<IInstanceClassificationEvidence>();
				mappingSets.add(mappingSet);
				_correctionCluster2NegativeC2CMappingMembers.put(correctionClusterCode, mappingSets);
			}			
			
			int count2 = 0;
			if(_negativeC2CMapping2Frequency.containsKey(mappingSet)){
				count2 = _negativeC2CMapping2Frequency.get(mappingSet);
				count2++;
				_negativeC2CMapping2Frequency.put(mappingSet, count2);
			} else {
				_negativeC2CMapping2Frequency.put(mappingSet, 1);
				count2 = 1;
			}
			
			int allCount2 = _allC2CMapping2Frequency.get(mappingSet);
			double rate2 = (double) count2 / (double) allCount2;
			_negativeC2CMapping2Accuracy.put(mappingSet, rate2);
		}
		
		///
		for (String ontClass : _ontClass2NegativeC2CMappingMembers.keySet()) {
			Collection<IInstanceClassificationEvidence> mappingSets = _ontClass2NegativeC2CMappingMembers.get(ontClass);
			_ontClass2HiddenEvidencesWithLocalRate.put(ontClass, this.groupMappingMembersWithRate(mappingSets));
		}
		
		
		for(String correctionClusterCode : _correctionCluster2NegativeC2CMappingMembers.keySet()){
			Collection<IInstanceClassificationEvidence> mappingSets = _correctionCluster2NegativeC2CMappingMembers.get(correctionClusterCode);
			Map<IInstanceClassificationEvidence, Integer> C2CMapping2Frequency = new HashMap<IInstanceClassificationEvidence, Integer>();
			Map<IInstanceClassificationEvidence, Double> C2CMapping2Rate = new HashMap<IInstanceClassificationEvidence, Double>();
			for (IInstanceClassificationEvidence mappingSet : mappingSets) {
				
				int count = 0;
				if(C2CMapping2Frequency.containsKey(mappingSet)){
					count = C2CMapping2Frequency.get(mappingSet);
					count++;
					C2CMapping2Frequency.put(mappingSet, count);
				} else {
					C2CMapping2Frequency.put(mappingSet, 1);
					count = 1;
				}
				
				int allCount = _allC2CMapping2Frequency.get(mappingSet);
				double rate = (double) count / (double) allCount;
				C2CMapping2Rate.put(mappingSet, rate);
			}
			_correctionCluster2NegativeC2CMappingMembersWithFrequency.put(correctionClusterCode, C2CMapping2Frequency);
			_correctionCluster2NegativeC2CMappingMembersWithRate.put(correctionClusterCode, C2CMapping2Rate);
		}
		
		for(IInstanceClassificationEvidence mappingSet: _negativeC2CMapping2Accuracy.keySet()){
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			double rate = _negativeC2CMapping2Accuracy.get(mappingSet);
			if(_ontClass2NegativeC2CMappingsWithAccuracy.containsKey(correctionTargetClass)){
				_ontClass2NegativeC2CMappingsWithAccuracy.get(correctionTargetClass).put(mappingSet, rate);
			} else {
				Map<IInstanceClassificationEvidence, Double> mappingRate = new HashMap<IInstanceClassificationEvidence, Double>();
				mappingRate.put(mappingSet, rate);
				_ontClass2NegativeC2CMappingsWithAccuracy.put(correctionTargetClass, mappingRate);
			}
		}
	}

	private String computeCorrectionClusterCode(IInstanceClassificationEvidence mappingSet) {
		return _correctionClusterCodeGenerator.generateCorrectionClusterCode(new CorrectionDirection(
				mappingSet.getCorrectionSourceClass(),
				mappingSet.getCorrectionTargetClass())
		);
	}

	private Map<IInstanceClassificationEvidence, Double> groupMappingMembersWithRate(Collection<IInstanceClassificationEvidence> mappingMembers) {
		Map<IInstanceClassificationEvidence, Integer> C2CMapping2Frequency = new HashMap<IInstanceClassificationEvidence, Integer>();
		Map<IInstanceClassificationEvidence, Double> C2CMapping2Rate = new HashMap<IInstanceClassificationEvidence, Double>();
		for (IInstanceClassificationEvidence mappingSet : mappingMembers) {

			int count = 0;
			if (C2CMapping2Frequency.containsKey(mappingSet)) {
				count = C2CMapping2Frequency.get(mappingSet);
				count++;
				C2CMapping2Frequency.put(mappingSet, count);
			} else {
				C2CMapping2Frequency.put(mappingSet, 1);
				count = 1;
			}

			int allCount = mappingMembers.size();
			double rate = (double) count / (double) allCount;
			C2CMapping2Rate.put(mappingSet, rate);
		}
		return C2CMapping2Rate;
	}
	
	@Override
	public int getAllConcept2OntClassMappingCount(){
		return this._classificationCorrectionRepository.getAllConcept2OntClassMappingCount();
	}
	
	@Override
	public int getFrequencyOfConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet){
		return this._allC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public int getFrequencyOfPositiveConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet){
		return this._positiveC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public int getFrequencyOfNegativeConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet){
		return this._negativeC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public double getRateOfPositiveConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet){
		return this._positiveC2CMapping2Accuracy.get(mappingSet);
	}
	
	@Override
	public double getRateOfNegativeConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet){
		return this._negativeC2CMapping2Accuracy.get(mappingSet);
	}
	
	@Override
	public Map<IConcept2OntClassMapping, Double> getC2CMapping(String ontClassName) {
		return _classificationCorrectionRepository.getC2CMapping(ontClassName);
	}
	
	@Override
	public int getNumberOfC2CMapping(String ontClassName){
		return _classificationCorrectionRepository.getNumberOfC2CMappings(ontClassName);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Integer> getNegativeConcept2OntClassMappingSetsWithFrequencyOfCorrectionCluster(String correctionClusterName){
		return this._correctionCluster2NegativeC2CMappingMembersWithFrequency.get(correctionClusterName);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithRateOfCorrectionCluster(String correctionClusterName){
		return this._correctionCluster2NegativeC2CMappingMembersWithRate.get(correctionClusterName);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Double> getPositiveConcept2OntClassMappingSetsWithRateOfOntClass(String className){
		return this._ontClass2PositiveC2CMappingsWithAccuracy.get(className);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithRateOfOntClass(String className){
		return this._ontClass2NegativeC2CMappingsWithAccuracy.get(className);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Double> getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className){
//		Map<IInstanceClassificationEvidence, Double> explicitEvidencesWithLocalRate = new HashMap<IInstanceClassificationEvidence, Double>();
//		for(String superClassName : _ontologyRepository.getDownwardCotopy(className)){
//			if(_ontClass2ExplicitEvidencesWithLocalRate.containsKey(superClassName)){
//			explicitEvidencesWithLocalRate.putAll(this._ontClass2ExplicitEvidencesWithLocalRate.get(superClassName));
//			}
//		}
//		if(_ontClass2ExplicitEvidencesWithLocalRate.containsKey(className)){
//		explicitEvidencesWithLocalRate.putAll(this._ontClass2ExplicitEvidencesWithLocalRate.get(className));
//		}
//		return explicitEvidencesWithLocalRate;
		
		
		return this._ontClass2ExplicitEvidencesWithLocalRate.get(className);
	}
	
	@Override
	public Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className){
//		Map<IInstanceClassificationEvidence, Double> negativeEvidencesWithLocalRate = new HashMap<IInstanceClassificationEvidence, Double>();
//		for(String superClassName : _ontologyRepository.getDownwardCotopy(className)){
//			if(_ontClass2HiddenEvidencesWithLocalRate.containsKey(superClassName)){
//			negativeEvidencesWithLocalRate.putAll(this._ontClass2HiddenEvidencesWithLocalRate.get(superClassName));
//			}
//		}
//		if(_ontClass2HiddenEvidencesWithLocalRate.containsKey(className)){
//		negativeEvidencesWithLocalRate.putAll(this._ontClass2HiddenEvidencesWithLocalRate.get(className));
//		}
//		return negativeEvidencesWithLocalRate;
		
		
		return this._ontClass2HiddenEvidencesWithLocalRate.get(className);
	}
	
	@Override
	public Collection<IInstanceClassificationEvidence> getPositiveConcept2OntClassMappingSetsOfOntClass(String className) {
//		Set<IInstanceClassificationEvidence> positiveEvidences = new HashSet<IInstanceClassificationEvidence>();
//		for(String superClassName : _ontologyRepository.getDownwardCotopy(className)){
//			if(_ontClass2PositiveC2CMappingMembers.containsKey(superClassName)){
//				positiveEvidences.addAll(this._ontClass2PositiveC2CMappingMembers.get(superClassName));
//			}
//		}
//		if(_ontClass2PositiveC2CMappingMembers.containsKey(className)){
//			positiveEvidences.addAll(this._ontClass2PositiveC2CMappingMembers.get(className));
//		}
//		return positiveEvidences;
		
		
		return _ontClass2PositiveC2CMappingMembers.get(className);
	}
	
	@Override
	public Collection<IInstanceClassificationEvidence> getNegativeConcept2OntClassMappingSetsOfOntClass(String className) {
//		Set<IInstanceClassificationEvidence> negativeEvidences = new HashSet<IInstanceClassificationEvidence>();
//		for(String superClassName : _ontologyRepository.getDownwardCotopy(className)){
//			if(_ontClass2NegativeC2CMappingMembers.containsKey(superClassName)){
//				negativeEvidences.addAll(this._ontClass2NegativeC2CMappingMembers.get(superClassName));
//			}
//		}
//		if(_ontClass2NegativeC2CMappingMembers.containsKey(className)){
//			negativeEvidences.addAll(this._ontClass2NegativeC2CMappingMembers.get(className));
//		}
//		return negativeEvidences;
		
		return _ontClass2NegativeC2CMappingMembers.get(className);
	}

}
