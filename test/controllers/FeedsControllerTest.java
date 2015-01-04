package controllers;

import org.junit.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.JOSEException;

import play.mvc.*;
import play.test.*;
import play.libs.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import java.util.*;

import models.*;
import lib.Util.*;

public class FeedsControllerTest extends ControllerTest {
	protected static User u;
	private static final String JWT_HEADER = "Authorization";
	private static final String USER_EMAIL = "allen@dxhackers.com";
	private static final String USER_NAME = "allenfantasy";
	private static final String USER_PASSWORD = "fantasy32097";
	
	@Test
	public void callIndex_JWT() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				try {
					String token;
					JsonNode resultNode;
					Result result;
					u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);

					// case: expired
					token = u.createJWT(Calendar.SECOND, 1); // expire after 1 second
					Thread.sleep(1000); // sleep one second. Now the token above should be expired
					
					result = callAction(
						controllers.routes.ref.FeedsController.index(),
						fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
					);
					resultNode = Json.parse(contentAsString(result));
					assertThat(status(result)).isEqualTo(UNAUTHORIZED);
					assertThat(resultNode.get("message").textValue()).isEqualTo("Expired token");
					assertThat(resultNode.get("code").asInt()).isEqualTo(AuthenticateAction.EXPIRED_TOKEN);

					// case: invalid/malicious token
					token = "aslgjasklgjasldjglasdjgljasg";
				  result = callAction(
				  	controllers.routes.ref.FeedsController.index(),
				  	fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
				  );
				  resultNode = Json.parse(contentAsString(result));
				  assertThat(status(result)).isEqualTo(UNAUTHORIZED);
				  assertThat(resultNode.get("message").textValue()).isEqualTo("Invalid token");
				  assertThat(resultNode.get("code").asInt()).isEqualTo(AuthenticateAction.INVALID_TOKEN);
				  
