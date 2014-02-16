package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.interfaces.IConcept2ClassMappingRule;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

/**
 * 
 * @author kangyan2003
 *
 */
public abstract class AbstractConcept2ClassMappingRule implements IConcept2ClassMappingRule {

	
	private String _instanceClassName;
	private IConcept2OntClassMapping _verifiedMap;
	private IConcept2OntClassMapping _oldMap;
	private IOntologyRepository _ontologyRepository;
	private IManufacturingLexicalMappingRepository _MLRepository;

	protected AbstractConcept2ClassMappingRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRepository MLRepository) {
		this._ontologyRepository = ontologyRepository;
		this._MLRepository = MLRepository;
	}

	@Override
	public void prepare(String instanceClassName, IConcept2OntClassMapping verifiedMap, IConcept2OntClassMapping oldMap) {
		this._instanceClassName = instanceClassName;
		this._verifiedMap = verifiedMap;
		this._oldMap = oldMap;
	}

	protected String getInstanceClassName() {
		return this._instanceClassName;
	}
	
	protected IConcept2OntClassMapping getVerifiedConcept2ClassMapping(){
		return this._verifiedMap;
	}
	
	protected IConcept2OntClassMapping getUnVerifiedConcept2ClassMapping(){
		return this._oldMap;
	}
	
	protected IOntologyRepository getOntologyRepository(){
		return this._ontologyRepository;
	}
	
	protected IManufacturingLexicalMappingRepository getManfacturingLexiconRepository(){
		return this._MLRepository;
	}
}
