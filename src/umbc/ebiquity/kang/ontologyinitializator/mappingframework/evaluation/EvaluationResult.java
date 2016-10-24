package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;

public class EvaluationResult {

	private double _totalError;
	private double _totalScore;
	private int _numberOfCorrection;
	private double _precision;
	private double _recall;
	private double _fmeasure;
	private double _correctionRate;
	private Set<String> _unclassifiedInstances; 
	private List<EvaluationRecord> _evaluationRecordList;
	private double _numberOfAllInstances;
	private double _numberOfClassifiedInstances;
	
	public EvaluationResult(){
		_evaluationRecordList = new ArrayList<EvaluationRecord>();
	}
	
	public void addEvaluationRecord(EvaluationRecord evaluationRecord) {
		this._evaluationRecordList.add(evaluationRecord);
	}

	public void setPrecision(double precision) {
		this._precision = precision;
	}

	public List<EvaluationRecord> getEvaluationRecordList(){
		return this._evaluationRecordList;
	}

	public void outputEvaluationResult(String fileFullName) {
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
		StringBuilder stringBuilder3 = new StringBuilder();
		int correctionCount = 0;
		for (EvaluationRecord record : _evaluationRecordList) {
			record.getEvaluationRecordType();
			String entityLabel = record.getEntityLabel();
			String expectedValue = record.getExpectedValue();
			String realValue = record.getRealValue();
			double score = record.getScore();
			String recordStr = "instance: <"  + entityLabel + ">, expected class: <" + expectedValue + ">, real class: <" + realValue + ">, score: <" + score +">";
			if (score < 1.0) {
				stringBuilder2.append(recordStr);
				stringBuilder2.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
				correctionCount++;
			} else {
				stringBuilder1.append(recordStr);
				stringBuilder1.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR); 
			}
			
		}
		
		
		for(String unclassifiedInstance : this._unclassifiedInstances){
			stringBuilder3.append(unclassifiedInstance);
			stringBuilder3.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR); 
		}
		
		stringBuilder.append("========= CORRECT CLASSIFIED INSTANCES ===========");
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(stringBuilder1.toString());
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("========= INCORRECT CLASSIFIED INSTANCES =========");
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(stringBuilder2.toString());
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("========= UNCLASSIFIED INSTANCES =========");
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(stringBuilder3.toString());
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("========= STASTICS ==========");
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Number of All Instances: " + _numberOfAllInstances);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Number of Classified instances: " + _numberOfClassifiedInstances);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Overall Score: " + _totalScore);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Overall Of Error: " + _totalError);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Recall: " + _recall);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Precision: " + _precision);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Fmeasure: " + _fmeasure);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Number Of Correction: " + _numberOfCorrection);
		stringBuilder.append(FileRepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Correction Rate: " + _correctionRate);
		
		
		FileAccessor.saveTripleString(fileFullName, stringBuilder.toString());
	}

	public void setTotalScore(double totalScore) {
		_totalScore = totalScore;
	}
	
	public void setTotalError(double totalError) { 
		_totalError = totalError;
	}

	public void setRecall(double recall) { 
		_recall = recall;
	}
	
	public void setCorrectionRate(double correctionRate) {
		this._correctionRate = correctionRate;
	}

	public void setNumberOfCorrections(int countOfCorrections) { 
		_numberOfCorrection = countOfCorrections;
	}

	public void setUnclassifiedInstances(Set<String> unclassifiedInstances) {
		_unclassifiedInstances = unclassifiedInstances;
	}
	
	public double getTotalScore(){
		return this._totalScore;
	}
	public double getTotalError(){
		return this._totalError;
	}
	
	public double getPrecision() {
		return this._precision;
	}
	
	public double getRecall(){
		return this._recall;
	}
	
	public double getCorrectionRate(){
		return this._correctionRate;
	}
	
	public int getNumberOfCorrections(){
		return this._numberOfCorrection;
	}
	
	public Set<String> getUnclassifiedInstances(){
		return this._unclassifiedInstances;
	}

	public void setFmeasure(double fmeasure) {
		this._fmeasure = fmeasure;
	}

	public void setNumberOfAllInstances(double numberOfAllInstances) {
		 this._numberOfAllInstances = numberOfAllInstances; 
	}

	public void setNumberOfClassifiedInstances(double numberOfClassifiedInstances) {
		this._numberOfClassifiedInstances = numberOfClassifiedInstances;
	}
}
