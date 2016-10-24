package umbc.ebiquity.kang.ontologyinitializator.repository.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;


public class DomainOntologyRepositoryTest {

	private static IOntologyRepository _repo;

	@BeforeClass
//	@Test
	public static void init() throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		_repo = OntologyRepositoryFactory.createOntologyRepository();
		
//		File dir = new File("/Users/yangkang/Desktop/");
//		if(dir.exists()){
//			System.out.println("here1");
//		} else{
//		 boolean success = dir.mkdirs();
//			System.out.println("here2 " + success);
//		}
		
	}

	@Test
//	@Ignore
	public void IsInTheSameClassHierarchyTest() {
		String firstClassName = "DeformationProcess";
		String secondClassName = "SolidificationProcess";
		boolean isInTheSameClassHierarchy = _repo.isInTheSameClassHierarchy(firstClassName, secondClassName);
		Assert.assertEquals(isInTheSameClassHierarchy, true);
		
		firstClassName = "ForgingService";
		secondClassName = "PackagingDesign";
		isInTheSameClassHierarchy = _repo.isInTheSameClassHierarchy(firstClassName, secondClassName);
		Assert.assertEquals(isInTheSameClassHierarchy, true);
		
		firstClassName = "Energy";
		secondClassName = "PackagingDesign";
		isInTheSameClassHierarchy = _repo.isInTheSameClassHierarchy(firstClassName, secondClassName);
		Assert.assertEquals(isInTheSameClassHierarchy, false);
		
		firstClassName = "Service";
		secondClassName = "ReverseEngineeringService";
		isInTheSameClassHierarchy = _repo.isInTheSameClassHierarchy(firstClassName, secondClassName);
		Assert.assertEquals(isInTheSameClassHierarchy, true);
		OntoClassInfo classInfo = _repo.getLightWeightOntClassByName("SheetMetalProcess");
		if(classInfo != null){
			System.out.println(classInfo.getOntClassName());
		} else {
			System.out.println("No sheet metal process");
		}
	}
	
	@Ignore
	@Test
	public void isSubClassOfTest() {
		
		String firstClassName = "ForgingService";
		String secondClassName = "ManufacturingService";
		boolean isSubClassOf = _repo.isSubClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, true);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "Joining";
		isSubClassOf = _repo.isSubClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, false);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "ManufacturingProcess";
		isSubClassOf = _repo.isSubClassOf(firstClassName, secondClassName, true);
		Assert.assertEquals(isSubClassOf, false);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "ManufacturingProcess";
		isSubClassOf = _repo.isSubClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, true);
	}
	
	@Ignore
	@Test
	public void isSuperClassOfTest() {
		
		String firstClassName = "ForgingService";
		String secondClassName = "ManufacturingService";
		boolean isSubClassOf = _repo.isSuperClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, false);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "Joining";
		isSubClassOf = _repo.isSuperClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, true);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "ManufacturingProcess";
		isSubClassOf = _repo.isSuperClassOf(firstClassName, secondClassName, true);
		Assert.assertEquals(isSubClassOf, false);
		
		firstClassName = "ManufacturingProcess";
		secondClassName = "ManufacturingProcess";
		isSubClassOf = _repo.isSuperClassOf(firstClassName, secondClassName, false);
		Assert.assertEquals(isSubClassOf, true);
	}

	@Ignore
    @Test
	public void ComputeHighestBranchedClassGlobalPathCodeTest() {
		String pathCode1 = "1-2-1-1";
		String pathCode2 = "1-2-2-2";
		String pathCode3 = "1-3-1-1";
		String pathCode4 = "1-4-2-2";
		Collection<String> pathCodeCollection = new HashSet<String>();
		pathCodeCollection.add(pathCode1);
		pathCodeCollection.add(pathCode2);
		pathCodeCollection.add(pathCode3);
		pathCodeCollection.add(pathCode4);
		String globalPathCode = _repo.computeReconcileOntClassGlobalPathCode(pathCodeCollection, 1);
		System.out.println("Global Path Code: " + globalPathCode);
		Assert.assertEquals(1-1, 1-1);
		
		pathCode1 = "1-1-1";
		pathCode2 = "1-1-2-1";
		pathCode3 = "1-1-2-2";
		pathCode4 = "1-1-3-2";
		Collection<String> pathCodeCollection2 = new HashSet<String>();
		pathCodeCollection2.add(pathCode1);
		pathCodeCollection2.add(pathCode2);
		pathCodeCollection2.add(pathCode3);
		pathCodeCollection2.add(pathCode4);
		String globalPathCode2 = _repo.computeReconcileOntClassGlobalPathCode(pathCodeCollection2, 1);
		System.out.println("Global Path Code: " + globalPathCode2);
		Assert.assertEquals(1-1-1, 1-1-1);
	}

	@Ignore
    @Test
	public void GetHighestBranchedOntClassTest() {
		String class1 = "ManufacturingProcess";
		String class2 = "Joining";
		String class3 = "AdditionProcess";
		String class4 = "LocalizedDeformation";
		
		OntoClassInfo OntClass1 = _repo.getLightWeightOntClassByName(class1);
		OntoClassInfo OntClass2 = _repo.getLightWeightOntClassByName(class2);
		OntoClassInfo OntClass3 = _repo.getLightWeightOntClassByName(class3);
		OntoClassInfo OntClass4 = _repo.getLightWeightOntClassByName(class4);
		Collection<OntoClassInfo> ontClassCollection = new HashSet<OntoClassInfo>();
		ontClassCollection.add(OntClass1);
		ontClassCollection.add(OntClass2);
		ontClassCollection.add(OntClass3);
		ontClassCollection.add(OntClass4);
		OntoClassInfo pivotClass = _repo.getReconcileOntClass(ontClassCollection);
		System.out.println("Pivot Class: " + pivotClass.getOntClassName());
		Assert.assertEquals(pivotClass.getOntClassName(), "ManufacturingProcess");

		class2 = "Joining";
		class3 = "AdditionProcess";
		class4 = "LocalizedDeformation";
		OntClass2 = _repo.getLightWeightOntClassByName(class2);
		OntClass3 = _repo.getLightWeightOntClassByName(class3);
		OntClass4 = _repo.getLightWeightOntClassByName(class4);
		Collection<OntoClassInfo> ontClassCollection2 = new HashSet<OntoClassInfo>();
		ontClassCollection2.add(OntClass2);
		ontClassCollection2.add(OntClass3);
		ontClassCollection2.add(OntClass4);
		OntoClassInfo pivotClass2 = _repo.getReconcileOntClass(ontClassCollection2);
		System.out.println("Pivot Class: " + pivotClass2.getOntClassName());
		Assert.assertEquals(pivotClass2.getOntClassName(), "ManufacturingProcess");
		
		class2 = "Service";
		class3 = "ManufacturingService";
		class4 = "WaterjetCuttingService";
		OntClass2 = _repo.getLightWeightOntClassByName(class2);
		OntClass3 = _repo.getLightWeightOntClassByName(class3);
		OntClass4 = _repo.getLightWeightOntClassByName(class4);
		Collection<OntoClassInfo> ontClassCollection3 = new HashSet<OntoClassInfo>();
		ontClassCollection3.add(OntClass2);
		ontClassCollection3.add(OntClass3);
		ontClassCollection3.add(OntClass4);
		OntoClassInfo pivotClass3 = _repo.getReconcileOntClass(ontClassCollection3);
		System.out.println("Pivot Class: " + pivotClass3.getOntClassName());
		Assert.assertEquals(pivotClass3.getOntClassName(), "WaterjetCuttingService");
	}

	@Ignore
    @Test
	public void GetSuperClassNamesInClassPath() {
    	String class1 = "Grinding";
    	
    	Collection<String> upwardCotopy = new HashSet<String>();
    	upwardCotopy.add("Grinding");
    	upwardCotopy.add("MechanicalMachining");
    	upwardCotopy.add("Machining");
    	upwardCotopy.add("SubtractionProcess");
    	upwardCotopy.add("ManufacturingProcess");
    	upwardCotopy.add("Process");
    	
    	
    	OntoClassInfo OntClass1 = _repo.getLightWeightOntClassByName(class1);
		Collection<String> superClasses = _repo.getUpwardCotopy(class1);
		for (String s : superClasses) {
			Assert.assertEquals(true, upwardCotopy.contains(s));
		}
		
		for (String s : upwardCotopy) {
			Assert.assertEquals(true, superClasses.contains(s));
		}
		
		Collection<OntoClassInfo> superClasses2 = _repo.getUpwardCotopy(OntClass1);
		Collection<String> superClasses3 = new HashSet<String>();
		for (OntoClassInfo s : superClasses2) {
			superClasses3.add(s.getOntClassName());
			Assert.assertEquals(true, upwardCotopy.contains(s.getOntClassName()));
		}
		
		superClasses.removeAll(superClasses3);
		Assert.assertEquals(0, superClasses.size());
	}
    
	@Ignore
    @Test
	public void GetNamesOfSubTributaryClasses() {
		
		String class1 = "HoleMaking";
		
		Collection<String> downwardCotopy = new HashSet<String>();
		downwardCotopy.add("CounterBoring");
		downwardCotopy.add("CounterSinking");
		downwardCotopy.add("Drilling");
		downwardCotopy.add("DeepHoleDrilling");
		downwardCotopy.add("Reaming");
		downwardCotopy.add("Tapping");
    	
		Collection<String> subTributaryClasses1 = _repo.getDownwardCotopy(class1);
//		print(subTributaryClasses1);
		for (String s : subTributaryClasses1) {
			Assert.assertEquals(true, downwardCotopy.contains(s));
		}
		for (String s : downwardCotopy) {
			Assert.assertEquals(true, subTributaryClasses1.contains(s));
		}
		Assert.assertEquals(false, subTributaryClasses1.contains(class1));
		
//		System.out.println();
//		String class2 = "ManufacturingService";
//		Collection<String> subTributaryClasses2 = _repo.getDownwardCotopy(class2);
//		print(subTributaryClasses2);
	}
    
	
	private void print(Collection<String> tokens){
		for(String token : tokens){
			System.out.println(token);
		}
	}

	@Ignore
	@Test
	public void IsCrossOverAtTest() {
		
		String class1 = "ManufacturingProcess";
		String class2 = "Joining";
		String class3 = "AdditionProcess";

		boolean isCrossOver = _repo.isCrossOverAt(class2, class3, class1);
		Assert.assertEquals(true, isCrossOver);

		class1 = "ManufacturingService";
		class2 = "AssemblyService";
		class3 = "BroachingService";
		isCrossOver = _repo.isCrossOverAt(class2, class3, class1);
		Assert.assertEquals(true, isCrossOver);
		
		class1 = "ManufacturingService";
		class2 = "MachiningService";
		class3 = "BroachingService";
		isCrossOver = _repo.isCrossOverAt(class2, class3, class1);
		Assert.assertEquals(false, isCrossOver);
		
		class1 = "ManufacturingService";
		class2 = "MachiningService";
		class3 = "ManufacturingService";
		isCrossOver = _repo.isCrossOverAt(class2, class3, class1);
		Assert.assertEquals(false, isCrossOver);
	}

	@Ignore
	@Test
	public void ExtractTributaryClasssetsTest() {
		String class0 = "ManufacturingProcess";
		
		String class1 = "ManufacturingProcess";
		String class2 = "Joining";
		String class3 = "AdditionProcess";
		String class4 = "LocalizedDeformation";
		Collection<String> ontClassCollection = new HashSet<String>();
		ontClassCollection.add(class1);
		ontClassCollection.add(class2);
		ontClassCollection.add(class3);
		ontClassCollection.add(class4);
		Collection<String> classsets = _repo.computeMaximalConvergenceSet(ontClassCollection, class0);
		Assert.assertEquals(true, classsets.contains(class1));
		Assert.assertEquals(true, classsets.contains(class2));
		Assert.assertEquals(true, classsets.contains(class3));
		Assert.assertEquals(true, classsets.contains(class4));
		
//		for(Set<String> classset : classsets){
//			System.out.println(classset);
//		}
		
		System.out.println();
		class0 = "ConsolidationProcess";
		
		class1 = "ManufacturingProcess";
		class2 = "Joining";
		class3 = "AdhesiveBonding";
		class4 = "RigidAdhesiveBonding";
		String class5 = "ConsolidationProcess";
		String class6 = "Process";
		Collection<String> ontClassCollection2 = new HashSet<String>();
		ontClassCollection2.add(class1);
		ontClassCollection2.add(class2);
		ontClassCollection2.add(class3);
		ontClassCollection2.add(class4);
		ontClassCollection2.add(class5);
		ontClassCollection2.add(class6);
		Collection<String> classsets2 = _repo.computeMaximalConvergenceSet(ontClassCollection2, class0);
		Assert.assertEquals(true, classsets2.contains((class5)));
		Assert.assertEquals(true, classsets2.contains((class6)));
		Assert.assertEquals(true, classsets2.contains((class1)));
		Assert.assertEquals(false, classsets2.contains((class2)));
		Assert.assertEquals(false, classsets2.contains((class3)));
		Assert.assertEquals(false, classsets2.contains((class4)));
		
	}

	private Set<String> createClassset(String... classes) {
		List<String> classset = new ArrayList<String>();
		for (String c : classes) {
			classset.add(c);
		}
		Collections.sort(classset);
		return new LinkedHashSet<String>(classset);
	}
	
}
