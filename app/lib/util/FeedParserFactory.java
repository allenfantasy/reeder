package lib.util;

// Java built-in packages
import java.net.MalformedURLException;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * FeedParserFactory
 * Accept a source URL and build RSSFeedParser / AtomFeedParser based on content type.
 * 
 * @author allen
 *
 */
public class FeedParserFactory {
	public FeedParserFactory() {
		
	}

	/**
	 * factory method
	 * 
	 * @return
	 */
	public static FeedParserFactory newInstance() {
  	return new FeedParserFactory();
  }

	/**
	 * Build feed parser
	 * 
	 * @param url source URL
	 * @return FeedParser object
	 * @throws MalformedURLException
	 */
  public FeedParser createFeedParser(String url) throws MalformedURLException {
    return isAtom(url) ? new AtomFeedParser(url) : new RSSFeedParser(url);
  }
  
  /**
   * Check if the source is atom.
   * Refer: http://stackoverflow.com/questions/7591097/determining-whether-a-feed-is-atom-or-rss
   * 
   * @param url source URL
   * @return
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
