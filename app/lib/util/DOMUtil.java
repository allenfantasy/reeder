package lib.util;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class DOMUtil {
	public static String getElementContent(Element elem, String name) {
  	Node item = elem.getElementsByTagName(name).item(0);
  	return (item == null) ? null : item.getTextContent();
  }
	
	public static boolean subElementExists(Element elem, String name) {
  	Node item = elem.getElementsByTagName(name).item(0);
  	return (item != null);
	}
	
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
	 * ReadXml function. This is originally from the Jakarta Commons
	 * Modeler.
	 * 
	 * @author Costin Manolache
	 */
	
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
