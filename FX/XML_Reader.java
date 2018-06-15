package application.support;

import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * XML Reader class
 *
 */
public class XML_Reader {

	private Document doc;

	/** Constructor of the XML_Reader CLass
	 * @param xmlPath
	 */
	public XML_Reader(String xmlPath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			this.doc = builder.parse(xmlPath);
		} catch (Exception e) {
			System.out.println("ERROR! --- Unable to read XML : "+xmlPath+"\n"+e);
		}
	}

	/** Returns matching node list based on the xpath
	 * @param xpathExpr
	 * @return NodeList
	 */
	public NodeList getNodeListFromXPATH(String xpathExpr) {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(xpathExpr);
			NodeList nodeList = (NodeList) expr.evaluate(this.doc, XPathConstants.NODESET);
			return nodeList;
		} catch (Exception e) {
			System.out.println("ERROR! --- Unbale to evaluate the xpath expression : "+xpathExpr+"\n"+e);
			return null;
		}
	}

}
