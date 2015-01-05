var app = angular.module('rssApp');

var TIMEOUT_DURATION = 40 * 1000; // 40s

var todayFilter = function(article) {

  var date = new Date(article.pub_date);
  var today = new Date();
  today.setHours(0); today.setMinutes(0); today.setSeconds(0);

  var tomorrow = new Date(article.pub_date);
  tomorrow.setDate(tomorrow.getDate() + 1);
  tomorrow.setHours(0); tomorrow.setMinutes(0); tomorrow.setSeconds(0);

  return today < date && date < tomorrow;
};
var starredFilter = function(article) {
  return article.starred;
};

app.factory('Feed',["$http", "$rootScope", "$state", "$stateParams", "$auth",
function($http, $rootScope, $state, $stateParams, $auth) {
  return {
    // datas
    alreadyFetched: false,
    feeds: [],
    feed: undefined,
    articles: [],
    article: undefined,

    /** View layer functions, handle datas bind to views **/
    // TODO separate these functions into DataCenter
    // and wrap all Feed's $http requests into Feed
    // and wrap all Article's $http requests info Article
    getFeeds: function() {
      return this.feeds;
    },
    addFeed: function(feed) {
      this.feeds.push(feed);
    },
    getFeed: function(feed) {
      return this.feed;
    },
    setFeed: function(feed) {
      this.feed = feed;
      this.articles = feed.articles;
    },
    removeFeed: function(feed) {
      var index;
      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id === feed.id) {
          index = i;
          break;
        }
      }
      this.feeds.splice(index, 1);
      console.log(this.feeds);
    },
    getArticles: function() {
      return this.articles;
    },
    getArticle: function() {
      return this.article;
    },
    setArticle: function(article) {
      this.article = article;
    },
    addArticles: function(feedId, articles) {
      var filters = {
        default: function(articles) {
          return articles;
        },
        today: function(articles) {
          return articles.filter(todayFilter);
        },
        star: function(articles) {
          return articles.filter(starredFilter);
        }
      };
      filters.all = filters.default;

      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id == feedId) {
          this.feeds[i].articles = this.feeds[i].articles.concat(articles);

          // update list section by emitting events
          var action = $stateParams.action || "default";

          console.log('current action: ' + action);
          $rootScope.$emit("addArticles", filters[action](articles));
        }
      }
    },
    /**
    * put all articles into list
    */
    setAll: function() {
      this.feed = undefined;
      this.articles = this.feeds.reduce(function(prev, feed) {
        return prev.concat(feed.articles);
      },[]);
    },
    /**
    * put all starred articles into list
    */
    setAllStarred: function() {
      this.feed = undefined;
      this.articles = this.feeds.reduce(function(prev, feed) {
        return prev.concat(feed.articles.filter(starredFilter));
      }, []);
    },
    setToday: function() {
      this.feed = undefined;
      this.articles = this.feeds.reduce(function(prev, feed) {
        return prev.concat(feed.articles.filter(todayFilter));
      }, []);
    },

    /*** $http request wrappers ***/
    all: function(success, error) {
      var self = this;
      if (!this.alreadyFetched) {
        // fetch from API for the very first successful request
        //console.log("for the first time");
        $http.get('api/feeds').success(function(feeds) {
          self.feeds = feeds;
          self.alreadyFetched = true;
          success(self.feeds);
        }).error(error);
      }
      else {
        // just return feeds
        //console.log("not the first time");
        success(this.feeds);
      }
    },
    updateFeedTitle: function(id, title, success, error) {
      $http.post('api/feeds/' + id, { title: title })
      .success(success)
      .error(error);
    },
    create: function(url, success, error) {
      console.log("before emitting");
      $rootScope.$emit("notice", {
        type: "info",
        content: "Adding feed..."
      });
      $http.post('api/feeds', { url: url }, { timeout: TIMEOUT_DURATION }) // 20s timeout
        .success(success).error(error);
    },
    delete: function(feedId, success, error) {
      console.log("ready to delete");
      $http.delete("api/feeds/" + feedId, {}, { timeout: TIMEOUT_DURATION })
        .success(success).error(error);
    },
    refresh: function(ids, success, error) {
      $http.post('api/feeds/refresh', { ids: ids }, { timeout: TIMEOUT_DURATION })
      .success(success)
      .error(error);
    },

    // TODO: decouple these mangling code...
    readArticles: function(success, error) {
      // Case A: "feed", "article"
      // 1. find feed
      // 2. check all feed's articles as readed
      // 3. mark feed as "readed"

      // Case B: "all", "star", "today"
      // 1. find all articles in the list
      // 2. check them as readed
      // 3. check if their feeds are readed as well,
      //    mark it as "readed" if so

      var feed = this.feed;
      var feedId;
      var articles = this.articles; // by reference ?
      if (feed) { // Case A
        feedId = feed.id;

        // update this.feeds to update sidebar
        for(var i = 0; i < this.feeds.length; i++) {
          if (this.feeds[i].id === feedId) {
            this.feeds[i].allReaded = true;
            break;
          }
        }

        // update this.articles to update list
        articles.forEach(function(article) {
          article.readed = true;
        });

        // sync database
        $http.post('api/feeds/' + feedId + '/read')
        .success(success).error(error);

      } else { // Case B

        var feedIds = [];
        var articleIds;

        // update this.articles to update list
        // and collect all feed ids
        articles.forEach(function(article) {
          article.readed = true;
          if (feedIds.indexOf(article.feed_id) === -1) {
            feedIds.push(article.feed_id);
          }
        });

        // update all related feeds in this.feeds to update sidebar
        this.feeds.forEach(function(feed) {
          if (feedIds.indexOf(feed.id) !== -1) {
            feed.allReaded = true;
          }
        });

        // sync database
        articleIds = articles.map(function(article) {
          return article.id;
        });
        $http.post('api/articles/read_batch', { ids: articleIds })
        .success(success).error(error);
      }
    },

    readArticle: function(article) {
      var feedId = article.feed_id;
      var articleId = article.id;
      var feed;
      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id === feedId) {
          feed = this.feeds[i];
          break;
        }
      }
      for(var j = 0; j < feed.articles.length; j++) {
        if (feed.articles[j].id === articleId) {
          feed.articles[j].readed = true;
          break;
        }
      }
      var readStatuses = feed.articles.map(function(article) {
        return article.readed;
      });
      if (readStatuses.indexOf(false) === -1) { // all readed
        feed.allReaded = true;
      }
    },
    unreadArticle: function(article) {
      var feedId = article.feed_id;
      var articleId = article.id;
      var feed;
      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id === feedId) {
          feed = this.feeds[i];
          break;
        }
      }
      for(var j = 0; j < feed.articles.length; j++) {
        if (feed.articles[j].id === articleId) {
          feed.articles[j].readed = false;
        }
      }
      // 有未读的内容
      if (feed.allReaded) feed.allReaded = false;
    }
  };
}]);
