package models;

// Java built-in packages
import java.util.*;

import javax.persistence.*;

// 3rd Party's packages (include Play)
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import play.data.validation.Constraints.*;
import play.db.ebean.*;

// Custom packages
import static lib.Util.*;
import lib.util.PasswordValidator;


@Entity
@Table(name="users")
public class User extends Model {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  
  @Required
  @Column(unique=true)
  private String email;
  
  @Required
  private String name;
  private String password; // should be signatured
  private String createdAt;
  
  @OneToMany(mappedBy="user", cascade=CascadeType.ALL)
  private List<Feed> feeds = new ArrayList<Feed>();
  
  // TODO: friends
  
  // static variables / finals
  private static final String TOKEN = "Reeder";
  private static final EmailValidator emailValidator = new EmailValidator();
  private static final PasswordValidator pwdValidator = new PasswordValidator();
  public static final Model.Finder<Long, User> find = new Model.Finder<Long, User>(
  		Long.class, User.class
  );
  
  // static methods
  
  public static List<User> all() {
  	return find.all();
  }
  public static User findById(Long id) {
  	return find.byId(id);
  }
  public static User findByEmail(String email) {
  	return find.where().eq("email", email).findUnique();
  }
  public static boolean exists(String email) {
  	User u = find.where().eq("email", email).findUnique();
  	return u != null;
  }
  
  // constructor
  public User(String email, String name, String password) {
  	String timestamp = Long.toString(System.currentTimeMillis());
  	this.email = email;
    this.name = name;
    this.createdAt = timestamp;
    this.password = password;
    //this.password = cryptWithMD5(password, timestamp, TOKEN);
  }
  
  // non-static methods(so called "member methods")  
  
  /**
   * Validate user
   * this method must be called before saving
   * 
   * @return null if user object is validate; return error message otherwise
   */
  public String validate() {
  	
  	// According to https://github.com/playframework/playframework/issues/925,
  	// email like user@tld is a valid email address,
  	// since Play email validation conforms to the emails address and domain
  	// name RFCs, and use regular expression recommended by HTML5 spec:
  	// http://www.w3.org/TR/html-markup/datatypes.html#form.data.emailaddress
  	
  	if (!emailValidator.isValid(email)) {
  		return "invalid email format";
  	}

  	// Check raw password only for the first time
  	if (id == null) {
  		// TODO: refactor this using Play's Constraints' validators
    	// should return different error msg

    	if (!pwdValidator.validate(password)) {
    		return "invalid password format";
    	}
  	}
  	return null;
  }

  /**
   * Encrypt password with MD5 before saving to database FOR THE FIRST TIME
   */
  @Override
  public void save() {
  	String error = validate();
  	if (error != null) {
  		System.out.println(error);
  		return;
  	}
  	if (id == null) {
  		password = cryptWithMD5(password, createdAt, TOKEN);
  	}

    super.save();
  }

  /**
   * update password to database
   * check password validity, crypt it and save
   * should call super.save() to avoid issue
   * 
   * @param password
   * @return
   */
  public String updatePassword(String password) {
  	if (!pwdValidator.validate(password)) {
  		return "Invalid password format";
  	}
  	password = cryptWithMD5(password, createdAt, TOKEN);
  	this.password = password;
  	super.save();
  	return null;
  }
  /**
   * authenticate password
   * 
   * @param plainPass
   * @return
   */
  public boolean authenticate(final String plainPass) {
  	if (password.equals(cryptWithMD5(plainPass, createdAt, TOKEN))) {
  		return true;
  	}
  	else {
  		return false;
  	}
  }
  
  public String getPassword() {
  	return this.password;
  }
  public String getEmail() {
  	return this.email;
  }
  public String getName() {
  	return this.name;
  }
  public void setName(String name) {
  	this.name = name;
  }
  public List<Feed> getFeeds() {
  	return this.feeds;
  }
  public void addFeed(Feed f) {
  	this.feeds.add(f);
  	f.setUser(this);
  }
  public void addFeeds(List<Feed> feeds) {
  	this.feeds.addAll(feeds);
  	for(Feed f : feeds) {
  		f.setUser(this);
  	}
  }
  
  /**
	 * Create Json Web Token
	 * @param user
	 * @return jwt
	 */
  public String createJWT(int validTimeUnit, int validTimeAmount) throws JOSEException {
  	// use user's encrypted password as secret key
 		byte[] sharedSecret = password.getBytes();
 				
 		// Create HMAC signer
 		JWSSigner signer = new MACSigner(sharedSecret);
 				
 		/**
 		 * JSON Web Token (JWT) draft-jones-json-web-token-06
 		 * 
 		 * JWT Claims 
 		 * http://self-issued.info/docs/draft-jones-json-web-token-06.html#anchor4
 		 * 
 		 * A JWT contains a set of cliams represented as a base64url encoded JSON object. Note however,
 		 * that the set of claims a JWT must contain to be considered valid is context-dependent and is
 		 * outside the scope of this specification.
 		 */
 		JWTClaimsSet claimsSet = new JWTClaimsSet();
 		
 		Date currentTime = new Date();
 		
 		claimsSet.setSubject(email);
 		claimsSet.setIssueTime(currentTime);
 		claimsSet.setIssuer("https://reeder.net");
 		
 		// set expiration time.
 		if (validTimeUnit != 0 && validTimeAmount != 0) {
 			Calendar c = Calendar.getInstance();
 	 		c.setTime(currentTime);
 	 		c.add(validTimeUnit, validTimeAmount);
 	 		claimsSet.setExpirationTime(c.getTime());
 		}
 				
 		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
 				
 		// Apply the HMAC
 		signedJWT.sign(signer);
 				
 		// To serialize to compact form, produces something like
 		// eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
 		String s = signedJWT.serialize();
 		return s;
  }
  
  // create a JWT doen't expire forever
  public String createJWT() throws JOSEException {
  	return createJWT(0, 0);
  }
  
  // private helpers
  
  
}
