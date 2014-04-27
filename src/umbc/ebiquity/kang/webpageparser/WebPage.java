package umbc.ebiquity.kang.webpageparser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityGraph;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityNode;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityPath;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityPathExtractor;
import umbc.ebiquity.kang.ontologyinitializator.entityframework.component.EntityValidator;
import umbc.ebiquity.kang.textprocessing.TextProcessingUtils;
import umbc.ebiquity.kang.webpageparser.WebTagNode.WebTagNodeType;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/***
 * 
 * @author kangyan2003
 */
public class WebPage {

	private String baseURL;
	private String hostName;
	private CrawlerUrl pageURL;
	
	private Document webPageDoc;
	private List<WebPage> predecessorList;
	private List<WebPage> descendantList;
//	private List<String> externalLinks;
	private Map<String, String> externalLinks;
	private List<WebTagPath> webPagePathList;
	private boolean visited;
	private boolean linksExtracted;
	private UrlValidator urlValidator;
	private Collection<EntityPath> entityPaths;
	private Collection<String> webPageTopics;
	private String webPageMainTopic;
	private Map<String, Integer> tagCounterMapper;

	public WebPage(CrawlerUrl pageURL) throws IOException {
		this.pageURL = pageURL;
		this.visited = false;
		this.linksExtracted = false;
		this.predecessorList = new ArrayList<WebPage>();
		this.descendantList = new ArrayList<WebPage>();
//		this.externalLinks = new ArrayList<String>();
		this.externalLinks = new LinkedHashMap<String, String>();
		this.tagCounterMapper = new HashMap<String, Integer>();
		this.webPagePathList = new ArrayList<WebTagPath>();
		this.entityPaths = new LinkedHashSet<EntityPath>();
		this.webPageTopics = new LinkedHashSet<String>();
		
		String[] schemes = { "http", "https" };
		this.urlValidator = new UrlValidator(schemes);
		this.initWebPageDoc();
	}

	private void initWebPageDoc() throws IOException {
		String URL = this.pageURL.getUrlString();
		this.visited = true;
		if (this.validateURL(URL)) {
			webPageDoc = Jsoup.connect(this.pageURL.getUrlString()).get();
			this.baseURL = webPageDoc.baseUri();
			this.hostName = this.getHostName(baseURL);
		} else {
			throw new IOException(URL + " is not a valid URL");
		}
	}
	
	public String getHostName(){
		return this.hostName;
	}
	
	public void setHostName(String hostName){
		this.hostName = hostName;
	}
	
	private String getHostName(String baseURL) {
		String[] tokens =  baseURL.split("/");
//		System.out.println(tokens[2]);
		return tokens[2];
	}

	public void addPredecessor(WebPage pageNode) {
		this.predecessorList.add(pageNode);
	}
	
	public void addDecendant(WebPage pageNode){
		this.descendantList.add(pageNode);
	}

	public CrawlerUrl getPageURL() {
		return pageURL;
	}
	
	public String getPageURLString(){
		return pageURL.getUrlString();
	}

	/***
	 * 
	 */
	public void analyzeWebPage() {
		this.extractWebPageTopics(webPageDoc);
		this.constructWebPagePaths(webPageDoc);
	}
	
	/***
	 * extract topics this web page talks about, mainly from the <head> element
	 * of the HTML
	 * @param webPageDoc
	 */
	private void extractWebPageTopics(Document webPageDoc){
//		print("\n@ Geting topic of web page: %s", this.getPageURLString());
		String webPageTitle = webPageDoc.title();
//		System.out.println("## title: " + webPageTitle);
		webPageTopics.add(webPageTitle);
	}

	/***
	 * 
	 * @param webPageDoc
	 */
	private void constructWebPagePaths(Document webPageDoc) {
		
//		print("\n@ Constructing WebPagePaths from: <%s>", this.getPageURLString());
		Element body = webPageDoc.body();
		for (Element child : body.children()) {
			
			String childTagName = child.tagName().toLowerCase();
//			System.out.println("## " + childTagName);
			if (!HTMLTags.getEliminatedTags().contains(childTagName) 
					&& !HTMLTags.getIgnoredTags().contains(childTagName)){
				
				WebTagPath path = new WebTagPath();
				this.webPagePathList.add(path);
				path.setHost(this.getPageURLString());
				WebTagNode webPageNode = this.createWebPageNode(child);
				webPageNode.setLeafNode(false);
				path.addNode(webPageNode);
				this.appendWebPagePathNode(path, child);
				
			}
		}
	}
	
