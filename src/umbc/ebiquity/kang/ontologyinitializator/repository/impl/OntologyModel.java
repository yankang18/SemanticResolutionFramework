package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.OntologyRepositorySchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.ibm.icu.util.StringTokenizer;

/***
 * 
 * @author Yan Kang 
 *
 */
public class OntologyModel implements IOntologyRepository {
	
	private enum FileType {
		OntoClass, OntoProperty, GlobalCode
	}
	
	private final String ONTOLOGY_PROPERTY_RECORDS_FILENAME = FileRepositoryParameterConfiguration.ONTOLOGY_PROPERTY_RECORDS_FILENAME;
	private final String ONTOLOGY_CLASS_RECORDS_FILENAME = FileRepositoryParameterConfiguration.ONTOLOGY_CLASS_RECORDS_FILENAME;
	private final String ONTOLOGY_CODED_CLASS_RECORDS_FILENAME = FileRepositoryParameterConfiguration.ONTOLOGY_CODED_CLASS_RECORDS_FILENAME;
	private final String ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH = FileRepositoryParameterConfiguration.getOntologyIndexFilesDirectoryFullPath();
	
	/**
	 * 
	 */
	private Map<String, OntoClassInfo> classURI2OntoClassObjectMap = new LinkedHashMap<String, OntoClassInfo>();
	
	/**
	 * 
	 */
	private Map<String, OntoClassInfo> className2OntoClassObjectMap = new LinkedHashMap<String, OntoClassInfo>();
	
	/**
	 * A collection of top level onto-classes. A top onto-class is defined as an
	 * onto-class that has no super onto-classes
	 */
	private Collection<OntoClassInfo> topLevelOntoClassCollection = new HashSet<OntoClassInfo>();
	
	/**
	 * A Map that maps the <strong>onto-class</strong> to its corresponding
	 * <strong>class hierarchical number</strong>
	 */
	private Map<OntoClassInfo, Integer> ontoClass2HierarchyNumberMap = new LinkedHashMap<OntoClassInfo, Integer>(); 
	
	/**
	 * A Map that maps the <strong>onto-class</strong> to its corresponding
	 * <strong>local path code</strong>
	 */
	private Map<OntoClassInfo, String> ontoClass2LocalPathCodeMap = new HashMap<OntoClassInfo, String>();
	
	/**
	 * 
	 */
	private Map<OntoClassInfo, Collection<OntPropertyInfo>> ontoClass2DeclaredOntPropertiesMap = new HashMap<OntoClassInfo, Collection<OntPropertyInfo>>();
	
	/**
	 * A Map that maps the <strong>global path code</strong> (in the form of
	 * string) to its corresponding <strong>onto-class</strong>
	 */
	private Map<String, OntoClassInfo> globalPathCode2OntClassMap = new HashMap<String, OntoClassInfo>();
	
	/**
	 * A Map that maps the <strong>class hierarchy number</strong> to its
	 * corresponding <strong>a set of onto-classes</strong>
	 */
	private Map<String, Set<OntoClassInfo>> classHierarchyNumber2OntoClassSet = new HashMap<String, Set<OntoClassInfo>>();
	
	/**
	 * A Map that maps the <strong>class hierarchy number</strong> to the top
	 * onto-class in this class hierarchy
	 */
	private Map<String, OntoClassInfo> classHierarchyNumber2TopOntoClass = new HashMap<String, OntoClassInfo>();

	/**
	 * A collection of global properties. A <strong>global property</strong> is
	 * defined as property has no domains.
	 */
	private Collection<OntPropertyInfo> globalPropertyCollection = new HashSet<OntPropertyInfo>();
	
	/**
	 * A Map that maps the <strong>URI of property</strong> to its corresponding
	 * <strong>property Java object</strong>
	 */
	private Map<String, OntPropertyInfo> propertyURI2OntoPropertyObjectMap = new HashMap<String, OntPropertyInfo>();
	
	/***
	 * 
	 */
    private OntModel _ontModel;
    
	public OntologyModel(OntModel ontModel) {
		this._ontModel = ontModel;
		this.populate();
	}

	public OntologyModel(){}
	
	private void populate() {
		this.recordOntProperties();
		this.recordAllOntClasses();
	}
	
	@Override
	public Collection<OntPropertyInfo> getAllOntProperties(){
		return this.propertyURI2OntoPropertyObjectMap.values();
	}
	
	@Override
	public Collection<OntoClassInfo> getAllOntClasses(){
		return this.classURI2OntoClassObjectMap.values();
	}
	
	@Override
	public OntoClassInfo getLightWeightOntClassByName(String className) {
//		System.out.println("HERE&&& " + className.trim());
		OntoClassInfo ontClassInfo = className2OntoClassObjectMap.get(className.trim());
		if(ontClassInfo == null) return null;
		OntoClassInfo ontClass = new OntoClassInfo(ontClassInfo.getURI(), 
												   ontClassInfo.getNameSpace(),
				                                   ontClassInfo.getOntClassName());
		return ontClass;
	}
	
	@Override
	public OntoClassInfo getHeavyWeightOntClassByName(String className) {
		OntoClassInfo ontClassInfo = className2OntoClassObjectMap.get(className.trim());
		if(ontClassInfo == null) return null;
		OntoClassInfo ontClass = new OntoClassInfo(ontClassInfo.getURI(), 
												   ontClassInfo.getNameSpace(),
				                                   ontClassInfo.getOntClassName());
		
		ontClass.addProperties(this.getDeclaredOntProperties(ontClass));
		ontClass.addSuperOntClassesInHierarchy(this.getUpwardCotopy(ontClass));
		ontClass.setHierarchyNumber(this.getOntClassHierarchyNumber(ontClass)); 
		ontClass.setLocalPathCode(this.getLocalPathCode(ontClass)); 
		return ontClass;
	}
	
	@Override
	public OntoClassInfo getOntClassByURI(String classURI){
		return classURI2OntoClassObjectMap.get(classURI.trim());
	}
	
	@Override
	public OntoClassInfo getOntClassByGlobalCode(String globalCodeString){ 
		return this.globalPathCode2OntClassMap.get(globalCodeString);
	}
	
	@Override
	public Map<String, OntoClassInfo> getGlobalPathCode2OntClassMap(){
		return this.globalPathCode2OntClassMap;
	}
	
	@Override
	public OntPropertyInfo getOntPropertyByURI(String propertyURI){
		return propertyURI2OntoPropertyObjectMap.get(propertyURI);
	}
	
	@Override
	public OntPropertyInfo getOntPropertyByName(String propertyName){
		return null;
	}
	
	@Override
	public Collection<OntPropertyInfo> getDeclaredOntProperties(OntoClassInfo ontClass){
		Collection<OntPropertyInfo> declaredProperties = this.ontoClass2DeclaredOntPropertiesMap.get(ontClass);
		if(declaredProperties == null){
			return new HashSet<OntPropertyInfo>();
		}
		return declaredProperties;
	}
	
