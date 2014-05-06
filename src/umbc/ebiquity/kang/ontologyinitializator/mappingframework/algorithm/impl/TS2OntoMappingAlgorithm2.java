package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmComponent;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IMappingAlgorithmVisitor;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.ITripleRepository;

/***
 * This is the class that establish schema-level mapping between the triple
 * store extracted from web sites and a domain ontology.
 * 
 * @author kangyan2003
 * 
 */
public class TS2OntoMappingAlgorithm2 implements IMappingAlgorithmVisitor, IMappingAlgorithm {

	private Map<String, MatchedOntProperty> relation2PropertyMap;
	private Map<String, String> informativeRelation2PropertyMap;
	private Collection<ClassifiedInstanceDetailRecord> classifiedInstances;
	
	private List<IMappingAlgorithmComponent> mappingAlgorithmComponents;
	private IRelation2PropertyMappingAlgorithm _relation2PropertyMappingAlgorithm;
	private IInstanceClassificationAlgorithm _instanceClassificationAlgorithm;
	
	
	public TS2OntoMappingAlgorithm2(
								   IRelation2PropertyMappingAlgorithm relation2PropertyMappingAlgorithm,
			                       IInstanceClassificationAlgorithm instanceClassificationAlgorithm) {
		this._relation2PropertyMappingAlgorithm = relation2PropertyMappingAlgorithm;
		this._instanceClassificationAlgorithm = instanceClassificationAlgorithm;
		this.addAlgorithmComponents();
	}

	private void addAlgorithmComponents() {
		this.mappingAlgorithmComponents = new ArrayList<IMappingAlgorithmComponent>();
		this.mappingAlgorithmComponents.add((IMappingAlgorithmComponent) _relation2PropertyMappingAlgorithm);
		this.mappingAlgorithmComponents.add((IMappingAlgorithmComponent) _instanceClassificationAlgorithm);
	}

	@Override
	public void mapping() {
		for (IMappingAlgorithmComponent component : mappingAlgorithmComponents) {
			component.accept(this);
		}
	}
	
	@Override
	public IClassifiedInstancesRepository getProprietoryClassifiedInstancesRepository() {
//		IClassifiedInstancesRepository repo = new ProprietoryClassifiedInstancesRepository(tripleStore.getRepositoryName(), 
//																									  ontologyRepository, 
//																									  _proprietaryManufacturingLexicalMappingRepository, 
//																									  relation2PropertyMap, 
//																									  classifiedInstances);
//		return repo;
		return null;
	}
	
	@Override
	public void visit(IRelation2PropertyMappingAlgorithm algorithm) {
		algorithm.mapRelations2OntProperties();
		this.relation2PropertyMap = algorithm.getRelation2PropertyMap();
		this.informativeRelation2PropertyMap = algorithm.getInformativeRelation2PropertyMap();
	}
	
	@Override
	public void visit(IInstanceClassificationAlgorithm algorithm) {
		algorithm.setRelation2PropertyMap(relation2PropertyMap);
		algorithm.getConcept2OntClassMapper().setDomainSpecificConceptMap(informativeRelation2PropertyMap);
		algorithm.classifyInstances();
		classifiedInstances = algorithm.getClassifiedInstances();
	}

	@Override
	public Collection<ClassifiedInstanceDetailRecord> getClassifiedInstances() {
		return classifiedInstances;
	}

	@Override
	public Map<String, MatchedOntProperty> getRelation2PropertyMap() {
		return relation2PropertyMap;
	}
}
