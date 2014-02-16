package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import umbc.ebiquity.kang.ontologyinitializator.mappingframework.rule.RuleEngine;
import umbc.ebiquity.kang.ontologyinitializator.ontology.OntoClassInfo;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceBasicRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IConcept2OntClassMapping;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IOntologyRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IProprietoryClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IUpdatedInstanceRecord;

public class MappingDataGateway {

	private IManufacturingLexicalMappingRepository _manufacturingLexicalMappingRepository;
	private IClassificationCorrectionRepository _classificationCorrectionRepository;
	private IProprietoryClassifiedInstancesRepository _proprietoryClassifiedInstanceRepository;
	
	public MappingDataGateway(
							  IProprietoryClassifiedInstancesRepository classifiedInstanceRepository,
		                      IClassificationCorrectionRepository classificationCorrectionRepository,
			                  IManufacturingLexicalMappingRepository manufacturingLexicalMappingRepository
			                  ) {
		this._proprietoryClassifiedInstanceRepository = classifiedInstanceRepository;
		this._manufacturingLexicalMappingRepository = manufacturingLexicalMappingRepository;
		this._classificationCorrectionRepository = classificationCorrectionRepository;
	}
	
	public void updateMappingInfo(Collection<IUpdatedInstanceRecord> instances){

//		StringBuilder correctedInstancesInfo = new StringBuilder();
		for (IUpdatedInstanceRecord updatedInstance : instances) {
			System.out.println("----------------------------------------");
			String origInstanceName = updatedInstance.getOriginalInstanceName();
			IClassifiedInstanceDetailRecord originalInstance = this._proprietoryClassifiedInstanceRepository.getClassifiedInstanceDetailRecordByInstanceName(origInstanceName);
			_proprietoryClassifiedInstanceRepository.updateInstance(updatedInstance);
			_classificationCorrectionRepository.extractCorrection(updatedInstance, originalInstance);
			_manufacturingLexicalMappingRepository.updateValidityOfConcept2OntClassMapping(updatedInstance, originalInstance);
		}
		
//		System.out.println(correctedInstancesInfo.toString());
	}
	
	public IUpdatedInstanceRecord createInstanceClassificationRecord(){
		return new UpdatedInstanceRecord();
	}

	public boolean saveRepository() {
		boolean succeed1 = _proprietoryClassifiedInstanceRepository.saveRepository();
		boolean succeed2 = _classificationCorrectionRepository.saveRepository();
		boolean succeed3 = _manufacturingLexicalMappingRepository.saveRepository();
		return succeed1 && succeed2 & succeed3;
	}

}
