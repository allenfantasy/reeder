'use strict';

var feedService = angular.module('feedService', []);

feedService.factory('Feed',["$http", "$rootScope", function($http, $rootScope) {
  return {
    // datas
    feeds: [],
    feed: undefined,
    articles: [],
    article: undefined,

    all: function(cb) {
      var self = this;
      $http.get('api/feeds').success(function(feeds) {
        self.feeds = feeds;
        cb(self.feeds);
      });
    },
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
    updateFeedTitle: function(id, title, success, error) {
      $http.post('api/feeds/' + id, { title: title })
        .success(success)
        .error(error);
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

    create: function(url, success, error) {
      $http.post('api/feeds', { url: url }).success(success).error(error);
    },
    validateURL: function(url) {
      // Credit to http://regexr.com?37i6s
      // Also the SO answer which provide the last link: http://stackoverflow.com/a/3809435/1301194
      var regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;
      return url.match(regex);
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
