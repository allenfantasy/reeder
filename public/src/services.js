'use strict';

var feedService = angular.module('feedService', []);

feedService.factory('Feed',["$http", function($http) {
  /*var feeds = [];
  var currentArticles = [];
  var currentArticle;*/
  return {
    data: {
      feeds: [],
      articles: [],
      article: null
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
    setArticles: function(articles) {
      this.data.articles = articles;
    },
    setArticle: function(article) {
      this.data.article = article;
    }
  };
}]);
