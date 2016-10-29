package umbc.ebiquity.kang.instanceconstructor.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple.BuiltinType;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple.PredicateType;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;

public class FileModelRepository implements IInstanceDescriptionModelRepository {

	private String tripleFullPath;
	
	private URL tripleStoreURI;
//	private String tripleStoreName;
	private int numberOfRelations;
	private int numberOfTriples;
	
	private Set<Triple> theWholeTripleSet;
	private InstanceDescriptionModel model;
	
	private void init() {
		tripleFullPath = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		System.out.println("Full Path: " + tripleFullPath);
		theWholeTripleSet = new LinkedHashSet<Triple>();
		numberOfTriples = 0;
		numberOfRelations = 0;
	}

	@Override
	public boolean save(IInstanceDescriptionModel model, String repositoryName) {
		System.out.println("Saving Extracted Triples ...");
		init();
		boolean hasTriples = false;
		StringBuilder triplesStringBuilder = new StringBuilder();
		// create data record for meta-data
		Map<String, String> metaDataRecord = new LinkedHashMap<String, String>();
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA);
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_URI, model.getSourceURL().toString());
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES, String.valueOf(numberOfTriples));
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS, String.valueOf(numberOfRelations));
		addRecord(triplesStringBuilder, metaDataRecord);

		for (InstanceTripleSet instanceTripleSet : model.getInstanceTripleSets()) {
			hasTriples = true;
			String subjectLabel = instanceTripleSet.getSubjectLabel();
			Map<String, Set<String>> relation2ValueMap = instanceTripleSet.getRelation2ValueMap();
			Map<String, Set<Concept>> instance2ConceptSetMap = instanceTripleSet.getInstance2ConceptualSetMap();
			for (String relationLabel : relation2ValueMap.keySet()) {

				// create data records for relation-values mappings
				Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
				for (String valueLabel : relation2ValueMap.get(relationLabel)) {
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE,
							MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_VALUE);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_SUBJECT, subjectLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE, relationLabel);
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_OBJECT, valueLabel);

					String predicateTypeStr;
					if (Triple.BuiltinPredicateSet.contains(relationLabel)) {
						predicateTypeStr = PredicateType.Builtin.toString();
					} else {
						predicateTypeStr = PredicateType.Custom.toString();
					}

					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT, "");
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE, predicateTypeStr);
					addRecord(triplesStringBuilder, tripleRecord);
					numberOfTriples++;
				}
			}

			for (String relationLabel : instance2ConceptSetMap.keySet()) {
				// create data records for class-concept mappings
				Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
				for (Concept concept : instance2ConceptSetMap.get(relationLabel)) {
					tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE,
							MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_CONCEPT_OF_INSTANCE);
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
					addRecord(triplesStringBuilder, tripleRecord);
					numberOfTriples++;
				}
			}
		}

		for (Triple triple : model.getRelationTypeTriple()) {
			hasTriples = true;
			// create data records for relation-to-property mappings
			Map<String, String> tripleRecord = new LinkedHashMap<String, String>();
			tripleRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE,
					MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_DEFINITION);
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
			addRecord(triplesStringBuilder, tripleRecord);
			numberOfTriples++;
			numberOfRelations++;
		}

		if (hasTriples) {
			System.out.println("Triples Extracted");
			String filePath = tripleFullPath;
			String fileName = repositoryName;
			String fileFullName = filePath + fileName;
			return this.saveTripleString(fileFullName, triplesStringBuilder.toString());
		}
		return true;
	}

	private void addRecord(StringBuilder triplesStringBuilder, Map<String, String> metaDataRecord) {
		triplesStringBuilder.append(JSONValue.toJSONString(metaDataRecord));
		triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
	}

	@Override
	public IInstanceDescriptionModel load(String repositoryName) {
		init();
		String fileFullName = tripleFullPath + repositoryName;
		try {
			loadTriples(fileFullName);
		} catch (IOException e) {
			// TODO: throw unchecked exception
		}
		return model;
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
	 * Load triples from the file system. This method will also populate various
	 * Sets and Maps with these triples. If succeed, return true. Otherwise
	 * return false
	 * 
	 * @param fileFullName the name of the file store triples.
	 * @throws IOException 
	 */
	private void loadTriples(String fileFullName) throws IOException{  
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
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
		
		model = new InstanceDescriptionModel(theWholeTripleSet, tripleStoreURI);
	}
	
	private void loadTriple(String line) throws MalformedURLException { 
		System.out.println("Loading: " + line);
		JSONObject record = (JSONObject) JSONValue.parse(line);
		String triple_record_type = (String) record.get(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE);
		if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA)) {
			this.getMetaData(record);
		} else {
			this.createTriple(record);
		}
	}
	
	private void getMetaData(JSONObject record) throws MalformedURLException { 
		String triple_store_URI = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_URI);
		String triple_store_Relations = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS);
		String triple_store_Triples = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES);
		this.tripleStoreURI = new URL(triple_store_URI);
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

}
