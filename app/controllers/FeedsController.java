package controllers;

//Java built-in packages
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import javax.persistence.*;

//3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.*;
import play.*;
import play.libs.Json;
import play.mvc.*;

// Custom packages
import models.*;
import lib.util.*;
import lib.exceptions.*;
import static lib.Util.*;

@With(AuthenticateAction.class)
public class FeedsController extends Controller {
	
	@BodyParser.Of(BodyParser.Json.class)
  public static Result index() {
		User user = getUser();
		List results = new ArrayList();
  	List<Feed> feeds = user.getFeeds();
  	//System.out.println(feeds);
  	for(Feed f : feeds) {
  		Map<String, Object> item = f.getData();
  		results.add(item);
  	}
  	JsonNode json = Json.toJson(results);
  	return ok(json);
  }

	@BodyParser.Of(BodyParser.Json.class)
  public static Result create() {
		User user = getUser();
		JsonNode returnNode;
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(buildErrorInfo("invalid json format"));
		}
		String url = json.findPath("url").textValue();
		if (url == null) {
			return badRequest(buildErrorInfo("no URL provided"));
		}
		FeedParserFactory factory = FeedParserFactory.newInstance();

		//parser.print2File();
		try {
		  FeedParser parser = factory.createFeedParser(url); 	// throws MalformedURLException
		  Feed feed = parser.readFeed(); 											// throws InvalidFeedException, IOException
		  user.addFeed(feed);
		  user.save();
		  //Feed.create(feed);
		  returnNode = Json.toJson(feed.getData());
		  return status(201, returnNode);
		} catch (MalformedURLException e) {
		  return badRequest(buildErrorInfo("invalid url format!"));
		} catch (InvalidFeedException e) {
		  return badRequest(buildErrorInfo(e.getMessage()));
		} catch (IOException e) {
		  e.printStackTrace();
		  return badRequest(buildErrorInfo("IOException"));
		} catch (Exception e) {
		  // protection
		  e.printStackTrace();
		  return badRequest(buildErrorInfo("Exception"));
		}
  }
	
  public static Result show(Long id) {
  	try {
  		Feed feed = Feed.findById(id);
  		JsonNode json = Json.toJson(feed.getData());
    	return ok(json);
  	} catch (EntityNotFoundException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (NullPointerException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (Exception e) {
  		e.printStackTrace();
  		return badRequest(buildErrorInfo("Exception"));
  	}
  }
	
  /**
   * update Feed's title ONLY
   * @param id
   * @return
   */
	@BodyParser.Of(BodyParser.Json.class)
  public static Result update(Long id) {
		try {
			JsonNode json = request().body().asJson();
	  	String title = json.findPath("title").textValue();
			Feed feed = Feed.findById(id);
			Feed.updateTitle(feed, title);
	  	return ok(json);
		} catch (NullPointerException e) {
			return notFound(buildErrorInfo("not found"));
		} catch (Exception e) {
			e.printStackTrace();
			return badRequest(buildErrorInfo("Exception"));
		}
  }

  public static Result delete(Long id) {
  	System.out.println("before deleting feed");
  	try {
  		Feed.delete(id);
  		return ok();
  	} catch (OptimisticLockException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (NullPointerException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (Exception e) {
  		e.printStackTrace();
  		return badRequest(buildErrorInfo("Exception"));
  	}
  }
  
  @BodyParser.Of(BodyParser.Json.class)
  public static Result refresh(Long id) {
  	try {
  		Feed feed = Feed.findById(id);
  		List<Map<String, Object>> newArticles = fetchNewArticles(feed);
  		
  		JsonNode json = Json.toJson(newArticles);
    	return ok(json);
  	} catch (EntityNotFoundException e) {
  		e.printStackTrace();
  		return notFound(buildErrorInfo("not found"));
  	} catch (RefreshFeedException e) {
  		e.printStackTrace();
  		return internalServerError(buildErrorInfo("refresh failed"));
  	} catch (Exception e) {
  		e.printStackTrace();
    	return internalServerError(buildErrorInfo("internal error"));
  	}
  }
  
	@BodyParser.Of(BodyParser.Json.class)
  public static Result refreshAll() {
		System.out.println("refreshAll");
		List results = new ArrayList();
		
		try {
			JsonNode jsonParams = request().body().asJson();
			if (jsonParams.findPath("ids").isArray()) {
				Iterator<JsonNode> iter = jsonParams.findPath("ids").iterator();
				
				// fetch new articles & add to result
				while (iter.hasNext()) {
					Long id = iter.next().asLong();
					Feed feed = Feed.findById(id);
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("feed_id", id);
					item.put("articles",fetchNewArticles(feed));
					//System.out.println("OK");
					results.add(item);
				}
				
		  	JsonNode json = Json.toJson(results);
		  	return ok(json);
			}
			else {
				return internalServerError(buildErrorInfo("something fuck up"));
			}
		} catch (RefreshFeedException e) {
			e.printStackTrace();
			return internalServerError(buildErrorInfo("refresh failed"));
		} catch (Exception e) {
			e.printStackTrace();
			return ok();
		}
  }
  
  /**
   * Set all articles under a feed into 'readed'
   */
  @BodyParser.Of(BodyParser.Json.class)
  public static Result read(Long id) {
  	try {
  		Feed feed = Feed.findById(id);
  		Set<Article> articles = feed.getArticles();
  		for(Article a : articles) {
  			// TODO do this in batch. one by one is tooooo bad.
  			a.read();
  		}
  		return ok();
  	} catch (EntityNotFoundException e) {
  		e.printStackTrace();
  		return notFound(buildErrorInfo("internal error"));
  	} catch (Exception e) {
  		e.printStackTrace();
  		return internalServerError(buildErrorInfo("internal error"));
  	}
  }
  
  /**
   * Fetch latest articles of feed
   * 
   * @param feed
   * @return new articles
   * @throws RefreshFeedException
   */
  private static List<Map<String, Object>> fetchNewArticles(Feed feed) throws RefreshFeedException {
		List<Map<String, Object>> newArticles = new ArrayList<Map<String, Object>>();
		FeedParserFactory factory = FeedParserFactory.newInstance();
  	try {
  		
  		FeedParser parser = factory.createFeedParser(feed.getSourceURL());
  		parser.writeFeed2File();
  		List<Article> articles = parser.fetchLatestArticles(feed); // here feed is updated

  		for(Article article : articles) {
  		 	newArticles.add(article.getData());
  		}
  	} catch (Exception e) {
  		e.printStackTrace();
  		RefreshFeedException ex = new RefreshFeedException();
  		ex.initCause(e);
  		throw ex;
  	}
		return newArticles;
  }
  
  private static User getUser() {
  	Map<String, Object> args = Http.Context.current().args;
  	return (User) args.get("user");
  }
}
