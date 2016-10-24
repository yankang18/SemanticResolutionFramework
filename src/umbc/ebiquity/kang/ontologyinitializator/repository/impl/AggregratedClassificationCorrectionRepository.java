package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordParser;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class AggregratedClassificationCorrectionRepository extends AbstractRepositoryBatchLoader implements IClassificationCorrectionRepository{

	private int _numberOfInstance;
	private IClassificationCorrectionRecordParser _classificationCorrectionRecordParser;
	private Set<IClassificationCorrection> _classificationCorrectionCollection;
	private List<IInstanceClassificationEvidence> _allInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _explicitInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _hiddenInstanceMembershipInferenceFacts;
	private Map<IConcept2OntClassMapping, Set<IInstanceClassificationEvidence>> c2cMapping_2_evidence;
	
	private Map<String, Map<IConcept2OntClassMapping, Double>> _ontClass_C2CMappingWithLocalRate;
	private Map<IConcept2OntClassMapping, Set<String>> c2cMapping_ontClass;
	private Map<OntoClassInfo, List<IConcept2OntClassMapping>> _ontClass_c2cMappings = new HashMap<OntoClassInfo, List<IConcept2OntClassMapping>>();

	public AggregratedClassificationCorrectionRepository(IClassificationCorrectionRecordParser classificationCorrectionRecordParser) {
		super(FileRepositoryParameterConfiguration.getInterpretationCorrectionDirectoryFullPath());
		this._classificationCorrectionRecordParser = classificationCorrectionRecordParser;
		_classificationCorrectionCollection = new LinkedHashSet<IClassificationCorrection>();
		_allInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
		_explicitInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
		_hiddenInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
		c2cMapping_2_evidence = new HashMap<IConcept2OntClassMapping, Set<IInstanceClassificationEvidence>>();
		_ontClass_C2CMappingWithLocalRate = new HashMap<String, Map<IConcept2OntClassMapping, Double>>();
		c2cMapping_ontClass = new HashMap<IConcept2OntClassMapping, Set<String>>();
		_ontClass_c2cMappings = new HashMap<OntoClassInfo, List<IConcept2OntClassMapping>>();
	}
	
	public Collection<IClassificationCorrection> getClassificationCorrections() {
		return this._classificationCorrectionCollection;
	}

	@Override
	protected void loadRecord(String line) {
		_classificationCorrectionRecordParser.parseRecord(line, this); 
	}
	
	@Override
	public void addClassificationCorrection(IClassificationCorrection correction) {
		this._classificationCorrectionCollection.add(correction);
		
	}

	@Override
	public void addExplicitInstanceClassificationEvidence(IInstanceClassificationEvidence evidence) {
		this._explicitInstanceMembershipInferenceFacts.add(evidence);
		this._allInstanceMembershipInferenceFacts.add(evidence);
		this.addC2C_Evidence(evidence);
	}
	
	@Override
	public void addHiddenInstanceClassificationEvidence(IInstanceClassificationEvidence evidence) {
		this._hiddenInstanceMembershipInferenceFacts.add(evidence);
		this._allInstanceMembershipInferenceFacts.add(evidence);	
		this.addC2C_Evidence(evidence);
	}
	
	private void addC2C_Evidence(IInstanceClassificationEvidence evidence) {
		OntoClassInfo targetOntClass = evidence.getCorrectionTargetClass();
		for (IConcept2OntClassMapping mapping : evidence.getConcept2OntClassMappingMembers()) {

			if (c2cMapping_ontClass.containsKey(mapping)) {
				c2cMapping_ontClass.get(mapping).add(targetOntClass.getOntClassName());
			} else {
				Set<String> ontClasses = new HashSet<String>();
				ontClasses.add(targetOntClass.getOntClassName());
				c2cMapping_ontClass.put(mapping, ontClasses);
			}

			if (c2cMapping_2_evidence.containsKey(mapping)) {
				c2cMapping_2_evidence.get(mapping).add(evidence);
			} else {
				Set<IInstanceClassificationEvidence> evidences = new HashSet<IInstanceClassificationEvidence>();
				evidences.add(evidence);
				c2cMapping_2_evidence.put(mapping, evidences);
			}

			if (_ontClass_c2cMappings.containsKey(targetOntClass)) {
				_ontClass_c2cMappings.get(targetOntClass).add(mapping);
			} else {
				List<IConcept2OntClassMapping> mappings = new ArrayList<IConcept2OntClassMapping>();
				mappings.add(mapping);
				_ontClass_c2cMappings.put(targetOntClass, mappings);
			}
		}

	}
	
	private boolean localRateOfC2CMappingInOntClassComputed = false;
	private void computeLocalRateOfC2CMappingInOntClass(){
		for (OntoClassInfo ontClassInfo : _ontClass_c2cMappings.keySet()) {
			_ontClass_C2CMappingWithLocalRate.put(ontClassInfo.getOntClassName(),
					this.groupMappingMembersWithRate(_ontClass_c2cMappings.get(ontClassInfo)));
		}
		localRateOfC2CMappingInOntClassComputed = true;
	}
	
	
	private Map<IConcept2OntClassMapping, Double> groupMappingMembersWithRate(List<IConcept2OntClassMapping> mappings) {
		Map<IConcept2OntClassMapping, Integer> C2CMapping2Frequency = new HashMap<IConcept2OntClassMapping, Integer>();
		Map<IConcept2OntClassMapping, Double> C2CMapping2Rate = new HashMap<IConcept2OntClassMapping, Double>();
		for (IConcept2OntClassMapping mapping : mappings) {

			int count = 0;
			if (C2CMapping2Frequency.containsKey(mapping)) {
				count = C2CMapping2Frequency.get(mapping);
				count++;
				C2CMapping2Frequency.put(mapping, count);
			} else {
				C2CMapping2Frequency.put(mapping, 1);
				count = 1;
			}

			int allCount = mappings.size();
			double rate = (double) count / (double) allCount;
			C2CMapping2Rate.put(mapping, rate);
		}
		return C2CMapping2Rate;
	}
	
	
	
	public int getAllConcept2OntClassMappingCount() {
		return _allInstanceMembershipInferenceFacts.size();
	}
	
	public Collection<IInstanceClassificationEvidence> getAllInstanceMembershipInferenceFacts(){
		return this._allInstanceMembershipInferenceFacts;
	}
	
	@Override
	public int getNumberOfC2CMappings(String ontClassName) {
		if (_ontClass_C2CMappingWithLocalRate.containsKey(ontClassName)) {
			return this._ontClass_C2CMappingWithLocalRate.get(ontClassName).keySet().size();
		} else {
			return 0;
		}
	}

	@Override
	public Map<IConcept2OntClassMapping, Double> getC2CMapping(String ontClassName) {
		if (!localRateOfC2CMappingInOntClassComputed) {
			this.computeLocalRateOfC2CMappingInOntClass();
		}
		return _ontClass_C2CMappingWithLocalRate.get(ontClassName);
	}
	
	@Override
	public double getC2CMappingRateInOntClass(IConcept2OntClassMapping mapping, String ontClassName){
		
		if(!localRateOfC2CMappingInOntClassComputed){
			this.computeLocalRateOfC2CMappingInOntClass();
		}
		
		if(_ontClass_C2CMappingWithLocalRate.containsKey(ontClassName)){
			Map<IConcept2OntClassMapping, Double> mapping2Rate = _ontClass_C2CMappingWithLocalRate.get(ontClassName);
			if(mapping2Rate.containsKey(mapping)){
				double rate = mapping2Rate.get(mapping);
				return rate;
			} else {
				double rate = 1 / ((double) mapping2Rate.size() + 1);
				return rate;
			}
		} else {
			return 1.0;
		}
	}
	
	@Override
	public Collection<String> getTargetClasses(IConcept2OntClassMapping c2cMapping){
		return this.c2cMapping_ontClass.get(c2cMapping);
	}
	
	
	
	public Collection<IInstanceClassificationEvidence> getHiddenInstanceMembershipInferenceFacts(){
		return this._hiddenInstanceMembershipInferenceFacts;
	}
	
	public Collection<IInstanceClassificationEvidence> getExplicitInstanceMembershipInferenceFacts(){
		return this._explicitInstanceMembershipInferenceFacts;
	}
	
	@Override
	public void showMappingInfo(){
		
		if(!localRateOfC2CMappingInOntClassComputed){
			this.computeLocalRateOfC2CMappingInOntClass();
		}
		
		System.out.println("----------------------------------------");
		for(IConcept2OntClassMapping mapping : c2cMapping_ontClass.keySet()){
			Set<String> ontClassSet = c2cMapping_ontClass.get(mapping);
			System.out.println("mapping: " + mapping.getMappingCode());
			for(String ontClassName : ontClassSet){
				System.out.println("         " + ontClassName);
			}
		}
		
		System.out.println("----------------------------------------");
		for(String ontClassName : _ontClass_C2CMappingWithLocalRate.keySet()){
			Map<IConcept2OntClassMapping, Double> c2c_rate = _ontClass_C2CMappingWithLocalRate.get(ontClassName);
			System.out.println("OntClass: " + ontClassName);
			for(IConcept2OntClassMapping mapping : c2c_rate.keySet()){
				double rate = c2c_rate.get(mapping);
				System.out.println("         " + mapping.getMappingCode() + ":" + rate);
			}
		}
	}
	
	public void showRepositoryDetail() {
		for (IClassificationCorrection correction : _classificationCorrectionCollection) {
			System.out.println("### CORRECTED INSTANCE <" + correction.getInstance() + "> was corrected from <"
					+ correction.getCorrectionSourceClass().getOntClassName() + "> to <" + correction.getCorrectionTargetClass().getOntClassName() + ">");
			for (IConcept2OntClassMapping mappingPair : correction.getHittedMappings()) {
				System.out.println("Hitted Mapping: " + mappingPair.toString());
			}
			for (IConcept2OntClassMapping mappingPair : correction.getAmbiguousMappings()) {
				System.out.println("Ambiguous Mapping: " + mappingPair.toString());
			}
			for (String concept : correction.getUnMappedConcepts()) {
				System.out.println("UnMapped Concept: " + concept);
			}

			Map<String, String> classHierarchyMap = correction.getMappedClassHierarchies();
			for (String hierarchyNumber : classHierarchyMap.keySet()) {
				System.out.println(hierarchyNumber + ": " + classHierarchyMap.get(hierarchyNumber));
			}
			System.out.println();
		}
		System.out.println("Total number of correction: " + _classificationCorrectionCollection.size());
		
//		System.out.println();
//		System.out.println("### NEGATIVE MAPPING CLUSTER");
//		for(String mappingClusterCode : _negativeMappingInCorrectionCluster2Frequency.keySet()){
//			int count = _negativeMappingInCorrectionCluster2Frequency.get(mappingClusterCode);
//			double rate = _negativeMappingInCorrectionCluster2Rate.get(mappingClusterCode);
//			System.out.println(mappingClusterCode + " : " + count + " : " + rate);
//		}
//		
//		System.out.println();
//		System.out.println("### NEGATIVE MAPPING in CORRECTION CLUSTER ");
//		for (String correctionclusterCode : _negativeMappingInCorrectionCluster.keySet()) {
//			System.out.println("CORRECTION CLUSTER: " + correctionclusterCode);
//			Map<String, Double> mapping2Rate = _negativeMappingInCorrectionCluster.get(correctionclusterCode);
//			for (String mappingCode : mapping2Rate.keySet()) {
//				System.out.println(" --" + mappingCode + " : " + mapping2Rate.get(mappingCode));
//			}
//
//		}
//		
//		System.out.println();
//		System.out.println("### POSITIVE MAPPING");
//		for(String mappingClusterCode : _positiveC2CMapping2Frequency.keySet()){
//			int count = _positiveC2CMapping2Frequency.get(mappingClusterCode);
//			double rate = _positiveC2CMapping2Rate.get(mappingClusterCode);
//			System.out.println(mappingClusterCode + " : " + count + " : " + rate);
//		}
//		
//		System.out.println();
//		System.out.println("### POSITIVE MAPPING to ONTOLOGY CLASS");
//		for (String classCode : _ontClass2PositiveC2CMappings.keySet()) {
//			System.out.println("ONTO-CLASS: " + classCode);
//			Map<String, Double> mapping2Rate = _ontClass2PositiveC2CMappings.get(classCode);
//			for (String mappingCode : mapping2Rate.keySet()) {
//				System.out.println(" --" + mappingCode + " : " + mapping2Rate.get(mappingCode));
//			}
//		}
//		
//		System.out.println();
//		System.out.println("### ALL MAPPING CLUSTER");
//		for(String mappingClusterCode : _allC2CMapping2Frequency.keySet()){
//			int count = _allC2CMapping2Frequency.get(mappingClusterCode);
//			System.out.println(mappingClusterCode + " : " + count);
//		}
	}

	@Override
	public boolean saveRepository() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<IClassificationCorrection> extractCorrection(IInstanceRecord updatedInstance,
			IClassifiedInstanceDetailRecord originalInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNumberOfInstance(int numberOfInstance) {
		_numberOfInstance += numberOfInstance;	
	}


}
