package umbc.ebiquity.kang.entityframework.object;

import java.util.Comparator;

import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;

public class EntityPathSorterByLeafNodeText implements Comparator<EntityPath>{

	@Override
	public int compare(EntityPath path1, EntityPath path2) {
		String leafNodeLabel1 = getProcessedTermLabel(path1.getLeafNodeContent());
		String leafNodeLabel2 = getProcessedTermLabel(path2.getLeafNodeContent());
		return leafNodeLabel1.compareTo(leafNodeLabel2);
	}
	
	private String getProcessedTermLabel(String termLabel) {
		String[] tokens = TextProcessingUtils.tokenizeLabel(TextProcessingUtils.removeStopwords(termLabel.trim().toLowerCase()));
		StringBuilder processedTermLabelSB = new StringBuilder();
		for (String token : tokens) {
			processedTermLabelSB.append(token);
		}
		return processedTermLabelSB.toString().trim();
	}

}