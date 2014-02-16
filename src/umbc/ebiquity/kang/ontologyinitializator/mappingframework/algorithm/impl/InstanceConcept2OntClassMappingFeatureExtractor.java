package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.ICorrectionClusterCodeGenerator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceConcept2OntClassMappingFeatureExtractor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;

public class InstanceConcept2OntClassMappingFeatureExtractor implements IInstanceConcept2OntClassMappingFeatureExtractor {
	
	private Map<IInstanceMembershipInfereceFact, Integer> _allC2CMapping2Frequency;
	private Map<IInstanceMembershipInfereceFact, Integer> _positiveC2CMapping2Frequency;
	private Map<IInstanceMembershipInfereceFact, Integer> _negativeC2CMapping2Frequency;
	private Map<IInstanceMembershipInfereceFact, Double> _positiveC2CMapping2Accuracy;
	private Map<IInstanceMembershipInfereceFact, Double> _negativeC2CMapping2Accuracy;
	private Map<String, List<IInstanceMembershipInfereceFact>> _correctionCluster2NegativeC2CMappingMembers;
	private Map<String, Map<IInstanceMembershipInfereceFact, Integer>> _correctionCluster2NegativeC2CMappingMembersWithFrequency;
	private Map<String, Map<IInstanceMembershipInfereceFact, Double>> _correctionCluster2NegativeC2CMappingMembersWithRate;
	private Map<String, Map<IInstanceMembershipInfereceFact, Double>> _ontClass2PositiveC2CMappingsWithAccuracy;
	private Map<String, Map<IInstanceMembershipInfereceFact, Double>> _ontClass2NegativeC2CMappingsWithAccuracy; 
	private Map<String, Map<IInstanceMembershipInfereceFact, Double>> _ontClass2PositiveC2CMappingsWithLocalRate;
	private Map<String, Map<IInstanceMembershipInfereceFact, Double>> _ontClass2NegativeC2CMappingsWithLocalRate;
	private IClassificationCorrectionRepository _classificationCorrectionRepository;
	private ICorrectionClusterCodeGenerator _correctionClusterCodeGenerator;
	
	private Map<String, List<IInstanceMembershipInfereceFact>> _ontClass2PositiveC2CMappingMembers;
	private Map<String, List<IInstanceMembershipInfereceFact>> _ontClass2NegativeC2CMappingMembers;
	
	public InstanceConcept2OntClassMappingFeatureExtractor(
			ICorrectionClusterCodeGenerator correctionClusterCodeGenerator,
			IClassificationCorrectionRepository classificationCorrectionRepository
	){
		_correctionClusterCodeGenerator = correctionClusterCodeGenerator;
		_classificationCorrectionRepository = classificationCorrectionRepository;
		this.init();
		this.computeMappingStatistics();
	}

	private void init() {
		_allC2CMapping2Frequency = new HashMap<IInstanceMembershipInfereceFact, Integer>();
		_positiveC2CMapping2Frequency = new HashMap<IInstanceMembershipInfereceFact, Integer>();
		_negativeC2CMapping2Frequency = new HashMap<IInstanceMembershipInfereceFact, Integer>();
		_positiveC2CMapping2Accuracy = new HashMap<IInstanceMembershipInfereceFact, Double>();
		_negativeC2CMapping2Accuracy = new HashMap<IInstanceMembershipInfereceFact, Double>();
		
		_ontClass2PositiveC2CMappingMembers = new HashMap<String, List<IInstanceMembershipInfereceFact>>();
		_ontClass2NegativeC2CMappingMembers = new HashMap<String, List<IInstanceMembershipInfereceFact>>();
		_ontClass2PositiveC2CMappingsWithAccuracy = new HashMap<String, Map<IInstanceMembershipInfereceFact, Double>>();
		_ontClass2NegativeC2CMappingsWithAccuracy = new HashMap<String, Map<IInstanceMembershipInfereceFact, Double>>();
		_ontClass2PositiveC2CMappingsWithLocalRate = new HashMap<String, Map<IInstanceMembershipInfereceFact, Double>>();
		_ontClass2NegativeC2CMappingsWithLocalRate = new HashMap<String, Map<IInstanceMembershipInfereceFact, Double>>();
		
		_correctionCluster2NegativeC2CMappingMembersWithFrequency = new HashMap<String, Map<IInstanceMembershipInfereceFact, Integer>>();
		_correctionCluster2NegativeC2CMappingMembersWithRate = new HashMap<String, Map<IInstanceMembershipInfereceFact, Double>>();
		_correctionCluster2NegativeC2CMappingMembers = new HashMap<String, List<IInstanceMembershipInfereceFact>>();
	}
	
