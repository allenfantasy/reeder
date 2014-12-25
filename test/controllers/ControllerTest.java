package controllers;

import org.junit.*;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

import lib.util.*;
import models.*;

public abstract class ControllerTest {
	protected static FakeApplication fakeApplication;
	
	@Before
	public void startFakeApplication() {
		fakeApplication = fakeApplication();
	}

  @After
  public void shutdownFakeApplication() {
  	stop(fakeApplication);
  }

  //helper methods
	protected static void createSomeFeeds() {
		String atomFileName = "test/fixtures/atom.xml";
		String rssFileName = "test/fixtures/rss-091.xml";
		try {
			// Add Atom feed
			AtomFeedParser atomParser = new AtomFeedParser(atomFileName, true);
			Feed atomFeed = atomParser.readFeed();
			Feed.create(atomFeed);
			// Add RSS feed
			RSSFeedParser rssParser = new RSSFeedParser(rssFileName, true);
			Feed rssFeed = rssParser.readFeed();
			Feed.create(rssFeed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  /**
   * teardown: remove all articles & feeds
   */
	protected static void cleanupDatabase() {
	  SqlUpdate removeAllArticles = Ebean.createSqlUpdate("DELETE FROM article");
	  removeAllArticles.execute();
	  SqlUpdate removeAllFeeds = Ebean.createSqlUpdate("DELETE FROM feed");
	  removeAllFeeds.execute();
	}
	protected String doubleQuotify(String str) {
		return str.replace('`', '"');
	}
}