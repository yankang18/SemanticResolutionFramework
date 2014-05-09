package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsAccessor;

public class EvaluationCorpusRecordsAccessor implements IEvaluationCorpusRecordsAccessor {
	
	public Map<String, String> instance2ClassMap;
	public Map<String, String> relation2PropertyMap;
	Map<String, String> concept2ClassMap;
	public Map<String, Map<String, List<String>>> instance2ConceptClassMappingPair;
	Map<String, Map<String, Set<String>>> rpMappingDetail;
	
	public EvaluationCorpusRecordsAccessor(){
		instance2ClassMap = new HashMap<String, String>();
		relation2PropertyMap = new HashMap<String, String>();
		concept2ClassMap = new HashMap<String, String>();
		instance2ConceptClassMappingPair = new LinkedHashMap<String, Map<String, List<String>>>();
		rpMappingDetail = new HashMap<String, Map<String, Set<String>>>();
	}

	
	private boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	private String trimBracket(String original){
		return original.substring(1, original.length() - 1);
	}

	@Override
	public String getClassLabelforInstance(String instanceLabel){
		return instance2ClassMap.get(instanceLabel);
	}
	
	@Override
	public boolean containsInstance(String instanceLabel){
		return instance2ClassMap.containsKey(instanceLabel);
	}
	
	@Override
	public Set<String> getInstanceSet(){
		return instance2ClassMap.keySet();
	}
	
	@Override
	public String getPropertyLabelforRelation(String relationLabel){
		return relation2PropertyMap.get(relationLabel);
	}

	@Override
	public String getOntClassForConcept(String conceptLabel){
		return concept2ClassMap.get(conceptLabel);
	}
	
	public Map<String, List<String>> getConcept2ClassMap(String instanceLabel){
		if(instance2ConceptClassMappingPair.containsKey(instanceLabel)){
			return 	this.instance2ConceptClassMappingPair.get(instanceLabel);
		}else{
			return new HashMap<String, List<String>>();
		}
	}

	@Override
	public List<String> getClassSet(String instanceLabel, String concept) {
		if (instance2ConceptClassMappingPair.containsKey(instanceLabel)) {
			Map<String, List<String>> concept2ClassMap = this.instance2ConceptClassMappingPair.get(instanceLabel);
			if (concept2ClassMap.containsKey(concept)) {
				return concept2ClassMap.get(concept);
			} else {
				return new ArrayList<String>();
			}

		} else {
			return new ArrayList<String>();
		}
	}

	@Override
	public void addConcept2ClassMap(String conceptLabel, String classLabel) {
		concept2ClassMap.put(conceptLabel, classLabel);		
	}
	
	@Override
	public void addClassifiedInstance(String instanceLabel, String classLabel) {
		instance2ClassMap.put(instanceLabel, classLabel);
	}
	
	@Override
	public void addRelation2PropertyMapping(String relationLabel, String propertyLabel) {
		relation2PropertyMap.put(relationLabel, propertyLabel);
	}
	
	@Override
	public void addConcept2ClassMappingForInstance(String instanceLabel, String conceptLabel, String classLabel) {
		if(instance2ConceptClassMappingPair.containsKey(instanceLabel)){
			Map<String, List<String>> concept2ClassSetMap = instance2ConceptClassMappingPair.get(instanceLabel);
			if(concept2ClassSetMap.containsKey(conceptLabel)){
				concept2ClassSetMap.get(conceptLabel).add(classLabel);
			} else {
				List<String> classSet = new ArrayList<String>();
				classSet.add(classLabel);
				concept2ClassSetMap.put(conceptLabel, classSet);
			}
		} else {
			Map<String, List<String>> concept2ClassMap = new HashMap<String, List<String>>();
			List<String> classSet = new ArrayList<String>();
			classSet.add(classLabel);
			concept2ClassMap.put(conceptLabel, classSet);
			instance2ConceptClassMappingPair.put(instanceLabel, concept2ClassMap);
		}
	}
	
	
	
	@Override
	public void showRecords(){
		System.out.println("-------------------- instance ----------------------");
		for (String instance : instance2ClassMap.keySet()) {
			String className = instance2ClassMap.get(instance);
			System.out.println(instance + " --> " + className);
			Map<String, List<String>> concept2ClassMap = instance2ConceptClassMappingPair.get(instance);
			if (concept2ClassMap != null) {
				for (String concept : concept2ClassMap.keySet()) {
					List<String> classSet = concept2ClassMap.get(concept);
					for (String c : classSet) {
						System.out.println("          " + concept + " : " + c);
					}
				}
			}
		}
		System.out.println("-------------------- concept 2 class ----------------------");
		for(String concept : concept2ClassMap.keySet()){
			String ontClass = concept2ClassMap.get(concept);
			System.out.println(concept + " : " + ontClass);
		}
		System.out.println("-------------------- relation ----------------------");
		for(String relation : relation2PropertyMap.keySet()){
			String property = relation2PropertyMap.get(relation);
			System.out.println(relation + " --> " + property);
		}
		
	}
	
//	public void addClassifiedInstance(
//			String instanceLabel, 
//			String classLabel, 
//			Set<IConcept2OntClassMapping> concept2ClassMappingSet
//			) {
//		instance2ClassMap.put(instanceLabel, classLabel);
//		for (IConcept2OntClassMapping concept2OntClassMapping : concept2ClassMappingSet) {
//			Map<String, String> concept2classMap = new HashMap<String, String>();
//			concept2classMap.put(concept2OntClassMapping.getConceptName(), concept2OntClassMapping.getMappedOntoClassName());
//			instance2ConceptClassMappingPair.put(instanceLabel, concept2classMap);
//		}
//	}

}
