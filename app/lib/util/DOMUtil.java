package lib.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMUtil {
	public static String getElementContent(Element elem, String name) {
  	Node item = elem.getElementsByTagName(name).item(0);
  	return (item == null) ? null : item.getTextContent();
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
}
