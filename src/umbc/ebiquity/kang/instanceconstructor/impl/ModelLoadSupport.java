package umbc.ebiquity.kang.instanceconstructor.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple.BuiltinType;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple.PredicateType;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;

public class ModelLoadSupport {
	
	private URL tripleStoreURI;
	private Set<Triple> theWholeTripleSet;
	
	public IInstanceDescriptionModel load(List<String> lines) throws MalformedURLException {
		theWholeTripleSet = new LinkedHashSet<Triple>();
		for (String line : lines) {
			System.out.println("Loading: " + line);
			JSONObject record = (JSONObject) JSONValue.parse(line);
			String triple_record_type = (String) record.get(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE);
			if (triple_record_type.equals(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA)) {
				this.getMetaData(record);
			} else {
				this.createTriple(record);
			}
		}
		return new InstanceDescriptionModel(theWholeTripleSet, tripleStoreURI);
	}

	private void getMetaData(JSONObject record) throws MalformedURLException {
		String triple_store_URI = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_URI);
//		String triple_store_Relations = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS);
//		String triple_store_Triples = (String) record.get(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES);
		this.tripleStoreURI = new URL(triple_store_URI);
//		this.numberOfRelations = Integer.valueOf(triple_store_Relations);
//		this.numberOfTriples = Integer.valueOf(triple_store_Triples);
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
