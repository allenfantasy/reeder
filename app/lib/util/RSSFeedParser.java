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

import models.Article;
import models.Feed;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

/*
 * RSS is a Web content format.
 * It's name a an acronym of "Really Simple Syndication"
 * RSS is dialect of XML. All RSS files must conform XML 1.0 specification.
 * At the top level, a RSS document is a <rss> element, with a mandatory attribute called version.
 */
public class RSSFeedParser implements FeedParser {
 	static final String XML_FILENAME = "feed.xml";
  	
  static final String TITLE = "title";
  static final String DESCRIPTION = "description";
  static final String CHANNEL = "channel";
  static final String LANGUAGE = "language";
  static final String COPYRIGHT = "copyright";
  static final String LINK = "link";
  static final String AUTHOR = "author";
  static final String ITEM = "item";
  static final String PUB_DATE = "pubDate";
  static final String GUID = "guid";
  
  static final String FEED_TYPE = "rss";
  
  final URL url;
	
  public RSSFeedParser(String feedUrl) throws MalformedURLException {
  	this.url = new URL(feedUrl);
  }
  
  /*
   * get rss feed from URL or local file
   * helper constructor for testing.
   */
  public RSSFeedParser(String fileName, Boolean isFile) throws MalformedURLException {
  	this.url = isFile ? new File(fileName).toURI().toURL()
  			              : new URL(fileName);
  }
  
  public Feed readFeed() throws IOException {
  	Feed feed = null;
  	try {
  		print2File();
  		
  		String title;
  		String link;
  		String pubDate;
  		String description;
  		String language;
  		String author;
  		String guid;
  		String version = null;
  		
  		File xmlFile = new File(XML_FILENAME);
  		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  		Document doc = dBuilder.parse(xmlFile);
  		
  		//optional but recommended
  		doc.getDocumentElement().normalize();
  		Node rssNode = doc.getElementsByTagName("rss").item(0);
  		
  	  if (rssNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element rssElem = (Element) rssNode;
  	  	version = rssElem.getAttribute("version");
  	  	// TODO: throw "invalid rss feed" exception if version is null
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
					/*
					 * RSS 0.92 Compatibility
					 * 
					 * In 0.91 <link> and <title> are `required` sub-element of <item>
					 * and <description> is `optional`
					 * In 0.92 all sub-elements are optional, to reflect actual
					 * pratice by bloggers, who are often proficient HTML coders.
					 * 
					 * Possible sub-elements:
					 * ======================
					 * 
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
					 * 
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
					 * 
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
					 * 
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
					 */
					
					/*
					 * RSS 2.0 Compatibility
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
  		
  	} catch (Exception e) {
  		e.printStackTrace();
  	}
  	return feed;
  }
  
  public List<Article> getArticles() {
  	List<Article> articles = new ArrayList<Article>();
  	try {
  		print2File();
  		String title;
  		String link;
  		String pubDate;
  		String description;
  		String author;
  		String guid;
  		File xmlFile = new File(XML_FILENAME);
  		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  		Document doc = dBuilder.parse(xmlFile);
  		
  		//optional but recommended
  		doc.getDocumentElement().normalize();
  	  Node channelNode = doc.getElementsByTagName("channel").item(0);
  	  
  	  if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element elem = (Element) channelNode;
				
				NodeList items = elem.getElementsByTagName("item");
				for(int index = 0; index < items.getLength(); index++) {
					Element itemElem = (Element) items.item(index);
					
					// <description> exists both in 0.91, 0.92, 2.0
					description = DOMUtil.getElementContent(itemElem, "description");

					// <title> & <link> exists in 0.91, 2.0
					title = DOMUtil.getElementContent(itemElem, "title");
					link = DOMUtil.getElementContent(itemElem, "link");
					
					// TODO: compatible with RSS 0.92

					author = DOMUtil.getElementContent(itemElem, "author");
					pubDate = DOMUtil.getElementContent(itemElem, "pubDate");
					guid = DOMUtil.getElementContent(itemElem, "guid");
					Article article = new Article(null, author, description, guid,
							link, title, pubDate);
					articles.add(article);
				}
  	  }
  		
  	} catch (Exception e) {
  		throw new RuntimeException(e);
  	}
	  return articles;
  }
  
  public void print2File() throws IOException {
  	// PLAN A
  	/*InputStream inStream = read();
  	BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
    String inputLine;
  	File targetFile = new File(XML_FILENAME);
  	if (!targetFile.exists()) targetFile.createNewFile();
  	OutputStream outStream = new FileOutputStream(targetFile);
  	
    while ((inputLine = in.readLine()) != null) {
    	byte[] bytes = inputLine.getBytes();
    	outStream.write(bytes);
    }
  	try {
    	in.close();
    	inStream.close();
    	outStream.close();
  	} catch (IOException e) {
  		e.printStackTrace();
  	}*/

  	// PLAN B
  	InputStream inStream = read();
  	File targetFile = new File(XML_FILENAME);
  	if (!targetFile.exists()) targetFile.createNewFile();
  	
  	FileUtils.copyInputStreamToFile(inStream, targetFile);
  }
  
  public String getSourceURL() {
  	return this.url.toString();
  }
  
  private InputStream read() {
  	try {
  		return url.openStream();
  	} catch (IOException e) {
  		throw new RuntimeException(e);
  	}
  }
}
