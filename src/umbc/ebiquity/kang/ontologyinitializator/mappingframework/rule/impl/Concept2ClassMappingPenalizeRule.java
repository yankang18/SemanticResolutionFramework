package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class Concept2ClassMappingPenalizeRule extends AbstractConcept2ClassMappingPenalizeRule {

	public Concept2ClassMappingPenalizeRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRepository MLRepository) {
		super(ontologyRepository, MLRepository);
	}

	@Override
	public boolean isMatch() {
		boolean isMatched = false;
		boolean isMappedConceptPreviously = this.getUnVerifiedConcept2ClassMapping().isMappedConcept();
		boolean isMappedConceptCurrently = this.getVerifiedConcept2ClassMapping().isMappedConcept();

//		System.out.println("isMappedConceptPreviously: " + isMappedConceptPreviously);
//		System.out.println("isMappedConceptCurrently: " + isMappedConceptCurrently);
		
		OntoClassInfo newMappedClass = this.getVerifiedConcept2ClassMapping().getMappedOntoClass();
		OntoClassInfo oldMappedClass = this.getUnVerifiedConcept2ClassMapping().getMappedOntoClass();
		String newMappedClassName = isMappedConceptCurrently ? newMappedClass.getOntClassName() : "";
		String oldMappedClassName = isMappedConceptPreviously ? oldMappedClass.getOntClassName() : "";
		boolean isMappedClassChanged = false;
		if (isMappedConceptPreviously && isMappedConceptCurrently) {
			newMappedClassName = newMappedClass.getOntClassName();
			oldMappedClassName = oldMappedClass.getOntClassName();
			isMappedClassChanged = !newMappedClassName.equals(oldMappedClassName);
		}
		
		if(isMappedConceptPreviously && !isMappedConceptCurrently){
			isMappedClassChanged = true;
		}

		/*
		 * The ONLY CONDITION we need to satisfy in order to apply this rule is
		 * that the mapped class of the concept should be changed. It can be
		 * either changed to another class or to null ( Note that if the mapped
		 * class was changed to null, it means that the previously mapped class
		 * was wrong )
		 */
		if (isMappedClassChanged) {
			System.out.println("Concept: " + this.getUnVerifiedConcept2ClassMapping().getConceptName());
			System.out.println("OldMappingClassName: " + oldMappedClassName);
			System.out.println("newMappingClassName: " + newMappedClassName);

			isMatched = true;
		}
		return isMatched;
	}

}
