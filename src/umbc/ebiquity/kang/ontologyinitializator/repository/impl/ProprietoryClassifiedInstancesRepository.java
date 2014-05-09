package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingBasicInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingDetailInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class ProprietoryClassifiedInstancesRepository implements IClassifiedInstancesRepository {

	public enum ClassifiedInstancesRepositoryType {
		Basic, Detail, All
	}

	private String _repositoryName;
	private Map<String, MatchedOntProperty> _relationName2RPMappingPairMap;
	private Map<String, IClassifiedInstanceDetailRecord> _instanceURI2DetailedInstanceRecordMap;
	private Map<String, IClassifiedInstanceBasicRecord> _instanceURI2BasicInstanceRecordMap;
	private Set<String> _instanceSet;
	private IManufacturingLexicalMappingRepository _manufacturingLexiconRepository;
	private IOntologyRepository _ontologyRepository;

	private StringBuilder _basicRecords;
	private StringBuilder _detailRecords;
	private StringBuilder _textRecords;
	
	public ProprietoryClassifiedInstancesRepository() throws IOException {
		this.init();
	}

	/***
	 * This Constructor is called when the mapping information is loaded from
	 * local storage.
	 * 
	 * @param repositoryName
	 * @param repoType
	 * @param domainOntologyRepository
	 * @param classificationCorrectionRepository
	 * @param manufacturingLexiconRepository
	 * @throws IOException
	 */
	public ProprietoryClassifiedInstancesRepository(String repositoryName, 
			                     ClassifiedInstancesRepositoryType repoType, 
			                     IOntologyRepository domainOntologyRepository,
			                     IManufacturingLexicalMappingRepository manufacturingLexiconRepository) throws IOException{
		this.init();
		this._repositoryName = repositoryName;
		this._ontologyRepository = domainOntologyRepository;
		this._manufacturingLexiconRepository = manufacturingLexiconRepository;
		boolean succeed = this.loadRepository(repositoryName, repoType);
		if(!succeed){
			throw new IOException("Load Mapping Information Repository Failed");
		}
	}
	
	/***
	 * This Constructor is called When the TS2OntoMappingAlgorithm is finished. That is this
	 * ProprietoryClassifiedInstancesRepository is created out of online mapping.
	 * 
	 * @param domainOntologyRepository
	 * @param manufacturingLexiconRepository
	 * @param classificationCorrectionRepository
	 * @param relation2PropertyMap
	 * @param classifiedInstanceDetailInfoList
	 * @param repositoryName
	 */
	public ProprietoryClassifiedInstancesRepository(String repositoryName,
			                     IOntologyRepository domainOntologyRepository, 
			                     IManufacturingLexicalMappingRepository manufacturingLexiconRepository,
                                 Map<String, MatchedOntProperty> relation2PropertyMap,
                                 Collection<ClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList) {
		this.init();
		this._repositoryName = repositoryName;
		this._manufacturingLexiconRepository = manufacturingLexiconRepository;
		this._ontologyRepository = domainOntologyRepository;
		this._relationName2RPMappingPairMap.putAll(relation2PropertyMap);
		this.populateMappingInfo(classifiedInstanceDetailInfoList);
//		this.addNewlyExtractedConcept2OntoClassMappings(classifiedInstanceDetailInfoList);
	}
	
	private void init(){
		this._relationName2RPMappingPairMap = new LinkedHashMap<String, MatchedOntProperty>();
		this._instanceURI2DetailedInstanceRecordMap = new HashMap<String, IClassifiedInstanceDetailRecord>();
		this._instanceURI2BasicInstanceRecordMap = new HashMap<String, IClassifiedInstanceBasicRecord>();
		this._instanceSet = new HashSet<String>();
	}
	
	@Override
	public MappingBasicInfo getMappingBasicInfo() {

		List<IClassifiedInstanceBasicRecord> classifiedInstanceBasicInfoList = new ArrayList<IClassifiedInstanceBasicRecord>();
		for (IClassifiedInstanceBasicRecord classifiedInstanceBasicInfo : this._instanceURI2BasicInstanceRecordMap.values()) {
			classifiedInstanceBasicInfoList.add(classifiedInstanceBasicInfo);
		}
		Collections.sort(classifiedInstanceBasicInfoList);
		
		List<IMatchedOntProperty> mappedRelationInfoList = new ArrayList<IMatchedOntProperty>();
		for(IMatchedOntProperty mappedRelationInfo: this._relationName2RPMappingPairMap.values()){
			mappedRelationInfoList.add(mappedRelationInfo);
		}
		Collections.sort(mappedRelationInfoList);

		return new MappingBasicInfo(mappedRelationInfoList, classifiedInstanceBasicInfoList); 
	}
	
	@Override
	public MappingDetailInfo getMappingDetailInfo(){
		
		List<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList = new ArrayList<IClassifiedInstanceDetailRecord>();
		for (IClassifiedInstanceDetailRecord classifiedInstanceDetailInfo : this._instanceURI2DetailedInstanceRecordMap.values()) {
			classifiedInstanceDetailInfoList.add(classifiedInstanceDetailInfo);
		}
		Collections.sort(classifiedInstanceDetailInfoList);

		List<IMatchedOntProperty> mappedRelationInfoList = new ArrayList<IMatchedOntProperty>();
		for(IMatchedOntProperty mappedRelationInfo: this._relationName2RPMappingPairMap.values()){
			mappedRelationInfoList.add(mappedRelationInfo);
		}
		Collections.sort(mappedRelationInfoList);

		return new MappingDetailInfo(mappedRelationInfoList, classifiedInstanceDetailInfoList); 
	}
	
	private void populateMappingInfo(Collection<ClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoList) {
		
		/*
		 * Group instances based on the classes they belong to
		 */
		for (ClassifiedInstanceDetailRecord classifiedInstanceDetailInfo : classifiedInstanceDetailInfoList) {
			/*
			 * get information on classified instance including the class name,
			 * class namespace, class URI, instance name and the similarity
			 */
			String className = classifiedInstanceDetailInfo.getOntoClassName();
			String classNS = classifiedInstanceDetailInfo.getOntoClassNameSpace();
			String classURI = classifiedInstanceDetailInfo.getOntoClassURI();
			String instanceName = classifiedInstanceDetailInfo.getInstanceLabel();
			String similarity = String.valueOf(classifiedInstanceDetailInfo.getSimilarity());
			
			/*
			 * 
			 */
			OntoClassInfo ontoClassInfo = new OntoClassInfo(classURI, classNS, className);
			ClassifiedInstanceBasicRecord classifiedInstanceBasicInfo = new ClassifiedInstanceBasicRecord(instanceName, ontoClassInfo, Double.valueOf(similarity));
			this._instanceURI2BasicInstanceRecordMap.put(instanceName, classifiedInstanceBasicInfo);
			this._instanceURI2DetailedInstanceRecordMap.put(instanceName, classifiedInstanceDetailInfo);
			this._instanceSet.add(instanceName);
			
			/*
			 * 
			 */
			this.addProperty2ValuePairs(classifiedInstanceDetailInfo);
//			OntoClassInfo matchedOntClass = classifiedInstanceDetailInfo.getMatchedOntoClass();
//			InstanceTripleSet tripleSet = classifiedInstanceDetailInfo.getTripleSet();
//			classifiedInstanceDetailInfo.clearProperty2Values();
//			for (String nonTaxonomicRel : tripleSet.getCustomRelation()) {
//				MatchedOntProperty pair = _relationName2RPMappingPairMap.get(nonTaxonomicRel);
//				if (pair != null) {
//					String targetPropertyURI = pair.getOntPropertyURI();
//					OntPropertyInfo ontPropertyInfo = _ontologyRepository.getOntPropertyByURI(targetPropertyURI);
//
//					/*
//					 * If the matched property is a global property, we will
//					 * define the value of the predicate as the value of
//					 * this property.
//					 */
//					if (_ontologyRepository.getGlobalOntProperties().contains(ontPropertyInfo)) {
//						Collection<String> relationValues = tripleSet.getCustomRelationValue(nonTaxonomicRel);
//						List<String> ptValues = new ArrayList<String>();
//						classifiedInstanceDetailInfo.addProperty2Values(ontPropertyInfo, ptValues);
//						for (String value : relationValues) {
//							ptValues.add(value);
//						}
//
//					} else if (matchedOntClass.getProperties().contains(ontPropertyInfo)) {
//						/*
//						 * If the matched property is the declared property
//						 * of the best matched class, we will define the
//						 * value of the predicate as the value of this
//						 * property
//						 */
//						Collection<String> relationValues = tripleSet.getCustomRelationValue(nonTaxonomicRel);
//						
//						List<String> ptValues = new ArrayList<String>();
//						classifiedInstanceDetailInfo.addProperty2Values(ontPropertyInfo, ptValues);
//						for (String value : relationValues) {
////							System.out.println("                 with value <" + value + ">");
//							ptValues.add(value);
//						}
//					}
//				}
//			}
		}
		
		
//		for (String relation : relationName2PropertyMap.keySet()) {
//			Relation2OntPropertyMappingPair pair = relationName2PropertyMap.get(relation);
//			if (pair != null) {
//				/*
//				 * get information on mapped relation and property
//				 */
//				String propertyName = pair.getOntPropertyName();
//				String propertyNS = pair.getOntPropertyNameSpace();
//				String propertyURI = pair.getOntPropertyURI();
//				String relationName = pair.getRelationName();
//				double similarity = pair.getSimilarity();
//				
//				System.out.println("Basic Mapping: " + relation + " - " + propertyName);
//				/*
//				 * cluster the relations with the same mapped property for
//				 * further process (e.g., return to client side)
//				 */
//				OntPropertyInfo ontoPropertyInfo = new OntPropertyInfo(propertyURI, propertyNS, propertyName);
////				Map<String, Double> relation2SimilarityMap = ontoProperty2RelationsMap.get(ontoPropertyInfo);
////				if (relation2SimilarityMap == null) {
////					relation2SimilarityMap = new HashMap<String, Double>();
////				} 
////				relation2SimilarityMap.put(relationName, similarity);
////				ontoProperty2RelationsMap.put(ontoPropertyInfo, relation2SimilarityMap);
//				
//				MappedRelationInfo mappedRelationInfo = new MappedRelationInfo(relationName, ontoPropertyInfo, similarity);
//				relation2MappedRelationInfoMap.put(relationName, mappedRelationInfo);
//			}
//		}
	}
	
	
	private void addProperty2ValuePairs(IClassifiedInstanceDetailRecord classifiedInstanceDetailInfo){
		
		OntoClassInfo matchedOntClass = classifiedInstanceDetailInfo.getMatchedOntoClass();
		InstanceTripleSet tripleSet = classifiedInstanceDetailInfo.getTripleSet();
		for (String nonTaxonomicRel : tripleSet.getCustomRelation()) {
			MatchedOntProperty pair = _relationName2RPMappingPairMap.get(nonTaxonomicRel);
			if (pair != null) {
				String targetPropertyURI = pair.getOntPropertyURI();
				OntPropertyInfo ontPropertyInfo = _ontologyRepository.getOntPropertyByURI(targetPropertyURI);

				/*
				 * If the matched property is a global property, we will
				 * define the value of the predicate as the value of
				 * this property.
				 */
				if (_ontologyRepository.getGlobalOntProperties().contains(ontPropertyInfo)) {
					Collection<String> relationValues = tripleSet.getCustomRelationValue(nonTaxonomicRel);
					List<String> ptValues = new ArrayList<String>();
					classifiedInstanceDetailInfo.addProperty2Values(ontPropertyInfo, ptValues);
					for (String value : relationValues) {
						ptValues.add(value);
					}

				} else if (matchedOntClass.getProperties().contains(ontPropertyInfo)) {
					/*
					 * If the matched property is the declared property
					 * of the best matched class, we will define the
					 * value of the predicate as the value of this
					 * property
					 */
					Collection<String> relationValues = tripleSet.getCustomRelationValue(nonTaxonomicRel);
					
					List<String> ptValues = new ArrayList<String>();
					classifiedInstanceDetailInfo.addProperty2Values(ontPropertyInfo, ptValues);
					for (String value : relationValues) {
//						System.out.println("                 with value <" + value + ">");
						ptValues.add(value);
					}
				}
			}
		}
	}
	
	private void createTextRecordOfMappingInfo(Set<String> instanceBlackList){
		System.out.println("### createTextRecordOfMappingInfo ");
		this._textRecords = new StringBuilder();
		for(String instanceName : _instanceURI2DetailedInstanceRecordMap.keySet()){
			
			//
			if(instanceBlackList.contains(instanceName)){
				continue;
			}
			IClassifiedInstanceDetailRecord classifiedInstanceInfo = _instanceURI2DetailedInstanceRecordMap.get(instanceName);
			OntoClassInfo ontoClassInfo = classifiedInstanceInfo.getMatchedOntoClass();
			String instanceOntClassName = ontoClassInfo.getOntClassName();
			
			//
			if (instanceOntClassName.equals("Any"))
				continue;
			
			String instance2OntClass = "<"+instanceName + ">	<" +instanceOntClassName + ">";
			System.out.println("I2C: " + instance2OntClass);
			_textRecords.append("C	" + instance2OntClass);
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			for (IConcept2OntClassMapping concept2OntClassMappingPair : classifiedInstanceInfo.getConcept2OntClassMappingPairs()) {
				
				if (concept2OntClassMappingPair.isMappedConcept()) {
					/*
					 * if the concept has mapped to certain onto-class, record
					 * the basic information of this onto-class
					 */
					Concept concept = new Concept(concept2OntClassMappingPair.getConceptName());
					OntoClassInfo ontoClassInfo2 = concept2OntClassMappingPair.getMappedOntoClass();
					System.out.println("C2C: " + concept.getConceptName() + " " + ontoClassInfo2.getOntClassName());
					String concept2OntClass = "               <" + concept.getConceptName() + ">	<" + ontoClassInfo2.getOntClassName() + ">";
					_textRecords.append(concept2OntClass);
					_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
				}
			}
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		
		for (IMatchedOntProperty mappedRelationInfo : _relationName2RPMappingPairMap.values()) {

			String relationName = mappedRelationInfo.getRelationName();
			String propertyName = mappedRelationInfo.getOntPropertyName();
			String relation2Property = "R	<" + relationName + ">	<" + propertyName + ">";
			_textRecords.append(relation2Property);
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
	}
	
	private void createTextRecordOfMappingInfo(){
		System.out.println("### createTextRecordOfMappingInfo ");
		this._textRecords = new StringBuilder();
		for(String instanceName : _instanceURI2DetailedInstanceRecordMap.keySet()){

			IClassifiedInstanceDetailRecord classifiedInstanceInfo = _instanceURI2DetailedInstanceRecordMap.get(instanceName);
			OntoClassInfo ontoClassInfo = classifiedInstanceInfo.getMatchedOntoClass();
			String instance2OntClass = "<"+instanceName + ">	<" + ontoClassInfo.getOntClassName() + ">";
			System.out.println("I2C: " + instance2OntClass);
			_textRecords.append("C	" + instance2OntClass);
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
			for (IConcept2OntClassMapping concept2OntClassMappingPair : classifiedInstanceInfo.getConcept2OntClassMappingPairs()) {
				
				if (concept2OntClassMappingPair.isMappedConcept()) {
					/*
					 * if the concept has mapped to certain onto-class, record
					 * the basic information of this onto-class
					 */
					Concept concept = new Concept(concept2OntClassMappingPair.getConceptName());
					OntoClassInfo ontoClassInfo2 = concept2OntClassMappingPair.getMappedOntoClass();
					System.out.println("C2C: " + concept.getConceptName() + " " + ontoClassInfo2.getOntClassName());
					String concept2OntClass = "               <" + concept.getConceptName() + ">	<" + ontoClassInfo2.getOntClassName() + ">";
					_textRecords.append(concept2OntClass);
					_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
				}
			}
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		
		for (IMatchedOntProperty mappedRelationInfo : _relationName2RPMappingPairMap.values()) {

			String relationName = mappedRelationInfo.getRelationName();
			String propertyName = mappedRelationInfo.getOntPropertyName();
			String relation2Property = "R	<" + relationName + ">	<" + propertyName + ">";
			_textRecords.append(relation2Property);
			_textRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
	}
	
	private void createJSONRecordOfBasicMappingInfo() {

		this._basicRecords = new StringBuilder();
		for(String instanceName : _instanceURI2BasicInstanceRecordMap.keySet()){

			IClassifiedInstanceBasicRecord basicInstanceInfo =	_instanceURI2BasicInstanceRecordMap.get(instanceName);
			OntoClassInfo ontoClassInfo = basicInstanceInfo.getMatchedOntoClass();
			String similarity = String.valueOf(basicInstanceInfo.getSimilarity());
			
			/*
             * 
             */
			String className = ontoClassInfo.getOntClassName();
			String classNS = ontoClassInfo.getNameSpace();
			String classURI = ontoClassInfo.getURI();
			Map<String, String> jsonBasicRecord = new LinkedHashMap<String, String>();
			jsonBasicRecord.put(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE, MappingInfoSchemaParameter.CLASSIFICATION);
			jsonBasicRecord.put(MappingInfoSchemaParameter.INSTANCE_NAME, instanceName);
			jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_NAME, className);
			jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, classNS);
			jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_URI, classURI);
			jsonBasicRecord.put(MappingInfoSchemaParameter.SIMILARITY, similarity);
			_basicRecords.append(JSONValue.toJSONString(jsonBasicRecord));
			_basicRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		for (IMatchedOntProperty mappedRelationInfo : _relationName2RPMappingPairMap.values()) {

			String relationName = mappedRelationInfo.getRelationName();
			String propertyName = mappedRelationInfo.getOntPropertyName();
			String propertyNS = mappedRelationInfo.getOntPropertyNameSpace();
			String propertyURI = mappedRelationInfo.getOntPropertyURI();
			double similarity = mappedRelationInfo.getSimilarity();
			
			Map<String, String> jsonBasicRecord = new LinkedHashMap<String, String>();
			jsonBasicRecord.put(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE, MappingInfoSchemaParameter.RELATION_MAPPING);
			jsonBasicRecord.put(MappingInfoSchemaParameter.RELATION_NAME, relationName);
			jsonBasicRecord.put(MappingInfoSchemaParameter.PROPERTY_NAME, propertyName);
			jsonBasicRecord.put(MappingInfoSchemaParameter.PROPERTY_NAMESPACE, propertyNS);
			jsonBasicRecord.put(MappingInfoSchemaParameter.PROPERTY_URI, propertyURI);
			jsonBasicRecord.put(MappingInfoSchemaParameter.SIMILARITY, String.valueOf(similarity));
			_basicRecords.append(JSONValue.toJSONString(jsonBasicRecord));
			_basicRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
	}

	@SuppressWarnings("unchecked")
	private void createJSONRecordOfDetailedMappingInfo() {
		System.out.println("@@ createJSONRecordOfDetailedMappingInfo");
		this._detailRecords = new StringBuilder();
		
		for(String instanceName : _instanceURI2DetailedInstanceRecordMap.keySet()){
			
			IClassifiedInstanceDetailRecord classifiedInstanceInfo = _instanceURI2DetailedInstanceRecordMap.get(instanceName);
//			System.out.println(" Instance Name: " + instanceName);
			double overall_similarity = classifiedInstanceInfo.getSimilarity();
			String instanceURI = instanceName;
			OntoClassInfo ontClassInfo = classifiedInstanceInfo.getMatchedOntoClass();
			String ontClassURI = ontClassInfo.getURI();
			String ontClassNS = ontClassInfo.getNameSpace();
			String ontClassName = ontClassInfo.getOntClassName();
//			System.out.println(" Class Name: " + ontClassName);
			List<Map<String, String>> RClassLevel1List = new ArrayList<Map<String, String>>();
			List<Map<String, String>> RClassLevel2List = new ArrayList<Map<String, String>>();
			List<Map<String, String>> RConceptList = new ArrayList<Map<String, String>>();
			Map<String, Collection<String>> property2ValuesMap = new LinkedHashMap<String, Collection<String>>();
			Map<String, Double> classHierarchyNumber2ClosenessScore = new HashMap<String, Double>();
			
			JSONObject jsonDetailRecord = new JSONObject();
			jsonDetailRecord.put(MappingInfoSchemaParameter.CLASS_URI, ontClassURI);
			jsonDetailRecord.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, ontClassNS);
			jsonDetailRecord.put(MappingInfoSchemaParameter.CLASS_NAME, ontClassName);
			jsonDetailRecord.put(MappingInfoSchemaParameter.OVERALL_SIMILARITY, overall_similarity);
			jsonDetailRecord.put(MappingInfoSchemaParameter.INSTANCE_URI, instanceURI);
			jsonDetailRecord.put(MappingInfoSchemaParameter.INSTANCE_NAME, instanceName);
			jsonDetailRecord.put(MappingInfoSchemaParameter.RECOMMENDED_CLASS_LEVEL1, RClassLevel1List);
			jsonDetailRecord.put(MappingInfoSchemaParameter.RECOMMENDED_CLASS_LEVEL2, RClassLevel2List);
			jsonDetailRecord.put(MappingInfoSchemaParameter.RELATED_CONCEPT, RConceptList);
			jsonDetailRecord.put(MappingInfoSchemaParameter.MAPPED_CLASS_HIERARCHY, classHierarchyNumber2ClosenessScore);
			jsonDetailRecord.put(MappingInfoSchemaParameter.PROPERTY, property2ValuesMap);
			
			for (OntoClassInfo firstLevelRecommendedOntoClasses : classifiedInstanceInfo.getFirstLevelRecommendedOntoClasses()) {
				Map<String, String> recommendedClassInfoMap = new LinkedHashMap<String, String>();
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_URI, firstLevelRecommendedOntoClasses.getURI());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, firstLevelRecommendedOntoClasses.getNameSpace());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_NAME, firstLevelRecommendedOntoClasses.getOntClassName());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.SIMILARITY, String.valueOf(firstLevelRecommendedOntoClasses.getSimilarityToConcept()));
				RClassLevel1List.add(recommendedClassInfoMap);
			}
			for (OntoClassInfo SecondLevelRecommendedOntoClasses : classifiedInstanceInfo.getSecondLevelRecommendedOntoClasses()) {
				Map<String, String> recommendedClassInfoMap = new LinkedHashMap<String, String>();
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_URI, SecondLevelRecommendedOntoClasses.getURI());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, SecondLevelRecommendedOntoClasses.getNameSpace());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.CLASS_NAME, SecondLevelRecommendedOntoClasses.getOntClassName());
				recommendedClassInfoMap.put(MappingInfoSchemaParameter.SIMILARITY, String.valueOf(SecondLevelRecommendedOntoClasses.getSimilarityToConcept()));
				RClassLevel2List.add(recommendedClassInfoMap);
			}
			for (IConcept2OntClassMapping concept2OntClassMappingPair : classifiedInstanceInfo.getConcept2OntClassMappingPairs()) {
				Map<String, String> relatedConceptInfoMap = new LinkedHashMap<String, String>();
				relatedConceptInfoMap.put(MappingInfoSchemaParameter.CONCEPT_NAME, concept2OntClassMappingPair.getConceptName());
				RConceptList.add(relatedConceptInfoMap);
				
				if (concept2OntClassMappingPair.isMappedConcept()) {
					/*
					 * if the concept has mapped to certain onto-class, record
					 * the basic information of this onto-class
					 */
					Concept concept = new Concept(concept2OntClassMappingPair.getConceptName());
					OntoClassInfo ontoClassInfo = concept2OntClassMappingPair.getMappedOntoClass();
					if (_manufacturingLexiconRepository.hasConcept2OntoClassMapping(concept, ontoClassInfo)) {
						relatedConceptInfoMap.put(MappingInfoSchemaParameter.IS_MAPPED_CONCEPT, "true");
						relatedConceptInfoMap.put(MappingInfoSchemaParameter.CLASS_URI, ontoClassInfo.getURI());
						relatedConceptInfoMap.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, ontoClassInfo.getNameSpace());
						relatedConceptInfoMap.put(MappingInfoSchemaParameter.CLASS_NAME, ontoClassInfo.getOntClassName());
						
						if(concept2OntClassMappingPair.isHittedMapping()){
							relatedConceptInfoMap.put(MappingInfoSchemaParameter.IS_HITTED_MAPPING, "true");
						} else {
							relatedConceptInfoMap.put(MappingInfoSchemaParameter.IS_HITTED_MAPPING, "false");
						}
						
					} else {
						relatedConceptInfoMap.put(MappingInfoSchemaParameter.IS_MAPPED_CONCEPT, "false");
					}
				} else {
					relatedConceptInfoMap.put(MappingInfoSchemaParameter.IS_MAPPED_CONCEPT, "false");
				}
			}
			
			for (String classHierarchyNumber : classifiedInstanceInfo.getClassHierarchyNumber2ClosenessScoreMap().keySet()) {
				classHierarchyNumber2ClosenessScore.put(classHierarchyNumber, classifiedInstanceInfo.getClassHierarchyNumber2ClosenessScoreMap().get(classHierarchyNumber));
			}

			for (OntPropertyInfo propertyInfo : classifiedInstanceInfo.getProperty2ValuesMap().keySet()) {

				Collection<String> values = classifiedInstanceInfo.getProperty2ValuesMap().get(propertyInfo);
				Collection<String> ptValues = new ArrayList<String>();
				property2ValuesMap.put(propertyInfo.getURI(), ptValues);
				for (String value : values) {
					ptValues.add(value);
				}
			}
			_detailRecords.append(jsonDetailRecord.toJSONString());
			_detailRecords.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		}
	}
	
	@Override
	public boolean saveRepository() {
		return this.saveRepository(_repositoryName);
	}
	
	private boolean saveRepository(String repositoryName) {
		this.createJSONRecordOfBasicMappingInfo();
		this.createJSONRecordOfDetailedMappingInfo();
		if (this.saveBasicMappingInfo(repositoryName) && this.saveDetailedMappingInfo(repositoryName)) {
			return true;
		} else {
			// TODO: to do some clean up work
			return false;
		}
	}
	
	public boolean saveHumanReadableFile(String goldenStandardsFullPath, Set<String> instanceBlackList){
		
		this.createTextRecordOfMappingInfo(instanceBlackList);
//		String dirFullPath = RepositoryParameterConfiguration.getMappingHumanReadableDirectoryFullPath();
		String dirFullPath = goldenStandardsFullPath;
		boolean dirExists = FileUtility.exists(dirFullPath);
		String fileFullName = dirFullPath + _repositoryName;;
		if(dirExists){
		 return FileAccessor.saveTripleString(fileFullName, _textRecords.toString());
		} else {
			boolean succeed = FileUtility.createDirectories(dirFullPath);
			if(succeed){
				return FileAccessor.saveTripleString(fileFullName, _textRecords.toString());
			} else {
				return false;
			}
		}
	}
	
	private boolean saveBasicMappingInfo(String fileName){
		String dirFullPath = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String fileFullName = dirFullPath + fileName;
		return FileAccessor.saveTripleString(fileFullName, _basicRecords.toString());
	}
	
	private boolean saveDetailedMappingInfo(String fileName){
		String dirFullPath = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		String fileFullName = dirFullPath + fileName;
		return FileAccessor.saveTripleString(fileFullName, _detailRecords.toString());
	}

	/**
	 * 
	 * @param mappingFileURI
	 * @param infoType
	 * @return
	 */
	private boolean loadRepository(String repositoryName, ClassifiedInstancesRepositoryType infoType) {

		this._repositoryName = repositoryName;
		String basicInfoDirFullPath = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		String basicInfoFullFileName = basicInfoDirFullPath + _repositoryName;
		String detailInfoDirFullPath = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		String detailInfoFullFileName = detailInfoDirFullPath + _repositoryName;
		
		if (infoType == ClassifiedInstancesRepositoryType.Basic) {
			System.out.println("--------- load basic records -------------------------");
			return this.loadRecords(basicInfoFullFileName, infoType);
		} else if (infoType == ClassifiedInstancesRepositoryType.Detail){
			System.out.println("---------- load detail records ------------------------");
			return this.loadRecords(detailInfoFullFileName, infoType);
		} else {
			System.out.println("---------- load both basic detail records ------------------------");
			boolean loadBasicInfoSucceed = this.loadRecords(basicInfoFullFileName, ClassifiedInstancesRepositoryType.Basic);
			boolean loadDetainInfoSucceed = this.loadRecords(detailInfoFullFileName, ClassifiedInstancesRepositoryType.Detail);
			return loadBasicInfoSucceed && loadDetainInfoSucceed;
		}
	}
	
	private boolean loadRecords(String fileFullName, ClassifiedInstancesRepositoryType infoType){
		
		File file = new File(fileFullName);
		BufferedReader reader = null;
		try{
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				if (ClassifiedInstancesRepositoryType.Basic == infoType) {
					this.loadBasicRecord(line);
				} else if (ClassifiedInstancesRepositoryType.Detail == infoType) {
					this.loadDetailRecord(line);
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
	
	private void loadBasicRecord(String line) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> recordMap = (JSONObject) JSONValue.parse(line);
		String recordType = recordMap.get(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE);
		if (recordType.equals(MappingInfoSchemaParameter.CLASSIFICATION)) {
			
			String instanceName = recordMap.get(MappingInfoSchemaParameter.INSTANCE_NAME);
			String className = recordMap.get(MappingInfoSchemaParameter.CLASS_NAME);
			String classNS = recordMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
			String classURI = recordMap.get(MappingInfoSchemaParameter.CLASS_URI);
			String similarity = recordMap.get(MappingInfoSchemaParameter.SIMILARITY);
			
//			System.out.println("class: " + className + "<" + similarity + ">");
			OntoClassInfo ontoClassInfo = new OntoClassInfo(classURI, classNS, className);			
			ClassifiedInstanceBasicRecord classifiedInstanceInfo = new ClassifiedInstanceBasicRecord(instanceName, ontoClassInfo, Double.valueOf(similarity));
			this._instanceURI2BasicInstanceRecordMap.put(instanceName, classifiedInstanceInfo);
			this._instanceSet.add(instanceName);
			
		} else if (recordType.equals(MappingInfoSchemaParameter.RELATION_MAPPING)) {
			
			String relationName = recordMap.get(MappingInfoSchemaParameter.RELATION_NAME);
			String propertyName = recordMap.get(MappingInfoSchemaParameter.PROPERTY_NAME);
			String propertyNS = recordMap.get(MappingInfoSchemaParameter.PROPERTY_NAMESPACE);
			String propertyURI = recordMap.get(MappingInfoSchemaParameter.PROPERTY_URI);
			double similarity = Double.valueOf(recordMap.get(MappingInfoSchemaParameter.SIMILARITY));
			
//			System.out.println("property: " + propertyName + "<" + similarity + ">");
			MatchedOntProperty mappedRelationInfo = new MatchedOntProperty(relationName, propertyURI, propertyNS,
					                                                                     propertyName, similarity);
			_relationName2RPMappingPairMap.put(relationName, mappedRelationInfo);
		}
//		System.out.println("--------------------------------------");
	}

	
	private void loadDetailRecord(String line) {
		ClassifiedInstanceDetailRecord classifiedInstanceInfo = this.createClassifiedInstanceInfoObject(line);
		String instanceName = classifiedInstanceInfo.getInstanceLabel();
		this._instanceURI2DetailedInstanceRecordMap.put(instanceName, classifiedInstanceInfo);
		this._instanceSet.add(instanceName);
//		System.out.println("--------------------------------------");
	}
	
	/**
	 * Create Java representation of classified instance from JSON record
	 * 
	 * @param line - a JSON string representation of instance record
	 * @return a Java representation of classified instance
	 */
	public ClassifiedInstanceDetailRecord createClassifiedInstanceInfoObject(String line) {

		JSONObject jsonMap = (JSONObject) JSONValue.parse(line);
		String instanceURI = (String) jsonMap.get(MappingInfoSchemaParameter.INSTANCE_URI);
		String instanceName = (String) jsonMap.get(MappingInfoSchemaParameter.INSTANCE_NAME);
		String classURI = (String) jsonMap.get(MappingInfoSchemaParameter.CLASS_URI);
		String classNS = (String) jsonMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String className = (String) jsonMap.get(MappingInfoSchemaParameter.CLASS_NAME);
		double sim = (Double) jsonMap.get(MappingInfoSchemaParameter.OVERALL_SIMILARITY);

//		System.out.println("Instance URI: " + instanceURI);
//		System.out.println("Instance Name: " + instanceName);
//		System.out.println("Class URI: " + classURI);
//		System.out.println("Class Name: " + className);
//		System.out.println("Similarity: " + sim);

		@SuppressWarnings("unchecked")
		List<Map<String, String>> recommendedClassLv1 = (List<Map<String, String>>) jsonMap.get(MappingInfoSchemaParameter.RECOMMENDED_CLASS_LEVEL1);
		Collection<OntoClassInfo> _rcl1 = new ArrayList<OntoClassInfo>();
		for (Map<String, String> map : recommendedClassLv1) {
			String ontoClassURI = map.get(MappingInfoSchemaParameter.CLASS_URI);
			String ontoClassNS = map.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
			String ontoClassName = map.get(MappingInfoSchemaParameter.CLASS_NAME);
			String ontoClassSimilarity = map.get(MappingInfoSchemaParameter.SIMILARITY);
			OntoClassInfo ontoClassInfo = new OntoClassInfo(ontoClassURI, ontoClassNS, ontoClassName);
			ontoClassInfo.setSimilarityToConcept(Double.valueOf(ontoClassSimilarity));
			_rcl1.add(ontoClassInfo);
//			System.out.println("recom 1: " + ontoClassName + " : " + ontoClassURI + " : " + ontoClassSimilarity);
		}

		@SuppressWarnings("unchecked")
		List<Map<String, String>> recommendedClassLv2 = (List<Map<String, String>>) jsonMap.get(MappingInfoSchemaParameter.RECOMMENDED_CLASS_LEVEL2);
		Collection<OntoClassInfo> _rcl2 = new ArrayList<OntoClassInfo>();
		for (Map<String, String> map : recommendedClassLv2) {
			String ontoClassURI = map.get(MappingInfoSchemaParameter.CLASS_URI);
			String ontoClassNS = map.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
			String ontoClassName = map.get(MappingInfoSchemaParameter.CLASS_NAME);
			String ontoClassSimilarity = map.get(MappingInfoSchemaParameter.SIMILARITY);
			OntoClassInfo ontoClassInfo = new OntoClassInfo(ontoClassURI, ontoClassNS, ontoClassName);
			ontoClassInfo.setSimilarityToConcept(Double.valueOf(ontoClassSimilarity));
			_rcl2.add(ontoClassInfo);
//			System.out.println("recom 2: " + ontoClassName + " : " + ontoClassURI + " : " + ontoClassSimilarity);
		}

		@SuppressWarnings("unchecked")
		List<Map<String, String>> relatedConcepts = (List<Map<String, String>>) jsonMap.get(MappingInfoSchemaParameter.RELATED_CONCEPT);
		Collection<IConcept2OntClassMapping> concept2OntClassMappingPairs = new LinkedHashSet<IConcept2OntClassMapping>();
		for (Map<String, String> map : relatedConcepts) {
			String conceptName = map.get(MappingInfoSchemaParameter.CONCEPT_NAME);
			Concept concept = new Concept(conceptName);
			
			String isMappedConcept = map.get(MappingInfoSchemaParameter.IS_MAPPED_CONCEPT);
			String isHittedMapping = map.get(MappingInfoSchemaParameter.IS_HITTED_MAPPING);
			if (isMappedConcept.equals("true")) {
				/*
				 * only when the concept has mapped to a onto-class, create the
				 * mapped onto-class java object
				 */
				String ontoClassURI = map.get(MappingInfoSchemaParameter.CLASS_URI);
				String ontoClassNS = map.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
				String ontoClassName = map.get(MappingInfoSchemaParameter.CLASS_NAME);
				OntoClassInfo ontoClassInfo = new OntoClassInfo(ontoClassURI, ontoClassNS, ontoClassName);
				
				if(_manufacturingLexiconRepository.hasConcept2OntoClassMapping(concept, ontoClassInfo)){
					
					double similarity = this._manufacturingLexiconRepository.getConcept2OntClassMappingSimilarity(concept, ontoClassInfo);
					MappingRelationType relation = this._manufacturingLexiconRepository.getConcept2OntClassMappingRelation(concept, ontoClassInfo);
					Concept2OntClassMapping pair = new Concept2OntClassMapping(concept, relation, ontoClassInfo, similarity);
					if(isHittedMapping.equals("true")){
						pair.setHittedMapping(true);
					} else {
						pair.setHittedMapping(false);
					}
					concept2OntClassMappingPairs.add(pair);
//					System.out.println("related concept: " + conceptName + " : " + ontoClassInfo.getOntClassName());
				} else {
					Concept2OntClassMapping pair = new Concept2OntClassMapping(concept, MappingRelationType.relatedTo, ontoClassInfo, 0.7);
					if (isHittedMapping.equals("true")) {
						pair.setHittedMapping(true);
					} else {
						pair.setHittedMapping(false);
					}
					concept2OntClassMappingPairs.add(pair);
//					System.out.println("related concept: " + conceptName + " : " + ontoClassInfo.getOntClassName());
				}

			} else {
				Concept2OntClassMapping pair= new Concept2OntClassMapping(concept);
				concept2OntClassMappingPairs.add(pair);
			}
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Double> classHierarchyNumber2ClosenessScoreMap = (JSONObject) jsonMap.get(MappingInfoSchemaParameter.MAPPED_CLASS_HIERARCHY);

		/*
		 * get all the super classes of the class that the instance belongs to
		 */
		OntoClassInfo instanceClassInfo = new OntoClassInfo(classURI, classNS, className);
		Collection<OntoClassInfo> ontClassSet = _ontologyRepository.getUpwardCotopy(instanceClassInfo);
		instanceClassInfo.addSuperOntClassesInHierarchy(ontClassSet);

		InstanceTripleSet instanceTripleSet = new InstanceTripleSet(instanceName);
		MatchedOntoClassInfo matchedOntoClassInfo = new MatchedOntoClassInfo();
		matchedOntoClassInfo.setMatchedOntoClassInfo(instanceClassInfo);
		matchedOntoClassInfo.setSimilarity(sim);
		matchedOntoClassInfo.setFirstLevelRecommendedOntoClasses(_rcl1);
		matchedOntoClassInfo.setSecondLevelRecommendedOntoClasses(_rcl2);
		matchedOntoClassInfo.recordClosenessScoresForClassHierarchy2(classHierarchyNumber2ClosenessScoreMap);
		
		ClassifiedInstanceDetailRecord classifiedInstanceDetailInfo = new ClassifiedInstanceDetailRecord(instanceTripleSet, matchedOntoClassInfo);
		classifiedInstanceDetailInfo.setConcept2OntClassMappingPairs(concept2OntClassMappingPairs);
		
		JSONObject property = (JSONObject) jsonMap.get(MappingInfoSchemaParameter.PROPERTY);
		@SuppressWarnings("unchecked")
		Map<String, List<String>> map = property;
		for (String key : map.keySet()) {
			OntPropertyInfo ontPropertyInfo = _ontologyRepository.getOntPropertyByURI(key);
//			System.out.println("property URI: " + ontPropertyInfo.getURI());
//			System.out.println("property NS: " + ontPropertyInfo.getNamespace());
//			System.out.println("property Name: " + ontPropertyInfo.getLocalName());
			@SuppressWarnings("unchecked")
			List<String> ptValues = (List<String>) property.get(key);
//			for (String value : ptValues) {
//				System.out.println("    value: " + value);
//			}
			classifiedInstanceDetailInfo.addProperty2Values(ontPropertyInfo, ptValues); // add properties this instance has and the values of these properties
		}
		return classifiedInstanceDetailInfo;
	}

	@Override
	public IClassifiedInstanceBasicRecord getClassifiedInstanceBasicRecordByInstanceName(String instanceName) {
		return this._instanceURI2BasicInstanceRecordMap.get(instanceName);
	}

	@Override
	public IClassifiedInstanceDetailRecord getClassifiedInstanceDetailRecordByInstanceName(String instanceName) {
		return _instanceURI2DetailedInstanceRecordMap.get(instanceName);
	}
	
	@Override
	public Collection<IClassifiedInstanceDetailRecord> getAllClassifiedInstanceDetailRecords() {
		return _instanceURI2DetailedInstanceRecordMap.values();
	}
	
	@Override
	public MatchedOntProperty getMatchedOntProperty(String relationLabel){
		return _relationName2RPMappingPairMap.get(relationLabel);
	}
	
	@Override
	public void updateInstance(IInstanceRecord updatedInstance) {
		if (updatedInstance.isDeletedInstance()) {
			this.deleteInstance(updatedInstance);
		} else {
			this.updateInstanceClass(updatedInstance);
			this.updateInstanceLabel(updatedInstance);
		}

	}
	
	private void deleteInstance(IInstanceRecord instance) {
		String originalLabel = instance.getOriginalInstanceName();
		_instanceURI2BasicInstanceRecordMap.remove(originalLabel);
		_instanceURI2DetailedInstanceRecordMap.remove(originalLabel);
		_instanceSet.remove(originalLabel);
	}

	private void updateInstanceLabel(IInstanceRecord updatedInstance) {
		String originalLabel = updatedInstance.getOriginalInstanceName();
		IClassifiedInstanceDetailRecord originalDetailedInstanceRecord1 = this._instanceURI2DetailedInstanceRecordMap.get(originalLabel);
		originalDetailedInstanceRecord1.setConcept2OntClassMappingPairs(updatedInstance.getConcept2OntClassMappingPairs());
		
		if (updatedInstance.isLabelChanged()) {
			System.out.println("###   INSTANCE LABEL CHANGED");
//			String originalLabel = updatedInstance.getOriginalInstanceName();
			String newLabel = updatedInstance.getUpdatedInstance();
			IClassifiedInstanceBasicRecord originalBasicInstanceRecord = this._instanceURI2BasicInstanceRecordMap.get(originalLabel);
			IClassifiedInstanceDetailRecord originalDetailedInstanceRecord = this._instanceURI2DetailedInstanceRecordMap.get(originalLabel);
			originalBasicInstanceRecord.setInstanceName(newLabel);
			originalDetailedInstanceRecord.setInstanceName(newLabel);
			_instanceURI2BasicInstanceRecordMap.put(newLabel, originalBasicInstanceRecord);
			_instanceURI2BasicInstanceRecordMap.remove(originalLabel);
			_instanceURI2DetailedInstanceRecordMap.put(newLabel, originalDetailedInstanceRecord);
			_instanceURI2DetailedInstanceRecordMap.remove(originalLabel);
			_instanceSet.add(newLabel);
			_instanceSet.remove(originalLabel);
			
//			IClassifiedInstanceBasicRecord originalBasicInstanceRecord2  = _instanceURI2BasicInstanceRecordMap.get(newLabel);
//			IClassifiedInstanceDetailRecord originalDetailedInstanceRecord2 = _instanceURI2DetailedInstanceRecordMap.get(newLabel);
//			String changedInstanceLabel1 = originalBasicInstanceRecord2.getInstanceLabel();
//			String changedInstanceLabel2 = originalDetailedInstanceRecord2.getInstanceLabel();
//			System.out.println(changedInstanceLabel1 + " " + changedInstanceLabel2);
			
		}
	}
	
	private void updateInstanceClass(IInstanceRecord updatedInstance) {
		if (updatedInstance.isOntClassChanged()) {
			System.out.println("   INSTANCE CLASS CHANGED");
			String instanceLabel = updatedInstance.getOriginalInstanceName();
			String classLabel = updatedInstance.getUpdatedClassName();
			this.updateInstanceClass(instanceLabel, classLabel);
		}
	}

	@Override
	public void updateInstanceClass(String instanceLabel, String classLabel) {
		IClassifiedInstanceBasicRecord originalBasicInstanceRecord = this._instanceURI2BasicInstanceRecordMap.get(instanceLabel);
		IClassifiedInstanceDetailRecord originalDetailedInstanceRecord = this._instanceURI2DetailedInstanceRecordMap.get(instanceLabel);

		OntoClassInfo ontoClassForBasicInstanceRecord = originalBasicInstanceRecord.getMatchedOntoClass();
		ontoClassForBasicInstanceRecord.setLabel(classLabel);
		ontoClassForBasicInstanceRecord.setURI(ontoClassForBasicInstanceRecord.getNameSpace() + classLabel);
		System.out.println("### Update Instance Class Label to: " + classLabel);
		OntoClassInfo ontoClassForDetailInstanceRecord = this._ontologyRepository.getHeavyWeightOntClassByName(classLabel);
		
		
		System.out.println(ontoClassForDetailInstanceRecord.getHierarchyNumber());
//		ontoClassForDetailInstanceRecord.addProperties(_ontologyRepository.getDeclaredOntProperties(ontoClassForDetailInstanceRecord));
//		ontoClassForDetailInstanceRecord.addSuperOntClassesInHierarchy(_ontologyRepository.getSuperOntoClassesInClassPath(ontoClassForDetailInstanceRecord));
//		ontoClassForDetailInstanceRecord.setHierarchyNumber(_ontologyRepository.getOntClassHierarchyNumber(ontoClassForDetailInstanceRecord)); 
//		ontoClassForDetailInstanceRecord.setLocalPathCode(_ontologyRepository.getLocalPathCode(ontoClassForDetailInstanceRecord)); 

		originalDetailedInstanceRecord.setMatchedOntoClass(ontoClassForDetailInstanceRecord);
		originalDetailedInstanceRecord.setSimilarity(0.8);
		originalDetailedInstanceRecord.getMatchedOntoClassInfo().setMatchedOntoClassInfo(ontoClassForDetailInstanceRecord);
		originalDetailedInstanceRecord.getMatchedOntoClassInfo().setSimilarity(0.8);
		originalDetailedInstanceRecord.getMatchedOntoClassInfo().setClassHierarchyNumber(ontoClassForDetailInstanceRecord.getHierarchyNumber());
		originalDetailedInstanceRecord.clearProperty2Values();
		this.addProperty2ValuePairs(originalDetailedInstanceRecord);
		
//		IClassifiedInstanceBasicRecord originalBasicInstanceRecord2 = this._instanceURI2BasicInstanceRecordMap.get(instanceLabel);
//		IClassifiedInstanceDetailRecord originalDetailedInstanceRecord2 = this._instanceURI2DetailedInstanceRecordMap.get(instanceLabel);
//		String changedClassLabel1 = originalBasicInstanceRecord2.getOntoClassName();
//		String changedClassLabel2 = originalDetailedInstanceRecord2.getOntoClassName();
//		System.out.println("&&& " + changedClassLabel1 + " " + changedClassLabel2);
	}

	@Override
	public Set<String> getInstanceSet(){
		return _instanceURI2DetailedInstanceRecordMap.keySet();
	}

	@Override
	public Set<String> getRelationSet(){
		return _relationName2RPMappingPairMap.keySet();
	}
	
	@Override
	public void showRepositoryDetail() {
		System.out.println("SHOW REPOSITORY DETAIL");
		for (String instanceName : _instanceURI2DetailedInstanceRecordMap.keySet()) {
			System.out.println("--------------------------------------------------------");
			IClassifiedInstanceDetailRecord classifiedInstanceInfo = _instanceURI2DetailedInstanceRecordMap.get(instanceName);
			double overall_similarity = classifiedInstanceInfo.getSimilarity();
			OntoClassInfo ontClassInfo = classifiedInstanceInfo.getMatchedOntoClass();
			String ontClassURI = ontClassInfo.getURI();
//			String ontClassNS = ontClassInfo.getNameSpace();
			String ontClassName = ontClassInfo.getOntClassName();
			
			System.out.println("Instance Name: " + instanceName);
			System.out.println("Class Name: " + ontClassName);
			System.out.println("Class URI: " + ontClassURI);
			System.out.println();
			
			for (String hierarchyNumber : classifiedInstanceInfo.getClassHierarchyNumber2ClosenessScoreMap().keySet()) {
				System.out.println("Hierarchy: " + hierarchyNumber + "  <"
						+ classifiedInstanceInfo.getClassHierarchyNumber2ClosenessScoreMap().get(hierarchyNumber) + ">");
			}
			System.out.println("Similarity: " + overall_similarity);
			System.out.println();
			
			for (OntoClassInfo firstLevelRecommendedOntoClasses : classifiedInstanceInfo.getFirstLevelRecommendedOntoClasses()) {
				System.out.println("with FR Class: [" + firstLevelRecommendedOntoClasses.getOntClassName() + "]");
			}
			for (OntoClassInfo SecondLevelRecommendedOntoClasses : classifiedInstanceInfo.getSecondLevelRecommendedOntoClasses()) {
				System.out.println("with SR Class: [" + SecondLevelRecommendedOntoClasses.getOntClassName() + "]");
			}
			for (IConcept2OntClassMapping concept2OntClassMappingPair : classifiedInstanceInfo.getConcept2OntClassMappingPairs()) {
				if (concept2OntClassMappingPair.isMappedConcept()) {
					System.out.println("with RM Concept: [" + concept2OntClassMappingPair.getConceptName() + "]");
					OntoClassInfo ontoClassInfo = concept2OntClassMappingPair.getMappedOntoClass();
					System.out.println("        Mapped to: [" + ontoClassInfo.getURI() + "]");
					if(concept2OntClassMappingPair.isHittedMapping()){
						System.out.println("        *** Hitted Mapping");
					}
				} else {
					System.out.println("with RU Concept: [" + concept2OntClassMappingPair.getConceptName() + "]");
				}
			}

//			for (OntPropertyInfo propertyInfo : classifiedInstanceInfo.getProperty2ValuesMap().keySet()) {
//				Collection<String> values = classifiedInstanceInfo.getProperty2ValuesMap().get(propertyInfo);
//				Collection<String> ptValues = new ArrayList<String>();
//				for (String value : values) {
//					ptValues.add(value);
//				}
//			}
		}
	}
	
	@Override
	public String getRepositoryName(){
		return this._repositoryName;
	}
	
	public IManufacturingLexicalMappingRepository getManufacturingLexicalMappingRepository(){
		return this._manufacturingLexiconRepository;
	}

}
