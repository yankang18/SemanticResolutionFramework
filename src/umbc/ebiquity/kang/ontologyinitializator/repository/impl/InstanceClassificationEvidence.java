package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.CorrectionDirection;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;

public class InstanceClassificationEvidence implements IInstanceClassificationEvidence {

	private Set<IConcept2OntClassMapping> _concept2ClassMappingSet;
	private OntoClassInfo _targetClass;
	private OntoClassInfo _sourceClass;
	private String _mappingSetCode;

	private InstanceClassificationEvidence(Collection<IConcept2OntClassMapping> mappingSet, OntoClassInfo sourceClass, OntoClassInfo targetClass) {
		_concept2ClassMappingSet = new HashSet<IConcept2OntClassMapping>(mappingSet);
		_sourceClass = sourceClass;
		_targetClass = targetClass;
		_mappingSetCode = createMemberInferenceFactCode(_concept2ClassMappingSet, targetClass.getOntClassName());
	}
	
	private InstanceClassificationEvidence(Collection<IConcept2OntClassMapping> mappingSet, String targetClassName) {
		_concept2ClassMappingSet = new HashSet<IConcept2OntClassMapping>(mappingSet);
		_mappingSetCode = createMemberInferenceFactCode(_concept2ClassMappingSet, targetClassName);
	}

	public static IInstanceClassificationEvidence createInstance (
														     Collection<IConcept2OntClassMapping> mappingSet,
															 OntoClassInfo sourceClass,
			                                                 OntoClassInfo targetClass
			                                                 ) 
	{
		return new InstanceClassificationEvidence(mappingSet, sourceClass, targetClass);
	}

	public static IInstanceClassificationEvidence createInstance(Collection<IConcept2OntClassMapping> mappingSet, String targetClassName) {
		return new InstanceClassificationEvidence(mappingSet, targetClassName);
	}

	public static String createMemberInferenceFactCode(Set<IConcept2OntClassMapping> positiveC2CMappingSet, String targetClassName) {
		List<String> labels = new ArrayList<String>();
		for (IConcept2OntClassMapping C2C : positiveC2CMappingSet) {
			String label = C2C.getMappingCode();
			labels.add(label);
		}
		Collections.sort(labels);
		return labels.toString() + "->[" + targetClassName + "]";
	}
	
	@Override
	public Set<IConcept2OntClassMapping> getConcept2OntClassMappingMembers(){
		return this._concept2ClassMappingSet;
	}

	@Override
	public OntoClassInfo getCorrectionSourceClass(){
		return _sourceClass;
	}
	
	@Override
	public OntoClassInfo getCorrectionTargetClass() {
		return _targetClass;
	}

//	@Override
//	public CorrectionDirection getCorrectionDirection() {
//		return new CorrectionDirection(_sourceClass, _targetClass);
//	}
	
	@Override
	public String getEvidenceCode(){
		return this._mappingSetCode;
	}
	
	@Override
	public int hashCode(){
		return this._mappingSetCode.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		IInstanceClassificationEvidence mappingSet = (IInstanceClassificationEvidence) obj;
		
		if (this.getEvidenceCode().equals(mappingSet.getEvidenceCode())) {
			return true;
		} else {
			return false;
		}
	}
}
