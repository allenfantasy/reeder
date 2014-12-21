package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Article;
import models.Feed;
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
  public static Result create() throws IOException {
		try {
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest(buildErrorInfo("invalid json format"));
			}
			String url = json.findPath("url").textValue();
			if (url == null) {
				return badRequest(buildErrorInfo("no URL provided"));
			}

			FeedParserFactory factory = FeedParserFactory.newInstance();
		  FeedParser parser = factory.createFeedParser(url);
		  //parser.print2File();
		  Feed feed = parser.readFeed();
	    Feed.create(feed);
	    return status(201, json);
		} catch (MalformedURLException e){
      //String errorMsg = e.getLocalizedMessage();
			return badRequest(buildErrorInfo("invalid url format!"));
		}
  }
	
  public static Result show(Long id) throws NullPointerException, EntityNotFoundException {
  	try {
  		Feed feed = Feed.findById(id);
  		JsonNode json = Json.toJson(feed.getData());
    	return ok(json);
  	} catch (EntityNotFoundException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (NullPointerException e) {
  		return notFound(buildErrorInfo("not found"));
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
		}
  }
  
  public static Result delete(Long id) throws NullPointerException, OptimisticLockException {
  	try {
  		Feed.delete(id);
  		return ok();
  	} catch (OptimisticLockException e) {
  		return notFound(buildErrorInfo("not found"));
  	} catch (NullPointerException e) {
  		return notFound(buildErrorInfo("not found"));
  	}
  }
  
  @BodyParser.Of(BodyParser.Json.class)
  public static Result refresh(Long id) {
  	try {
  		Feed feed = Feed.findById(id);
  		RSSFeedParser parser = new RSSFeedParser(feed.getSourceURL());
  		parser.print2File();
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
  		return notFound(buildErrorInfo("internal error"));
  	} catch (Exception e) {
  		return internalServerError(buildErrorInfo("internal error"));
  	}
  }
  
  private static ObjectNode buildErrorInfo(String msg) {
  	ObjectNode errorInfo = Json.newObject();
		errorInfo.put("message", msg);
		return errorInfo;
  }
}