	@Override
	public void computeMappingStatistics(){
		
		// NOTE: the ORDER to compute the frequency matters
		for (IInstanceMembershipInfereceFact mappingSet : _classificationCorrectionRepository.getAllInstanceMembershipInferenceFacts()) {
			if (_allC2CMapping2Frequency.containsKey(mappingSet)) {
				int count = _allC2CMapping2Frequency.get(mappingSet);
				count++;
				_allC2CMapping2Frequency.put(mappingSet, count);
			} else {
				_allC2CMapping2Frequency.put(mappingSet, 1);
			}
		}
		
		for (IInstanceMembershipInfereceFact mappingSet : _classificationCorrectionRepository.getExplicitInstanceMembershipInferenceFacts()) {
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			if (_ontClass2PositiveC2CMappingMembers.containsKey(correctionTargetClass)) {
				_ontClass2PositiveC2CMappingMembers.get(correctionTargetClass).add(mappingSet);
			} else {
				List<IInstanceMembershipInfereceFact> mappingSets = new ArrayList<IInstanceMembershipInfereceFact>();
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
			Collection<IInstanceMembershipInfereceFact> mappingSets = _ontClass2PositiveC2CMappingMembers.get(ontClass);
			_ontClass2PositiveC2CMappingsWithLocalRate.put(ontClass, this.groupMappingMembersWithRate(mappingSets));
		}
		
		
		
		for(IInstanceMembershipInfereceFact mappingSet: _positiveC2CMapping2Accuracy.keySet()){
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			
			double rate = _positiveC2CMapping2Accuracy.get(mappingSet);
			if(_ontClass2PositiveC2CMappingsWithAccuracy.containsKey(correctionTargetClass)){
				_ontClass2PositiveC2CMappingsWithAccuracy.get(correctionTargetClass).put(mappingSet, rate);
			} else {
				Map<IInstanceMembershipInfereceFact, Double> mappingRate = new HashMap<IInstanceMembershipInfereceFact, Double>();
				mappingRate.put(mappingSet, rate);
				_ontClass2PositiveC2CMappingsWithAccuracy.put(correctionTargetClass, mappingRate);
			}
		}
		
		for (IInstanceMembershipInfereceFact mappingSet : _classificationCorrectionRepository.getHiddenInstanceMembershipInferenceFacts()) {
			String correctionClusterCode = this.computeCorrectionClusterCode(mappingSet); 
			
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			if (_ontClass2NegativeC2CMappingMembers.containsKey(correctionTargetClass)) {
				_ontClass2NegativeC2CMappingMembers.get(correctionTargetClass).add(mappingSet);
			} else {
				List<IInstanceMembershipInfereceFact> mappingSets = new ArrayList<IInstanceMembershipInfereceFact>();
				mappingSets.add(mappingSet);
				_ontClass2NegativeC2CMappingMembers.put(correctionTargetClass, mappingSets);
			}	
			
			if (_correctionCluster2NegativeC2CMappingMembers.containsKey(correctionClusterCode)) {
				_correctionCluster2NegativeC2CMappingMembers.get(correctionClusterCode).add(mappingSet);
			} else {
				List<IInstanceMembershipInfereceFact> mappingSets = new ArrayList<IInstanceMembershipInfereceFact>();
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
			Collection<IInstanceMembershipInfereceFact> mappingSets = _ontClass2NegativeC2CMappingMembers.get(ontClass);
			_ontClass2NegativeC2CMappingsWithLocalRate.put(ontClass, this.groupMappingMembersWithRate(mappingSets));
		}
		
		
		for(String correctionClusterCode : _correctionCluster2NegativeC2CMappingMembers.keySet()){
			Collection<IInstanceMembershipInfereceFact> mappingSets = _correctionCluster2NegativeC2CMappingMembers.get(correctionClusterCode);
			Map<IInstanceMembershipInfereceFact, Integer> C2CMapping2Frequency = new HashMap<IInstanceMembershipInfereceFact, Integer>();
			Map<IInstanceMembershipInfereceFact, Double> C2CMapping2Rate = new HashMap<IInstanceMembershipInfereceFact, Double>();
			for (IInstanceMembershipInfereceFact mappingSet : mappingSets) {
				
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
		
		for(IInstanceMembershipInfereceFact mappingSet: _negativeC2CMapping2Accuracy.keySet()){
			String correctionTargetClass = mappingSet.getCorrectionTargetClass().getOntClassName();
			double rate = _negativeC2CMapping2Accuracy.get(mappingSet);
			if(_ontClass2NegativeC2CMappingsWithAccuracy.containsKey(correctionTargetClass)){
				_ontClass2NegativeC2CMappingsWithAccuracy.get(correctionTargetClass).put(mappingSet, rate);
			} else {
				Map<IInstanceMembershipInfereceFact, Double> mappingRate = new HashMap<IInstanceMembershipInfereceFact, Double>();
				mappingRate.put(mappingSet, rate);
				_ontClass2NegativeC2CMappingsWithAccuracy.put(correctionTargetClass, mappingRate);
			}
		}
	}

	private String computeCorrectionClusterCode(IInstanceMembershipInfereceFact mappingSet) {
		return _correctionClusterCodeGenerator.generateCorrectionClusterCode(new CorrectionDirection(
				mappingSet.getCorrectionSourceClass(),
				mappingSet.getCorrectionTargetClass())
		);
	}

	private Map<IInstanceMembershipInfereceFact, Double> groupMappingMembersWithRate(Collection<IInstanceMembershipInfereceFact> mappingMembers) {
		Map<IInstanceMembershipInfereceFact, Integer> C2CMapping2Frequency = new HashMap<IInstanceMembershipInfereceFact, Integer>();
		Map<IInstanceMembershipInfereceFact, Double> C2CMapping2Rate = new HashMap<IInstanceMembershipInfereceFact, Double>();
		for (IInstanceMembershipInfereceFact mappingSet : mappingMembers) {

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
	public int getFrequencyOfConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet){
		return this._allC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public int getFrequencyOfPositiveConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet){
		return this._positiveC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public int getFrequencyOfNegativeConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet){
		return this._negativeC2CMapping2Frequency.get(mappingSet);
	}
	
	@Override
	public double getRateOfPositiveConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet){
		return this._positiveC2CMapping2Accuracy.get(mappingSet);
	}
	
	@Override
	public double getRateOfNegativeConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet){
		return this._negativeC2CMapping2Accuracy.get(mappingSet);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Integer> getNegativeConcept2OntClassMappingSetsWithFrequencyOfCorrectionCluster(String correctionClusterName){
		return this._correctionCluster2NegativeC2CMappingMembersWithFrequency.get(correctionClusterName);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithRateOfCorrectionCluster(String correctionClusterName){
		return this._correctionCluster2NegativeC2CMappingMembersWithRate.get(correctionClusterName);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Double> getPositiveConcept2OntClassMappingSetsWithRateOfOntClass(String className){
		return this._ontClass2PositiveC2CMappingsWithAccuracy.get(className);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithRateOfOntClass(String className){
		return this._ontClass2NegativeC2CMappingsWithAccuracy.get(className);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Double> getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className){
		return this._ontClass2PositiveC2CMappingsWithLocalRate.get(className);
	}
	
	@Override
	public Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className){
		return this._ontClass2NegativeC2CMappingsWithLocalRate.get(className);
	}
	
	@Override
	public Collection<IInstanceMembershipInfereceFact> getPositiveConcept2OntClassMappingSetsOfOntClass(String className) {
		return _ontClass2PositiveC2CMappingMembers.get(className);
	}
	
	@Override
	public Collection<IInstanceMembershipInfereceFact> getNegativeConcept2OntClassMappingSetsOfOntClass(String className) {
		return _ontClass2NegativeC2CMappingMembers.get(className);
	}

}
