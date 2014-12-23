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
    setFeed: function(feed) {
      this.data.feed = feed;
      this.data.articles = feed.articles;
    },
    setArticle: function(article) {
      this.data.article = article;
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
