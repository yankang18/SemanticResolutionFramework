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

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

@Deprecated
public class ClassificationCorrectionRepository implements IClassificationCorrectionRepository {

	private static final String CLASSIFICATION_CORRECTION_DIRECTROY_FULL_PATH = RepositoryParameterConfiguration.getClassificationCorrectionDirectoryFullPath();
	private static final String CLASSIFICATION_CORRECTION_REPOSITORY_NAME = RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_REPOSITORY_NAME;
	private static final String NEGATIVE_CONCEPT_CLASS_MAPPING_FILE_NAME = RepositoryParameterConfiguration.NEGATIVE_CONCEPT_CLASS_MAPPING;
	private static final String POSITIVE_CONCEPT_CLASS_MAPPING_FILE_NAME = RepositoryParameterConfiguration.POSITIVE_CONCEPT_CLASS_MAPPING;
	private static final String CONCEPT_CLASS_MAPPING_FILE_NAME = RepositoryParameterConfiguration.All_CONCEPT_CLASS_MAPPING;
	private static final String CLASSIFICATION_CORRECTION_REPOSITORY_FULL_PATH = CLASSIFICATION_CORRECTION_DIRECTROY_FULL_PATH + CLASSIFICATION_CORRECTION_REPOSITORY_NAME;
	private static final String NEGATIVE_CONCEPT_CLASS_MAPPING_FULL_PATH = CLASSIFICATION_CORRECTION_DIRECTROY_FULL_PATH + NEGATIVE_CONCEPT_CLASS_MAPPING_FILE_NAME;
	private static final String POSITIVE_CONCEPT_CLASS_MAPPING_FULL_PATH = CLASSIFICATION_CORRECTION_DIRECTROY_FULL_PATH + POSITIVE_CONCEPT_CLASS_MAPPING_FILE_NAME; 
	private static final String All_CONCEPT_CLASS_MAPPING_FULL_PATH = CLASSIFICATION_CORRECTION_DIRECTROY_FULL_PATH + CONCEPT_CLASS_MAPPING_FILE_NAME;

	private IOntologyRepository _ontologyRepository;
	private Set<IClassificationCorrection> _classificationCorrectionCollection;
	private List<IInstanceClassificationEvidence> _allInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _explicitInstanceMembershipInferenceFacts;
	private List<IInstanceClassificationEvidence> _hiddenInstanceMembershipInferenceFacts;

	public ClassificationCorrectionRepository(IOntologyRepository domainOntologyRepository) {
		_ontologyRepository = domainOntologyRepository;
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
			this.addInstanceClassificationEvidence(CCM);
			this.addExplicitInstanceClassificationEvidence(CCM);
			
			System.out.println("Explicit Instance Classification Evidence: " + CCM);
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
					this.addInstanceClassificationEvidence(CCM);
					this.addExplicitInstanceClassificationEvidence(CCM);
					
					System.out.println("Explicit Instance Classification Evidence: " + CCM);
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
				this.addInstanceClassificationEvidence(CCM);
				this.addHiddenInstanceClassificationEvidence(CCM);
				
				System.out.println("Hidden Instance Classification Evidence: " + CCM);
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
	
	@Override
	public void addClassificationCorrection(IClassificationCorrection correction) {
		// TODO Auto-generated method stub
		
	}
	
	public void addInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
	}

