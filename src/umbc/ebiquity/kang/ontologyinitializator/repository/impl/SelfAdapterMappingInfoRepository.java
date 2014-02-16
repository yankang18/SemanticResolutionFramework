package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingBasicInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingDetailInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMappingInfoRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IUpdatedInstanceRecord;

public class SelfAdapterMappingInfoRepository implements IMappingInfoRepository {
	
	private IMappingInfoRepository _mappingInfoRepository;
	private IOntologyRepository _ontologyRepository;
	public SelfAdapterMappingInfoRepository(IMappingInfoRepository originalMappingInfoReopsitory, IOntologyRepository ontologyRepository){
		_mappingInfoRepository = originalMappingInfoReopsitory;
		_ontologyRepository = ontologyRepository;
	}

	@Override
	public boolean saveRepository() {
		return _mappingInfoRepository.saveRepository();
	}

	@Override
	public void updateMappingInfo(Collection<IUpdatedInstanceRecord> instances) {
		for (IUpdatedInstanceRecord instance : instances) {
			for (IConcept2OntClassMapping mapping : instance.getConcept2OntClassMappingPairs()) {
				if (!mapping.isMappedConcept()) {
					String updatedClassName = instance.getUpdatedClassName();
					OntoClassInfo updatedOntClass = _ontologyRepository.getLightWeightOntClassByName(updatedClassName);
					OntoClassInfo topLevelClass = _ontologyRepository.getTopLevelClass(updatedOntClass);
					MappingRelationType relation = MappingRelationType.relatedTo;
					mapping.setMappedOntoClass(topLevelClass, relation, 0.70);
				}
			}
		}
		_mappingInfoRepository.updateMappingInfo(instances);
	}

	@Override
	public MappingBasicInfo getMappingBasicInfo() {
		return _mappingInfoRepository.getMappingBasicInfo();
	}

	@Override
	public MappingDetailInfo getMappingDetailInfo() {
		return _mappingInfoRepository.getMappingDetailInfo();
	}

	@Override
	public IUpdatedInstanceRecord createInstanceClassificationRecord() {
		return _mappingInfoRepository.createInstanceClassificationRecord();
	}

	@Override
	public IClassifiedInstanceBasicRecord getClassifiedInstanceBasicInfoByInstanceName(String instanceName) {
		return _mappingInfoRepository.getClassifiedInstanceBasicInfoByInstanceName(instanceName);
	}
 
	@Override
	public IClassifiedInstanceDetailRecord getClassifiedInstanceDetailInfoByInstanceName(String instanceName) {
		return _mappingInfoRepository.getClassifiedInstanceDetailInfoByInstanceName(instanceName);
	}

	@Override
	public void showRepositoryDetail() {
		_mappingInfoRepository.showRepositoryDetail();
	}

	@Override
	public String getRepositoryName() {
		return _mappingInfoRepository.getRepositoryName();
	}

}
