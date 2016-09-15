package umbc.ebiquity.kang.instanceconstructor.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityNode;
import umbc.ebiquity.kang.instanceconstructor.model.Triple.BuiltinPredicate;
import umbc.ebiquity.kang.instanceconstructor.model.Triple.BuiltinType;
import umbc.ebiquity.kang.instanceconstructor.model.Triple.PredicateType;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;

/**
 * 
 * Stores RDF Triples extracted from Entity Graph or loaded from local storage
 * (e.g., file system or data base)
 * 
 * @author Yan Kang
 * 
 */
public class InstanceDescriptionModel implements IInstanceDescriptionModel {
	
//	private String projectDir;
	private String tripleStoreURI;
	private String tripleStoreName;
	private int numberOfRelations;
	private int numberOfTriples;
//	private String storeDirectory = "/TripleStorage/";
	/**
	 * This Set stores all the triples
	 */
	private Set<Triple> theWholeTripleSet;
	
	private Set<Triple> relationTypeTriple;
	
	/**
	 * This Map maps subjects to their group
	 */
	private Map<String, InstanceTripleSet> subject2TripleSetMap;
	
	/**
	 * This Set stores all triples with custom predicates
	 */
	private Set<Triple> triplesWithCustomRelation;
	
	/**
	 * This Set stores triples with predicates that are property subsumption relation
	 */
	private Set<Triple> triplesWithPropertySubsumptionPredicate;
	
	/**
	 * This Set stores triples with predicates that are class subsumption relation
	 */
	private Set<Triple> triplesWithClassSubsumptionPredicate;
	
	/**
	 * 
	 */
	private Set<Triple> triplesOfInstance2ConceptsRelation;
	
	/**
	 * This Map maps predicates to objects of these predicates
	 */
	private Map<String, Set<String>> customRelation2ObjectMap;
	
	/**
	 * This Map maps predicates to subjects of these predicates
	 */
	private Map<String, Set<String>> customRelation2SubjectMap;
	
	/**
	 * 
	 */
	private Map<EntityNode, Set<EntityNode>> classNodeDescendantMap;
	
	/**
	 * 
	 */
	private Map<EntityNode, Set<EntityNode>> classNodePrecedentMap;
	
	/**
	 * 
	 */
	private Map<EntityNode, Set<EntityNode>> propertyNodeDescendantMap;

	/**
	 * 
	 */
	private Map<EntityNode, Set<EntityNode>> propertyNodePrecedentMap;
	
	
	private static final String TRIPLE_REPOSITORY_DIRECTORY_FULL_PATH = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
	

	public InstanceDescriptionModel() {
		this.init();
	}

	/**
	 * 
	 * @param knowledgeBaseURI
	 * @param knowledgeBaseName
	 * @param tripleSet
	 */
	public InstanceDescriptionModel(Collection<Triple> tripleSet, URL webSiteURL) {
		this.init();
		this.tripleStoreURI = webSiteURL.toString();
		this.theWholeTripleSet.addAll(tripleSet);
		this.tripleAssignment(tripleSet);
		this.constructHierarchy(classNodeDescendantMap, classNodePrecedentMap, triplesWithClassSubsumptionPredicate);
		this.constructHierarchy(propertyNodeDescendantMap, propertyNodePrecedentMap, triplesWithPropertySubsumptionPredicate);
	}
	
