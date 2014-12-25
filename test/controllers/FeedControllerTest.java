package controllers;

import org.junit.*;
import java.sql.*;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.*;
import play.test.*;
import play.libs.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import java.util.*;
import models.*;

public class FeedControllerTest extends ControllerTest {
	
  @Test
  public void callIndex() {
  	running(fakeApplication, new Runnable() {
  		@Override
			public void run() {
  			createSomeFeeds();
  			
				Result result = callAction(
			  	controllers.routes.ref.FeedsController.index()
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
			  
			  cleanupDatabase();
			}
		});
  }
  
  @Test
  public void callCreate() {
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {	  	
  	  	JsonNode body = Json.parse(doubleQuotify("{`url`:`http://www.36kr.com/feed`}"));
  	  	Result result = callAction(
  	  		controllers.routes.ref.FeedsController.create(),
  	  		fakeRequest().withJsonBody(body)
  	  	);
  	  	assertThat(status(result)).isEqualTo(CREATED);
  	  	
  	  	// check if Feed object is created
  	  	List<Feed> feeds = Feed.all();
  	  	assertThat(feeds.size()).isEqualTo(1);
  	  	// check if there's any Article object created
  	  	List<Article> articles = Article.all();
  	  	assertThat(articles.size()).isGreaterThan(0);  	  	
  	  	// check feed content
  	  	Feed feed = feeds.get(0);
  	  	assertThat(feed.getTitle()).isEqualTo("36氪 | 关注互联网创业");
  	  	
  	  	cleanupDatabase();
  	  }
  	});
  }
  
  @Test
  public void callShow() {
  	running(fakeApplication, new Runnable() {
  		@Override
  		public void run() {
  			createSomeFeeds();
  			List<Feed> feeds = Feed.all();
  		  Feed feed = feeds.get(0);
  			
  		  Result result = callAction(
  		  	controllers.routes.ref.FeedsController.show(feed.id)
  		  );
  		  assertThat(status(result)).isEqualTo(OK);
  		  assertThat(contentType(result)).isEqualTo("application/json");
  		  
  		  JsonNode feedNode;
  		  feedNode = Json.parse(contentAsString(result));
			  assertThat(feedNode.get("title").textValue()).isEqualTo("Example Feed");
			  assertThat(feedNode.get("pubDate").textValue()).isEqualTo("2003-12-13T18:30:02Z");
			  assertThat(feedNode.get("version").textValue()).isEqualTo("1.0");
			  assertThat(feedNode.get("type").textValue()).isEqualTo("atom");
			  
			  cleanupDatabase();
  		}
  	});
  }
  
  @Test
  public void callShowNotFound() {
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {
  		  createSomeFeeds();
  		  Result result = callAction(
  		  	// magic number?
  		  	controllers.routes.ref.FeedsController.show(1234567)
  		  );
  		  assertThat(status(result)).isEqualTo(NOT_FOUND);
  		  JsonNode resultNode = Json.parse(contentAsString(result));
  		  assertThat(resultNode.get("message").textValue()).isEqualTo("not found");
  		  
  		  cleanupDatabase();
  		}
  	});
  }
  
  @Test
  public void callUpdate() {
  	running(fakeApplication, new Runnable() {
  	  @Override
  	  public void run() {
  	  	createSomeFeeds();
  	  	
  	  	final String NEW_TITLE = "MyCustomTitle";
  	  	Feed feed = Feed.all().get(0);
  	  	Long id = feed.id;
  	  	
  	  	JsonNode body = Json.parse(doubleQuotify(
  	  		"{`title`: `" + NEW_TITLE + "`}"
  	  	));
  	  	
  	  	Result result = callAction(
  	  		controllers.routes.ref.FeedsController.update(id),
  	  		fakeRequest().withJsonBody(body)
  	  	);
  	  	
  	  	assertThat(status(result)).isEqualTo(OK);
  	  	feed = Feed.findById(id);
  	  	assertThat(feed.getTitle()).isEqualTo(NEW_TITLE);
  	  	
  	  	cleanupDatabase();
  	  }
  	});
  }
  
  @Test
  public void callDelete() {
  	running(fakeApplication, new Runnable() {
  		@Override
  		public void run() {
  			createSomeFeeds();
  			
  			List<Feed> feeds = Feed.all();
  			Feed f = feeds.get(0);
  			
  			// have 2 feeds at first
  			assertThat(feeds.size()).isEqualTo(2);
  			
  			Result result = callAction(
  				controllers.routes.ref.FeedsController.delete(f.id)
  			);
  			
  			// have 1 feed now
  			assertThat(Feed.all().size()).isEqualTo(1);
  			
  			cleanupDatabase();
  		}
  	});
  }
  
  @Test
  public void callRefresh() {
  	// TODO
  }
  
  @Test
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
  		  
  			cleanupDatabase();
  		}
  	});
  }
  
}
