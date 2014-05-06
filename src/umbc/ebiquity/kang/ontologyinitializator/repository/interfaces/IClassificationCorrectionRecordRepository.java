package umbc.ebiquity.kang.ontologyinitializator.repository.interfaces;

import java.util.Collection;

public interface IClassificationCorrectionRecordRepository {

	public void addClassificationCorrection(IClassificationCorrection correction);

	public void addExplicitInstanceClassificationEvidence(IInstanceClassificationEvidence evidence);

	public void addHiddenInstanceClassificationEvidence(IInstanceClassificationEvidence evidence);

	public void addNumberOfInstance(int numberOfInstance);


}
