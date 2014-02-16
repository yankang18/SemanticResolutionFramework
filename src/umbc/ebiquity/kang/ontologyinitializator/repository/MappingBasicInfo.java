package umbc.ebiquity.kang.ontologyinitializator.repository;

import java.util.Collection;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IMatchedOntProperty;

public class MappingBasicInfo {
	
	private Collection<IMatchedOntProperty> _mappedRelationInfoCollection;
	private Collection<IClassifiedInstanceBasicRecord> _classifiedInstanceBasicInfoCollection;
	
	public MappingBasicInfo(Collection<IMatchedOntProperty> mappedRelationInfoCollection,
			                Collection<IClassifiedInstanceBasicRecord> classifiedInstanceBasicInfoCollection){
		_mappedRelationInfoCollection = mappedRelationInfoCollection;
		_classifiedInstanceBasicInfoCollection = classifiedInstanceBasicInfoCollection;
	}
	
	public Collection<IClassifiedInstanceBasicRecord> getClassifiedInstanceBasicInfoCollection(){
		return _classifiedInstanceBasicInfoCollection;
	}
	
	public Collection<IMatchedOntProperty> getMappedRelationInfoCollection(){
		return _mappedRelationInfoCollection;
	}
}
