package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

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

public class AggregratedClassifiedInstanceRepository extends AbstractRepositoryBatchLoader implements IClassifiedInstancesAccessor {

	private Map<String, List<String>> _classLabel2InstanceLabelsMap;
	private List<String> _allInstances;
	private IOntologyRepository _ontologyRepository;
	
	public AggregratedClassifiedInstanceRepository(IOntologyRepository ontologyRepository){
		super(RepositoryParameterConfiguration.getMappingBasicInfoDirectoryFullPath());
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
	protected void loadRecord(String line) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> recordMap = (JSONObject) JSONValue.parse(line);
		String recordType = recordMap.get(MappingInfoSchemaParameter.BASIC_MAPPING_INFO_RECORD_TYPE);
		if (recordType.equals(MappingInfoSchemaParameter.CLASSIFICATION)) {
			
			String instanceName = recordMap.get(MappingInfoSchemaParameter.INSTANCE_NAME);
			String className = recordMap.get(MappingInfoSchemaParameter.CLASS_NAME).trim();
			
//			for (String subClassName : _ontologyRepository.getUpwardCotopy(className)) {
//				List<String> instances = this._classLabel2InstanceLabelsMap.get(subClassName);
//				if(instances == null){
//					instances = new ArrayList<String>();
//				}
//				instances.add(instanceName);
//				this._classLabel2InstanceLabelsMap.put(className, instances);
//			}
			
			List<String> instances = this._classLabel2InstanceLabelsMap.get(className);
			if(instances == null){
				instances = new ArrayList<String>();
			}
			instances.add(instanceName);
			this._classLabel2InstanceLabelsMap.put(className, instances);
			_allInstances.add(instanceName);
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
