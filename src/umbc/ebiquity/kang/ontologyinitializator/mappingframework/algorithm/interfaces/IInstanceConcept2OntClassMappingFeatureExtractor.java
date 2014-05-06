package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceClassificationEvidence;

public interface IInstanceConcept2OntClassMappingFeatureExtractor {

	void computeMappingStatistics();

	int getFrequencyOfConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet);

	int getFrequencyOfPositiveConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet);

	int getFrequencyOfNegativeConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet);

	double getRateOfPositiveConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet);
 
	double getRateOfNegativeConcept2OntClassMappingSet(IInstanceClassificationEvidence mappingSet);

	Map<IInstanceClassificationEvidence, Double> getPositiveConcept2OntClassMappingSetsWithRateOfOntClass(String className);

	Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithRateOfOntClass(String className);

	Map<IInstanceClassificationEvidence, Integer> getNegativeConcept2OntClassMappingSetsWithFrequencyOfCorrectionCluster(
			String correctionClusterName);

	Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithRateOfCorrectionCluster(String correctionClusterName);

	int getAllConcept2OntClassMappingCount();

	Map<IInstanceClassificationEvidence, Double> getPositiveConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className);

	Map<IInstanceClassificationEvidence, Double> getNegativeConcept2OntClassMappingSetsWithLocalRateOfOntClass(String className);

	Collection<IInstanceClassificationEvidence> getPositiveConcept2OntClassMappingSetsOfOntClass(String className);

	Collection<IInstanceClassificationEvidence> getNegativeConcept2OntClassMappingSetsOfOntClass(String className);

	Map<IConcept2OntClassMapping, Double> getC2CMapping(String ontClassName);

	int getNumberOfC2CMapping(String ontClassName);  

}
