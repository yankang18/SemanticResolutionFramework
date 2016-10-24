package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordParser;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class ProprietoryClassificationCorrectionRepository implements IClassificationCorrectionRepository {


	private int _numberOfInstance = 0;
	private String _repositoryName;
	private IOntologyRepository _ontologyRepository;
	private IClassificationCorrectionRecordParser _classificationCorrectionRecordParser;
	private Set<IClassificationCorrection> _classificationCorrectionCollection;
	private List<IInstanceClassificationEvidence> _allInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _explicitInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _hiddenInstanceMembershipInferenceFacts;

	public ProprietoryClassificationCorrectionRepository(
			 											  String fileFullName,
			 											  IOntologyRepository domainOntologyRepository,
			 											  IClassificationCorrectionRecordParser classificationCorrectionRecordParser
			 											) {
		_repositoryName = fileFullName;
		_ontologyRepository = domainOntologyRepository;
		_classificationCorrectionRecordParser = classificationCorrectionRecordParser;
		_classificationCorrectionCollection = new LinkedHashSet<IClassificationCorrection>();
		_allInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
		_explicitInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
		_hiddenInstanceMembershipInferenceFacts = new ArrayList<IInstanceClassificationEvidence>();
	}
	
	@Override
	public Collection<IClassificationCorrection> getClassificationCorrections() {
		return this._classificationCorrectionCollection;
	}

	@Override
	public Collection<IClassificationCorrection> extractCorrection(IInstanceRecord correctedInstance, IClassifiedInstanceDetailRecord originalInstance) {
		System.out.println(" ");
		System.out.println("###   EXTRACTION CORRECTIONS and INTERPRETATION EVIDENCE for instance: " + correctedInstance.getUpdatedInstance());
		_numberOfInstance ++;
		String sourceClassName = correctedInstance.getOriginalClassName();
		String targetClassName = correctedInstance.getUpdatedClassName();
		Collection<String> mappedOntClassesBeforeUpdated = new HashSet<String>();

		// record all the mapped classes from the original instance. That
		// is, the mapped classes before modified by human.
		for (IConcept2OntClassMapping mappingPair : originalInstance.getConcept2OntClassMappingPairs()) {
			mappingPair.setProvenantHostInstance(correctedInstance.getPrevenantInstance());
			mappingPair.setHostInstance(correctedInstance.getUpdatedInstance());
			if (mappingPair.isMappedConcept()) {
				mappedOntClassesBeforeUpdated.add(mappingPair.getMappedOntoClassName());
			}
		}
		
		Set<String> addedExplicitEvidences = new HashSet<String>();

		// extract all the class-sets that contributed to the classification of
		// the target class.
		
		//TODO: Should wrap the following two methods in class
		Collection<String> convergeClassSetBeforeUpdated = _ontologyRepository.computeMaximalConvergenceSet(mappedOntClassesBeforeUpdated, targetClassName);
		Collection<Set<IConcept2OntClassMapping>> c2cMappingSets = this.getConcept2ClassMappingSets(convergeClassSetBeforeUpdated, originalInstance.getConcept2OntClassMappingPairs());
		for (Set<IConcept2OntClassMapping> c2cMappingSet : c2cMappingSets) {
			IInstanceClassificationEvidence CCM = this.createInstanceClassificationCorrectionEvidence(c2cMappingSet, sourceClassName, targetClassName);
			String key = CCM.getEvidenceCode();
			addedExplicitEvidences.add(key);
//			this.addInstanceClassificationEvidence(CCM);
			this.addExplicitInstanceClassificationEvidence(CCM);
			
			System.out.println("Explicit Instance Classification Evidence: " + CCM.getEvidenceCode());
		}
		
		Collection<String> mappedOntClassesAfterUpdated = new HashSet<String>();
		for (IConcept2OntClassMapping mappingPair : correctedInstance.getConcept2OntClassMappingPairs()) {
			mappingPair.setProvenantHostInstance(correctedInstance.getPrevenantInstance());
			mappingPair.setHostInstance(correctedInstance.getUpdatedInstance());
			if (mappingPair.isMappedConcept()) {
				mappedOntClassesAfterUpdated.add(mappingPair.getMappedOntoClassName());
			}
		}
		
		if (!mappedOntClassesBeforeUpdated.containsAll(mappedOntClassesAfterUpdated)) { // if the mapped classes have been changed by human expert
			
			System.out.println("Corrected Instance Mapped Classes: " + mappedOntClassesAfterUpdated);
			Collection<String> convergeClassSetAfterUpdated = _ontologyRepository.computeMaximalConvergenceSet(mappedOntClassesAfterUpdated, targetClassName);
			Collection<Set<IConcept2OntClassMapping>> c2cMappingSets2 = this.getConcept2ClassMappingSets(convergeClassSetAfterUpdated, correctedInstance.getConcept2OntClassMappingPairs());
			for (Set<IConcept2OntClassMapping> c2cMappingSet : c2cMappingSets2) { 
				System.out.println("Explicit Mapping Set from Corrected Instance: " + c2cMappingSet);
				String key = this.createKey(c2cMappingSet, targetClassName);
				if (!addedExplicitEvidences.contains(key)) { // if the positive C2CMappingSet has already been added, we will not add it again.
					
					IInstanceClassificationEvidence CCM = this.createInstanceClassificationCorrectionEvidence(c2cMappingSet, sourceClassName, targetClassName);
//					this.addInstanceClassificationEvidence(CCM);
					this.addExplicitInstanceClassificationEvidence(CCM);
					
					System.out.println("Explicit Instance Classification Evidence: " + CCM.getEvidenceCode());
				}
			}
		}
		
		Collection<IClassificationCorrection> corrections = new ArrayList<IClassificationCorrection>();
		if (correctedInstance.isOntClassChanged()) { // if the classification has been corrected
			IClassificationCorrection correction = this.createCorrection(correctedInstance);

			System.out.println("### CORRECTED INSTANCE " + correctedInstance.getUpdatedInstance() + " was corrected from <"
					+ correctedInstance.getOriginalClassName() + "> to <" + correctedInstance.getUpdatedClassName() + ">");


			Collection<String> tributaryClasssetsToSourceClass = _ontologyRepository.computeMaximalConvergenceSet(mappedOntClassesBeforeUpdated, sourceClassName);
			Collection<Set<IConcept2OntClassMapping>> c2cMappingSet3 = this.getConcept2ClassMappingSets(tributaryClasssetsToSourceClass, originalInstance.getConcept2OntClassMappingPairs());
			
			
			// IMPORTANT: remove mappings that contribute to correct
			// classification (positive mapping)
			c2cMappingSet3.removeAll(c2cMappingSets);
			
			for (Set<IConcept2OntClassMapping> negativeC2CMappingSet : c2cMappingSet3) {
				IInstanceClassificationEvidence CCM = this.createInstanceClassificationCorrectionEvidence(negativeC2CMappingSet, sourceClassName, targetClassName);
//				this.addInstanceClassificationEvidence(CCM);
				this.addHiddenInstanceClassificationEvidence(CCM);
				
				System.out.println("Hidden Instance Classification Evidence: " + CCM.getEvidenceCode());
			}

			for (IConcept2OntClassMapping mapping: originalInstance.getConcept2OntClassMappingPairs()){
				correction.addConcept2OntClassMapping(mapping);
			}
			
			for (String hierarchyNumber : originalInstance.getClassHierarchyNumber2ClosenessScoreMap().keySet()) {
				correction.addMappedClassHierarchy(hierarchyNumber,
						String.valueOf(originalInstance.getClassHierarchyNumber2ClosenessScoreMap().get(hierarchyNumber)));
			}
			this._classificationCorrectionCollection.add(correction);
			corrections.add(correction);
		}
		return corrections;
	} 

	private String createKey(Set<IConcept2OntClassMapping> mappingSet, String targetClassName) {
		return InstanceClassificationEvidence.createMemberInferenceFactCode(mappingSet, targetClassName);
	}
	
	private Collection<Set<IConcept2OntClassMapping>> getConcept2ClassMappingSets(Collection<String> tributaryClasssets,
																				  Collection<IConcept2OntClassMapping> concept2OntClassMappingPairs) {
		Collection<Set<IConcept2OntClassMapping>> mappingSets = new HashSet<Set<IConcept2OntClassMapping>>();
		Map<String, Collection<IConcept2OntClassMapping>> ontClass2Mapping = new HashMap<String, Collection<IConcept2OntClassMapping>>();
		// group mappings that map to the same class
		for (IConcept2OntClassMapping mapping : concept2OntClassMappingPairs) {
			if (mapping.isMappedConcept()) {
				String mappedClassName = mapping.getMappedOntoClassName();
				if (ontClass2Mapping.containsKey(mappedClassName)) {
					ontClass2Mapping.get(mappedClassName).add(mapping);
				} else {
					Collection<IConcept2OntClassMapping> mappings = new HashSet<IConcept2OntClassMapping>();
					mappings.add(mapping);
					ontClass2Mapping.put(mappedClassName, mappings);
				}
			}
		}

		for (String c : tributaryClasssets) {
			Collection<IConcept2OntClassMapping> mappingCollection = ontClass2Mapping.get(c);
			for (IConcept2OntClassMapping mapping : mappingCollection) {
				Set<IConcept2OntClassMapping> mappingSet = new HashSet<IConcept2OntClassMapping>();
				mappingSet.add(mapping);
				mappingSets.add(mappingSet);
			}
		}
		return mappingSets;
	}
	
//	private void addInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
//		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
//	}

	@Override
	public void addClassificationCorrection(IClassificationCorrection correction) {
		this._classificationCorrectionCollection.add(correction);		
	}
	
	@Override
	public void addHiddenInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
		_hiddenInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
	}

	@Override
	public void addExplicitInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
		_explicitInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
	}
	
	private IInstanceClassificationEvidence createInstanceClassificationCorrectionEvidence(Set<IConcept2OntClassMapping> mappingSet, String sourceClassName, String targetClassName){
		OntoClassInfo correctionSourceClass = _ontologyRepository.getLightWeightOntClassByName(sourceClassName);
		OntoClassInfo correctionTagetClass = _ontologyRepository.getLightWeightOntClassByName(targetClassName);
		return InstanceClassificationEvidence.createInstance(mappingSet, correctionSourceClass, correctionTagetClass);
	}
	
	private IClassificationCorrection createCorrection(IInstanceRecord correctedInstance){
		IClassificationCorrection correction = ClassificationCorrection.createInstance();
		correction.setInstancePrevenance(correctedInstance.getPrevenantInstance());
		correction.setInstance(correctedInstance.getUpdatedInstance());
		correction.setCorrectionSource(_ontologyRepository.getLightWeightOntClassByName(correctedInstance.getOriginalClassName()));
		correction.setCorrectionTarget(_ontologyRepository.getLightWeightOntClassByName(correctedInstance.getUpdatedClassName()));
		return correction;
	}
	
	@Override
	public boolean saveRepository() {
		StringBuilder correctionRecord = new StringBuilder();
		
		correctionRecord.append(this.createJSONRecordForMetaData());
		correctionRecord.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		
		for (IClassificationCorrection correction : _classificationCorrectionCollection) {
			correctionRecord.append(this.createJSONRecordForCorrection(correction));
			correctionRecord.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		for (IInstanceClassificationEvidence negativeMappingSet : _hiddenInstanceMembershipInferenceFacts) {
			correctionRecord.append(this.createJSONRecordForMapping(negativeMappingSet, MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_HIDDEN_EVIDENCE));
			correctionRecord.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		for (IInstanceClassificationEvidence positiveMappingSet : _explicitInstanceMembershipInferenceFacts) {
			correctionRecord.append(this.createJSONRecordForMapping(positiveMappingSet, MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_EXPLICIT_EVIDENCE));
			correctionRecord.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		boolean correctionSaved = false;
		if (this.isNotEmpty(correctionRecord.toString())) {
			correctionSaved = FileAccessor.saveTripleString(this._repositoryName, correctionRecord.toString());
		} else {
			correctionSaved = true;
		}
		return correctionSaved;
	}
	
	// move this to other class
	private boolean isNotEmpty(String string){
		return !string.trim().equals("");	
	}

	private String createJSONRecordForMetaData(){
		Map<String, Object> mappingRecord = new LinkedHashMap<String, Object>();
		mappingRecord.put(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE, MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_METADATA);
		mappingRecord.put(MappingInfoSchemaParameter.NUMBER_OF_INSTANCE, String.valueOf(this._numberOfInstance));
		mappingRecord.put(MappingInfoSchemaParameter.NUMBER_OF_CORRECTION, String.valueOf(this._classificationCorrectionCollection.size()));
		return JSONValue.toJSONString(mappingRecord);
	}
	
	private String createJSONRecordForMapping(IInstanceClassificationEvidence mappingSet, String recordType) {
		Map<String, Object> mappingRecord = new LinkedHashMap<String, Object>();
		OntoClassInfo sourceOntClass = mappingSet.getCorrectionSourceClass();
		OntoClassInfo targetOntClass = mappingSet.getCorrectionTargetClass();
		
		Map<String, String> sourceClassMap = new LinkedHashMap<String, String>();
		Map<String, String> targetClassMap = new LinkedHashMap<String, String>();
		List<Map<String, String>> mappingList = new ArrayList<Map<String, String>>();
		mappingRecord.put(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE, recordType);
		mappingRecord.put(MappingInfoSchemaParameter.CORRECTION_SOURCE, sourceClassMap);
		mappingRecord.put(MappingInfoSchemaParameter.CORRECTION_TARGET, targetClassMap);
		mappingRecord.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING, mappingList);
		
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_NAME, sourceOntClass.getOntClassName());
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, sourceOntClass.getNameSpace());
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_URI, sourceOntClass.getURI());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_NAME, targetOntClass.getOntClassName());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, targetOntClass.getNameSpace());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_URI, targetOntClass.getURI());
		
		boolean flag = false;
		for (IConcept2OntClassMapping mapping : mappingSet.getConcept2OntClassMappingMembers()) {
			if (flag == false) {
				flag = true;
				String prevenance = mapping.getProvenantHostInstance();
				String satellite = mapping.getHostInstance();
				mappingRecord.put(MappingInfoSchemaParameter.PREVENANCE_OF_INSTANCE, prevenance);
				mappingRecord.put(MappingInfoSchemaParameter.INSTANCE_NAME, satellite);
			}
			Map<String, String> mappingMap = new LinkedHashMap<String, String>();
			mappingList.add(mappingMap);
			String conceptName = mapping.getConceptName();
			OntoClassInfo ontClass = mapping.getMappedOntoClass();
			String className = ontClass.getOntClassName();
			String classNS = ontClass.getNameSpace();
			String classURI = ontClass.getURI();
			double score = mapping.getMappingScore();

			mappingMap.put(MappingInfoSchemaParameter.CONCEPT_NAME, conceptName);
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAME, className);
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, classNS);
			mappingMap.put(MappingInfoSchemaParameter.CLASS_URI, classURI);
			mappingMap.put(MappingInfoSchemaParameter.MAPPING_SCORE, String.valueOf(score));

		}
		return JSONValue.toJSONString(mappingRecord);
	}

	private String createJSONRecordForCorrection(IClassificationCorrection correction) {

		String instanceSource = correction.getInstanceSource();
		String instance = correction.getInstance();
		OntoClassInfo sourceOntClass = correction.getCorrectionSourceClass();
		OntoClassInfo targetOntClass = correction.getCorrectionTargetClass();
		Map<String, Object> correction_direction_map = new LinkedHashMap<String, Object>();
		Map<String, String> sourceClassMap = new LinkedHashMap<String, String>();
		Map<String, String> targetClassMap = new LinkedHashMap<String, String>();
		
		correction_direction_map.put(MappingInfoSchemaParameter.CORRECTION_SOURCE, sourceClassMap);
		correction_direction_map.put(MappingInfoSchemaParameter.CORRECTION_TARGET, targetClassMap);
		
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_NAME, sourceOntClass.getOntClassName());
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, sourceOntClass.getNameSpace());
		sourceClassMap.put(MappingInfoSchemaParameter.CLASS_URI, sourceOntClass.getURI());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_NAME, targetOntClass.getOntClassName());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, targetOntClass.getNameSpace());
		targetClassMap.put(MappingInfoSchemaParameter.CLASS_URI, targetOntClass.getURI());

		Collection<Map<String, String>> hittedMappings = new ArrayList<Map<String, String>>();
		for (IConcept2OntClassMapping mapping : correction.getHittedMappings()) {
			Map<String, String> mappingMap = new LinkedHashMap<String, String>();
			hittedMappings.add(mappingMap);
			mappingMap.put(MappingInfoSchemaParameter.CONCEPT_NAME, mapping.getConceptName());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAME, mapping.getMappedOntoClass().getOntClassName());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, mapping.getMappedOntoClass().getNameSpace());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_URI, mapping.getMappedOntoClass().getURI());
			mappingMap.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION, mapping.getRelation().toString());
		}

		Collection<Map<String, String>> ambiguousMappings = new ArrayList<Map<String, String>>();
		for (IConcept2OntClassMapping mapping : correction.getAmbiguousMappings()) {
			Map<String, String> mappingMap = new LinkedHashMap<String, String>();
			ambiguousMappings.add(mappingMap);
			mappingMap.put(MappingInfoSchemaParameter.CONCEPT_NAME, mapping.getConceptName());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAME, mapping.getMappedOntoClass().getOntClassName());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, mapping.getMappedOntoClass().getNameSpace());
			mappingMap.put(MappingInfoSchemaParameter.CLASS_URI, mapping.getMappedOntoClass().getURI());
			mappingMap.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION, mapping.getRelation().toString());
		}

		Collection<String> unMappedConcepts = new ArrayList<String>();
		for (String concept : correction.getUnMappedConcepts()) {
			unMappedConcepts.add(concept);
		}

		Map<String, String> mappedClassHierarchies = correction.getMappedClassHierarchies();
		Map<String, Object> jsonRecord = new LinkedHashMap<String, Object>();
		jsonRecord.put(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE, MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_CORRECTION);
		jsonRecord.put(MappingInfoSchemaParameter.INSTANCE_NAME, instance);
		jsonRecord.put(MappingInfoSchemaParameter.INSTANCE_SOURCE, instanceSource);
		jsonRecord.put(MappingInfoSchemaParameter.CORRECTION_DIRECTION, correction_direction_map);
		jsonRecord.put(MappingInfoSchemaParameter.HITTED_MAPPINGS, hittedMappings);
		jsonRecord.put(MappingInfoSchemaParameter.AMBIGUOUS_MAPPINGS, ambiguousMappings);
		jsonRecord.put(MappingInfoSchemaParameter.UNMAPPED_CONCEPTS, unMappedConcepts);
		jsonRecord.put(MappingInfoSchemaParameter.MAPPED_CLASS_HIERARCHIES, mappedClassHierarchies);
		return JSONValue.toJSONString(jsonRecord);
	}

	@Override
	public boolean loadRepository() {
		_classificationCorrectionCollection.clear();
		boolean loadCorrectionsSucceed = loadRecords(this._repositoryName);
		return loadCorrectionsSucceed;
	}

	private boolean loadRecords(String fileFullName) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadRecord(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void loadRecord(String line) {
		_classificationCorrectionRecordParser.parseRecord(line, this); 
	}
	
	@Override
	public int getAllConcept2OntClassMappingCount() {
		return _allInstanceMembershipInferenceFacts.size();
	}
	
	@Override
	public Collection<IInstanceClassificationEvidence> getAllInstanceMembershipInferenceFacts(){
		return this._allInstanceMembershipInferenceFacts;
	}
	
	@Override
	public Collection<IInstanceClassificationEvidence> getHiddenInstanceMembershipInferenceFacts(){
		return this._hiddenInstanceMembershipInferenceFacts;
	}
	
	@Override
	public Collection<IInstanceClassificationEvidence> getExplicitInstanceMembershipInferenceFacts(){
		return this._explicitInstanceMembershipInferenceFacts;
	}
	
	@Override
	public void showRepositoryDetail() {
		System.out.println("Number Of Instances: " + this._numberOfInstance);
		System.out.println("Number Of Corrections: " + this._classificationCorrectionCollection.size());
		for (IClassificationCorrection correction : _classificationCorrectionCollection) {
			System.out.println("### CORRECTED INSTANCE <" + correction.getInstance() + "> was corrected from <"
					+ correction.getCorrectionSourceClass() + "> to <" + correction.getCorrectionTargetClass() + ">");
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
	public void addNumberOfInstance(int numberOfInstance) {
		this._numberOfInstance = numberOfInstance;
	}

	@Override
	public Collection<String> getTargetClasses(IConcept2OntClassMapping c2cMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getC2CMappingRateInOntClass(IConcept2OntClassMapping mapping, String ontClassName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void showMappingInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<IConcept2OntClassMapping, Double> getC2CMapping(String ontClassName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfC2CMappings(String ontClassName) {
		// TODO Auto-generated method stub
		return 0;
	}

}