	@Override
	public int getOntClassHierarchyNumber(OntoClassInfo ontClassInfo){
		return this.ontoClass2HierarchyNumberMap.get(ontClassInfo);
	}
	
	@Override
	public String getLocalPathCode(OntoClassInfo ontClassInfo){ 
		return this.ontoClass2LocalPathCodeMap.get(ontClassInfo);
	}

	@Override
	public String getGlobalPathCode(OntoClassInfo ontoClassInfo) {
		return getOntClassHierarchyNumber(ontoClassInfo) + "-" + getLocalPathCode(ontoClassInfo);
	}
	
	@Override
	public Collection<OntoClassInfo> getOntClassesInClassHierarchy(String classHierarchyNumber){
		return this.classHierarchyNumber2OntoClassSet.get(classHierarchyNumber);
	}
	
	@Override
	public OntoClassInfo getTopLevelClass(OntoClassInfo ontClass) {
		int hierarchyNumber = this.ontoClass2HierarchyNumberMap.get(ontClass);
		return this.classHierarchyNumber2TopOntoClass.get(String.valueOf(hierarchyNumber));
	}
	
	@Override
	public Collection<OntoClassInfo> getOntClassesInClassHierarchy(OntoClassInfo ontClass){
		int classHierarchyNumber = this.ontoClass2HierarchyNumberMap.get(ontClass);
		return this.getOntClassesInClassHierarchy(String.valueOf(classHierarchyNumber)); 
	}
	
	@Override
	public Collection<OntoClassInfo> getUpwardCotopy(OntoClassInfo ontClass) {
		Collection<OntoClassInfo> superOntClassesInPath = new ArrayList<OntoClassInfo>();
//		System.out.println("@@ " + ontClass.getOntClassName());
		int classHierarchyNumber = this.getOntClassHierarchyNumber(ontClass);
		String localPathCodeString = this.getLocalPathCode(ontClass);
		String[] localPathCodeArray = tokenizePathCodeString(localPathCodeString);
		String classHierarchyNumerStr = String.valueOf(classHierarchyNumber);
		for (String code : localPathCodeArray) {
			classHierarchyNumerStr = classHierarchyNumerStr + "-" + code;
			OntoClassInfo superOntClass = this.getOntClassByGlobalCode(classHierarchyNumerStr);
			if(superOntClass != null){
				superOntClassesInPath.add(superOntClass);
			}
		}
		return superOntClassesInPath;
	}
	
	@Override
	public Collection<String> getSemanticCotopy(String className) {
		Collection<String> maximalTributaryClassSet = new HashSet<String>();
		maximalTributaryClassSet.addAll(this.getUpwardCotopy(className));
		maximalTributaryClassSet.addAll(this.getDownwardCotopy(className));
		return maximalTributaryClassSet;
	}
	
	@Override
	public Collection<String> getUpwardCotopy(String className){
		Collection<String> superOntClassesInPath = new ArrayList<String>();
		OntoClassInfo ontClass = this.getLightWeightOntClassByName(className);
		int classHierarchyNumber = this.getOntClassHierarchyNumber(ontClass);
		String localPathCodeString = this.getLocalPathCode(ontClass);
		String[] localPathCodeArray = tokenizePathCodeString(localPathCodeString);
		String classHierarchyNumerStr = String.valueOf(classHierarchyNumber);
		for (String code : localPathCodeArray) {
			classHierarchyNumerStr = classHierarchyNumerStr + "-" + code;
			OntoClassInfo superOntClass = this.getOntClassByGlobalCode(classHierarchyNumerStr);
			if(superOntClass != null){
				superOntClassesInPath.add(superOntClass.getOntClassName());
			}
		}
		return superOntClassesInPath;
	}

	@Override
	public Collection<String> getDownwardCotopy(String className){
		Collection<String> subTributaryClasses = new HashSet<String>();
		OntoClassInfo ontClass = this.getLightWeightOntClassByName(className);
		String globalPathCode = this.getGlobalPathCode(ontClass);
//		int globalPathCodeLength = tokenizePathCodeString(globalPathCode).length;
//		Collection<String> subClasses = new HashSet<String>();
//		Set<String> subClassLocalCode = new HashSet<String>();
		for (String GPC : globalPathCode2OntClassMap.keySet()) {
			if (this.isSubPathCodeOf(globalPathCode, GPC)) {
//				String token = tokenizePathCodeString(GPC)[globalPathCodeLength];
//				subClassLocalCode.add(token);
//				subClasses.add(globalPathCode2OntClassMap.get(GPC).getOntClassName());
				subTributaryClasses.add(globalPathCode2OntClassMap.get(GPC).getOntClassName());
			}
		}
//		if (subClassLocalCode.size() > 1) {
//			subTributaryClasses.addAll(subClasses);
//		} 
		return subTributaryClasses;
	}
	
	@Override
	public int getDepth(String className){
		String globalPathCode = this.getGlobalPathCode(this.getLightWeightOntClassByName(className));
		return this.tokenizePathCodeString(globalPathCode).length;
	}
	
