package lib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.json.*;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Util {
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
   * digest password from plain-text using MD5
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
	 * build error info
	 * @param msg
	 * @return
	 */
  public static ObjectNode buildErrorInfo(String msg) {
		ObjectNode errorInfo = Json.newObject();
		errorInfo.put("message", msg);
		return errorInfo;
  }
}
