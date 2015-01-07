package controllers;

//Java built-in packages
import java.util.*;
import java.net.*;
import java.io.*;

//3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.*;
import com.avaje.ebean.*;
import javax.persistence.*;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.libs.F.*;

// Custom packages
import models.*;
import lib.util.*;
import lib.exceptions.*;
import static lib.util.Util.*;

@With(AuthenticateAction.class)
public class FeedsController extends Controller {
	
	/**
	 * Get all feeds of the authenticated user
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
  public static Result index() {
		User user = getUser();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
  	List<Feed> feeds = user.getFeeds();
  	//System.out.println(feeds);
  	for(Feed f : feeds) {
  		Map<String, Object> item = f.getData();
  		results.add(item);
  	}
  	JsonNode json = Json.toJson(results);
  	return ok(json);
  }

	/**
	 * Create a feed from URL in request body
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Promise<Result> create() {
    Promise<Result> promise = Promise.promise(
    	new Function0<Result>() {
    		public Result apply() {
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
			}
    );
    
    return promise.map(new Function<Result, Result>() {
      public Result apply(Result result) {
      	return result;
      }
    });
	}
	
	/**
	 * Get a feed
	 * 
	 * @param id
	 * @return
	 */
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
   * 
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

	/**
	 * Delete a feed (and it's all articles)
	 * 
	 * @param id
	 * @return
	 */
  public static Result delete(Long id) {
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
  
  /**
   * Refresh a feed
   * 
   * @param id
   * @return
   */
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
  
  /**
   * Refresh all feeds of the authenticated user
   * 
   * @return
   */
  @BodyParser.Of(BodyParser.Json.class)
	public static Promise<Result> refreshAll() {
    Promise<Result> promise = Promise.promise(
    	new Function0<Result>() {
    		public Result apply() {
    			System.out.println("refreshAll");
    			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    			
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
			}
    );
    
    return promise.map(new Function<Result, Result>() {
      public Result apply(Result result) {
      	return result;
      }
    });
	}
  
  /**
   * Set all articles of a feed readed
   * 
   * @param id
   * @return
   */
  @BodyParser.Of(BodyParser.Json.class)
  public static Result read(Long id) {
  	try {
  		String readSql = "UPDATE article SET is_readed=1 WHERE feed_id = :id";
  		SqlUpdate readArticles = Ebean.createSqlUpdate(readSql);
  		readArticles.setParameter("id", id);
  		readArticles.execute();
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
  		//parser.writeFeed2File();
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
