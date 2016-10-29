package umbc.ebiquity.kang.ontologyinitializator.algorithm.test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository;
import umbc.ebiquity.kang.instanceconstructor.impl.FileModelRepository;
import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceDescriptionModelConstructionHelper;
import umbc.ebiquity.kang.instanceconstructor.model.builder.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Relation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IRelation2PropertyMappingAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.MatchedOntProperty;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class Relation2OntPropertyMappingTest {
	
	private IOntologyRepository ontologyRepository;
	private IInstanceDescriptionModel extractedTripleStore;;
	
	@Before
	public void Init() throws IOException{ 
		FileRepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop/";
		FileRepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv1.owl";
		ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		
		String webSiteURLString = "http://www.numericalconcepts.com";
		URL webSiteURL = new URL(webSiteURLString);
		IInstanceDescriptionModelRepository repo = new FileModelRepository();
		extractedTripleStore = InstanceDescriptionModelConstructionHelper.createModel(webSiteURL, repo);
	}
	
	@Test
	@Ignore
	public void mapRelation2PropertyWithFakeDataTest(){
		IRelation2PropertyMapper relation2PropertyMapper = new Relation2PropertyMapper();
		MSMResult result = relation2PropertyMapper.matchRelations2OntProperties(this.createRelations(), ontologyRepository.getAllOntProperties());
		if (result != null) {
			ArrayList<SubMapping> subMappingList = result.getSubMappings();
			for (int i = 0; i < subMappingList.size(); i++) {
				SubMapping subMapping = subMappingList.get(i);
				String relationName = subMapping.s.getLocalName();
				String ontPropertyName = subMapping.t.getLocalName();
				double sim = subMapping.getSimilarity();
				System.out.println(relationName + " <-> "+ ontPropertyName + "  " + sim);
			}
		}
	}
	
	@Test
//	@Ignore
	public void mapRelation2PropertyWithRealDataTest() {
		IRelation2PropertyMappingAlgorithm relation2PropertyMappingAlgorithm = new Relation2PropertyMappingAlgorithm(extractedTripleStore,
				ontologyRepository, new Relation2PropertyMapper());
		relation2PropertyMappingAlgorithm.mapRelations2OntProperties();
		for(String relationLabel : relation2PropertyMappingAlgorithm.getRelation2PropertyMap().keySet()){
			MatchedOntProperty property = relation2PropertyMappingAlgorithm.getRelation2PropertyMap().get(relationLabel);
			String propertyName = property.getOntPropertyName();
			String relationName = property.getRelationName();
			double similarity = property.getSimilarity();
			System.out.println(relationName + " <-> "+ propertyName + " " + similarity);
		}
		for(String relationLabel : relation2PropertyMappingAlgorithm.getInformativeRelation2PropertyMap().keySet()){
			String propertyName = relation2PropertyMappingAlgorithm.getInformativeRelation2PropertyMap().get(relationLabel);
			System.out.println(propertyName + " <-> "+ propertyName);
		}
	}

	private Collection<OntPropertyInfo> createRelations(){
		Collection<OntPropertyInfo> ontPropertyList = new ArrayList<OntPropertyInfo>();
		String relation1 = "Our shim producing capabilities include";
		String rangeStr1 = "Die design and production";
		String rangeStr2 = "Wire EDM machining";
		String rangeStr3 = "Heat treating and plating";
		String rangeStr4 = "Bar coding";
		String rangeStr5 = "CNC punching";
		String rangeStr6 = "Special packaging";
		String rangeStr7 = "Circle shearing";
		String rangeStr8 = "Abrasive waterjet cutting";
		OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(relation1, "", relation1);
		OntResourceInfo range1 = new OntResourceInfo(rangeStr1, "", rangeStr1);
		OntResourceInfo range2 = new OntResourceInfo(rangeStr2, "", rangeStr2);
		OntResourceInfo range3 = new OntResourceInfo(rangeStr3, "", rangeStr3);
		OntResourceInfo range4 = new OntResourceInfo(rangeStr4, "", rangeStr4);
		OntResourceInfo range5 = new OntResourceInfo(rangeStr5, "", rangeStr5);
		OntResourceInfo range6 = new OntResourceInfo(rangeStr6, "", rangeStr6);
		OntResourceInfo range7 = new OntResourceInfo(rangeStr7, "", rangeStr7);
		OntResourceInfo range8 = new OntResourceInfo(rangeStr8, "", rangeStr8);
		ontPropertyInfo.addObjectCandidate(range1);
		ontPropertyInfo.addObjectCandidate(range2);
		ontPropertyInfo.addObjectCandidate(range3);
		ontPropertyInfo.addObjectCandidate(range4);
		ontPropertyInfo.addObjectCandidate(range5);
		ontPropertyInfo.addObjectCandidate(range6);
		ontPropertyInfo.addObjectCandidate(range7);
		ontPropertyInfo.addObjectCandidate(range8);
		ontPropertyList.add(ontPropertyInfo);
		return ontPropertyList;
	}
}
