package controllers;

//Java built-in packages
import org.junit.*;

import java.text.ParseException;
import java.util.*;

// 3rd Party's packages (include Play)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.*;
import com.ning.http.client.providers.jdk.ResponseBodyPart;

import play.mvc.*;
import play.test.*;
import play.libs.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static lib.util.Util.*;
import lib.util.AtomFeedParser;
import models.*;

public class UsersControllerTest extends ControllerTest {
	protected static User u;
	private static final String EMAIL = "allen@dxhackers.com";
	private static final String USERNAME = "allenfantasy";
	private static final String PASSWORD = "asglasi476sngl8";
	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
	
	@Test
	public void callLoginSuccess() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				cleanupDatabase();
				createUser(EMAIL, USERNAME, PASSWORD);
				String bodyString = String.format(
						"{`email`:`%s`,`password`:`%s`}", EMAIL, PASSWORD);
				JsonNode body = Json.parse(doubleQuotify(bodyString));
				Result result = callAction(
					controllers.routes.ref.UsersController.login(),
					fakeRequest().withJsonBody(body)
				);
				JsonNode resultNode;
				resultNode = Json.parse(contentAsString(result));
				String token = resultNode.get("token").textValue();
				try {
					SignedJWT signedJWT = SignedJWT.parse(token);
					ReadOnlyJWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
					String email = claimsSet.getSubject();
					
					assertThat(email).isEqualTo(EMAIL);

					User user = User.findByEmail(email);
					byte[] sharedSecret = user.getPassword().getBytes();
					JWSVerifier verifier = new MACVerifier(sharedSecret);
					
					assertThat(signedJWT.verify(verifier)).isEqualTo(true);
					
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Test
	public void callLoginFail() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				cleanupDatabase();
				createUser(EMAIL, USERNAME, PASSWORD);
				
				String bodyString;
				JsonNode body;
				Result result;
				JsonNode resultNode;
				
				bodyString = "{`email`:`anonymous@hack.com`,"
													+ "`password`: `askfhalgh`}";
				body = Json.parse(doubleQuotify(bodyString));
				result = callAction(
					controllers.routes.ref.UsersController.login(),
					fakeRequest().withJsonBody(body)
				);
				resultNode = Json.parse(contentAsString(result));
				
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				assertThat(resultNode.get("message").textValue())
								.isEqualTo("Invalid email");
				
				bodyString = "{`email`:`allen@dxhackers.com`,"
												  + "`password`: `87654321`}";
				body = Json.parse(doubleQuotify(bodyString));
				result = callAction(
					controllers.routes.ref.UsersController.login(),
					fakeRequest().withJsonBody(body)
				);
				resultNode = Json.parse(contentAsString(result));
				
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				assertThat(resultNode.get("message").textValue())
								.isEqualTo("Invalid password");
			}
		});
	}

	@Test
	public void callLogout() {
		// We even don't need to implement logout API,
		// Since the "logout" functionality is implemented
		// purely on frontend by deleting Token
	}
	
	@Test
	public void callRegister() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				cleanupDatabase();
				
				JsonNode body;
				JsonNode resultNode;
				Result result;
				String bodyString;
				
				// Case: valid
				bodyString = String.format(
					"{`email`:`%s`,`password`:`%s`,`username`:`%s`}", EMAIL, PASSWORD, USERNAME);
				body = Json.parse(doubleQuotify(bodyString));
				result = callAction(
					controllers.routes.ref.UsersController.register(),
					fakeRequest().withJsonBody(body)
				);
				System.out.println(contentAsString(result));
				assertThat(status(result)).isEqualTo(OK);

				// Case: invalid - email already exists
				result = callAction(
					controllers.routes.ref.UsersController.register(),
					fakeRequest().withJsonBody(body)
				);
				resultNode = Json.parse(contentAsString(result));
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				assertThat(resultNode.get("message").textValue()).isEqualTo("email is taken");
				
				// Case: invalid - invalid password format
				String anotherEmail = "allen@fantasy.me";
				String weakPassword = "12345";
			  bodyString = String.format(
			  		"{`email`:`%s`,`password`:`%s`,`username`:`%s`}",
			  		anotherEmail, weakPassword, USERNAME);
			  body = Json.parse(doubleQuotify(bodyString));
				result = callAction(
					controllers.routes.ref.UsersController.register(),
					fakeRequest().withJsonBody(body)
				);
				resultNode = Json.parse(contentAsString(result));
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				assertThat(resultNode.get("message").textValue())
													.isEqualTo("invalid password format");
				
				// Case: invalid - invalid email format
				String invalidEmail = "asjqoigwjqo#asasgj.com";
				String validPassword = "fantasy88302#";
				bodyString = String.format(
			  		"{`email`:`%s`,`password`:`%s`,`username`:`%s`}",
			  		invalidEmail, validPassword, USERNAME);
			  body = Json.parse(doubleQuotify(bodyString));
				result = callAction(
					controllers.routes.ref.UsersController.register(),
					fakeRequest().withJsonBody(body)
				);
				resultNode = Json.parse(contentAsString(result));
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				assertThat(resultNode.get("message").textValue())
													.isEqualTo("invalid email format");
			}
		});
	}

	@Test
	public void callGetProfile() {
		running(fakeApplication, new Runnable() {

			@Override
			public void run() {
				cleanupDatabase();
				u = createUser(getUserEmail(), getUserName(), getUserPassword());
				try {
					String token = u.createJWT();
					
					Result result = callAction(
						controllers.routes.ref.UsersController.getProfile(),
						fakeRequest().withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(OK);
				  JsonNode resultNode = Json.parse(contentAsString(result));
				  assertThat(resultNode.get("name").textValue()).isEqualTo(getUserName());

				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Test
	public void callUpdateProfile() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				cleanupDatabase();
				u = createUser(getUserEmail(), getUserName(), getUserPassword());
				Long id = u.id;
				try {
					String token = u.createJWT();
					final String DUMMY = "dummy";
					
					ObjectNode body = nodeFactory.objectNode();
					body.put("name", DUMMY);
					
					Result result = callAction(
						controllers.routes.ref.UsersController.updateProfile(),
						fakeRequest().withJsonBody(body).withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(OK);
					u = User.findById(id);
				  assertThat(u.getName()).isEqualTo(DUMMY);

				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Test
	public void callUpdatePassword() {
		running(fakeApplication, new Runnable() {
			
			@Override
			public void run() {
				cleanupDatabase();
				u = createUser(getUserEmail(), getUserName(), getUserPassword());
				Long id = u.id;
				try {
					String token = u.createJWT();
					final String DUMMY_PASSWORD = "dummy9138940";
					ObjectNode body = nodeFactory.objectNode();
					body.put("password", DUMMY_PASSWORD);
					
					Result result = callAction(
						controllers.routes.ref.UsersController.updatePassword(),
						fakeRequest().withJsonBody(body).withHeader(getJWTHeader(), getBearerToken(token))
					);
					assertThat(status(result)).isEqualTo(OK);
					// check return token
					u = User.findById(id);
					JsonNode resBody = Json.parse(contentAsString(result));
					assertThat(resBody.get("token").textValue()).isEqualTo(u.createJWT(Calendar.DATE, 7));
					
					// check if password is updated
					u = User.findById(id);
					assertThat(u.authenticate(DUMMY_PASSWORD)).isTrue();
					
				} catch (JOSEException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
