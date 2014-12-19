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
  
  final URL url;
	
  public RSSFeedParser(String feedUrl) throws MalformedURLException {
  	this.url = new URL(feedUrl);
  }
  
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
  		String type = "rss";
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
  	  }
  	  Node channelNode = doc.getElementsByTagName("channel").item(0);
  	  if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
  	  	Element elem = (Element) channelNode;
  	  	
  	  	// create feed
  	  	title = DOMUtil.getElementContent(elem, "title");
  	  	link = DOMUtil.getElementContent(elem, "link");
  	  	pubDate = DOMUtil.getElementContent(elem, "pubDate");
  	  	description = DOMUtil.getElementContent(elem, "description");
  	  	language = DOMUtil.getElementContent(elem, "language");
				feed = new Feed(title, link, description, language, pubDate,
						this.getSourceURL(), type, version);
				
				NodeList items = elem.getElementsByTagName("item");
				for(int index = 0; index < items.getLength(); index++) {
					Element itemElem = (Element) items.item(index);
					title = DOMUtil.getElementContent(itemElem, "title");
					description = DOMUtil.getElementContent(itemElem, "description");
					author = DOMUtil.getElementContent(itemElem, "author");
					pubDate = DOMUtil.getElementContent(itemElem, "pubDate");
					link = DOMUtil.getElementContent(itemElem, "link");
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
					title = DOMUtil.getElementContent(itemElem, "title");
					description = DOMUtil.getElementContent(itemElem, "description");
					author = DOMUtil.getElementContent(itemElem, "author");
					pubDate = DOMUtil.getElementContent(itemElem, "pubDate");
					link = DOMUtil.getElementContent(itemElem, "link");
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
