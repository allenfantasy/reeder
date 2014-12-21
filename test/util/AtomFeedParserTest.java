package util;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import models.Feed;
import models.Article;
import lib.util.*;

public class AtomFeedParserTest {
  
	@Test
	public void getSourceURLTest() {
		String url = "http://www.guokr.com/rss";
		try {
		  AtomFeedParser parser = new AtomFeedParser(url);
			assertThat(parser.getSourceURL()).isEqualTo(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readFeedTest() {
		try {
			String fileName = "test/fixtures/atom.xml";
			AtomFeedParser parser = new AtomFeedParser(fileName, true);
			Feed feed = parser.readFeed();
			
			String TITLE = "Example Feed";
			String LINK = "http://example.org/feed/";
			String DESCRIPTION = "A subtitle.";
			String PUB_DATE = "2003-12-13T18:30:02Z";
			String TYPE = "atom";
			String VERSION = "1.0";
			
			assertThat(feed.getTitle()).isEqualTo(TITLE);
			assertThat(feed.getLink()).isEqualTo(LINK);
			assertThat(feed.getDescription()).isEqualTo(DESCRIPTION);
			assertThat(feed.getPubDate()).isEqualTo(PUB_DATE);
			assertThat(feed.getType()).isEqualTo(TYPE);
			assertThat(feed.getVersion()).isEqualTo(VERSION);
			
			// Test items
			LinkedHashSet<Article> articles = (LinkedHashSet<Article>) feed.getArticles();
			Iterator<Article> iter = articles.iterator();
			Article item = (Article) iter.next();
			
			// First item
			String ITEM_TITLE = "Atom-Powered Robots Run Amok";
			String ITEM_LINK = "http://example.org/2003/12/13/atom03";
			String ITEM_GUID = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a";
			String ITEM_PUBDATE = "2003-12-13T18:30:02Z";
			String ITEM_DESCRIPTION = "Some text.";
			String ITEM_AUTHOR = "John Doe";
			assertThat(item.getTitle()).isEqualTo(ITEM_TITLE);
			assertThat(item.getLink()).isEqualTo(ITEM_LINK);
			assertThat(item.getGuid()).isEqualTo(ITEM_GUID);
			assertThat(item.getPubDate()).isEqualTo(ITEM_PUBDATE);
			assertThat(item.getDescription()).isEqualTo(ITEM_DESCRIPTION);
		  assertThat(item.getAuthor()).isEqualTo(ITEM_AUTHOR);	
			
		  // Second item
		  item = (Article) iter.next();
		  ITEM_TITLE = "Custom entry title";
		  ITEM_LINK = "http://allen.fantasy.me/2014/12/20";
		  ITEM_GUID = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6b";
		  ITEM_PUBDATE = "2007-12-13T18:30:02Z";
		  ITEM_DESCRIPTION = "This is the real content";
		  ITEM_AUTHOR = "Pineapple Allen";
			assertThat(item.getTitle()).isEqualTo(ITEM_TITLE);
			assertThat(item.getLink()).isEqualTo(ITEM_LINK);
			assertThat(item.getGuid()).isEqualTo(ITEM_GUID);
			assertThat(item.getPubDate()).isEqualTo(ITEM_PUBDATE);
			assertThat(item.getDescription()).isEqualTo(ITEM_DESCRIPTION);
		  assertThat(item.getAuthor()).isEqualTo(ITEM_AUTHOR);	

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getArticlesTest() {
		// TODO
	}
}