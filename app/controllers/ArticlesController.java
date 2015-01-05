package controllers;

// Java built-in packages
import java.util.*;
import javax.persistence.EntityNotFoundException;

// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.Json;
import play.mvc.*;

import com.avaje.ebean.*;

// Custom packages
import models.Article;
import static lib.Util.*;

@With(AuthenticateAction.class)
public class ArticlesController extends Controller {
  
	@BodyParser.Of(BodyParser.Json.class)
	public static Result index() {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
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
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
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
		try {
			JsonNode jsonParams = request().body().asJson();
			if (jsonParams == null) {
				return badRequest(buildErrorInfo("invalid json"));
			}
			JsonNode idsNode = jsonParams.findPath("ids");
			if (idsNode.isMissingNode()) {
				return badRequest(buildErrorInfo("no ids provided"));
			}
			
			Iterator<JsonNode> iter = idsNode.iterator();
			String updateSql = "UPDATE article SET is_readed=1 WHERE id in (";
			
			// do something only when not empty
			if (iter.hasNext()) {
				while (iter.hasNext()) {
					Long id = iter.next().asLong();
					// TODO: type check? Custom Exceptions?
					updateSql += id + ",";
				}
				
				// remove last ',' & add right parentheses
				updateSql = updateSql.substring(0, updateSql.length() - 1);
				updateSql += ")";
				
				SqlUpdate readArticles = Ebean.createSqlUpdate(updateSql);
				// can assign this statement to an int,
				// to check how many rows effected
				readArticles.execute(); 
			}

			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(buildErrorInfo("internal error"));
		}
	}
	
	public static Result unreadBatch() {
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
