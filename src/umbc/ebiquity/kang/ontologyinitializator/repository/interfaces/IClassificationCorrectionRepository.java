package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;

public interface IClassificationCorrectionRepository {

	Collection<IClassificationCorrection> getClassificationCorrections();
	
	boolean saveRepository();

	boolean loadRepository();

	void showRepositoryDetail();

	Collection<IClassificationCorrection> extractCorrection(IUpdatedInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance); 
	
	int getAllConcept2OntClassMappingCount(); 

//	void addPositiveConcept2OntClassMapping(Set<IConcept2OntClassMapping> mappingSet,  String sourceClassName, String targetClassName);

//	void addConcept2OntClassMapping(Set<IConcept2OntClassMapping> mappingSet, String sourceClassName, String targetClassName);

	Collection<IInstanceMembershipInfereceFact> getAllInstanceMembershipInferenceFacts();

	Collection<IInstanceMembershipInfereceFact> getHiddenInstanceMembershipInferenceFacts();

	Collection<IInstanceMembershipInfereceFact> getExplicitInstanceMembershipInferenceFacts();      
}
