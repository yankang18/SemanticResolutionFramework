package umbc.ebiquity.kang.ontologyinitializator.evaluation.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.EvaluationResult;
import umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation.InterpretationEvaluator;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ClassifiedInstancesRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.InterpretationCorrectionRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.ManufacturingLexicalMappingRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.factories.OntologyRepositoryFactory;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.EvaluationCorpus;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.EvaluationCorpusRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.ProprietoryClassifiedInstancesRepository.ClassifiedInstancesRepositoryType;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.FileUtility;

public class InterpretedInstancesEvaluation {
	
	
	private static InterpretationEvaluator _evaluator;
	private static IOntologyRepository _ontologyRepository;
	private static IEvaluationCorpusRecordsAccessor _evaluationCorpusRecordsAccessor;

	@BeforeClass
	public static void SetUp() throws IOException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		_evaluationCorpusRecordsAccessor = new EvaluationCorpusRecordsAccessor();
		_ontologyRepository = OntologyRepositoryFactory.createOntologyRepository();
		_evaluator = new InterpretationEvaluator(_ontologyRepository);
//		_ontologyRepository.printOntologyInfo();
	}
	
	@Ignore
	@Test
	public void evaluate() throws IOException {  
//		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
//		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";
		String homeURL = "http://www.numericalconcepts.com/";
		URL webURL = new URL(homeURL);
		String _repositoryName = FileUtility.convertURL2FileName(webURL);
		String evaluationCorpusFullPath = RepositoryParameterConfiguration.getMappingHumanReadableDirectoryFullPath();
		String proprietaryClassifiedInstanceRepositoryFullPath = RepositoryParameterConfiguration.getMappingDetailinfoDirectoryFullPath();
		boolean dirExists1 = FileUtility.exists(evaluationCorpusFullPath);
		boolean dirExists2 = FileUtility.exists(proprietaryClassifiedInstanceRepositoryFullPath);
		String fileFullName = evaluationCorpusFullPath + _repositoryName;
//		System.out.println("Path: " + fileFullName);
		if (dirExists1 && dirExists2) {
			IEvaluationCorpusRecordsReader evaluationCorpusRecordsReader = new EvaluationCorpus(fileFullName, 	
																								_ontologyRepository,
																								_evaluationCorpusRecordsAccessor);
			((EvaluationCorpus) evaluationCorpusRecordsReader).loadRepository();
			IManufacturingLexicalMappingRepository proprietoryManufacturingLexicalMappingRepository = ManufacturingLexicalMappingRepositoryFactory.createProprietaryManufacturingLexiconRepository(_repositoryName);
			IClassifiedInstancesRepository proprietoryClassifiedInstancesRepository = new ProprietoryClassifiedInstancesRepository
				       (
				        _repositoryName, 
						ClassifiedInstancesRepositoryType.All, 
						_ontologyRepository, 
						proprietoryManufacturingLexicalMappingRepository
	                    );
//			evaluationCorpusRecordsReader.showRecords();
			EvaluationResult evaluationResult = _evaluator.evaluate(evaluationCorpusRecordsReader, proprietoryClassifiedInstancesRepository); 
			System.out.println("Score: " + evaluationResult.getOverallScore());
		} else {
		}
	}
	
//	@Ignore
	@Test 
	public void ClassificationCorrectionClusterFeatureWrapperTest() throws MalformedURLException {
		RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH = "/Users/yankang/Desktop";
		RepositoryParameterConfiguration.ONTOLOGY_OWL_FILE_FULL_PATH = "/Users/yankang/Desktop/Ontologies/MSDL-Fullv2.owl";

		String homeURL = "http://www.numericalconcepts.com/";
		URL webURL = new URL(homeURL);
		String _repositoryName = FileUtility.convertURL2FileName(webURL);
		String evaluationCorpusFullPath = RepositoryParameterConfiguration.getMappingHumanReadableDirectoryFullPath();
		System.out.println("path: " + evaluationCorpusFullPath);
		boolean dirExists1 = FileUtility.exists(evaluationCorpusFullPath);
		String fileFullName = evaluationCorpusFullPath + _repositoryName;
		
		String fileFullName2 = "/Users/yankang/Desktop/standards/numericalconcepts";
		if (dirExists1) {
			IEvaluationCorpusRecordsReader evaluationCorpusRecordsReader = new EvaluationCorpus(fileFullName2, _ontologyRepository,
					_evaluationCorpusRecordsAccessor);
			((EvaluationCorpus) evaluationCorpusRecordsReader).loadRepository();
			evaluationCorpusRecordsReader.showRecords();
		}
	}

}
