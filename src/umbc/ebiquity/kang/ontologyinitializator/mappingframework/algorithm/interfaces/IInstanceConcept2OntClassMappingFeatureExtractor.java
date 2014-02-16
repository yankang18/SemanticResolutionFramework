package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceMembershipInfereceFact;

public interface IInstanceConcept2OntClassMappingFeatureExtractor {

	void computeMappingStatistics();

	int getFrequencyOfConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet);

	int getFrequencyOfPositiveConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet);

	int getFrequencyOfNegativeConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet);

	double getRateOfPositiveConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet);
 
	double getRateOfNegativeConcept2OntClassMappingSet(IInstanceMembershipInfereceFact mappingSet);

	Map<IInstanceMembershipInfereceFact, Double> getPositiveConcept2OntClassMappingSetsWithRateOfOntClass(String className);

	Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithRateOfOntClass(String className);

	Map<IInstanceMembershipInfereceFact, Integer> getNegativeConcept2OntClassMappingSetsWithFrequencyOfCorrectionCluster(
			String correctionClusterName);

	Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithRateOfCorrectionCluster(String correctionClusterName);

	int getAllConcept2OntClassMappingCount();

	Map<IInstanceMembershipInfereceFact, Double> getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className);

	Map<IInstanceMembershipInfereceFact, Double> getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className);

	Collection<IInstanceMembershipInfereceFact> getPositiveConcept2OntClassMappingSetsOfOntClass(String className);

	Collection<IInstanceMembershipInfereceFact> getNegativeConcept2OntClassMappingSetsOfOntClass(String className);  

}