	private void init(){
		/*
		 * 
		 */
//		this.projectDir = System.getProperty("user.dir");
		this.theWholeTripleSet = new LinkedHashSet<Triple>();
		this.subject2TripleSetMap = new LinkedHashMap<String, InstanceTripleSet>();
		this.relationTypeTriple = new HashSet<Triple>();
	
		this.triplesWithCustomRelation = new LinkedHashSet<Triple>();
		this.triplesWithPropertySubsumptionPredicate = new LinkedHashSet<Triple>();
		this.triplesWithClassSubsumptionPredicate = new LinkedHashSet<Triple>();
		this.triplesOfInstance2ConceptsRelation = new LinkedHashSet<Triple>();
		this.customRelation2ObjectMap = new LinkedHashMap<String, Set<String>>();
		this.customRelation2SubjectMap = new LinkedHashMap<String, Set<String>>();
		this.classNodeDescendantMap = new LinkedHashMap<EntityNode, Set<EntityNode>>();
		this.classNodePrecedentMap = new LinkedHashMap<EntityNode, Set<EntityNode>>();
		this.propertyNodeDescendantMap = new LinkedHashMap<EntityNode, Set<EntityNode>>();
		this.propertyNodePrecedentMap = new LinkedHashMap<EntityNode, Set<EntityNode>>();
	}

	
	/**
	 * This method does the groundwork including group triples based on their
	 * subjects, types of predicate and predicates themselves
	 */
	private void tripleAssignment(Collection<Triple> tripleSet) {

		for (Triple triple : tripleSet) {
			PredicateType predicateType = triple.getPredicateType();
			String subject = triple.getSubject().trim();
			
			/*
			 * Group triples based on subjects of triples
			 */
			InstanceTripleSet instanceTripleSet;
			if(subject2TripleSetMap.containsKey(subject)){
				instanceTripleSet = subject2TripleSetMap.get(subject);
			} else {
				 instanceTripleSet = new InstanceTripleSet(subject);
				 subject2TripleSetMap.put(subject, instanceTripleSet);
			}

			/*
			 * Separate triples based on the type of predicate. There are two
			 * types of predicate: Custom and Builtin 
			 * (1) If the type is Custom, it is highly likely that the predicate corresponds a property of the ontology 
			 * (2) If the type is Builtin, it is likely that the predicate corresponds a subsumption relation.
			 */
			if (PredicateType.Custom == predicateType) {
				
				triplesWithCustomRelation.add(triple);
				instanceTripleSet.addNonTaxonomicTriple(triple);
				String customRelation = triple.getPredicate();
				/*
				 * Create HashMap that maps custom predicates to their subjects.
				 */
				Set<String> relationSubjects = null;
				if(customRelation2SubjectMap.containsKey(customRelation)){
					relationSubjects = customRelation2SubjectMap.get(customRelation);
				}else{
					relationSubjects = new HashSet<String>();
					customRelation2SubjectMap.put(customRelation, relationSubjects);
				}
				relationSubjects.add(subject);
				
				/*
				 * Create HashMap that maps custom predicates to their objects.
				 */
				String object= triple.getObject();
				Set<String> relationObjects = null;
				if(customRelation2ObjectMap.containsKey(customRelation)){ 
					relationObjects = customRelation2ObjectMap.get(customRelation);
				}else{
					relationObjects = new HashSet<String>();
					customRelation2ObjectMap.put(customRelation, relationObjects);
				}
				relationObjects.add(object);
				numberOfTriples++;
				
			} else if (PredicateType.Builtin == predicateType) {
				if (BuiltinPredicate.SubRole.toString().equals(triple.getPredicate())) {
					triplesWithPropertySubsumptionPredicate.add(triple);
					instanceTripleSet.addTaxonomicTriple(triple);
					numberOfTriples++;
				} else if (BuiltinPredicate.SubConcept.toString().equals(triple.getPredicate())) {
					triplesWithClassSubsumptionPredicate.add(triple);
					instanceTripleSet.addTaxonomicTriple(triple);
					numberOfTriples++;
				} else if (BuiltinPredicate.hasConcept.toString().equals(triple.getPredicate())) {
					triplesOfInstance2ConceptsRelation.add(triple);
					instanceTripleSet.addInstance2ConceptTriple(triple);
					numberOfTriples++;
				} else if (BuiltinPredicate.isTypeOf.toString().equals(triple.getPredicate())){
					relationTypeTriple.add(triple);
					numberOfRelations++;
					numberOfTriples++;
				}
			}
		}
	}

