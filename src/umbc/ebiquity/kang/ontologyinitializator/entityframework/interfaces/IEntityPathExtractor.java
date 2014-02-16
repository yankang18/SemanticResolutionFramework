package umbc.ebiquity.kang.ontologyinitializator.entityframework.interfaces;

import java.net.URL;
import java.util.Collection;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.EntityPath;

public interface IEntityPathExtractor {
	public Collection<EntityPath> constructEntityPaths();
	public URL getWebSiteURL();
}
