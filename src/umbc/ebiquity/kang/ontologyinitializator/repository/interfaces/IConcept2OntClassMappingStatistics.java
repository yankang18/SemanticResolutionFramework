package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

public interface IConcept2OntClassMappingStatistics {

	public int getUndeterminedCounts();

	public int getSucceedCounts();

	public int getFailedCounts();

	public double getSimilarity();

	public int getVerificationAttempts();

}
