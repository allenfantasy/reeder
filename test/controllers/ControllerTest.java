package controllers;

// Java built-in packages
import java.util.*;
import org.junit.*;

// 3rd Party's packages (include Play)
import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import com.avaje.ebean.*;

// Custom packages
import lib.util.*;
import models.*;

public abstract class ControllerTest {
	protected static FakeApplication fakeApplication;
	
	@Before
	public void startFakeApplication() {
		fakeApplication = fakeApplication();
		//System.out.println("ControllerTest#start");
	}

  @After
  public void shutdownFakeApplication() {
		//System.out.println("ControllerTest#shutdown");
  	stop(fakeApplication);
  }

  //helper methods
  protected static User createUser(String email, String username,
  		String password) {
  	User u = new User(email, username, password);
  	u.save();
  	return u;
  }
	protected static List<Feed> createSomeFeeds() {
		List<Feed> feeds = new ArrayList<Feed>();
		String atomFileName = "test/fixtures/atom.xml";
		String rssFileName = "test/fixtures/rss-091.xml";
		try {
			// Add Atom feed
			AtomFeedParser atomParser = new AtomFeedParser(atomFileName, true);
			Feed atomFeed = atomParser.readFeed();
			Feed.create(atomFeed);
			feeds.add(atomFeed);
			// Add RSS feed
			RSSFeedParser rssParser = new RSSFeedParser(rssFileName, true);
			Feed rssFeed = rssParser.readFeed();
			Feed.create(rssFeed);
			feeds.add(rssFeed);
			return feeds;
		} catch (Exception e) {
			e.printStackTrace();
			return feeds;
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
	  SqlUpdate removeAllUsers = Ebean.createSqlUpdate("DELETE FROM user");
	  removeAllUsers.execute();
	}
	protected String doubleQuotify(String str) {
		return str.replace('`', '"');
	}
}