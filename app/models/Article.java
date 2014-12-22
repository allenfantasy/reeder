package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Constraints.*;
import play.db.ebean.*;

@Entity
public class Article extends Model {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  
  @Column(columnDefinition="text")
  private String description;

  private String title;
  private String link;
  private String author;
  private String pubDate;
  private String guid;
  private Boolean isReaded;
  
  @ManyToOne
  @Required
  private Feed feed = new Feed();
  
  /*
   * Generic query helper for entity Company with id Long
   */
  public static Model.Finder<Long, Article> find = new Model.Finder<Long, Article>(
  		Long.class, Article.class
  );
  
  public static List<Article> all() {
  	return find.all();
  }
  
  /*
   * Constructor
   */
  public Article(Feed feed, String author, String description, String guid,
  							String link, String title, String pubDate) {
  	this.feed = feed;
  	this.author = author;
  	this.description = description;
  	this.guid = guid;
  	this.link = link;
  	this.title = title;
  	this.pubDate = pubDate;
  	this.isReaded = false; // default unread
  }
  
  public static Article findById(Long id) {
  	return find.byId(id);
  }
  
  public Map<String, Object> getData() {
  	Map<String, Object> item = new HashMap<String, Object>();
  	item.put("id", id);
  	item.put("author", author);
		item.put("description", description);
		item.put("guid", guid);
		item.put("link", link);
		item.put("title", title);
    item.put("readed", isReaded);
    item.put("feed_title", feed.getTitle());
		return item;
  }
  
  @Override
  public boolean equals(Object other) {
  	// TODO: test this
  	if (other.getClass() != Article.class) return false;
  	Article article = (Article)other;
  	if (article.getGuid() == null) return false;
  	return article.getGuid() == this.getGuid();
  }

  public Feed getFeed() {
  	return feed;
  }
  public void setFeed(Feed feed) {
  	this.feed = feed;
  }
  
  public String getTitle() {
  	return title;
  }
  public void setTitle(String title) {
  	this.title = title;
  }
  
  public String getDescription() {
  	return description;
  }
  public void setDescription(String description) {
  	this.description = description;
  }
  
  public String getLink() {
  	return link;
  }
  public void setLink(String link) {
  	this.link = link;
  }
  
  public String getAuthor() {
  	return author;
  }
  public void setAuthor(String author) {
  	this.author = author;
  }
  
  public String getGuid() {
  	return guid;
  }
  public void setGuid(String guid) {
  	this.guid = guid;
  }
  
  public String getPubDate() {
  	return this.pubDate;
  }
  public void setPubDate(String pubDate) {
  	this.pubDate = pubDate;
  }
  
  public Boolean isReaded() {
  	return this.isReaded;
  }
  public void read() {
  	this.isReaded = true;
  	this.save();
  }
  public void unread() {
  	this.isReaded = false;
  	this.save();
  }
  
  // helper
  public String toString() {
  	return "title: " + title + " link: " + link + " description: " + description
  			+ " author: " + author + " pubDate: " + pubDate;
  }
}
