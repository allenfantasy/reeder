package controllers;

//Java built-in packages
import java.util.*;

import com.avaje.ebeaninternal.server.lib.DaemonScheduleThreadPool;
// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jwt.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;

import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action.Simple;
import play.mvc.*;
import play.mvc.Http.*;
// Custom packages
import models.User;
import static lib.Util.*;

public class AuthenticateAction extends Simple {
	public static final int INVALID_TOKEN = 1;
	public static final int USER_NOT_FOUND = 2;
	public static final int EXPIRED_TOKEN = 3;
	
	@Override
	public Promise<SimpleResult> call(Context ctx) throws Throwable {
		Promise<SimpleResult> ret = null;
		
		Http.Request request = ctx.request();
		String token = request.getHeader("Authorization");
		if (token == null) {
		  ret = buildSimpleErrorResult("Invalid token", INVALID_TOKEN);
		}
		else {
			try {
				SignedJWT signedJWT = SignedJWT.parse(token);
				ReadOnlyJWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
				String email = claimsSet.getSubject();
				
			  User user = User.findByEmail(email);
			  if (user == null) {
			  	ret = buildSimpleErrorResult("No matching user, invalid token", USER_NOT_FOUND);
			  } else {
			  	byte[] secretKey = user.getPassword().getBytes();
			  	JWSVerifier verifier = new MACVerifier(secretKey);
			  	
			  	if (signedJWT.verify(verifier)) {
			  		Date expiration = claimsSet.getExpirationTime();
			  		if (expiration == null) { // not expire forever
			  			ctx.args.put("user", user);
			  			ret = delegate.call(ctx);
			  			return ret;
			  		}
			  		if (expiration.before(new Date())) { // expired
			  			ret = buildSimpleErrorResult("Expired token", EXPIRED_TOKEN);
			  		}
			  		else {
			  			ctx.args.put("user", user);
			  			ret = delegate.call(ctx);
			  		}
			  	} else {
			  		ret = buildSimpleErrorResult("Invalid token", INVALID_TOKEN);
			  	}
			  }
			  // Q: Should we rebuild a new token automatically when expired ?
			  // A: No. Return failed message, and let user re-login to fetch new token.
			} catch (java.text.ParseException e) {
				ret = buildSimpleErrorResult("Invalid token", INVALID_TOKEN);
			} catch (JOSEException e) {
				ret = buildSimpleErrorResult("Invalid token", INVALID_TOKEN);
			}
		}
		return ret;
	}
	
	private Promise<SimpleResult> buildSimpleErrorResult(String msg, int statusCode) {
		ObjectNode errorInfo = buildErrorInfo(msg);
		errorInfo.put("code", statusCode);
		return F.Promise.pure((SimpleResult) unauthorized(errorInfo));
	}
}
