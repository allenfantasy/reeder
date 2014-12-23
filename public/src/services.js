'use strict';

var feedService = angular.module('feedService', []);

feedService.factory('Feed',["$http", function($http) {
  return {
    data: {
      feeds: [],
      feed: undefined,
      articles: [],
      article: undefined
    },
    getData: function() {
      return this.all();
    },
    all: function() {
      var self = this;
      $http.get('api/feeds').success(function(feeds) {
        self.data.feeds = feeds;
      });
      return this.data;
    },
    addFeed: function(feed) {
      this.data.feeds.push(feed); 
    },
    setFeed: function(feed) {
      this.data.feed = feed;
      this.data.articles = feed.articles;
    },
    setArticle: function(article) {
      this.data.article = article;
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
  return {
    read: function(id) {
      $http.post('api/articles/' + id + '/read').success(function(data) {
        console.log(data);
      });
    },
    unread: function(id) {
      $http.post('api/articles/' + id + '/unread').success(function(data) {
        console.log(data);
      });
    }
    // TODO: read batch
  };
}]);
