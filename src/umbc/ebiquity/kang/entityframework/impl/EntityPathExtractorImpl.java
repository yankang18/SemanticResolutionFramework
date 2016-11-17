package umbc.ebiquity.kang.entityframework.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import umbc.ebiquity.kang.entityframework.IEntityPathExtractor;
import umbc.ebiquity.kang.entityframework.object.Entity;
import umbc.ebiquity.kang.entityframework.object.EntityPath;
import umbc.ebiquity.kang.entityframework.object.Entity.TermType;
import umbc.ebiquity.kang.textprocessing.util.TextProcessingUtils;
import umbc.ebiquity.kang.websiteparser.ICrawler;
import umbc.ebiquity.kang.websiteparser.IWebPage;
import umbc.ebiquity.kang.websiteparser.IWebPageNode;
import umbc.ebiquity.kang.websiteparser.IWebPagePath;
import umbc.ebiquity.kang.websiteparser.impl.SimplePageTemplatesSplitter;
import umbc.ebiquity.kang.websiteparser.impl.WebPageImpl;
import umbc.ebiquity.kang.websiteparser.impl.WebPageNode;
import umbc.ebiquity.kang.websiteparser.impl.WebPagePath;
import umbc.ebiquity.kang.websiteparser.object.HTMLTags;
import umbc.ebiquity.kang.websiteparser.object.LeafNode;
import umbc.ebiquity.kang.websiteparser.object.LeafNode.LeafType;
import umbc.ebiquity.kang.websiteparser.support.IWebPageParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.IWebSiteParsedPathsHolder;
import umbc.ebiquity.kang.websiteparser.support.impl.DefaultWebPagePathsParser;

public class EntityPathExtractorImpl implements IEntityPathExtractor {
	
	// TODO: need to add a logger here
	
	private Collection<WebPagePath> templatePaths;
	private List<EntityPath> termPaths;
	private List<IWebPageParsedPathsHolder> webPages;
	private SimplePageTemplatesSplitter templatesSplitter;
//	 private String webSiteHomeUrl;
	private int numWebpage = 0;
	private int webPageIndex = 0;

	private URL webSiteURL;

	public static IEntityPathExtractor create(IWebSiteParsedPathsHolder parsedPathsHolder) throws IOException {
		return new EntityPathExtractorImpl(parsedPathsHolder, null);
	}

	EntityPathExtractorImpl(IWebSiteParsedPathsHolder parsedPathsHolder, SimplePageTemplatesSplitter templatesSplitter) {
		this.webPages = parsedPathsHolder.getWebPageParsedPathHolders();
		this.webSiteURL = parsedPathsHolder.getWebSiteURL();
		this.templatesSplitter = templatesSplitter;
		this.termPaths = new ArrayList<EntityPath>();
	}
	
	public List<EntityPath> extract() {
		System.out.println("Constructing Entity Paths ...");
//		if (this.templatesSplitter != null) {
//			templatePaths = templatesSplitter.splitPageTemplates(webPages);
//		} 
		numWebpage = webPages.size();
		for (IWebPageParsedPathsHolder webPage : webPages) {
			webPageIndex++;
			termPaths.addAll(this.constructEntityPaths(webPage, webPageIndex));
		}
		return termPaths;
	}

