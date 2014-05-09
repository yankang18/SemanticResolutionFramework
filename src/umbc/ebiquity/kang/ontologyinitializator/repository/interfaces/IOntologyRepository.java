package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;

public interface IOntologyRepository {
	
	public static final String OWL = "http://www.w3.org/2002/07/owl#";
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String OWL_NOTHING = OWL + "Nothing";
	public static final String OWL_THING = OWL + "Thing";
	public static final String OWL_BottomDataProperty = OWL + "bottomDataProperty";
	public static final String OWL_BottomObjectProperty = OWL + "bottomObjectProperty";
	
	
	public boolean saveRepository();

	public boolean loadRepository();
	
	/**
	 * 
	 * @return
	 */
	public Collection<OntPropertyInfo> getAllOntProperties();
	
	public Collection<OntoClassInfo> getAllOntClasses();
	
	public OntoClassInfo getLightWeightOntClassByName(String className);
	
	public OntoClassInfo getOntClassByURI(String classURI);
	
	/**
	 * 
	 * @param globalCodeString
	 * @return
	 */
	public OntoClassInfo getOntClassByGlobalCode(String globalCodeString);
	
	public OntPropertyInfo getOntPropertyByURI(String propertyURI);
	
	public OntPropertyInfo getOntPropertyByName(String propertyName);
	
	/**
	 * 
	 * @param ontClassInfo
	 * @return
	 */
	public int getOntClassHierarchyNumber(OntoClassInfo ontClassInfo);
	
	/**
	 * @param ontClassInfo
	 * @return
	 */
	public String getLocalPathCode(OntoClassInfo ontClassInfo);

	/***
	 * get the collection of onto-classes of the class hierarchy specified by
	 * the hierarchy number
	 * 
	 * @param classHierarchyNumber - the hierarchy number of the requested hierarchy
	 * @return a collection of onto-classes that are in the requested hierarchy
	 */
	public Collection<OntoClassInfo> getOntClassesInClassHierarchy(String classHierarchyNumber);
	
	/***
	 * get the collection of onto-classes of the class hierarchy specified by an
	 * onto-class that is in his hierarchy
	 * 
	 * @param ontoClass
	 *            -
	 * @return a collection of onto-classes that are in the requested hierarchy
	 */
	public Collection<OntoClassInfo> getOntClassesInClassHierarchy(OntoClassInfo ontClass);
	
	/**
	 * Get all global properties. A global property is defined as a property
	 * that has no domain, which means that a global property can be property of
	 * any instances without modify the class membership
	 * 
	 * @return a collection of global properties
	 */
	public Collection<OntPropertyInfo> getGlobalOntProperties();

	/**
	 * 
	 * @param ontClass
	 * @return
	 */
	public Collection<OntPropertyInfo> getDeclaredOntProperties(OntoClassInfo ontClass);

	/**
	 * 
	 * @param ontClass
	 * @return
	 */
	public OntoClassInfo getTopLevelClass(OntoClassInfo ontClass);

	public Map<String, OntoClassInfo> getGlobalPathCode2OntClassMap();

	public String getGlobalPathCode(OntoClassInfo ontoClassInfo);

	boolean isInTheSameClassHierarchy(String firstClassName, String secondClassName);

	boolean isSubClassOf(String firstClassName, String secondClassName, boolean exclusive);

	boolean isSuperClassOf(String firstClassName, String secondClassName, boolean exclusive);

	OntoClassInfo getReconcileOntClass(Collection<String> localPathCodes, int classHierarchyNumber);

	String computeReconcileOntClassGlobalPathCode(Collection<String> localPathCodes, int classHierarchyNumber);

	OntoClassInfo getReconcileOntClass(Collection<OntoClassInfo> ontClassCollection);

	boolean isCrossOverAt(String ontClass1, String ontClass2, String pivotOntClass);

	OntoClassInfo getHeavyWeightOntClassByName(String className);

	Collection<String> computeMaximalConvergenceSet(Collection<String> mappedOntClasses, String pivotOntClass);

	Collection<String> getDownwardCotopy(String className);

	Collection<String> getUpwardCotopy(String className);

	Collection<OntoClassInfo> getUpwardCotopy(OntoClassInfo ontoClassInfo);

	Collection<String> getSemanticCotopy(String className);

	int getDepth(String className);
 
	void printOntologyInfo();

//	boolean isSibling(String firstClassName, String secondClassName);

	boolean isInTheSamePath(String firstClassName, String secondClassName);

	String getLowestCommonAncestor(String globalPathOne, String globalPathTwo);

}
