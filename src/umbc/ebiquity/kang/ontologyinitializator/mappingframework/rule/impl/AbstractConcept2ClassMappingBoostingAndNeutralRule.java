package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.entityframework.object.Concept;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsUpdater.MappingVericationResult;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public abstract class AbstractConcept2ClassMappingBoostingAndNeutralRule extends AbstractConcept2ClassMappingRule  {

	protected AbstractConcept2ClassMappingBoostingAndNeutralRule(IOntologyRepository ontologyRepository,
			IManufacturingLexicalMappingRecordsAccessor MLRepository) {
		super(ontologyRepository, MLRepository);
	}
	
	protected void run(MappingVericationResult vericationResult) {
		Concept concept = this.getVerifiedConcept2ClassMapping().getConcept();
		MappingRelationType relation = this.getUnVerifiedConcept2ClassMapping().getRelation();
		String newMappedClassName = this.getVerifiedConcept2ClassMapping().getMappedOntoClassName();
		
		System.out.println("    " + vericationResult + " the mapping: <" + concept.getConceptName() + " " + relation.toString() + " " + newMappedClassName + ">");
		OntoClassInfo ontoClass = this.getOntologyRepository().getLightWeightOntClassByName(newMappedClassName);
		if (ontoClass != null) {
			OntoClassInfo newMappedClass = new OntoClassInfo(ontoClass.getURI(), ontoClass.getNameSpace(), newMappedClassName);
			if (this.getManfacturingLexiconRepository().hasConcept2OntoClassMapping(concept, ontoClass)) {
				// if the mapping has already existed in the Manufacturing
				// Lexicon Repository, just update the relation (currently the
				// relation in our application has no use)
				this.getManfacturingLexiconRepository().updateConcept2OntoClassMappingRelation(concept, newMappedClass, relation);
			} else {
				// if the mapping is new mapping, add this mapping.
				double sim = 0.75; // default similarity for new mapping
				this.getManfacturingLexiconRepository().addNewConcept2OntoClassMapping(concept, relation, newMappedClass, sim);
			}
			
			this.getManfacturingLexiconRepository().updateConcept2OntoClassMappingVerificationResult(concept, newMappedClass, vericationResult);
		}
	}

}
