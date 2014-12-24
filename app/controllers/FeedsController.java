package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Article;
import models.Feed;
import lib.exceptions.InvalidFeedException;
import lib.util.*;
import play.*;
import play.libs.Json;
import play.mvc.*;

public class FeedsController extends Controller {
	
	@BodyParser.Of(BodyParser.Json.class)
  public static Result index() {
		List results = new ArrayList();
  	List<Feed> feeds = Feed.all();
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
		  Feed.create(feed);
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
	
	@BodyParser.Of(BodyParser.Json.class)
  public static Result update(Long id) throws NullPointerException {
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
  		RSSFeedParser parser = new RSSFeedParser(feed.getSourceURL());
  		parser.writeFeed2File();
  		List<Article> articles = parser.getArticles();
  		List<Article> newArticles = new ArrayList<Article>();
  		for(Article article : articles) {
  		  Boolean flag = feed.getArticles().add(article);
  		  if (flag) {
  		  	newArticles.add(article);
  		  }
  		}
  		feed.save();
  		JsonNode json = Json.toJson(newArticles);
    	return ok(json);
  	} catch (EntityNotFoundException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (Exception e) {
  		e.printStackTrace();
    	return internalServerError(buildErrorInfo("internal error"));
  	}
  }
  
  /*
   * Set all articles under a feed into 'readed'
   */
  @BodyParser.Of(BodyParser.Json.class)
  public static Result read(Long id) {
  	try {
  		Feed feed = Feed.findById(id);
  		Set<Article> articles = feed.getArticles();
  		for(Article a : articles) {
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
  
  private static ObjectNode buildErrorInfo(String msg) {
  	ObjectNode errorInfo = Json.newObject();
		errorInfo.put("message", msg);
		return errorInfo;
  }
}
