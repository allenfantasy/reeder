package models;

import java.util.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Feed extends Model {
	protected static Finder<Long, Feed> find = new Finder(Long.class, Feed.class);
	
	@Id
  public Long id;

  @Required
  private String title;
  private String link;
  private String sourceUrl;
  private String pubDate;
  private String description;
  private String language;
  private String type;
  private String version;
  
  @OneToMany(mappedBy="feed", cascade=CascadeType.ALL)
  public Set<Article> articles = new LinkedHashSet<Article>();
  
  public Feed() {};
  
  public Feed(JsonNode node) {
    this.title = node.findPath("title").textValue();
  	this.link = node.findPath("link").textValue();
  	this.description = node.findPath("description").textValue();
  	this.language = node.findPath("language").textValue();
  	this.pubDate = node.findPath("pubDate").textValue();
  }
  
  /*
   * Full-attr constructor
   */
  public Feed(String title, String link, String description, String language,
  		String pubDate, String sourceUrl, String type, String version) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.pubDate = pubDate;
		this.sourceUrl = sourceUrl;
		this.type = type;
		this.version = version;
  }
  
  /*
   * Part-attr constructor: ignore type and version
   */
  public Feed(String title, String link, String description, String language,
  		String pubDate, String sourceUrl) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.pubDate = pubDate;
		this.sourceUrl = sourceUrl;
  }
 
  public Map<String, Object> getData() {
  	Map<String, Object> item = new HashMap<String, Object>();
  	item.put("id", id);
		item.put("title", title);
		item.put("link", link);
		item.put("description", description);
		item.put("pubDate", pubDate);
		item.put("sourceUrl", sourceUrl);
		item.put("type", type);
		item.put("version", version);
		
		// preprocess all articles (remove the 'feed' reference)
		List<Object> articleValues = new ArrayList<Object>();
		for (Article a : articles) {
			articleValues.add(a.getData());
		}
		item.put("articles", articleValues);
  	return item;
  }
  
  public static List<Feed> all() {
    return find.all();
  }
  public static Feed findById(Long id) {
  	return find.ref(id);
  }
 
  /*
   * toString() helper
   * Override to print it, for debugging convenience 
   */
  @Override
  public String toString() {
  	String str = "====== " + "Feed" + id + " ======\n";
  	str += "title: " + title + "\n" + "link: " + link + "\n"
  + "description: " + description + "\n" + "language: " + language + "\n"
  + "\n" + "pubDate: " + pubDate + "\n";
  	for (Article article : this.getArticles()) {
  		str += "\n---------------\n";
  		str += article.toString();
  	}
  	return str;
  }
  
  public Set<Article> getArticles() {
  	return articles;
  }
  
  public String getTitle() {
  	return this.title;
  }
  
  public String getLink() {
  	return this.link;
  }
  
  public String getDescription() {
  	return this.description;
  }
  
  public String getLanguage() {
  	return this.language;
  }
  
  public String getPubDate() {
  	return this.pubDate;
  }
  
  public String getSourceURL() {
  	return this.sourceUrl;
  }
  
  public String getType() {
  	return this.type;
  }
  
  public String getVersion() {
  	return this.version;
  }

  public static void create(Feed feed) {
  	feed.save();
  }
  
  /*
   * only can update `title` of Feed
   */
  public static void updateTitle(Feed feed, String title) {
    if (title != null) {
    	feed.title = title;
    	feed.update(); // to database
    }
  }

  public static void delete(Long id) {
  	find.ref(id).delete();
  }
}
