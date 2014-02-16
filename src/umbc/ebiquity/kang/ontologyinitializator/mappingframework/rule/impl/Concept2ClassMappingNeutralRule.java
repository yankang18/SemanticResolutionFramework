package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ManufacturingLexicalMappingRepository.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class Concept2ClassMappingNeutralRule extends AbstractConcept2ClassMappingBoostingAndNeutralRule {

	public Concept2ClassMappingNeutralRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRepository MLRepository) {
		super(ontologyRepository, MLRepository);
	}

	@Override
	public boolean isMatch() {
		boolean isAligned = false;
		boolean isMappedConceptCurrently = this.getVerifiedConcept2ClassMapping().isMappedConcept();
		if (isMappedConceptCurrently) {
			// if the concept is mapping to certain onto-class

			String instanceClassName = this.getInstanceClassName();
			String newMappedClassName = this.getVerifiedConcept2ClassMapping().getMappedOntoClassName();

			boolean inTheSameClassHierarchy = this.getOntologyRepository().isInTheSameClassHierarchy(instanceClassName, newMappedClassName);
			if (inTheSameClassHierarchy) {
				// if the mapped onto-class is NOT in the same class hierarchy
				// of the class that the instance belongs to, then this rule is
				// matched
				isAligned = false;
			} else {
				isAligned = true;
			}
		}
		return isAligned;
	}

	@Override
	public void run() {
		System.out.println("Concept2ClassMappingNeuralRule applied");
		this.run(MappingVericationResult.Unknow);
	}
}
