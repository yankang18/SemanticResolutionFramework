package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsUpdater.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public abstract class AbstractConcept2ClassMappingPenalizeRule extends AbstractConcept2ClassMappingRule {
	 
	protected AbstractConcept2ClassMappingPenalizeRule(IOntologyRepository ontologyRepository,
			IManufacturingLexicalMappingRecordsAccessor MLRepository) {
		super(ontologyRepository, MLRepository);
	}

	@Override
	public void run() {
		System.out.println("Concept2ClassMappingPenalizeRule applied");
		this.getManfacturingLexiconRepository().updateConcept2OntoClassMappingRelation(this.getUnVerifiedConcept2ClassMapping().getConcept(), 
                                                                                       this.getUnVerifiedConcept2ClassMapping().getMappedOntoClass(), 
                                                                                       this.getUnVerifiedConcept2ClassMapping().getRelation());
		this.getManfacturingLexiconRepository().updateConcept2OntoClassMappingVerificationResult(this.getUnVerifiedConcept2ClassMapping().getConcept(), 
				                                                                                 this.getUnVerifiedConcept2ClassMapping().getMappedOntoClass(), 
				                                                                                 MappingVericationResult.Failed);
	}

}
