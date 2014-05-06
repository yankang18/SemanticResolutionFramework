package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;

public interface IRelation2PropertyMappingAlgorithm {

	/***
	 * mapping relations from the Triple Repository to onto-properties defined in the Domain Ontology
	 * @return
	 */
	public void mapRelations2OntProperties();

	public Map<String, MatchedOntProperty> getRelation2PropertyMap();

	public Map<String, String> getInformativeRelation2PropertyMap();

}
