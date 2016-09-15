package umbc.ebiquity.kang.instanceconstructor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;

public class InstanceTripleSet implements IInstanceTripleSet {

	private String subjectLabel;
	private String processedSubjectLabel;
	private Set<Triple> nonTaxonomicTriples;
	private Set<Triple> taxonomicTriples;
	
	// instance to its concept set map
	private Set<Triple> instance2ConceptSetTriples;
	private Map<String, Set<String>> customRelation2ValueMap;
	private Map<String, Set<String>> taxonomicRelationValueMap;
	private Map<String, Set<Concept>> instance2ConceptualSetMap;
	private Set<Concept> conceptSet;
	
	public InstanceTripleSet(String subjectLabel) {
		this.subjectLabel = subjectLabel.trim();
		this.nonTaxonomicTriples = new LinkedHashSet<Triple>();
		this.taxonomicTriples = new LinkedHashSet<Triple>();
		this.instance2ConceptSetTriples = new LinkedHashSet<Triple>();
		this.customRelation2ValueMap = new HashMap<String, Set<String>>();
		this.taxonomicRelationValueMap = new HashMap<String, Set<String>>();
		this.instance2ConceptualSetMap = new HashMap<String, Set<Concept>>();
		this.conceptSet = new HashSet<Concept>();
	}

	public void addInstance2ConceptTriple(Triple triple) {
		
		Concept concept = triple.getConcept();
		conceptSet.add(concept);
		String predicate = triple.getPredicate();
//		String object = triple.getObject();
		
		Set<Concept> conceptualSet;
		if(instance2ConceptualSetMap.containsKey(predicate)){
			conceptualSet = instance2ConceptualSetMap.get(predicate);
		} else {
			conceptualSet = new HashSet<Concept>();
			instance2ConceptualSetMap.put(predicate, conceptualSet);
		}
		conceptualSet.add(concept);
		
		instance2ConceptSetTriples.add(triple);
	}
	
	public boolean containsConcept() {
		return instance2ConceptualSetMap.values().size() > 0 ? true : false;
	}

	public void addNonTaxonomicTriple(Triple triple) {
		String predicate = triple.getPredicate();
		String object = triple.getObject();
		
		Set<String> relationValueMap;
		if(customRelation2ValueMap.containsKey(predicate)){
			relationValueMap = customRelation2ValueMap.get(predicate);
		} else {
			relationValueMap = new HashSet<String>();
			customRelation2ValueMap.put(predicate, relationValueMap);
		}
		relationValueMap.add(object);
//		nonTaxonomicRelationValueMap.put(predicate, relationValueMap);
		nonTaxonomicTriples.add(triple);
	}

	public void addTaxonomicTriple(Triple triple) {
		String predicate = triple.getPredicate();
		String object = triple.getObject();
		
		Set<String> relationValueMap;
		if(taxonomicRelationValueMap.containsKey(predicate)){
			relationValueMap = taxonomicRelationValueMap.get(predicate);
		} else {
			relationValueMap = new HashSet<String>();
			taxonomicRelationValueMap.put(predicate, relationValueMap);
		}
		relationValueMap.add(object);
//		taxonomicRelationValueMap.put(predicate, relationValueMap);
		taxonomicTriples.add(triple);
	}

	public void printTriples() {
		System.out.println("* <" + subjectLabel + ">");
		for (String predicate : customRelation2ValueMap.keySet()) {
			System.out.println("      <" + predicate + ">");
			Collection<String> objects = customRelation2ValueMap.get(predicate);
			for (String object : objects) {
				System.out.println("                  <" + object + ">");
			}
		}
		for (String predicate : instance2ConceptualSetMap.keySet()) {
			System.out.println("   <" + predicate + ">");
			Collection<Concept> concepts = instance2ConceptualSetMap.get(predicate);
			for (Concept concept : concepts) {
				System.out.println("              <" + concept.getConceptName() + ">  <" + concept.getScore() + ">");
			}
		}
	}
	
	public Collection<Concept> getConceptSet(){
		return this.conceptSet;
	}
	
	public Collection<String> getCustomRelation(){
		return customRelation2ValueMap.keySet();
	}
	
	public Collection<String> getCustomRelationValue(String relation){
		return customRelation2ValueMap.get(relation);
	}
	
	public Collection<String> getTaxonomicRelationValue() {
		Collection<String> emptyCollection = new ArrayList<String>();
		if (taxonomicRelationValueMap.get("SubConcept") == null) {
			return emptyCollection;
		}
		return taxonomicRelationValueMap.get("SubConcept");
	}

	public Map<String, Set<String>> getRelation2ValueMap(){
		return this.customRelation2ValueMap;
	}
	
	public Map<String, Set<String>> getTaxonomicRelation2ValueMap(){
		return this.taxonomicRelationValueMap;
	}
	
	public Map<String, Set<Concept>> getInstance2ConceptualSetMap(){
		return this.instance2ConceptualSetMap;
	}
	
	public String getSubjectLabel() {
		return subjectLabel;
	}

	public void setProcessedSubjectLabel(String processedSubjectLabel) {
		this.processedSubjectLabel = processedSubjectLabel;
	}

	public String getProcessedSubjectLabel() {
		return processedSubjectLabel;
	}
	
	@Override
	public int hashCode() {
		return this.subjectLabel.trim().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		InstanceTripleSet cluster = (InstanceTripleSet) obj;
		return this.getSubjectLabel().equals(cluster.getSubjectLabel());
	}
}
