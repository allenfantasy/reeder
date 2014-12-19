package lib;

import java.util.*;

import org.json.*;

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
}