	@Override
	public boolean isInTheSamePath(String firstClassName, String secondClassName){
		if(firstClassName.equals(secondClassName)) return true;
		
		if(this.isSubClassOf(firstClassName, secondClassName, true) || this.isSuperClassOf(firstClassName, secondClassName, true)){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isSubClassOf(String firstClassName, String secondClassName, boolean exclusive){
		OntoClassInfo firstClass = this.getLightWeightOntClassByName(firstClassName);
		OntoClassInfo secondClass = this.getLightWeightOntClassByName(secondClassName);
		if(firstClass == null || secondClass == null) return false;
		
		String firstGPC = this.getGlobalPathCode(firstClass);
		String secondGPC = this.getGlobalPathCode(secondClass);
		
		if(!exclusive && firstGPC.equals(secondGPC)){
			return true;
		}
		
		if(this.isSubPathCodeOf(secondGPC, firstGPC)){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isSuperClassOf(String firstClassName, String secondClassName, boolean exclusive){
		OntoClassInfo firstClass = this.getLightWeightOntClassByName(firstClassName);
		OntoClassInfo secondClass = this.getLightWeightOntClassByName(secondClassName);
		if(firstClass == null || secondClass == null) return false;
		
		String firstGPC = this.getGlobalPathCode(firstClass);
		String secondGPC = this.getGlobalPathCode(secondClass);
		
		if(!exclusive && firstGPC.equals(secondGPC)){
			return true;
		}
		
		if(this.isSubPathCodeOf(firstGPC, secondGPC)){
			return true;
		}
		
		return false;
	}
	
	/***
	 * 
	 * @param globalPathOne
	 * @param globalPathTwo
	 * @return true if the globalPathOne is sub-PathCode of globalPathTwo.
	 *         Otherwise return false. NOTE that if globalPathOne is equal to
	 *         globalPathTwo, this fuction will return false.
	 */
	private boolean isSubPathCodeOf(String globalPathOne, String globalPathTwo){
		if (globalPathOne.equals(globalPathTwo))
			return false;
		
		String[] subjectTokens = this.tokenizePathCodeString(globalPathOne);
		String[] objectTokens = this.tokenizePathCodeString(globalPathTwo);
		int sizeOfSubject = subjectTokens.length;
		int sizeOfObject = objectTokens.length;

		if (sizeOfSubject >= sizeOfObject)
			return false;

		for (int i = 0; i < sizeOfSubject; i++) {
			if (!subjectTokens[i].equals(objectTokens[i])) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getLowestCommonAncestor(String firstClassName, String secondClassName){
		if (firstClassName.equals(secondClassName))
			return firstClassName;
		
		OntoClassInfo firstClass = this.getLightWeightOntClassByName(firstClassName);
		OntoClassInfo secondClass = this.getLightWeightOntClassByName(secondClassName);
		
		String firstGPC = this.getGlobalPathCode(firstClass);
		String secondGPC = this.getGlobalPathCode(secondClass);
		
		String[] subjectTokens = this.tokenizePathCodeString(firstGPC);
		String[] objectTokens = this.tokenizePathCodeString(secondGPC);
		int sizeOfSubject = subjectTokens.length;
		int sizeOfObject = objectTokens.length;
		int size = sizeOfObject <= sizeOfSubject ? sizeOfObject : sizeOfSubject;
		StringBuilder globalClassPath = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (subjectTokens[i].equals(objectTokens[i])) {
				globalClassPath.append(subjectTokens[i] + "-");
			} else {
				break;
			}
		}
		
		String globalPathString = globalClassPath.toString();
//		System.out.println("$" + globalPathString +" "+ " "+globalPathString.length());
		globalPathString = globalPathString.substring(0, globalPathString.length() - 1);
		return this.getOntClassByGlobalCode(globalPathString).getOntClassName();
		
	}
	
	

//	@Override
//	public Collection<Set<String>> retainOntClassesInSemanticCotopyOfOntClass(Collection<String> mappedOntClasses, String pivotOntClass){
//		Collection<Set<String>> tributaryClasssets = new HashSet<Set<String>>();
//		Collection<String> mappedOntClasses1 = new HashSet<String>(mappedOntClasses);
//		Collection<String> mappedOntClasses2 = new HashSet<String>(mappedOntClasses);
//		mappedOntClasses1.retainAll(this.getUpwardCotopy(this.getLightWeightOntClassByName(pivotOntClass)));
//		mappedOntClasses2.retainAll(this.getDownwardCotopy(this.getLightWeightOntClassByName(pivotOntClass)));
//		
//		for(String ontClass1 : mappedOntClasses1){
//			Set<String> tributaryClassset = new HashSet<String>();
//			tributaryClassset.add(ontClass1);
//			tributaryClasssets.add(tributaryClassset);
//		}
//		if (mappedOntClasses2.size() > 1) {
//			for (String ontClass1 : mappedOntClasses2) {
//				for (String ontClass2 : mappedOntClasses2) {
//					if (!ontClass1.equals(ontClass2) && this.isCrossOverAt(ontClass1, ontClass2, pivotOntClass)) {
//						Set<String> tributaryClassset = new LinkedHashSet<String>();
//						if (ontClass1.compareTo(ontClass2) < 0) {
//							tributaryClassset.add(ontClass1);
//							tributaryClassset.add(ontClass2);
//						} else {
//							tributaryClassset.add(ontClass2);
//							tributaryClassset.add(ontClass1);
//						}
//						tributaryClasssets.add(tributaryClassset);
//					}
//				}
//			}
//		}
//		return tributaryClasssets;
//	}
	
	@Override
	public Collection<String> computeMaximalConvergenceSet(Collection<String> mappedOntClasses, String pivotOntClass){
		Collection<String> tributaryClasssets = new HashSet<String>();
		Collection<String> mappedOntClasses1 = new HashSet<String>(mappedOntClasses);
		Collection<String> mappedOntClasses2 = new HashSet<String>(mappedOntClasses);
		mappedOntClasses1.retainAll(this.getUpwardCotopy(pivotOntClass));
		mappedOntClasses2.retainAll(this.getDownwardCotopy(pivotOntClass));
		
		for(String ontClass1 : mappedOntClasses1){
			tributaryClasssets.add(ontClass1);
		}
		if (mappedOntClasses2.size() > 1) {
			for (String ontClass1 : mappedOntClasses2) {
				for (String ontClass2 : mappedOntClasses2) {
					if (!ontClass1.equals(ontClass2) && this.isCrossOverAt(ontClass1, ontClass2, pivotOntClass)) {
						tributaryClasssets.add(ontClass1);
						tributaryClasssets.add(ontClass2);
					}
				}
			}
		}
		return tributaryClasssets;
	}
	
	
	@Override
	public boolean isCrossOverAt(String ontClass1, String ontClass2, String pivotOntClass) {
//		String localPathCode1 = this.getLocalPathCode(this.getLightWeightOntClassByName(ontClass1));
//		String localPathCode2 = this.getLocalPathCode(this.getLightWeightOntClassByName(ontClass2));
//		
//		Collection<String> localPathCodeCollection = new HashSet<String>();
//		localPathCodeCollection.add(localPathCode1);
//		localPathCodeCollection.add(localPathCode2);
//		int classHierarchyNumber = this.getOntClassHierarchyNumber(this.getLightWeightOntClassByName(pivotOntClass));
//		String globalPathCode4 = this.getGlobalPathCode(this.getLightWeightOntClassByName(pivotOntClass));
//		String globalPathCode3 = this.computeReconcileOntClassGlobalPathCode(localPathCodeCollection, classHierarchyNumber);
//		return globalPathCode3.equals(globalPathCode4); 
		
		String pathCode1 = this.getGlobalPathCode(this.getLightWeightOntClassByName(ontClass1));
		String pathCode2 = this.getGlobalPathCode(this.getLightWeightOntClassByName(ontClass2));
		String pathCode = this.getGlobalPathCode(this.getLightWeightOntClassByName(pivotOntClass));
		
		if(!this.isSubPathCodeOf(pathCode, pathCode1) || !this.isSubPathCodeOf(pathCode, pathCode2)){
			return false;
		}
		
		String[] token1 = tokenizePathCodeString(pathCode1);
		String[] token2 = tokenizePathCodeString(pathCode2);
		String[] token0 = tokenizePathCodeString(pathCode);
		
		if(!token1[token0.length].equals(token2[token0.length])){
			return true;
		}
		return false;
		
	}

	@Override
	public Collection<OntPropertyInfo> getGlobalOntProperties(){
		return this.globalPropertyCollection;
	}
	
	/**
	 * To check if the first class and second one are in the same class hierarchy
	 * 
	 * @param firstClassName
	 * @param secondClassName
	 * @return true if the first class is a subclass of the second one.
	 */
	@Override
	public boolean isInTheSameClassHierarchy(String firstClassName, String secondClassName) {
		OntoClassInfo firstClass = this.getLightWeightOntClassByName(firstClassName);
		OntoClassInfo secondClass = this.getLightWeightOntClassByName(secondClassName);
		if(firstClass == null || secondClass == null) return false;
		String firstClassHierarchyNumber = String.valueOf(this.getOntClassHierarchyNumber(firstClass));
		String secondClassHierarchyNumer = String.valueOf(this.getOntClassHierarchyNumber(secondClass));
		return firstClassHierarchyNumber.equals(secondClassHierarchyNumer);
	}
	
	@Override
	public OntoClassInfo getReconcileOntClass(Collection<OntoClassInfo> ontClassCollection) {
		if (ontClassCollection.size() == 0) return null;
		Collection<String> localPathCodes = new ArrayList<String>();
		int classHierarchyNumber = -1;
		for (OntoClassInfo ontClass : ontClassCollection) {
			if (classHierarchyNumber == -1) {
				classHierarchyNumber = this.getOntClassHierarchyNumber(ontClass);
			}
			System.out.println("-has member: " + ontClass.getOntClassName());
			String pathCodeStr = this.getLocalPathCode(ontClass);
			localPathCodes.add(pathCodeStr);
		}
		return this.getReconcileOntClass(localPathCodes, classHierarchyNumber);
	}
	
	@Override
	public OntoClassInfo getReconcileOntClass(Collection<String> localPathCodes, int classHierarchyNumber){
		String identifiedGlobalPathCode = this.computeReconcileOntClassGlobalPathCode(localPathCodes, classHierarchyNumber);
		OntoClassInfo ontClassInfo = this.getOntClassByGlobalCode(identifiedGlobalPathCode);
//		System.out.println(ontClassInfo.getURI());
		return ontClassInfo;
	}
	
	@Override
	public String computeReconcileOntClassGlobalPathCode(Collection<String> pathCodes, int classHierarchyNumber){
		int numberOfPathCodes = pathCodes.size();
		/*
		 * if has more than one matched ontology class
		 */
		int maxPathCodeLength = 0;
		List<String[]> pathCodeArrayList = new ArrayList<String[]>();
		for (String pathCode : pathCodes) {
//			System.out.println("Path Code: " + pathCode);
			String[] pathCodeArray = this.tokenizePathCodeString(pathCode);
			pathCodeArrayList.add(pathCodeArray);
			if(pathCodeArray.length > maxPathCodeLength){
				maxPathCodeLength = pathCodeArray.length;
			}
		}
		
		String identifiedOntClassPathCode = "";
		for (int currentPathLevelPointer = 0; currentPathLevelPointer < maxPathCodeLength; currentPathLevelPointer++) {
			
			String firstCode = "";
			int currentPathCodePointer = 0;
			for (; currentPathCodePointer < numberOfPathCodes; currentPathCodePointer++) {
				String[] pathCodeArray = pathCodeArrayList.get(currentPathCodePointer);
				if (!this.outOfIndexOfPathCodeArray(pathCodeArray.length, currentPathLevelPointer)) {
					firstCode = pathCodeArray[currentPathLevelPointer];
					break;
				}
			}
			
			boolean pathForkHited = false;
			for (; currentPathCodePointer < numberOfPathCodes; currentPathCodePointer++) {
				String[] pathCodeArray = pathCodeArrayList.get(currentPathCodePointer);
				if (!this.outOfIndexOfPathCodeArray(pathCodeArray.length, currentPathLevelPointer)) {
					String code = pathCodeArray[currentPathLevelPointer];
					if(!code.equals(firstCode)){
						pathForkHited = true;
						break;
					}
				}
			}
			
			if(pathForkHited){
				break;
			} else {
				identifiedOntClassPathCode += "-" + firstCode;
			}
		}

		identifiedOntClassPathCode = classHierarchyNumber + "-" + identifiedOntClassPathCode.substring(1);
		return identifiedOntClassPathCode;
	}
	
	private boolean outOfIndexOfPathCodeArray(int pathCodeArrayLength, int currentPointer){
		return pathCodeArrayLength <= currentPointer;
	}
	
	/**
	 * This method has two objectives: <br/>
	 * (1) Record all <strong>onto-classes</strong> from the ontology, including the <strong>declared properties</strong> of these classes. <br/>
	 * (2) Hash the <strong>class hierarchy number</strong> for each class and <strong>local/global path code</strong> for each class. <br/>
	 */
	private void recordAllOntClasses() {

		Iterator<OntClass> ontClasses = _ontModel.listClasses();
		int classHierarchyNumber = 1; // the class hierarchy number start from 1.
		while (ontClasses.hasNext()) {
			OntClass ontClass = ontClasses.next();
			// System.out.println("??? " + ontClass.getURI() + "   " + ontClass.getLocalName());
			if (ontClass.getURI() != null) {
				OntoClassInfo ontClassInfo = new OntoClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName());
				this.recordDeclaredProperties(ontClass, ontClassInfo);
				this.classURI2OntoClassObjectMap.put(ontClass.getURI(), ontClassInfo);
				this.className2OntoClassObjectMap.put(ontClass.getLocalName(), ontClassInfo);
				classHierarchyNumber = this.hashClassesInClassHierarchy(ontClass, classHierarchyNumber);
			}
		}
	}

	/**
	 * Hash the class hierarchy number and local/global path code for each
	 * class. In This method, following objects will be created. <br/>
	 * (1) a topLevelOntoClassCollection HashSet stores top level classes in the
	 * ontology. <br/>
	 * (2) an ontoClass2HierarchyNumberMap HashMap maps classes to their class
	 * hierarchy number. <br/>
	 * (3) an ontoClass2LocalPathCodeMap HashMap maps classes to their local
	 * path code. <br/>
	 * (4) a globalPathCode2OntoClassMap HashMap maps global path codes to
	 * classes. <br/>
	 * 
	 * Definition: <br/>
	 * The <strong>local path code</strong> of a onto-class is the position of a
	 * onto-class in a class hierarchy. <br/>
	 * The <strong>global path code</strong> of a onto-class is the
	 * concatenation of the <strong>class hierarchy number</strong> of the
	 * onto-class and the <strong>local path code</strong> of this onto-class <br/>
	 * 
	 * 
	 * @param ontClass
	 * @param classHierarchyNumber
	 * @return
	 */
	private int hashClassesInClassHierarchy(OntClass ontClass, int classHierarchyNumber) {

		if (ontClass.getSuperClass() == null || ontClass.getSuperClass().getURI().equals(OWL_THING)) { 
			/*
			 * Hashing class hierarchy number starts from top level classes. A top
			 * level class is defined as a class has no super class or only has
			 * OWL:THING class as its super class.
			 */
			OntoClassInfo ontClassInfo = new OntoClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName());
			this.recordDeclaredProperties(ontClass, ontClassInfo);
			this.topLevelOntoClassCollection.add(ontClassInfo);
			this.classHierarchyNumber2TopOntoClass.put(String.valueOf(classHierarchyNumber), ontClassInfo);
			this.ontoClass2HierarchyNumberMap.put(ontClassInfo, classHierarchyNumber);
			this.ontoClass2LocalPathCodeMap.put(ontClassInfo, "1");
			this.globalPathCode2OntClassMap.put(classHierarchyNumber + "-1", ontClassInfo);
			this.groupOntClassesInSameClassHierarchy(ontClassInfo, String.valueOf(classHierarchyNumber));
			
			Iterator<OntClass> subClasses = ontClass.listSubClasses(true); // only get direct sub-classes of current class
			int subClassNumber = 1;
			while (subClasses.hasNext()) {
				OntClass subClass = subClasses.next();
				String localPathCode = "1-" + subClassNumber;
				this.hashSubClassesInClassHierarchy(subClass, classHierarchyNumber, localPathCode);
				subClassNumber++;
			}
			classHierarchyNumber++;
		}
		return classHierarchyNumber;
	}
	
	private void hashSubClassesInClassHierarchy(OntClass ontClass, int classHierarchyNumber, String localPathCode){
		OntoClassInfo ontClassInfo = new OntoClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName());
		this.recordDeclaredProperties(ontClass, ontClassInfo);
		this.ontoClass2HierarchyNumberMap.put(ontClassInfo, classHierarchyNumber);
		this.ontoClass2LocalPathCodeMap.put(ontClassInfo, localPathCode);
		this.globalPathCode2OntClassMap.put(classHierarchyNumber + "-" + localPathCode, ontClassInfo);
		this.groupOntClassesInSameClassHierarchy(ontClassInfo, String.valueOf(classHierarchyNumber));
		Iterator<OntClass> subClasses = ontClass.listSubClasses(true); // only get direct sub-classes of current class
		int subClassNumber = 1;
		while (subClasses.hasNext()) {
			OntClass subClass = subClasses.next();
			String newLocalPathCode = localPathCode + "-" + subClassNumber;
			this.hashSubClassesInClassHierarchy(subClass, classHierarchyNumber, newLocalPathCode);
			subClassNumber++;
		}
	}
	
	/**
	 * group onto-classes from the same class hierarchy by using HashSet, the
	 * key of which is the class hierarchy number and the value of which is a
	 * set of onto-classes with the same class hierarchy number
	 * 
	 * @param ontClass - the onto-class
	 * @param classHierarchyNumber - the class hierarchy number of the inputed onto-class
	 */
	private void groupOntClassesInSameClassHierarchy(OntoClassInfo ontClass, String classHierarchyNumber) {
		String classHierarchyNumberStr = String.valueOf(classHierarchyNumber);
		Set<OntoClassInfo> ontClassSet = classHierarchyNumber2OntoClassSet.get(classHierarchyNumberStr);
		if (ontClassSet != null) {
			ontClassSet.add(ontClass);
		} else {
			ontClassSet = new LinkedHashSet<OntoClassInfo>();
			classHierarchyNumber2OntoClassSet.put(classHierarchyNumberStr, ontClassSet);
			ontClassSet.add(ontClass);
		}
	}

	/**
	 * Get <strong>declared properties</strong> of an ontClass excluding global properties and
	 * store them into ontClassInfo. A declared property may be a global
	 * property.
	 * 
	 * @param ontClass
	 * @param ontClassInfo
	 */
	private void recordDeclaredProperties(OntClass ontClass, OntoClassInfo ontClassInfo) {
		Iterator<OntProperty> ontProperties = ontClass.listDeclaredProperties();
		while (ontProperties.hasNext()) {
			OntProperty ontProperty = ontProperties.next();
			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), 
					                                              ontProperty.getNameSpace(),
					                                              ontProperty.getLocalName());
			if (!globalPropertyCollection.contains(ontPropertyInfo)) /* if the declared property is not a global property */
			{
				ontClassInfo.addProperty(ontPropertyInfo);
				
				Collection<OntPropertyInfo> declaredProperties = ontoClass2DeclaredOntPropertiesMap.get(ontClassInfo);
				if(declaredProperties == null){
					declaredProperties = new HashSet<OntPropertyInfo>();
					ontoClass2DeclaredOntPropertiesMap.put(ontClassInfo, declaredProperties);
				}
				declaredProperties.add(ontPropertyInfo);
			}
		}
	}
	
