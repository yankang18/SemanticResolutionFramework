package umbc.ebiquity.kang.websiteparser.object;

import java.util.HashSet;
import java.util.Set;

public class HTMLTags {
	
	
	private static Set<String> ignoredTags;
	private static Set<String> emphasisTags;
	private static Set<String> eliminatedTages;
	private static Set<String> topicTags;
	
	private static Set<String> linkTags;
	private static Set<String> appearanceTags;
	private static Set<String> imgTags;
	private static Set<String> formTags;
	
	private static Set<String> listTags;
	private static Set<String> tableTags;
	private static Set<String> blockTags;
	
	private static Set<String> headerTags;
	
	
	static {
		
		eliminatedTages = new HashSet<String>();
		ignoredTags = new HashSet<String>();
		emphasisTags = new HashSet<String>();
		topicTags = new HashSet<String>();
		linkTags = new HashSet<String>();
		
		imgTags = new HashSet<String>();
		formTags = new HashSet<String>();
		listTags = new HashSet<String>();
		tableTags = new HashSet<String>();
		blockTags = new HashSet<String>();
		headerTags = new HashSet<String>();
		
		
		
		/*
		 * 
		 */
		// meta info related tags
		eliminatedTages.add("link");
		eliminatedTages.add("meta");
		eliminatedTages.add("style");
		eliminatedTages.add("stript");
		eliminatedTages.add("nostript");
		eliminatedTages.add("pre");
		eliminatedTages.add("bdo");
		eliminatedTages.add("code");
		eliminatedTages.add("del");
		eliminatedTages.add("var");
		
		eliminatedTages.add("LINK");
		eliminatedTages.add("META");
		eliminatedTages.add("STYLE");
		eliminatedTages.add("STRIPT");
		eliminatedTages.add("NOSTRIPT");
		eliminatedTages.add("PRE");
		eliminatedTages.add("BDO");
		eliminatedTages.add("CODE");
		eliminatedTages.add("DEL");
		eliminatedTages.add("VAR");
		
		// form related tags
		eliminatedTages.add("form");
		eliminatedTages.add("input");
		eliminatedTages.add("textarea");
		eliminatedTages.add("select");
		eliminatedTages.add("option");
		eliminatedTages.add("optgroup");
		eliminatedTages.add("button");
		eliminatedTages.add("label");
		eliminatedTages.add("fieldset");
		eliminatedTages.add("legend");
		eliminatedTages.add("br");
		
		eliminatedTages.add("FORM");
		eliminatedTages.add("INPUT");
		eliminatedTages.add("TEXTAREA");
		eliminatedTages.add("SELECT");
		eliminatedTages.add("OPTION");
		eliminatedTages.add("OPTGROUP");
		eliminatedTages.add("BUTTON");
		eliminatedTages.add("LABEL");
		eliminatedTages.add("FIELDSET");
		eliminatedTages.add("LEGEND");
		eliminatedTages.add("BR");
		
		// img related tags
//		eliminatedTages.add("img"); 
//		eliminatedTages.add("area");
//		eliminatedTages.add("map");
//		eliminatedTages.add("object");
//		eliminatedTages.add("param");
//		
//		eliminatedTages.add("IMG"); 
//		eliminatedTages.add("AREA");
//		eliminatedTages.add("MAP");
//		eliminatedTages.add("OBJECT");
//		eliminatedTages.add("PARAM");
		
		/*
		 * 
		 */
//		formTags.add("form");
		formTags.add("input");
		formTags.add("textarea");
		formTags.add("select");
		formTags.add("option");
		formTags.add("optgroup");
		formTags.add("button");
		formTags.add("label");
		formTags.add("fieldset");
		formTags.add("legend");
		
//		formTags.add("FORM");
		formTags.add("INPUT");
		formTags.add("TEXTAREA");
		formTags.add("SELECT");
		formTags.add("OPTION");
		formTags.add("OPTGROUP");
		formTags.add("BUTTON");
		formTags.add("LABEL");
		formTags.add("FIELDSET");
		formTags.add("LEGEND");

		/*
		 * 
		 */
		imgTags.add("img");
		imgTags.add("area");
		imgTags.add("map");
		imgTags.add("object");
		imgTags.add("param");
		
		imgTags.add("IMG");
		imgTags.add("AREA");
		imgTags.add("MAP");
		imgTags.add("OBJECT");
		imgTags.add("PARAM");
		
		/*
		 * 
		 */
		
		// appearance related tags
		ignoredTags.add("tt");
		ignoredTags.add("sub");
		ignoredTags.add("sup");
		ignoredTags.add("small");
		ignoredTags.add("i");
		ignoredTags.add("cite");
		ignoredTags.add("ins");
		ignoredTags.add("kdb");
		ignoredTags.add("sample");
		ignoredTags.add("hr");
//		ignoredTags.add("br");
		
		ignoredTags.add("TT");
		ignoredTags.add("SUB");
		ignoredTags.add("SUP");
		ignoredTags.add("SMALL");
		ignoredTags.add("I");
		ignoredTags.add("CITE");
		ignoredTags.add("INS");
		ignoredTags.add("KDB");
		ignoredTags.add("SAMPLE");
		ignoredTags.add("HR");
//		ignoredTags.add("BR");
		
		
		/*
		 * 
		 */
		
		//
		topicTags.add("caption");
		topicTags.add("strong");
		topicTags.add("em");
		topicTags.add("b");
		topicTags.add("h1");
		topicTags.add("h2");
		topicTags.add("h3");
		topicTags.add("h4");
		topicTags.add("h5");
		topicTags.add("h6");
//		topicTags.add("a");
		
		topicTags.add("CAPTION");
		topicTags.add("STRONG");
		topicTags.add("EM");
		topicTags.add("B");
		topicTags.add("H1");
		topicTags.add("H2");
		topicTags.add("H3");
		topicTags.add("H4");
		topicTags.add("H5");
		topicTags.add("H6");
//		topicTags.add("A");
		
		//
		headerTags.add("h1");
		headerTags.add("h2");
		headerTags.add("h3");
		headerTags.add("h4");
		headerTags.add("h5");
		headerTags.add("h6");
		
		headerTags.add("H1");
		headerTags.add("H2");
		headerTags.add("H3");
		headerTags.add("H4");
		headerTags.add("H5");
		headerTags.add("H6");
		
		//
		emphasisTags.add("strong");
		emphasisTags.add("em");
		emphasisTags.add("b");
		emphasisTags.add("h1");
		emphasisTags.add("h2");
		emphasisTags.add("h3");
		emphasisTags.add("h4");
		emphasisTags.add("h5");
		emphasisTags.add("h6");
		
		emphasisTags.add("STRONG");
		emphasisTags.add("EM");
		emphasisTags.add("B");
		emphasisTags.add("H1");
		emphasisTags.add("H2");
		emphasisTags.add("H3");
		emphasisTags.add("H4");
		emphasisTags.add("H5");
		emphasisTags.add("H6");
		
		//
		linkTags.add("a");
		linkTags.add("A");
		
		//
		listTags.add("ul");
		listTags.add("ol");
		listTags.add("li");
		
		listTags.add("UL");
		listTags.add("OL");
		listTags.add("LI");
		
		//
		tableTags.add("table");
		tableTags.add("tbody");
		tableTags.add("thead");
		tableTags.add("tfoot");
		tableTags.add("tr");
		tableTags.add("td");
		tableTags.add("th");
		
		tableTags.add("TABLE");
		tableTags.add("TBODY");
		tableTags.add("THEAD");
		tableTags.add("TFOOT");
		tableTags.add("TR");
		tableTags.add("TD");
		tableTags.add("TH");
		
		//
		blockTags.add("p");
		blockTags.add("blockquote");
//		blockTags.add("span");
		
		blockTags.add("P");
		blockTags.add("BLOCKQUOTE");
//		blockTags.add("SPAN");
		
	}
	
