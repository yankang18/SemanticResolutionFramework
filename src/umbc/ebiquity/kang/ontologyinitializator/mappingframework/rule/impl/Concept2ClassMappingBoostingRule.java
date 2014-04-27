package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsUpdater.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class Concept2ClassMappingBoostingRule extends AbstractConcept2ClassMappingBoostingAndNeutralRule {

	public Concept2ClassMappingBoostingRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRecordsAccessor MLRepository) {
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
				isAligned = true;
			}
		}
		return isAligned;
	}

	@Override
	public void run() {
		System.out.println("Concept2ClassMappingBoostingRule applied");	
		this.run(MappingVericationResult.Succeed);
	}

}
