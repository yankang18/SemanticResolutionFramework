package umbc.ebiquity.kang.instanceconstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.entityframework.object.Concept;

public interface IInstanceTripleSet {

	public Collection<Concept> getConceptSet();

	public Collection<String> getCustomRelation();

	public Collection<String> getCustomRelationValue(String relation);

	public Collection<String> getTaxonomicRelationValue();

	public Map<String, Set<String>> getRelation2ValueMap();

	public Map<String, Set<String>> getTaxonomicRelation2ValueMap();

	public Map<String, Set<Concept>> getInstance2ConceptualSetMap();

	public String getSubjectLabel();

	public String getProcessedSubjectLabel();

	public boolean containsConcept();

}
