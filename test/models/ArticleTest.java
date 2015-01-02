package models;

import java.util.*;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import static org.fest.assertions.Assertions.*;
import models.*;

public class ArticleTest extends ModelTest {
	private static final String AUTHOR_A = "allen";
	private static final String DESC_A = "article description";
	private static final String GUID_A = "123456";
	private static final String LINK_A = "http://dummy.com/articles/1";
	private static final String TITLE_A = "ArticleTitleA";
	private static final String PUBDATE_A = "2015-01-01";
	
	private static final String GUID_B = "654321";
	private static final String LINK_B = "http://dummy.com/articles/2";
  
	@Test
  public void EqualsTest() {
  	Set<Article> articles = new HashSet<Article>();
  	
  	Feed f = new Feed("TestTitle", "http://dummy.com", "Testing Description",
  			"en-us", "2015-01-01", "http://dummy.com/rss", "rss", "2.0");
  	Article a1 = new Article(f, AUTHOR_A, DESC_A, GUID_A, LINK_A,
  			TITLE_A, PUBDATE_A);
  	Article a2 = new Article(f, AUTHOR_A, DESC_A, GUID_A, LINK_A,
  			TITLE_A, PUBDATE_A);
  	Article a3 = new Article(f, AUTHOR_A, DESC_A, GUID_B, LINK_A,
  			TITLE_A, PUBDATE_A); // different guid (a1 - a3)
  	Article a4 = new Article(f, AUTHOR_A, DESC_A, null, LINK_A,
  			TITLE_A, PUBDATE_A); // null guid (a1 - a4)

  	Article a5 = new Article(f, AUTHOR_A, DESC_A, null, LINK_B,
  			TITLE_A, PUBDATE_A); // both's guid are none, different link (a4 - a5)

  	Article a6 = new Article(f, AUTHOR_A, DESC_A, null, null,
  			TITLE_A, PUBDATE_A); // both's guid & link are none, same title (a6 - a7)

  	Article a7 = new Article(f, AUTHOR_A, DESC_A, null, null,
  			TITLE_A, PUBDATE_A); // both's guid & link are none, same title (a6 - a7)
    
  	// symmetric
    assertThat(a1.equals(a2)).isEqualTo(true);
    assertThat(a2.equals(a1)).isEqualTo(true);

    assertThat(a1.equals(a3)).isEqualTo(false);
    assertThat(a3.equals(a1)).isEqualTo(false);

    assertThat(a1.equals(a4)).isEqualTo(false);
    assertThat(a4.equals(a1)).isEqualTo(false);

    assertThat(a4.equals(a5)).isEqualTo(false);
    assertThat(a5.equals(a4)).isEqualTo(false);

    assertThat(a6.equals(a7)).isEqualTo(true);
    assertThat(a7.equals(a6)).isEqualTo(true);
    
  	boolean flag1 = articles.add(a1);
    
    assertThat(flag1).isEqualTo(true);
    
    assertThat(articles.contains(a2)).isEqualTo(true);
    assertThat(articles.contains(a3)).isEqualTo(false);
    assertThat(articles.contains(a4)).isEqualTo(false);
    assertThat(articles.contains(a5)).isEqualTo(false);
    assertThat(articles.contains(a6)).isEqualTo(false);
    assertThat(articles.contains(a7)).isEqualTo(false);
  }
  
  /*@Test
  public void createTest() {
  	Article article = new Article(null, AUTHOR_A,
  			DESC_A, GUID_A, LINK_A, TITLE_A, PUBDATE_A);
  	article.save();
  }*/
}
