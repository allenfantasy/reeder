package lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lib.exceptions.InvalidAtomFeedException;
import lib.exceptions.InvalidFeedException;
import models.Article;
import models.Feed;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

public class AtomFeedParser extends FeedParser {
  static final String FEED_TYPE = "atom";

  final URL url;
  private String feed_type = FEED_TYPE;

  public AtomFeedParser(String feedUrl) throws MalformedURLException {
  	this.url = new URL(feedUrl);
  }
  
  /**
   * get atom feed from URL or local file
   * helper constructor for testing
   */
  public AtomFeedParser(String filename, boolean isFile) throws MalformedURLException {
  	this.url = isFile ? new File(filename).toURI().toURL()
  										: new URL(filename);
  }
  
  public Feed readFeed() throws IOException, InvalidFeedException {
  	Feed feed = null;
  	File xmlFile = null;
  	try {
  		String filePath = writeFeed2File();  // throws InvalidAtomFeedException
  		
  		/*
  		 * Atom Format (RFC4287)
  		 * http://www.ietf.org/rfc/rfc4287.txt
  		 * 
  		 * Here lists all related sub-elements of <feed>:
  		 * 
  		 * <title>    atom:title   -- REQUIRED
  		 * <link>     atom:link    -- REQUIRED
  		 * <updated>  atom:updated -- REQUIRED
  		 * <author>   atom:author  -- REQUIRED
  		 *    Note: REQUIRED unless all of child atom:entry elements contains <author>
  		 * <subtitle>   					 -- OPTIONAL
  		 */
  		String title; 							// <title>
  		String link = null; 				// <link> 
  		String guid; 								// <id> REQUIRED
  		String description; 				// <subtitle>
  		String author = null;    		// <author> -> <name>
  		String pubDate;     				// <updated>
  		String language = null;    	// no element respectively
  		String version = "1.0";
  		
  		String linkHref;
  		
  		xmlFile = new File(filePath);
  		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  		Document doc = dBuilder.parse(xmlFile);
  		
  		// optional but recommended
  		doc.getDocumentElement().normalize();
  		
  		Node atomNode = doc.getElementsByTagName("feed").item(0);
  		if (atomNode.getNodeType() == Node.ELEMENT_NODE) {
  			Element elem = (Element) atomNode;
  			
  			// create feed
  			title = DOMUtil.getElementContent(elem, "title");
  			// GETTING LINK
  			//link = DOMUtil.getElementContent(elem, "link");
				Node linkNode = elem.getElementsByTagName("link").item(0);
				linkHref = ((Element)linkNode).getAttribute("href");
				if (linkHref != null && !linkHref.equals("")) {
					link = linkHref;
				}
  			
  			guid = DOMUtil.getElementContent(elem, "id");
  			description = DOMUtil.getElementContent(elem, "subtitle");
  			// get <author> => <name>
  			Element authorElem = (Element) elem.getElementsByTagName("author").item(0);
  			author = DOMUtil.getElementContent(authorElem, "name");
  			pubDate = DOMUtil.getElementContent(elem, "updated");
  			
  			feed = new Feed(title, link, description, language, pubDate,
  					this.getSourceURL(), FEED_TYPE, version);
  			
  			// add articles to feed
  			NodeList items = elem.getElementsByTagName("entry");
  			for(int index = 0; index < items.getLength(); index++) {
  				Element itemElem = (Element) items.item(index);
  				/*
  				 * Atom Format (RFC4287)
  				 * http://www.ietf.org/rfc/rfc4287.txt
  				 * 
  				 * atom:entry <entry>
  				 * 
  				 * Here listed all related sub-elements of <entry>:
  				 * 
  				 * <id>     -- atom:id     REQUIRED
  				 * <summary> -- atom:summary REQUIRED
  				 *    Note: REQUIRED in either of the following cases:
  				 *    (1) the <entry> contains an <content> that has a "src" attribute (and thus empty)
  				 *    (2) the <entry> contains content that is encoded in Base64;
  				 *    i.e., the "type" attribute of <content> is a MIME media type [MIMEREG],
  				 *    but is not an XML media type [RFC3023], does not begin with "text/",
  				 *    and does not end with "/xml" or "+xml".
  				 *    
  				 * <title>  -- atom:title  REQUIRED
  				 * <updated> -- atom:updated REQUIRED
  				 * <author> -- atom:author REQUIRED
  				 *    Note: REQUIRED unless the <entry> contains an <source> element
  				 *          that contains an <author> OR in an Atom Feed Document,
  				 *          the <feed> contains an <author> itself
  				 * 
  				 * GETTING DESCRIPTION
  				 * 
  				 * IF <content> and <content> has value
  				 *   use value of <content>
  				 * ELSE
  				 *   use value of <summary>
  				 * 
  				 * GETTING LINK
  				 * 
  				 * IF <content> && <content> has "src" attribute
  				 *   use "src" attribute of <content>
  				 * ELSE IF <link>
  				 *   use the value of 1st <link>
  				 * ELSE
  				 *   null
  				 */
  				String itemDesc;  		       // check "GETTING DESCRIPTION"
  				String itemTitle;            // <title>
  				String itemLink = null;      // check "GETTING LINK"
  				String itemAuthor;           // <author> => <name>
  				String itemPubDate;          // <updated>
  				String itemGuid;             // <id>
  				
  				// GETTTING DESCRIPTION
  				itemDesc = DOMUtil.getElementContent(itemElem, "content");
  				if (itemDesc == null || itemDesc.equals("")) {
  					itemDesc = DOMUtil.getElementContent(itemElem, "summary");
  				}
  				
  				// GETTING LINK
  				Node contentNode = itemElem.getElementsByTagName("content").item(0);
  				if (contentNode != null) {
  					String contentSrc = ((Element)contentNode).getAttribute("src");
  					if (contentSrc != null && !contentSrc.equals("")) {
  						itemLink = contentSrc;
  					}
  				}
  				if (itemLink == null) {
  					Node itemLinkNode = itemElem.getElementsByTagName("link").item(0);
  					String itemLinkHref = ((Element)itemLinkNode).getAttribute("href");
  					if (itemLinkHref != null && !itemLinkHref.equals("")) {
  						itemLink = itemLinkHref;
  					}
  				}
  				// GETTING AUTHOR
  				if (DOMUtil.subElementExists(itemElem, "author")) {
  					Element itemAuthorElem = (Element) itemElem.getElementsByTagName("author").item(0);
  					itemAuthor = DOMUtil.getElementContent(itemAuthorElem, "name");
  				} else {
  					itemAuthor = author;
  				}
  				
  				itemTitle = DOMUtil.getElementContent(itemElem, "title");
  				itemPubDate = DOMUtil.getElementContent(itemElem, "updated");
  				itemGuid = DOMUtil.getElementContent(itemElem, "id");
  				
  				// create article
  				Article article = new Article(feed, itemAuthor, itemDesc, itemGuid,
  						itemLink, itemTitle, itemPubDate);
  				feed.getArticles().add(article);
  			}
  		}
  	} catch (InvalidFeedException e) {
  		InvalidFeedException exception = new InvalidAtomFeedException(e.getMessage());
  		exception.initCause(e);
  		throw exception;
  	} catch (Exception e) {
  		InvalidFeedException exception = new InvalidAtomFeedException(e.getMessage());
  		exception.initCause(e);
  		e.printStackTrace();
  		throw exception;
  	} finally {
  		cleanFile(xmlFile);
  	}
  	return feed;
  }
  