	private Collection<EntityPath> constructEntityPaths(IWebPageParsedPathsHolder webPage, int webPageIndex) {
		// System.out.println("-------------------------------------------------------------------------------------------------");
		// System.out.println(" <" + webPage.getPageURLString() + ">... ");

		List<EntityPath> entityPaths = new ArrayList<EntityPath>();
		Set<String> visitedWebPagePaths = new HashSet<String>();
		Map<String, EntityPath> constructedEntityPaths = new HashMap<String, EntityPath>();
		List<IWebPagePath> webPagePaths = webPage.listWebTagPathsWithTextContent();
		int size = webPagePaths.size();
		for (int pathIndex = 0; pathIndex < size; pathIndex++) {
			IWebPagePath webPagePath = webPagePaths.get(pathIndex);
			// System.out.println(" <" + pathIndex + ">... ");

			if(skipPath(webPagePath)){
				continue;
			}
			
			String pathID = webPagePath.getPathID();
			Collection<Entity> allEntities = new ArrayList<Entity>();

			// Skip Web Page Path that has already been visited. In other words,
			// the Term Path of this Web Page Path has already been created.
			if (visitedWebPagePaths.contains(pathID)) {
				continue;
			} else {
				visitedWebPagePaths.add(pathID);
			}

			 // Get the last node (leaf node) in the Web Page Path
			IWebPageNode lastNode = webPagePath.getLastNode();
			String tagName = lastNode.getTag();
			String textualDescription = lastNode.getFullContent();
			String nodePattern = lastNode.getNodePattern();

			// if(!"text".equalsIgnoreCase(tagName)){
			// continue;
			// }

//			// TODO: also should check if there is any CSS applied to this leaf
//			// node.
//			LeafNode leafNode = new LeafNode(LeafType.Term, textualDescription);

			// If the last node has no textual description, skip this Web Page
			// Path
			if (TextProcessingUtils.isStringEmpty(textualDescription)) {
				continue;
			}
			
			// TODO: also should check if there is any CSS applied to this leaf
			// node.
			LeafNode leafNode = new LeafNode(LeafType.Term, textualDescription);

			// System.out.println("-> path: " + pathID + " ... ");
			// System.out.println("-> leaf node: with tag [" + tagName + "] with
			// content [" + textualDescription + "]");
			// System.out.println("-> with pattern [" + nodePattern +"]");

			int pathLevel = 1;

			/*
			 * Step 1: search the topics from the local area of current node.
			 * Topics found here should have higher score, since they are
			 * located in the same structure container with the possible
			 * instances.
			 */
			pathLevel++;
			Collection<Entity> localTopics = this.getLocalTopics(lastNode, pathLevel, webPageIndex);

			/*
			 * Step 2: search the topics from the regional area of current node.
			 */
			IWebPageNode structureNode = this.getContainerStructureNode(lastNode);
			Collection<Entity> regionalTopics = new ArrayList<Entity>();
			pathLevel = this.getRegionalTopics(structureNode, pathIndex, webPagePaths, pathLevel, webPageIndex, regionalTopics);

			/*
			 * Step 3: search the topics from the ancestors of current node.
			 */
			IWebPageNode parentNode = structureNode.getParent();
			pathLevel++;
			Collection<Entity> globalTopics = this.getTopicsFromAncestors(parentNode, pathIndex, webPagePaths, pathLevel, webPageIndex);

			// System.out.println("# leaf node topics:");
			// for (Entity topic : leafNodeTopics) {
			// System.out.println(" " + topic.getEntityLabel());
			// }
			// System.out.println("# local topics:");
			// for (Entity topic : localTopics) {
			// System.out.println(" " + topic.getEntityLabel());
			// }
			// System.out.println("# regional topics:");
			// for (Entity topic : regionalTopics) {
			// System.out.println(" " + topic.getEntityLabel());
			// }
			// System.out.println("# ancestor topics:");
			// for (Entity topic : globalTopics) {
			// System.out.println(" " + topic.getEntityLabel());
			// }

			// allEntities.addAll(leafNodeTopics);
			allEntities.addAll(localTopics);
			allEntities.addAll(regionalTopics);
			allEntities.addAll(globalTopics);

			/*
			 * 
			 */
			EntityPath entityPath = new EntityPath(webPagePath, leafNode);
			entityPath.addEntities(allEntities);
			entityPaths.add(entityPath);
			constructedEntityPaths.put(pathID, entityPath);
			// System.out.print("# All topics: ");
			// for(Entity topic : allEntities) {
			// System.out.print(" {" + topic.getEntityLabel() + "} ");
			// }
			// System.out.println("]");
			// System.out.println("<" + pathIndex + " of " + size + ">");
			// System.out.println("webpage <" + this.webPageIndex + " of " +
			// this.numWebpage + ">");
		}

		return entityPaths;
	}

	/**
	 * determine if the give path should be skipped or not.
	 * 
	 * @param webPagePath
	 *            the web page path
	 * @return true if this path should be skipped
	 */
	private boolean skipPath(IWebPagePath webPagePath) {
		if (templatesSplitter == null) {
			return false;
		} else if (templatePaths.contains(webPagePath)) {
			System.out.println("-> Skiped template path: " + webPagePath.getPathID());
			return true;
		}
		return false;
	}