				  // case: valid token
				  token = u.createJWT();
				  result = callAction(
				  	controllers.routes.ref.FeedsController.index(),
				  	fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
				  );
				  resultNode = Json.parse(contentAsString(result));
				  assertThat(status(result)).isEqualTo(OK);
				  

				} catch (JOSEException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
	
  @Test
  public void callIndex() {
  	running(fakeApplication, new Runnable() {
  		@Override
			public void run() {
  			u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
  			List<Feed> feeds = createSomeFeeds();
  			u.addFeeds(feeds);
  			u.save();
  			
  			try {
  				String token = u.createJWT();
  				Result result = callAction(
  				  controllers.routes.ref.FeedsController.index(),
  				  fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
  				);
  				assertThat(status(result)).isEqualTo(OK);
  				assertThat(contentType(result)).isEqualTo("application/json");
  				  
  				JsonNode feed;
  				JsonNode article;
  				
  				// First item: Atom
  				feed = Json.parse(contentAsString(result)).get(0);
  				assertThat(feed.get("title").textValue()).isEqualTo("Example Feed");
  				assertThat(feed.get("pubDate").textValue()).isEqualTo("2003-12-13T18:30:02Z");
  				assertThat(feed.get("version").textValue()).isEqualTo("1.0");
  				assertThat(feed.get("type").textValue()).isEqualTo("atom");
  				article = feed.get("articles").get(0);
  				assertThat(article.get("author").textValue()).isEqualTo("John Doe");
  				assertThat(article.get("readed").booleanValue()).isEqualTo(false);
  				assertThat(article.get("title").textValue()).isEqualTo("Atom-Powered Robots Run Amok");
  				assertThat(article.get("pub_date").textValue()).isEqualTo("2003-12-13T18:30:02Z");
  				assertThat(article.get("description").textValue()).isEqualTo("Some text.");
  				assertThat(article.get("link").textValue()).isEqualTo("http://example.org/2003/12/13/atom03");
  				  
  				// Second item: RSS
  				feed = Json.parse(contentAsString(result)).get(1);
  				assertThat(feed.get("title").textValue()).isEqualTo("WriteTheWeb");
  				assertThat(feed.get("pubDate").textValue()).isEqualTo(null);
  				assertThat(feed.get("version").textValue()).isEqualTo("0.91");
  				assertThat(feed.get("type").textValue()).isEqualTo("rss");
  				article = feed.get("articles").get(0);
  				assertThat(article.get("title").textValue()).isEqualTo("Giving the world a pluggable Gnutella");
  				assertThat(article.get("description").textValue()).isEqualTo("WorldOS is a framework on which to build programs that work like Freenet or Gnutella -allowing distributed applications using peer-to-peer routing.");
  				assertThat(article.get("link").textValue()).isEqualTo("http://writetheweb.com/read.php?item=24");
  				assertThat(article.get("feed_title").textValue()).isEqualTo("WriteTheWeb");
  			} catch (JOSEException e) {
  				System.out.println("WTF...");
  				e.printStackTrace();
  			}
			}
		});
  }
  
  @Test
  public void callCreate() {
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {
  	  	try {
    	  	String token;
  				Result result;
  				u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
  			  token = u.createJWT();
  				
    	  	JsonNode body = Json.parse(doubleQuotify("{`url`:`http://www.36kr.com/feed`}"));
    	  	result = callAction(
    	  		controllers.routes.ref.FeedsController.create(),
    	  		fakeRequest().withJsonBody(body).withHeader(JWT_HEADER, getBearerToken(token))
    	  	);
    	  	assertThat(status(result)).isEqualTo(CREATED);
    	  	
    	  	// check if Feed object is created
     	  	u = User.findByEmail(USER_EMAIL);
    	  	List<Feed> feeds = u.getFeeds();
    	  	assertThat(feeds.size()).isEqualTo(1);
    	  	// check if there's any Article object created
    	  	List<Article> articles = Article.all();
    	  	assertThat(articles.size()).isGreaterThan(0);  	  	
    	  	// check feed content
    	  	Feed feed = feeds.get(0);
    	  	assertThat(feed.getTitle()).isEqualTo("36氪 | 关注互联网创业");
  	  	} catch (JOSEException e) {
  	  		System.out.println("something wrong in createJWT");
  	  	}
  	  }
  	});
  }
  
  @Test
  public void callShow() {
  	running(fakeApplication, new Runnable() {
  		@Override
  		public void run() {
  			try {
    			String token;
  				Result result;
  				u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
    			u.addFeeds(createSomeFeeds());
    			u.save();
  			  token = u.createJWT();
  			  
    			List<Feed> feeds = Feed.all();
    		  Feed feed = feeds.get(0);
    			
    		  result = callAction(
    		  	controllers.routes.ref.FeedsController.show(feed.id),
    		  	fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
    		  );
    		  assertThat(status(result)).isEqualTo(OK);
    		  assertThat(contentType(result)).isEqualTo("application/json");
    		  
    		  JsonNode feedNode;
    		  feedNode = Json.parse(contentAsString(result));
  			  assertThat(feedNode.get("title").textValue()).isEqualTo("Example Feed");
  			  assertThat(feedNode.get("pubDate").textValue()).isEqualTo("2003-12-13T18:30:02Z");
  			  assertThat(feedNode.get("version").textValue()).isEqualTo("1.0");
  			  assertThat(feedNode.get("type").textValue()).isEqualTo("atom");  				
  			} catch (JOSEException e) {
  	  		System.out.println("something wrong in createJWT");
  			}
  		}
  	});
  }
  
  @Test
  public void callShowNotFound() {
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {
  	  	try {
  	  		String token;
  				Result result;
  				u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
    			u.addFeeds(createSomeFeeds());
    			u.save();
  			  token = u.createJWT();
    		  
  			  result = callAction(
    		  	// magic number?
    		  	controllers.routes.ref.FeedsController.show(1234567),
    		  	fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
    		  );
    		  assertThat(status(result)).isEqualTo(NOT_FOUND);
    		  JsonNode resultNode = Json.parse(contentAsString(result));
    		  assertThat(resultNode.get("message").textValue()).isEqualTo("not found");
  	  	} catch (JOSEException e) {
  	  		System.out.println("something wrong in createJWT");
  	  	}
  		}
  	});
  }
  
  @Test
  public void callUpdate() {
  	final String NEW_TITLE = "MyCustomTitle";
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {
  	  	try {
    	  	String token;
  				Result result;
  				u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
    			u.addFeeds(createSomeFeeds());
    			u.save();
  			  token = u.createJWT();

    	  	Feed feed = Feed.all().get(0);
    	  	Long id = feed.id;
    	  	
    	  	JsonNode body = Json.parse(doubleQuotify(
    	  		"{`title`: `" + NEW_TITLE + "`}"
    	  	));
    	  	
    	  	result = callAction(
    	  		controllers.routes.ref.FeedsController.update(id),
    	  		fakeRequest().withJsonBody(body).withHeader(JWT_HEADER, getBearerToken(token))
    	  	);
    	  	
    	  	assertThat(status(result)).isEqualTo(OK);
    	  	feed = Feed.findById(id);
    	  	assertThat(feed.getTitle()).isEqualTo(NEW_TITLE);

  	  	} catch (JOSEException e) {
  	  		System.out.println("something wrong in createJWT");
  	  	}
  	  }
  	});
  }
  
  @Test
  public void callDelete() {
  	running(fakeApplication, new Runnable() {
  		@Override
  		public void run() {
  			try {
  	  		String token;
  				Result result;
  				u = createUser(USER_EMAIL, USER_NAME, USER_PASSWORD);
    			u.addFeeds(createSomeFeeds());
    			u.save();
  			  token = u.createJWT();
    			
  			  u = User.findByEmail(USER_EMAIL);
    			List<Feed> feeds = u.getFeeds();
    			Feed f = feeds.get(0);
    			
    			// have 2 feeds at first
    			assertThat(u.getFeeds().size()).isEqualTo(2);
    			assertThat(Feed.all().size()).isEqualTo(2);
    			
    			result = callAction(
    				controllers.routes.ref.FeedsController.delete(f.id),
    				fakeRequest().withHeader(JWT_HEADER, getBearerToken(token))
    			);
    			
    			u = User.findByEmail(USER_EMAIL);
    			// have 1 feed now
    			assertThat(Feed.all().size()).isEqualTo(1);
    			assertThat(u.getFeeds().size()).isEqualTo(1);

  			} catch (JOSEException e) {
  	  		System.out.println("something wrong in createJWT");
  			}
  		}
  	});
  }
  
  @Test
  public void callRefresh() {
  	// TODO
  }
  
  //@Test
  public void callRead() {
  	running(fakeApplication, new Runnable() {
  		@Override
  		public void run() {
  			createSomeFeeds();
  			
  			// TODO: there are some problems...
  			
  			//Feed f = Feed.first();
  			/*Feed f = Feed.all().get(0);
  			LinkedHashSet<Article> articles = (LinkedHashSet<Article>) f.getArticles();
  			Iterator<Article> iter = articles.iterator();
  			Article article;
  			Long id = f.id;
  			
  			while (iter.hasNext()) {
  				article = iter.next();
  				assertThat(article.isReaded()).isEqualTo(false);
  			}
  			
  			// do the update
  			Result result = callAction(
  				controllers.routes.ref.FeedsController.read(id)
  			);
  			
  			f = Feed.findById(id);
  		  articles = (LinkedHashSet<Article>) f.getArticles();
  			iter = articles.iterator();
  			while (iter.hasNext()) {
  				article = iter.next();
  				assertThat(article.isReaded()).isEqualTo(true);
  			}*/
  		}
  	});
  }
  
  private String getBearerToken(String token) {
  	return "Bearer " + token;
  }
  
}
