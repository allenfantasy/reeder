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

public class RSSFeedParserTest {
  
	@Test
	public void getSourceURLTest() {
		String url = "http://www.36kr.com/feed";
		try {
			RSSFeedParser parser = new RSSFeedParser(url);
			assertThat(parser.getSourceURL()).isEqualTo(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readFeedTest_V091() {
		try {
			String fileName = "test/fixtures/rss-091.xml";
			RSSFeedParser parser = new RSSFeedParser(fileName, true);
			Feed feed = parser.readFeed();
			
			String TITLE = "WriteTheWeb"; 							// Required in 0.91
			String LINK = "http://writetheweb.com";			// Required in 0.91
			String LANGUAGE = "en-us"; 									// Required in 0.91
			String DESCRIPTION = "News for web users that write back"; // Required in 0.91
			String PUB_DATE = null; // <pubDate> & <lastBuildDate> are Optional in 0.91
			String TYPE = "rss";
			String VERSION = "0.91";

			assertThat(feed.getTitle()).isEqualTo(TITLE);
			assertThat(feed.getLink()).isEqualTo(LINK);
			assertThat(feed.getLanguage()).isEqualTo(LANGUAGE);
			assertThat(feed.getDescription()).isEqualTo(DESCRIPTION);
			assertThat(feed.getPubDate()).isEqualTo(PUB_DATE);
			assertThat(feed.getType()).isEqualTo(TYPE);
			assertThat(feed.getVersion()).isEqualTo(VERSION);
		
			// Test item
			String ITEM_TITLE = "Giving the world a pluggable Gnutella";
			String ITEM_LINK = "http://writetheweb.com/read.php?item=24";
			String ITEM_DESC = "WorldOS is a framework on which to build programs"
					+ " that work like Freenet or Gnutella -allowing distributed"
					+ " applications using peer-to-peer routing.";
			
			LinkedHashSet<Article> articles = (LinkedHashSet<Article>) feed.getArticles();
			Iterator<Article> iter = articles.iterator();
			Article firstItem = (Article) iter.next();
			
			assertThat(firstItem.getTitle()).isEqualTo(ITEM_TITLE);
			assertThat(firstItem.getLink()).isEqualTo(ITEM_LINK);
			assertThat(firstItem.getDescription()).isEqualTo(ITEM_DESC);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readFeedTest_V092() {
		try {
			String fileName = "test/fixtures/rss-092.xml";
			RSSFeedParser parser = new RSSFeedParser(fileName, true);
			Feed feed = parser.readFeed();
			
			// RSS 0.92 is upward-compatible with RSS 0.91
			String TITLE = "Dave Winer: Grateful Dead";
			String LINK = "http://www.scripting.com/blog/categories/gratefulDead.html";
			String LANGUAGE = null;  // <language> is Required in 0.91, but Optional in 0.92.
			String DESCRIPTION = "A high-fidelity Grateful Dead song every day. "
					+ "This is where we're experimenting with enclosures on RSS news items that download "
					+ "when you're not using your computer. If it works (it will) it will be the end of "
					+ "the Click-And-Wait multimedia experience on the Internet. ";
			String PUB_DATE = "Fri, 13 Apr 2001 19:23:02 GMT";
			String TYPE = "rss";
			String VERSION = "0.92";
			
			assertThat(feed.getTitle()).isEqualTo(TITLE);
			assertThat(feed.getLink()).isEqualTo(LINK);
			assertThat(feed.getLanguage()).isEqualTo(LANGUAGE);
			assertThat(feed.getDescription()).isEqualTo(DESCRIPTION);
			assertThat(feed.getPubDate()).isEqualTo(PUB_DATE);
			assertThat(feed.getType()).isEqualTo(TYPE);
			assertThat(feed.getVersion()).isEqualTo(VERSION);

			// Test items
			String ENCLOSURE_ITEM_LINK = "http://www.scripting.com/mp3s/weatherReportDicksPicsVol7.mp3";
			String ENCLOSURE_ITEM_DESC = "It's been a few days since I added a song to the Grateful Dead channel. "
					+ "Now that there are all these new Radio users, many of whom are tuned into this channel (it's "
					+ "#16 on the hotlist of upstreaming Radio users, there's no way of knowing how many non-upstreaming "
					+ "users are subscribing, have to do something about this..). Anyway, tonight's song is a live "
					+ "version of Weather Report Suite from Dick's Picks Volume 7. It's wistful music. Of course "
					+ "a beautiful song, oft-quoted here on Scripting News. <i>A little change, the wind and rain.</i>\n";

			String SOURCE_ITEM_LINK = "http://scriptingnews.userland.com/xml/scriptingNews2.xml";
			String SOURCE_ITEM_TITLE = "Scripting News";
			// trick to have "double quote" in string-literal
			// Source: http://stackoverflow.com/questions/3034186/in-java-is-there-a-way-to-write-a-string-literal-without-having-to-escape-quote
			String SOURCE_ITEM_DESC = new String("Kevin Drennan started a <a href=`http://deadend.editthispage.com/`>"
					+ "Grateful Dead Weblog</a>. Hey it's cool, he even has a <a href=`http://deadend.editthispage.com/directory/61`>"
					+ "directory</a>. <i>A Frontier 7 feature.</i>").replace('`', '"');
			
			LinkedHashSet<Article> articles = (LinkedHashSet<Article>) feed.articles;
			Iterator<Article> iter = articles.iterator();
			Article enclosureItem = (Article) iter.next();
			Article sourceItem = (Article) iter.next();
			
			assertThat(enclosureItem.getLink()).isEqualTo(ENCLOSURE_ITEM_LINK);
			assertThat(enclosureItem.getDescription()).isEqualTo(ENCLOSURE_ITEM_DESC);

			assertThat(sourceItem.getTitle()).isEqualTo(SOURCE_ITEM_TITLE);
			assertThat(sourceItem.getLink()).isEqualTo(SOURCE_ITEM_LINK);
			assertThat(sourceItem.getDescription()).isEqualTo(SOURCE_ITEM_DESC);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readFeedTest_V2() {
		try {
			String filename = "test/fixtures/rss-2.xml";
			RSSFeedParser parser = new RSSFeedParser(filename, true);
			Feed feed = parser.readFeed();
			
			// RSS 2.0
			String TITLE = "Liftoff News";
			String LINK = "http://liftoff.msfc.nasa.gov/";
			String LANGUAGE = "en-us";  // <language> is Optional in 2.0
			String DESCRIPTION = "Liftoff to Space Exploration.";
			String PUB_DATE = "Tue, 10 Jun 2003 04:00:00 GMT";
			String TYPE = "rss";
			String VERSION = "2.0";

			assertThat(feed.getTitle()).isEqualTo(TITLE);
			assertThat(feed.getLink()).isEqualTo(LINK);
			assertThat(feed.getLanguage()).isEqualTo(LANGUAGE);
			assertThat(feed.getDescription()).isEqualTo(DESCRIPTION);
			assertThat(feed.getPubDate()).isEqualTo(PUB_DATE);
			assertThat(feed.getType()).isEqualTo(TYPE);
			assertThat(feed.getVersion()).isEqualTo(VERSION);

			// Test item
			String ITEM_TITLE = "Star City";
			String ITEM_LINK = "http://liftoff.msfc.nasa.gov/news/2003/news-starcity.asp";
			String ITEM_DESC = new String("How do Americans get ready to work with Russians aboard the"
					+ " International Space Station? They take a crash course in culture, language and "
					+ "protocol at Russia's <a href=`http://howe.iki.rssi.ru/GCTC/gctc_e.htm`>Star City</a>.").replace('`', '"'); // same trick
			String ITEM_PUBDATE = "Tue, 03 Jun 2003 09:39:21 GMT";
			String ITEM_GUID = "http://liftoff.msfc.nasa.gov/2003/06/03.html#item573";
			
			LinkedHashSet<Article> articles = (LinkedHashSet<Article>) feed.articles;
			Iterator<Article> iter = articles.iterator();
			Article firstItem = (Article) iter.next();
			
			assertThat(firstItem.getTitle()).isEqualTo(ITEM_TITLE);
			assertThat(firstItem.getLink()).isEqualTo(ITEM_LINK);
			assertThat(firstItem.getDescription()).isEqualTo(ITEM_DESC);
			assertThat(firstItem.getPubDate()).isEqualTo(ITEM_PUBDATE);
			assertThat(firstItem.getGuid()).isEqualTo(ITEM_GUID);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getArticlesTest() {
		// TODO
	}
}
