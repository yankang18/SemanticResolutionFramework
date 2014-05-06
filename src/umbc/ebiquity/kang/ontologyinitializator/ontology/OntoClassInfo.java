package umbc.ebiquity.kang.ontologyinitializator.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;

/**
 * 
 * @author kangyan2003
 *
 */
public class OntoClassInfo {
	
	/**
	 * basic information including class label, URI, and namespace
	 */
	private String label;
	private String URI;
	private String nameSpace;
	private int hierarchyNumber;
	private String localPathCode;
	
	/**
	 * record all the onto-properties this onto-class has
	 */
	private Collection<OntPropertyInfo> properties;
	
	private double similarityToConcept;
	private OntoClassInfo bestMatchedSuperOntoClassInfo;
	
	/**
	 * record all the super onto-classes of this onto-class
	 */
	private Collection<OntoClassInfo> superOntClassInHierarchy;
//	private int repeats;

//	public OntoClassInfo(String classLabel) {
//		this.label = classLabel;
//		this.properties = new ArrayList<String>();
//		this.superOntClassInHierarchy = new LinkedHashSet<OntoClassInfo>();
////		this.repeats = 1;
//	}
	
	public OntoClassInfo(String classLabel) {
		this.label = classLabel;
		this.properties = new ArrayList<OntPropertyInfo>();
		this.superOntClassInHierarchy = new LinkedHashSet<OntoClassInfo>();
	}
	
	public OntoClassInfo(String URI, String nameSpace, String classLabel) {
		this.URI = URI;
		this.nameSpace = nameSpace;
		this.label = classLabel;
		this.properties = new ArrayList<OntPropertyInfo>();
		this.superOntClassInHierarchy = new LinkedHashSet<OntoClassInfo>();
	}

	public String getOntClassName() {
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}
	
	public void addProperty(OntPropertyInfo property) {
		this.properties.add(property);
	}
	
	public void addProperties(Collection<OntPropertyInfo> properties) {
		this.properties.addAll(properties);
	}
	
	public void addSuperOntClassesInHierarchy(Collection<OntoClassInfo> ontClasses){
		this.superOntClassInHierarchy.addAll(ontClasses);
	}
	
	public void addSuperOntClassInHierarchy(OntoClassInfo ontClass){
		this.superOntClassInHierarchy.add(ontClass);
	}
	
	public boolean hasSuperClasses() {
		return this.superOntClassInHierarchy.size() > 0 ? true : false;
	}

	public Collection<OntPropertyInfo> getProperties(){
		return this.properties;
	}
	
	public Collection<OntoClassInfo> getSuperOntClassInHierarchy(){
		return this.superOntClassInHierarchy;
	}

	public void printOntClassInfo() {
		System.out.println(this.label);
		for (OntPropertyInfo property : properties) {
			System.out.println("   " + property.getURI());
		}
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getURI() {
		return URI;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getNameSpace() { 
		return nameSpace;
	}
	
	@Override
	public int hashCode() {
		return this.URI.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		OntoClassInfo ontClassInfo = (OntoClassInfo) obj;
		if (this.getURI().equals(ontClassInfo.getURI())) {
			return true;
		} else {
			return false;
		}
	}

	public void setSimilarityToConcept(double similarityToConcept) {
		this.similarityToConcept = similarityToConcept;
	}

	public double getSimilarityToConcept() {
		return similarityToConcept;
	}

	
	public void setBestMatchedSuperOntoClassInfo(OntoClassInfo SuperOntoClassInfo){
		this.bestMatchedSuperOntoClassInfo = SuperOntoClassInfo;
	}
	
	public OntoClassInfo getBestMatchedSuperOntoClassInfo(){
		return this.bestMatchedSuperOntoClassInfo;
	}

	public void setHierarchyNumber(int hierarchyNumber) {
		this.hierarchyNumber = hierarchyNumber;
	}
	
	public int getHierarchyNumber(){
		return this.hierarchyNumber;
	}

	public void setLocalPathCode(String localPathCode) {
		this.localPathCode = localPathCode;
	}
	
	public String getLocalPathCode(){
		return this.localPathCode;
	}

}
