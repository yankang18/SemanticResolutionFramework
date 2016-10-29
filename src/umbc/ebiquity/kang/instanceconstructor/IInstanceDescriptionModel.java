package umbc.ebiquity.kang.instanceconstructor;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.impl.InstanceTripleSet;
import umbc.ebiquity.kang.instanceconstructor.impl.Triple;

public interface IInstanceDescriptionModel {
	
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

	Set<Triple> getRelationTypeTriple();

	URL getSourceURL(); 

}
