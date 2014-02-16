package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;

public class ClassificationCorrection implements IClassificationCorrection {
	
	private String _instance_source;
	private String _instance;
	private OntoClassInfo _correction_source;
	private OntoClassInfo _correction_target;
	
	private Collection<IConcept2OntClassMapping> _hitted_mappings;
	private Collection<IConcept2OntClassMapping> _ambiguous_mappings;
	private Collection<String> _upMappedConcepts;
	private Map<String, String> _mappedClassHierarchies;
	
	private ClassificationCorrection(){
		_hitted_mappings = new ArrayList<IConcept2OntClassMapping>();
		_ambiguous_mappings = new ArrayList<IConcept2OntClassMapping>();
		_upMappedConcepts = new ArrayList<String>();
		_mappedClassHierarchies = new HashMap<String, String>();
	}
	public static IClassificationCorrection createInstance(){
		return new ClassificationCorrection();
	}
	@Override
	public void setCorrectionSource(OntoClassInfo source) {
		this._correction_source = source;
	}
	@Override
	public void setCorrectionTarget(OntoClassInfo target) {
		this._correction_target = target;
	}
	@Override
	public void addConcept2OntClassMapping(IConcept2OntClassMapping mapping){
		if (mapping.isHittedMapping()) {
			_hitted_mappings.add(mapping);
		} else if (mapping.isMappedConcept()) {
			_ambiguous_mappings.add(mapping);
		} else if (!mapping.isMappedConcept()) {
			_upMappedConcepts.add(mapping.getConceptName());
		}
	}
	@Override
	public void addMappedClassHierarchy(String hierarchyNumber, String closenessScore){
		_mappedClassHierarchies.put(hierarchyNumber, closenessScore);
	}

	@Override
	public OntoClassInfo getCorrectionSourceClass(){
		return this._correction_source;
	}
	@Override
	public OntoClassInfo getCorrectionTargetClass(){
		return this._correction_target;
	}
	@Override
	public Collection<String> getUnMappedConcepts(){
		return this._upMappedConcepts;
	}
	@Override
	public Map<String, String> getMappedClassHierarchies(){
		return this._mappedClassHierarchies;
	}
	@Override
	public Collection<IConcept2OntClassMapping> getHittedMappings(){
		return this._hitted_mappings;
	}
	@Override
	public Collection<IConcept2OntClassMapping> getAmbiguousMappings(){
		return this._ambiguous_mappings;
	}
	@Override
	public String getInstance() {
		return this._instance;
	}
	@Override
	public void setInstance(String instanceName) {
		this._instance = instanceName;
	}
	@Override
	public String getInstanceSource() {
		return _instance_source;
	}
	@Override
	public void setInstancePrevenance(String instanceSource) {
		_instance_source = instanceSource;		
	}

	@Override
	public String getCorrectionRepresentationCode() {
		return "<" + this._instance_source + ">@<" + this._instance + ">@<" + this._correction_source.getOntClassName() + ">@<"
				+ this._correction_target.getOntClassName() + ">";
	}

	@Override
	public int hashCode() {
		return this.getCorrectionRepresentationCode().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ClassificationCorrection correction = (ClassificationCorrection) obj;
		if (this.getCorrectionRepresentationCode().equals(correction.getCorrectionRepresentationCode())) {
			return true;
		} else {
			return false;
		}
	}
	
}