	@Override
	public void addHiddenInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
		_hiddenInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
//		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
	}

	@Override
	public void addExplicitInstanceClassificationEvidence(IInstanceClassificationEvidence concept2OntClassMappingSet) {
		_explicitInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
//		_allInstanceMembershipInferenceFacts.add(concept2OntClassMappingSet);
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
		for (IClassificationCorrection correction : _classificationCorrectionCollection) {
			correctionRecord.append(this.createJSONRecordForCorrection(correction));
			correctionRecord.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		StringBuilder negativeMappingRecord = new StringBuilder();
		for (IInstanceClassificationEvidence negativeMappingSet : _hiddenInstanceMembershipInferenceFacts) {
			negativeMappingRecord.append(this.createJSONRecordForMapping(negativeMappingSet));
			negativeMappingRecord.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		StringBuilder positiveMappingRecord = new StringBuilder();
		for (IInstanceClassificationEvidence positiveMappingSet : _explicitInstanceMembershipInferenceFacts) {
			positiveMappingRecord.append(this.createJSONRecordForMapping(positiveMappingSet));
			positiveMappingRecord.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		StringBuilder allMappingRecord = new StringBuilder();
		for (IInstanceClassificationEvidence mappingSet : _allInstanceMembershipInferenceFacts) {
			allMappingRecord.append(this.createJSONRecordForMapping(mappingSet));
			allMappingRecord.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		boolean correctionSaved = false;
		if (this.isNotEmpty(correctionRecord.toString())) {
			correctionSaved = FileAccessor.saveTripleString(CLASSIFICATION_CORRECTION_REPOSITORY_FULL_PATH, correctionRecord.toString());
		} else {
			correctionSaved = true;
		}
		boolean hittedMappingSaved = false;
		if (this.isNotEmpty(negativeMappingRecord.toString())) {
			hittedMappingSaved = FileAccessor.saveTripleString(NEGATIVE_CONCEPT_CLASS_MAPPING_FULL_PATH, negativeMappingRecord.toString());
		} else {
			hittedMappingSaved = true;
		}
		
		boolean positiveMappingSaved = false;
		if (this.isNotEmpty(positiveMappingRecord.toString())) {
			positiveMappingSaved = FileAccessor.saveTripleString(POSITIVE_CONCEPT_CLASS_MAPPING_FULL_PATH, positiveMappingRecord.toString());
		} else {
			positiveMappingSaved = true;
		}
		
		boolean mappingSaved = false;
		if (this.isNotEmpty(allMappingRecord.toString())) {
			mappingSaved = FileAccessor.saveTripleString(All_CONCEPT_CLASS_MAPPING_FULL_PATH, allMappingRecord.toString());
		} else {
			mappingSaved = true;
		}
		return correctionSaved && hittedMappingSaved && positiveMappingSaved && mappingSaved;
	}
	
	// move this to other class
	private boolean isNotEmpty(String string){
		return !string.trim().equals("");	
	}

	private String createJSONRecordForMapping(IInstanceClassificationEvidence mappingSet) {
		Map<String, Object> mappingRecord = new LinkedHashMap<String, Object>();
		OntoClassInfo sourceOntClass = mappingSet.getCorrectionSourceClass();
		OntoClassInfo targetOntClass = mappingSet.getCorrectionTargetClass();
		
		Map<String, String> sourceClassMap = new LinkedHashMap<String, String>();
		Map<String, String> targetClassMap = new LinkedHashMap<String, String>();
		List<Map<String, String>> mappingList = new ArrayList<Map<String, String>>();
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
		OntoClassInfo source = correction.getCorrectionSourceClass();
		OntoClassInfo target = correction.getCorrectionTargetClass();
		Map<String, String> correction_direction_map = new LinkedHashMap<String, String>();
		correction_direction_map.put(MappingInfoSchemaParameter.CORRECTION_SOURCE, source.getOntClassName());
		correction_direction_map.put(MappingInfoSchemaParameter.CORRECTION_TARGET, target.getOntClassName());

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
		boolean loadCorrectionsSucceed =  loadRecords(CLASSIFICATION_CORRECTION_REPOSITORY_FULL_PATH, 0);
		boolean loadHittedMappingSucceed = loadRecords(NEGATIVE_CONCEPT_CLASS_MAPPING_FULL_PATH, 1);
		boolean loadPositiveMappingSucceed = loadRecords(POSITIVE_CONCEPT_CLASS_MAPPING_FULL_PATH, 2);
		boolean loadAllMappingSucceed = loadRecords(All_CONCEPT_CLASS_MAPPING_FULL_PATH, 3);
		return loadCorrectionsSucceed && loadHittedMappingSucceed && loadPositiveMappingSucceed && loadAllMappingSucceed;
	}

	private boolean loadRecords(String fileFullName, int flag) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				if (0 == flag) {
					this.loadCorrectionRecord(line);
				} else if (1 == flag) {
					this.loadNegativeMappingRecord(line);
				} else if (2 == flag){
					this.loadPositiveMappingRecord(line);
				} else if (3 == flag) {
					this.loadAllMappingRecord(line);
				}
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

	private void loadAllMappingRecord(String line) {
		_allInstanceMembershipInferenceFacts.add(this.loadMappingRecord(line));
	}

	private void loadNegativeMappingRecord(String line) {
		_hiddenInstanceMembershipInferenceFacts.add(this.loadMappingRecord(line));
	}
	
	private void loadPositiveMappingRecord(String line){
		_explicitInstanceMembershipInferenceFacts.add(this.loadMappingRecord(line));
	}
	
	private IInstanceClassificationEvidence loadMappingRecord(String line) {
		JSONObject map = (JSONObject) JSONValue.parse(line);
		String prevenance = (String) map.get(MappingInfoSchemaParameter.PREVENANCE_OF_INSTANCE);
		String satellite = (String) map.get(MappingInfoSchemaParameter.INSTANCE_NAME);
		map.get(MappingInfoSchemaParameter.CORRECTION_SOURCE);
		map.get(MappingInfoSchemaParameter.CORRECTION_TARGET);
		map.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING);
		
		@SuppressWarnings("unchecked")
		Map<String, String> sourceClassMap = (Map<String, String>) map.get(MappingInfoSchemaParameter.CORRECTION_SOURCE);
		@SuppressWarnings("unchecked")
		Map<String, String> targetClassMap = (Map<String, String>) map.get(MappingInfoSchemaParameter.CORRECTION_TARGET);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> mappingList = (List<Map<String, String>>) map.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING);
		
		String sClassName = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_NAME);
		String sClassNS = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String sClassURI = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_URI);
		OntoClassInfo sourceOntClass = new OntoClassInfo(sClassURI, sClassNS, sClassName);
		String tClassName = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_NAME);
		String tClassNS = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String tClassURI = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_URI);
		OntoClassInfo targetOntClass = new OntoClassInfo(tClassURI, tClassNS, tClassName);
		Collection<IConcept2OntClassMapping> mappingSet = new HashSet<IConcept2OntClassMapping>();
		for(Map<String, String> mappingMap : mappingList){
			String conceptName = mappingMap.get(MappingInfoSchemaParameter.CONCEPT_NAME);
			String className = mappingMap.get(MappingInfoSchemaParameter.CLASS_NAME);
			String classNS = mappingMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
			String classURI = mappingMap.get(MappingInfoSchemaParameter.CLASS_URI);	
			double score = Double.valueOf(mappingMap.get(MappingInfoSchemaParameter.MAPPING_SCORE));
			IConcept2OntClassMapping mapping = new Concept2OntClassMapping(new Concept(conceptName), new OntoClassInfo(classURI, classNS, className), score); 
			mapping.setProvenantHostInstance(prevenance);
			mapping.setHostInstance(satellite);
			mappingSet.add(mapping);
		}
		return InstanceClassificationEvidence.createInstance(mappingSet, sourceOntClass, targetOntClass);
	}

	private void loadCorrectionRecord(String line) {

		JSONObject map = (JSONObject) JSONValue.parse(line);
		String instance_name = (String) map.get(MappingInfoSchemaParameter.INSTANCE_NAME);
		String instance_source = (String) map.get(MappingInfoSchemaParameter.INSTANCE_SOURCE);

		JSONObject correction_direction = (JSONObject) map.get(MappingInfoSchemaParameter.CORRECTION_DIRECTION);
		String correction_source = (String) correction_direction.get(MappingInfoSchemaParameter.CORRECTION_SOURCE);
		String correction_target = (String) correction_direction.get(MappingInfoSchemaParameter.CORRECTION_TARGET);

		IClassificationCorrection correction = ClassificationCorrection.createInstance();
		correction.setInstancePrevenance(instance_source);
		correction.setInstance(instance_name);
		correction.setCorrectionSource(_ontologyRepository.getLightWeightOntClassByName(correction_source));
		correction.setCorrectionTarget(_ontologyRepository.getLightWeightOntClassByName(correction_target));

		JSONArray hitted_mappings = (JSONArray) map.get(MappingInfoSchemaParameter.HITTED_MAPPINGS);
		for (int i = 0; i < hitted_mappings.size(); i++) {
			Concept2OntClassMapping mapping = createConcept2OntClassMappingPair((JSONObject) hitted_mappings.get(i));
			mapping.setHittedMapping(true);
			correction.addConcept2OntClassMapping(mapping);
		}
		JSONArray ambiguous_mappings = (JSONArray) map.get(MappingInfoSchemaParameter.AMBIGUOUS_MAPPINGS);
		for (int i = 0; i < ambiguous_mappings.size(); i++) {
			Concept2OntClassMapping mapping = createConcept2OntClassMappingPair((JSONObject) ambiguous_mappings.get(i));
			mapping.setHittedMapping(false);
			correction.addConcept2OntClassMapping(mapping);
		}
		JSONArray upmapped_concepts = (JSONArray) map.get(MappingInfoSchemaParameter.UNMAPPED_CONCEPTS);
		for (int i = 0; i < upmapped_concepts.size(); i++) {
			String unmapped_concept = (String) upmapped_concepts.get(i);
			correction.addConcept2OntClassMapping(new Concept2OntClassMapping(new Concept(unmapped_concept), null, null, 0.0));
		}

		JSONObject mapped_class_hierarchies = (JSONObject) map.get(MappingInfoSchemaParameter.MAPPED_CLASS_HIERARCHIES);
		for (Object hierarchyNumber : mapped_class_hierarchies.keySet()) {
			String hierarchyNumberString = (String) hierarchyNumber;
			String closeness = (String) mapped_class_hierarchies.get(hierarchyNumberString);
			correction.addMappedClassHierarchy(hierarchyNumberString, closeness);
		}
		_classificationCorrectionCollection.add(correction);
	}

	private Concept2OntClassMapping createConcept2OntClassMappingPair(JSONObject mapping) {
		String concept_name = (String) mapping.get(MappingInfoSchemaParameter.CONCEPT_NAME);
		String class_name = (String) mapping.get(MappingInfoSchemaParameter.CLASS_NAME);
		String class_namespace = (String) mapping.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String class_uri = (String) mapping.get(MappingInfoSchemaParameter.CLASS_URI);
		String concept_class_mapping_relation = (String) mapping.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION);
		Concept concept = new Concept(concept_name);
		OntoClassInfo ontClass = new OntoClassInfo(class_uri, class_namespace, class_name);
		MappingRelationType relation = MappingInfoSchemaParameter.getMappingRelationType(concept_class_mapping_relation);
		return new Concept2OntClassMapping(concept, relation, ontClass, 1.0);
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
		// TODO Auto-generated method stub
		
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
