package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;


public class Concept2OntClassMapping implements Comparable<Concept2OntClassMapping>, IConcept2OntClassMapping {

	private Concept _concept = null;
	private MappingRelationType _relation = null;
	private OntoClassInfo _ontClassInfo = null;
	private double _score = 0.0; 
	private String _provenance;
	private String _instance;
	private boolean _isMappedConcept = false;
	private boolean _isDirectMapping = true;
	private boolean _isHittedMapping = false;
	private boolean _isManualMapping = false;
	
	public Concept2OntClassMapping(Concept concept) {
		this(concept, null, null, 0.0);
	}
	
	public Concept2OntClassMapping(Concept concept, OntoClassInfo ontClassInfo, double mappingScore) {
		this(concept, MappingRelationType.relatedTo, ontClassInfo, mappingScore);
	}
	
	public Concept2OntClassMapping(Concept concept, MappingRelationType relation, OntoClassInfo ontClassInfo, double similarity) {
		this.setMappedOntoClass(ontClassInfo, relation);
		this._score = similarity;
		this._concept = concept;
	}

	@Override
	public int compareTo(Concept2OntClassMapping wrapper) {
		if (this._score > wrapper._score) {
			return -1;
		} else if (this._score == wrapper._score) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public MappingRelationType getRelation(){
		return this._relation;
	}

	@Override
	public OntoClassInfo getMappedOntoClass() {
		return _ontClassInfo;
	}

	@Override
	public String getMappedOntoClassName() {
		return _ontClassInfo.getOntClassName().trim();
	}

	@Override
	public double getMappingScore() {
		return this._score;
	}

	@Override
	public String getConceptName() {
		return this._concept.getConceptName().trim();
	}

	@Override
	public Concept getConcept() {
		return this._concept;
	}

	@Override
	public boolean isMappedConcept() {
		return this._isMappedConcept;
	}

	@Override
	public void setMappedOntoClass(OntoClassInfo mappedOntoClass, MappingRelationType relation) {
		this._ontClassInfo = mappedOntoClass;
		if(relation == null){
			this._relation = MappingRelationType.relatedTo;
		} else {
			this._relation = relation;
		}
		if (mappedOntoClass == null) {
			this._isMappedConcept = false;
			this._isDirectMapping = false;
			this._isHittedMapping = false;
			this._isManualMapping = false;
		} else {
			this._isMappedConcept = true;
		}		
	}
	
	@Override
	public void setMappedOntoClass(OntoClassInfo mappedOntoClass, MappingRelationType relation, double mappingScore) {
		this._score = mappingScore;
		this.setMappedOntoClass(mappedOntoClass, relation);
	}

	public void setDirectMapping(boolean isDirectMapping) {
		this._isDirectMapping = isDirectMapping;
	}
	
	public void setManualMapping(boolean isManualMapping){
		this._isManualMapping = isManualMapping;
	}
	
	@Override
	public void setHostInstance(String instance){
		this._instance = instance;
	}

	@Override
	public boolean isDirectMapping() {
		return _isDirectMapping;
	}
	
	@Override
	public boolean isManualMapping() {
		return _isManualMapping;
	}
	
	public void setHittedMapping(boolean isHittedMapping) {
		this._isHittedMapping = isHittedMapping;
	}

	@Override
	public boolean isHittedMapping() {
		return this._isHittedMapping;
	}

	@Override
	public String toString() {
		String mappedClassName;
		if (_ontClassInfo == null) {
			mappedClassName = "@NULL@";
		} else {
			mappedClassName = this._ontClassInfo.getOntClassName();
		}
		return "<" + TextProcessingUtils.getProcessedLabelWithStemming(this._concept.getConceptName(), "") + ">@<" + mappedClassName + ">";
	}
	
	@Override
	public String getMappingCode(){
		return this.toString();
	}
	
	@Override
	public int hashCode(){
		return this.getMappingCode().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		IConcept2OntClassMapping mapping = (IConcept2OntClassMapping) obj;
		if (this.getMappingCode().equals(mapping.getMappingCode())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getProvenantHostInstance() {
		return _provenance;
	}
	
	@Override
	public String getHostInstance(){
		return this._instance;
	}

	@Override
	public void setProvenantHostInstance(String provenance) {
		this._provenance = provenance;
	}

}
