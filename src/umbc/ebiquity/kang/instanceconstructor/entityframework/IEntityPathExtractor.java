package umbc.ebiquity.kang.instanceconstructor.entityframework;

import java.net.URL;
import java.util.Collection;

import umbc.ebiquity.kang.instanceconstructor.entityframework.object.EntityPath;

public interface IEntityPathExtractor {
	public Collection<EntityPath> extractor();
	public URL getWebSiteURL();
}
