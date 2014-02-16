package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule;

import java.util.ArrayList;
import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.Concept2ClassMappingBoostingRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.Concept2ClassMappingNeutralRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.Concept2ClassMappingPenalizeRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.RestrictedConcept2ClassMappingPenalizeRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.SetNewMappedClassForConceptRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl.SetNullMappedClassForConceptRule;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IConcept2ClassMappingRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMappingPairSet;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class RuleEngine {

	private Collection<IConcept2ClassMappingRule> _concept2ClassMappingUpdateRuleSet;
	

	public void applyConcept2ClassMappingUpdateRules(String instanceClassName,
			                                         IConcept2OntClassMappingPairSet newConceptSet, 
			                                         IConcept2OntClassMappingPairSet oldConceptSet,
			                                         IOntologyRepository ontologyRepository,
			                                         IManufacturingLexicalMappingRepository MLRepository) {

		this.loadConcept2ClassMappingUpdateRules(ontologyRepository, MLRepository);
		for (IConcept2OntClassMapping newConcept2OntClassMapingPair : newConceptSet.getConcept2OntClassMappingPairs()) {
			
//			System.out.println(newConcept2OntClassMapingPair.getMappingCode());
//			System.out.println(newConcept2OntClassMapingPair.isDirectMapping());
//			System.out.println(newConcept2OntClassMapingPair.isManualMapping());
			
			if (newConcept2OntClassMapingPair.isDirectMapping()
					|| (!newConcept2OntClassMapingPair.isDirectMapping() && newConcept2OntClassMapingPair.isManualMapping())) {

				String conceptName = newConcept2OntClassMapingPair.getConceptName();
				IConcept2OntClassMapping oldConcept2OntClassMappingPair = oldConceptSet.getConcept2OntClassMappingPairByConceptName(conceptName);
				if (oldConcept2OntClassMappingPair != null) {
					for (IConcept2ClassMappingRule rule : _concept2ClassMappingUpdateRuleSet) {
						rule.prepare(instanceClassName, newConcept2OntClassMapingPair, oldConcept2OntClassMappingPair);
						if (rule.isMatch()) {
							rule.run();
						}
					}
				}
			}
		}
	}
	
	public void addConcept2ClassMappingUpdateRules(IConcept2ClassMappingRule rule){
		_concept2ClassMappingUpdateRuleSet.add(rule);
	}

	
	private void loadConcept2ClassMappingUpdateRules(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRepository MLRepository) {
		_concept2ClassMappingUpdateRuleSet = new ArrayList<IConcept2ClassMappingRule>();
		_concept2ClassMappingUpdateRuleSet.add(new Concept2ClassMappingBoostingRule(ontologyRepository, MLRepository));
		_concept2ClassMappingUpdateRuleSet.add(new Concept2ClassMappingPenalizeRule(ontologyRepository, MLRepository));
		_concept2ClassMappingUpdateRuleSet.add(new Concept2ClassMappingNeutralRule(ontologyRepository, MLRepository));
//		_concept2ClassMappingUpdateRuleSet.add(new SetNewMappedClassForConceptRule(ontologyRepository, MLRepository));
//		_concept2ClassMappingUpdateRuleSet.add(new SetNullMappedClassForConceptRule(ontologyRepository, MLRepository));
	}
}
