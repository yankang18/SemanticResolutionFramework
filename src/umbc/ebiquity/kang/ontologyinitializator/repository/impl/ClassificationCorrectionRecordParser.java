package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordParser;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRecordRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;

public class ClassificationCorrectionRecordParser implements IClassificationCorrectionRecordParser {

	@Override
	public void parseRecord(String record, IClassificationCorrectionRecordRepository repository) {
		JSONObject map = (JSONObject) JSONValue.parse(record);
		String record_type = (String) map.get(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE);
		if(record_type.equals(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_CORRECTION)){
			IClassificationCorrection correction = loadCorrectionEvidence(record);
			repository.addClassificationCorrection(correction);
		} else if (record_type.equals(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_EXPLICIT_EVIDENCE)){
			IInstanceClassificationEvidence evidence = this.loadInstanceInterpretationEvidence(record);
			repository.addExplicitInstanceClassificationEvidence(evidence);
		} else if (record_type.equals(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_HIDDEN_EVIDENCE)){
			IInstanceClassificationEvidence evidence = this.loadInstanceInterpretationEvidence(record);
			repository.addHiddenInstanceClassificationEvidence(evidence);
		} else if (record_type.equals(MappingInfoSchemaParameter.CORRECTION_RECORD_TYPE_METADATA)){
			JSONObject map2 = (JSONObject) JSONValue.parse(record);
			int numberOfInstance = Integer.valueOf(String.valueOf(map2.get(MappingInfoSchemaParameter.NUMBER_OF_INSTANCE)));
			repository.addNumberOfInstance(numberOfInstance);
		}
	}
	
	
	private IInstanceClassificationEvidence loadInstanceInterpretationEvidence(String line){
		JSONObject map = (JSONObject) JSONValue.parse(line);
		String prevenanceHostInstance = (String) map.get(MappingInfoSchemaParameter.PREVENANCE_OF_INSTANCE);
		String hostInstance = (String) map.get(MappingInfoSchemaParameter.INSTANCE_NAME);
		
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
			mapping.setProvenantHostInstance(prevenanceHostInstance);
			mapping.setHostInstance(hostInstance);
			mappingSet.add(mapping);
		}
		return InstanceClassificationEvidence.createInstance(mappingSet, sourceOntClass, targetOntClass);
	}

	private IClassificationCorrection loadCorrectionEvidence(String line) {

		JSONObject map = (JSONObject) JSONValue.parse(line);
		String instance_name = (String) map.get(MappingInfoSchemaParameter.INSTANCE_NAME);
		String instance_source = (String) map.get(MappingInfoSchemaParameter.INSTANCE_SOURCE);

		JSONObject correction_direction = (JSONObject) map.get(MappingInfoSchemaParameter.CORRECTION_DIRECTION);
		@SuppressWarnings("unchecked")
		Map<String, String> sourceClassMap = (Map<String, String>) correction_direction.get(MappingInfoSchemaParameter.CORRECTION_SOURCE);
		@SuppressWarnings("unchecked")
		Map<String, String> targetClassMap = (Map<String, String>) correction_direction.get(MappingInfoSchemaParameter.CORRECTION_TARGET);
		String sClassName = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_NAME);
		String sClassNS = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String sClassURI = (String) sourceClassMap.get(MappingInfoSchemaParameter.CLASS_URI);
		OntoClassInfo sourceOntClass = new OntoClassInfo(sClassURI, sClassNS, sClassName);
		String tClassName = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_NAME);
		String tClassNS = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
		String tClassURI = (String) targetClassMap.get(MappingInfoSchemaParameter.CLASS_URI);
		OntoClassInfo targetOntClass = new OntoClassInfo(tClassURI, tClassNS, tClassName);
		
		IClassificationCorrection correction = ClassificationCorrection.createInstance();
		correction.setInstancePrevenance(instance_source);
		correction.setInstance(instance_name);
		correction.setCorrectionSource(sourceOntClass);
		correction.setCorrectionTarget(targetOntClass);

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
		return correction;
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
}