	private void constructHierarchy(Map<EntityNode, Set<EntityNode>> nodeDescendantMap, Map<EntityNode, Set<EntityNode>> nodePrecedentMap,
			Set<Triple> tripleSet) {

		for (Triple triple : tripleSet) {
			// if (triple.getBuiltinPredicate() == BuiltinPredicate.SubClass ||
			// triple.getBuiltinPredicate() == BuiltinPredicate.Type) {
			EntityNode node = new EntityNode(triple.getObject());
			EntityNode descendant = new EntityNode(triple.getSubject());
			if (nodeDescendantMap.containsKey(node)) {
				nodeDescendantMap.get(node).add(descendant);
			} else {
				Set<EntityNode> descendantSet = new LinkedHashSet<EntityNode>();
				descendantSet.add(descendant);
				nodeDescendantMap.put(node, descendantSet);
			}

			if (nodePrecedentMap.containsKey(descendant)) {
				nodePrecedentMap.get(descendant).add(node);
			} else {
				Set<EntityNode> precedentSet = new LinkedHashSet<EntityNode>();
				precedentSet.add(node);
				nodePrecedentMap.put(descendant, precedentSet);
			}
			// }
		}
	}
	
//	/**
//	 * 
//	 * @param tripleStoreURI
//	 * @return
//	 */
//	private String getTripleStoreName(String tripleStoreURI) {
//		int indexOfFirstPeriod = tripleStoreURI.indexOf(".");
//		String temp = tripleStoreURI.substring(indexOfFirstPeriod + 1);
//		indexOfFirstPeriod = temp.indexOf(".");
//		temp = temp.substring(0, indexOfFirstPeriod);
//		return temp;
//	}