	private void appendWebPagePathNode(WebTagPath path, Element element) {
		
		if (!this.hasOnlyOneTextNode(element)) {
			List<Node> childNodeList = element.childNodes();
			int numOfChildrenNode = childNodeList.size();
			
			/*
			 * Clone paths before navigating children of current element.
			 */
			int numOfPaths = numOfChildrenNode;
			WebTagPath[] webPagePaths = null;
			if (numOfPaths > 0) {
				webPagePaths = new WebTagPath[numOfPaths];
				
				/*
				 * The first path is not cloned but the path that has already
				 * been created. We reuse this path to hold the first child of
				 * current element
				 */
				webPagePaths[0] = path;
				
				/*
				 * Clone other paths to hold children except the first child of
				 * current element
				 */
				for (int i = 1; i < numOfPaths; i++) {
					webPagePaths[i] = path.clone();
					webPagePaths[i].setHost(this.getPageURLString());
				}
			}
			
			String textContent = "";
			int indexOfNode = 0;
			int indexOfPath = 0;
			for (; indexOfNode < numOfChildrenNode; indexOfNode++) {
				
				Node firstChildNode = childNodeList.get(indexOfNode);
				if (firstChildNode instanceof TextNode) {
					TextNode textNode = (TextNode) firstChildNode;
					String text = textNode.text();
					if (!TextProcessingUtils.isStringEmpty(text)) {
						textContent += text;
					}

				} else if (firstChildNode instanceof Element) {
					
					Element elementNode = (Element) firstChildNode;
					String combinedText = elementNode.text();
					String elementNodeTagName = elementNode.tagName().toLowerCase();
					
					if (HTMLTags.getIgnoredTags().contains(elementNodeTagName)) {
						
//						String text = elementNode.text();
						if (!TextProcessingUtils.isStringEmpty(combinedText)) {
							textContent += combinedText;
						}
						
					} else if (HTMLTags.getTopicTags().contains(elementNodeTagName)){
						
//						System.out.println("#### Topic Tag: " + elementNodeTagName);
						if (textContent != null && !TextProcessingUtils.isStringEmpty(textContent)) {
							WebTagNode newWebPageNode = this.createWebPageNode(textContent);
							newWebPageNode.setLeafNode(true);
							WebTagPath webPagePath = webPagePaths[indexOfPath];
							webPagePath.addNode(newWebPageNode);

							/*
							 * The first path has already been added to the web
							 * page path list. Other paths are clones, thus they
							 * should be added to the web page path list.
							 */
							if (indexOfPath != 0) {
								this.webPagePathList.add(webPagePath);
							}
							indexOfPath++;
							textContent = "";
						}
						
//						String text = elementNode.text();
						if (!TextProcessingUtils.isStringEmpty(combinedText)) {
//							System.out.println("#### content: " + combinedText);
							WebTagNode newWebPageNode = this.createWebPageNode(elementNode);
							newWebPageNode.setLeafNode(true);
							WebTagPath webPagePath = webPagePaths[indexOfPath];
							webPagePath.addNode(newWebPageNode);
							if (indexOfPath != 0) {
//								System.out.println("#### *content: " + combinedText);
								this.webPagePathList.add(webPagePath);
							}
							indexOfPath++;
						}
						
					} else if (HTMLTags.getEliminatedTags().contains(elementNodeTagName)){
						
						if (textContent != null && !TextProcessingUtils.isStringEmpty(textContent)) {
							WebTagNode newWebPageNode = this.createWebPageNode(textContent);
							newWebPageNode.setLeafNode(true);
							WebTagPath webPagePath = webPagePaths[indexOfPath];
							webPagePath.addNode(newWebPageNode);
							if (indexOfPath != 0) {
								this.webPagePathList.add(webPagePath);
							}
							indexOfPath++;
							textContent = "";
						}
						
					} else {
						
						if (textContent != null && !TextProcessingUtils.isStringEmpty(textContent)) {
							WebTagNode newWebPageNode = this.createWebPageNode(textContent);
							newWebPageNode.setLeafNode(true);
							WebTagPath webPagePath = webPagePaths[indexOfPath];
							webPagePath.addNode(newWebPageNode);
							if (indexOfPath != 0) {
								this.webPagePathList.add(webPagePath);
							}
							indexOfPath++;
							textContent = "";
						}
						
						if (!TextProcessingUtils.isStringEmpty(combinedText)) {
							
							// Here should create a unique number for each newly
							// created WebPageNode
							WebTagNode newWebPageNode = this.createWebPageNode(elementNode);
							WebTagPath webPagePath = webPagePaths[indexOfPath];
							webPagePath.addNode(newWebPageNode);
							if (indexOfPath != 0) {
								this.webPagePathList.add(webPagePath);
							}
							newWebPageNode.setLeafNode(false);
							this.appendWebPagePathNode(webPagePath, elementNode);
							indexOfPath++;
						}
					}
				}
			}
			
			/*
			 * This is dealing with the situation where the last node of current
			 * element is a text node.
			 */
			if (textContent != null && !TextProcessingUtils.isStringEmpty(textContent)) {
				WebTagNode newWebPageNode = this.createWebPageNode(textContent);
				newWebPageNode.setLeafNode(true);
				WebTagPath webPagePath = webPagePaths[indexOfPath];
				webPagePath.addNode(newWebPageNode);
				if (indexOfPath != 0) {
					this.webPagePathList.add(webPagePath);
				}
			}
			
		} else {
//			WebTagNode webPageNode = path.getLastNode();
//			webPageNode.setLeafNode(true); 
			
			WebTagNode webPageNode = path.getLastNode();
			WebTagNode newWebPageNode = this.createWebPageNode(webPageNode.getFullContent());
			newWebPageNode.setLeafNode(true);
			path.addNode(newWebPageNode);
		}
	}


