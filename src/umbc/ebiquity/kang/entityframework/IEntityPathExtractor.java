package umbc.ebiquity.kang.entityframework;

import java.net.URL;
import java.util.Collection;

import umbc.ebiquity.kang.entityframework.object.EntityPath;

public interface IEntityPathExtractor {
	public Collection<EntityPath> extractor();
	public URL getWebSiteURL();
}