	/**
	 * Save triples to local storage (i.e., file system) in the format of JSON.
	 * If succeed, return true, otherwise return false. <br/>
	 * 
	 * There are three types of records: <Strong>metadata</Strong>, <Strong>relation-to-property mapping
	 * (R2P)</Strong> and <Strong>concept-of-instance mapping (CoI)</Strong>. Every record has a Record
	 * Type attribute to indicate the type of this record. <br/>
	 * 
	 * <ul>
	 * <li>Record of metadata type has two additional attributes:
	 * Triple_Store_URI and Triple_Store_Name.</li>
	 * 
	 * <li>Record of R2P type has additional attributes: Subject, Predicate,
	 * Object, Normalized Subject, Normalized Subject and Predicate Type.</li>
	 * 
	 * <li>Record of CoI type has additional attributes: Subject, Predicate,
	 * Object, Normalized Subject, Normalized Subject, Predicate Type and
	 * Is_From_Instance.</li>
	 * </ul>
	 * 
	 * @return true if save succeed, false otherwise
	 */
	public boolean save(String tripleRepositoryFileName) {
		System.out.println("Saving Extracted Triples ...");
//		this.tripleStoreURI = tripleStoreURI;
		this.tripleStoreName = tripleRepositoryFileName;
		
		boolean hasTriples = false;
		StringBuilder triplesStringBuilder = new StringBuilder();
		/*
		 * create data record for meta-data
		 */
		Map<String, String> metaDataRecord = new LinkedHashMap<String, String>();
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA);
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_URI, this.tripleStoreURI);
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES, String.valueOf(this.numberOfTriples));
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS, String.valueOf(this.numberOfRelations));
		triplesStringBuilder.append(JSONValue.toJSONString(metaDataRecord));
		triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
		
		for (InstanceTripleSet instanceTripleSet : this.subject2TripleSetMap.values()) {
			hasTriples = true;
			String subjectLabel = instanceTripleSet.getSubjectLabel();
			Map<String, Set<String>> relation2ValueMap = instanceTripleSet.getRelation2ValueMap();
			Map<String, Set<Concept>> instance2ConceptSetMap = instanceTripleSet.getInstance2ConceptualSetMap();
			for (String relationLabel : relation2ValueMap.keySet()) {
				/*
				 * create data records for relation-values mappings
				 */
				Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
				for (String valueLabel : relation2ValueMap.get(relationLabel)) {
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_VALUE);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_SUBJECT, subjectLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE, relationLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_OBJECT, valueLabel);
					
					String predicateTypeStr = "";
					if (Triple.BuiltinPredicateSet.contains(relationLabel)) {
						predicateTypeStr = PredicateType.Builtin.toString();
					} else {
						predicateTypeStr = PredicateType.Custom.toString();
					}
					
					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE, predicateTypeStr);
					triplesStringBuilder.append(JSONValue.toJSONString(tripleRecord));
					triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
					numberOfTriples++;
				}
			}
			
			for (String relationLabel : instance2ConceptSetMap.keySet()) {
				/*
				 * create data records for class-concept mappings
				 */
				Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
				for (Concept concept : instance2ConceptSetMap.get(relationLabel)) {
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_CONCEPT_OF_INSTANCE);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_SUBJECT, subjectLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE, relationLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_OBJECT, concept.getConceptName());
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_OBJECT_AS_CONCEPT_SCORE, String.valueOf(concept.getScore()));

					/*
					 * 
					 */
					String predicateTypeStr = "";
					if (Triple.BuiltinPredicateSet.contains(relationLabel)) {
						predicateTypeStr = PredicateType.Builtin.toString();
					} else {
						predicateTypeStr = PredicateType.Custom.toString();
					}

					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE, predicateTypeStr);
					/*
					 * 
					 */
					String isFromInstance = String.valueOf(concept.isFromInstance());
					tripleRecord.put(MappingInfoSchemaParameter.IS_FROM_INSTANCE, isFromInstance);
					triplesStringBuilder.append(JSONValue.toJSONString(tripleRecord));
					triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
					numberOfTriples++;
				}
			}
		}
		
		for (Triple triple : relationTypeTriple) {
			/*
			 * create data records for relation-to-property mappings
			 */
			Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_DEFINITION);
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_SUBJECT, triple.getSubject());
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE, triple.getPredicate());
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_OBJECT, triple.getObject());

			String predicateTypeStr = "";
			if (Triple.BuiltinPredicateSet.contains(triple.getPredicate())) {
				predicateTypeStr = PredicateType.Builtin.toString();
			} else {
				predicateTypeStr = PredicateType.Custom.toString();
			}

			tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT, "");
			tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT, "");
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE, predicateTypeStr);
			triplesStringBuilder.append(JSONValue.toJSONString(tripleRecord));
			triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
			numberOfTriples++;
			numberOfRelations++;
		}
		
		if (hasTriples) {
			System.out.println("Triples Extracted");
			String filePath = TRIPLE_REPOSITORY_DIRECTORY_FULL_PATH;
			String fileName = this.tripleStoreName;
			String fileFullName = filePath + fileName;
			return this.saveTripleString(fileFullName, triplesStringBuilder.toString());
		}
		return true;
	}
	
	private boolean saveTripleString(String fileFullName, String tripleString) {

		File file = new File(fileFullName);
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(tripleString);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param tripleStoreURI
	 *                 the URI of the triple store
	 * @return 
	 */
	public boolean loadRepository(String tripleRepositoryFileName){
		this.tripleStoreName = tripleRepositoryFileName;
		String fileName = tripleRepositoryFileName;
		String filePath = RepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		String fileFullName = filePath + fileName;
		return loadTriples(fileFullName);
	}
	
	/**
	 * Load triples from local storage (i.e., file system). This
	 * method will also populate various Sets and Maps with these triples. If
	 * succeed, return true. Otherwise return false
	 * 
	 * @param fileName - the name of the file store triples.
	 * @return true if load succeed, false otherwise
	 */
	private boolean loadTriples(String fileFullName){ 
		System.out.println("Loading Triples from " + fileFullName);
		File file = new File(fileFullName);
		BufferedReader reader = null;
		try{
			
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadTriple(line);
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
		
		this.tripleAssignment(theWholeTripleSet);
		this.constructHierarchy(classNodeDescendantMap, classNodePrecedentMap, triplesWithClassSubsumptionPredicate);
		this.constructHierarchy(propertyNodeDescendantMap, propertyNodePrecedentMap, triplesWithPropertySubsumptionPredicate);
		return true;
	}
	
	private void loadTriple(String line) {
		System.out.println("Loading: " + line);
		JSONObject record = (JSONObject) JSONValue.parse(line);
		String triple_record_type = (String) record.get(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE);
		if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA)) {
			this.getMetaData(record);
		} else {
			this.createTriple(record);
		}
	}
	
	/**
	 * 
	 * @param token
	 */
	private void getMetaData(JSONObject record) {
		String triple_store_URI = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_URI);
		String triple_store_Relations = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS);
		String triple_store_Triples = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES);
		this.tripleStoreURI = triple_store_URI;
		this.numberOfRelations = Integer.valueOf(triple_store_Relations);
		this.numberOfTriples = Integer.valueOf(triple_store_Triples);
	}

	private void createTriple(JSONObject record) {
		
		Triple triple = null;
		String triple_record_type = (String) record.get(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE);
		String subject = (String) record.get(MappingInfoSchemaParameter.TRIPLE_SUBJECT);
		String object = (String) record.get(MappingInfoSchemaParameter.TRIPLE_OBJECT);
		String predicate = (String) record.get(MappingInfoSchemaParameter.TRIPLE_PREDICATE);
		String n_object = (String) record.get(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT);
		String n_subject = (String) record.get(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT);
		String predicate_Type = (String) record.get(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE);
		
		if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_CONCEPT_OF_INSTANCE)) { 
			boolean isFromInstance = Boolean.valueOf((String) record.get(MappingInfoSchemaParameter.IS_FROM_INSTANCE));
			// TODO chech the nullpointer exception
			double score = Double.valueOf((String) record.get(MappingInfoSchemaParameter.TRIPLE_OBJECT_AS_CONCEPT_SCORE));
			Concept concept = new Concept(object, isFromInstance);
			concept.setScore(score);
			triple = new Triple(subject, concept);
		} else if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_VALUE)) {
			triple = new Triple(subject, n_subject, predicate, object, n_object);
		} else if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_DEFINITION)) {
			triple = new Triple(subject, BuiltinType.Property);
		}

		String predicateType = predicate_Type.trim();
		if (predicateType.equals(Triple.PredicateType.Builtin.toString())) {
			triple.setPredicateType(PredicateType.Builtin);
		} else if (predicateType.equals(Triple.PredicateType.Custom.toString())) {
			triple.setPredicateType(PredicateType.Custom);
		}
		theWholeTripleSet.add(triple);
	}

	public Collection<InstanceTripleSet> getInstanceTripleSets(){
		return subject2TripleSetMap.values();
	}
	
	public InstanceTripleSet getInstanceTripleSetByInstanceName(String instanceName){ 
		return this.subject2TripleSetMap.get(instanceName);
	}

	public Collection<Triple> getCustomRelationTriples(){
		List<Triple> tripleList = new ArrayList<Triple>(this.triplesWithCustomRelation);
		Collections.sort(tripleList, new TripleSorterBySubject());
		return tripleList;
	}
	
	@Override
	public Map<String, Collection<Triple>> getInstanceName2CustomRelationTripleMap() {
		List<Triple> tripleList = new ArrayList<Triple>(this.triplesWithCustomRelation);
		Collections.sort(tripleList, new TripleSorterBySubject());
		return this.groupTriplesByInstanceName(tripleList);
	}

	public Collection<Triple> getConceptRelationTriples(){
		List<Triple> tripleList = new ArrayList<Triple>(this.triplesOfInstance2ConceptsRelation);
		Collections.sort(tripleList, new TripleSorterBySubject());
		return tripleList;
	}
	
	@Override
	public Map<String, Collection<Triple>> getInstanceName2ConceptRelationTripleMap(){
		List<Triple> tripleList = new ArrayList<Triple>(this.triplesOfInstance2ConceptsRelation);
		Collections.sort(tripleList, new TripleSorterBySubject());
		return this.groupTriplesByInstanceName(tripleList);
	}
	
	@Override
	public Set<Triple> getRelationTypeTriple(){
		return relationTypeTriple;
	}
	
	private Map<String, Collection<Triple>> groupTriplesByInstanceName(List<Triple> tripleList){
		Map<String, Collection<Triple>> subject2TripleMap = new LinkedHashMap<String, Collection<Triple>>();
		for (Triple triple : tripleList) {
			String subjectLabel = triple.getSubject();
			Collection<Triple> triples;
			if (subject2TripleMap.containsKey(subjectLabel)) {
				triples = subject2TripleMap.get(subjectLabel);
			} else {
				triples = new ArrayList<Triple>();
				subject2TripleMap.put(subjectLabel, triples);
			}
			triples.add(triple);
		}
		return subject2TripleMap;
	}
	
	public Collection<String> getObjectTermsOfRelation(String relation){ 
		return this.customRelation2ObjectMap.get(relation);
	}
	
	public Collection<String> getSubjectTermsOfRelation(String relation){
		return this.customRelation2SubjectMap.get(relation);
	}
	
	public Collection<String> getCustomRelations(){
		Collection<String> relationCollection = new LinkedHashSet<String>();
		for(Triple triple : this.getCustomRelationTriples()){
			relationCollection.add(triple.getPredicate());
		}
		return relationCollection;
	}
	
	
	/**
	 * All the following printXXX methods are for test/debug purpose
	 * 
	 */
	
	public void printMetaData(){
		System.out.println("Triple Store URI: " + this.tripleStoreURI);
		System.out.println("Triple Store Name: " + this.tripleStoreName);
	}

	public void printTriplesWithCustomRelation() {
		this.printTriplesGroupBySuject(this.getInstanceName2CustomRelationTripleMap());
	}
	
	public void printTriplesOfInstance2ConceptRelation(){
		this.printTriplesGroupBySuject(this.getInstanceName2ConceptRelationTripleMap());
	}

	private void printTriplesGroupBySuject(Map<String, Collection<Triple>> triplesGroupBySubject) {
		for (String subject : triplesGroupBySubject.keySet()) {
			System.out.println("<" + subject + ">");
			for (Triple triple : triplesGroupBySubject.get(subject)) {
				System.out.println("              <" + triple.getPredicate() + "> <" + triple.getObject() + ">");
			}
		}
	}

	public void printClassHierarchy() {
		System.out.println("\nPrinting Class Hierarchy ...");
		Set<String> visitedNodeSet = new HashSet<String>();
		for (EntityNode node : classNodeDescendantMap.keySet()) {
			if (classNodePrecedentMap.get(node) == null) {
				String indent = "# ";
				System.out.println(indent + node.getLabel());
				visitedNodeSet.add(node.getLabel());
				Set<EntityNode> descedantSet = classNodeDescendantMap.get(node);
				indent = indent + "      ";
				for (EntityNode descedant : descedantSet) {
					String descedantStr = descedant.getLabel().toLowerCase();
					if (visitedNodeSet.contains(descedantStr)) {
						continue;
					}
					if (!node.getLabel().toLowerCase().equals(descedant.getLabel().toLowerCase())) {
						visitedNodeSet.add(descedantStr);
						System.out.println(indent + descedant.getLabel());
						this.printDescedants(descedant, indent + "      ", visitedNodeSet);
					}
				}
			}
		}
	}

	public void printPropertyHierarchy() {
		System.out.println("printing property hierarchy ...");
		Set<String> visitedNodeSet = new HashSet<String>();
		for (EntityNode node : propertyNodeDescendantMap.keySet()) {
			if (propertyNodePrecedentMap.get(node) == null) {
				String indent = "& ";
				System.out.println(indent + node.getLabel());
				visitedNodeSet.add(node.getLabel());
				Set<EntityNode> descedantSet = propertyNodeDescendantMap.get(node);
				indent = indent + "      ";
				for (EntityNode descedant : descedantSet) {
					String descedantStr = descedant.getLabel().toLowerCase();
					if (visitedNodeSet.contains(descedantStr)) {
						continue;
					}
					if (!node.getLabel().toLowerCase().equals(descedant.getLabel().toLowerCase())) {
						visitedNodeSet.add(descedantStr);
						System.out.println(indent + descedant.getLabel());
						this.printDescedants(descedant, indent + "      ", visitedNodeSet);
					}
				}
			}
		}
	}

	private void printDescedants(EntityNode node, String indent, Set<String> visitedNodeSet) {

		Set<EntityNode> descedantSet = classNodeDescendantMap.get(node);
		if (descedantSet == null)
			return;
		for (EntityNode descedant : descedantSet) {
			String descedantStr = descedant.getLabel().toLowerCase();
			if (visitedNodeSet.contains(descedantStr)) {
				continue;
			}
			if (!node.getLabel().toLowerCase().equals(descedant.getLabel().toLowerCase())) {
				visitedNodeSet.add(descedantStr);
				System.out.println(indent + descedant.getLabel());
				this.printDescedants(descedant, indent + "      ", visitedNodeSet);
			}
		}
	}

	@Override
	public void showTriples() {
		for (InstanceTripleSet tripleGroup: subject2TripleSetMap.values()) {
			tripleGroup.printTriples();
		}
	}

	@Override
	public String getRepositoryName() {
		return this.tripleStoreName;
	}

}
