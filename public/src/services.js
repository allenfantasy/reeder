'use strict';

var feedService = angular.module('feedService', []);

feedService.factory('Feed',["$http", function($http) {
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

    setFeedCleared: function() {
      this.feed.cleared = false;
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
