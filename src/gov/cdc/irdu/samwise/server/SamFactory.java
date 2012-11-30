/**
 * 
 */
package gov.cdc.irdu.samwise.server;

import gov.cdc.irdu.samwise.Config;
import gov.cdc.irdu.samwise.shared.sam.SAM;
import gov.cdc.irdu.samwise.shared.sam.SamExplanation;
import gov.cdc.irdu.samwise.shared.sam.SamGroup;
import gov.cdc.irdu.samwise.shared.sam.SamRating;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Joel M. Rives
 * Feb 23, 2011
 */
public class SamFactory {

	private static Config CONFIG = Config.getConfig();
	private static Logger LOG = Logger.getLogger(SamFactory.class);

	private List<SamGroup> samGroups;
	
	public SamFactory() throws DocumentException {
		if (null == samGroups)
			samGroups = parse(getSamDefinition());
	}
	
	public SAM getSamForDocument(gov.cdc.irdu.samwise.shared.Document document) {
		SAM sam = new SAM();
		sam.setDocument(document);
		List<SamGroup> groups = new ArrayList<SamGroup>();
		for (SamGroup group : samGroups) {
			groups.add(group.clone());
		}
		sam.setGroups(groups);
		return sam;
	}
	
	@SuppressWarnings("unchecked")
	private List<SamGroup> parse(String xml) throws DocumentException {
		List<SamGroup> groups = new ArrayList<SamGroup>();
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(xml));
		
		List<Node> groupNodes = (List<Node>) document.selectNodes("//group"); 
		
		for (Node node: groupNodes) {
			groups.add(parseGroup((Element)node));
		}
		
		return groups;
	}
	
	@SuppressWarnings("unchecked")
	private SamGroup parseGroup(Element groupElement) {
		SamGroup group = new SamGroup();
		group.setTitle(groupElement.attributeValue("name"));
		
		List<SamRating> ratings = new ArrayList<SamRating>();
		List<Node> itemNodes = (List<Node>) groupElement.elements("item");
		
		for (Node node: itemNodes) {
			ratings.add(parseRating((Element) node));
		}
		
		group.setRatings(ratings);
		return group;
	}
	
	private SamRating parseRating(Element ratingElement) {
		SamRating rating = new SamRating(ratingElement.attributeValue("value"));

		String hasAnalysis = ratingElement.attributeValue("hasAnalysis");
		if (null != hasAnalysis && hasAnalysis.equalsIgnoreCase("true"))
			rating.setHasAnalysis(true);

		SamExplanation explanation = new SamExplanation();
		Element element = ratingElement.element("explanation");
		explanation.setText(element.getTextTrim());
		
		element = ratingElement.element("superior");
		explanation.setSuperior(element.getTextTrim());
		explanation.setSuperiorList(parseList(element));
		
		element = ratingElement.element("adequate");
		explanation.setAdequate(element.getTextTrim());
		explanation.setAdequateList(parseList(element));
		
		element = ratingElement.element("not-suitable");
		explanation.setNotSuitable(element.getTextTrim());
		explanation.setNotSuitableList(parseList(element));
		
		rating.setExplanation(explanation);
		return rating;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> parseList(Element element) {
		Element list = element.element("list");
		
		if (null == list)
			return null;
		
		List<Node> itemNodes = list.selectNodes("list-item");
		List<String> values = new ArrayList<String>();
		
		for (Node node: itemNodes) {
			Element item = (Element) node;
			values.add(item.getTextTrim());
		}
		
		return values;
	}

	private String getSamDefinition() {
		Writer writer = null;
		try {
			Reader reader = new InputStreamReader(CONFIG.getSamXml());
			writer = new StringWriter();
			char[] buffer = new char[2048];
			
			for (int count = reader.read(buffer); count > -1; count = reader.read(buffer)) {
				writer.write(buffer, 0, count);
			}
		} catch (Exception e) {
			LOG.error("Failed to load SAM XML", e);
			return null;
		} 
		
		return writer.toString();
	}
	
}
