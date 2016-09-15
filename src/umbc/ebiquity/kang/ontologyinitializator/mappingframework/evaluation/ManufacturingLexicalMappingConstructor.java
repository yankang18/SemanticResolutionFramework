package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.Concept;
import umbc.ebiquity.kang.instanceconstructor.model.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.model.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.impl.Concept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IConcept2OntClassMapper;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces.IInstanceClassificationAlgorithm;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter.MappingRelationType;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InstanceDescriptionModelFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;

public class ManufacturingLexicalMappingConstructor extends AbstractWebUrlLoader {
	
	public static void main(String[] args) throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/home/yankang/Desktop/";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/home/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		String fileFullPath = "/home/yankang/Desktop/WebUrls_TripleStore_forConstructingMLM.txt";
		RepositoryParameterConfiguration.MANUFACTUIRNG_LEXICON_HOST_DIRECTORY = "/home/yankang/Desktop/MLM";
		IOntologyRepository _ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		
		
		Concept2OntClassMapper mapper = new Concept2OntClassMapper();
		ManufacturingLexicalMappingConstructor PCIRAC = new ManufacturingLexicalMappingConstructor(_ontologyRepository, mapper);
//		PCIRAC.loadRecords(fileFullPath);
//		PCIRAC.construct(PopulationType.CRAWL_INDICATED);
//		PCIRAC.createManufacturingLexicalMapping("_MLM");
		
		
		PCIRAC.CreatePreDefinedLexicalMapping("/home/yankang/Desktop/initialLexicalMappings","_MLM_forBayes");
	}
	
	private IOntologyRepository ontologyRepository;
	private IConcept2OntClassMapper concept2OntClassMapper;
	private Map<Concept, List<Concept2OntClassMapping>> Concept2RelatedConceptMap;
	private Set<Concept2OntClassMapping> concept2OntClassMap;
	private Set<Concept2OntClassMapping> concept2OntClassMap2;
	private Set<Concept2OntClassMapping> concept2OntClassMap3;
	
	public ManufacturingLexicalMappingConstructor(IOntologyRepository ontologyRepository, IConcept2OntClassMapper concept2OntClassMapper){
		this.ontologyRepository = ontologyRepository;
		this.concept2OntClassMapper = concept2OntClassMapper;
		this.Concept2RelatedConceptMap = new HashMap<Concept, List<Concept2OntClassMapping>>();
		this.concept2OntClassMap = new HashSet<Concept2OntClassMapping>();
		this.concept2OntClassMap2 = new HashSet<Concept2OntClassMapping>();
//		this.concept2OntClassMap3 = new HashSet<Concept2OntClassMapping>();
	}
	
	
	public void construct(PopulationType populationType) throws IOException{  
		Map<String, Boolean> crawlIndicators;
		switch (populationType) {
		case ONLY_CRAWL_FAILED:
			crawlIndicators = webSiteRecrawlFailed;
			break;
		case CRAWL_INDICATED:
			crawlIndicators = webSiteRecrawlIndicated;
			break;
		case CRAWL_ALL:
			crawlIndicators = webSiteRecrawlAll;
			break;
		default:
			crawlIndicators = webSiteRecrawlFailed;
		}
		for (String webSiteURLStr : crawlIndicators.keySet()) {
			boolean recrawl = crawlIndicators.get(webSiteURLStr);
			if (recrawl) {
				URL webSiteURL = new URL(webSiteURLStr);
				IInstanceDescriptionModel tripleStore = InstanceDescriptionModelFactory.createModel(webSiteURL, true);
				classifyInstances(tripleStore.getInstanceTripleSets(), ontologyRepository.getAllOntClasses());
			}
		}
		this.inferClassforUnMappedConcept();
//		this.addPreDefinedLexicalMapping();
	}
	
	public void classifyInstances(Collection<InstanceTripleSet> instanceTripleSets, Collection<OntoClassInfo> ontClasses) {
		
		for (InstanceTripleSet instanceTripleSet : instanceTripleSets) {
			
			System.out.println();
			System.out.println("Identifying class for: <" + instanceTripleSet.getSubjectLabel() + ">");
			
			Set<Concept> fullConceptSet = this.collectConcepts(instanceTripleSet);
			Collection<Concept2OntClassMapping> concept2OntClassMappingPairs = concept2OntClassMapper.mapConcept2OntClass(fullConceptSet, ontologyRepository.getAllOntClasses());
			concept2OntClassMap.addAll(concept2OntClassMappingPairs);
			Set<Concept> mappedConcept = new HashSet<Concept>();
//			Set<Concept2OntClassMapping> concept2ClassMap = new HashSet<Concept2OntClassMapping>();
			for (Concept2OntClassMapping c2cMapping : concept2OntClassMappingPairs) {
//				concept2ClassMap.add(c2cMapping);
				mappedConcept.add(c2cMapping.getConcept());
			}

			for (Concept concept : fullConceptSet) {
				if (!mappedConcept.contains(concept)) {
					if (Concept2RelatedConceptMap.containsKey(concept)) {
						Concept2RelatedConceptMap.get(concept).addAll(concept2OntClassMappingPairs);
					} else {
						Concept2RelatedConceptMap.put(concept, new ArrayList<Concept2OntClassMapping>(concept2OntClassMappingPairs));
					}
				}
			}
		}
	}
	
	public void inferClassforUnMappedConcept() {
		for (Concept concept : Concept2RelatedConceptMap.keySet()) {
			List<Concept2OntClassMapping> relatedConcepts = Concept2RelatedConceptMap.get(concept);
			int totalOfNumberOfRelatedConcept = relatedConcepts.size();
			if(totalOfNumberOfRelatedConcept == 0){
				continue;
			}
			System.out.println();
			System.out.println("$$: " + concept.getConceptName());
			Map<String, List<Concept2OntClassMapping>> xxxx = this.groupOntClassesBasedOnHierarchy(relatedConcepts);
			Map<String, Double> score2ClassMap = new HashMap<String, Double>();
			for (String hierarchy : xxxx.keySet()) {
				List<Concept2OntClassMapping> classesInSameHierarchy = xxxx.get(hierarchy);
				int NumberOfRelatedConcept = classesInSameHierarchy.size();
				System.out.println("   -" + hierarchy);
				Concept2OntClassMapping firstClass = classesInSameHierarchy.get(0);
				double totalScore = firstClass.getMappingScore();
				String topClass = firstClass.getMappedOntoClassName();
				
				System.out.println("       -" + firstClass.getConceptName() + ":" + firstClass.getMappedOntoClassName());
				for(int i = 1; i< NumberOfRelatedConcept; i++){
					Concept2OntClassMapping mapping = classesInSameHierarchy.get(i);
					totalScore += mapping.getMappingScore();
					String className = mapping.getMappedOntoClassName();
					if(ontologyRepository.isSuperClassOf(className, topClass, true)){
						topClass = className;
					} else if(!ontologyRepository.isInTheSamePath(className, topClass)){
//						System.out.println("$" + className +" "+ " "+topClass);
						topClass = ontologyRepository.getLowestCommonAncestor(className, topClass);
					}
					System.out.println("       -" + mapping.getConceptName() + ":" + mapping.getMappedOntoClassName());
				}
				
				double score = totalScore / NumberOfRelatedConcept; // approximate the similarity between the concept and the top-class
				double support = (double) Math.log(NumberOfRelatedConcept + 1) / (double) Math.log(totalOfNumberOfRelatedConcept + 1); // measures the likelihood that the concept map to a class in a specific hierarchy
				double num = NumberOfRelatedConcept;
				System.out.println("       @@TOP-Class: " + topClass + " with score: " + score + " support: " + support + " number: " + num);
//				if (num > 1) {
//					score2ClassMap.put(topClass, score * support);
//				}
				score2ClassMap.put(topClass, score * support);
			}
			
			Map<String, Double> sorted = this.sortByValues(score2ClassMap);
//			if(sorted.size() > 0){
//				System.out.println();
//				System.out.println(concept.getConceptName());
			// }
			
			double totalScore = 0.0;
			int topK = 3;
			int i = 0;
			for (String className : sorted.keySet()) {
				if (i < topK) {
					totalScore += sorted.get(className);
					i++;
				}
			}
			i = 0;
			for (String className : sorted.keySet()) {
				if (i < topK) {
					System.out.println(i + "## className: " + className + " " + sorted.get(className) / totalScore);
					Concept2OntClassMapping c2cMapping = new Concept2OntClassMapping(concept, ontologyRepository.getLightWeightOntClassByName(className), sorted.get(className) / totalScore);
					concept2OntClassMap2.add(c2cMapping);
					i++;
				}
			}
		}
	}
	
	public void CreatePreDefinedLexicalMapping(String fullPath, String mappingName) throws IOException{ 
		this.concept2OntClassMap3 = new HashSet<Concept2OntClassMapping>();
		this.loadPredefinedLexicalMappings(fullPath);
		IManufacturingLexicalMappingRepository MLM = ManufacturingLexicalMappingRepositoryFactory.createProprietaryManufacturingLexiconRepository(mappingName);
		for (Concept2OntClassMapping mapping : concept2OntClassMap3) {
			MLM.addNewConcept2OntoClassMapping(mapping.getConcept(), 
											   MappingRelationType.relatedTo, 
											   mapping.getMappedOntoClass(),
											   mapping.getMappingScore());
		}
		MLM.showRepositoryDetail();
		MLM.saveRepository();
	} 
	
	
	private boolean loadPredefinedLexicalMappings(String fileFullName) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadMapping(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private void loadMapping(String line) {
		if (this.isBlank(line))
			return;
		
		System.out.println(line);
		String[] tokens = line.split("=");
		
		
		String ConceptName = tokens[0].trim().toLowerCase();
		String className = tokens[1].trim();
		if (tokens.length == 2) {
			Concept concept = new Concept(ConceptName);
			Concept2OntClassMapping c2cMapping = new Concept2OntClassMapping(concept, ontologyRepository.getLightWeightOntClassByName(className), 1.0);
			concept2OntClassMap3.add(c2cMapping);
		}
	}
	
	private boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	
	public void createManufacturingLexicalMapping(String repositoryName) throws IOException {
		IManufacturingLexicalMappingRepository MLM = ManufacturingLexicalMappingRepositoryFactory.createProprietaryManufacturingLexiconRepository(repositoryName);
		for (Concept2OntClassMapping mapping : concept2OntClassMap) {
			MLM.addNewConcept2OntoClassMapping(mapping.getConcept(), 
											   MappingRelationType.relatedTo, 
											   mapping.getMappedOntoClass(),
											   mapping.getMappingScore());
		}
		
		for (Concept2OntClassMapping mapping : concept2OntClassMap2) {
			MLM.addNewConcept2OntoClassMapping(mapping.getConcept(), 
											   MappingRelationType.relatedTo, 
											   mapping.getMappedOntoClass(),
											   mapping.getMappingScore());
		}
		
//		for (Concept2OntClassMapping mapping : concept2OntClassMap3) {
//			MLM.addNewConcept2OntoClassMapping(mapping.getConcept(), 
//											   MappingRelationType.relatedTo, 
//											   mapping.getMappedOntoClass(),
//											   mapping.getMappingScore());
//		}
		
		MLM.showRepositoryDetail();
		MLM.saveRepository();
	}
	
	 /*
     * Java method to sort Map in Java by value e.g. HashMap or Hashtable
     * throw NullPointerException if Map contains null values
     * It also sort values even if they are duplicates
     */
    public Map<String,Double> sortByValues(Map<String,Double> map){
        List<Map.Entry<String,Double>> entries = new LinkedList<Map.Entry<String,Double>>(map.entrySet());
     
        Collections.sort(entries, new Comparator<Map.Entry<String,Double>>() {

            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        });
     
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<String, Double> sortedMap = new LinkedHashMap<String,Double>();
     
        for(Map.Entry<String,Double> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
     
        return sortedMap;
    }

	
	private Map<String, List<Concept2OntClassMapping>> groupOntClassesBasedOnHierarchy(List<Concept2OntClassMapping> relatedConcepts) {
		Map<String, List<Concept2OntClassMapping>> hierarchy2OntClassesMap = new HashMap<String, List<Concept2OntClassMapping>>();
		for(Concept2OntClassMapping map : relatedConcepts){
			String hierarchyStr = String.valueOf(ontologyRepository.getOntClassHierarchyNumber(map.getMappedOntoClass()));
			if(hierarchy2OntClassesMap.containsKey(hierarchyStr)){
				hierarchy2OntClassesMap.get(hierarchyStr).add(map);
			} else {
				List<Concept2OntClassMapping> classes = new ArrayList<Concept2OntClassMapping>();
				classes.add(map);
				hierarchy2OntClassesMap.put(hierarchyStr, classes);
			}
		}
		return hierarchy2OntClassesMap;
	}


	private Set<Concept> collectConcepts(InstanceTripleSet instanceTripleSet){
		Map<Concept, Concept> fullConcepts = new LinkedHashMap<Concept, Concept>();
		
		/*
		 * 
		 */
		Collection<Concept> conceptSet = instanceTripleSet.getConceptSet();
		if (conceptSet != null) {
			for (Concept concept : conceptSet) {
				/*
				 *  record the concepts learned from the Entity Graph
				 */
				String value = concept.getConceptName().trim();
				// apply stemmer here?
				concept.updateLabel(TextProcessingUtils.tokenizeLabel2String(value, true, true, 1));
				this.addConcept(fullConcepts, concept);
//				fullConceptSet.add(concept);
				double score = Math.log(concept.getScore()) > 1 ? Math.log(concept.getScore()) : 1;
				System.out.println("   - concept: " + value + ", " + score);
			}
		}
		return new HashSet<Concept>(fullConcepts.values());
	}
	
	private void addConcept(Map<Concept, Concept> concepts, Concept concept){
		if(!this.isValidConcept(concept)) return; 
		
		if (concepts.containsKey(concept)) {
			concepts.get(concept).addScore(concept.getScore());
		} else {
			concepts.put(concept, concept);
		}
	}
	
	private boolean isValidConcept(Concept concept) {
		String conceptName = concept.getConceptName().trim();
		String regex = "\\d+";
		if (conceptName.equals("") || conceptName.matches(regex))
			return false;

		return true;

	}

}
