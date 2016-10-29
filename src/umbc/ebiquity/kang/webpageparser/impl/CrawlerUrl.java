package umbc.ebiquity.kang.webpageparser.impl;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * encapsulates the URL visited by the crawler.
 * */
public class CrawlerUrl {

	private int depth = 0;
	private String urlString = null;
	private URL url = null;
	private boolean isAllowedToVisit = true;
	private boolean isVisited = false;
	private boolean isCheckedForPermission = false;

	public CrawlerUrl(String urlString, int depth) {
		this.depth = depth;
		this.urlString = urlString;
		this.computeURL();
	}

	public boolean isVisited() {
		return this.isVisited;
	}

	public boolean isCheckedForPermission() {
		return this.isCheckedForPermission;
	}

	public boolean isAllowedToVisit() {
		return this.isAllowedToVisit;
	}

	public URL getURL() {
		return this.url;
	}

	public int getDepth() {
		return this.depth;
	}

	public String getUrlString() {
		return this.urlString;
	}

	public void setAllowedToVisit(boolean isAllowToVisit) {
		this.isAllowedToVisit = isAllowToVisit;
		this.isCheckedForPermission = true;
	}

	public void setIsVisited() {
		this.isVisited = true;
	}

	private void computeURL() {
		try {
			url = new URL(this.urlString);
		} catch (MalformedURLException e) {
			// something is wrong with the urlString.
		}
	}

	@Override
	public String toString() {
		return this.urlString + " [depth=" + depth + " visit="
				+ this.isAllowedToVisit + " check = "
				+ this.isCheckedForPermission + "]";
	}
	
	@Override
	public int hashCode(){
		return this.urlString.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		// Is this really good ??!!
		return this.hashCode() == obj.hashCode();
	}

}