	public static Set<String> getIgnoredTags(){
		return ignoredTags;
	}
	
	public static Set<String> getEliminatedTags(){
		return eliminatedTages;
	}
	
	public static Set<String> getEmphasisTages(){
		return emphasisTags;
	}
	
	public static Set<String> getTopicTags(){
		return topicTags;
	}
	
	public static Set<String> getLinkTags(){
		return linkTags;
	}
	
	public static Set<String> getListTags(){
		return listTags;
	}
	
	public static Set<String> getTableTags(){
		return tableTags;
	}
	
	public static Set<String> getFormTags(){
		return formTags;
	}
	
	public static Set<String> getImageTags(){
		return imgTags;
	}
	
	public static Set<String> getBlockTags(){
		return blockTags;
	}
	
	public static Set<String> getHeaderTags(){
		return headerTags;
	}
	
	public static int getIntegerForHeaderTag(String headerTag) {
		headerTag = headerTag.toLowerCase();
		if ("h1".equals(headerTag)) {
			return 6;
		} else if ("h2".equals(headerTag)) {
			return 5;
		} else if ("h3".equals(headerTag)) {
			return 4;
		} else if ("h4".equals(headerTag)) {
			return 3;
		} else if ("h5".equals(headerTag)) {
			return 2;
		} else if ("h6".equals(headerTag)) {
			return 1;
		} else {
			return 9;
		}
	}
	
	public static double getScoreForHeaderTag(String headerTag){
		headerTag = headerTag.toLowerCase();
		if ("h1".equals(headerTag)) {
			return 4.0;
		} else if ("h2".equals(headerTag)) {
			return 6.0;
		} else if ("h3".equals(headerTag)) {
			return 8.0;
		} else if ("h4".equals(headerTag)) {
			return 9.0;
		} else if ("h5".equals(headerTag)) {
			return 6.0;
		} else if ("h6".equals(headerTag)) {
			return 3.0;
		} else {
			return 0.0;
		}
	}

}
