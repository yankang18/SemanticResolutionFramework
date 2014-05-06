package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.HashSet;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public class CorrectionDirection {

	private Set<String> _sourceClassSet;
	private String _targetClassName;
	
	
	public CorrectionDirection(OntoClassInfo sourceClass, OntoClassInfo targetClass) {
		this._sourceClassSet = new HashSet<String>();
		this._sourceClassSet.add(sourceClass.getOntClassName());
		this._targetClassName = targetClass.getOntClassName();
	}
	
	
	public CorrectionDirection(String sourceClass, String targetClass) {
		this._sourceClassSet = new HashSet<String>();
		this._sourceClassSet.add(sourceClass);
		this._targetClassName = targetClass;
	}
	
	public CorrectionDirection(Set<String> sourceClassSet, String targetClass) {
		this._sourceClassSet = new HashSet<String>();
		this._sourceClassSet.addAll(sourceClassSet);
		this._targetClassName = targetClass;
	}
	
	
	
	public void addSouceClass(String className){
		_sourceClassSet.add(className);
	}
	
	public Set<String> getSourceClassSet(){
		return this._sourceClassSet;
	}
	
	public String getTargetClassName(){
		return this._targetClassName;
	}
}
