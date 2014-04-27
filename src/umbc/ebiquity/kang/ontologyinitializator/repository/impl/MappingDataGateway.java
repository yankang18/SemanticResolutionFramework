package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassificationCorrectionRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstanceDetailRecord;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IManufacturingLexicalMappingRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IClassifiedInstancesRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IInstanceRecord;

public class MappingDataGateway {

	private IManufacturingLexicalMappingRepository _manufacturingLexicalMappingRepository;
	private IClassificationCorrectionRepository _classificationCorrectionRepository;
	private IClassifiedInstancesRepository _proprietoryClassifiedInstanceRepository;
	
	public MappingDataGateway(
							  IClassifiedInstancesRepository classifiedInstanceRepository,
		                      IClassificationCorrectionRepository classificationCorrectionRepository,
			                  IManufacturingLexicalMappingRepository manufacturingLexicalMappingRepository
			                  ) {
		this._proprietoryClassifiedInstanceRepository = classifiedInstanceRepository;
		this._manufacturingLexicalMappingRepository = manufacturingLexicalMappingRepository;
		this._classificationCorrectionRepository = classificationCorrectionRepository;
	}
	
	public void updateMappingInfo(Collection<IInstanceRecord> instances){

//		StringBuilder correctedInstancesInfo = new StringBuilder();
		for (IInstanceRecord updatedInstance : instances) {
			System.out.println("----------------------------------------");
			String origInstanceName = updatedInstance.getOriginalInstanceName();
			IClassifiedInstanceDetailRecord originalInstance = this._proprietoryClassifiedInstanceRepository.getClassifiedInstanceDetailRecordByInstanceName(origInstanceName);
			_proprietoryClassifiedInstanceRepository.updateInstance(updatedInstance);
			_classificationCorrectionRepository.extractCorrection(updatedInstance, originalInstance);
			_manufacturingLexicalMappingRepository.updateValidityOfConcept2OntClassMapping(updatedInstance, originalInstance);
		}
//		System.out.println(correctedInstancesInfo.toString());
	}
	
	public IInstanceRecord createInstanceClassificationRecord(){
		return new UpdatedInstanceRecord();
	}

	public boolean saveRepository() {
		boolean succeed1 = _proprietoryClassifiedInstanceRepository.saveRepository();
		boolean succeed2 = _classificationCorrectionRepository.saveRepository();
		boolean succeed3 = _manufacturingLexicalMappingRepository.saveRepository();
		return succeed1 && succeed2 & succeed3;
	}

}
