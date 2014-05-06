package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityValidator;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.SimilarityAlgorithm.SimilarityType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmComponent;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmVisitor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.OrderedWordListSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.impl.SimpleLabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class Relation2PropertyMappingAlgorithm implements IRelation2PropertyMappingAlgorithm, IMappingAlgorithmComponent {
	
	/**
	 * Java representation of a triple store extracted from a web site
	 */
	private ITripleRepository tripleStore;
	
	/**
	 * Java representation of the specific domain ontology
	 */
	private IOntologyRepository domaindOntology;
	
	/**
	 * threshold for legitimate matched onto-properties in domain ontology
	 */
	private double thresholdForRelation2Property = 0.60;
	private double thresholdForDifference = 0.2;
	
	private Map<String, MatchedOntProperty> relation2PropertyMap;
	private Map<String, String> informativeRelation2PropertyMap;
	
	/**
	 * algorithm computes the similarity between two labels. This algorithm
	 * combines several (current two) similarity computing method
	 */
	private SimilarityAlgorithm similarityAlg = new SimilarityAlgorithm();
	private EntityValidator commonValidator = new EntityValidator();
	private IRelation2PropertyMapper relation2PropertyMapper;
	private ILabelSimilarity labelSimilarity;

	public Relation2PropertyMappingAlgorithm(ITripleRepository tripleStore, IOntologyRepository ontologyRepository, IRelation2PropertyMapper relation2PropertyMapper){
		this.tripleStore = tripleStore;
		this.domaindOntology = ontologyRepository;
		this.relation2PropertyMapper = relation2PropertyMapper;
		this.labelSimilarity = new SimpleLabelSimilarity(new OrderedWordListSimilarity());
		this.init();
	}
	
	private void init(){
		this.relation2PropertyMap = new LinkedHashMap<String, MatchedOntProperty>();
		this.informativeRelation2PropertyMap = new LinkedHashMap<String, String>();
	}
	
	@Override
	public void mapRelations2OntProperties(){
		Collection<OntPropertyInfo> ontRelationCollection = this.extractRelationsFromTripleRepository();
		System.out.println("kk:");
		for(OntPropertyInfo info : ontRelationCollection){
			System.out.println("kk: " + info.getLocalName());;
		}
		MSMResult result = this.relation2PropertyMapper.matchRelations2OntProperties(ontRelationCollection, domaindOntology.getAllOntProperties());
		this.hashMatchedRelation2PropertyPairs(result);
	}

	private Collection<OntPropertyInfo> extractRelationsFromTripleRepository() {
		Collection<OntPropertyInfo> ontPropertyList = new ArrayList<OntPropertyInfo>();

		// get identified relations from the Triple Store.
		Collection<String> relations = tripleStore.getCustomRelations();
		for (String relation : relations) {
//			System.out.println("@ property: " + relation);
			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(relation, "", relation);
			ontPropertyList.add(ontPropertyInfo);
			// get domain terms of this relation
			for (String subject : tripleStore.getSubjectTermsOfRelation(relation)) {
				if (!commonValidator.isValidRangeTerm(subject))
					continue;
//				System.out.println("  *subject: " + subject);
				OntResourceInfo subjectCandidate = new OntResourceInfo(subject, "", subject);
				ontPropertyInfo.addSubjectCandidate(subjectCandidate);
			}

			// get range terms of this relation
			for (String object : tripleStore.getObjectTermsOfRelation(relation)) {
				if (!commonValidator.isValidRangeTerm(object))
					continue;
//				System.out.println("   *object: " + object);
				OntResourceInfo objectCandidate = new OntResourceInfo(object, "", object);
				ontPropertyInfo.addObjectCandidate(objectCandidate);
			}
		}
		return ontPropertyList;
	}
	
	/**
	 * record (hash) mapped relation-property pairs
	 * 
	 * @param result
	 */
	private void hashMatchedRelation2PropertyPairs(MSMResult result){
		System.out.println("Hashing Matched Property Pairs ...");
		if (result != null) {
			ArrayList<SubMapping> subMappingList = result.getSubMappings();
			for (int i = 0; i < subMappingList.size(); i++) {
				SubMapping subMapping = subMappingList.get(i);
				String relationName = subMapping.s.getLocalName();
				String ontPropertyName = subMapping.t.getLocalName();
				String ontPropertyURI = subMapping.t.getURI();
				String ontPropertyNS = subMapping.t.getNameSpace();
//				System.out.println(" having: " + relationName + " --> " + ontPropertyName);
				double sim = subMapping.getSimilarity();
				if(sim >= this.thresholdForRelation2Property){
					
					if(relation2PropertyMap.containsKey(relationName)){
						MatchedOntProperty origMappedProperty = relation2PropertyMap.get(relationName);
						String origPropertyName = origMappedProperty.getOntPropertyName();
						double origSim = origMappedProperty.getSimilarity();
						if (sim > origSim
								&& (ontPropertyName.length() < origPropertyName.length() || ontPropertyName.compareTo(origPropertyName) > 0)) {
							MatchedOntProperty pair = new MatchedOntProperty(relationName, ontPropertyURI, ontPropertyNS, ontPropertyName, sim);
//							System.out.println(" replaced: " + relationName + " --> " + pair.getOntPropertyName());
							relation2PropertyMap.put(relationName, pair);
						}
					} else {
						MatchedOntProperty pair = new MatchedOntProperty(relationName, ontPropertyURI, ontPropertyNS, ontPropertyName, sim);
//						System.out.println(" added: " + relationName + " --> " + pair.getOntPropertyName());
						relation2PropertyMap.put(relationName, pair);
					}
				}
			}
			
			for(String relationName : relation2PropertyMap.keySet()){
				String ontPropertyName = relation2PropertyMap.get(relationName).getOntPropertyName();
				double sim1 = labelSimilarity.computeLabelSimilarity(ontPropertyName, relationName);
				double sim2 = similarityAlg.getSimilarity(SimilarityType.Ngram, ontPropertyName, relationName);
				if (Math.max(sim1, sim2) < thresholdForDifference) {
					relationName = TextProcessingUtils.tokenizeLabel2String(relationName, true, true, 1);
					ontPropertyName = TextProcessingUtils.tokenizeLabel2String(ontPropertyName, true, true, 1);
					informativeRelation2PropertyMap.put(relationName, ontPropertyName);
				}
			}
		}
	}
	
	@Override
	public Map<String, MatchedOntProperty> getRelation2PropertyMap(){
		return this.relation2PropertyMap;
	}
	
	@Override
	public Map<String, String> getInformativeRelation2PropertyMap(){
		return this.informativeRelation2PropertyMap;
	}

	@Override
	public void accept(IMappingAlgorithmVisitor visitor) {
		visitor.visit(this);
	}
	
}
