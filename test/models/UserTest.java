package models;

//Java built-in packages
import org.junit.*;

// 3rd Party's packages (including Play)
import javax.persistence.PersistenceException;
import play.mvc.*;
import play.test.*;
import static org.fest.assertions.Assertions.*;

// Custom packages
import lib.util.*;

public class UserTest extends ModelTest {
	private static final String EMAIL = "allen@dxhackers.com";
	private static final String USERNAME = "allenfantasy";
	private static final String PASSWORD = "fantasy236950";
	
	private static final String RSS = "rss";
	private static final String ATOM = "atom";
	
	@Test
	public void saveTest() {
		User user = createUser(EMAIL, USERNAME, PASSWORD);
		assertThat(user.id).isNotNull();
		assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
	}
	
  @Test
  public void existTest() {
  	assertThat(User.all().size()).isEqualTo(0);
  	User user = createUser(EMAIL, USERNAME, PASSWORD);
  	assertThat(User.exists(EMAIL)).isEqualTo(true);
  	assertThat(User.all().size()).isEqualTo(1);
  }
  
  @Test
  public void uniqueTest() {
  	User user1 = createUser(EMAIL, USERNAME, PASSWORD);
  	user1.save();
  	try {
  		User user2 = createUser(EMAIL, "allen", PASSWORD);
  	} catch (Exception e) {
  		assertThat(e.getClass()).isEqualTo(PersistenceException.class);
  		//e.printStackTrace();
  	}
  }
  
  @Test
  public void authenticateTest() {
  	User user = createUser(EMAIL, USERNAME, PASSWORD);
  	assertThat(user.authenticate("6378921")).isEqualTo(false);
  	assertThat(user.authenticate(PASSWORD)).isEqualTo(true);
  }
  
  @Test
  public void feedsTest() {
  	User user = createUser(EMAIL, USERNAME, PASSWORD);
  	assertThat(user.getFeeds().size()).isEqualTo(0);
  	
  	Feed f = createFeed("test/fixtures/rss-091.xml", RSS);
  	assertThat(f.getUser()).isEqualTo(null);
  	
  	user.addFeed(f);
  	assertThat(user.getFeeds().size()).isEqualTo(1);
  	
  	// actually, this relationship is not sync to database yet.
  	// here's the proof
  	User user1 = User.findByEmail(EMAIL);
  	assertThat(user1.getFeeds().size()).isEqualTo(0);
  	
  	user.save();
  	User user2 = User.findByEmail(EMAIL);
  	assertThat(user2.getFeeds().size()).isEqualTo(1);
  }
  
  // factory methods
  
  private User createUser(String email, String username,
  												String password) {
  	User u = new User(email, username, password);
  	u.save();
  	
  	return u;
  }
  private Feed createFeed(String filename, String type) {
  	FeedParser parser;
  	Feed feed = null;
  	try {
  		if (type == RSS) {
  			parser = new RSSFeedParser(filename, true);
  		} else if (type == ATOM) {
  			parser = new AtomFeedParser(filename, true);
  		} else {
  			throw new Exception();
  		}
  		feed = parser.readFeed();
  		Feed.create(feed);
  		return feed;
  	} catch (Exception e) {
  		e.printStackTrace();
  		return feed;
  	}
  }
}
