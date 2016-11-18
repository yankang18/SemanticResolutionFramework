package umbc.ebiquity.kang.instanceconstructor.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple.PredicateType;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;

public class ModelSaveSupport {
	
	public RecordsHolder record(IInstanceDescriptionModel model) {
		System.out.println("Record Model ...");
		int numberOfRecords = 0;
		int numberOfRelations = 0;
//		StringBuilder triplesStringBuilder = new StringBuilder();
		RecordsHolder recordsHolder = new RecordsHolder(); 
		for (InstanceTripleSet instanceTripleSet : model.getInstanceTripleSets()) {
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
					addRecord(recordsHolder, tripleRecord);
					numberOfRecords++;
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
					addRecord(recordsHolder, tripleRecord);
					numberOfRecords++;
				}
			}
		}

		for (Triple triple : model.getRelationTypeTriple()) {
			// create data records for relation-to-property mappings
			Map<String, String> record = new LinkedHashMap<String, String>();
			record.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE,
					MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_RELATION_DEFINITION);
			record.put(MappingInfoSchemaParameter.TRIPLE_SUBJECT, triple.getSubject());
			record.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE, triple.getPredicate());
			record.put(MappingInfoSchemaParameter.TRIPLE_OBJECT, triple.getObject());

			String predicateTypeStr = "";
			if (Triple.BuiltinPredicateSet.contains(triple.getPredicate())) {
				predicateTypeStr = PredicateType.Builtin.toString();
			} else {
				predicateTypeStr = PredicateType.Custom.toString();
			}

			record.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_OBJECT, "");
			record.put(MappingInfoSchemaParameter.NORMALIZED_TRIPLE_SUBJECT, "");
			record.put(MappingInfoSchemaParameter.TRIPLE_PREDICATE_TYPE, predicateTypeStr);
			addRecord(recordsHolder, record);
			numberOfRecords++;
			numberOfRelations++;
		}
		
		// create data record for meta-data
		Map<String, String> metaDataRecord = new LinkedHashMap<String, String>();
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE, MappingInfoSchemaParameter.TRIPLE_RECORD_TYPE_META_DATA);
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_URI, model.getSourceURL().toString());
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_TRIPLES, String.valueOf(numberOfRecords));
		metaDataRecord.put(MappingInfoSchemaParameter.TRIPLE_STORE_NUMBER_OF_RELATIONS, String.valueOf(numberOfRelations));
		addRecord(recordsHolder, metaDataRecord);

		recordsHolder.setNumOfRelations(numberOfRelations);
		return recordsHolder;
	}
	
	private void addRecord(RecordsHolder recordsHolder, Map<String, String> metaDataRecord) {
		recordsHolder.addRecord(metaDataRecord);
	}

}
