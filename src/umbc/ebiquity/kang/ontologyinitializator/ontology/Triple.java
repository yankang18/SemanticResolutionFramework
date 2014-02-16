package umbc.ebiquity.kang.ontologyinitializator.ontology;

import java.util.HashSet;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.Concept;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.EntityNode;


public class Triple {
	
	public enum BuiltinPredicate {
		SubClass, SubProperty, Type, /* temporary */SubConcept, /* temporary */SubRole, isAspectOf
	}

	public enum PredicateType {
		Custom, Builtin
	}
	
	private String subject;
	private String object;
	private String processedSubjectLabel;
	private String processedObjectLabel;
	private String predicate;
	private PredicateType predicateType;
	private BuiltinPredicate builtinPredicate;
	private Concept concept;
	private boolean inferred = false;
	
	public static Set<String> BuiltinPredicateSet;
	static {
		BuiltinPredicateSet = new HashSet<String>();
		BuiltinPredicateSet.add(BuiltinPredicate.SubClass.toString());
		BuiltinPredicateSet.add(BuiltinPredicate.SubProperty.toString());
		BuiltinPredicateSet.add(BuiltinPredicate.Type.toString());
		BuiltinPredicateSet.add(BuiltinPredicate.SubConcept.toString());
		BuiltinPredicateSet.add(BuiltinPredicate.SubRole.toString());
		BuiltinPredicateSet.add(BuiltinPredicate.isAspectOf.toString());
	}
	
	public Triple(String subject, String predicate, String object) {
		this.init(subject, "", predicate, object, "");
	}
	
	public Triple(String subject, String processedSubjectLable, String predicate, String object, String processedObjectLabel) {
//		this.predicateType = PredicateType.Custom;
		this.init(subject, processedSubjectLable, predicate, object, processedObjectLabel);
	}
    
    public Triple(String subject, String processedSubjectLable, BuiltinPredicate predicate, String object, String processedObjectLabel){
//    	this.predicateType = PredicateType.Builtin;
    	this.builtinPredicate = predicate;
    	this.init(subject, processedSubjectLable, predicate.toString(), object, processedObjectLabel);
    }
    
    public Triple(EntityNode instanceNode, Concept concept) {
    	this.builtinPredicate = BuiltinPredicate.isAspectOf;
    	this.concept = concept;
    	this.init(instanceNode.getLabel(), instanceNode.getProcessedTermLabel(), builtinPredicate.toString(), concept.getConceptName(), concept.getConceptName());
	}
    
    public Triple(String instanceStr, Concept concept) {
    	this.builtinPredicate = BuiltinPredicate.isAspectOf;
    	this.concept = concept;
    	this.init(instanceStr, instanceStr, builtinPredicate.toString(), concept.getConceptName(), concept.getConceptName());
	}

	private void init(String subject, String processedSubjectLabel, String predicate, String object, String processedObjectLabel){
    	this.subject = subject;
    	this.processedSubjectLabel = processedSubjectLabel;
    	this.predicate = predicate;
    	this.object = object;
    	this.processedObjectLabel = processedObjectLabel;
    }

	public String getSubject() {
		return subject.trim();
	}

	public String getObject() {
		return object.trim();
	}

	public String getPredicate() {
		return predicate.trim();
	}
	
	public String getProcessedSubjectLabel() {
		return processedSubjectLabel.trim();
	}

	public String getProcessedObjectLabel() {
		return processedObjectLabel.trim();
	}

	public PredicateType getPredicateType() {
		return predicateType;
	}
	
	public void setPredicateType(PredicateType predicateType){
		this.predicateType = predicateType;
	}
	
	public void setInferred(boolean inferred) {
		this.inferred = inferred;
	}

	public Concept getConcept(){
		return this.concept;
	}
	
	public boolean isInferred() {
		return inferred;
	}
	
	@Override
	public String toString() {
		return "<" + this.subject + ">  <" + this.predicate + ">  <" + this.object + ">";
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Triple triple = (Triple) obj;
		return this.toString().equals(triple.toString());
	}

	public BuiltinPredicate getBuiltinPredicate() {
		return builtinPredicate;
	}

}
