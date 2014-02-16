package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;
import java.util.Map;
import umbc.ebiquity.kang.ontologyinitializator.ontology.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.ontology.Triple;

public interface ITripleRepository {
	
	public boolean saveRepository(String repositoryFullName);

	public boolean loadRepository(String repositoryFullName);

	public Collection<InstanceTripleSet> getInstanceTripleSets();
	
	public InstanceTripleSet getInstanceTripleSetByInstanceName(String instanceName);

	public Collection<Triple> getCustomRelationTriples();
	
	public Map<String, Collection<Triple>> getInstanceName2CustomRelationTripleMap();

	public Collection<Triple> getConceptRelationTriples();
	
	public Map<String, Collection<Triple>> getInstanceName2ConceptRelationTripleMap();
	
	public Collection<String> getObjectTermsOfRelation(String relation);
	
	public Collection<String> getSubjectTermsOfRelation(String relation);
	
	public Collection<String> getCustomRelations();
	
	public String getRepositoryName();

	void showTriples();

}
