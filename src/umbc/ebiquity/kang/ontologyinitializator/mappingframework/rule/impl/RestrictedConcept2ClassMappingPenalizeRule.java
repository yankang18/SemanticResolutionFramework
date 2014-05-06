package umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.impl;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class RestrictedConcept2ClassMappingPenalizeRule extends AbstractConcept2ClassMappingPenalizeRule {

	public RestrictedConcept2ClassMappingPenalizeRule(IOntologyRepository ontologyRepository, IManufacturingLexicalMappingRecordsAccessor MLRepository) {
		super(ontologyRepository, MLRepository);
	}

	@Override
	public boolean isMatch() {
		boolean isMatched = false;
		boolean isMappedConceptPreviously = this.getUnVerifiedConcept2ClassMapping().isMappedConcept();
		boolean isMappedConceptCurrently = this.getVerifiedConcept2ClassMapping().isMappedConcept();

		OntoClassInfo newMappedClass = this.getVerifiedConcept2ClassMapping().getMappedOntoClass();
		OntoClassInfo oldMappedClass = this.getUnVerifiedConcept2ClassMapping().getMappedOntoClass();
		String newMappedClassName = "";
		String oldMappedClassName = "";
		boolean isMappedClassChanged = false;
		if(isMappedConceptPreviously && isMappedConceptCurrently){
			 newMappedClassName = newMappedClass.getOntClassName();
			 oldMappedClassName = oldMappedClass.getOntClassName();
			 isMappedClassChanged = !newMappedClassName.equals(oldMappedClassName);
		}
		
		if(isMappedConceptPreviously && !isMappedConceptCurrently){
			isMappedClassChanged = true;
		}

		/*
		 * The FIRST CONDITION we need to satisfy in order to apply this rule is
		 * that the mapped class of the concept should be changed. It can be
		 * either changed to another class or to null ( Note that if the mapped
		 * class was changed to null, it means that the previously mapped class
		 * was wrong )
		 */
		if (isMappedClassChanged) {
			System.out.println("Concept: " + this.getUnVerifiedConcept2ClassMapping().getConceptName());
			System.out.println("OldMappingClassName: " + oldMappedClassName);
			System.out.println("newMappingClassName: " + newMappedClassName);
			
			boolean oldIsSuperClassOfNew = this.getOntologyRepository().isSuperClassOf(oldMappedClassName, newMappedClassName, false);
			/*
			 * The SECOND CONDITION we need to satisfy in order to apply
			 * this rule is that the old mapped class is NOT a super class
			 * of the new mapped class. Because if the old mapped class is a
			 * super class of the new mapped class and when the instance is
			 * classified into the new mapped class, we still can infer that
			 * the instance is a type of the old mapped class.
			 */
			if (!oldIsSuperClassOfNew) {
				isMatched = true;
			}
		}
		return isMatched;
	}
}
