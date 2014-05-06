package umbc.ebiquity.kang.ontologyinitializator.repository;

import java.util.Collection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMatchedOntProperty;

public class MappingDetailInfo {
	
	private Collection<IMatchedOntProperty> _mappedRelationInfoCollection;
	private Collection<IClassifiedInstanceDetailRecord> _classifiedInstanceDetailInfoCollection;

	public MappingDetailInfo(Collection<IMatchedOntProperty> mappedRelationInfoCollection,
			                Collection<IClassifiedInstanceDetailRecord> classifiedInstanceDetailInfoCollection){
		_mappedRelationInfoCollection = mappedRelationInfoCollection;
		_classifiedInstanceDetailInfoCollection = classifiedInstanceDetailInfoCollection;
	}
	
	public Collection<IClassifiedInstanceDetailRecord> getClassifiedInstanceDetailRecords(){
		return _classifiedInstanceDetailInfoCollection;
	}
	
	public Collection<IMatchedOntProperty> getMappedRelationInfoCollection(){
		return _mappedRelationInfoCollection;
	}
}
