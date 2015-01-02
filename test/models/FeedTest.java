package models;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import models.*;

public class FeedTest extends ModelTest {
	private static final String TITLE = "FeedTitle";
	private static final String LINK = "http://blog.dxhackers.com/feed";
	private static final String DESC = "Feed description";
	private static final String LANG = "en-us";
	private static final String PUB_DATE = "2015-01-01";
	private static final String SOURCE = "http://blog.dxhackers.com";
	private static final String TYPE = "rss";
	private static final String VERSION = "2.0";
	
	private static final String EMAIL = "allen@dxhackers.com";
	private static final String USERNAME = "allenfantasy";
	private static final String PASSWORD = "1234567";
	
  /*@Test
  public void createTest() {
  	// when one feed is created, it MUST have a user attached
  	Feed f = new Feed(TITLE, LINK, DESC, LANG, PUB_DATE
  			, SOURCE, TYPE, VERSION);
  	User u = createUser(EMAIL, USERNAME, PASSWORD);
  	
  	f.save();
  }*/
  
  private User createUser(String email, String username,
			String password) {
  	User u = new User(email, username, password);
  	u.save();

  	return u;
  }
}
