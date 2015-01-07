package lib.util;

// Java built-in packages
import java.security.*;
import java.util.*;

// 3rd Party's packages (include Play)
import org.json.*;
import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Util
 * Static utility helpers
 * 
 * @author allen
 *
 */
public class Util {
	/**
	 * Convert JSON string to HashMap
	 * 
	 * @param t
	 * @return
	 * @throws JSONException if string is not valid JSON
	 */
	public static HashMap<String, String> jsonToMap(String t) throws JSONException {
  	HashMap<String, String> map = new HashMap<String, String>();
  	JSONObject jObject = new JSONObject(t);
  	Iterator<?> keys = jObject.keys();
  	
  	while(keys.hasNext()) {
  		String key = (String)keys.next();
  		String value = jObject.getString(key);
  		map.put(key, value);
  	}
  	
  	System.out.println("json: " + jObject);
  	System.out.println("map: " + map);
  	return map;
  }
  
  /**
   * Digest password from plain-text using MD5
   * @param pass
   * @return digested password
   */
  public static String cryptWithMD5(String pass, String salt,
  		String token) {
  	String plain = pass + salt + token;
  	try {
  		MessageDigest md = MessageDigest.getInstance("MD5");
  		byte[] plainBytes = plain.getBytes();
  		md.reset();
  		byte[] digested = md.digest(plainBytes);
  		StringBuffer sb = new StringBuffer();
  		for(int i = 0; i < digested.length; i++) {
  			sb.append(Integer.toHexString(0xff & digested[i]));
  		}
  		return sb.toString();
  	} catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
  	}
  }
  
  /**
	 * Build error info object
	 * @param msg
	 * @return
	 */
  public static ObjectNode buildErrorInfo(String msg) {
		ObjectNode errorInfo = Json.newObject();
		errorInfo.put("message", msg);
		return errorInfo;
  }
}
