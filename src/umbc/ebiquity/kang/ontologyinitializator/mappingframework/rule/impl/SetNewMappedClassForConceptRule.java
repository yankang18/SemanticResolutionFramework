package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class SetNewMappedClassForConceptRule extends AbstractConcept2ClassMappingRule {

	public SetNewMappedClassForConceptRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRecordsAccessor MLRepository) {
		super(ontologyRepository, MLRepository);
	}
	
	@Override 
	public boolean isMatch() {
		return this.getVerifiedConcept2ClassMapping().isMappedConcept();
	}

	@Override
	public void run() {
		String newMappedClassName = this.getVerifiedConcept2ClassMapping().getMappedOntoClassName();
		OntoClassInfo ontoClass = this.getOntologyRepository().getLightWeightOntClassByName(newMappedClassName);
		if (ontoClass != null) {
			OntoClassInfo newMappedClass = new OntoClassInfo(ontoClass.getURI(), ontoClass.getNameSpace(), newMappedClassName);
			this.getUnVerifiedConcept2ClassMapping().setMappedOntoClass(newMappedClass, this.getVerifiedConcept2ClassMapping().getRelation());
		}
	}
}
