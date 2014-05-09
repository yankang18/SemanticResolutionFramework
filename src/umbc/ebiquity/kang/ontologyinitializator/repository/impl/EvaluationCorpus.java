package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsAccessor;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IEvaluationCorpusRecordsReader;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;

public class EvaluationCorpus implements IEvaluationCorpusRecordsReader {

	private String _repositoryFullName;
	private IEvaluationCorpusRecordsAccessor _evaluationCorpus;
	public EvaluationCorpus(String fileFullName, IOntologyRepository ontologyRepository,
			                IEvaluationCorpusRecordsAccessor evaluationCorpus) {
		this._repositoryFullName = fileFullName;
		this._evaluationCorpus = evaluationCorpus;
	}

	public boolean loadRepository() {
		System.out.println("Load Manufacturing Lexicon Repository");
		return loadRecords(_repositoryFullName);
	}

//	private boolean loadRecords(String fileFullName) {
//
//		File file = new File(fileFullName);
//		BufferedReader reader = null;
//		try {
//			String line;
//			reader = new BufferedReader(new FileReader(file));
//			while ((line = reader.readLine()) != null) {
//				this.loadRecord(line);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
//		return true;
//	}

	
	private boolean loadRecords(String fileFullName) {

		File file = new File(fileFullName);
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
	
			String preRecordType = "None";
			String instance = "";
			while ((line = reader.readLine()) != null) {
				// this.loadRecord(line);

				if (this.isBlank(line) || line.contains("==")) {
					preRecordType = "Blank";
					continue;
				}
				
				String[] tokens = line.split("	");
				String type = tokens[0].trim();
					if ("R".equals(type)) {
						preRecordType = "Relation";
//						System.out.println("@1 " + record);
						String relationLabel = trimBracket(tokens[1]);
						String propertyLabel = trimBracket(tokens[2]);
						_evaluationCorpus.addRelation2PropertyMapping(relationLabel, propertyLabel);
					} else if ("C".equals(type)) {
						preRecordType = "Classification";
//						System.out.println("@2 " + record);
						String instanceLabel = trimBracket(tokens[1]);
						String classLabel = trimBracket(tokens[2]);
						instance = instanceLabel;
						_evaluationCorpus.addClassifiedInstance(instanceLabel, classLabel);
					} else {
//						System.out.println("@3 " + record);
						String conceptLabel = trimBracket(tokens[0].trim());
						String classLabel = trimBracket(tokens[1].trim());
						if(preRecordType.equals("Classification")){
							_evaluationCorpus.addConcept2ClassMappingForInstance(instance, conceptLabel, classLabel);
							
						}
						_evaluationCorpus.addConcept2ClassMap(conceptLabel, classLabel);
					}
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
	
	private String trimBracket(String original){
		return original.substring(1, original.length() - 1);
	}
	
	
	@Override
	public String getClassLabelforInstance(String instanceLabel){
		return _evaluationCorpus.getClassLabelforInstance(instanceLabel);
	}
	
	@Override
	public String getPropertyLabelforRelation(String relationLabel){
		return _evaluationCorpus.getPropertyLabelforRelation(relationLabel);
	}
	
	@Override
	public List<String> getClassSet(String instanceLabel, String concept) {
		return _evaluationCorpus.getClassSet(instanceLabel, concept);
	}

	@Override
	public void showRecords() {
		_evaluationCorpus.showRecords();		
	}

	@Override
	public String getOntClassForConcept(String conceptLabel) {
		return _evaluationCorpus.getOntClassForConcept(conceptLabel);
	}

	@Override
	public boolean containsInstance(String instanceLabel) {
		return _evaluationCorpus.containsInstance(instanceLabel);
	}

	@Override
	public Set<String> getInstanceSet() {
		return _evaluationCorpus.getInstanceSet();
	}

	
}
