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

public class AtomFeedParser implements FeedParser {
  static final String XML_FILENAME = "atom.xml";
  
  final URL url;
  // TODO: many static finals..
  public AtomFeedParser(String feedUrl) throws MalformedURLException {
  	this.url = new URL(feedUrl);
  }
  
  public Feed readFeed() throws IOException {
  	Feed feed = null;
  	// TODO;
  	return feed;
  }
  
  public List<Article> getArticles() {
  	// TODO;
  	return new ArrayList<Article>();
  }
  
  public void print2File() throws IOException {
  	// TODO;
  }
  
  public String getSourceURL() {
  	return this.url.toString();
  }
}
