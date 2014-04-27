package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.io.IOException;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.AbstractWebUrlLoader.PopulationType;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.EvaluationRecord.EvaluationRecordType;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class InterpretationEvaluator {

	public static void main(String[] args) throws IOException {
		
//		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		IOntologyRepository _ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
//		_ontologyRepository.printOntologyInfo();
		InterpretationEvaluator evaluator = new InterpretationEvaluator(_ontologyRepository);
		double score = evaluator.computeClassMatchScore("EngineeringService", "InspectionService");
		System.out.println(score);
	}

	private IOntologyRepository _ontologyRepository;
	public InterpretationEvaluator(IOntologyRepository ontologyRepository) {
		_ontologyRepository = ontologyRepository;
	}
	
	public EvaluationResult evaluate(
									 IEvaluationCorpusRecordsReader evaluationCorpusRecordsReader,
									 IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository
									 ){
		EvaluationResult evaluationResult = new EvaluationResult();
		int countOfRecall = 0;
		int countOfCorrections = 0;
		double totalScore = 0.0;
		double totalError = 0.0;
		for(String instanceName : proprietoryClassifiedInstancesRepository.getInstanceSet()){
			IClassifiedInstanceDetailRecord classifiedInstanceInfo = proprietoryClassifiedInstancesRepository.getClassifiedInstanceDetailRecordByInstanceName(instanceName);
			OntoClassInfo ontClassInfo = classifiedInstanceInfo.getMatchedOntoClass();
			String expected_className = ontClassInfo.getOntClassName();
			String real_className = evaluationCorpusRecordsReader.getClassLabelforInstance(instanceName);
			
			if(real_className == null) {
				continue;
			}
			double score = this.computeClassMatchScore(expected_className, real_className);
			if (score != 1.0) {
				countOfCorrections++;
				totalError += (1 - score);
			}
			totalScore += score;
			countOfRecall++;
			
			EvaluationRecord evaluationRecord = new EvaluationRecord(EvaluationRecordType.CLASSIFICATION, instanceName, expected_className, real_className, score);
			evaluationResult.addEvaluationRecord(evaluationRecord);
		}
		
//		for(String relationName : proprietoryClassifiedInstancesRepository.getRelationSet()){
//			MatchedOntProperty property = proprietoryClassifiedInstancesRepository.getMatchedOntProperty(relationName);
//			String expected_propertyName = property.getOntPropertyName();
//			String real_propertyName = evaluationCorpusRecordsReader.getPropertyLabelforRelation(relationName);
//			double score = computePropertyMatchScore(expected_propertyName, real_propertyName);
//			totalScore += score;
//			count++;
//			EvaluationRecord evaluationRecord = new EvaluationRecord(EvaluationRecordType.RELATION_PROPERTY_MAPPING, relationName, expected_propertyName, real_propertyName, score);
//			evaluationResult.addEvaluationRecord(evaluationRecord);
//		}
		
		double overallScore = totalScore / countOfRecall;
		evaluationResult.setOverallScore(overallScore);
		evaluationResult.setTotalError(totalError);
		evaluationResult.setNumberOfCorrections(countOfCorrections);
		evaluationResult.setNumberOfRecall(countOfRecall);
		return evaluationResult;
	}

	private double computeClassMatchScore(String expected_className, String real_className) {
		double score = 0.0;
		if (expected_className.equals(real_className))
			score = 1.0;
		boolean isInTheSameClassHierarchy = _ontologyRepository.isInTheSameClassHierarchy(expected_className, real_className);
		
//		boolean isSuperClass = _ontologyRepository.isSuperClassOf(expected_className, real_className, true);
//		if(isSuperClass){
//			double expected_class_depth = _ontologyRepository.getDepth(expected_className);
//			double real_class_depth = _ontologyRepository.getDepth(real_className);
//			score = expected_class_depth / real_class_depth;
//		}

		if (isInTheSameClassHierarchy) {
			System.out.println("here1");
			double correctness = 0.0;
			double expected_class_depth = _ontologyRepository.getDepth(expected_className);
			double real_class_depth = _ontologyRepository.getDepth(real_className);
			double coverage = 0.0;
			if (_ontologyRepository.isSubClassOf(real_className, expected_className, true)) {
				System.out.println("here2");
				correctness = 1.0;
				coverage = expected_class_depth / real_class_depth;
				score = 5 * correctness * coverage / (4 * coverage + correctness);
			} else if (_ontologyRepository.isSuperClassOf(real_className, expected_className, true)) {
				System.out.println("here3");
				correctness = real_class_depth / expected_class_depth;
				coverage = 1.0;
				score = 5 * correctness * coverage / (4 * coverage + correctness);
			}
		}

		return score;
	}
	
	private double computePropertyMatchScore(String expected_propertyName, String real_propertyName){
		double score = 0.0;
		if (expected_propertyName.equals(real_propertyName))
			score = 1.0;
		return score;
	}

}
