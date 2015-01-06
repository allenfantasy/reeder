package controllers;

// Java built-in packages
import java.util.*;

// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.nimbusds.jose.*;
import play.*;
import play.libs.Json;
import play.mvc.*;

// Custom packages
import models.User;
import static lib.Util.*;

public class UsersController extends ApplicationController {
	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result login() {
		JsonNode reqJson = request().body().asJson();
		if (reqJson == null) {
			return badRequest(buildErrorInfo("invalid json format"));
		}
		String email = reqJson.findPath("email").textValue();
		String password = reqJson.findPath("password").textValue();
		if (email == null) {
			return badRequest(buildErrorInfo("no email provided"));
		}
		if (password == null) {
		  return badRequest(buildErrorInfo("no password provided"));
		}
		User u = User.findByEmail(email);
		if (u == null) {
			return badRequest(buildErrorInfo("Invalid email"));
		}
		if (u.authenticate(password)) { // valid
			try {
				String token = u.createJWT(Calendar.DATE, 7); // 7天有效期
			  JsonNode returnNode;
			  Map<String, Object> returnObj = new HashMap<String, Object>();
			  returnObj.put("token", token);
			  returnNode = Json.toJson(returnObj);
				return ok(returnNode);
			} catch (JOSEException e) {
				e.printStackTrace();
				return internalServerError(buildErrorInfo("internal server error"));
			}
			
		} else {
			return badRequest(buildErrorInfo("Invalid password"));
		} 
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result register() {
		JsonNode reqJson = request().body().asJson();
		if (reqJson == null) {
			return badRequest(buildErrorInfo("invalid json format"));
		}
		String email = reqJson.findPath("email").textValue();
		String password = reqJson.findPath("password").textValue();
		String username = reqJson.findPath("username").textValue();
		if (email == null) return badRequest(buildErrorInfo("no email"));
		if (password == null) return badRequest(buildErrorInfo("no password"));
		if (username == null) return badRequest(buildErrorInfo("no username"));
		
		User u = User.findByEmail(email);
		if (u != null) return badRequest(buildErrorInfo("email is taken"));
		
		u = new User(email, username, password);
		String error = u.validate();
		if (error == null){
			u.save();
			ObjectNode info = Json.newObject();
			info.put("id", u.id);
			return ok(info);
		} else {
			return badRequest(buildErrorInfo(error));
		}
	}
	
	@With(AuthenticateAction.class)
	public static Result getProfile() {
		User user = getUser();
		ObjectNode info = Json.newObject();
		info.put("name", user.getName());
		return ok(info);
	}
	
	@With(AuthenticateAction.class)
	public static Result updateProfile() {
		User user = getUser();
		JsonNode reqJson = request().body().asJson();
		if (reqJson == null) {
			return badRequest(buildErrorInfo("invalid json format"));
		}
		String name = reqJson.findPath("name").textValue();
		if (name == null) return badRequest(buildErrorInfo("no name"));
		
		user.setName(name);
		user.save();
		return ok();
	}
	
	@With(AuthenticateAction.class)
	public static Result updatePassword() {
		User user = getUser();
		JsonNode reqJson = request().body().asJson();
		if (reqJson == null) {
			return badRequest(buildErrorInfo("invalid json format"));
		}
		String password = reqJson.findPath("password").textValue();
		if (password == null) return badRequest(buildErrorInfo("no password"));
		
		String error = user.updatePassword(password);
		
		if (error == null) {
			try {
				ObjectNode resNode = nodeFactory.objectNode();
			  resNode.put("token", user.createJWT(Calendar.DATE, 7));
			  return ok(resNode);
			}
		  catch (JOSEException e) {
		  	e.printStackTrace();
		  	return internalServerError(buildErrorInfo("internal server error"));
		  }
		}
		else {
			return badRequest(buildErrorInfo(error));			
		}
	}
	
  private static User getUser() {
  	Map<String, Object> args = Http.Context.current().args;
  	return (User) args.get("user");
  }
}
