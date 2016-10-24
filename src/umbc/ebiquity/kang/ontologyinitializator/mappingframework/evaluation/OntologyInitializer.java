package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.AbstractWebUrlLoader.PopulationType;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class OntologyInitializer {

	public static void main(String[] args) throws IOException {
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/home/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/home/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		IOntologyRepository _ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		
		String instancefileFullPath = "/home/yankang/Desktop/initialInstances.txt";
		String initfileFullPath = "/home/yankang/Desktop/_init";
		OntologyInitializer PCIRAC = new OntologyInitializer(_ontologyRepository);
		PCIRAC.loadInstances(instancefileFullPath);
		PCIRAC.showInstances();
		PCIRAC.saveRepository(initfileFullPath);
	}
	
	private StringBuilder _basicRecords;
	private IOntologyRepository _ontologyRepository;
	private Map<String, String> _instance2ClassMap;
	private Map<String, Integer> _instance2CountMap;

	
	public OntologyInitializer(IOntologyRepository ontologyRepository) {
		_ontologyRepository = ontologyRepository;
		_instance2ClassMap = new HashMap<String, String>();
		_instance2CountMap = new HashMap<String, Integer>();
	}
	
	public boolean loadInstances(String fileFullName){
		return this.loadRecords(fileFullName);
	}

	private boolean loadRecords(String fileFullName) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadRecord(line);
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
	
	private void loadRecord(String line) {
		if (this.isBlank(line))
			return;
		
		System.out.println(line);
		String[] tokens = line.split("=");
		
		
		String instanceName = tokens[0].trim().toLowerCase();
		String className = tokens[1].trim();
		int count = 1;
		if (tokens.length == 3) {
			count = Integer.valueOf(tokens[2].trim());
		}
		_instance2ClassMap.put(instanceName, className);
		_instance2CountMap.put(instanceName, count);
	}

	private void createJSONRecordOfBasicMappingInfo() {

		this._basicRecords = new StringBuilder();
		for (String instanceName : _instance2ClassMap.keySet()) {
			String className = _instance2ClassMap.get(instanceName);
			int count = _instance2CountMap.get(instanceName);
			OntoClassInfo ontClass = _ontologyRepository.getLightWeightOntClassByName(className);
			String classNS = ontClass.getNameSpace();
			String classURI = ontClass.getURI();
			String similarity = "8.0";
			for (int i = 0; i < count; i++) {
				Map<String, String> jsonBasicRecord = new LinkedHashMap<String, String>();
				jsonBasicRecord.put(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE, MappingInfoSchemaParameter.CLASSIFICATION);
				jsonBasicRecord.put(MappingInfoSchemaParameter.INSTANCE_NAME, instanceName);
				jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_NAME, className);
				jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_NAMESPACE, classNS);
				jsonBasicRecord.put(MappingInfoSchemaParameter.CLASS_URI, classURI);
				jsonBasicRecord.put(MappingInfoSchemaParameter.SIMILARITY, similarity);
				_basicRecords.append(JSONValue.toJSONString(jsonBasicRecord));
				_basicRecords.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
			}
		}

	}
	
	public boolean saveRepository(String fileFullName) {
		this.createJSONRecordOfBasicMappingInfo();
		if (this.saveBasicMappingInfo(fileFullName)) {
			return true;
		} else {
			// TODO: to do some clean up work
			return false;
		}
	}
	
	private boolean saveBasicMappingInfo(String fileFullName){
		return FileAccessor.saveTripleString(fileFullName, _basicRecords.toString());
	}

	private boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	public void showInstances(){
		
		StringBuilder records = new StringBuilder();
		for (String instanceName : _instance2ClassMap.keySet()) {
			String className = _instance2ClassMap.get(instanceName);
			int count = _instance2CountMap.get(instanceName);
//			System.out.println("@ " + className);
			OntoClassInfo ontClass = _ontologyRepository.getLightWeightOntClassByName(className);
			String classNS = ontClass.getNameSpace();
			String classURI = ontClass.getURI();
			String similarity = "8.0";
			for (int i = 0; i < count; i++) {
				records.append(instanceName + "   ");
				records.append(className + "   ");
				records.append(classNS + "   ");
				records.append(classURI + "   ");
				records.append(similarity + "   ");
				records.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
			}
		}
		
		System.out.println();
		System.out.println(records.toString());
	}
}