	/**
	 * This method has Three objectives: <br/>
	 * (1) Record all the properties by using HashMap (the key of the map is URI of property). <br/>
	 * (2) Record global properties. <br/>
	 * (3) Record the domains, all possible subjects and objects of each property.  <br/>
	 */
	private void recordOntProperties(){
		
		Iterator<OntProperty> allOntProperties = _ontModel.listAllOntProperties();
		while (allOntProperties.hasNext()) {
			OntProperty ontProperty = allOntProperties.next();
			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), 
					                                              ontProperty.getNameSpace(),
					                                              ontProperty.getLocalName());
			
			if (ontProperty.isDatatypeProperty()) {
				ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
			} else if (ontProperty.isObjectProperty()) {
				ontPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
			}
			
			propertyURI2OntoPropertyObjectMap.put(ontPropertyInfo.getURI(), ontPropertyInfo);
			
			if (ontProperty.getDomain() == null) { 
				System.out.println("Global Property: " + ontProperty.getURI());
				this.globalPropertyCollection.add(ontPropertyInfo); // record global properties, which are properties that have no domain
			}
			
			Iterator<? extends OntResource> domains = ontProperty.listDomain();
			while(domains.hasNext()){
				OntResource domain = domains.next();
				if(domain.isURIResource()){
					/*
					 * Should also consider UNION or INTERSECTION Domain
					 */
					this.addPropertySubjectCandidate(ontPropertyInfo, domain);
					this.addPropertyDomain(ontPropertyInfo, domain);
					OntClass domainOntClass = _ontModel.getOntClass(domain.getURI());
					Iterator<OntClass> subClasses = domainOntClass.listSubClasses(true);
					if (subClasses != null) {
						this.addPropertySubjectCandidates(ontPropertyInfo, subClasses);
					}
				}
			}
			