	private List<Entity> getTopicsFromAncestors(IWebPageNode node, int structureNodeResidePathIndex, List<IWebPagePath> webPagePaths,
			int pathLevel, int webPageIndex) {

		List<Entity> topics = new ArrayList<Entity>();
		if (node == null) {
			return topics;
		}

		// System.out.println("-> getTopicsFromAncestors of: " +
		// node.getPrefixPathID() + node.getTag());

		/*
		 * Step 2: search the topics inside the structure container. Topics
		 * found here should have higher score, since they are located in the
		 * same structure container with the possible instances.
		 */
		pathLevel++;
		Collection<Entity> localTopics = this.getLocalTopics(node, pathLevel, webPageIndex);
		int numOfLocalTopics = localTopics.size();
		if (numOfLocalTopics != 0) {
			topics.addAll(localTopics);
		}

		/*
		 * Step 3: search the topics from siblings of the structure container.
		 * Topics found here should have relatively lower score.
		 */
		IWebPageNode structureNode = this.getContainerStructureNode(node);
		if (structureNode == null) {
			return topics;
		}
		// System.out.println("-> <structure node:" +
		// structureNode.getPrefixPathID() + structureNode.getTag() + ">");
		pathLevel++;
		List<Entity> regionalTopics = new ArrayList<Entity>();
		// Collection<Term> regionalTopics =
		// this.getRegionalTopics(structureNode, structureNodeResidePathIndex,
		// webPagePaths, pathLevel);
		pathLevel = this.getRegionalTopics(structureNode, structureNodeResidePathIndex, webPagePaths, pathLevel, webPageIndex,
				regionalTopics);
		int numOfRegionalTopics = regionalTopics.size();
		if (numOfRegionalTopics != 0) {
			topics.addAll(regionalTopics);
		}

		/*
		 * Step 4: search the topics from the ancestors of current node.
		 */
		IWebPageNode parentNode = structureNode.getParent();
		pathLevel++;
		List<Entity> ancestorTopics = this.getTopicsFromAncestors(parentNode, structureNodeResidePathIndex, webPagePaths, pathLevel,
				webPageIndex);
		int numOfAncestorTopics = ancestorTopics.size();
		if (numOfAncestorTopics != 0) {
			topics.addAll(ancestorTopics);
		}
		return topics;
	}

	/***
	 * 
	 * @param leafNode
	 * @return
	 */
	private Collection<Entity> getLeafNodeTopics(WebPageNode leafNode, int pathLevel, int webPageIndex) {
		Collection<Entity> topics = new ArrayList<Entity>();
		for (WebPageNode childNode : leafNode.listChildren()) {
			Collection<Entity> returnedTopics = this.extractTopics(childNode);
			if (returnedTopics.size() > 0) {
				for (Entity term : returnedTopics) {
					term.setScore(10);
					term.setLevel(pathLevel);
					term.setWebPageIndex(webPageIndex);
					topics.add(term);
				}
			}
		}
		return topics;
	}

