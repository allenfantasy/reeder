package lib.util;

// Java built-in packages
import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;

// Custom packages
import lib.exceptions.*;
import models.*;
import static lib.util.DOMUtil.*;

/*
 * RSS is a Web content format.
 * It's name a an acronym of "Really Simple Syndication"
 * RSS is dialect of XML. All RSS files must conform XML 1.0 specification.
 * At the top level, a RSS document is a <rss> element, with a mandatory attribute called version.
 */

/*
 * RSS Format
 * 
 * All RSS Format document construct like the following, in which <channel>
 * element is wrapped into a <rss> element.
 * 
 * <rss version="x.xx">
 *   <channel>...</channel>
 * </rss>
 * 
 * Inside <channel>, different versions of RSS have various specs.
 * Check the detailed specs from:
 * 
 * RSS 0.9:  http://www.rssboard.org/rss-0-9-0
 * RSS 0.91: http://www.rssboard.org/rss-0-9-1-netscape
 * RSS 0.92: http://www.rssboard.org/rss-0-9-2
 * RSS 1.0:  http://web.resource.org/rss/1.0
 * RSS 2.0:  http://validator.w3.org/feed/docs/rss2.html
 * 					 http://www.rssboard.org/rss-2-0
 * ================================================
 * Luckily, in all specs, the basic unit of Article in <channel> is <item>
 * 
 * Here we list some specs for <item>, differ from versions:
 * 
 * RSS 0.91
 * 
 * <link> and <title> are `required` sub-element of <item>
 * and <description> is `optional`
 * 
 * RSS 0.92
 * 
 * In RSS 0.92 all sub-elements are optional, to reflect actual
 * pratice by bloggers, who are often proficient HTML coders.
 * 
 * Possible sub-elements:
 * 
 * ======================
 * <source>:
 * It's value is the name of the RSS channel that
 * the item came from, derived from its <title>
 * It has one required attribute, url, which links to
 * the XMLization of the source
 * 
 * EXAMPLE
 * <source url="http://www.myapplemenu.com/cgi-bin/surfView.cgi
 * ?category=applesurf&mainfull=1000">AppleSurf</source>
 * ======================
 * <enclosure>:
 * It has three required attributes.
 * url says where the enclosure is located,
 * length says how big it is in bytes,
 * and type says what its type is, a standard MIME type.
 * 
 * EXAMPLE
 * <enclosure url="http://www.scripting.com/mp3s/weatherReportSuite.mp3"
 * length="12216320" type="audio/mpeg" />
 * ======================
 * <category>:
 * It has one optional attribute, domain, a string that
 * identifies a categorization taxonomy.
 * The value of the element is a forward-slash-separated string
 * that identifies a hierarchic location in the indicated taxonomy.
 * Processors may establish conventions for the interpretation of
 * categories.
 * 
 * EXAMPLE
 * <category domain="http://www.superopendirectory.com/">
 * standards/xsl/implementations</category>
 * 
 * <category domain="http://www.fool.com/cusips">MSFT</category>
 * ======================
 * <cloud>:
 * It specifies a Web service that supports the rssCloud
 * interface which can be implemented in HTTP-POST, XML-RPC or
 * SOAP 1.1
 * 
 * Its purpose is to allow processes to register with a cloud
 * to be notified of updates the channel.
 *
 * EXAMPLE
 * <cloud domain="data.ourfavoritesongs.com" port="80" path="/RPC2"
 * registerProcedure="ourFavoriteSongs.rssPleaseNotify"
 * protocol="xml-rpc"/>
 * ======================
 * 
 * We will implement <source>, <enclosure> and <category>
 * We will NOT implement <cloud>, since this service is only
 * a simple RSS aggregator and reader.
 * 
 * RSS 2.0
 * 
 * Elements of <item> are listed below:
 * 
 * <title>
 * <link>
 * <description>
 * <author>
 * <category>
 * <comments>
 * <enclosure>
 * <guid>
 * <pubDate>
 * <source>
 * 
 * All elements of an item are optional, however at least one of title or description must be present.
 */

/**
 * RSSFeedParser
 * parser rss format document into Feed object
 * 
 * @author allen
 *
 */
public class RSSFeedParser extends FeedParser {
  static final String FEED_TYPE = "rss";
  
  private final URL url;
  private String feed_type = FEED_TYPE;
	
  /**
   * Get RSS feed from URL
   * @param feedUrl
   * @throws MalformedURLException
   */
  public RSSFeedParser(String feedUrl) throws MalformedURLException {
  	this.url = new URL(feedUrl);
  }
  