  public List<Article> fetchLatestArticles(Feed feed) throws InvalidFeedException {
  	List<Article> articles = new ArrayList<Article>();
  	File xmlFile = null;
  	try {
  		String filePath = writeFeed2File(); // throws InvalidAtomFeedException
  		
  		String author = null;    		// <author> -> <name>
  		String pubDate;
  		
  		xmlFile = new File(filePath);
  		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  		Document doc = dBuilder.parse(xmlFile);
  		
  		// optional but recommended
  		doc.getDocumentElement().normalize();
  		
  		Node atomNode = doc.getElementsByTagName("feed").item(0);
  		
  		if (atomNode.getNodeType() == Node.ELEMENT_NODE) {
  			Element elem = (Element) atomNode;
  			
  			// check whether feed is updated, jump out if out.
  			pubDate = DOMUtil.getElementContent(elem, "updated");
  	  	System.out.println(feed.getPubDate());
  	  	System.out.println(pubDate);
  	  	
  			if (pubDate.equals(feed.getPubDate())) {
  				System.out.println("pubDate equals");
  				return articles;
  			}
  			else {
  				System.out.println("pubDate not equal");
  				feed.setPubDate(pubDate);
  				
  				// get <author> => <name>
    			Element authorElem = (Element) elem.getElementsByTagName("author").item(0);
    			author = DOMUtil.getElementContent(authorElem, "name");
    			NodeList items = elem.getElementsByTagName("entry");
    			for(int index = 0; index < items.getLength(); index++) {
    				Element itemElem = (Element) items.item(index);
    				String itemDesc;  		       // check "GETTING DESCRIPTION"
    				String itemTitle;            // <title>
    				String itemLink = null;      // check "GETTING LINK"
    				String itemAuthor;           // <author> => <name>
    				String itemPubDate;          // <updated>
    				String itemGuid;             // <id>
    				
    				// GETTTING DESCRIPTION
    				itemDesc = DOMUtil.getElementContent(itemElem, "content");
    				if (itemDesc == null || itemDesc.equals("")) {
    					itemDesc = DOMUtil.getElementContent(itemElem, "summary");
    				}
    				
    				// GETTING LINK
    				Node contentNode = itemElem.getElementsByTagName("content").item(0);
    				if (contentNode != null) {
    					String contentSrc = ((Element)contentNode).getAttribute("src");
    					if (contentSrc != null && !contentSrc.equals("")) {
    						itemLink = contentSrc;
    					}
    				}
    				if (itemLink == null) {
    					Node itemLinkNode = itemElem.getElementsByTagName("link").item(0);
    					String itemLinkHref = ((Element)itemLinkNode).getAttribute("href");
    					if (itemLinkHref != null && !itemLinkHref.equals("")) {
    						itemLink = itemLinkHref;
    					}
    				}
    				// GETTING AUTHOR
    				if (DOMUtil.subElementExists(itemElem, "author")) {
    					Element itemAuthorElem = (Element) itemElem.getElementsByTagName("author").item(0);
    					itemAuthor = DOMUtil.getElementContent(itemAuthorElem, "name");
    				} else {
    					itemAuthor = author;
    				}
    				
    				itemTitle = DOMUtil.getElementContent(itemElem, "title");
    				itemPubDate = DOMUtil.getElementContent(itemElem, "updated");
    				itemGuid = DOMUtil.getElementContent(itemElem, "id");
    				
    				// create Article
    				Article article = new Article(feed, itemAuthor, itemDesc, itemGuid,
    						itemLink, itemTitle, itemPubDate);
    				
    				// avoid duplicate articles
    				if (!feed.getArticles().contains(article)) {
    					System.out.println("ADD ARTICLE");
    					articles.add(article);
    					feed.getArticles().add(article);
    				}
    			}
  			}
  		}
  	} catch (InvalidFeedException e) {
  		InvalidFeedException exception = new InvalidAtomFeedException(e.getMessage());
  		exception.initCause(e);
  		throw exception;
  	} catch (Exception e) {
  		InvalidFeedException exception = new InvalidAtomFeedException(e.getMessage());
  		exception.initCause(e);
  		e.printStackTrace();
  		throw exception;
  	} finally {
  		cleanFile(xmlFile);
  	}
  	
  	feed.save(); // update feed
  	
  	return articles;
  }
  
  public String writeFeed2File() throws InvalidFeedException {
  	String filePath = generateRandomFilepath();
  	InputStream inStream = read();
  	File targetFile;
  	if (inStream == null) {
  		throw new InvalidAtomFeedException("fail to fetch url inputstream");
  	}
  	try {
  		targetFile = new File(filePath);
  		if (!targetFile.exists()) targetFile.createNewFile();
  	} catch (IOException e) {
  		throw new InvalidAtomFeedException("cannot create file");
  	}
  	try {
  		FileUtils.copyInputStreamToFile(inStream, targetFile);
  	} catch (IOException e) {
  		throw new InvalidAtomFeedException("cannot read inputstream to file");
  	}
  	return filePath;
  }
  
  public String getSourceURL() {
  	return this.url.toString();
  }
  
  /**
   * Only for testing
   * @return {String} a random file path
   */
  public String getRandomPath() {
  	return generateRandomFilepath();
  }
  
  /**
   * Provide feed type for generateRandomFilepath()
   */
  protected String getFeedType() {
  	return this.feed_type;
  }
  protected void cleanFile(File f) {
  	if (f != null) f.delete();
  }
  
  private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			return null;
		}
	}
  
}
