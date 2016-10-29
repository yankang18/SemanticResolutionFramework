package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class UpdatedInstanceRecord implements IInstanceRecord{

	private String _prevanceOfInstance;
	private String _originalInstanceName;
	private String _originalClassName;
	private String _updatedInstanceName;
	private String _updatedClassName;
//	private OntoClassInfo _updatedOntClass;
	private boolean _isUpdatedInstance;
	private boolean _isDeletedInstance;
	private Collection<IConcept2OntClassMapping> _concept2OntClassMappings;
	private Map<String, IConcept2OntClassMapping> _conceptName2MappingPairMap;
	
	public UpdatedInstanceRecord(){
		this._concept2OntClassMappings = new ArrayList<IConcept2OntClassMapping>();
		this._conceptName2MappingPairMap = new HashMap<String, IConcept2OntClassMapping>();
	}
	
	@Override
	public void setOriginalInstanceName(String originalInstaneName) {
		_originalInstanceName = originalInstaneName.trim();
	}

	@Override
	public void setOriginalClassName(String originalClassName) {
		_originalClassName = originalClassName.trim();
	}

	@Override
	public void setUpdatedInstanceName(String updatedInstanceName) {
		_updatedInstanceName = updatedInstanceName.trim();
	}

	@Override
	public void setUpdatedClassName(String updatedClassName) {
		_updatedClassName = updatedClassName.trim();
	}

	@Override
	public void isUpdatedInstance(boolean isUpdatedInstance) {
		this._isUpdatedInstance = isUpdatedInstance;
	}
	
	@Override
	public void setDeletedInstance(boolean isDeletedInstance) {
		this._isDeletedInstance = isDeletedInstance;
	}

	@Override
	public void addConcept2OntClassMappingPair(Concept concept, MappingRelationType relation, OntoClassInfo ontClass, boolean isDirectMapping, boolean isManualMapping, double similarity) {
		Concept2OntClassMapping pair = new Concept2OntClassMapping(concept, relation, ontClass, similarity);
		pair.setDirectMapping(isDirectMapping);
		pair.setManualMapping(isManualMapping);
		_concept2OntClassMappings.add(pair);
		_conceptName2MappingPairMap.put(concept.getConceptName(), pair);
	}
	
	@Override
	public String getOriginalInstanceName() {
		return this._originalInstanceName;
	}

	@Override
	public String getOriginalClassName() {
		return this._originalClassName;
	}

	@Override
	public String getUpdatedInstance() {
		return this._updatedInstanceName;
	}

	@Override
	public String getUpdatedClassName() {
		return this._updatedClassName;
	}

	@Override
	public boolean isUpdatedInstance() {
		return this._isUpdatedInstance;
	}
	
	@Override
	public boolean isDeletedInstance() {
		return this._isDeletedInstance;
	}

	@Override
	public Collection<IConcept2OntClassMapping> getConcept2OntClassMappingPairs() {
		return _concept2OntClassMappings;
	}

	@Override
	public IConcept2OntClassMapping getConcept2OntClassMappingPairByConceptName(String conceptName) {
		return _conceptName2MappingPairMap.get(conceptName);
	}
	
	@Override
	public boolean isOntClassChanged(){
		return !this._originalClassName.equals(this._updatedClassName);
	}
	
	@Override
	public boolean isLabelChanged() {
		return !_originalInstanceName.equals(_updatedInstanceName);		
	}

	@Override
	public String getPrevenantInstance() {
		return _prevanceOfInstance;
	}

	@Override
	public void setPrevenanceOfInstance(String prevenanceOfInstance) {
		_prevanceOfInstance = prevenanceOfInstance;
	}

//	@Override
//	public OntoClassInfo getUpdatedOntClass() {
//		return this._updatedOntClass;
//	}
//
//	@Override
//	public void setUpdatedOntClass(OntoClassInfo updatedOntClass) {
//		this._updatedOntClass = updatedOntClass;
//	}
}
