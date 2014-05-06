package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

public class EvaluationRecord {
	public enum EvaluationRecordType{CLASSIFICATION, RELATION_PROPERTY_MAPPING}
	
	private String _instanceLabel;
	private String _expectedValue; 
	private String _realValue;
	private double _score;
	private EvaluationRecordType _evaluationRecordType;
	
	public EvaluationRecord(EvaluationRecordType evaluationRecordType, String entityLabel, String expectedValue, String realValue, double score){
		this._evaluationRecordType = evaluationRecordType;
		this._instanceLabel = entityLabel;
		this._expectedValue = expectedValue;
		this._realValue = realValue;
		this._score = score;
	}
	
	public String getEntityLabel(){
		return this._instanceLabel;
	}
	
	public String getExpectedValue(){
		return this._expectedValue;
	}
	
	public String getRealValue(){
		return this._realValue;
	}
	
	public double getScore(){
		return this._score;
	}
	
	public EvaluationRecordType getEvaluationRecordType(){
		return this._evaluationRecordType;
	}
	
}