	/**
	 * To check if a element contains only one text node that is not empty. One
	 * special case is that if this element contains multiple br tag, but only
	 * one non-empty text node, we still treat this element as the one contains
	 * only one text node that is not empty.
	 */
	private boolean hasOnlyOneTextNode(Element element) {
		int numberOfTextNode = 0;
		for (Node child : element.childNodes()) {
			if (child instanceof TextNode) {
				if(!TextProcessingUtils.isStringEmpty(((TextNode) child).text())){
					numberOfTextNode++;
				}
			} else if (child instanceof Element) {
				if (!"br".equals(((Element) child).tagName().toLowerCase())) {
					return false;
				}
			}
		}

		if (numberOfTextNode > 1) {
			return false;
		}
		return true;
	}

	private WebTagNode createWebPageNode(Element child) {
		Integer counter = tagCounterMapper.get(child.tagName().toLowerCase());
		if (counter == null) {
			counter = new Integer(1);
			tagCounterMapper.put(child.tagName(), counter);
		} else {
			counter ++;
			tagCounterMapper.put(child.tagName(), counter);
		}
		
		WebTagNode node = new WebTagNode(child, counter);
		return node;
	}
	
	private WebTagNode createWebPageNode(String textNodeContent) {
		WebTagNode node = new WebTagNode(textNodeContent);		
		return node;
	}
	
