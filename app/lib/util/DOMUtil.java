package lib.util;

// Java built-in packages
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * DOMUtil
 * Provide common operations towards org.w3c.dom.* a.k.a Document, Element, etc.
 * 
 * @author allen
 *
 */
public class DOMUtil {
	/**
	 * Get specified element's text content; null if specified element not found.
	 * 
	 * @param elem Parent element
	 * @param name Specified element name
	 * @return 
	 */
	public static String getElementContent(Element elem, String name) {
  	Node item = elem.getElementsByTagName(name).item(0);
  	return (item == null) ? null : item.getTextContent();
  }
	
	/**
	 * Check if specified element is a subElement of parent element
	 * 
	 * @param elem Parent element
	 * @param name Specified element name
	 * @return
	 */
	public static boolean subElementExists(Element elem, String name) {
  	Node item = elem.getElementsByTagName(name).item(0);
  	return (item != null);
	}
	
	/**
	 * 
	 * @param event
	 * @param eventReader
	 * @return
	 * @throws XMLStreamException
	 */
	public String getCharacterData(XMLEvent event, XMLEventReader eventReader)
      throws XMLStreamException {
  	String result = "";
  	event = eventReader.nextEvent();
  	if (event instanceof Characters) {
  		result = event.asCharacters().getData();
  	}
  	return result;
  }
	
	/**
	 * Get XML document from inputStream
	 * 
	 * @param inStream
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document readXml(InputStream inStream) throws SAXException, IOException,
	ParserConfigurationException {
		return MyXMLReader.readXml(inStream);
	}
}

/**
 * XMLReader. This is originally from the Jakarta Commons
 * Modeler.
 *  
 * @author Costin Manolache
 */
class MyXMLReader {
	public static Document readXml(InputStream inStream) throws SAXException, IOException,
		ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	
		//dbFactory.setValidating(true);
		dbFactory.setIgnoringComments(false);
		dbFactory.setIgnoringElementContentWhitespace(true);
		dbFactory.setNamespaceAware(true);
		//dbFactory.setCoalescing(true);
		//dbFactory.setExpandEntityReferences(true);
	
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		dBuilder.setEntityResolver(new NullResolver());
		//dBuilder.setErrorHandler(new MyErrorHandler());
	
	return dBuilder.parse(inStream);
	}
}
class NullResolver implements EntityResolver {
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
		IOException {
		return new InputSource(new StringReader(""));
	}
}
