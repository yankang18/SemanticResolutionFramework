package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;

public interface IUpdatedInstanceRecord extends IConcept2OntClassMappingPairSet{
	
	public String getPrevenanceOfInstance();
	
	public String getOriginalInstanceName();

	public String getOriginalClassName();

	public String getUpdatedInstance();

	/***
	 * The UpdatedOntoClassName always points to the right class label of the
	 * instance. If the class label has been changed (by domain expert), the
	 * UpdatedOntoClassName is pointing to the new class label. Otherwise, the
	 * UpdatedOntoClassName is the same as the OrigOntoClassName.
	 * @return 
	 */
	public String getUpdatedClassName();
	
//	public OntoClassInfo getUpdatedOntClass();

	public boolean isUpdatedInstance();

	public void setPrevenanceOfInstance(String prevenanceOfInstance);
	
	public void setOriginalInstanceName(String originalInstaneName);

	public void setOriginalClassName(String originalClassName);

	public void setUpdatedInstanceName(String updatedInstanceName);

	public void setUpdatedClassName(String updatedClassName);
	
//	public void setUpdatedOntClass(OntoClassInfo updatedOntClass);

	public void isUpdatedInstance(boolean isUpdatedInstance);

	public Collection<IConcept2OntClassMapping> getConcept2OntClassMappingPairs();

	/***
	 * the onto-class for the instance has been changed means that the original
	 * classification for this instance was incorrect and it has been corrected
	 * by domain expert.
	 */
	boolean isOntClassChanged();

	void addConcept2OntClassMappingPair(Concept concept, MappingRelationType relation, OntoClassInfo ontClass, boolean isDirectMapping,
			boolean isManualMapping, double similarity);

	public boolean isLabelChanged();  
	
}
