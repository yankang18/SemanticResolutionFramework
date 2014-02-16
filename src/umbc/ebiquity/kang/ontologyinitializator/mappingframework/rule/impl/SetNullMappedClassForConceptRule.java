package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class SetNullMappedClassForConceptRule extends AbstractConcept2ClassMappingRule {

	public SetNullMappedClassForConceptRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRepository MLRepository) {
		super(ontologyRepository, MLRepository);
	}

	@Override
	public boolean isMatch() {
		return !this.getVerifiedConcept2ClassMapping().isMappedConcept();
	}

	@Override
	public void run() {
		this.getUnVerifiedConcept2ClassMapping().setMappedOntoClass(null, null);
	}

}
