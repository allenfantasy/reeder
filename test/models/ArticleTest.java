package models;

import java.util.*;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import static org.fest.assertions.Assertions.*;
import models.*;

public class ArticleTest extends ModelTest {
  @Test
  public void EqualsTest() {
  	Set<Article> articles = new HashSet<Article>();
  	Feed f = new Feed("TestTitle", "http://dummy.com", "Testing Description", "en-us", "2015-01-01",
  			"http://dummy.com/rss", "rss", "2.0");
  	Article a1 = new Article(f, "allen", "article description", "123456", "http://dummy.com/articles/1", "ArticleTitle",
  			"2015-01-01");
  	Article a2 = new Article(f, "allen", "article description", "123456", "http://dummy.com/articles/1", "ArticleTitle",
  			"2015-01-01");
    
  	boolean flag1 = articles.add(a1);
    
    assertThat(flag1).isEqualTo(true);
    assertThat(articles.size()).isEqualTo(1);
    
    boolean flag2 = articles.add(a2);
    
    assertThat(flag2).isEqualTo(false);
    assertThat(articles.size()).isEqualTo(1);
    
    assertThat(articles.contains(a2)).isEqualTo(true);
  }
}
