package umbc.ebiquity.kang.webpageparser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import com.hp.hpl.jena.util.FileManager;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.AbbrConverter;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ILabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo.ClassType;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;

public class ResourceHub {
	private static OntResourceModel ontResModel;
	private static List<OntResourceInfo> allNamedClassList;
	private static List<String> tokenizeClassNameList;
	private static Analyzer analyer;;
	
	static {
		String projectDir = System.getProperty("user.dir");
		String dir = projectDir + "/Ontologies/MSDL-Fullv1.owl";
		System.out.println(dir);
		InputStream instream = FileManager.get().open(dir);
		ontResModel = new OntResourceModel(instream);
		tokenizeClassNameList = new ArrayList<String>();
//		analyer = new PorterStemStopWordAnalyzer();
	    allNamedClassList = ontResModel.listAllNamedClasses();
	    
	    
		for (OntResourceInfo resInfo : allNamedClassList) {
			List<String> wordList = AbbrConverter.convertLabel2FullWordList(resInfo.getLocalName());
			StringBuilder wordstringBuilder = new StringBuilder();
			for(String word : wordList){
				wordstringBuilder.append(word +  " ");
			}
			Reader reader = new StringReader(wordstringBuilder.toString().trim());
			TokenStream stream = analyer.tokenStream(null, reader);
			TermAttribute term = stream.addAttribute(TermAttribute.class);
			StringBuilder stringBuilder = new StringBuilder();
			try {
				while (stream.incrementToken()) {
					stringBuilder.append(term.term() + " ");
				}
				
				System.out.println("class: " + stringBuilder.toString());
				resInfo.setTokenizedName(stringBuilder.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static List<OntResourceInfo> listAllNamedClasses(){
		return allNamedClassList;
	}
	
	public static List<OntClassInfo> searchInfoOfMatchedClasses(String className, int numberOfReturns) {
		
		ArrayList<OntClassInfo> returnInfo = new ArrayList<OntClassInfo>();
		if (className.trim().isEmpty()) {
			return returnInfo;
		}
		
		Reader reader = new StringReader(className);
		TokenStream stream = analyer.tokenStream(null, reader);
		TermAttribute term = stream.addAttribute(TermAttribute.class);
//		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
		StringBuilder stringBuilder = new StringBuilder();
		try {
			while (stream.incrementToken()) {
				stringBuilder.append(term.term() + " ");
			}

			String tokenizedClassName = stringBuilder.toString().trim();
			System.out.println("tokenized class name: " + tokenizedClassName);
			ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_1to1);
			ArrayList<ClassInfoWrapper> classInfoWrapperList = new ArrayList<ClassInfoWrapper>();
			List<OntResourceInfo> allNamedClassList = listAllNamedClasses();
			for (OntResourceInfo namedClass : allNamedClassList) {
//				String localName = namedClass.getLocalName();
				String localName = namedClass.getTokenizedName();
				double labelSimilarity = labelSim.getSimilarity(tokenizedClassName, localName);
//				double labelSimilarity = labelSim.getSimilarity(className, localName);
				if (labelSimilarity > 0.5) {
					String NameSpace = namedClass.getNamespace();
					String URI = namedClass.getURI();
					OntClassInfo classInfo = new OntClassInfo(URI, NameSpace, localName, ClassType.NamedClass);
					ClassInfoWrapper classInfoWrapper = new ClassInfoWrapper(labelSimilarity, classInfo);
					classInfoWrapperList.add(classInfoWrapper);
				}
			}

			Collections.sort(classInfoWrapperList);
			int counter = 0;
			for (ClassInfoWrapper infoWrapper : classInfoWrapperList) {
				System.out.println("*" + infoWrapper.getOntClassInfo().getLocalName() + " " + infoWrapper.getSimilarity());
				returnInfo.add(infoWrapper.getOntClassInfo());
				counter++;
				if (counter == numberOfReturns) {
					break;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnInfo;
	}
}

class ClassInfoWrapper implements Comparable<ClassInfoWrapper> {

	private double similarity;
//	protected String className;
	protected OntClassInfo classInfo;
//	protected String NS;
	
	public ClassInfoWrapper(double similarity, OntClassInfo classInfo) {
		this.similarity = similarity;
		this.classInfo = classInfo;
//		this.className = classInfo.getLocalName().trim();
//		this.NS = classInfo.getNamespace();
	}
	
	@Override
	public int compareTo(ClassInfoWrapper other) {
		if (this.getSimilarity() > other.getSimilarity()) {
			return -1;
		} else if (this.getSimilarity() < other.getSimilarity()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public OntClassInfo getOntClassInfo() {
		return this.classInfo;
	}

	public double getSimilarity() {
		return similarity;
	}
}
