package umbc.ebiquity.kang.ontologyinitializator.repository;

public class MappingInfoSchemaParameter {

	/**
	 * Operating System specific line separator that separates lines in a file
	 */
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 *  schema for extracted triples stored as JSON
	 */
	public static String TRIPLE_RECORD_TYPE =  "Record_Type"; // It has three following types
	public static String TRIPLE_RECORD_TYPE_META_DATA = "Meta_Data";
	public static String TRIPLE_RECORD_TYPE_PROPERTY_MAPPING = "Property_Mapping";
	public static String TRIPLE_RECORD_TYPE_CONCEPT_OF_INSTANCE = "Concept_Of_Instance";
	public static String TRIPLE_STORE_URI = "Triple_Store_URI";
	public static String TRIPLE_STORE_NAME = "Triple_Store_Name";
	
	public static String TRIPLE_SUBJECT = "Subject";
	public static String TRIPLE_OBJECT = "Object";
	public static String TRIPLE_PREDICATE = "Predicate";
	public static String NORMALIZED_TRIPLE_SUBJECT = "N_Subject";
	public static String NORMALIZED_TRIPLE_OBJECT = "N_Object";
	public static String TRIPLE_PREDICATE_TYPE = "Predicate_Type";  //builtin or custom
	public static String IS_FROM_INSTANCE = "Is_From_Instance";
	
	/**
	 * schema for classified triples and mapped relations in files in JSON
	 */
	public static String BASIC_MAPPING_INFO_RECORD_TYPE = "Record_Type"; // It has two following types
	public static String CLASSIFICATION = "Classification";
	public static String RELATION_MAPPING = "Record_Mapping";
	public static String RELATION_NAME = "R_Name";
	public static String PROPERTY_URI = "P_URI";
	public static String PROPERTY_NAME = "P_Name";
	public static String PROPERTY_NAMESPACE = "P_NS";
	
	public static String CLASS_URI = "Cl_URI";
	public static String CLASS_NAME = "Cl_Name";
	public static String CLASS_NAMESPACE = "Cl_NS";
	public static String INSTANCE_URI = "Ins_URI";
	public static String INSTANCE_NAME = "Ins_Name";
	public static String OVERALL_SIMILARITY = "Overall_Similarity";
	public static String SIMILARITY = "Similarity";
	public static String RECOMMENDED_CLASS_LEVEL1 = "RCL1";
	public static String RECOMMENDED_CLASS_LEVEL2 = "RCL2";
	public static String RELATED_CONCEPT = "RC";
	public static String PROPERTY = "PT";
	
	
	public static String CONCEPT_NAME = "Concept_Name";
	public static String IS_MAPPED_CONCEPT = "isMappedConcept";
	public static String IS_HITTED_MAPPING = "isHittedMapping";
//	public static String CONCEPT_ONTOCLASS_RELATION = "Relation";
	
	/**
	 * 
	 */
	public static String MAPPED_ONTOCLASS_LIST = "MappedOntoClassList";
	public static String CONCEPT_CLASS_MAPPING_RELATION = "Mapping_Relation";
//    public static String MAPPING_ATTEMPTS = "Mapping_Attempts";
    public static String MAPPING_VERIFICATION_ATTEMPTS = "Mapping_Verication_Attempts";
    
    public static String VERICATION_SUCCEED_COUNTS = "Succeed_Counts";
    public static String VERICATION_UNDETERMINED_COUNTS = "Undetermined_Counts";
    public static String VERICATION_FAILED_COUNTS = "Failed_Counts";

	public static String MAPPED_CLASS_HIERARCHY = "Mapped_Class_Hierarchy";
    
    /**
     * 
     */
	public static String INSTANCE_SOURCE = "Instance_Source";
	public static String CORRECTION_SOURCE = "Correction_Source";
	public static String CORRECTION_TARGET = "Correction_Target";
	public static String CORRECTION_DIRECTION = "Correction_Direction";
	public static String HITTED_MAPPINGS = "Hitted_Mappings";
	public static String AMBIGUOUS_MAPPINGS = "Ambiguous_Mappings";
	public static String UNMAPPED_CONCEPTS = "UnMapping_Concepts";
	public static String MAPPED_CLASS_HIERARCHIES = "Mapped_Class_Hierarchies";
	public static String CLOSENESS_TO_HIERARCHY = "Closeness_to_hierarchy";

	public static String PREVENANCE_OF_INSTANCE = "Prevenance_of_Instance";
	public static String CORRECTION_TARGET_CLASS_HIERARCHY = "Correction_Target_Class_Hierarchy";
	public static String CORRECTION_TARGET_CLASS_INFO = "Correction_Target_Class";
	public static String TRIPLE_OBJECT_AS_CONCEPT_SCORE = "Triple_Object_As_Concept_Score";
	public static String CONCEPT_CLASS_MAPPING = "Concept_Class_Mapping";
	public static String MAPPING_SCORE = "Mapping_Score"; 
    
	public enum MappingRelationType {
		relatedTo, narrower, broader
	}

	public static MappingRelationType getMappingRelationType(String relationString) {
		if (MappingRelationType.relatedTo.toString().equalsIgnoreCase(relationString)) {
			return MappingRelationType.relatedTo;
		} else if (MappingRelationType.narrower.toString().equalsIgnoreCase(relationString)) {
			return MappingRelationType.narrower;
		} else if (MappingRelationType.broader.toString().equalsIgnoreCase(relationString)) {
			return MappingRelationType.broader;
		} else {
			return MappingRelationType.relatedTo;
		}
	}

}
