package umbc.ebiquity.kang.entityframework.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class EntityValidator {
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isValideEntity(Entity entity){
		String entityLabel = entity.getEntityLabel().trim();
		return this.isValidEntityLabel(entityLabel);
	}
	
	/**
	 * 
	 */
	public boolean isValidEntityNode(EntityNode entityNode){
		String entityLabel = entityNode.getLabel().trim();
		return this.isValidEntityLabel(entityLabel);
	}
	
	/**
	 * 
	 * @param entityLabel
	 * @return
	 */
	private boolean isValidEntityLabel(String entityLabel){
		boolean isValidEntity = true;
		if (entityLabel.equals("")) {
			isValidEntity = false;
		} else if (entityLabel.indexOf(".") == entityLabel.length() - 1) {
			// if end with the "period"
			isValidEntity = false;
		} else if (this.containsAuxiliaryVerb(entityLabel)) {
			// if contains auxiliary verb
			isValidEntity = false;
		} else if (this.containsOnlyPunctuation(entityLabel)) {
			// if contains only punctuation
			isValidEntity = false;
		} else if (this.isShortString(entityLabel, 2)){
			// if too short
			isValidEntity = false;
		} else if (TextProcessingUtils.containsWebPageStopwords(entityLabel)){
			// if contains web page stop words, e.g., 
			isValidEntity = false;
		} else if (TextProcessingUtils.containsOnlyDefaultStopwords(entityLabel)){
			isValidEntity = false;
		}
		return isValidEntity;
	}
	
	/**
	 * 
	 * @param relation
	 * @param allowedLength
	 * @return
	 */
	public boolean isValidRelation(EntityNode relation, int allowedLength){
		
		String relationLabel = relation.getLabel().trim();
		boolean isValidateRelation = true;
		if(!isValidEntityNode(relation)){
			isValidateRelation = false;
		} else if (this.containsSentencePunctuationForRelation(relationLabel)) {
			isValidateRelation = false;
		} else if (this.containsNumbers(relationLabel)){
			isValidateRelation = false;
		} else if (this.isLongWordString(relationLabel, allowedLength)) {
			isValidateRelation = false;
		} else if (this.containsOnlyNumbers(relationLabel)){
			isValidateRelation = false;
		}
		return isValidateRelation;
	}
	
	public boolean isValidInstance(EntityNode instance, int allowedLength){
		String instanceLabel = instance.getLabel().trim();
		return this.isValidInstance(instanceLabel, allowedLength);
	}
	
	public boolean isValidConcept(Concept concept, int allowedLength){
		String conceptLabel = concept.getConceptName().trim();
		return this.isValidInstance(conceptLabel, allowedLength);
	}
	
	private boolean isValidInstance(String instanceLabel, int allowedLength){
		boolean isValidInstance = true;
		
		if(!isValidEntityLabel(instanceLabel)){
			isValidInstance = false;
		} else if (this.containsSentencePunctuation(instanceLabel)) {
			isValidInstance = false;
		} else if (this.containsOnlyPunctuation(instanceLabel)) {
			isValidInstance = false;
		} else if (this.isLongWordString(instanceLabel, allowedLength)) {
			isValidInstance = false;
		} else if (this.containsOnlyNumbers(instanceLabel)){
			isValidInstance = false;
		}
		return isValidInstance;
	}
	
//	public Collection<EntityNode> validateRelations(Set<EntityNode> relations){
//		
//		Collection<EntityNode> validatedRelations = new ArrayList<EntityNode>();
//		if (relations == null) {
//			return validatedRelations;
//		}
//		
//		for (EntityNode relation : relations) {
//			
//			String relationLabel = relation.getLabel().trim();
//			boolean isValidateRelation = true;
//			if (relationLabel.indexOf(".") == relationLabel.length() - 1) {
//				isValidateRelation = false;
//			} else if (this.containsAuxiliaryVerb(relationLabel)) {
//				isValidateRelation = false;
//			} else if (this.containsSentencePunctuationForRelation(relationLabel)) {
//				isValidateRelation = false;
//			} else if (this.containsOnlyPunctuation(relationLabel)) {
//				isValidateRelation = false;
//			} else if (this.containsNumbers(relationLabel)){
//				isValidateRelation = false;
//			} else if (this.isLongWordString(relationLabel, 7)) {
//				isValidateRelation = false;
//			} else if (this.isShortString(relationLabel, 3)){
//				isValidateRelation = false;
//			} else if (this.containsOnlyNumbers(relationLabel)){
//				isValidateRelation = false;
//			}
//			
//			if (isValidateRelation) {
//				validatedRelations.add(relation);
//			}
//		}
//		return validatedRelations;
//	}
	
	public Collection<EntityNode> validateRangeTerms(Collection<EntityNode> rangeTerms) {
		Collection<EntityNode> validatedRangeTerms = new ArrayList<EntityNode>();
		if (rangeTerms == null) {
			return validatedRangeTerms;
		}
		int candidateRangeTermCount = rangeTerms.size();
		int invalidatedRangeTermCount = 0;
		
		for (EntityNode rangeTerm : rangeTerms) {
			String rangeLabel = rangeTerm.getLabel().trim();
//			System.out.println(rangeLabel);
			boolean isValidateRangeTerm = this.isValidRangeTerm(rangeLabel);
			if (isValidateRangeTerm) {
				validatedRangeTerms.add(rangeTerm);
			}

		}
//		double inValidatedRate = (double) invalidatedRangeTermCount / (double) candidateRangeTermCount;
//		if(inValidatedRate > 0.75) {
//			return new ArrayList<EntityNode>();
//		}
		return validatedRangeTerms;
	}
	
	public boolean isValidRangeTerm(String rangeLabel){
		boolean isValidaRangeTerm = true;
		if (rangeLabel.indexOf(".") == rangeLabel.length() - 1) {
			isValidaRangeTerm = false;
		} else if (this.containsAuxiliaryVerb(rangeLabel)) {
			isValidaRangeTerm = false;
		} else if (this.containsSentencePunctuation(rangeLabel)) {
			isValidaRangeTerm = false;
		} else if (this.containsOnlyPunctuation(rangeLabel)) {
			isValidaRangeTerm = false;
		} else if (this.isLongWordString(rangeLabel, 10)) {
			isValidaRangeTerm = false;
		} else if (this.isShortString(rangeLabel, 2)) {
			isValidaRangeTerm = false;
		}
		return isValidaRangeTerm;
		
	}

	private boolean isShortString(String label, int stringLength) {
		if (label.replaceAll("\\p{Punct}\\>\\<", "").length() <= stringLength) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isLongWordString(String label, int wordCount) {
//		String[] tokens = TextProcessingUtils.tokenizeLabel(TextProcessingUtils.removeStopwords(label.toLowerCase()));
//		if (tokens.length > wordCount) {
		if (label.split(" ").length >= wordCount) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean containsOnlyPunctuation(String rangeLabel) {
		return rangeLabel.replaceAll("\\p{Punct}\\>\\<", "").trim().equals("");
	}
	
	private boolean containsOnlyNumbers(String label) {
		for (char c : label.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	private boolean containsSentencePunctuation(String rangeLabel) {
		String patternStr = "[?!;,\"\']";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher m = pattern.matcher(rangeLabel);
		return m.find();
	}

	private boolean containsSentencePunctuationForRelation(String relationLabel) {
		String patternStr = "[?!;,.\"\']";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher m = pattern.matcher(relationLabel);
		return m.find();
	}
	
	private boolean containsNumbers(String relationLabel){
		String patternStr = ".*[0-9].*";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher m = pattern.matcher(relationLabel);
		return m.find();
	}
	
	private boolean containsAuxiliaryVerb(String label) {
		label = label.toLowerCase();
		if (label.contains(" are ") || 
				label.contains(" is ") || 
				label.contains(" were ") || 
				label.contains(" was ") || 
				label.contains(" am ") ||
				label.contains(" has ") ||
				label.contains(" have ") ||
				label.contains(" had ") ||
				label.contains("are ") || 
				label.contains("is ") || 
				label.contains("were ") || 
				label.contains("was ") || 
				label.contains("am ") ||
				label.contains("has ") ||
				label.contains("have ") ||
				label.contains("had ") ||
				label.contains(" do ") ||
				label.contains(" does ") ||
				label.contains(" did ") ||
				label.contains("do ") ||
				label.contains("does ") ||
				label.contains("did ")) {
			return true;
		}
		return false;
	}

}
