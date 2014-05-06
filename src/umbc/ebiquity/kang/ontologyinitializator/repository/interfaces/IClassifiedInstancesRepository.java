package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.MappingBasicInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingDetailInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;

public interface IClassifiedInstancesRepository {
	
    public boolean saveRepository();
	
	public MappingBasicInfo getMappingBasicInfo();
	
	public MappingDetailInfo getMappingDetailInfo(); 

	public IClassifiedInstanceBasicRecord getClassifiedInstanceBasicRecordByInstanceName(String instanceName);
	
	public IClassifiedInstanceDetailRecord getClassifiedInstanceDetailRecordByInstanceName(String instanceName);
	
	public void showRepositoryDetail();

	public String getRepositoryName();

	public void updateInstance(IInstanceRecord updatedInstance);

	public Collection<IClassifiedInstanceDetailRecord> getAllClassifiedInstanceDetailRecords();

	void updateInstanceClass(String instanceLabel, String classLabel);
	
	public Set<String> getInstanceSet();

	Set<String> getRelationSet();

	MatchedOntProperty getMatchedOntProperty(String relationLabel);  
	
}