	/***
	 * 
	 * @param structureNode
	 * @return
	 */
	private Collection<Entity> getLocalTopics(IWebPageNode node, int pathLevel, int webPageIndex) {

		Collection<Entity> topics = new ArrayList<Entity>();
		// /*
		// * if current node corresponds to a data column of a table (Currently,
		// * only search table for possible topics).
		// */
		if ("td".equals(node.getTag().toLowerCase())) {

			/*
			 * search row (tr) this data column (td) resides in
			 */
			IWebPageNode tableRowNode = node;
			while (tableRowNode != null && !"tr".equals(tableRowNode.getTag().toLowerCase())) {
				tableRowNode = tableRowNode.getParent();
			}

			if (tableRowNode != null) {

				/*
				 * find the first data column (td) of the found row. The first
				 * data column is the place, from which we can find topics. (It
				 * is highly possible that these topics correspond to
				 * properties/roles)
				 */
				List<Node> childNodes = tableRowNode.getWrappedElement().childNodes();
				Element firstTdElement = null;
				for (Node childNode : childNodes) {
					if (childNode instanceof Element) {
						Element e = (Element) childNode;
						String tagName = e.tagName();
						if ("td".equals(tagName)) {
							firstTdElement = e;
							break;
						}
					}
				}

				if (firstTdElement != null) {
					/*
					 * extract topics from the first data column
					 */
					// TODO: should test whether the first column contains
					// the property information
					Entity term = extractTermFromUnitElement(firstTdElement);
					if (term != null) {
						term.setLevel(pathLevel);

						// here assume the first column contains header
						term.setScore(7.0);
						term.setWebPageIndex(webPageIndex);
						term.setTermType(TermType.Role);
						topics.add(term);
					}
				}
			}
		}

		/*
		 * TEST
		 */
		// for (Entity term : topics) {
		// System.out.print("{L} "+term.getEntityLabel() + " ");
		// }
		// if (topics.size() > 0) {
		// System.out.println();
		// }

		return topics;
	}

	/***
	 * 
	 * @param structureNode
	 * @param structureNodeResidePathIndex
	 * @param webPagePaths
	 * @param pathLevel
	 * @return
	 */
	private int getRegionalTopics(IWebPageNode structureNode, int structureNodeResidePathIndex, List<IWebPagePath> webPagePaths, int pathLevel,
			int webPageIndex, Collection<Entity> topics) {

		// System.out.println("-> getRegionalTopics of: " +
		// structureNode.getPrefixPathID() + "|" + structureNode.getTag());
		// Collection<Term> topics = new ArrayList<Term>();

		int headerTagInteger = 0;
		boolean headerTagFound = false;
		boolean onlyHeaderTag = false;
		for (int i = structureNodeResidePathIndex - 1; i >= 0; i--) {

			IWebPagePath path = webPagePaths.get(i);

			/*
			 * first to make sure we are searching siblings of structure node.
			 */
			if (path.getPathID().startsWith(structureNode.getPrefixPathID())) {
				IWebPageNode node = path.getNode(structureNode.getPrefixPathID());

				/*
				 * to check if this sibling node has the same pattern as the
				 * structure node. If they have the same pattern, ignore this
				 * sibling and continue searching.
				 */
				if (node == null)
					continue;
				if (!structureNode.getNodePattern().equals(node.getNodePattern())) {
					/*
					 * TEST
					 */
					// System.out.println(" - Structure Node pattern: " +
					// structureNode.getNodePattern());
					// System.out.println(" - Node pattern: " +
					// node.getNodePattern());
					//
					/*
					 * (i) We always search concepts contained in header tags
					 * (e.g., h1, h2,...). (ii) After the first time we have
					 * found any concept in certain sibling, we will only search
					 * concepts contained in header tag. (iii) If certain header
					 * tag have been identified, we will only search header tags
					 * that are bigger the one just found.
					 */
					Collection<Entity> extractedTopics = this.extractTopics(node);
					if (extractedTopics.size() != 0) {

						if (headerTagFound || onlyHeaderTag) {
							for (Entity topic : extractedTopics) {
								if (HTMLTags.getHeaderTags().contains(topic.getWrappingTag())) {
									int tempHeaderTagInteger = HTMLTags.getIntegerForHeaderTag(topic.getWrappingTag());
									if (tempHeaderTagInteger > headerTagInteger) {
										headerTagInteger = tempHeaderTagInteger;
										topic.setScore(HTMLTags.getScoreForHeaderTag(topic.getWrappingTag()));
										topic.setLevel(pathLevel);
										topic.setWebPageIndex(webPageIndex);
										topics.add(topic);
										pathLevel++;
									}
								}
							}

						} else {

							for (Entity topic : extractedTopics) {
								if (HTMLTags.getHeaderTags().contains(topic.getWrappingTag())) {
									headerTagFound = true;
									headerTagInteger = HTMLTags.getIntegerForHeaderTag(topic.getWrappingTag());
									topic.setScore(HTMLTags.getScoreForHeaderTag(topic.getWrappingTag()));
									topic.setLevel(pathLevel);
									topic.setWebPageIndex(webPageIndex);
									pathLevel++;
								} else {
									topic.setScore(6.0);
									topic.setLevel(pathLevel);
									topic.setWebPageIndex(webPageIndex);
								}
								topics.add(topic);
							}
						}

						onlyHeaderTag = true;
					} else {
						if (!headerTagFound && !onlyHeaderTag) {
							break;
						}
					}
				}
			} else {
				break;
			}
		}

		/*
		 * TEST
		 */
		// for (Entity term : topics) {
		// System.out.print("{R} "+term.getEntityLabel() + " ");
		// }
		// if (topics.size() > 0) {
		// System.out.println();
		// }
		return pathLevel;
	}

