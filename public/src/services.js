'use strict';

var feedService = angular.module('feedService', []);

var todayFilter = function(article) {
  var date = new Date(article.pub_date);
  var today = new Date(article.pub_date);
  today.setHours(0); today.setMinutes(0); today.setSeconds(0);

  var tomorrow = new Date(article.pub_date);
  tomorrow.setDate(tomorrow.getDate() + 1);
  tomorrow.setHours(0); tomorrow.setMinutes(0); tomorrow.setSeconds(0);

  return today < date && date < tomorrow;
};
var starredFilter = function(article) {
  return article.starred;
};


feedService.factory('Feed',["$http", "$rootScope", "$state", function($http, $rootScope, $state) {
  return {
    // datas
    alreadyFetched: false,
    feeds: [],
    feed: undefined,
    articles: [],
    article: undefined,

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
      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id == feedId) {
          this.feeds[i].articles = this.feeds[i].articles.concat(articles);

          // update list section by emitting events
          var state = $state.$current.name;
          if ((state == "feed" || state == "article") && this.feed.id == feedId) {
            $rootScope.$emit("addArticles", articles);
          } else if (state == "today") {
            $rootScope.$emit("addArticles", articles.filter(todayFilter));
          } else if (state == "star") {
            $rootScope.$emit("addArticles", articles.filter(starredFilter));
          } else if (state == "all") {
            $rootScope.$emit("addArticles", articles);
          }
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

    all: function(success, error) {
      var self = this;
      if (!this.alreadyFetched) {
        // fetch from API for the very first successful request
        console.log("for the first time");
        $http.get('api/feeds').success(function(feeds) {
          self.feeds = feeds;
          self.alreadyFetched = true;
          success(self.feeds);
        }).error(error);
      }
      else {
        // just return feeds
        console.log("not the first time");
        cb(this.feeds);
      }
    },
    updateFeedTitle: function(id, title, success, error) {
      $http.post('api/feeds/' + id, { title: title })
      .success(success)
      .error(error);
    },
    create: function(url, success, error) {
      $http.post('api/feeds', { url: url })
        .success(success)
        .error(error);
    },
    refresh: function(ids, success, error) {
      $http.post('api/feeds/refresh', { ids: ids }, { timeout: 20 * 1000 })
        .success(success)
        .error(error);
    },
    readFeed: function() {
      var feedId = this.feed.id;
      var feed;
      // update this.feeds to update sidebar
      for(var i = 0; i < this.feeds.length; i++) {
        if (this.feeds[i].id === feedId) {
          this.feeds[i].allReaded = true;
          break;
        }
      }

      // update this.articles to update list
      this.articles.forEach(function(article) {
        article.readed = true;
      });

      // sync database
      $http.post('api/feeds/' + feedId + '/read').success(function(data) {
        // TODO deal with failure, add callbacks
        console.log('read feed success');
        console.log(data);
      });
    },

    validateURL: function(url) {
      // Credit to http://regexr.com?37i6s
      // Also the SO answer which provide the last link: http://stackoverflow.com/a/3809435/1301194
      var regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;
      return url.match(regex);
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

feedService.factory('Article', ["$http", function($http) {
  // TODO: Add callbacks
  return {
    read: function(id) {
      $http.post('api/articles/' + id + '/read').success(function(data) {
        console.log('read success');
        console.log(data);
      });
    },
    unread: function(id) {
      $http.post('api/articles/' + id + '/unread').success(function(data) {
        console.log('unread success');
        console.log(data);
      });
    },
    star: function(id) {
      $http.post('api/articles/' + id + '/star').success(function(data) {
        console.log('star success');
        console.log(data);
      });
    },
    unstar: function(id) {
      $http.post('api/articles/' + id + '/unstar').success(function(data) {
        console.log('unstar success');
        console.log(data);
      });
    }
    // TODO: read batch
  };
}]);
