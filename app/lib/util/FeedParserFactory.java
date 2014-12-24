package lib.util;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class FeedParserFactory {
	public FeedParserFactory() {
		
	}

	public static FeedParserFactory newInstance() {
  	return new FeedParserFactory();
  }
	
  public FeedParser createFeedParser(String url) throws MalformedURLException {
    return isAtom(url) ? new AtomFeedParser(url) : new RSSFeedParser(url);
  }
  
  /*
   * Source: http://stackoverflow.com/questions/7591097/determining-whether-a-feed-is-atom-or-rss
   */
  private boolean isAtom(String url) {
  	try {
  		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    	f.setNamespaceAware(true);
    	DocumentBuilder builder = f.newDocumentBuilder();
    	Document doc = builder.parse(url);
    	Element e = doc.getDocumentElement();
    	return e.getLocalName().equals("feed") &&
    			e.getNamespaceURI().equals("http://www.w3.org/2005/Atom");
  	} catch (Exception e) {
  		// TODO: throw custom exception
  		return false;
  	}
  	
  }
}
