package controllers;

// Java built-in packages
import java.util.*;

// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.*;
import com.nimbusds.jose.*;
import play.*;
import play.libs.Json;
import play.mvc.*;

// Custom packages
import models.User;
import static lib.Util.*;

public class UsersController extends ApplicationController {
	
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
			return ok();
		} else {
			return badRequest(buildErrorInfo(error));
		}
	}
}
