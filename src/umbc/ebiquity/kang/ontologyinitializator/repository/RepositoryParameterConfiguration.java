package umbc.ebiquity.kang.ontologyinitializator.repository;

public class RepositoryParameterConfiguration {
	
	

	/**
	 * Operating System specific line separator that separates lines in a file
	 */
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 * 
	 */
	public static String REPOSITORIES_DIRECTORY_FULL_PATH = System.getProperty("user.dir");
	

	/**
	 * 
	 */
	public static String ONTOLOGY_OWL_FILE_FULL_PATH = System.getProperty("user.dir") + "/MSDL-Fullv1.owl";
	
	/*
	 * 
	 */
	
	
	/**
	 *  
	 */
	private final static String TRIPLE_REPOSITORY_DIRECTORY = "/TripleRepository/";
	
	/**
	 * 
	 */
	private final static String ONTOLOGY_INDEX_FILES_DIRECTORY = "/OntologyRepository/"; 
	
	/**
	 * 
	 */
	public final static String ONTOLOGY_CLASS_RECORDS_FILENAME = "OntologyClassRecords";
	
	/**
	 * 
	 */
	public final static String ONTOLOGY_PROPERTY_RECORDS_FILENAME = "OntologyPropertyRecords";

	/**
	 * 
	 */
	public final static String ONTOLOGY_CODED_CLASS_RECORDS_FILENAME = "OntologyIndexedClassRecords";
	
	/**
	 * 
	 */
	public final static String MANUFACTURING_LEXICON_NAME = "ManufacturingLexicon";
	
	/**
	 * 
	 */
	public final static String CLASSIFICATION_CORRECTION_REPOSITORY_NAME = "ClassificationCorrections";
	
	public final static String NEGATIVE_CONCEPT_CLASS_MAPPING = "NegativeConceptClassMappings";
	public static final String POSITIVE_CONCEPT_CLASS_MAPPING = "PositiveConceptClassMappings";
	public final static String All_CONCEPT_CLASS_MAPPING = "AllConceptClassMapping";
	/**
	 * 
	 */
	private final static String MANUFACTURING_LEXICON_DIRECTORY = "/ManufacturingLexiconRepository/";
	
	/**
	 * 
	 */
	private final static String CLASSIFICATION_CORRECTION_DIRECTORY = "/ClassificationCorrectionRepository/";
	
	
	/**
	 * 
	 */
	private final static String MAPPING_BASIC_INFO_DIRECTORY = "/MappingInformationRepository/BasicInfo/";
	
	/**
	 * 
	 */
	private final static String MAPPING_DETAIL_INFO_DIRECTORY = "/MappingInformationRepository/DetailInfo/";
	
	
	private final static String REPOSITORIES_DIRECTORY_NAME = "/Repositories";

	public static String getOntologyIndexFilesDirectoryFullPath() {
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.ONTOLOGY_INDEX_FILES_DIRECTORY;
		return projectDir + storageDir;
	}
	
	public static String getClassificationCorrectionDirectoryFullPath(){
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.CLASSIFICATION_CORRECTION_DIRECTORY;
		return projectDir + storageDir;
	}

	public static String getManufacturingLexiconDirectoryFullPath() {
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.MANUFACTURING_LEXICON_DIRECTORY;
		return projectDir + storageDir;
	}

	public static String getTripleRepositoryDirectoryFullPath() {
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.TRIPLE_REPOSITORY_DIRECTORY;
		return projectDir + storageDir;
	}

	public static String getMappingBasicInfoDirectoryFullPath() {
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.MAPPING_BASIC_INFO_DIRECTORY;
		return projectDir + storageDir;
	}

	public static String getMappingDetailinfoDirectoryFullPath() {
		String projectDir = RepositoryParameterConfiguration.REPOSITORIES_DIRECTORY_FULL_PATH + REPOSITORIES_DIRECTORY_NAME;
		String storageDir = RepositoryParameterConfiguration.MAPPING_DETAIL_INFO_DIRECTORY;
		return projectDir + storageDir;
	}
}
