package utils;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import utils.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import models.Feed;
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
	public void readFeedTest() {
		try {
			String fileName = "test/fixtures/rss-091.xml";
			RSSFeedParser parser = new RSSFeedParser(fileName, true);
			Feed feed = parser.readFeed();
			
			String TITLE = "WriteTheWeb";
			String LINK = "http://writetheweb.com";
			String LANGUAGE = "en-us";
			String DESCRIPTION = "News for web users that write back";
			String TYPE = "rss";
			String VERSION = "0.91";
			assertThat(feed.getTitle()).isEqualTo(TITLE);
			assertThat(feed.getLink()).isEqualTo(LINK);
			assertThat(feed.getLanguage()).isEqualTo(LANGUAGE);
			assertThat(feed.getDescription()).isEqualTo(DESCRIPTION);
			assertThat(feed.getPubDate()).isEqualTo(null);
			assertThat(feed.getType()).isEqualTo(TYPE);
			assertThat(feed.getVersion()).isEqualTo(VERSION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
