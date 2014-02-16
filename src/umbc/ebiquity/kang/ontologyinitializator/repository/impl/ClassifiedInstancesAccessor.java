package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.Debugger;

public class ClassifiedInstancesAccessor implements IClassifiedInstancesAccessor {

	private Map<String, List<String>> _classLabel2InstanceLabelsMap;
	private List<String> _allInstances;
	private IOntologyRepository _ontologyRepository;
	
	public ClassifiedInstancesAccessor(IOntologyRepository ontologyRepository){
		_ontologyRepository = ontologyRepository;
		this.init();
		this.populate();
	}
	
	private void init(){
		_classLabel2InstanceLabelsMap = new HashMap<String, List<String>>();
		_allInstances = new ArrayList<String>();
	}
	
	
	@Override
	public List<String> getInstances(){
		return _allInstances;
	}
	
	@Override
	public Collection<OntoClassInfo> getAllClasses(){
		return _ontologyRepository.getAllOntClasses();
	}
	
	@Override
	public List<String> getInstancesOfOntClass(OntoClassInfo ontClass){
		return this.getInstancesOfOntClass(ontClass.getOntClassName());
	}

	@Override
	public List<String> getInstancesOfOntClass(String className) {
		List<String> instances = new ArrayList<String>();
		if (_classLabel2InstanceLabelsMap.containsKey(className)) {
			instances.addAll(_classLabel2InstanceLabelsMap.get(className));
		}
		for (String namesOfSubClass : _ontologyRepository.getDownwardCotopy(className)) {
			if (_classLabel2InstanceLabelsMap.containsKey(namesOfSubClass)) {
				instances.addAll(_classLabel2InstanceLabelsMap.get(namesOfSubClass));
			}
		}
		return instances;
	}
	
	private void populate(){
		loadRepository();
	}
	
	@Override
	public boolean loadRepository() {
		
		String basicInfoDirFullPath = RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath();
		Debugger.print("Load Dir: %s ", basicInfoDirFullPath);
		File dir = new File(basicInfoDirFullPath);
		for(File file :dir.listFiles()){
			if(!file.isHidden() && file.isFile()){
				Debugger.print("Load File: %s ", file.getName());
				this.loadRecords(file);
			}
		}
		return false;
	}

	private boolean loadRecords(File file) {

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
		
		@SuppressWarnings("unchecked")
		Map<String, String> recordMap = (JSONObject) JSONValue.parse(line);
		String recordType = recordMap.get(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE);
		if (recordType.equals(MappingInfoSchemaParameter.CLASSIFICATION)) {
			
			String instanceName = recordMap.get(MappingInfoSchemaParameter.INSTANCE_NAME);
			String className = recordMap.get(MappingInfoSchemaParameter.CLASS_NAME).trim();
//			String classNS = recordMap.get(MappingInfoSchemaParameter.CLASS_NAMESPACE);
//			String classURI = recordMap.get(MappingInfoSchemaParameter.CLASS_URI);
//			String similarity = recordMap.get(MappingInfoSchemaParameter.SIMILARITY);
			
			List<String> instances = this._classLabel2InstanceLabelsMap.get(className);
			if(instances == null){
				instances = new ArrayList<String>();
			}
			instances.add(instanceName);
			_allInstances.add(instanceName);
			this._classLabel2InstanceLabelsMap.put(className, instances);
		} 
	}
	

	@Override
	public void showRepositoryDetail() {
		int instance_count = 0;
		for(String className : _classLabel2InstanceLabelsMap.keySet()){
			System.out.println("### " + className);
			for(String instanceName : _classLabel2InstanceLabelsMap.get(className)){
				System.out.println("        " + instanceName);
				instance_count++;
			}
		}	
		System.out.println();
		System.out.println("Number of Instance: " + instance_count);
	}
	
}
