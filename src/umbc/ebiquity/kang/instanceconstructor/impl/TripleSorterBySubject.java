package umbc.ebiquity.kang.instanceconstructor.impl;

import java.util.Comparator;

public class TripleSorterBySubject implements Comparator<Triple>{

	@Override
	public int compare(Triple triple1, Triple triple2) {
		return triple1.getProcessedSubjectLabel().compareTo(triple2.getProcessedSubjectLabel());
	}

}
