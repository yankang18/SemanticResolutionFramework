package umbc.ebiquity.kang.entityframework.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import umbc.ebiquity.kang.websiteparser.IWebPagePath;
import umbc.ebiquity.kang.websiteparser.object.LeafNode;
import umbc.ebiquity.kang.websiteparser.object.LeafNode.LeafType;

public class EntityPath {

	private IWebPagePath path;
	private Collection<Entity> entityPath;
	private LeafNode leafNode;
	private String topDownPathString;
	private String bottomUpPathString;

	public EntityPath(IWebPagePath webPagePath, LeafNode leafNode) {
		this.path = webPagePath;
		this.leafNode = leafNode;
	}

	public IWebPagePath getWebPagePath() {
		return path;
	}

	public String getWebPageUrl() {
		return path.getHost();
	}

	public void addEntities(Collection<Entity> conceptPath) {
		this.entityPath = conceptPath;
	}

	public Collection<Entity> getEntities() {
		return this.entityPath;
	}

	public int getNumberOfEntities() {
		return this.entityPath.size();
	}

	public LeafNode getLeafNode() {
		return leafNode;
	}

	public String getLeafNodeContent() {
		return leafNode.getNodeContent().trim();
	}

	public String printPathTopDown() {

		if (topDownPathString == null) {
			StringBuilder pathSB = new StringBuilder();
			List<Entity> termList = new ArrayList<Entity>();
			termList.addAll(entityPath);
			int size = termList.size();
			for (int i = size - 1; i >= 0; i--) {
				String termLabel = termList.get(i).getPresentingTermLabel().trim();
				if (!"".equals(termLabel)) {
					pathSB.append("[" + termLabel + " <" + termList.get(i).getCombinedScoreString() + ">" + "]/");
				}
			}
			LeafType leafType = leafNode.getLeafType();
			if (LeafType.Term == leafType) {
				pathSB.append("[" + leafNode.getNodeContent() + "]");
			} else {
				pathSB.append("{" + leafNode.getNodeContent() + "}");
			}
			topDownPathString = pathSB.toString();
		}
		return this.topDownPathString;
	}

	public String printPathBottomUp() {

		if (bottomUpPathString == null) {
			StringBuilder pathSB = new StringBuilder();
			LeafType leafType = leafNode.getLeafType();
			if (LeafType.Term == leafType) {
				pathSB.append("[" + leafNode.getNodeContent() + "]/");
			} else {
				pathSB.append("{" + leafNode.getNodeContent() + "}/");
			}
			for (Entity term : this.entityPath) {
				String termLabel = term.getPresentingTermLabel().trim();
				if (!"".equals(termLabel)) {
					pathSB.append("[" + termLabel + " <" + term.getCombinedScoreString() + ">" + "]/");
				}
			}
			String tempPathString = pathSB.toString();
			bottomUpPathString = tempPathString.substring(0, tempPathString.length() - 1);
		}
		return this.bottomUpPathString;
	}

	@Override
	public int hashCode() {
		return this.printPathBottomUp().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		EntityPath conceptPath = (EntityPath) obj;
		return this.printPathBottomUp().equals(conceptPath.printPathBottomUp());
	}

}