	/***
	 * 
	 * @param node
	 * @return
	 */
	private Collection<Entity> extractTopics(IWebPageNode node) {

		// TODO: should also consider css files
		Collection<Entity> entities = new LinkedHashSet<Entity>();
		if (node == null)
			return entities;
		String nodeTag = node.getTag().toLowerCase();
		if (HTMLTags.getListTags().contains(nodeTag)) {
			// return empty topic collection
			// System.out.println(nodeTag + " is contained in list tags");
			return entities;

		} else if (HTMLTags.getTableTags().contains(nodeTag)) {
			// TODO: more sophisticated approach should be taken
			return entities;
		} else if ("font".equals(nodeTag)) {

			String content = node.getFullContent();
			if (!TextProcessingUtils.isStringEmpty(content)) {
				Entity topic = new Entity(content);
				topic.setWrappingTag(nodeTag);
				entities.add(topic);
			}

		} else if (HTMLTags.getBlockTags().contains(nodeTag)) {
			// String content = node.getFullContent();
			// if (!TextProcessingUtils.isStringEmpty(content)) {
			// Entity topic = new Entity(content);
			// topic.setWrappingTag(nodeTag);
			// entities.add(topic);
			// }
		}
		// else if ("img".equals(nodeTag)) {
		// String content = node.getFullContent();
		// if (!TextProcessingUtils.isStringEmpty(content)) {
		// Term topic = new Term(content);
		// topic.setWrappingTag(nodeTag);
		// concepts.add(topic);
		// }
		//
		// }
		else if ("a".equals(nodeTag)) {

			boolean topicFound = false;
			for (Element element : node.getWrappedElement().getAllElements()) {
				String tagName = element.tagName().toLowerCase();
				if (HTMLTags.getTopicTags().contains(tagName)) {
					topicFound = true;
					String conceptContent = TextProcessingUtils.escapeSpecial(element.text());
					if (!TextProcessingUtils.isStringEmpty(conceptContent)) {
						Entity topic = new Entity(conceptContent);
						topic.setWrappingTag(element.tagName());
						// need add reference of <a>
						entities.add(topic);
					}
				}
				// else if ("img".equals(tagName)) {
				// Term term = this.extractTermFromImageElement(element);
				// if (term != null) {
				// System.out.println(" image tag: " + element.tagName());
				// concepts.add(term);
				// }
				// }
			}

			if (!topicFound) {
				String conceptContent = node.getFullContent();
				if (!TextProcessingUtils.isStringEmpty(conceptContent)) {
					Entity topic = new Entity(conceptContent);
					topic.setWrappingTag(nodeTag);
					// need add reference of <a>
					entities.add(topic);
				}
			}

		} else if (HTMLTags.getTopicTags().contains(nodeTag)) {

			String conceptContent = node.getFullContent();
			if (!TextProcessingUtils.isStringEmpty(conceptContent)) {
				Entity topic = new Entity(conceptContent);
				topic.setWrappingTag(nodeTag);
				entities.add(topic);
			}

		}
		// else if ("td".equals(nodeTag)) {
		// // WebPageNode lastNode = node.getResidePath().getLastNode();
		// System.out.println(" td tag: " + nodeTag);
		// Term term = extractTermFromUnitElement(node.getWrappedElement());
		// if (term != null) {
		// concepts.add(term);
		// }
		//
		// }
		else if ("span".equals(nodeTag)) {

			String textContent = node.getFullContent();
			Element element = node.getWrappedElement();

			// System.out.println(" Block element tag: " + element.tagName());
			if (this.isUpperCase(textContent)) {
				Entity topic = new Entity(textContent);
				topic.setWrappingTag(element.tagName());
				entities.add(topic);
				return entities;
			}
			/*
			 * if contains table, return empty concept collection
			 */
			for (String tag : HTMLTags.getTableTags()) {
				if (element.select(tag).size() > 0) {
					// System.out.println(" element contains table tags");
					return entities;
				}
			}

			/*
			 * if contains list, return empty concept collection
			 */
			for (String tag : HTMLTags.getListTags()) {
				if (element.select(tag).size() > 0) {
					// System.out.println(" element contains list tags");
					return entities;
				}
			}

			/*
			 * if contains form-related tags, return empty concept collection
			 */
			for (String tag : HTMLTags.getFormTags()) {
				if (element.select(tag).size() > 0) {
					// System.out.println(" element contains eliminated tags");
					return entities;
				}
			}

			for (Element containedElement : element.select("*")) {

				String tagName = containedElement.tagName().toLowerCase();
				if (HTMLTags.getTopicTags().contains(tagName)) {
					String conceptContent = TextProcessingUtils.escapeSpecial(containedElement.text());
					if (!TextProcessingUtils.isStringEmpty(conceptContent)) {
						// System.out.println(" topic tag: " + tagName);
						Entity topic = new Entity(conceptContent);
						topic.setWrappingTag(tagName);
						entities.add(topic);
					}
				}

				// if ("img".equals(tagName)) {
				// Term term =
				// this.extractTermFromImageElement(containedElement);
				// if (term != null) {
				// System.out.println(" image tag: " + tagName);
				// concepts.add(term);
				// }
				// }

				if ("a".equals(tagName)) {
					String conceptContent = TextProcessingUtils.escapeSpecial(containedElement.text());
					// System.out.println(" link tag: " + conceptContent);
					if (!TextProcessingUtils.isStringEmpty(conceptContent) && this.isUpperCase(conceptContent)) {
						Entity topic = new Entity(conceptContent);
						topic.setWrappingTag(tagName);
						entities.add(topic);
					}
					// else {
					// for (Element imgElement : containedElement.select("img"))
					// {
					// Term term = this.extractTermFromImageElement(imgElement);
					// if (term != null) {
					// concepts.add(term);
					// }
					// }
					//
					// for (Element imgElement : containedElement.select("IMG"))
					// {
					// Term term = this.extractTermFromImageElement(imgElement);
					// if (term != null) {
					// concepts.add(term);
					// }
					// }
					// }
				}

			}

			// if (concepts.size() == 0 && !"".equals(textContent.trim())) {
			// // If current element contains text except but have no topic
			// // (from topic tag), extract the possible concept sentence from
			// // this text as the concept (There are many possible strategies.
			// // e.g., extract last sentence or extract certain
			// // amount of words from the text).
			// String conceptSentence =
			// this.extractConceptSentence(textContent.trim());
			// Term topic = new Term(conceptSentence);
			// topic.setWrappingTag(nodeTag);
			// concepts.add(topic);
			// return concepts;
			// }

		}

		// <span>, <font>
		return entities;
	}

