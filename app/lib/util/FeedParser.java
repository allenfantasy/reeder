package lib.util;

import java.io.*;
import java.util.List;

import models.Feed;
import models.Article;

public interface FeedParser {
  public Feed readFeed() throws IOException;
  public List<Article> getArticles();
  public void print2File() throws IOException;
  public String getSourceURL();
}