	/***
	 * get all web page paths
	 * @return a list of WebPagePath instances
	 */
	public List<WebTagPath> listWebTagPaths(){
		return this.webPagePathList;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<WebTagPath> listWebTagPathsWithTextContent() {
		
		/*
		 * Note here we use ArrayList that allows duplicate paths.  
		 */
		List<WebTagPath> webPagePathsWithLeafContent = new ArrayList<WebTagPath>();
		for (WebTagPath path : webPagePathList) {
			if (path.containsTextContent() || path.getLastNode().getTag().equals("img")) {
				webPagePathsWithLeafContent.add(path);
			}
		}
		return webPagePathsWithLeafContent;
	}

	/**
	 * extract out-going links from the WebPage instance
	 * @param visitedLinkSet
	 */
	public void extractLinks(Set<String> visitedLinkSet) {
//		System.out.println("base url " + hostName);
		Elements links = webPageDoc.select("a[href]");
//		print("Links (%d)", links.size());
		for (Element link : links) {
			String href = link.attr("abs:href").trim();
			
			if(visitedLinkSet.contains(href)){
				continue;
			}
			
//			System.out.println("---- " + href);
			if(validateLink(href)){
//				System.out.println("---- " + true);
				String text = link.text();
				externalLinks.put(href, text);
			} else {
//				System.out.println("---- " + false);
			}
		}
		this.linksExtracted = true;
	}
	
	private boolean validateLink(String href) {
		if (this.validateURL(href) && this.isWebPage(href) && this.withinHostDomain(href) && this.isUsefulWebPage(href)) {
			return true;
		}
		return false;
	}
	
	private boolean isUsefulWebPage(String href){
//		String[] tokens = href.split("/");
//		String lastToken = tokens[tokens.length -1];
////		System.out.println("lastToken: " + lastToken);
//		String[] tokens2 = lastToken.split("_|-|\\.");
////		lastToken = lastToken.replaceAll("_|-|\\.", " ");

		List<String> list = new ArrayList<String>();
		String[] tokens = href.split("/");
		for (String token : tokens) {
			for(String token2 : token.split("_|-|\\.") )
				list.add(token2);
		} 
		Set<String> XXX = new HashSet<String>();
		XXX.add("blog");
		XXX.add("blogs");
		XXX.add("faq");
		XXX.add("faqs");
		XXX.add("carreer");
		XXX.add("carreers");
		XXX.add("portfolios");
		XXX.add("email");
		XXX.add("Email");
		XXX.add("request");
		
		for (String token : list) {
//			System.out.println("Token: " + token);
			if (XXX.contains(token)) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] arg){
		
//		EntityValidator validator = new EntityValidator();
//		boolean xx = TextProcessingUtils.containsOnlyDefaultStopwords("stimulation");
//		System.out.println(xx);
		
//		String webURL = "http://navitekgroup.com/services/contract-engineering/";
//		CrawlerUrl url = new CrawlerUrl(webURL, 1);
//		EntityPathExtractor entityPathExtractor = new EntityPathExtractor();
//		EntityGraph entityGraph = new EntityGraph();
//		try {
//			WebPage page = new WebPage(url);
//			page.analyzeWebPage();
//			List<WebTagPath> xxx = page.listWebTagPathsWithTextContent();
//			for(WebTagPath path : xxx){
////				System.out.println(path.getPathPattern());
//				System.out.println(path.getPathID());
//			}
//			System.out.println();
//			System.out.println();
//			Collection<EntityPath> entityPaths  = entityPathExtractor.constructEntityPaths(page, 1);
//			for(EntityPath entityPath : entityPaths){
//				System.out.println(entityPath.printPathTopDown());
//			}
//			
//			entityGraph.initializeEntityGraph(entityPaths);
//			entityGraph.printForwardTermGraphNodesAfterAnalyzing();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	private boolean validateURL(String URL) {
		if (urlValidator.isValid(URL)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isWebPage(String href){
		if(href.endsWith(".pdf") || href.endsWith(".jpg")){
			return false;
		}
		return true;
	}
	
	private boolean withinHostDomain(String href){
		String trimedHref = href.replace("http://", "").replace("http://", "");
//		String domainName = trimedHref.split("/")[0];
		return trimedHref.startsWith(hostName);
		
//		String trimedDomainName = domainName.replace("www.", "").replace(".com", "");
////		System.out.println("trimedDomainName: " + trimedDomainName);
//		String trimedHostName = hostName.replace("www.", "").replace(".com", "");
////		System.out.println("trimedHostName: " + trimedHostName);
//		List<String> domainWordList = new ArrayList<String>(Arrays.asList(trimedDomainName.split("\\.")));
//		List<String> hostWordList = new ArrayList<String>(Arrays.asList(trimedHostName.split("\\.")));
////		System.out.println("domainWordList: " + domainWordList);
////		System.out.println("hostWordList: " + hostWordList);
//		domainWordList.retainAll(hostWordList);
//		if (domainWordList.size() > 0) {
//			return true;
//		} else {
//			return false;
//		}
	}
	
	
	public void addEntityPath(EntityPath entityPath){
		this.entityPaths.add(entityPath);
		entityPath.setWebPage(this);
	}

	public Collection<EntityPath> getEntityPaths(){
		return this.entityPaths;
	}
	
	public Map<String, String> getExternalLinks(){
//		if (this.linksExtracted == false) {
//			this.extractLinks();
//		}
		return this.externalLinks;
	}

	public boolean isVisited() {
		return visited;
	}
	
	public String getBaseURL(){
		return this.baseURL;
	}

	public void setWebPageMainTopic(String webPageMainTopic) {
		this.webPageMainTopic = webPageMainTopic;
	}

	public String getWebPageMainTopic() {
		return webPageMainTopic;
	}
	
	private void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
	
}
