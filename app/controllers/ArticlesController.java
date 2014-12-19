package controllers;

import java.util.*;

import javax.persistence.EntityNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.libs.Json;
import play.mvc.*;
import models.Article;

public class ArticlesController extends Controller {
  
	@BodyParser.Of(BodyParser.Json.class)
	public static Result index() {
		List results = new ArrayList();
		List<Article> articles = Article.all();
		for(Article article : articles) {
			Map<String, Object> item = article.getData();
			results.add(item);
		}
		JsonNode json = Json.toJson(results);
		return ok(json);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result read(Long id) {
		try {
			Article a = Article.findById(id);
			a.read();
			return ok();
		} catch (EntityNotFoundException e) {
			return notFound(buildErrorInfo("not found"));
		} catch (Exception e) {
			return internalServerError(buildErrorInfo("internal error"));
		}
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result unread(Long id) {
		try {
			Article a = Article.findById(id);
			a.unread();
			return ok();
		} catch (EntityNotFoundException e) {
			return notFound(buildErrorInfo("not found"));
		} catch (Exception e) {
			return internalServerError(buildErrorInfo("internal error"));
		}
	}
	
	public static Result readBatch() {
		return TODO;
	}
	
	public static Result unreadPatch() {
		return TODO;
	}
	
	private static ObjectNode buildErrorInfo(String msg) {
  	ObjectNode errorInfo = Json.newObject();
		errorInfo.put("message", msg);
		return errorInfo;
  }
}