			Iterator<? extends OntResource> ranges = ontProperty.listRange();
			while(ranges.hasNext()){
				OntResource range = ranges.next();
				if (range.isURIResource()) {
					/*
					 * Should also consider UNION or INTERSECTION Range 
					 */
//					System.out.println("@@ " + range.getURI());
					OntClass rangeOntClass = _ontModel.getOntClass(range.getURI());
					if (rangeOntClass != null) {
						this.addPropertyObjectCandidate(ontPropertyInfo, range);
						Iterator<OntClass> subClasses = rangeOntClass.listSubClasses(true);
						if (subClasses != null) {
							this.addPropertyObjectCandidates(ontPropertyInfo, subClasses);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Record all the terms/concepts that might be the object of the property.
	 * 
	 * @param property
	 * @param condidates
	 */
	private void addPropertyObjectCandidates(OntPropertyInfo property, Iterator<OntClass> condidates) {
		while(condidates.hasNext()){
			OntClass condidate = condidates.next();
			if(condidate.isURIResource()){
				this.addPropertyObjectCandidate(property, condidate);
				Iterator<OntClass> subClasses = condidate.listSubClasses(true);
				this.addPropertyObjectCandidates(property, subClasses);
			}
		}
	}
	
	/**
	 * Record all terms/concepts that might be the subject of the property.
	 * 
	 * @param property
	 * @param condidates
	 */
	private void addPropertySubjectCandidates(OntPropertyInfo property, Iterator<OntClass> condidates){
		while(condidates.hasNext()){
			OntClass condidate = condidates.next();
			if(condidate.isURIResource()){
				this.addPropertySubjectCandidate(property, condidate);
				Iterator<OntClass> subClasses = condidate.listSubClasses(true);
				this.addPropertySubjectCandidates(property, subClasses);
			}
		}
	}
	
	private void addPropertyObjectCandidate(OntPropertyInfo property, OntResource res) {
		OntResourceInfo candidate = new OntResourceInfo(res.getURI(), res.getNameSpace(), res.getLocalName());
		property.addObjectCandidate(candidate);
	}
	
	private void addPropertySubjectCandidate(OntPropertyInfo property, OntResource res){
		OntResourceInfo candidate = new OntResourceInfo(res.getURI(), res.getNameSpace(), res.getLocalName());
		property.addSubjectCandidate(candidate);
	}
	
	private void addPropertyDomain(OntPropertyInfo property, OntResource res) {
		OntResourceInfo domain = new OntResourceInfo(res.getURI(), res.getNameSpace(), res.getLocalName());
		property.addDomain(domain);
	}
	
	private String[] tokenizePathCodeString(String pathCodeString) {
		StringTokenizer textTokenizer = new StringTokenizer(pathCodeString, "-");
		String[] tokens = new String[textTokenizer.countTokens()];
		for (int i = 0; textTokenizer.hasMoreTokens(); i++) {
			String token = textTokenizer.nextToken();
			tokens[i] = token;
		}
		return tokens;
	}
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean saveRepository() {
		System.out.println("save ontology to local files ...");
		
		StringBuilder globalPathCode2OntoClassMappingStrBuilder = new StringBuilder();
		StringBuilder ontoClassCollectionStrBuilder = new StringBuilder();
		StringBuilder ontoPropertyCollectionStrBuilder= new StringBuilder();
		
		for (String globalPathCode : globalPathCode2OntClassMap.keySet()) {
			OntoClassInfo ontoClassInfo = globalPathCode2OntClassMap.get(globalPathCode);
			String classURI = ontoClassInfo.getURI();
			String classNS = ontoClassInfo.getNameSpace();
			String className = ontoClassInfo.getOntClassName();
			String localPathCode = ontoClass2LocalPathCodeMap.get(ontoClassInfo);
			int classHierarchyNumber = ontoClass2HierarchyNumberMap.get(ontoClassInfo);
			boolean isTopClass = false;
			if (this.topLevelOntoClassCollection.contains(ontoClassInfo)) {
				isTopClass = true;
			}
			
			//get URIs of declared properties of the onto-class
			List<String> ontoPropertyUriList = new ArrayList<String>();
			for(OntPropertyInfo propertyInfo : ontoClassInfo.getProperties()){
				ontoPropertyUriList.add(propertyInfo.getURI());
			}
			
			/*
			 * record the global path code to onto-class mapping
			 */
			Map<String, String> ontoClassIndexRecord = new LinkedHashMap<String, String>();
			ontoClassIndexRecord.put(OntologyRepositorySchemaParameter.GLOBAL_PATH_CODE, globalPathCode);
			ontoClassIndexRecord.put(OntologyRepositorySchemaParameter.CLASS_URI, classURI);
			globalPathCode2OntoClassMappingStrBuilder.append(JSONObject.toJSONString(ontoClassIndexRecord)); // add coded class record
			globalPathCode2OntoClassMappingStrBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR); 
			
			/*
			 * record information of a onto-class (in the form of JSON)
			 */
			JSONObject ontoClassRecord = new JSONObject();
			ontoClassRecord.put(OntologyRepositorySchemaParameter.CLASS_URI, classURI);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.CLASS_NAMESPACE, classNS);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.CLASS_NAME, className);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.LOCAL_PATH_CODE, localPathCode);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.CLASS_HIERARCHY_NUMBER, classHierarchyNumber);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.IS_TOP_LEVEL_CLASS, isTopClass);
			ontoClassRecord.put(OntologyRepositorySchemaParameter.DECLARED_PROPERTY_LIST, ontoPropertyUriList);
			ontoClassCollectionStrBuilder.append(ontoClassRecord.toJSONString()); // add class record
			ontoClassCollectionStrBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR); 
		}
		
		for(String propertURI : propertyURI2OntoPropertyObjectMap.keySet()){
			JSONObject ontoPropertyRecord = new JSONObject();
			OntPropertyInfo propertyInfo = propertyURI2OntoPropertyObjectMap.get(propertURI);
			String localName = propertyInfo.getLocalName();
			String nameSpace = propertyInfo.getNamespace();
			OntPropertyType propertyType = propertyInfo.getPropertyType();
			boolean isGlobalProperty = false;
			if(this.globalPropertyCollection.contains(propertyInfo)){
				isGlobalProperty = true;
			}
			
			/*
			 * record information of a onto-property (in the form of JSON)
			 */
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.PROPERTY_URI, propertURI);
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.PROPERTY_NAMESPACE, nameSpace);
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.PROPERTY_NAME, localName);
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.PROPERTY_TYPE, propertyType.toString());
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.IS_GLOBAL_PROPERTY, isGlobalProperty);
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.DOMAIN_RESOURCE_LIST, this.createResourceList(propertyInfo.getAllDomainClasses()));
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.RANGE_RESOURCE_LIST, this.createResourceList(propertyInfo.getAllRangeClasses()));
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.SUBJECT_RESOURCE_LIST, this.createResourceList(propertyInfo.getSubjectCandidates()));
			ontoPropertyRecord.put(OntologyRepositorySchemaParameter.OBJECT_RESOURCE_LIST, this.createResourceList(propertyInfo.getObjectCandidates()));
			ontoPropertyCollectionStrBuilder.append(ontoPropertyRecord.toJSONString()); // add property record
			ontoPropertyCollectionStrBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		}
		
		/*
		 * save various records in form of JSON to files
		 */
		boolean succeed1 = FileAccessor.saveTripleString(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_CODED_CLASS_RECORDS_FILENAME, 
				                                                globalPathCode2OntoClassMappingStrBuilder.toString());
		boolean succeed2 = FileAccessor.saveTripleString(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_CLASS_RECORDS_FILENAME, 
				                                                ontoClassCollectionStrBuilder.toString());
		boolean succeed3 = FileAccessor.saveTripleString(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_PROPERTY_RECORDS_FILENAME, 
				                                                ontoPropertyCollectionStrBuilder.toString());

		if (succeed1 && succeed2 && succeed3) {
			return true;
		} else {
			return false;
		}

	}
	
	private List<Map<String, String>> createResourceList(Collection<OntResourceInfo> resourceList){
		List<Map<String, String>> resourceRecordList = new ArrayList<Map<String, String>>();
		for(OntResourceInfo ontoResourceInfo : resourceList){
			Map<String, String> resourceRecord = new LinkedHashMap<String, String>();
			resourceRecord.put(OntologyRepositorySchemaParameter.RESOURCE_URI, ontoResourceInfo.getURI());
			resourceRecord.put(OntologyRepositorySchemaParameter.RESOURCE_NAMESPACE, ontoResourceInfo.getNamespace());
			resourceRecord.put(OntologyRepositorySchemaParameter.RESOURCE_NAME, ontoResourceInfo.getLocalName());
			resourceRecordList.add(resourceRecord);
		}
		return resourceRecordList;
	}
	
	/**
	 * Load all the data from the Manufacturing Lexicon Repository
	 */
	public boolean loadRepository() { 
		System.out.println("load ontology from local files ...");
		/*
		 * NOTE: the sequence of the records loading matters
		 */
		boolean succeed1 = loadRecords(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_PROPERTY_RECORDS_FILENAME, FileType.OntoProperty);
		boolean succeed2 = loadRecords(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_CLASS_RECORDS_FILENAME, FileType.OntoClass);
		boolean succeed3 = loadRecords(ONTOLOGY_REPOSITORY_DIRECTORY_FULL_PATH + ONTOLOGY_CODED_CLASS_RECORDS_FILENAME, FileType.GlobalCode);
		
		return succeed1 && succeed2 && succeed3;
	}

	private boolean loadRecords(String fileFullName, FileType fileType) {
		System.out.println("load " + fileType.toString() + "...");
		
		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			if (fileType == FileType.GlobalCode) {
				String line;
				reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					this.loadGlobalCode2OntoClassRecord(line);
				}
			} else if (fileType == FileType.OntoClass) {
				String line;
				reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					this.loadOntoClassRecord(line);
				}
				
			} else if (fileType == FileType.OntoProperty) {
				String line;
				reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					this.loadOntoPropertyRecord(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private void loadOntoPropertyRecord(String line) {
				
		JSONObject recordMap = (JSONObject) JSONValue.parse(line);	
		String propertyURI = (String) recordMap.get(OntologyRepositorySchemaParameter.PROPERTY_URI);
		String propertyNS = (String) recordMap.get(OntologyRepositorySchemaParameter.PROPERTY_NAMESPACE);
		String propertyName = (String) recordMap.get(OntologyRepositorySchemaParameter.PROPERTY_NAME);
		String propertyType = (String) recordMap.get(OntologyRepositorySchemaParameter.PROPERTY_TYPE);
		boolean isGlobalProperty = (Boolean) recordMap.get(OntologyRepositorySchemaParameter.IS_GLOBAL_PROPERTY);
		OntPropertyInfo propertyInfo = new OntPropertyInfo(propertyURI, propertyNS, propertyName);
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> domainResourceList = (List<Map<String, String>>) recordMap.get(OntologyRepositorySchemaParameter.DOMAIN_RESOURCE_LIST);
		this.addResourceList(propertyInfo, domainResourceList, 0);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> rangeResourceList = (List<Map<String, String>>) recordMap.get(OntologyRepositorySchemaParameter.RANGE_RESOURCE_LIST);
		this.addResourceList(propertyInfo, rangeResourceList, 1);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> subejctResourceList = (List<Map<String, String>>) recordMap.get(OntologyRepositorySchemaParameter.SUBJECT_RESOURCE_LIST);
		this.addResourceList(propertyInfo, subejctResourceList, 2);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> objectResourceList = (List<Map<String, String>>) recordMap.get(OntologyRepositorySchemaParameter.OBJECT_RESOURCE_LIST);
		this.addResourceList(propertyInfo, objectResourceList, 3);

		if (OntPropertyType.ObjectProperty.toString().equals(propertyType)) {
			propertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
		} else if (OntPropertyType.DataTypeProperty.toString().equals(propertyType)) {
			propertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
		}
		
		if (isGlobalProperty) {
			this.globalPropertyCollection.add(propertyInfo);
		} 
		this.propertyURI2OntoPropertyObjectMap.put(propertyURI, propertyInfo);
	}
	
	private void addResourceList(OntPropertyInfo propertyInfo, List<Map<String, String>> resourceList, int flag){
		
		for (Map<String, String> resourceRecord : resourceList) {
			String resourceURI = resourceRecord.get(OntologyRepositorySchemaParameter.RESOURCE_URI);
			String resourceNS = resourceRecord.get(OntologyRepositorySchemaParameter.RESOURCE_NAMESPACE);
			String resourceName = resourceRecord.get(OntologyRepositorySchemaParameter.RESOURCE_NAME);
			OntResourceInfo resourceInfo = new OntResourceInfo(resourceURI, resourceNS, resourceName);
			switch (flag) {
			case 0:
				propertyInfo.addDomain(resourceInfo);
				break;
			case 1:
				propertyInfo.addRanges(resourceInfo);
				break;
			case 2:
				propertyInfo.addSubjectCandidate(resourceInfo);
				break;
			case 3:
				propertyInfo.addObjectCandidate(resourceInfo);
			}
		}
	}

	private void loadOntoClassRecord(String line) {
		
		JSONObject recordMap = (JSONObject) JSONValue.parse(line);
		String classURI = (String) recordMap.get(OntologyRepositorySchemaParameter.CLASS_URI);
		String classNS = (String) recordMap.get(OntologyRepositorySchemaParameter.CLASS_NAMESPACE);
		String className = (String) recordMap.get(OntologyRepositorySchemaParameter.CLASS_NAME);
		String localCode = (String) recordMap.get(OntologyRepositorySchemaParameter.LOCAL_PATH_CODE);
		long classHierarchyNumber = (Long) recordMap.get(OntologyRepositorySchemaParameter.CLASS_HIERARCHY_NUMBER);
		boolean isTopLevel = (Boolean) recordMap.get(OntologyRepositorySchemaParameter.IS_TOP_LEVEL_CLASS);
		
		OntoClassInfo ontoClassInfo = new OntoClassInfo(classURI, classNS, className);
		@SuppressWarnings("unchecked")
		List<String> ontoPropertyUriList = (List<String>) recordMap.get(OntologyRepositorySchemaParameter.DECLARED_PROPERTY_LIST);
		for (String propertyURI : ontoPropertyUriList) {
			// TODO:
			
			OntPropertyInfo ontPropertyInfo = propertyURI2OntoPropertyObjectMap.get(propertyURI);
			Collection<OntPropertyInfo> declaredProperties = ontoClass2DeclaredOntPropertiesMap.get(ontoClassInfo);
			if(declaredProperties == null){
				declaredProperties = new HashSet<OntPropertyInfo>();
				ontoClass2DeclaredOntPropertiesMap.put(ontoClassInfo, declaredProperties);
			}
			declaredProperties.add(ontPropertyInfo);
			
			ontoClassInfo.addProperty(ontPropertyInfo);
		}

		if (isTopLevel) {
			topLevelOntoClassCollection.add(ontoClassInfo);
			classHierarchyNumber2TopOntoClass.put(String.valueOf(classHierarchyNumber), ontoClassInfo);
		}
		this.groupOntClassesInSameClassHierarchy(ontoClassInfo,String.valueOf(classHierarchyNumber));
		this.classURI2OntoClassObjectMap.put(classURI, ontoClassInfo);
		this.className2OntoClassObjectMap.put(className, ontoClassInfo);
		this.ontoClass2HierarchyNumberMap.put(ontoClassInfo, (int) classHierarchyNumber);
		this.ontoClass2LocalPathCodeMap.put(ontoClassInfo, localCode);

	}

	private void loadGlobalCode2OntoClassRecord(String line) {
		@SuppressWarnings("unchecked")
		Map<String, String> recordMap = (JSONObject) JSONValue.parse(line);		
		String globalCode = recordMap.get(OntologyRepositorySchemaParameter.GLOBAL_PATH_CODE);
		String classURI = recordMap.get(OntologyRepositorySchemaParameter.CLASS_URI);
		globalPathCode2OntClassMap.put(globalCode, this.classURI2OntoClassObjectMap.get(classURI));
	}
	
	/*
	 * 
	 */
	
	/**
	 * for test and dubug
	 */
	@Override
	public void printOntologyInfo(){
		this.printTopLevelOntoClass();
		this.printAllOntoClasses();
		this.printClassHierarchyInfoOfOntoClasses();
		this.printGlobalPathCodeOfOntoClasses();
		this.printClassHierarchies();
		this.printGlobalOntoProperties();
		this.printAllOntoProperties();
	}
	
	
	/**
	 * for test and debug 
	 */
	private void printTopLevelOntoClass(){
		System.out.println("=================== Top Level Onto-Class ===================");
		for(OntoClassInfo ontoClassInfo : this.topLevelOntoClassCollection){
			System.out.println(ontoClassInfo.getOntClassName());
		}
	}
	
	/**
	 * 
	 */
	private void printAllOntoClasses() {
		System.out.println("=================== ALL Onto-Class =========================");
		for (String classURI : this.classURI2OntoClassObjectMap.keySet()) {
			OntoClassInfo ontoClassInfo = classURI2OntoClassObjectMap.get(classURI);
			System.out.println(ontoClassInfo.getOntClassName());
			for (OntPropertyInfo ontPropertyInfo : ontoClassInfo.getProperties()) {
				System.out.println("    " + ontPropertyInfo.getLocalName());
			}
		}
	}

	/**
	 * for test and debug 
	 */
	private void printClassHierarchyInfoOfOntoClasses(){
		System.out.println("=================== Hierarchy Code of Onto-Class ===================");
		for(OntoClassInfo ontClassInfo : ontoClass2HierarchyNumberMap.keySet()){
			int classHierarchyNumber = ontoClass2HierarchyNumberMap.get(ontClassInfo);
			String localPathCode = ontoClass2LocalPathCodeMap.get(ontClassInfo);
			System.out.println("<" + ontClassInfo.getOntClassName() + "> (" + classHierarchyNumber + ") (" + localPathCode + ")");
		}
	}
	
	/**
	 * for test and debug 
	 */
	private void printGlobalPathCodeOfOntoClasses() {
		System.out.println("=================== Global Code of Onto-Class =====================");
		for (String globalCode : this.globalPathCode2OntClassMap.keySet()) {
			System.out.println("(" + globalCode + ") <" + globalPathCode2OntClassMap.get(globalCode).getOntClassName() + ">");
		}
	}

	/**
	 * for test and debug 
	 */
	private void printClassHierarchies() {
		System.out.println("=================== Hierarchy Group of Onto-Class ===================");
		for (String classHierarchyNumber : this.classHierarchyNumber2OntoClassSet.keySet()) {
			System.out.println("Class Hierarchy Number: " + classHierarchyNumber);
			for (OntoClassInfo ontClassInfo : classHierarchyNumber2OntoClassSet.get(classHierarchyNumber)) {
				System.out.println("                         " + ontClassInfo.getOntClassName());
			}
		}
	}
	
	private void printGlobalOntoProperties(){
		System.out.println("=================== Global Onto-Properties ===================");
		for(OntPropertyInfo propertyInfo : this.globalPropertyCollection){
			System.out.println(propertyInfo.getLocalName());
		}
	} 
	
	private void printAllOntoProperties() {
		System.out.println("=================== ALL Onto-Properties ======================");
		for (String propertyURI : propertyURI2OntoPropertyObjectMap.keySet()) {
			OntPropertyInfo ontPropertyInfo = propertyURI2OntoPropertyObjectMap.get(propertyURI);
			System.out.println(ontPropertyInfo.getLocalName());
			this.printResourceInfo(ontPropertyInfo.getObjectCandidates(), "Object");
			this.printResourceInfo(ontPropertyInfo.getSubjectCandidates(), "Subject");
			this.printResourceInfo(ontPropertyInfo.getAllDomainClasses(), "Domain");
			this.printResourceInfo(ontPropertyInfo.getAllRangeClasses(), "Range");
		}
	}

	private void printResourceInfo(Collection<OntResourceInfo> ontResourceInfoList, String type){
		for(OntResourceInfo ontResourceInfo : ontResourceInfoList){
			System.out.println("      " + type +": "+ ontResourceInfo.getLocalName());
		}
	}

}
