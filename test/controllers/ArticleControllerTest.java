package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.*;
import org.junit.runner.notification.RunNotifier;

import play.mvc.*;
import play.test.*;
import play.libs.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import java.util.*;

import models.*;

public class ArticleControllerTest extends ControllerTest {
	@Test
  public void callIndex() {
    running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				createSomeFeeds();
				Result result = callAction(
					controllers.routes.ref.ArticlesController.index()
				);
				assertThat(status(result)).isEqualTo(OK);
				JsonNode articlesNode = Json.parse(contentAsString(result));
				if (articlesNode.isArray()) {
					assertThat(articlesNode.size()).isGreaterThan(0);
				}
				cleanupDatabase();
			}
			
		});
  }
	
	@Test
	public void callRead() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				createSomeFeeds();
				Article article = Article.all().get(0);
				Long id = article.id;
				Result result = callAction(
					controllers.routes.ref.ArticlesController.read(id)
				);
				article = Article.findById(id);
				assertThat(article.isReaded()).isEqualTo(true);
				cleanupDatabase();
			}
		});
	}
	
	@Test
	public void callUnread() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				createSomeFeeds();
				// "read" the first article
				Article article = Article.all().get(0);
				Long id = article.id;
				assertThat(article.isReaded()).isEqualTo(false);
				article.read();
				assertThat(article.isReaded()).isEqualTo(true);
				
				Result result = callAction(
					controllers.routes.ref.ArticlesController.unread(id)
				);
				article = Article.findById(id);
				assertThat(article.isReaded()).isEqualTo(false);
				cleanupDatabase();
			}
		});
	}
	
	@Test
	public void callReadBatch() {
		// TODO
	}
	
	@Test
	public void callUnreadBatch() {
		// TODO
	}
}
