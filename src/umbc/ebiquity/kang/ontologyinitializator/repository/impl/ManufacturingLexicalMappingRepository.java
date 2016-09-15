package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.RuleEngine;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.exception.NoSuchEntryItemException;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingStatistics;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

/**
 * 
 * @author kangyan2003
 *
 */
@Deprecated
public class ManufacturingLexicalMappingRepository implements IManufacturingLexicalMappingRepository {
	
	private IOntologyRepository _ontologyRepository;
	
	/**
	 * This map functions as an index of the Manufacturing Lexicon Repository.
	 * It maps concept (by name) to all onto-classes (by name) it has mapped to.
	 */
	private Map<String, Collection<String>> concept2OntoClassReferenceMap;

	/**
	 * 
	 */
    private Map<String, Map<String, Map<String, String>>> detailConcept2OntoClassMap;
    
	private static final String MANUFACTURING_LEXICON_DIRECTROY_FULL_PATH = RepositoryParameterConfiguration.getManufacturingLexiconDirectoryFullPath();
	private static final String MANUFACTURING_LEXICON_REPOSITORY_NAME = RepositoryParameterConfiguration.MANUFACTURING_LEXICON_NAME;
	private static final String MANUFACTURING_LEXICON_REPOSITORY_FULL_PATH = MANUFACTURING_LEXICON_DIRECTROY_FULL_PATH + MANUFACTURING_LEXICON_REPOSITORY_NAME;
	private RuleEngine _ruleEngine;
	
    public ManufacturingLexicalMappingRepository(IOntologyRepository ontologyRepository, RuleEngine ruleEngine){
    	this.detailConcept2OntoClassMap = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		this.concept2OntoClassReferenceMap = new LinkedHashMap<String, Collection<String>>();
		this._ontologyRepository = ontologyRepository;
		this._ruleEngine = ruleEngine;
//		this.loadLexiconRepository();
    }

