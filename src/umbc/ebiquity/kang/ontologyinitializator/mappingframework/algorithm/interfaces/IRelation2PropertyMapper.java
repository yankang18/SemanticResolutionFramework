package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;

import java.util.Collection;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;

public interface IRelation2PropertyMapper {
	/**
	 * compute similarity between two collections of the properties
	 * 
	 * @param sPropertyCollection
	 * @param tPropertyCollection
	 * @return
	 */
	public MSMResult matchRelations2OntProperties(Collection<OntPropertyInfo> sPropertyNodeSet, Collection<OntPropertyInfo> tPropertyNodeSet);
}
