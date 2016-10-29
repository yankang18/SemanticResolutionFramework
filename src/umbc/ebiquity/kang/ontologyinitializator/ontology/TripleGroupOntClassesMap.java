package umbc.ebiquity.kang.ontologyinitializator.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import umbc.ebiquity.kang.instanceconstructor.impl.InstanceTripleSet;
import umbc.ebiquity.kang.ontologyinitializator.repository.impl.Concept2OntClassMapping;

public class TripleGroupOntClassesMap {
	
	
	private InstanceTripleSet tripleCluster;
	private List<Concept2OntClassMapping> matchedOntClassCollection;
	private boolean isSorted = false;
	
	public TripleGroupOntClassesMap(InstanceTripleSet tripleCluster){
		this.tripleCluster = tripleCluster;
		matchedOntClassCollection = new ArrayList<Concept2OntClassMapping>();
	}
	
//	public void addMatchedOntClass(OntClassInfo ontClassInfo, List<MatchedOntPropertyPair> matchedOntPropertyPairList, double similarity){
//		MatchedOntClassWrapper matchedOntClass = new MatchedOntClassWrapper(ontClassInfo, matchedOntPropertyPairList, similarity);
//		matchedOntClassCollection.add(matchedOntClass);
//		this.isSorted = false;
//	}
	
	private void sortMatchedOntClasses(){
		Collections.sort(matchedOntClassCollection);
		this.isSorted = true;
	}

	public Concept2OntClassMapping getBestMatchedOntClass() {
		if (!this.hasMatchedOntClass())
			return null;
		
		if (!this.isSorted) {
			this.sortMatchedOntClasses();
		}
		return this.matchedOntClassCollection.get(0); 
	}

	public boolean hasMatchedOntClass() {
		if (this.matchedOntClassCollection.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
