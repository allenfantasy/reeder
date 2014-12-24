package lib.util;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import lib.exceptions.InvalidFeedException;
import models.Feed;
import models.Article;

/**
 * Use abstract class since we have some public interfaces
 * as well as some common implementation.
 */
abstract public class FeedParser {
	// interfaces
  public abstract Feed readFeed() throws IOException, InvalidFeedException;
  public abstract List<Article> getArticles() throws InvalidFeedException;
  public abstract String getSourceURL();
  
  protected abstract String writeFeed2File() throws InvalidFeedException;
  protected abstract void cleanFile(File f);
  protected abstract String getFeedType();

  static final SecureRandom random = new SecureRandom();
  
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
