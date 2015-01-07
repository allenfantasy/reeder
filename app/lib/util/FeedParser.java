package lib.util;

// Java built-in packages
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

// Custom packages
import lib.exceptions.InvalidFeedException;
import models.*;

/**
 * FeedParser
 * fetch content from source URL and parse into Feed
 * 
 * @author allen
 * 
 */
abstract public class FeedParser {
	// interfaces
	
	/**
   * fetch document from source URL and parse into Feed object
   * 
	 * @return Feed object
	 * @throws IOException
	 * @throws InvalidFeedException
	 */
  public abstract Feed readFeed() throws IOException, InvalidFeedException;
  
  /**
   * fetch document from Feed's source URL, get latest Articles
   * and update Feed
   * 
   * @param feed
   * @return latest Articles
   * @throws InvalidFeedException
   */
  public abstract List<Article> fetchLatestArticles(Feed feed) throws InvalidFeedException;
  
  /**
   * Get source URL
   * @return source URL
   */
  public abstract String getSourceURL();

  protected abstract void cleanFile(File f);
  protected abstract String getFeedType();

  static final SecureRandom random = new SecureRandom();
  
  /**
   * Generate a random file path
   * @return
   */
  protected String generateRandomFilepath() {
  	String folderPath = ".tmpfile" + File.separator;
 	  String filename = (new BigInteger(130, random)).toString(32);
 	  String fileSuffix = "-" + getFeedType() + ".xml";
 	  String filePath = folderPath + filename + fileSuffix;
 	  File f = new File(filePath);
 	  f.getParentFile().mkdirs();
  	return filePath;
  }
}
