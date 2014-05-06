package umbc.ebiquity.kang.ontologyinitializator.mappingframework.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.xml.serialize.LineSeparator;

import umbc.ebiquity.kang.ontologyinitializator.repository.FileAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.RepositoryParameterConfiguration;

public class EvaluationResult {

	private double _overallScore;
	private double _totalError;
	private int _numberOfCorrection;
	private int _numberOfRecall;
	private List<EvaluationRecord> _evaluationRecordList;
	
	public EvaluationResult(){
		_evaluationRecordList = new ArrayList<EvaluationRecord>();
	}
	
	public void addEvaluationRecord(EvaluationRecord evaluationRecord) {
		this._evaluationRecordList.add(evaluationRecord);
	}

	public void setOverallScore(double overallScore) {
		this._overallScore = overallScore;
	}

	public double getOverallScore() {
		return this._overallScore;
	}
	
	public List<EvaluationRecord> getEvaluationRecordList(){
		return this._evaluationRecordList;
	}

	public void outputEvaluationResult(String fileFullName) {
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
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
				stringBuilder2.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
				correctionCount++;
			} else {
				stringBuilder1.append(recordStr);
				stringBuilder1.append(RepositoryParameterConfiguration.LINE_SEPARATOR); 
			}
			
		}
		stringBuilder.append(stringBuilder1.toString());
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append(stringBuilder2.toString());
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Number Of Recall: " + _numberOfRecall);
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Number Of Correction: " + correctionCount);
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Overall Score: " + _overallScore);
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Overall Of Error: " + _totalError);
		stringBuilder.append(RepositoryParameterConfiguration.LINE_SEPARATOR);
		stringBuilder.append("Average Error: " + _totalError / (double) _numberOfRecall);
		
		
		FileAccessor.saveTripleString(fileFullName, stringBuilder.toString());
	}

	public void setTotalError(double totalError) { 
		_totalError = totalError;
	}

	public void setNumberOfCorrections(int countOfCorrections) { 
		_numberOfCorrection = countOfCorrections;
	}
	
	public void setNumberOfRecall(int countOfRecall) {
		_numberOfRecall = countOfRecall;
	}
	
	public double getTotalError(){
		return this._totalError;
	}
	
	public int getNumberOfCorrections(){
		return this._numberOfCorrection;
	}

	public int getNumberOfRecall(){
		return this._numberOfRecall;
	}

}