  /**
   * Get RSS feed from URL or local file
   * helper constructor for testing.
   * @param fileName
   * @param isFile
   * @throws MalformedURLException
   */
  public RSSFeedParser(String fileName, boolean isFile) throws MalformedURLException {
  	this.url = isFile ? new File(fileName).toURI().toURL()
  			              : new URL(fileName);
  }
  
  /**
   * Fetch document from source URL and parse into Feed object
   * 
   * @return Feed object, which type is "rss"
   * @throws IOException
   * @throws InvalidFeedException
   */
  public Feed readFeed() throws IOException, InvalidFeedException {
  	Feed feed = null;
  	File xmlFile = null;
  	try {
  		InputStream inStream = read();
  		if (inStream == null) {
    		throw new InvalidRSSFeedException("fail to fetch url inputstream");
    	}
  		
  		String title;
  		String link;
  		String pubDate;
  		String description;
  		String language;
  		String author;
  		String guid;
  		String version = null;

  		Document doc = readXml(inStream);
  		
  		//optional but recommended
  		doc.getDocumentElement().normalize();
  		Node rssNode = doc.getElementsByTagName("rss").item(0);
  		
  	  if (rssNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element rssElem = (Element) rssNode;
  	  	version = rssElem.getAttribute("version");
  	  	if (version == null) {
  	  		throw new InvalidRSSFeedException("version is null");
  	  	}
  	  }
  	  Node channelNode = doc.getElementsByTagName("channel").item(0);
  	  if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element elem = (Element) channelNode;
  	  	
  	  	// create feed
  	  	title = DOMUtil.getElementContent(elem, "title");
  	  	link = DOMUtil.getElementContent(elem, "link");
  	  	pubDate = DOMUtil.getElementContent(elem, "pubDate");
  	  	if (pubDate == null) {
  	  		pubDate = DOMUtil.getElementContent(elem, "lastBuildDate");
  	  	}
  	  	
  	  	description = DOMUtil.getElementContent(elem, "description");
  	  	language = DOMUtil.getElementContent(elem, "language");
				feed = new Feed(title, link, description, language, pubDate,
						this.getSourceURL(), FEED_TYPE, version);
				
				NodeList items = elem.getElementsByTagName("item");
				for(int index = 0; index < items.getLength(); index++) {
					Element itemElem = (Element) items.item(index);
					

					// <description> exists both in 0.91, 0.92, 2.0
					description = DOMUtil.getElementContent(itemElem, "description");

					// <title> & <link> exists in 0.91, 2.0
					title = DOMUtil.getElementContent(itemElem, "title");
					link = DOMUtil.getElementContent(itemElem, "link");
					
					if (version.equals("0.92")) {
						// Implement <source>, <enclosure> and <category>
						if (DOMUtil.subElementExists(itemElem, "source")) {
							title = DOMUtil.getElementContent(itemElem, "source");
							Element sourceElem = (Element) elem.getElementsByTagName("source").item(0);
							link = sourceElem.getAttribute("url");
						} else if (DOMUtil.subElementExists(itemElem, "enclosure")) {
							Element enclosureElem = (Element) elem.getElementsByTagName("enclosure").item(0);
							link = enclosureElem.getAttribute("url");
						} else if (DOMUtil.subElementExists(itemElem, "category")) {
							Element categoryElem = (Element) elem.getElementsByTagName("category").item(0);
							link = categoryElem.getAttribute("domain");
						}
					}

					// <author>, <pubDate> and <guid> exists ONLY in RSS 2.0
					author = DOMUtil.getElementContent(itemElem, "author");
					pubDate = DOMUtil.getElementContent(itemElem, "pubDate");
					guid = DOMUtil.getElementContent(itemElem, "guid");
					Article article = new Article(feed, author, description, guid,
							link, title, pubDate);
					feed.getArticles().add(article);
				}
  	  }
  	} catch (InvalidFeedException e) {
  		InvalidFeedException exception = new InvalidRSSFeedException(e.getMessage());
  		exception.initCause(e);
  		throw exception;
  	} catch (SAXParseException e) {
  		InvalidFeedException exception = new InvalidRSSFeedException("invalid xml document");
  		exception.initCause(e);
  		throw exception;
  	} catch (Exception e) {
  		InvalidFeedException exception = new InvalidRSSFeedException(e.getMessage());
  		exception.initCause(e);
  		e.printStackTrace();
  		throw exception;
  	} finally {
  	  cleanFile(xmlFile);  		
  	}
  	return feed;
  }
  
  /**
   * Fetch latest Articles of Feed, and update the Feed
   * If feed is not updated, return empty list and do nothing
   * 
   * @param feed
   * @return new Articles of feed
   * @throws InvalidFeedException
   */
  public List<Article> fetchLatestArticles(Feed feed) throws InvalidFeedException {
  	List<Article> articles = new ArrayList<Article>();
  	File xmlFile = null;
  	try {
  		InputStream inStream = read();
  		if (inStream == null) {
    		throw new InvalidRSSFeedException("fail to fetch url inputstream");
    	}
  		
  		String title;
  		String link;
  		String pubDate;
  		String description;
  		String author;
  		String guid;
  		String version = null;

  		Document doc = readXml(inStream);
  		
  		//optional but recommended
  		doc.getDocumentElement().normalize();
  	  Node channelNode = doc.getElementsByTagName("channel").item(0);
  	  
  	  // get version of this RSS document
  		Node rssNode = doc.getElementsByTagName("rss").item(0);
  	  if (rssNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element rssElem = (Element) rssNode;
  	  	version = rssElem.getAttribute("version");
  	  	if (version == null) {
  	  		throw new InvalidRSSFeedException("version is null");
  	  	}
  	  }
  	  
  	  if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element elem = (Element) channelNode;

    		// check whether feed is updated, jump out if not.
  	  	pubDate = DOMUtil.getElementContent(elem, "pubDate");
  	  	if (pubDate == null) {
  	  		pubDate = DOMUtil.getElementContent(elem, "lastBuildDate");
  	  	}
  	  	System.out.println(feed.getPubDate());
  	  	System.out.println(pubDate);
  	  	
  	  	if (pubDate.equals(feed.getPubDate())) { // check value equality via .equals()
  	  		System.out.println("pubDate equals");
  	  		return articles;
  	  	}
  	  	else {
  	  		feed.setPubDate(pubDate);
  	  		System.out.println("pubDate not equal");
  	  		
  	  		NodeList items = elem.getElementsByTagName("item");
  				for(int index = 0; index < items.getLength(); index++) {
  					Element itemElem = (Element) items.item(index);
  					
  					// <description> exists both in 0.91, 0.92, 2.0
  					description = DOMUtil.getElementContent(itemElem, "description");

  					// <title> & <link> exists in 0.91, 2.0
  					title = DOMUtil.getElementContent(itemElem, "title");
  					link = DOMUtil.getElementContent(itemElem, "link");
  					
  					// compatible with RSS 0.92
  					if (version.equals("0.92")) {
  						// Implement <source>, <enclosure> and <category>
  						if (DOMUtil.subElementExists(itemElem, "source")) {
  							title = DOMUtil.getElementContent(itemElem, "source");
  							Element sourceElem = (Element) elem.getElementsByTagName("source").item(0);
  							link = sourceElem.getAttribute("url");
  						} else if (DOMUtil.subElementExists(itemElem, "enclosure")) {
  							Element enclosureElem = (Element) elem.getElementsByTagName("enclosure").item(0);
  							link = enclosureElem.getAttribute("url");
  						} else if (DOMUtil.subElementExists(itemElem, "category")) {
  							Element categoryElem = (Element) elem.getElementsByTagName("category").item(0);
  							link = categoryElem.getAttribute("domain");
  						}
  					}

  					author = DOMUtil.getElementContent(itemElem, "author");
  					pubDate = DOMUtil.getElementContent(itemElem, "pubDate");
  					guid = DOMUtil.getElementContent(itemElem, "guid");
  					
  					// create Article
  					Article article = new Article(feed, author, description, guid,
  							link, title, pubDate);
  					
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
  		InvalidFeedException exception = new InvalidRSSFeedException(e.getMessage());
  		exception.initCause(e);
  		throw exception;
  	} catch (SAXParseException e) {
  		InvalidFeedException exception = new InvalidRSSFeedException(e.getMessage());
  		exception.initCause(e);
  		throw exception;
  	} catch (Exception e) {
  		InvalidFeedException exception = new InvalidRSSFeedException(e.getMessage());
  		exception.initCause(e);
  		e.printStackTrace();
  		throw exception; 
  	} finally {
  	  cleanFile(xmlFile);
  	}
  	
  	feed.save(); // update feed
  	System.out.println(feed.getArticles().size());
	  return articles;
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
  
  protected String getFeedType() {
  	return this.feed_type;
  }
  protected void cleanFile(File f) {
  	if (f != null) {
  		f.delete();
  	}
  }
  private InputStream read() {
  	try {
  		return url.openStream();
  	} catch (IOException e) {
  		return null;
  	}
  }
}
