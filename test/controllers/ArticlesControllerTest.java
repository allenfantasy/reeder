package controllers;

// Java built-in packages
import java.util.*;
import org.junit.*;

// 3rd Party packages (include Play)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.nimbusds.jose.JOSEException;
import play.mvc.*;
import play.libs.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

// Custom packages
import models.*;

public class ArticlesControllerTest extends ControllerTest {
	protected static User u;
	
	@Test
  public void callIndex() {
    running(fakeApplication, new Runnable() {
			@Override
			public void run() {
				try {
					cleanupDatabase();
					String token;
					u = createUser(getUserEmail(), getUserName(), getUserPassword());
					u.addFeeds(createSomeFeeds());
					u.save();
					token = u.createJWT();
					Result result = callAction(
						controllers.routes.ref.ArticlesController.index(),
						fakeRequest().withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(OK);
					JsonNode articlesNode = Json.parse(contentAsString(result));
					if (articlesNode.isArray()) {
						assertThat(articlesNode.size()).isGreaterThan(0);
					}
				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
  }
	
	@Test
	public void callRead() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				try {
					cleanupDatabase();
					String token;
					u = createUser(getUserEmail(), getUserName(), getUserPassword());
					u.addFeeds(createSomeFeeds());
					u.save();
					token = u.createJWT();
					
					Article article = Article.all().get(0);
					Long id = article.id;
					callAction(
						controllers.routes.ref.ArticlesController.read(id),
						fakeRequest().withHeader(getJWTHeader(), getBearerToken(token))
					);
					article = Article.findById(id);
					assertThat(article.isReaded()).isEqualTo(true);
				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Test
	public void callUnread() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				try {
					cleanupDatabase();
					String token;
					u = createUser(getUserEmail(), getUserName(), getUserPassword());
					u.addFeeds(createSomeFeeds());
					u.save();
					token = u.createJWT();
					
					// "read" the first article
					Article article = Article.all().get(0);
					Long id = article.id;
					assertThat(article.isReaded()).isEqualTo(false);
					article.read();
					assertThat(article.isReaded()).isEqualTo(true);
					
					callAction(
						controllers.routes.ref.ArticlesController.unread(id),
						fakeRequest().withHeader(getJWTHeader(), getBearerToken(token))
					);
					article = Article.findById(id);
					assertThat(article.isReaded()).isEqualTo(false);
				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Test
	public void callReadBatch() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				try {
					cleanupDatabase();
					String token;
					Result result;
					u = createUser(getUserEmail(), getUserName(), getUserPassword());
					token = u.createJWT();
					createSomeFeeds();
					JsonNodeFactory nodeFactory = JsonNodeFactory.instance; // singleton
				  JsonNode resultNode;
				  
					// Case: valid request
					Long id1, id2;
					Set<Article> articles;
					Article article;
					ObjectNode body = nodeFactory.objectNode();
					ArrayNode idsNode = nodeFactory.arrayNode();
		
					articles = Feed.all().get(0).getArticles();
					id1 = articles.iterator().next().id;
					articles = Feed.all().get(1).getArticles();
					id2 = articles.iterator().next().id;
					
					idsNode.add(id1).add(id2);
					body.put("ids", idsNode);
					
					article = Article.findById(id1);
					assertThat(article.isReaded()).isFalse();
					article = Article.findById(id2);
					assertThat(article.isReaded()).isFalse();
				
					result = callAction(
						controllers.routes.ref.ArticlesController.readBatch(),
						fakeRequest().withJsonBody(body).withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(OK);
					
					article = Article.findById(id1);
					assertThat(article.isReaded()).isTrue();
					article = Article.findById(id2);
					assertThat(article.isReaded()).isTrue();
					
					// Case: invalid json
					result = callAction(
						controllers.routes.ref.ArticlesController.readBatch(),
						fakeRequest().withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(BAD_REQUEST);
					resultNode = Json.parse(contentAsString(result));
					assertThat(resultNode.get("message").textValue())
								.isEqualTo("invalid json");
					
					// Case: valid json but invalid contents
					ObjectNode invalidJson = nodeFactory.objectNode();
					invalidJson.put("dummy", "malicious");
					result = callAction(
						controllers.routes.ref.ArticlesController.readBatch(),
						fakeRequest().withJsonBody(invalidJson).withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(BAD_REQUEST);
					resultNode = Json.parse(contentAsString(result));
					assertThat(resultNode.get("message").textValue())
								.isEqualTo("no ids provided");
				} catch (JOSEException e) {
					e.printStackTrace();
				}	
			}
		});
	}
	
	@Test
	public void callUnreadBatch() {
		// TODO
	}
}
