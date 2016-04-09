package umbc.ebiquity.kang.ontologyinitializator.entityframework;

import java.net.URL;
import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityPath;

public interface IEntityPathExtractor {
	public Collection<EntityPath> extractor();
	public URL getWebSiteURL();
}
