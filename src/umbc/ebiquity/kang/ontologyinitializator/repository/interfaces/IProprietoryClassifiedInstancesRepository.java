package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.MappingBasicInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingDetailInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;

public interface IProprietoryClassifiedInstancesRepository {
	
    public boolean saveRepository();
	
	public MappingBasicInfo getMappingBasicInfo();
	
	public MappingDetailInfo getMappingDetailInfo(); 

	public IClassifiedInstanceBasicRecord getClassifiedInstanceBasicRecordByInstanceName(String instanceName);
	
	public IClassifiedInstanceDetailRecord getClassifiedInstanceDetailRecordByInstanceName(String instanceName);
	
	public void showRepositoryDetail();

	public String getRepositoryName();

	public void updateInstance(IUpdatedInstanceRecord updatedInstance);

	public Collection<IClassifiedInstanceDetailRecord> getAllClassifiedInstanceDetailRecords();

	public Set<String> getInstanceSet();
 
	void updateInstanceClass(String instanceLabel, String classLabel); 
	
}
