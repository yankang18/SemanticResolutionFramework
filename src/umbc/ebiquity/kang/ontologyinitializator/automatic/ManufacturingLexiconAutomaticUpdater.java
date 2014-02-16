package umbc.ebiquity.kang.ontologyinitializator.automatic;


public class ManufacturingLexiconAutomaticUpdater {
	
//	public void update(){
//		MappingDetailInfo mappingDetailInfo = _mappingInfoRepository.getMappingDetailInfo();
//		Collection<IClassifiedInstanceDetailInfo> instanceDetailInfoCollection = mappingDetailInfo
//				.getClassifiedInstanceDetailInfoCollection();
//		
//		Collection<IUpdatedInstanceRecord> updatedInstanceRecords = new ArrayList<IUpdatedInstanceRecord>();
//		for (IClassifiedInstanceDetailInfo instanceDetailInfo : instanceDetailInfoCollection) {
//			IUpdatedInstanceRecord instanceRecordSetter = _mappingInfoRepository.createInstanceClassificationRecord();
//			updatedInstanceRecords.add(instanceRecordSetter);
//			String origInstanceName = instanceDetailInfo.getInstanceLabel();
//			String origOntoClassName = instanceDetailInfo.getOntoClassName();
//			String origOntoClassNS = instanceDetailInfo.getOntoClassNameSpace();
//			String origOntoClassURI = instanceDetailInfo.getOntoClassURI();
//			instanceRecordSetter.setOriginalInstanceName(origInstanceName);
//			instanceRecordSetter.setOriginalClassName(origOntoClassName);
//			instanceRecordSetter.setUpdatedInstanceName(origInstanceName);
//			instanceRecordSetter.setUpdatedClassName(origOntoClassName);
//			instanceRecordSetter.isUpdatedInstance(false);
//
//			for (IConcept2OntClassMapping concept2OntClassMappingPair : instanceDetailInfo.getConcept2OntClassMappingPairs()) {
//				String conceptName = concept2OntClassMappingPair.getConceptName();
//				Concept concept = new Concept(conceptName);
//				if (concept2OntClassMappingPair.isMappedConcept()) {
//					OntoClassInfo mappedOntClass = concept2OntClassMappingPair.getMappedOntoClass();
//					String className = mappedOntClass.getOntClassName();
//					String classNS = mappedOntClass.getNameSpace();
//					String classURI = mappedOntClass.getURI();
//					double score = concept2OntClassMappingPair.getMappingScore();
//					MappingRelationType relation = concept2OntClassMappingPair.getRelation();
//					OntoClassInfo ontClass = new OntoClassInfo(classURI, classNS, className);
//					instanceRecordSetter.addConcept2OntClassMappingPair(concept, relation, ontClass, score);
//				} else {
//					MappingRelationType relation = MappingRelationType.relatedTo;
//					OntoClassInfo ontClass = new OntoClassInfo(origOntoClassURI, origOntoClassNS, origOntoClassName);
//					OntoClassInfo topLevelClass = _ontologyRepository.getTopLevelClass(ontClass);
//					instanceRecordSetter.addConcept2OntClassMappingPair(concept, relation, topLevelClass, 8.0);
//				}
//			}
//		}
//		
//		_mappingInfoRepository.updateMappingInfo(updatedInstanceRecords);
//	}

}