	private boolean isUpperCase(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLowerCase(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean hasOnlyOneTextNode(Element element) {
		int numberOfTextNode = 0;
		for (Node child : element.childNodes()) {
			if (child instanceof TextNode) {
				if (!TextProcessingUtils.isStringEmpty(((TextNode) child).text())) {
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

	private boolean hasOnlyOneElementNode(Element element) {
		int numberOfElementNode = 0;
		for (Node child : element.childNodes()) {
			if (child instanceof TextNode) {
				if (!TextProcessingUtils.isStringEmpty(((TextNode) child).text())) {
					return false;
				}
			} else if (child instanceof Element) {
				if (!"br".equals(((Element) child).tagName().toLowerCase())) {
					numberOfElementNode++;
				}
			}
		}
		if (numberOfElementNode > 1) {
			return false;
		}
		return true;
	}

	private Entity extractTermFromUnitElement(Element element) {
		Entity term = null;
		if (this.hasOnlyOneTextNode(element)) {
			String content = element.text();
			if (!TextProcessingUtils.isStringEmpty(content)) {
				term = new Entity(element.text());
				term.setWrappingTag("text");
			}
		} else if (this.hasOnlyOneElementNode(element)) {
			Element theElement = null;
			for (Element childElem : element.children()) {
				if (!"br".equals((childElem).tagName().toLowerCase())) {
					theElement = childElem;
				}
			}

			String tagName = theElement.tagName().toLowerCase();
			if ("font".endsWith(tagName)) {

				String content = theElement.text();
				if (!TextProcessingUtils.isStringEmpty(content)) {
					term = new Entity(content);
					term.setWrappingTag(tagName);
				}

			} else if ("img".equals(tagName)) {
				// String content = node.getFullContent();
				// if (!"".equals(content)) {
				// term = new Term(content);
				// term.setWrappingTag(tagName);
				// }

			} else if ("a".equals(tagName)) {

				String content = theElement.text();
				if (!TextProcessingUtils.isStringEmpty(content)) {
					term = new Entity(content);
					term.setWrappingTag(tagName);
				} else {
					// theElement.select("img");
					// term = this.extractTermFromImageElement(element);
				}
			} else if (HTMLTags.getTopicTags().contains(tagName)) {

				String content = theElement.text();
				if (!TextProcessingUtils.isStringEmpty(content)) {
					term = new Entity(content);
					term.setWrappingTag(tagName);
				}
			}
		}

		return term;
	}

	private Entity extractTermFromImageElement(Element img) {

		String imgName = img.attr("alt");
		String imgAlt = img.attr("title");
		Entity term = null;
		if (TextProcessingUtils.isStringEmpty(imgName)) {
			if (!TextProcessingUtils.isStringEmpty(imgAlt)) {
				term = new Entity(imgAlt);
				term.setWrappingTag(img.tagName());
			}
		} else {

			term = new Entity(imgName);
			term.setWrappingTag(img.tagName());
		}
		return term;
	}

	/***
	 * 
	 * @param textContent
	 * @return
	 */
	private String extractConceptSentence(String textContent) {

		char[] textContentChars = textContent.toCharArray();
		int numOfChars = textContentChars.length;
		char[] conceptSentenceChars = new char[numOfChars];
		conceptSentenceChars[numOfChars - 1] = textContentChars[numOfChars - 1];
		int i = numOfChars - 2;
		int j = numOfChars - 2;
		int numOfCharsForConceptSentence = 1;
		for (; i >= 0; i--) {
			if ('.' != textContentChars[i]) {
				conceptSentenceChars[j] = textContentChars[i];
				numOfCharsForConceptSentence++;
				j--;
			} else {
				break;
			}
		}
		String conceptSentence = new String(conceptSentenceChars).substring(j + 1);
		return conceptSentence.replaceAll("\\s+", " ");
	}

	private IWebPageNode getContainerStructureNode(IWebPageNode node) {

		if (HTMLTags.getListTags().contains(node.getTag())) {
			// search node with tag "ol" or "ul"
			if (node.getTag().toLowerCase().equals("li")) {
				// node with tag "li"
				// WebPageNode containerNode = node.getParent();
				IWebPageNode containerNode = node;
				while (containerNode != null && !node.getParent().getTag().toLowerCase().equals("ol")
						&& !node.getParent().getTag().toLowerCase().equals("ul")) {
					containerNode = containerNode.getParent();
				}
				return containerNode;

			} else {
				// node with tag "ol" or "ul"
				return node;
			}

		} else if (HTMLTags.getTableTags().contains(node.getTag())) {

			// search node with tag "table"
			if (!node.getTag().toLowerCase().equals("table")) {

				// WebPageNode containerNode = node.getParent();
				IWebPageNode containerNode = node;
				while (containerNode != null && !containerNode.getTag().toLowerCase().equals("table")) {
					containerNode = containerNode.getParent();
				}
				return containerNode;

			} else {
				return node;
			}

		} else {
			return node;
			// return node.getParent();
		}
	}

//	@Override
//	public URL getWebSiteURL() {
//		return webSiteURL;
//	}
	
	public Collection<EntityPath> getEntityPaths() {
		return this.termPaths;
	}



}
