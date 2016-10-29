package umbc.ebiquity.kang.ontologyinitializator.testdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.impl.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.MatchedOntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.UpdatedInstanceRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class FakeDataCreator {
	
	private static IOntologyRepository _ontologyRepository;
	
	public FakeDataCreator() throws IOException {
//		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
//		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
	}
	
	public Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords1() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Precision Machining";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("products", "Product");
		c2cMapping.put("ManufacturingService", "Service");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Abrasive Waterjet Cutting";
		originalClassName = "ManufacturingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("capabilities", "Process");
		c2cMapping.put("carbon steel shims", "CarbonSteel");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shim Sets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("shims", "NULL");
		c2cMapping.put("shim types", "NULL");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shims";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("military defense", "DefenseAndMilitary");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Spiral Wound Gaskets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("types gaskets", "NULL");
		c2cMapping.put("gaskets", "NULL");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		return XXX;
	}
	
	
	public Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords2() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Sheet Metal Fabrication";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("products", "Product");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Sheet Metal Fabrications";
		originalClassName = "SheetMetalService";
		updatedClassName = "Process";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("products", "Product");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Aerospace Waterjet Cutting";
		originalClassName = "WaterjetCuttingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("abrasive waterjet cutting", "WaterjetCuttingService");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "ShellMoldCasting&SandCasting";
		originalClassName = "ExpendableMold";
		updatedClassName = "Casting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("Casting", "Casting");
		c2cMapping.put("ShellModeCasting", "ShellMoldCasting");
		c2cMapping.put("SandCasting", "SandCasting");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Aerospace Waterjet Cutting";
		originalClassName = "Machining";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("WaterJetCutting", "WaterJetCutting");
		c2cMapping.put("MechanicalMachining", "MechanicalMachining");
		c2cMapping.put("Machining", "Machining");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		return XXX;
	}
	
	public Map<IInstanceRecord, IClassifiedInstanceDetailRecord> createUpdatedInstanceRecordsAndClassifiedInstanceRecords() {
		Map<IInstanceRecord, IClassifiedInstanceDetailRecord> XXX = new LinkedHashMap<IInstanceRecord, IClassifiedInstanceDetailRecord>();

		String instanceName = "Precision Machining";
		String originalClassName = "SheetMetalService";
		String updatedClassName = "Process";
		Map<String, String> c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("products", "Product");
		c2cMapping.put("ManufacturingService", "Service");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Abrasive Waterjet Cutting";
		originalClassName = "ManufacturingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("capabilities", "Process");
		c2cMapping.put("carbon steel shims", "CarbonSteel");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shim Sets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("shims", "NULL");
		c2cMapping.put("shim types", "NULL");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Shims";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("military defense", "DefenseAndMilitary");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Spiral Wound Gaskets";
		originalClassName = "SheetMetalService";
		updatedClassName = "Product";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("types gaskets", "NULL");
		c2cMapping.put("gaskets", "NULL");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Sheet Metal Fabrication";
		originalClassName = "SheetMetalService";
		updatedClassName = "Process";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("products", "Product");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Sheet Metal Fabrications";
		originalClassName = "SheetMetalService";
		updatedClassName = "Process";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("sheet metal fabrication", "SheetMetalService");
		c2cMapping.put("products", "Product");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Aerospace Waterjet Cutting";
		originalClassName = "WaterjetCuttingService";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("ManufacturingService", "Service");
		c2cMapping.put("abrasive waterjet cutting", "WaterjetCuttingService");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "ShellMoldCasting&SandCasting";
		originalClassName = "ExpendableMold";
		updatedClassName = "Casting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("Casting", "Casting");
		c2cMapping.put("ShellModeCasting", "ShellMoldCasting");
		c2cMapping.put("SandCasting", "SandCasting");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));

		instanceName = "Aerospace Waterjet Cutting";
		originalClassName = "Machining";
		updatedClassName = "WaterJetCutting";
		c2cMapping = new HashMap<String, String>();
		c2cMapping.put("WaterJetCutting", "WaterJetCutting");
		c2cMapping.put("MechanicalMachining", "MechanicalMachining");
		c2cMapping.put("Machining", "Machining");
		XXX.put(createUpdatedInstanceRecord(instanceName, instanceName, instanceName, originalClassName, updatedClassName, c2cMapping),
				createClassifiedInstanceRecord(instanceName, originalClassName, c2cMapping));
		return XXX;
	}

	public IInstanceRecord createUpdatedInstanceRecord(String prevenanceInstanceName, String originalInstanceName, String currentInstanceName, String originalClassName, String updatedClassName,
			Map<String, String> conept2classMappings) {

		IInstanceRecord updatedInstanceRecord = new UpdatedInstanceRecord();
		updatedInstanceRecord.setOriginalInstanceName(originalInstanceName);
		updatedInstanceRecord.setUpdatedInstanceName(currentInstanceName);
		updatedInstanceRecord.setPrevenanceOfInstance(prevenanceInstanceName);
		updatedInstanceRecord.setOriginalClassName(originalClassName);
		updatedInstanceRecord.setUpdatedClassName(updatedClassName);
		for (String conceptName : conept2classMappings.keySet()) {
			String className = conept2classMappings.get(conceptName);
			Concept concept = new Concept(conceptName);
			if (!"NULL".equals(className)) {
				updatedInstanceRecord.addConcept2OntClassMappingPair(concept, MappingRelationType.relatedTo,
						_ontologyRepository.getLightWeightOntClassByName(className), true, false, 0.7);
			} else {
				updatedInstanceRecord.addConcept2OntClassMappingPair(concept, null, null, true, false, 0.0);
			}
		}
		return updatedInstanceRecord;
	}

	public IClassifiedInstanceDetailRecord createClassifiedInstanceRecord(String instanceName, String originalClassName,
			Map<String, String> conept2classMappings) {

		InstanceTripleSet instance = new InstanceTripleSet(instanceName);
		MatchedOntoClassInfo matchedOntClassInfo = new MatchedOntoClassInfo();
		matchedOntClassInfo.setMatchedOntoClassInfo(createOntClass(originalClassName));
		matchedOntClassInfo.setSimilarity(0.7);

		ClassifiedInstanceDetailRecord classifiedInstanceDetailInfo = new ClassifiedInstanceDetailRecord(instance, matchedOntClassInfo);
		Collection<IConcept2OntClassMapping> mappings = new ArrayList<IConcept2OntClassMapping>();
		for (String conceptName : conept2classMappings.keySet()) {
			String className = conept2classMappings.get(conceptName);
				mappings.add(createConcept2OntClassMapping(conceptName, className , true));
		}
		classifiedInstanceDetailInfo.setConcept2OntClassMappingPairs(mappings);
		return classifiedInstanceDetailInfo;

	}

	public Concept2OntClassMapping createConcept2OntClassMapping(String conceptName, String className, boolean hittedMapping) {
		Concept concept = new Concept(conceptName);
		Concept2OntClassMapping c2c;
		if ("NULL".equals(className)) {
			c2c = new Concept2OntClassMapping(concept);
			c2c.setDirectMapping(true);
		} else {

			OntoClassInfo ontoClassInfo = createOntClass(className);
			c2c = new Concept2OntClassMapping(concept, ontoClassInfo, 0.7);
			c2c.setDirectMapping(true);
		}
		return c2c;
	}
	
	public OntoClassInfo createOntClass(String className) {
		String ontoClassName = className;
		String ontoClassNS = _ontologyRepository.getLightWeightOntClassByName(ontoClassName).getNameSpace();
		String ontoClassURI = _ontologyRepository.getLightWeightOntClassByName(ontoClassName).getURI();
		return new OntoClassInfo(ontoClassURI, ontoClassNS, ontoClassName);
	}
}
