package controllers;

// Java built-in packages
import java.util.*;
import javax.persistence.EntityNotFoundException;

// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.JsonNode;
import play.*;
import play.libs.Json;
import play.mvc.*;

// Custom packages
import models.Article;
import static lib.Util.*;

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
	public static Result starredIndex() {
		List results = new ArrayList();
		List<Article> articles = Article.allStarred();
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
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result star(Long id) {
		try {
			Article a = Article.findById(id);
			a.star();
			return ok(Json.toJson(a.getData()));
		} catch (EntityNotFoundException e) {
			return notFound(buildErrorInfo("not found"));
		} catch (Exception e) {
			return internalServerError(buildErrorInfo("internal error"));
		}
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result unstar(Long id) {
		try {
			Article a = Article.findById(id);
			a.unstar();
			return ok(Json.toJson(a.getData()));
		} catch (EntityNotFoundException e) {
			return notFound(buildErrorInfo("not found"));
		} catch (Exception e) {
			return internalServerError(buildErrorInfo("internal error"));
		}
	}
}
