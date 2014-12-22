'use strict';

var feedService = angular.module('feedService', []);

feedService.factory('Feed',["$http", function($http) {
  /*var feeds = [];
  var currentArticles = [];
  var currentArticle;*/
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