	/**
	 * Load all the data from the Manufacturing Lexicon Repository
	 */
	public boolean loadRepository() {
		System.out.println("Load Manufacturing Lexicon Repository");
		return loadRecords(MANUFACTURING_LEXICON_REPOSITORY_FULL_PATH);
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
		
		JSONObject map = (JSONObject) JSONValue.parse(line);
		String conceptLabel = (String) map.get(MappingInfoSchemaParameter.CONCEPT_NAME);

		Collection<String> ontoClassNameList = new HashSet<String>();
		Map<String, Map<String, String>> detailMappedOntoClassList = new LinkedHashMap<String, Map<String, String>>();
		this.concept2OntoClassReferenceMap.put(conceptLabel, ontoClassNameList);
		this.detailConcept2OntoClassMap.put(conceptLabel, detailMappedOntoClassList);
		
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> ontoClassList = (Map<String, Map<String, String>>) map.get(MappingInfoSchemaParameter.MAPPED_ONTOCLASS_LIST);
		for(Map<String, String> mappedOntoClassInfo : ontoClassList.values()){
			/*
			 * 
			 */
			String className = mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_NAME);
			String classNS = mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
			String classURI = mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_URI);
			String similarity = mappedOntoClassInfo.get(MappingInfoSchemaParameter.SIMILARITY);
			String relation = mappedOntoClassInfo.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION);
			String verificationAttempts = mappedOntoClassInfo.get(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS);
			String succeedAttempts = mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS);
			String failedAttempts = mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS);
			String undeterminedCounts = mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS);
			
			System.out.println(conceptLabel + ":" + className);
			/*
			 * 
			 */
			ontoClassNameList.add(className);
			/*
			 * 
			 */
			Map<String, String> detailMappedOntoClassInfo = new LinkedHashMap<String, String>();
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_NAME, className);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, classNS);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_URI, classURI);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.SIMILARITY, similarity);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION, relation);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS, verificationAttempts);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS, succeedAttempts);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS, failedAttempts);
			detailMappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS, undeterminedCounts);
			detailMappedOntoClassList.put(className, detailMappedOntoClassInfo);
		}
	}

	@Override
	public boolean hasConcept(Concept concept){
		return concept2OntoClassReferenceMap.containsKey(concept.getConceptName());
	}

	@Override
	public boolean hasConcept2OntoClassMapping(Concept concept, OntoClassInfo ontoClass) {
		if (!this.hasConcept(concept)) {
			return false;
		} else {
			if(this.getMappedOntoClasses(concept.getConceptName()).contains(ontoClass.getOntClassName())){
				return true;
			} else {
				return false;
			}
		}
	}
	
	private Collection<String> getMappedOntoClasses(String conceptName) {
		return concept2OntoClassReferenceMap.get(conceptName);
	}

	@Override
	public void addNewConcept2OntoClassMappings(Collection<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList) {
		for (IClassifiedInstanceDetailRecord classifiedInstanceInfo : classifiedInstanceDetailInfoList) {
			for (IConcept2OntClassMapping concept2OntClassMappingPair : classifiedInstanceInfo.getConcept2OntClassMappingPairs()){
				if (concept2OntClassMappingPair.isMappedConcept()) {
					this.addNewConcept2OntoClassMapping(concept2OntClassMappingPair.getConcept(), 
							                                                       concept2OntClassMappingPair.getRelation(), 
							                                                       concept2OntClassMappingPair.getMappedOntoClass(), 
							                                                       concept2OntClassMappingPair.getMappingScore());
				}
			}
		}
	}
	
	@Override
	public void addNewConcept2OntoClassMapping(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity) {
		if (this.hasConcept2OntoClassMapping(concept, ontoClass)) {
			/*
			 * The entry for this mapping has already existed
			 */
			return;
		} else if (this.hasConcept(concept)) {
			/*
			 * The entry for the concept has already existed in the
			 * Manufacturing Lexicon. Therefore, add this ontoClass to the entry
			 * of this concept.
			 */
			this.addNewEntryItem(concept, mappingRelationType, ontoClass, similarity);

		} else {
			/*
			 * There is no entry for the concept exists in the Manufacturing
			 * Lexicon. Therefore, create an entry for this concept and add this
			 * ontoClass to this newly created entry.
			 */
			this.createNewEntry(concept, mappingRelationType, ontoClass, similarity);
		}
	}
	
	@Override
	public void updateValidityOfConcept2OntClassMapping(IInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance) {
		System.out.println("   UPDATE VALIDITY Of Concept2OntClassMapping");
		String origOntoClassName = updatedInstance.getOriginalClassName();
		String updatedOntoClassName = updatedInstance.getUpdatedClassName();
		boolean isClassLabelChanged = updatedInstance.isOntClassChanged();
		String instanceClassName = isClassLabelChanged ? updatedOntoClassName : origOntoClassName;
//		_ruleEngine.applyConcept2ClassMappingUpdateRules(instanceClassName, updatedInstance, originalInstance, this);
	}
	
	@Override
	public void updateConcept2OntoClassMappingRelation(Concept concept, OntoClassInfo ontoClass, MappingRelationType mappingRelationType) {
		Map<String, String> mappedClassEntryItem = this.getMappedClassEntryItem(concept, ontoClass);
		if(mappedClassEntryItem != null){
			mappedClassEntryItem.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION, mappingRelationType.toString());
		}
	}
	
	@Override
	public void updateConcept2OntoClassMappingVerificationResult(
			                                                      Concept concept, 
			                                                      OntoClassInfo ontoClass,
			                                                      MappingVericationResult verificationResult
			                                                     ) {
		
		Map<String, String> mappedClassEntryItem = this.getMappedClassEntryItem(concept, ontoClass);
		if(mappedClassEntryItem != null){
			int verication_attempts = Integer.valueOf(mappedClassEntryItem.get(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS));
			int verication_succeed_counts = Integer.valueOf(mappedClassEntryItem.get(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS));
			int verication_failed_counts = Integer.valueOf(mappedClassEntryItem.get(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS));
			int verication_undetermined_counts = Integer.valueOf(mappedClassEntryItem.get(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS));
//			double similarity = Double.valueOf(entryItem.get(MappingInfoSchemaParameter.SIMILARITY));
			verication_attempts++;
			switch (verificationResult) {
			case Succeed: {
				verication_succeed_counts++;
			}
				break;
			case Failed: {
				verication_failed_counts++;
			}
				break;
			case Unknow: {
				verication_undetermined_counts++;
			}
				break;
			}
			mappedClassEntryItem.put(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS, String.valueOf(verication_attempts));
			mappedClassEntryItem.put(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS, String.valueOf(verication_undetermined_counts));
			mappedClassEntryItem.put(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS, String.valueOf(verication_succeed_counts));
			mappedClassEntryItem.put(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS, String.valueOf(verication_failed_counts));
		}
	}
	
	private Map<String, String> getMappedClassEntryItem(Concept concept, OntoClassInfo ontClass){
		if (this.hasConcept2OntoClassMapping(concept, ontClass)) {
			Map<String, Map<String, String>> mappedOntoClassInfoCollection = this.detailConcept2OntoClassMap.get(concept.getConceptName());
			return mappedOntoClassInfoCollection.get(ontClass.getOntClassName());
		} else {
			return null;
		}
	}

	/**
	 * Create an new Entry for a particular concept to the Manufacturing Lexicon
	 * 
	 * @param concept
	 * @param mappingRelationType
	 * @param ontoClass
	 * @param similarity
	 */
	private void createNewEntry(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity) {
		Map<String, Map<String, String>> mappedOntoClassInfoCollection = new LinkedHashMap<String, Map<String,String>>();
		detailConcept2OntoClassMap.put(concept.getConceptName(), mappedOntoClassInfoCollection);
		Map<String, String> mappedOntoClassInfo = this.createNewEntryItem(ontoClass, mappingRelationType, similarity);
		mappedOntoClassInfoCollection.put(ontoClass.getOntClassName(), mappedOntoClassInfo);
		
		// refresh the Manufacturing Lexicon Index accordingly
		Collection<String> classNameCollection = new HashSet<String>();
		classNameCollection.add(ontoClass.getOntClassName());
		concept2OntoClassReferenceMap.put(concept.getConceptName(), classNameCollection);
	}

	
	/**
	 * Add a new Entry Item for a particular concept to the Manufacturing Lexicon
	 * 
	 * @param concept
	 * @param mappingRelationType
	 * @param ontoClass
	 * @param similarity
	 */
	private void addNewEntryItem(Concept concept, MappingRelationType mappingRelationType, OntoClassInfo ontoClass, double similarity) {
		Map<String, Map<String, String>> mappedOntoClassInfoCollection = this.detailConcept2OntoClassMap.get(concept.getConceptName());
		Map<String, String> mappedOntoClassInfo = this.createNewEntryItem(ontoClass, mappingRelationType, similarity);
		mappedOntoClassInfoCollection.put(ontoClass.getOntClassName(), mappedOntoClassInfo);
		
		// refresh the Manufacturing Lexicon Index accordingly
		concept2OntoClassReferenceMap.get(concept.getConceptName()).add(ontoClass.getOntClassName()); 
	}
	/**
	 * @param ontoClass
	 * @return
	 */
	private Map<String, String> createNewEntryItem(OntoClassInfo ontoClass, MappingRelationType mappingRelationType, double similarity) {
		Map<String, String> mappedOntoClassInfo = new LinkedHashMap<String, String>();
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_NAME, ontoClass.getOntClassName());
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, ontoClass.getNameSpace());
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.CLASS_URI, ontoClass.getURI());
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION, mappingRelationType.toString());
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.SIMILARITY, String.valueOf(similarity));
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS, "0");
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS, "0");
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS, "0");
		mappedOntoClassInfo.put(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS, "0");
		return mappedOntoClassInfo;
	}
	
	@Override
	public Collection<String> getAllConcepts(){
		Collection<String> concepts = detailConcept2OntoClassMap.keySet();
		if(concepts == null){
			concepts = new ArrayList<String>();
		}
		return concepts;
	}
	
	@Override
	public Collection<OntoClassInfo> getMappedOntClasses(String conceptName) {
		Collection<OntoClassInfo> mappingOntClassCollection = new ArrayList<OntoClassInfo>();
		Map<String, Map<String, String>> entryItems = detailConcept2OntoClassMap.get(conceptName);

		if (entryItems != null) {
			for (Map<String, String> entryItem : entryItems.values()) {
				String className = entryItem.get(MappingInfoSchemaParameter.CLASS_NAME);
				String classNS = entryItem.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
				String classURI = entryItem.get(MappingInfoSchemaParameter.CLASS_URI);
				mappingOntClassCollection.add(new OntoClassInfo(classURI, classNS, className));
			}
		}

		return mappingOntClassCollection;
	}

	@Override
	public Collection<OntoClassInfo> getMappedOntClasses(Concept concept) {
		return getMappedOntClasses(concept.getConceptName());
	}

	@Override
	public MappingRelationType getConcept2OntClassMappingRelation(Concept concept, OntoClassInfo ontClass){
		Map<String, String> entryItem = detailConcept2OntoClassMap.get(concept.getConceptName()).get(ontClass.getOntClassName());
		String relation = entryItem.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION);
		// Currently only use relatedTo relation between concept and ont-class
		return MappingRelationType.relatedTo;
	}
	
	@Override
	public double getConcept2OntClassMappingSimilarity(Concept concept, OntoClassInfo ontClass){
		Map<String, String> entryItem = detailConcept2OntoClassMap.get(concept.getConceptName()).get(ontClass.getOntClassName());
		double similarity = Double.valueOf(entryItem.get(MappingInfoSchemaParameter.SIMILARITY));
		return similarity;
	}
	
	@Override
	public IConcept2OntClassMappingStatistics getConcept2ClassMappingStatistics(Concept concept, OntoClassInfo ontoClass) throws NoSuchEntryItemException{

		if (!this.hasConcept2OntoClassMapping(concept, ontoClass)) {
			throw new NoSuchEntryItemException("Mapping {" + concept.getConceptName() + "," + ontoClass.getOntClassName() + "} Does Not Exist");
		}

		Map<String, String> entryItem = detailConcept2OntoClassMap.get(concept.getConceptName()).get(ontoClass.getOntClassName());

		double similarity = Double.valueOf(entryItem.get(MappingInfoSchemaParameter.SIMILARITY));
		int verificationAttempts =  Integer.valueOf(entryItem.get(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS));
		int undeterminedCounts = Integer.valueOf(entryItem.get(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS));
		int succeedAttempts = Integer.valueOf(entryItem.get(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS));
		int failedAttempts = Integer.valueOf(entryItem.get(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS));

		Concept2ClassMappingStatistics statistics = new Concept2ClassMappingStatistics();
		statistics.setVerificationAttempts(verificationAttempts);
		statistics.setUndeterminedCounts(undeterminedCounts);
		statistics.setSucceedCounts(succeedAttempts);
		statistics.setFailedCounts(failedAttempts);
		statistics.setSimilarity(similarity);
		return statistics;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean saveRepository() {
		
		StringBuilder entries = new StringBuilder();
		for (String conceptName : detailConcept2OntoClassMap.keySet()) {
			Map<String, Map<String, String>> mappedOntoClassDetails = detailConcept2OntoClassMap.get(conceptName);
			JSONObject jsonDetailRecord = new JSONObject();
			jsonDetailRecord.put(MappingInfoSchemaParameter.CONCEPT_NAME, conceptName);
			jsonDetailRecord.put(MappingInfoSchemaParameter.MAPPED_ONTOCLASS_LIST, mappedOntoClassDetails);
			entries.append(jsonDetailRecord.toJSONString());
			entries.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		return FileAccessor.saveTripleString(MANUFACTURING_LEXICON_REPOSITORY_FULL_PATH, entries.toString());
	}

	@Override
	public void showLexiconIndex() {
		for (String conceptName : concept2OntoClassReferenceMap.keySet()) {
			System.out.println("<" + conceptName + "> mapped to");
			for (String className : concept2OntoClassReferenceMap.get(conceptName)) {
				System.out.println("          <" + className + ">");
			}
		}
	}

	@Override
	public void showRepositoryDetail() {
		for (String conceptName : detailConcept2OntoClassMap.keySet()) {
			Map<String, Map<String, String>> mappedOntoClassDetails = detailConcept2OntoClassMap.get(conceptName);
			System.out.println("Concept Name: " + conceptName);
			for (String className : mappedOntoClassDetails.keySet()) {
				Map<String, String> mappedOntoClassInfo = mappedOntoClassDetails.get(className);
				System.out.println("   Class Name:              " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_NAME));
				System.out.println("   Class NS:                " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_NAMESPACE));
				System.out.println("   Class URI:               " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.CLASS_URI));
				System.out.println("   Relation:                " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.CONCEPT_CLASS_MAPPING_RELATION));
				System.out.println("   Similarity:              " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.SIMILARITY));
				System.out.println("   Verication Attempts:     " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.MAPPING_VERIFICATION_ATTEMPTS));
				System.out.println("   Verication Undetermined: " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_UNDETERMINED_COUNTS));
				System.out.println("   Verication Succeed:      " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_SUCCEED_COUNTS));
				System.out.println("   Vertication Failed:      " + mappedOntoClassInfo.get(MappingInfoSchemaParameter.VERICATION_FAILED_COUNTS));
				System.out.println();
			}
			System.out.println();
		}		
	}

	@Override
	public Map<String, Map<String, String>> getMappedOntClassDetailInformation(String conceptName) {
		// TODO Auto-generated method stub
		return null;
	}
}
