package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;

public interface IClassificationCorrectionRepository extends IClassificationCorrectionRecordRepository {

	boolean saveRepository();

	boolean loadRepository();

	void showRepositoryDetail();

	Collection<IClassificationCorrection> extractCorrection(IInstanceRecord updatedInstance, IClassifiedInstanceDetailRecord originalInstance); 
	
	Collection<IClassificationCorrection> getClassificationCorrections();
	
	int getAllConcept2OntClassMappingCount(); 

	Collection<IInstanceClassificationEvidence> getAllInstanceMembershipInferenceFacts();

	Collection<IInstanceClassificationEvidence> getHiddenInstanceMembershipInferenceFacts();

	Collection<IInstanceClassificationEvidence> getExplicitInstanceMembershipInferenceFacts();

//	Collection<IInstanceClassificationEvidence> getAllInstanceMembershipInferenceFacts(IConcept2OntClassMapping c2cMapping);

	Collection<String> getTargetClasses(IConcept2OntClassMapping c2cMapping);

	double getC2CMappingRateInOntClass(IConcept2OntClassMapping mapping, String ontClassName);

	void showMappingInfo();

	Map<IConcept2OntClassMapping, Double> getC2CMapping(String ontClassName);

	int getNumberOfC2CMappings(String ontClassName);         
}
